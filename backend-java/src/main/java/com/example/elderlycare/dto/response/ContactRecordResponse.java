package com.example.elderlycare.dto.response;

import com.example.elderlycare.entity.ContactRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 联系记录（通知）响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactRecordResponse {

    private Integer id;

    private Integer userId;

    private Integer familyMemberId;

    /** 通知类型：emergency / alert / info / doctor_notification */
    private String type;

    /** 通知状态：sent / acknowledged / ignored / escalated */
    private String status;

    /** 通知消息内容 */
    private String message;

    /** 目标联系人姓名 */
    private String targetName;

    /** 目标联系人电话 */
    private String targetPhone;

    /** 确认时间（null 表示未确认） */
    private String acknowledgedAt;

    /** 通知创建时间 */
    private String createdAt;

    /**
     * 从实体转换为 DTO
     */
    public static ContactRecordResponse fromEntity(ContactRecord record) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ContactRecordResponse resp = new ContactRecordResponse();
        resp.setId(record.getId());
        resp.setUserId(record.getUserId());
        resp.setFamilyMemberId(record.getFamilyMemberId());
        resp.setType(record.getType());
        resp.setStatus(record.getStatus());
        resp.setMessage(record.getMessage());
        resp.setTargetName(record.getTargetName());
        resp.setTargetPhone(record.getTargetPhone());
        resp.setAcknowledgedAt(record.getAcknowledgedAt() != null
                ? record.getAcknowledgedAt().format(fmt) : null);
        resp.setCreatedAt(record.getCreatedAt() != null
                ? record.getCreatedAt().format(fmt) : null);
        return resp;
    }
}
