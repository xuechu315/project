package com.elderly.care.entity;

import com.elderly.care.enums.UserType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Integer id;
    private String username;
    private String password;
    private UserType userType;
    private String name;
    private String phone;
    private LocalDateTime createdAt;
}
