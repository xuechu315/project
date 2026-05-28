package com.elderly.care.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private Integer id;
    private String username;
    private String name;
    private String userType;
    private String phone;
}