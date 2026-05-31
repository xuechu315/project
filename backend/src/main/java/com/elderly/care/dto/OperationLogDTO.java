package com.elderly.care.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogDTO {
    private Long id;
    private String operator;
    private String operation;
    private LocalDateTime createdAt;
}
