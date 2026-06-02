package com.example.elderlycare.agent;

import com.example.elderlycare.entity.ContactRecord;
import com.example.elderlycare.entity.Doctor;
import com.example.elderlycare.entity.ElderDoctorRelation;
import com.example.elderlycare.entity.FamilyMember;
import com.example.elderlycare.repository.ContactRecordRepository;
import com.example.elderlycare.repository.DoctorRepository;
import com.example.elderlycare.repository.ElderDoctorRelationRepository;
import com.example.elderlycare.repository.FamilyMemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 家属通知Agent
 *
 * 职责：负责在紧急或异常情况下自动联系老人的家属和社区医生
 * - 查询老人绑定的家属列表，记录通知
 * - 查询老人关联的社区医生，记录通知
 * - 支持紧急通知的升级策略（超时未确认则再次通知）
 * - 提供通知历史查询和状态管理
 * - 支持异步批量通知
 *
 * 工作流：
 *   EmergencyResponseAgent / SOSServiceImpl → 本 Agent
 *     → 查询家属/医生列表 → 创建通知记录 → 标记状态
 *     → 紧急场景 5 分钟未确认自动升级重通知
 */
@Component
public class FamilyNotificationAgent {

    private static final Logger log = LoggerFactory.getLogger(FamilyNotificationAgent.class);

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Autowired
    private ElderDoctorRelationRepository elderDoctorRelationRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ContactRecordRepository contactRecordRepository;

    // ==================== 对外通知方法 ====================

    /**
     * 通知所有家属（通用方法）
     * 保存通知记录到数据库
     *
     * @param userId  老人用户ID
     * @param message 通知内容
     * @param type    通知类型（emergency / alert / info）
     * @return 成功通知的家属数量
     */
    public int notifyFamily(Integer userId, String message, String type) {
        List<FamilyMember> familyMembers = familyMemberRepository.findByUserId(userId);
        if (familyMembers.isEmpty()) {
            log.warn("老人 {} 未绑定任何家属，无法通知家属", userId);
            return 0;
        }

        int count = 0;
        for (FamilyMember member : familyMembers) {
            try {
                saveContactRecord(
                        userId,
                        member.getFamilyId(),   // 使用家属的用户ID
                        type,
                        "sent",
                        message,
                        member.getName(),
                        member.getPhone()
                );
                log.info("已通知家属: userId={}, familyId={}, name={}, phone={}",
                        userId, member.getFamilyId(), member.getName(), member.getPhone());
                count++;
            } catch (Exception e) {
                log.error("通知家属失败: userId={}, familyMemberId={}, error={}",
                        userId, member.getId(), e.getMessage());
            }
        }
        return count;
    }

    /**
     * 通知社区医生
     * 保存通知记录到数据库
     *
     * @param userId  老人用户ID
     * @param message 通知内容
     * @param type    通知类型
     * @return 成功通知的医生数量
     */
    public int notifyCommunityDoctor(Integer userId, String message, String type) {
        List<ElderDoctorRelation> relations = elderDoctorRelationRepository.findByElderId(userId);
        if (relations.isEmpty()) {
            log.warn("老人 {} 未绑定社区医生，无法通知医生", userId);
            return 0;
        }

        int count = 0;
        for (ElderDoctorRelation relation : relations) {
            try {
                // 查询医生详细信息
                Doctor doctor = doctorRepository.findById(relation.getDoctorId()).orElse(null);
                String doctorName = (doctor != null) ? doctor.getName() : "医生(ID=" + relation.getDoctorId() + ")";
                String doctorPhone = (doctor != null) ? doctor.getPhone() : "";

                saveContactRecord(
                        userId,
                        relation.getDoctorId(),  // 使用医生ID
                        "doctor_notification",
                        "sent",
                        message,
                        doctorName,
                        doctorPhone
                );

                log.info("已通知社区医生: userId={}, doctorId={}, name={}, phone={}",
                        userId, relation.getDoctorId(), doctorName, doctorPhone);
                count++;
            } catch (Exception e) {
                log.error("通知社区医生失败: userId={}, doctorId={}, error={}",
                        userId, relation.getDoctorId(), e.getMessage());
            }
        }
        return count;
    }

    /**
     * 同时通知所有家属和社区医生（紧急场景使用）
     *
     * @param userId  老人用户ID
     * @param message 通知内容
     * @param type    通知类型
     * @return 通知结果汇总
     */
    public NotificationSummary notifyAll(Integer userId, String message, String type) {
        int familyCount = notifyFamily(userId, message, type);
        int doctorCount = notifyCommunityDoctor(userId, message, type);
        NotificationSummary summary = new NotificationSummary(familyCount, doctorCount);
        log.info("通知汇总: {}", summary);
        return summary;
    }

    // ==================== 通知历史查询 ====================

