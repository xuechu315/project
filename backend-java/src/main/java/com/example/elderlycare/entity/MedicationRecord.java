package com.example.elderlycare.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用药记录实体类
 */
@Entity
@Table(name = "medication_record", indexes = {
    @Index(name = "idx_medication_id", columnList = "medication_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "medication_id", nullable = false)
    private Integer medicationId;

    @Column(name = "taken_at")
    private LocalDateTime takenAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MedicationStatus status;

    /**
     * 用药状态枚举
     */
    public enum MedicationStatus {
        已服用, 漏服, 跳过
    }
}