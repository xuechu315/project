package com.elderly.care.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthData {

    private Integer id;
    private Integer elderId;
    private Integer heartRate;
    private Integer systolicPressure;
    private Integer diastolicPressure;
    private Integer steps;
    private String acceleration;
    private LocalDateTime recordedAt;
}
