package com.example.elderlycare.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Integer id;

    private String name;

    private String userType;

    private Integer age;

    private String gender;

    private String bloodType;

    private Float height;

    private Float weight;
}