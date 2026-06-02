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

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 应急响应Agent（主治医生角色）
 *
 * ==================== 核心设计原则 ====================
 * 一、冲突决策（分诊护士）：人工定规则，AI执行
 *    1. 人工制定优先级排序标准（AlertPriorityRule）
 *    2. AI只负责按规则自动排队、执行排序动作
 *    3. AI不能自己修改排序逻辑
 *
 * 二、应急响应（主治医生）：AI初评标准化通知
 *    1. AI自动初评风险等级（轻微/中度/危重）
 *    2. 轻微风险：AI全自动通知家属、医生，无需人工介入
 *    3. 中/危重风险：AI推送给后台工作人员，由人工决策处置
 *    4. 人工制定分级标准（阈值），AI照章执行
 *    5. 人工可修正AI判错的风险等级，优化规则
 *
 * ==================== 典型流程 ====================
 * 异常信号 → AI初评风险等级
 *   ├─ 轻微 → AI自动通知家属+医生（自动化）
 *   ├─ 中度 → AI推送给后台人工 → 人工决策（打120/上门急救/观察）
 *   └─ 危重 → AI推送预警 → 同步调度救援 + 人工介入
 *
 * ==================== 多异常排队流程 ====================
 * 多个异常同时涌入 → 进入事件队列 → AI按优先级规则自动排序 → 逐个处理
 */
@Component
public class EmergencyResponseAgent {

    private static final Logger log = LoggerFactory.getLogger(EmergencyResponseAgent.class);

    @Autowired
    private EmergencyService emergencyService;

    @Autowired
    private FamilyNotificationAgent familyNotificationAgent;

    @Autowired
    private StaffNotificationAgent staffNotificationAgent;

    // ==================== 分诊护士模块：优先级规则（人工定义） ====================

    /**
     * 告警类型优先级枚举（人工制定规则，AI不能修改）
     * 数值越小，优先级越高
     *
     * 人工定义规则：
     * - 跌倒告警 > SOS求助 > 心率异常 > 血压异常 > 体温异常 > 其他
     */
    public enum AlertType {
        FALL(1, "跌倒告警"),
        SOS(2, "SOS求助"),
        HEART_RATE_CRITICAL(3, "心率严重异常"),
        HEART_RATE_WARNING(4, "心率轻度异常"),
        BLOOD_PRESSURE_CRITICAL(5, "血压严重异常"),
        BLOOD_PRESSURE_WARNING(6, "血压轻度异常"),
        TEMPERATURE_CRITICAL(7, "体温严重异常"),
        TEMPERATURE_WARNING(8, "体温轻度异常"),
        BEHAVIOR_ANOMALY(9, "行为异常"),
        OTHER(10, "其他异常");

        private final int priority;  // 优先级数值，人工定义
        private final String description;

        AlertType(int priority, String description) {
            this.priority = priority;
            this.description = description;
        }

        public int getPriority() { return priority; }
        public String getDescription() { return description; }
    }

    /**
     * 风险等级枚举（人工制定分级标准）
     *
     * 人工定义分级阈值（见RiskLevelThreshold配置）：
     * - 轻微：指标轻度超标，无立即危险
     * - 中度：指标明显异常，需要关注
     * - 危重：指标严重超标，需要立即干预
     */
    public enum RiskLevel {
        MINOR("轻微", 1),      // AI全自动处理
        MODERATE("中度", 2),   // 推送人工决策
        CRITICAL("危重", 3);   // AI处理 + 人工介入

        private final String description;
        private final int level;

        RiskLevel(String description, int level) {
            this.description = description;
            this.level = level;
        }

        public String getDescription() { return description; }
        public int getLevel() { return level; }
    }

    /**
     * 风险分级阈值配置（人工制定，AI不能修改）
     * 这些阈值决定了什么指标算轻微/中度/危重
     */
    public static class RiskLevelThreshold {
        // 心率阈值
        public static final int HEART_RATE_MINOR_LOW = 50;
        public static final int HEART_RATE_MINOR_HIGH = 100;
        public static final int HEART_RATE_MODERATE_LOW = 40;
        public static final int HEART_RATE_MODERATE_HIGH = 120;
        // 超出范围即为危重

