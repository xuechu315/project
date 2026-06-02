package com.example.elderlycare.agent;

import com.example.elderlycare.entity.ContactRecord;
import com.example.elderlycare.repository.ContactRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 后台工作人员通知Agent
 *
 * 职责：在中度/危重风险时，推送预警给后台工作人员
 * - 接收来自 EmergencyResponseAgent 的预警事件
 * - 维护在线工作人员列表（通过 WebSocket 连接）
 * - 推送预警消息给所有在线工作人员
 * - 记录通知历史
 *
 * 工作流程：
 *   EmergencyResponseAgent（判定中/危重）
 *     → StaffNotificationAgent.notifyStaff()
 *     → 推送给所有在线工作人员
 *     → 工作人员人工决策 → EmergencyResponseAgent.humanDecision()
 */
@Component
public class StaffNotificationAgent {

    private static final Logger log = LoggerFactory.getLogger(StaffNotificationAgent.class);

    @Autowired
    private ContactRecordRepository contactRecordRepository;

    /**
     * 在线工作人员列表（实际应通过 WebSocket 会话管理）
     * 此处简化实现
     */
    private final CopyOnWriteArrayList<StaffSession> onlineStaff = new CopyOnWriteArrayList<>();

    /**
     * 工作人员会话（简化版）
     */
    public static class StaffSession {
        private final String sessionId;
        private final String staffName;
        private final LocalDateTime loginTime;
        private boolean active;

        public StaffSession(String sessionId, String staffName) {
            this.sessionId = sessionId;
            this.staffName = staffName;
            this.loginTime = LocalDateTime.now();
            this.active = true;
        }

        public String getSessionId() { return sessionId; }
        public String getStaffName() { return staffName; }
        public LocalDateTime getLoginTime() { return loginTime; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
    }

    /**
     * 推送预警给所有在线工作人员
     *
     * @param eventId       事件ID
     * @param userId        老人用户ID
     * @param riskLevel     风险等级（轻微/中度/危重）
     * @param alertType     告警类型
     * @param description   预警描述
     * @return 成功通知的工作人员数量
     */
    public int notifyStaff(String eventId, Integer userId, String riskLevel,
                          String alertType, String description) {
        // 构建预警消息
        String message = buildAlertMessage(eventId, userId, riskLevel, alertType, description);

        // 保存通知记录
        saveStaffNotificationRecord(eventId, userId, message);

        // 推送给所有在线工作人员
        int count = 0;
        for (StaffSession session : onlineStaff) {
            if (session.isActive()) {
                try {
                    // TODO: 通过 WebSocket 推送实际消息
                    // webSocketService.sendToSession(session.getSessionId(), message);
                    log.info("推送预警给工作人员: sessionId={}, staffName={}, message={}",
                            session.getSessionId(), session.getStaffName(), message);
                    count++;
                } catch (Exception e) {
                    log.error("推送预警失败: sessionId={}, error={}",
                            session.getSessionId(), e.getMessage());
                }
            }
        }

        log.info("预警推送完成: eventId={}, 发送给了 {} 位工作人员", eventId, count);
        return count;
    }

    /**
     * 推送预警给 EmergencyResponseAgent.AlertEvent
     */
    public int notifyStaffFromEvent(EmergencyResponseAgent.AlertEvent event) {
        String alertType = event.getAlertType() != null ?
                event.getAlertType().getDescription() : "未知";
        String riskLevel = event.getFinalRiskLevel() != null ?
                event.getFinalRiskLevel().getDescription() : "未知";
        String description = event.getOriginalRequest() != null &&
                event.getOriginalRequest().getBehaviorNote() != null ?
                event.getOriginalRequest().getBehaviorNote() : "无详细描述";

        return notifyStaff(event.getEventId(), event.getUserId(), riskLevel, alertType, description);
    }

    /**
     * 构建预警消息
     */
    private String buildAlertMessage(String eventId, Integer userId, String riskLevel,
                                    String alertType, String description) {
        StringBuilder sb = new StringBuilder();
        sb.append("【").append(riskLevel).append("预警】");
        sb.append("事件ID: ").append(eventId).append("\n");
        sb.append("老人ID: ").append(userId).append("\n");
        sb.append("告警类型: ").append(alertType).append("\n");
        sb.append("风险等级: ").append(riskLevel).append("\n");
        sb.append("详细描述: ").append(description).append("\n");
        sb.append("请及时处理！");
        return sb.toString();
    }

    /**
     * 保存工作人员通知记录
     */
    private void saveStaffNotificationRecord(String eventId, Integer userId, String message) {
        try {
            ContactRecord record = new ContactRecord();
            record.setUserId(userId);
            record.setType("staff_notification");
            record.setStatus("sent");
            record.setMessage(message);
            record.setTargetName("后台工作人员");
            contactRecordRepository.save(record);
        } catch (Exception e) {
            log.error("保存工作人员通知记录失败: eventId={}, error={}", eventId, e.getMessage());
        }
    }

    // ==================== 工作人员会话管理 ====================

    /**
     * 工作人员上线
     */
    public void staffLogin(String sessionId, String staffName) {
        StaffSession session = new StaffSession(sessionId, staffName);
        onlineStaff.add(session);
        log.info("工作人员上线: sessionId={}, staffName={}, 当前在线人数={}",
                sessionId, staffName, onlineStaff.size());
    }

    /**
     * 工作人员下线
     */
    public void staffLogout(String sessionId) {
        onlineStaff.removeIf(s -> s.getSessionId().equals(sessionId));
        log.info("工作人员下线: sessionId={}, 当前在线人数={}",
                sessionId, onlineStaff.size());
    }

    /**
     * 获取当前在线工作人员数量
     */
    public int getOnlineStaffCount() {
        return (int) onlineStaff.stream().filter(StaffSession::isActive).count();
    }

    /**
     * 获取待处理事件列表（供工作人员界面展示）
     */
    public List<AlertTask> getPendingTasks() {
        // 这里应该从 EmergencyResponseAgent 获取
        // 简化实现，返回空列表
        return List.of();
    }

    /**
     * 预警任务（供工作人员界面展示）
     */
    public static class AlertTask {
        private final String eventId;
        private final Integer userId;
        private final String riskLevel;
        private final String alertType;
        private final String description;
        private final long timestamp;

        public AlertTask(String eventId, Integer userId, String riskLevel,
                        String alertType, String description, long timestamp) {
            this.eventId = eventId;
            this.userId = userId;
            this.riskLevel = riskLevel;
            this.alertType = alertType;
            this.description = description;
            this.timestamp = timestamp;
        }

        public String getEventId() { return eventId; }
        public Integer getUserId() { return userId; }
        public String getRiskLevel() { return riskLevel; }
        public String getAlertType() { return alertType; }
        public String getDescription() { return description; }
        public long getTimestamp() { return timestamp; }
    }
}
