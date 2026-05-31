package com.example.elderlycare.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 药品响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationResponse {

    private Integer id;

    private String name;

    private String description;

    private String dosage;

    private String frequency;

    private String time;
}