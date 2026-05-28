package com.elderly.care.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElderDoctorRelation {

    private Integer id;
    private Integer elderId;
    private Integer doctorId;
    private LocalDateTime createdAt;
    private Elder elder;
    private Doctor doctor;
}
