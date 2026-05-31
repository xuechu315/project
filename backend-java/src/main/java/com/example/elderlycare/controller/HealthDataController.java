package com.example.elderlycare.controller;

import com.example.elderlycare.dto.request.HealthDataRequest;
import com.example.elderlycare.dto.response.ApiResponse;
import com.example.elderlycare.dto.response.HealthDataResponse;
import com.example.elderlycare.service.HealthDataService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 健康数据控制器
 */
@RestController
@RequestMapping("/api")
public class HealthDataController {

    @Autowired
    private HealthDataService healthDataService;

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
     * 添加健康数据
     */
    @PostMapping("/health-data")
    public ResponseEntity<ApiResponse<String>> addHealthData(@Valid @RequestBody HealthDataRequest request) {
        healthDataService.addHealthData(request);
        return ResponseEntity.ok(ApiResponse.success("Health data added successfully", null));
    }
}