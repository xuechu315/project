package com.elderly.care.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbnormalEventDTO {
    private Integer id;
    private Integer elderId;
    private String type;
    private String severity;
    private Float confidence;
    private String detectedBy;
    private LocalDateTime timestamp;
    private Integer resolved;
}
