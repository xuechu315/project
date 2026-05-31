package com.example.elderlycare.service;

/**
 * AI分析服务接口
 */
public interface AiAnalysisService {
    
    /**
     * 分析心率数据
     * @param heartRate 心率
     * @return 分析结果
     */
    String analyzeHeartRate(int heartRate);
    
    /**
     * 分析血压数据
     * @param systolic 收缩压
     * @param diastolic 舒张压
     * @return 分析结果
     */
    String analyzeBloodPressure(int systolic, int diastolic);

    /**
     * 分析加速度数据（仅后端使用，不返回给老人端展示）
     * @param x X轴加速度 (m/s²)
     * @param y Y轴加速度 (m/s²)
     * @param z Z轴加速度 (m/s²)
     * @return 分析结果
     */
    String analyzeAcceleration(double x, double y, double z);
}