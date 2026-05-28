package com.elderly.care.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElderDTO {
    private Integer id;
    private Integer userId;
    private String username;
    private String name;
    private Integer age;
    private String gender;
    private String bloodType;
    private Float height;
    private Float weight;
    private String phone;
    private Integer emergencyContactId;
    private LocalDateTime createdAt;
}
