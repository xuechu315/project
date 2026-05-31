package com.example.elderlycare.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 家属成员实体类
 */
@Entity
@Table(name = "family_member", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_family_id", columnList = "family_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "family_id", nullable = false)
    private Integer familyId;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "relationship", length = 20)
    private String relationship;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}