package com.example.elderlycare.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 联系记录实体类
 */
@Entity
@Table(name = "contact_record", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_family_member_id", columnList = "family_member_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "family_member_id", nullable = false)
    private Integer familyMemberId;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "status", length = 20)
    private String status = "sent";

    /** 通知消息内容 */
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    /** 目标姓名（冗余存储，方便查看） */
    @Column(name = "target_name", length = 50)
    private String targetName;

    /** 目标电话（冗余存储，方便查看） */
    @Column(name = "target_phone", length = 20)
    private String targetPhone;

    /** 通知已读确认时间（null 表示未读） */
    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}