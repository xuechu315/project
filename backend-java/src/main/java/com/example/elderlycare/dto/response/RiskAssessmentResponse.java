package com.example.elderlycare.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 风险评估响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskAssessmentResponse {

    private String riskLevel;

    private Double riskScore;

    private String analysis;

    private String recommendation;

    private Boolean needEmergency;

    public static RiskAssessmentResponse minor(String analysis, String recommendation) {
        return new RiskAssessmentResponse("轻微", 0.3, analysis, recommendation, false);
    }

    public static RiskAssessmentResponse urgent(String analysis, String recommendation) {
        return new RiskAssessmentResponse("紧急", 0.8, analysis, recommendation, true);
    }
}
