package com.elderly.care.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {

    private Integer id;
    private Integer userId;
    private String name;
    private String phone;
    private String department;
    private LocalDateTime createdAt;
}
