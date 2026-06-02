package com.example.elderlycare.service;

import com.example.elderlycare.dto.request.EmergencyAssessmentRequest;
import com.example.elderlycare.dto.request.EmergencyDispatchRequest;
import com.example.elderlycare.dto.request.RoutePlanningRequest;
import com.example.elderlycare.dto.response.EmergencyDispatchResponse;
import com.example.elderlycare.dto.response.RiskAssessmentResponse;
import com.example.elderlycare.dto.response.RoutePlanningResponse;

/**
 * 应急响应服务接口
 */
public interface EmergencyService {

    /**
     * 风险评估 - 根据心率、血压、加速度数据判断紧急程度
     */
    RiskAssessmentResponse assessRisk(EmergencyAssessmentRequest request);

    /**
     * 路线规划 - 计算两点间距离与ETA
     */
    RoutePlanningResponse planRoute(RoutePlanningRequest request);

    /**
     * 应急调度 - 生成应急任务
     */
    EmergencyDispatchResponse dispatchEmergency(EmergencyDispatchRequest request);

    /**
     * 更新任务状态
     */
    void updateTaskStatus(Integer taskId, String status);

    /**
     * 计算两点间距离（Haversine公式）
     */
    double calculateDistance(double lat1, double lng1, double lat2, double lng2);

    /**
     * 估算到达时间
     */
    int estimateTravelTime(double distanceKm, String transportMode);
}
