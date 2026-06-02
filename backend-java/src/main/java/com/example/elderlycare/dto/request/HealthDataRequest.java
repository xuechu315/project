package com.example.elderlycare.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 健康数据请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthDataRequest {

    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    private Integer heartRate;

    private Integer systolicPressure;

    private Integer diastolicPressure;

    private Integer steps;
}