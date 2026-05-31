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

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}