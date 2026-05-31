package com.example.elderlycare.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private UserResponse user;

    public static LoginResponse of(UserResponse user) {
        LoginResponse response = new LoginResponse();
        response.setUser(user);
        return response;
    }
}