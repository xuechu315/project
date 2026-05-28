package com.elderly.care.controller;

import com.elderly.care.dto.HealthDataDTO;
import com.elderly.care.dto.Result;
import com.elderly.care.entity.HealthData;
import com.elderly.care.service.HealthDataService;
import com.elderly.care.utils.DtoConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "健康数据管理", description = "健康数据的上报和查询接口")
public class HealthController {

    private final HealthDataService healthDataService;

    /**
     * 上报健康数据
     * POST /api/health/report
     */
    @PostMapping("/report")
    @Operation(summary = "上报健康数据")
    public Result<HealthDataDTO> reportHealthData(@RequestBody HealthData healthData) {
        HealthData saved = healthDataService.reportHealthData(healthData);
        return Result.success(DtoConverter.convertToHealthDataDTO(saved));
    }

    /**
     * 获取最新健康数据
     * GET /api/health/latest?elderId=1
     */
    @GetMapping("/latest")
    @Operation(summary = "获取最新健康数据")
    public Result<HealthDataDTO> getLatestHealthData(@RequestParam Integer elderId) {
        HealthData data = healthDataService.getLatestHealthData(elderId);
        return Result.success(DtoConverter.convertToHealthDataDTO(data));
    }

    /**
     * 获取历史健康数据
     * GET /api/health/history?elderId=1&days=7
     */
    @GetMapping("/history")
    @Operation(summary = "获取历史健康数据")
    public Result<List<HealthDataDTO>> getHistoryHealthData(
            @RequestParam Integer elderId,
            @RequestParam(defaultValue = "7") Integer days) {

        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(days);

        List<HealthData> data = healthDataService.getHistoryHealthData(
                elderId, start, end);
        return Result.success(DtoConverter.convertToHealthDataDTOList(data));
    }

    /**
     * 获取首页仪表盘数据
     * GET /api/health/dashboard?userId=1
     */
    @GetMapping("/dashboard")
    @Operation(summary = "获取首页仪表盘数据")
    public Result<Map<String, Object>> getDashboardData(@RequestParam Integer userId) {
        Map<String, Object> dashboard = new HashMap<>();

        // 获取最新健康数据
        HealthData latest = healthDataService.getLatestHealthData(userId);

        dashboard.put("heartRate", latest != null ? latest.getHeartRate() : null);
        dashboard.put("steps", latest != null ? latest.getSteps() : 0);

        return Result.success(dashboard);
    }
}