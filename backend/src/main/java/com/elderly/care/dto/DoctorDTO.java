package com.elderly.care.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {
    private Integer id;
    
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    @NotBlank(message = "手机号不能为空")
    private String phone;
    
    @NotBlank(message = "科室不能为空")
    private String department;
    
    private LocalDateTime createdAt;
}
