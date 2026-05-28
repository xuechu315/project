package com.elderly.care.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationRecordDTO {
    private Integer id;
    private Integer medicationId;
    private String medicationName;
    private String status;
    private LocalDateTime takenAt;
}
