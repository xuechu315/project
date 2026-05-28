package com.elderly.care.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Elder {

    private Integer id;
    private Integer userId;
    private Integer age;
    private String gender;
    private String bloodType;
    private Float height;
    private Float weight;
    private Integer emergencyContactId;
    private LocalDateTime createdAt;
    private User user;
}