    /**
     * 获取老人的通知历史
     *
     * @param userId 老人用户ID
     * @return 最近的通知记录列表
     */
    public List<ContactRecord> getNotificationHistory(Integer userId) {
        return contactRecordRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 获取老人的通知历史（按类型筛选）
     *
     * @param userId 老人用户ID
     * @param type   通知类型
     * @return 最近的通知记录列表
     */
    public List<ContactRecord> getNotificationHistoryByType(Integer userId, String type) {
        return contactRecordRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(r -> type.equals(r.getType()))
                .collect(Collectors.toList());
    }

    // ==================== 通知状态管理 ====================

    /**
     * 更新通知状态（家属已确认/已忽略等），同时记录确认时间
     *
     * @param recordId 通知记录ID
     * @param status   新状态（acknowledged / ignored / failed）
     */
    public void acknowledgeNotification(Integer recordId, String status) {
        contactRecordRepository.findById(recordId).ifPresent(record -> {
            record.setStatus(status);
            if ("acknowledged".equals(status)) {
                record.setAcknowledgedAt(LocalDateTime.now());
            }
            contactRecordRepository.save(record);
            log.info("通知状态已更新: recordId={}, status={}, acknowledgedAt={}",
                    recordId, status, record.getAcknowledgedAt());
        });
    }

    /**
     * 批量确认通知（家属端统一确认）
     *
     * @param userId     老人用户ID
     * @param typeFilter 按类型筛选（可选，null 表示全部确认）
     */
    public int acknowledgeAllByUser(Integer userId, String typeFilter) {
        List<ContactRecord> records = contactRecordRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId);
        int count = 0;
        for (ContactRecord record : records) {
            if ("sent".equals(record.getStatus())
                    && (typeFilter == null || typeFilter.equals(record.getType()))) {
                record.setStatus("acknowledged");
                record.setAcknowledgedAt(LocalDateTime.now());
                contactRecordRepository.save(record);
                count++;
            }
        }
        log.info("批量确认通知: userId={}, count={}", userId, count);
        return count;
    }

    // ==================== 紧急通知升级机制 ====================

    /**
     * 检查未确认的紧急通知，对超过 {@code timeoutMinutes} 分钟未确认的进行升级重通知
     * <p>
     * 升级时会：
     * 1. 将原通知标记为 escalated
     * 2. 创建一条新的升级通知（数据库记录）
     *
     * @param timeoutMinutes 超时分钟数
     * @param type           要检查的通知类型（如 "emergency"、"emergency_escalation"、"alert"）
     * @return 被升级的通知数量
     */
    public int escalateUnacknowledgedNotifications(int timeoutMinutes, String type) {
        List<ContactRecord> unacknowledged = contactRecordRepository
                .findByTypeAndStatusAndAcknowledgedAtIsNull(type, "sent");

        int escalated = 0;
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(timeoutMinutes);

        for (ContactRecord record : unacknowledged) {
            if (record.getCreatedAt().isBefore(deadline)) {
                // 标记为升级状态
                record.setStatus("escalated");
                contactRecordRepository.save(record);
                log.warn("通知超时未确认，已升级: recordId={}, userId={}, targetId={}, type={}",
                        record.getId(), record.getUserId(), record.getFamilyMemberId(), record.getType());

                // 创建一条新的通知记录作为升级
                String escalationMessage = "[升级通知] 此前通知尚未得到确认，请立即查看！" +
                        (record.getMessage() != null ? " 原消息：" + record.getMessage() : "");
                ContactRecord escalationRecord = new ContactRecord();
                escalationRecord.setUserId(record.getUserId());
                escalationRecord.setFamilyMemberId(record.getFamilyMemberId());
                escalationRecord.setType(type + "_escalation");
                escalationRecord.setStatus("sent");
                escalationRecord.setMessage(escalationMessage);
                escalationRecord.setTargetName(record.getTargetName());
                escalationRecord.setTargetPhone(record.getTargetPhone());
                contactRecordRepository.save(escalationRecord);

                escalated++;
            }
        }

        if (escalated > 0) {
            log.info("通知升级完成: type={}, 升级 {} 条", type, escalated);
        }
        return escalated;
    }

    // ==================== 内部方法 ====================

    /**
     * 保存联系记录到数据库（含完整的消息内容和联系人信息）
     */
    private void saveContactRecord(Integer userId, Integer targetId, String type,
                                    String status, String message,
                                    String targetName, String targetPhone) {
        ContactRecord record = new ContactRecord();
        record.setUserId(userId);
        record.setFamilyMemberId(targetId);
        record.setType(type);
        record.setStatus(status);
        record.setMessage(message);
        record.setTargetName(targetName);
        record.setTargetPhone(targetPhone);
        contactRecordRepository.save(record);
    }

    // 邮件发送相关方法已移除

    // ==================== 内部类型定义 ====================

    /**
     * 通知汇总
     */
    public static class NotificationSummary {
        private final int familyNotified;
        private final int doctorNotified;

        public NotificationSummary(int familyNotified, int doctorNotified) {
            this.familyNotified = familyNotified;
            this.doctorNotified = doctorNotified;
        }

        public int getFamilyNotified() { return familyNotified; }
        public int getDoctorNotified() { return doctorNotified; }
        public int getTotalNotified() { return familyNotified + doctorNotified; }

        @Override
        public String toString() {
            return String.format("已通知 %d 位家属, %d 位社区医生", familyNotified, doctorNotified);
        }
    }
}
