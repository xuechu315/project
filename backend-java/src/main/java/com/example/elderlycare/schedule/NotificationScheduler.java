package com.example.elderlycare.schedule;

import com.example.elderlycare.agent.FamilyNotificationAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 通知定时任务
 *
 * 职责：
 * - 定期检查未确认的通知，超时自动升级重通知
 * - 覆盖紧急通知（emergency）、升级通知（emergency_escalation）、健康预警（alert）三种类型
 */
@Component
public class NotificationScheduler {

    private static final Logger log = LoggerFactory.getLogger(NotificationScheduler.class);

    @Autowired
    private FamilyNotificationAgent familyNotificationAgent;

    /**
     * 每 2 分钟检查一次紧急通知的超时升级
     * 超过 5 分钟仍未确认的紧急通知，自动升级并再次通知
     */
    @Scheduled(fixedRate = 120000)
    public void escalateUnacknowledgedEmergencies() {
        log.debug("开始检查未确认的紧急通知...");
        int escalated = familyNotificationAgent.escalateUnacknowledgedNotifications(5, "emergency");
        if (escalated > 0) {
            log.warn("紧急通知升级完成: {} 条通知已升级", escalated);
        }
    }

    /**
     * 每 3 分钟检查一次已升级但未确认的通知
     * 超过 5 分钟仍未确认的升级通知，继续二次升级
     */
    @Scheduled(fixedRate = 180000)
    public void escalateUnacknowledgedEscalations() {
        log.debug("开始检查未确认的升级通知...");
        int escalated = familyNotificationAgent.escalateUnacknowledgedNotifications(5, "emergency_escalation");
        if (escalated > 0) {
            log.warn("二次升级通知完成: {} 条通知已再次升级", escalated);
        }
    }

    /**
     * 每 5 分钟检查一次健康预警（alert 类型）的超时升级
     * 超过 10 分钟仍未确认的预警通知，自动升级重通知
     */
    @Scheduled(fixedRate = 300000)
    public void escalateUnacknowledgedAlerts() {
        log.debug("开始检查未确认的健康预警通知...");
        int escalated = familyNotificationAgent.escalateUnacknowledgedNotifications(10, "alert");
        if (escalated > 0) {
            log.warn("健康预警升级完成: {} 条通知已升级", escalated);
        }
    }
}
