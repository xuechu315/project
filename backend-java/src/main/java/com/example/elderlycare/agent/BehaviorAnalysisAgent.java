package com.example.elderlycare.agent;

import com.example.elderlycare.service.AiAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 行为分析Agent
 * 
 * 职责：分析老人佩戴设备的加速度/陀螺仪数据
 * - 跌倒检测：识别剧烈加速度变化，判定是否发生跌倒
 * - 静止检测：长时间无活动可能意味着晕厥或设备脱落
 * - 行为分类：区分正常活动、轻微晃动、剧烈冲击
 * 
 * 输入：加速度三轴数据 (m/s²)
 * 输出：行为分析结果 + 风险判断
 * 
 * 触发场景：
 *   1. 移动端上报加速度数据时实时分析
 *   2. 定时轮询最近数据，检测长时间静止
 */
@Component
public class BehaviorAnalysisAgent {

    private static final Logger log = LoggerFactory.getLogger(BehaviorAnalysisAgent.class);

    @Autowired
    private AiAnalysisService aiAnalysisService;

    /** 跌倒检测阈值：合成加速度超过此值判定为剧烈冲击 (m/s²) */
    private static final double FALL_THRESHOLD_HIGH = 25.0;
    /** 疑似跌倒阈值 */
    private static final double FALL_THRESHOLD_WARN = 15.0;
    /** 重力加速度参考值 (1g ≈ 9.8 m/s²) */
    private static final double GRAVITY = 9.8;
    /** 静止判定阈值：合成加速度接近1g (m/s²) */
    private static final double STILL_THRESHOLD = 1.5;

    /**
     * 分析加速度数据，检测跌倒风险
     *
     * @param x X轴加速度 (m/s²)
     * @param y Y轴加速度 (m/s²)
     * @param z Z轴加速度 (m/s²)
     * @return 行为分析结果
     */
    public BehaviorResult analyzeAcceleration(double x, double y, double z) {
        double magnitude = Math.sqrt(x * x + y * y + z * z);
        log.debug("加速度合成值: {}.2f m/s² (x={}.2f, y={}.2f, z={}.2f)", magnitude, x, y, z);

        // 规则判定
        if (magnitude > FALL_THRESHOLD_HIGH) {
            return new BehaviorResult(
                    BehaviorType.FALL_DETECTED,
                    magnitude,
                    "检测到剧烈冲击（合成加速度 " + String.format("%.1f", magnitude) + " m/s²），疑似跌倒！",
                    true);
        }

        if (magnitude > FALL_THRESHOLD_WARN) {
            // 调用 AI 进行辅助判断
            String aiResult = analyzeWithAi(x, y, z, magnitude);
            return new BehaviorResult(
                    BehaviorType.SUSPICIOUS_FALL,
                    magnitude,
                    "检测到较大加速度变化，" + aiResult,
                    aiResult.contains("跌倒"));
        }

        if (Math.abs(magnitude - GRAVITY) < STILL_THRESHOLD) {
            return new BehaviorResult(
                    BehaviorType.STILL,
                    magnitude,
                    "老人处于静止状态，活动正常。",
                    false);
        }

        if (magnitude > GRAVITY + 3) {
            return new BehaviorResult(
                    BehaviorType.ACTIVE_MOVEMENT,
                    magnitude,
                    "检测到活跃运动，老人正在进行日常活动。",
                    false);
        }

        return new BehaviorResult(
                BehaviorType.NORMAL_ACTIVITY,
                magnitude,
                "活动正常，未检测到异常。",
                false);
    }

    /**
     * 判断是否为长时间静止（可能预示晕厥或设备脱落）
     *
     * @param magnitude   当前合成加速度
     * @param stillMinutes 已持续静止的分钟数
     * @return 是否需要关注
     */
    public boolean isProlongedInactivity(double magnitude, int stillMinutes) {
        boolean isStill = Math.abs(magnitude - GRAVITY) < STILL_THRESHOLD;
        return isStill && stillMinutes >= 60; // 超过1小时无活动报警
    }

    /**
     * 调用 AI 做深度行为分析
     */
    private String analyzeWithAi(double x, double y, double z, double magnitude) {
        try {
            return aiAnalysisService.analyzeAcceleration(x, y, z);
        } catch (Exception e) {
            log.error("AI行为分析失败: {}", e.getMessage());
            return "AI分析暂不可用，请人工判断。";
        }
    }

    // ==================== 内部类型定义 ====================

    /**
     * 行为类型
     */
    public enum BehaviorType {
        /** 正常日常活动（走路、坐卧等） */
        NORMAL_ACTIVITY,
        /** 活跃运动（快走、做操等） */
        ACTIVE_MOVEMENT,
        /** 静止状态 */
        STILL,
        /** 疑似跌倒（需要AI或人工确认） */
        SUSPICIOUS_FALL,
        /** 确认跌倒 */
        FALL_DETECTED
    }

    /**
     * 行为分析结果
     */
    public static class BehaviorResult {
        private final BehaviorType type;
        private final double magnitude;
        private final String description;
        private final boolean fallRisk;

        public BehaviorResult(BehaviorType type, double magnitude, String description, boolean fallRisk) {
            this.type = type;
            this.magnitude = magnitude;
            this.description = description;
            this.fallRisk = fallRisk;
        }

        public BehaviorType getType() { return type; }
        public double getMagnitude() { return magnitude; }
        public String getDescription() { return description; }
        public boolean isFallRisk() { return fallRisk; }

        /** 是否属于需要通知家属/医生的异常行为 */
        public boolean needsNotification() {
            return type == BehaviorType.SUSPICIOUS_FALL || type == BehaviorType.FALL_DETECTED;
        }
    }
}
