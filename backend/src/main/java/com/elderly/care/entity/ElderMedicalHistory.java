package com.elderly.care.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElderMedicalHistory {

    private Integer id;
    private Integer elderId;
    private String diseaseName;
    private LocalDate diagnosedAt;
    private String description;
    private LocalDateTime createdAt;
    private Elder elder;
}
