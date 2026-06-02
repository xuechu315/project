package com.example.elderlycare.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "elder_doctor_relation", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"elder_id", "doctor_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElderDoctorRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "elder_id", nullable = false)
    private Integer elderId;

    @Column(name = "doctor_id", nullable = false)
    private Integer doctorId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
