package com.example.elderlycare.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应DTO - 与前端期望格式一致
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Integer id;
    private String username;
    private String name;
    private String userType;
    private String phone;

    public static LoginResponse of(UserResponse user) {
        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setName(user.getName());
        response.setUserType(user.getUserType());
        response.setPhone(user.getPhone());
        return response;
    }
}
