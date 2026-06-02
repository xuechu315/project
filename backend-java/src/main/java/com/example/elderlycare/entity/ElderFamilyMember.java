package com.example.elderlycare.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 老人家属实体 (对应 family_member 表，用于管理员端绑定家属到老人)
 */
@Entity
@Table(name = "family_member")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElderFamilyMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "elder_id")
    private Integer elderId;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "relationship", length = 20)
    private String relationship;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