        // 血压收缩压阈值
        public static final int SYSTOLIC_MINOR_LOW = 90;
        public static final int SYSTOLIC_MINOR_HIGH = 140;
        public static final int SYSTOLIC_MODERATE_LOW = 80;
        public static final int SYSTOLIC_MODERATE_HIGH = 160;
        // 超出范围即为危重

        // 舒张压阈值
        public static final int DIASTOLIC_MINOR_LOW = 60;
        public static final int DIASTOLIC_MINOR_HIGH = 90;
        public static final int DIASTOLIC_MODERATE_LOW = 50;
        public static final int DIASTOLIC_MODERATE_HIGH = 100;

        // 体温阈值
        public static final float TEMPERATURE_MINOR_LOW = 36.0f;
        public static final float TEMPERATURE_MINOR_HIGH = 37.5f;
        public static final float TEMPERATURE_MODERATE_LOW = 35.0f;
        public static final float TEMPERATURE_MODERATE_HIGH = 38.5f;
    }

    // ==================== 分诊护士模块：事件队列 ====================

    /**
     * 告警事件（进入队列排队的基本单元）
     */
    public static class AlertEvent {
        private final String eventId;
        private final AlertType alertType;
        private final Integer userId;
        private final long timestamp;
        private final EmergencyAssessmentRequest originalRequest;
        private RiskLevel aiRiskLevel;  // AI初步判定的风险等级
        private RiskLevel finalRiskLevel; // 最终风险等级（人工可修正）
        private boolean processed;
        private String processedBy;  // "AI" 或 "MANUAL"
        private String notes;       // 备注（如人工修正原因）

        public AlertEvent(AlertType alertType, Integer userId, EmergencyAssessmentRequest request) {
            this.eventId = UUID.randomUUID().toString().substring(0, 8);
            this.alertType = alertType;
            this.userId = userId;
            this.timestamp = System.currentTimeMillis();
            this.originalRequest = request;
            this.processed = false;
        }

        // Getters
        public String getEventId() { return eventId; }
        public AlertType getAlertType() { return alertType; }
        public Integer getUserId() { return userId; }
        public long getTimestamp() { return timestamp; }
        public EmergencyAssessmentRequest getOriginalRequest() { return originalRequest; }
        public RiskLevel getAiRiskLevel() { return aiRiskLevel; }
        public RiskLevel getFinalRiskLevel() { return finalRiskLevel; }
        public boolean isProcessed() { return processed; }
        public String getProcessedBy() { return processedBy; }
        public String getNotes() { return notes; }

