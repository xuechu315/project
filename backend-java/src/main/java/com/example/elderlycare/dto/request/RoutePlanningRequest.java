package com.example.elderlycare.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 路线规划请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoutePlanningRequest {

    private Double originLat;

    private Double originLng;

    private Double destLat;

    private Double destLng;

    private String transportMode;
}
