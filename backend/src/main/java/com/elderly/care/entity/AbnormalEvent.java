package com.elderly.care.entity;

import com.elderly.care.enums.EventType;
import com.elderly.care.enums.Severity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbnormalEvent {

    private Integer id;
    private Integer elderId;
    private EventType type;
    private Severity severity;
    private Float confidence;
    private String detectedBy;
    private LocalDateTime timestamp;
    private Integer resolved;
}
