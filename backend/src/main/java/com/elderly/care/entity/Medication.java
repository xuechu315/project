package com.elderly.care.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medication {

    private Integer id;
    private Integer elderId;
    private String name;
    private String description;
    private String dosage;
    private String frequency;
    private String time;
    private LocalDateTime createdAt;
    private Elder elder;
}
