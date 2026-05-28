package com.elderly.care.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationRecord {

    private Integer id;
    private Integer medicationId;
    private LocalDateTime takenAt;
    private MedicationStatus status;
    private Medication medication;

    public enum MedicationStatus {
        taken("已服用"),
        missed("漏服"),
        skipped("跳过");

        private final String description;

        MedicationStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
