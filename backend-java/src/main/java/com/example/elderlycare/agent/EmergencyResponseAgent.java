package com.example.elderlycare.agent;

import com.example.elderlycare.dto.request.EmergencyAssessmentRequest;
import com.example.elderlycare.dto.request.EmergencyDispatchRequest;
import com.example.elderlycare.dto.response.EmergencyDispatchResponse;
import com.example.elderlycare.dto.response.RiskAssessmentResponse;
import com.example.elderlycare.service.EmergencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 应急响应Agent
 *
 * 职责：协调应急响应的全流程
 * - 接收来自 HealthMonitorAgent / BehaviorAnalysisAgent / SOS 的异常信号
 * - 调用 EmergencyService 进行风险评估和应急调度
 * - 协调 FamilyNotificationAgent 通知家属和社区医生
 * - 管理应急任务的升级策略
 *
 * 典型流程：
 *   异常信号 → assessAndRespond() → 高风险？
 *     ├─ 否 → 仅记录，可选通知家属关注
 *     └─ 是 → dispatchEmergency() → FamilyNotificationAgent.notifyAll() → 监控响应状态
 */
@Component
public class EmergencyResponseAgent {

    private static final Logger log = LoggerFactory.getLogger(EmergencyResponseAgent.class);

    @Autowired
    private EmergencyService emergencyService;

    @Autowired
    private FamilyNotificationAgent familyNotificationAgent;

    /**
     * 接收评估请求，综合判断风险并自动触发响应
     *
     * @param request 风险评估请求（含心率、血压、加速度、行为描述等）
     * @return 综合应急响应结果
     */
    public EmergencyResponseResult assessAndRespond(EmergencyAssessmentRequest request) {
        log.info("EmergencyResponseAgent 接收评估请求: userId={}", request.getUserId());

        // 1. 调用 EmergencyService 进行 AI 风险评估
        RiskAssessmentResponse risk = emergencyService.assessRisk(request);
        log.info("风险评估结果: level={}, score={}, needEmergency={}",
                risk.getRiskLevel(), risk.getRiskScore(), risk.getNeedEmergency());

        // 2. 根据评估结果决定下一步
        if (risk.getNeedEmergency()) {
            // 高风险：调度 + 全面通知
            EmergencyDispatchRequest dispatchReq = new EmergencyDispatchRequest();
            dispatchReq.setUserId(request.getUserId());
            dispatchReq.setEmergencyType("medical");
            dispatchReq.setEmergencyLevel(risk.getRiskLevel());
            // 获取最新位置（可从 request 或数据库获取，此处简化使用默认值）
            dispatchReq.setUserLat(31.2304);
            dispatchReq.setUserLng(121.4737);

            EmergencyDispatchResponse dispatch = emergencyService.dispatchEmergency(dispatchReq);

            // 自动通知家属 + 社区医生
            String message = String.format("【紧急通知】老人(%d)出现%s状况：%s。建议：%s",
                    request.getUserId(), risk.getRiskLevel(), risk.getAnalysis(), risk.getRecommendation());
            familyNotificationAgent.notifyAll(request.getUserId(), message, "emergency");

            return new EmergencyResponseResult(
                    ResponseAction.EMERGENCY_DISPATCHED,
                    risk,
                    dispatch,
                    "紧急情况已处理：已调度救援 + 已通知家属和社区医生"
            );
        } else if ("紧急".equals(risk.getRiskLevel())) {
            // 中高风险：通知家属关注，暂不调度
            String message = String.format("【健康提醒】老人(%d)健康指标出现异常：%s。建议：%s",
                    request.getUserId(), risk.getAnalysis(), risk.getRecommendation());
            familyNotificationAgent.notifyFamily(request.getUserId(), message, "alert");

            return new EmergencyResponseResult(
                    ResponseAction.FAMILY_NOTIFIED,
                    risk,
                    null,
                    "已通知家属关注老人状况"
            );
        } else {
            // 低风险：仅记录
            return new EmergencyResponseResult(
                    ResponseAction.MONITOR_ONLY,
                    risk,
                    null,
                    "风险较低，继续常规监测"
            );
        }
    }

    /**
     * 处理来自 HealthMonitorAgent 的健康预警
     */
    public void handleHealthAlert(Integer userId, String alertMessage,
                                   HealthMonitorAgent.AlertLevel level) {
        log.info("处理健康预警: userId={}, level={}", userId, level);

        switch (level) {
            case CRITICAL:
                // 构建评估请求并触发完整响应流程
                EmergencyAssessmentRequest req = new EmergencyAssessmentRequest();
                req.setUserId(userId);
                // alertMessage 已包含 AI 建议
                req.setBehaviorNote(alertMessage);
                assessAndRespond(req);
                break;
            case WARNING:
                String msg = String.format("【健康预警】老人(%d)健康指标轻度异常：%s",
                        userId, alertMessage);
                familyNotificationAgent.notifyFamily(userId, msg, "alert");
                break;
            default:
                // NORMAL 不做处理
                break;
        }
    }

    /**
     * 处理来自 BehaviorAnalysisAgent 的跌倒预警
     */
    public void handleFallAlert(Integer userId,
                                 BehaviorAnalysisAgent.BehaviorResult behaviorResult) {
        log.info("处理跌倒预警: userId={}, behaviorType={}", userId, behaviorResult.getType());

        // 跌倒事件统一按紧急情况处理
        EmergencyAssessmentRequest req = new EmergencyAssessmentRequest();
        req.setUserId(userId);
        req.setHeartRate(null); // 跌倒场景下可能无法获取实时心率
        req.setBehaviorNote(behaviorResult.getDescription());

        assessAndRespond(req);
    }

    /**
     * 处理主动 SOS 求助
     */
    public void handleSosRequest(Integer userId, String location) {
        log.info("处理SOS求助: userId={}, location={}", userId, location);

        String message = String.format("【SOS紧急求助】老人(%d) 正在通过 SOS 发起求助！位置：%s",
                userId, location != null ? location : "未知");
        familyNotificationAgent.notifyAll(userId, message, "emergency");
    }

    // ==================== 内部类型定义 ====================

    /** 应急响应动作 */
    public enum ResponseAction {
        /** 仅监测，无需干预 */
        MONITOR_ONLY,
        /** 已通知家属 */
        FAMILY_NOTIFIED,
        /** 已调度救援 */
        EMERGENCY_DISPATCHED
    }

    /** 应急响应结果 */
    public static class EmergencyResponseResult {
        private final ResponseAction action;
        private final RiskAssessmentResponse riskAssessment;
        private final EmergencyDispatchResponse dispatch;
        private final String summary;

        public EmergencyResponseResult(ResponseAction action,
                                        RiskAssessmentResponse riskAssessment,
                                        EmergencyDispatchResponse dispatch,
                                        String summary) {
            this.action = action;
            this.riskAssessment = riskAssessment;
            this.dispatch = dispatch;
            this.summary = summary;
        }

        public ResponseAction getAction() { return action; }
        public RiskAssessmentResponse getRiskAssessment() { return riskAssessment; }
        public EmergencyDispatchResponse getDispatch() { return dispatch; }
        public String getSummary() { return summary; }

        public boolean isEmergency() { return action == ResponseAction.EMERGENCY_DISPATCHED; }
    }
}
