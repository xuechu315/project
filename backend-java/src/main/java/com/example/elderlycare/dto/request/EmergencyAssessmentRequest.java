package com.example.elderlycare.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 应急风险评估请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyAssessmentRequest {

    private Integer userId;

    private Integer heartRate;

    private Integer systolic;

    private Integer diastolic;

    private Double accelerationX;

    private Double accelerationY;

    private Double accelerationZ;

    private String behaviorNote;

    /** 是否为SOS紧急求助 */
    private Boolean sosRequest;
}
