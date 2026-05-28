package com.elderly.care.controller;

import com.elderly.care.dto.Result;
import com.elderly.care.entity.Elder;
import com.elderly.care.entity.ElderDoctorRelation;
import com.elderly.care.entity.HealthData;
import com.elderly.care.service.ElderDoctorRelationService;
import com.elderly.care.service.ElderService;
import com.elderly.care.service.HealthDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "医生工作站", description = "医生专属功能接口")
public class DoctorDashboardController {

    private final ElderDoctorRelationService relationService;
    private final ElderService elderService;
    private final HealthDataService healthDataService;

    /**
     * 获取医生Dashboard统计数据
     * GET /api/doctor/dashboard/stats?doctorId=1
     */
    @GetMapping("/dashboard/stats")
    @Operation(summary = "获取医生Dashboard统计数据")
    public Result<Map<String, Object>> getDashboardStats(@RequestParam Integer doctorId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取该医生签约的所有老人
        List<ElderDoctorRelation> relations = relationService.getRelationsByDoctorId(doctorId);
        List<Integer> elderIds = relations.stream()
                .map(ElderDoctorRelation::getElderId)
                .collect(Collectors.toList());
        
        // 总签约老人数
        stats.put("totalElders", elderIds.size());
        
        // TODO: 这里可以添加更多统计逻辑
        // 例如：今日危急告警数、未读咨询数等
        stats.put("criticalAlerts", 0);
        stats.put("unreadConsultations", 0);
        stats.put("todayTasks", 0);
        
        return Result.success(stats);
    }

    /**
     * 获取医生签约的老人列表（带最新健康数据）
     * GET /api/doctor/elders?doctorId=1
     */
    @GetMapping("/elders")
    @Operation(summary = "获取签约老人列表")
    public Result<List<Map<String, Object>>> getEldersWithHealthData(@RequestParam Integer doctorId) {
        // 获取该医生签约的所有老人ID
        List<ElderDoctorRelation> relations = relationService.getRelationsByDoctorId(doctorId);
        List<Integer> elderIds = relations.stream()
                .map(ElderDoctorRelation::getElderId)
                .collect(Collectors.toList());
        
        if (elderIds.isEmpty()) {
            return Result.success(Collections.emptyList());
        }
        
        // 获取老人详细信息和健康数据
        List<Map<String, Object>> elderList = new ArrayList<>();
        for (Integer elderId : elderIds) {
            Optional<Elder> elderOpt = elderService.getElderById(elderId);
            if (elderOpt.isPresent()) {
                Elder elder = elderOpt.get();
                HealthData latestHealth = healthDataService.getLatestHealthData(elderId);
                
                Map<String, Object> elderInfo = new HashMap<>();
                elderInfo.put("id", elder.getId());
                
                // 从user对象获取姓名
                if (elder.getUserId() != null) {
                    // 这里需要通过userId查询User，暂时先放userId
                    elderInfo.put("userId", elder.getUserId());
                    elderInfo.put("name", "老人" + elder.getId()); // 临时方案，后续需要关联User表
                }
                
                elderInfo.put("age", elder.getAge());
                elderInfo.put("gender", elder.getGender());
                
                // 健康数据
                if (latestHealth != null) {
                    elderInfo.put("heartRate", latestHealth.getHeartRate());
                    elderInfo.put("systolicPressure", latestHealth.getSystolicPressure());
                    elderInfo.put("diastolicPressure", latestHealth.getDiastolicPressure());
                    elderInfo.put("steps", latestHealth.getSteps());
                    elderInfo.put("recordedAt", latestHealth.getRecordedAt());
                    
                    // 判断健康状态
                    String status = determineHealthStatus(latestHealth);
                    elderInfo.put("healthStatus", status);
                } else {
                    elderInfo.put("heartRate", null);
                    elderInfo.put("systolicPressure", null);
                    elderInfo.put("diastolicPressure", null);
                    elderInfo.put("steps", 0);
                    elderInfo.put("recordedAt", null);
                    elderInfo.put("healthStatus", "unknown");
                }
                
                elderList.add(elderInfo);
            }
        }
        
        return Result.success(elderList);
    }

    /**
     * 判断健康状态
     */
    private String determineHealthStatus(HealthData health) {
        // 心率异常判断
        if (health.getHeartRate() != null) {
            if (health.getHeartRate() > 100 || health.getHeartRate() < 50) {
                return "critical"; // 危急
            }
            if (health.getHeartRate() > 90 || health.getHeartRate() < 60) {
                return "warning"; // 警告
            }
        }
        
        // 血压异常判断
        if (health.getSystolicPressure() != null && health.getDiastolicPressure() != null) {
            if (health.getSystolicPressure() >= 140 || health.getDiastolicPressure() >= 90) {
                return "warning"; // 高血压
            }
            if (health.getSystolicPressure() < 90 || health.getDiastolicPressure() < 60) {
                return "warning"; // 低血压
            }
        }
        
        return "normal"; // 正常
    }
}
