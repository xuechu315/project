package com.example.elderlycare.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 应急调度响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyDispatchResponse {

    private Integer taskId;

    private String status;

    private String emergencyLevel;

    private LocalDateTime createdAt;

    private LocalDateTime estimatedArrivalTime;

    private Double distanceKm;

    private Integer estimatedMinutes;

    private String assignedTeam;

    private String message;
}
