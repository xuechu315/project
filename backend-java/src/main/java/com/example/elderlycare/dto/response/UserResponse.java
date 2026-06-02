package com.example.elderlycare.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Integer id;
    private String username;
    private String name;
    private String userType;
    private String phone;
    private Integer age;
    private String gender;
    private String bloodType;
    private Float height;
    private Float weight;
    private String createdAt;
}
