package com.example.elderlycare.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 健康数据响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthDataResponse {

    private Integer id;

    private Integer heartRate;

    private Integer systolicPressure;

    private Integer diastolicPressure;

    private Integer steps;

    private String recordedAt;

    /**
     * 设置记录时间
     */
    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}