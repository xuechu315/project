package com.example.elderlycare.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 家属绑定实体 (对应 family_bind 表，用于移动端家属绑定)
 */
@Entity
@Table(name = "family_bind")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "family_id")
    private Integer familyId;

    @Column(name = "relationship", length = 20)
    private String relationship;

    @Column(name = "name", length = 20)
    private String name;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
