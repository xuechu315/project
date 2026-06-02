package com.example.elderlycare.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 路线规划响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoutePlanningResponse {

    private Double distanceKm;

    private Integer estimatedMinutes;

    private String transportMode;

    private List<RouteStep> steps;

    private String polyline;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RouteStep {
        private Integer stepIndex;
        private String instruction;
        private Double distanceKm;
        private Integer durationSeconds;
    }
}
