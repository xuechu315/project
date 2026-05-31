package com.example.elderlycare.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 药品请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationRequest {

    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    @NotBlank(message = "药品名称不能为空")
    private String name;

    private String description;

    @NotBlank(message = "服用剂量不能为空")
    private String dosage;

    @NotBlank(message = "服用频次不能为空")
    private String frequency;

    @NotBlank(message = "提醒时间不能为空")
    private String time;
}