package com.elderly.care.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationDTO {
    private Integer id;
    
    @NotNull(message = "老人ID不能为空")
    private Integer elderId;
    
    @NotBlank(message = "药品名称不能为空")
    private String name;
    
    private String description;
    private String dosage;
    private String frequency;
    private String time;
    private LocalDateTime createdAt;
}
