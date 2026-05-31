package com.example.elderlycare.controller;

import com.example.elderlycare.dto.response.ApiResponse;
import com.example.elderlycare.service.AiAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * AI分析控制器
 */
@RestController
@RequestMapping("/api")
public class AiAnalysisController {

    private static final Logger log = LoggerFactory.getLogger(AiAnalysisController.class);

    @Autowired
    private AiAnalysisService aiAnalysisService;

    /**
     * 分析心率数据
     * GET /api/health/analyze/heartrate?heartRate=xxx
     */
    @GetMapping("/health/analyze/heartrate")
    public ResponseEntity<ApiResponse<Map<String, String>>> analyzeHeartRate(
            @RequestParam Integer heartRate) {
        
        String analysis = aiAnalysisService.analyzeHeartRate(heartRate);
        
        Map<String, String> result = new HashMap<>();
        result.put("analysis", analysis);
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 分析血压数据
     * GET /api/health/analyze/bloodpressure?systolic=xxx&diastolic=xxx
     */
    @GetMapping("/health/analyze/bloodpressure")
    public ResponseEntity<ApiResponse<Map<String, String>>> analyzeBloodPressure(
            @RequestParam Integer systolic,
            @RequestParam Integer diastolic) {
        
        String analysis = aiAnalysisService.analyzeBloodPressure(systolic, diastolic);
        
        Map<String, String> result = new HashMap<>();
        result.put("analysis", analysis);
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 分析加速度数据（仅后端记录，不向前端返回分析内容）
     * GET /api/health/analyze/acceleration?x=xxx&y=xxx&z=xxx
     */
    @GetMapping("/health/analyze/acceleration")
    public ResponseEntity<ApiResponse<String>> analyzeAcceleration(
            @RequestParam Double x,
            @RequestParam Double y,
            @RequestParam Double z) {

        String analysis = aiAnalysisService.analyzeAcceleration(x, y, z);
        log.info("【加速度AI分析成功】x={}, y={}, z={}, 分析结果: {}", x, y, z, analysis);

        return ResponseEntity.ok(ApiResponse.success("received", null));
    }
}
