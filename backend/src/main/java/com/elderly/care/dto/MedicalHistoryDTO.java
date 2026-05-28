package com.elderly.care.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalHistoryDTO {
    private Integer id;
    
    @NotNull(message = "老人ID不能为空")
    private Integer elderId;
    
    @NotBlank(message = "疾病名称不能为空")
    private String diseaseName;
    
    private LocalDate diagnosedAt;
    private String description;
    private LocalDateTime createdAt;
}
