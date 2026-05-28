package com.elderly.care.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SosRecordDTO {
    private Integer id;
    
    @NotNull(message = "老人ID不能为空")
    private Integer elderId;
    
    private String location;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
