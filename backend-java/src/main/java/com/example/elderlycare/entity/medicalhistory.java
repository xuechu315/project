package com.example.elderlycare.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 老人既往病史实体类
 */
@Entity
@Table(name = "medical_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "elder_id", nullable = false)
    private Integer elderId;

    @Column(name = "disease_name", nullable = false, length = 100)
    private String diseaseName;

    @Column(name = "diagnosis_date")
    private LocalDate diagnosisDate;

    @Column(name = "treatment", length = 500)
    private String treatment;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}