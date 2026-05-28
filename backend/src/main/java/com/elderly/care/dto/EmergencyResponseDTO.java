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
public class EmergencyResponseDTO {
    @NotBlank(message = "响应单号不能为空")
    private String id;
    
    @NotNull(message = "事件ID不能为空")
    private Integer eventId;
    
    private String ambulanceId;
    private Integer eta;
    private Float distance;
    private String status;
    private LocalDateTime dispatchedAt;
}
