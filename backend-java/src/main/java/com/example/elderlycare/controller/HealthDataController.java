package com.example.elderlycare.controller;

import com.example.elderlycare.agent.EmergencyResponseAgent;
import com.example.elderlycare.agent.HealthMonitorAgent;
import com.example.elderlycare.agent.HealthMonitorAgent.HealthMonitorResult;
import com.example.elderlycare.dto.request.HealthDataRequest;
import com.example.elderlycare.dto.response.ApiResponse;
import com.example.elderlycare.dto.response.HealthDataResponse;
import com.example.elderlycare.entity.HealthData;
import com.example.elderlycare.service.HealthDataService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 健康数据控制器
 * 
 * 职责：接收硬件设备/移动端上报的健康数据，并触发Agent实时分析
 */
@RestController
@RequestMapping("/api")
public class HealthDataController {

    private static final Logger log = LoggerFactory.getLogger(HealthDataController.class);

    @Autowired
    private HealthDataService healthDataService;

    @Autowired
    private HealthMonitorAgent healthMonitorAgent;

    @Autowired
    private EmergencyResponseAgent emergencyResponseAgent;

    /**
     * 获取健康数据列表
     */
    @GetMapping("/health-data")
    public ResponseEntity<ApiResponse<List<HealthDataResponse>>> getHealthData(
            @RequestParam(defaultValue = "1") Integer user_id) {
        List<HealthDataResponse> data = healthDataService.getHealthDataByUserId(user_id);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * 添加健康数据，同时触发Agent实时分析
     * 
     * 工作流：
     *   1. 数据入库
     *   2. HealthMonitorAgent 规则引擎分析（毫秒级）
     *   3. 异常时自动触发 EmergencyResponseAgent 应急响应
     *   4. 严重异常 → 调度救援 + 通知家属和社区医生
     *   5. 轻度异常 → 通知家属关注
     */
    @PostMapping("/health-data")
    public ResponseEntity<ApiResponse<String>> addHealthData(@Valid @RequestBody HealthDataRequest request) {
        // 1. 保存数据到数据库
        HealthData savedData = healthDataService.addHealthData(request);
        log.info("健康数据已入库: id={}, userId={}, heartRate={}, bp={}/{}",
                savedData.getId(), savedData.getUserId(),
                savedData.getHeartRate(), savedData.getSystolicPressure(), savedData.getDiastolicPressure());

        // 2. 调用健康监测Agent进行实时分析
        HealthMonitorResult result = healthMonitorAgent.monitor(savedData);

        // 3. 如果检测到异常，触发应急响应Agent
        if (result.needsNotification()) {
            log.warn("健康数据检测到异常 [{}]，userId={}，触发应急响应",
                    result.getLevel(), request.getUserId());
            emergencyResponseAgent.handleHealthAlert(
                    request.getUserId(),
                    result.getAiSuggestion(),
                    result.getLevel());
        }

        return ResponseEntity.ok(ApiResponse.success("Health data added successfully", null));
    }
}