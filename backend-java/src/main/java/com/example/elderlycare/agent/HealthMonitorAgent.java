package com.example.elderlycare.agent;

import com.example.elderlycare.entity.HealthData;
import com.example.elderlycare.service.AiAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 健康监测Agent
 * 
 * 职责：持续监测老人的心率、血压、步数等健康数据
 * - 基于规则快速检测异常指标（瞬时判断）
 * - 对可疑数据调用AI进行深度分析
 * - 输出预警等级，供 EmergencyResponseAgent 决策
 * 
 * 工作流程：HealthDataController 接收数据 → 数据入库（HealthDataServiceImpl）
 *        → HealthMonitorAgent 分析异常 → 异常时触发 EmergencyResponseAgent
 */
@Component
public class HealthMonitorAgent {

    private static final Logger log = LoggerFactory.getLogger(HealthMonitorAgent.class);

    @Autowired
    private AiAnalysisService aiAnalysisService;

    // ==================== 正常范围阈值 ====================
    /** 心率下限（心动过缓警戒线） */
    private static final int HR_LOW = 40;
    /** 心率上限（心动过速警戒线） */
    private static final int HR_HIGH = 180;
    /** 收缩压下限 */
    private static final int SYS_LOW = 70;
    /** 收缩压上限 */
    private static final int SYS_HIGH = 190;
    /** 舒张压下限 */
    private static final int DIA_LOW = 40;
    /** 舒张压上限 */
    private static final int DIA_HIGH = 120;

    /**
     * 监测单条健康数据，返回预警等级和分析建议
     *
     * @param data 健康数据实体
     * @return 监测结果（等级 + AI 建议）
     */
    public HealthMonitorResult monitor(HealthData data) {
        log.debug("HealthMonitorAgent 监测数据: userId={}, heartRate={}, bp={}/{}",
                data.getUserId(), data.getHeartRate(),
                data.getSystolicPressure(), data.getDiastolicPressure());

        // 1. 规则引擎快速检测
        AlertLevel level = detectAnomaly(data);
        if (level == AlertLevel.NORMAL) {
            return new HealthMonitorResult(AlertLevel.NORMAL, null);
        }

        // 2. 异常时调用 AI 分析，获取可读的健康建议
        String aiSuggestion = analyzeWithAi(data, level);
        log.warn("健康数据异常 [{}]: userId={}, AI分析={}", level, data.getUserId(), aiSuggestion);

        return new HealthMonitorResult(level, aiSuggestion);
    }

    /**
     * 规则快速检测 —— 不依赖外部API，低延迟
     */
    private AlertLevel detectAnomaly(HealthData data) {
        // 心率检测
        if (data.getHeartRate() != null) {
            int hr = data.getHeartRate();
            if (hr < HR_LOW || hr > HR_HIGH) {
                return AlertLevel.CRITICAL;
            }
            if (hr < 50 || hr > 140) {
                return AlertLevel.WARNING;
            }
        }

        // 血压检测
        if (data.getSystolicPressure() != null && data.getDiastolicPressure() != null) {
            int sys = data.getSystolicPressure();
            int dia = data.getDiastolicPressure();
            if (sys < SYS_LOW || sys > SYS_HIGH || dia < DIA_LOW || dia > DIA_HIGH) {
                return AlertLevel.CRITICAL;
            }
            if (sys > 160 || dia > 100 || sys < 80) {
                return AlertLevel.WARNING;
            }
        }

        return AlertLevel.NORMAL;
    }

    /**
     * 调用 AI 做深度分析，生成可读建议
     */
    private String analyzeWithAi(HealthData data, AlertLevel level) {
        try {
            if (data.getHeartRate() != null && isAbnormalHeartRate(data.getHeartRate())) {
                return aiAnalysisService.analyzeHeartRate(data.getHeartRate());
            }
            if (data.getSystolicPressure() != null && data.getDiastolicPressure() != null
                    && isAbnormalBloodPressure(data.getSystolicPressure(), data.getDiastolicPressure())) {
                return aiAnalysisService.analyzeBloodPressure(
                        data.getSystolicPressure(), data.getDiastolicPressure());
            }
            // 各项指标单独看都正常但综合有异常 → 给通用提示
            return "检测到健康数据异常，建议持续观察。";
        } catch (Exception e) {
            log.error("AI分析调用失败: {}", e.getMessage());
            return "检测到健康数据异常，AI分析暂时不可用，请人工核查。";
        }
    }

    private boolean isAbnormalHeartRate(int hr) {
        return hr < 50 || hr > 140;
    }

    private boolean isAbnormalBloodPressure(int sys, int dia) {
        return sys > 160 || dia > 100 || sys < 80 || dia < 50;
    }

    // ==================== 内部类型定义 ====================

    /**
     * 预警等级
     */
    public enum AlertLevel {
        /** 数据正常，无需处理 */
        NORMAL,
        /** 轻度异常，建议关注 */
        WARNING,
        /** 严重异常，需要立即响应 */
        CRITICAL
    }

    /**
     * 监测结果
     */
    public static class HealthMonitorResult {
        private final AlertLevel level;
        private final String aiSuggestion;

        public HealthMonitorResult(AlertLevel level, String aiSuggestion) {
            this.level = level;
            this.aiSuggestion = aiSuggestion;
        }

        public AlertLevel getLevel() { return level; }
        public String getAiSuggestion() { return aiSuggestion; }

        /** 是否安全（无异常） */
        public boolean isSafe() { return level == AlertLevel.NORMAL; }
        /** 是否需要通知家属/医生 */
        public boolean needsNotification() { return level != AlertLevel.NORMAL; }
        /** 是否紧急 */
        public boolean isCritical() { return level == AlertLevel.CRITICAL; }
    }
}