        // Setters
        public void setAiRiskLevel(RiskLevel aiRiskLevel) { this.aiRiskLevel = aiRiskLevel; }
        public void setFinalRiskLevel(RiskLevel finalRiskLevel) { this.finalRiskLevel = finalRiskLevel; }
        public void setProcessed(boolean processed) { this.processed = processed; }
        public void setProcessedBy(String processedBy) { this.processedBy = processedBy; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    /**
     * 告警事件队列（AI按规则自动排序）
     * 核心：人工定排序规则，AI只执行排序动作
     */
    private final CopyOnWriteArrayList<AlertEvent> alertQueue = new CopyOnWriteArrayList<>();
    private final Map<String, AlertEvent> eventStore = new ConcurrentHashMap<>();  // 用于按eventId查询

    /**
     * 将告警事件加入队列（AI自动执行）
     * 排序规则由人工定义（见compareEvents方法）
     */
    public String enqueueAlert(AlertType alertType, Integer userId, EmergencyAssessmentRequest request) {
        AlertEvent event = new AlertEvent(alertType, userId, request);
        eventStore.put(event.getEventId(), event);
        alertQueue.add(event);

        // AI自动按人工定义的规则排序
        sortQueue();

        log.info("告警事件入队: eventId={}, type={}, userId={}, 队列长度={}",
                event.getEventId(), alertType, userId, alertQueue.size());
        return event.getEventId();
    }

    /**
     * AI自动执行排序（按人工定义的优先级规则）
     * 规则：优先级数值小的排前面，同优先级按时间戳早的排前面
     */
    private void sortQueue() {
        alertQueue.sort((e1, e2) -> {
            // 首先按告警类型优先级排序（人工定义）
            int typeCompare = Integer.compare(e1.getAlertType().getPriority(), e2.getAlertType().getPriority());
            if (typeCompare != 0) return typeCompare;

            // 同类型按时间戳排序（早的在前）
            return Long.compare(e1.getTimestamp(), e2.getTimestamp());
        });
    }

    /**
     * 获取当前队列状态（供后台展示）
     */
    public List<AlertEvent> getQueueSnapshot() {
        return new ArrayList<>(alertQueue);
    }

    /**
     * 从队列中取出下一个待处理事件（AI执行）
     */
    public AlertEvent dequeueNext() {
        if (alertQueue.isEmpty()) return null;
        return alertQueue.remove(0);
    }

    /**
     * 标记事件为已处理
     */
    public void markEventProcessed(String eventId, String processedBy) {
        AlertEvent event = eventStore.get(eventId);
        if (event != null) {
            event.setProcessed(true);
            event.setProcessedBy(processedBy);
        }
    }

    /**
     * 人工修正风险等级（人工修正AI判错的特殊病例）
     * @param eventId 事件ID
     * @param correctedLevel 修正后的风险等级
     * @param reason 修正原因
     */
    public void correctRiskLevel(String eventId, RiskLevel correctedLevel, String reason) {
        AlertEvent event = eventStore.get(eventId);
        if (event != null) {
            log.info("人工修正风险等级: eventId={}, 原等级={}, 修正后={}, 原因={}",
                    eventId, event.getFinalRiskLevel(), correctedLevel, reason);
            event.setFinalRiskLevel(correctedLevel);
            event.setNotes(reason);
        }
    }

    // ==================== 应急响应Agent核心逻辑 ====================

    /**
     * 接收评估请求，AI自动初评风险等级并触发标准化响应
     *
     * @param request 风险评估请求（含心率、血压、加速度、行为描述等）
     * @return 综合应急响应结果
     */
    public EmergencyResponseResult assessAndRespond(EmergencyAssessmentRequest request) {
        log.info("EmergencyResponseAgent 接收评估请求: userId={}", request.getUserId());

        // 1. AI自动初评风险等级（按人工定义的阈值）
        RiskLevel riskLevel = assessRiskLevel(request);
        log.info("AI初评风险等级: userId={}, level={}", request.getUserId(), riskLevel);

        // 2. 根据AI评估的风险等级决定处理方式
        switch (riskLevel) {
            case MINOR:
                return handleMinorRisk(request, riskLevel);
            case MODERATE:
                return handleModerateRisk(request, riskLevel);
            case CRITICAL:
                return handleCriticalRisk(request, riskLevel);
            default:
                return new EmergencyResponseResult(
                        ResponseAction.MONITOR_ONLY,
                        riskLevel,
                        null,
                        "未知风险等级，保持监测"
                );
        }
    }

    /**
     * AI评估风险等级（按人工定义的阈值）
     * 轻微/中度/危重的判断标准由人工在RiskLevelThreshold中定义
     */
    private RiskLevel assessRiskLevel(EmergencyAssessmentRequest request) {
        // 如果有跌倒行为描述，直接判定为危重（人工规则）
        if (request.getBehaviorNote() != null &&
            (request.getBehaviorNote().contains("跌倒") || request.getBehaviorNote().contains("摔倒"))) {
            return RiskLevel.CRITICAL;
        }

        // SOS求助直接判定为危重
        if (Boolean.TRUE.equals(request.getSosRequest())) {
            return RiskLevel.CRITICAL;
        }

        // 计算综合风险评分
        int riskScore = 0;

        // 心率评估
        if (request.getHeartRate() != null) {
            int hr = request.getHeartRate();
            if (hr < RiskLevelThreshold.HEART_RATE_MODERATE_LOW || hr > RiskLevelThreshold.HEART_RATE_MODERATE_HIGH) {
                riskScore += 3;  // 危重指标
            } else if (hr < RiskLevelThreshold.HEART_RATE_MINOR_LOW || hr > RiskLevelThreshold.HEART_RATE_MINOR_HIGH) {
                riskScore += 2;  // 中度指标
            } else {
                riskScore += 1;  // 轻微异常
            }
        }

        // 血压评估
        if (request.getSystolic() != null && request.getDiastolic() != null) {
            int sys = request.getSystolic();
            int dia = request.getDiastolic();
            if (sys < RiskLevelThreshold.SYSTOLIC_MODERATE_LOW || sys > RiskLevelThreshold.SYSTOLIC_MODERATE_HIGH ||
                dia < RiskLevelThreshold.DIASTOLIC_MODERATE_LOW || dia > RiskLevelThreshold.DIASTOLIC_MODERATE_HIGH) {
                riskScore += 3;  // 危重指标
            } else if (sys < RiskLevelThreshold.SYSTOLIC_MINOR_LOW || sys > RiskLevelThreshold.SYSTOLIC_MINOR_HIGH ||
                       dia < RiskLevelThreshold.DIASTOLIC_MINOR_LOW || dia > RiskLevelThreshold.DIASTOLIC_MINOR_HIGH) {
                riskScore += 2;  // 中度指标
            } else {
                riskScore += 1;  // 轻微异常
            }
        }

        // 加速度异常检测（疑似跌倒）
        if (request.getAccelerationX() != null && request.getAccelerationY() != null && request.getAccelerationZ() != null) {
            double accel = Math.sqrt(
                request.getAccelerationX() * request.getAccelerationX() +
                request.getAccelerationY() * request.getAccelerationY() +
                request.getAccelerationZ() * request.getAccelerationZ()
            );
            // 加速度大于4 m/s² 可能表示跌倒
            if (accel > 6.0) {
                riskScore += 4;  // 强跌倒信号
            } else if (accel > 4.0) {
                riskScore += 2;  // 轻度异常
            }
        }

        // 根据综合评分判定风险等级
        if (riskScore >= 5) {
            return RiskLevel.CRITICAL;
        } else if (riskScore >= 3) {
            return RiskLevel.MODERATE;
        } else {
            return RiskLevel.MINOR;
        }
    }

    /**
     * 处理轻微风险（AI全自动处理）
     * 轻微：AI自动发消息通知家属、医生，全程自动化，人工不介入
     */
    private EmergencyResponseResult handleMinorRisk(EmergencyAssessmentRequest request, RiskLevel level) {
        log.info("处理轻微风险: userId={}", request.getUserId());

        // AI自动通知家属（轻微风险只通知家属，不调度救援）
        String message = String.format("【健康提醒】老人(%d)健康指标出现轻微异常，建议关注。",
                request.getUserId());
        familyNotificationAgent.notifyFamily(request.getUserId(), message, "minor_alert");

        // 将事件加入队列记录（由AI自动处理完成）
        String eventId = enqueueAlert(AlertType.OTHER, request.getUserId(), request);
        markEventProcessed(eventId, "AI_AUTO");

        return new EmergencyResponseResult(
                ResponseAction.AI_AUTO_MINOR,
                level,
                null,
                "轻微风险：AI已自动通知家属关注"
        );
    }

    /**
     * 处理中度风险（推送人工决策）
     * AI初评后推送给后台工作人员，由人工决定如何处置
     */
    private EmergencyResponseResult handleModerateRisk(EmergencyAssessmentRequest request, RiskLevel level) {
        log.info("处理中度风险: userId={}", request.getUserId());

        // 将事件加入队列，等待人工决策
        String eventId = enqueueAlert(AlertType.OTHER, request.getUserId(), request);
        AlertEvent event = eventStore.get(eventId);
        event.setAiRiskLevel(level);
        event.setFinalRiskLevel(level);  // 初始与AI判定一致，人工可修正

        // AI通知家属（提前告知）
        String familyMessage = String.format("【健康关注】老人(%d)健康指标出现中度异常，医护人员正在评估处理中，请保持电话畅通。",
                request.getUserId());
        familyNotificationAgent.notifyFamily(request.getUserId(), familyMessage, "moderate_alert");

        // 推送预警给后台工作人员（人工决策点）
        pushToStaffQueue(event);

        return new EmergencyResponseResult(
                ResponseAction.PENDING_HUMAN_DECISION,
                level,
                null,
                "中度风险：已推送给后台工作人员，等待人工决策处置"
        );
    }

    /**
     * 处理危重风险（AI处理 + 人工介入）
     * AI立即调度救援，同时推送预警给人工
     */
    private EmergencyResponseResult handleCriticalRisk(EmergencyAssessmentRequest request, RiskLevel level) {
        log.info("处理危重风险: userId={}", request.getUserId());

        // 将事件加入队列
        String eventId = enqueueAlert(AlertType.OTHER, request.getUserId(), request);
        AlertEvent event = eventStore.get(eventId);
        event.setAiRiskLevel(level);
        event.setFinalRiskLevel(level);

        // 1. AI自动调度应急救援（无需人工确认）
        EmergencyDispatchRequest dispatchReq = new EmergencyDispatchRequest();
        dispatchReq.setUserId(request.getUserId());
        dispatchReq.setEmergencyType("critical_health");
        dispatchReq.setEmergencyLevel("危重");
        dispatchReq.setUserLat(31.2304);  // 默认位置，可从request获取
        dispatchReq.setUserLng(121.4737);

        EmergencyDispatchResponse dispatch = emergencyService.dispatchEmergency(dispatchReq);

        // 2. AI自动通知家属和社区医生
        String message = String.format("【紧急通知】老人(%d)健康状况出现危重异常，救援已派出，请立即联系或前往现场！",
                request.getUserId());
        familyNotificationAgent.notifyAll(request.getUserId(), message, "critical_emergency");

        // 3. 推送预警给后台工作人员（人工监控和介入）
        pushToStaffQueue(event);

        return new EmergencyResponseResult(
                ResponseAction.EMERGENCY_DISPATCHED_WITH_HUMAN,
                level,
                dispatch,
                "危重风险：AI已自动调度救援 + 已通知家属 + 已推送人工监控"
        );
    }

    /**
     * 推送事件到后台工作人员队列（人工决策点）
     */
    private void pushToStaffQueue(AlertEvent event) {
        log.info("推送至后台人工队列: eventId={}, riskLevel={}", event.getEventId(), event.getFinalRiskLevel());
        // 调用 StaffNotificationAgent 推送预警给所有在线工作人员
        staffNotificationAgent.notifyStaffFromEvent(event);
    }

    // ==================== 对外接口方法 ====================

    /**
     * 处理来自 HealthMonitorAgent 的健康预警
     */
    public void handleHealthAlert(Integer userId, String alertMessage,
                                   HealthMonitorAgent.AlertLevel level) {
        log.info("处理健康预警: userId={}, level={}", userId, level);

        EmergencyAssessmentRequest req = new EmergencyAssessmentRequest();
        req.setUserId(userId);
        req.setBehaviorNote(alertMessage);

        // 根据告警级别确定告警类型
        AlertType alertType = AlertType.OTHER;
        switch (level) {
            case CRITICAL:
                alertType = AlertType.HEART_RATE_CRITICAL;
                break;
            case WARNING:
                alertType = AlertType.HEART_RATE_WARNING;
                break;
            default:
                break;
        }

        enqueueAlert(alertType, userId, req);
        assessAndRespond(req);
    }

    /**
     * 处理来自 BehaviorAnalysisAgent 的跌倒预警
     */
    public void handleFallAlert(Integer userId,
                                 BehaviorAnalysisAgent.BehaviorResult behaviorResult) {
        log.info("处理跌倒预警: userId={}, behaviorType={}", userId, behaviorResult.getType());

        EmergencyAssessmentRequest req = new EmergencyAssessmentRequest();
        req.setUserId(userId);
        req.setHeartRate(null);  // 跌倒场景可能无实时心率
        req.setBehaviorNote(behaviorResult.getDescription());

        assessAndRespond(req);
    }

    /**
     * 处理主动 SOS 求助
     */
    public void handleSosRequest(Integer userId, String location) {
        log.info("处理SOS求助: userId={}, location={}", userId, location);

        EmergencyAssessmentRequest req = new EmergencyAssessmentRequest();
        req.setUserId(userId);
        req.setSosRequest(true);
        if (location != null) {
            req.setBehaviorNote("SOS求助位置: " + location);
        }

        assessAndRespond(req);
    }

    // ==================== 人工决策支持接口 ====================

    /**
     * 获取待人工决策的事件列表
     */
    public List<AlertEvent> getPendingEvents() {
        return alertQueue.stream()
                .filter(e -> !e.isProcessed() && e.getFinalRiskLevel() != RiskLevel.MINOR)
                .collect(Collectors.toList());
    }

    /**
     * 人工决策处置（中/危重风险时人工介入）
     * @param eventId 事件ID
     * @param decision 决策（DISPATCH_AMBULANCE / HOME_VISIT / MONITOR / DISMISS）
     * @param notes 决策备注
     */
    public void humanDecision(String eventId, String decision, String notes) {
        AlertEvent event = eventStore.get(eventId);
        if (event == null) {
            log.warn("人工决策失败，未找到事件: eventId={}", eventId);
            return;
        }

        log.info("人工决策: eventId={}, decision={}, riskLevel={}, notes={}",
                eventId, decision, event.getFinalRiskLevel(), notes);
        event.setNotes(notes);

        switch (decision) {
            case "DISPATCH_AMBULANCE":
                // 人工决策：派救护车
                EmergencyDispatchRequest dispatchReq = new EmergencyDispatchRequest();
                dispatchReq.setUserId(event.getUserId());
                dispatchReq.setEmergencyType("human_dispatched");
                dispatchReq.setEmergencyLevel(event.getFinalRiskLevel().getDescription());
                dispatchReq.setUserLat(31.2304);
                dispatchReq.setUserLng(121.4737);
                emergencyService.dispatchEmergency(dispatchReq);
                familyNotificationAgent.notifyAll(event.getUserId(),
                        "【人工决策】已派出急救小组前往老人位置，请保持联系。", "human_dispatched");
                break;

            case "HOME_VISIT":
                // 人工决策：上门查看
                familyNotificationAgent.notifyCommunityDoctor(event.getUserId(),
                        "【人工决策】老人需要上门健康评估，请安排医护人员前往。", "home_visit");
                break;

            case "MONITOR":
                // 人工决策：继续观察
                familyNotificationAgent.notifyFamily(event.getUserId(),
                        "【人工决策】医护人员评估后建议继续观察，如有变化请立即通知。", "monitor");
                break;

            case "DISMISS":
                // 人工决策：误报/撤销
                log.info("人工判定为误报: eventId={}", eventId);
                break;

            default:
                log.warn("未知决策类型: {}", decision);
        }

        markEventProcessed(eventId, "HUMAN");
    }

    // ==================== 内部类型定义 ====================

    /** 应急响应动作 */
    public enum ResponseAction {
        /** 仅监测，无需干预（低风险） */
        MONITOR_ONLY,
        /** AI自动处理轻微异常 */
        AI_AUTO_MINOR,
        /** 等待人工决策（中度风险） */
        PENDING_HUMAN_DECISION,
        /** 已调度救援 + 人工介入（危重风险） */
        EMERGENCY_DISPATCHED_WITH_HUMAN
    }

    /** 应急响应结果 */
    public static class EmergencyResponseResult {
        private final ResponseAction action;
        private final RiskLevel riskLevel;
        private final EmergencyDispatchResponse dispatch;
        private final String summary;

        public EmergencyResponseResult(ResponseAction action, RiskLevel riskLevel,
                                        EmergencyDispatchResponse dispatch, String summary) {
            this.action = action;
            this.riskLevel = riskLevel;
            this.dispatch = dispatch;
            this.summary = summary;
        }

        public ResponseAction getAction() { return action; }
        public RiskLevel getRiskLevel() { return riskLevel; }
        public EmergencyDispatchResponse getDispatch() { return dispatch; }
        public String getSummary() { return summary; }

        public boolean isEmergency() {
            return action == ResponseAction.EMERGENCY_DISPATCHED_WITH_HUMAN;
        }

        public boolean needsHumanIntervention() {
            return action == ResponseAction.PENDING_HUMAN_DECISION ||
                   action == ResponseAction.EMERGENCY_DISPATCHED_WITH_HUMAN;
        }
    }
}
