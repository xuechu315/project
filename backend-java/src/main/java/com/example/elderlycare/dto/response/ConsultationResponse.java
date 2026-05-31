package com.example.elderlycare.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 咨询响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationResponse {

    private Integer id;

    private String message;

    private String response;

    private String type;

    private String createdAt;

    /**
     * 设置创建时间
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}