package com.example.elderlycare.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 应急调度请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyDispatchRequest {

    private Integer userId;

    private Double userLat;

    private Double userLng;

    private String emergencyType;

    private String emergencyLevel;

    private String address;

    private String contactPhone;
}
