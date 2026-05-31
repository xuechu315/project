package com.example.elderlycare.service;

import com.example.elderlycare.dto.response.UserResponse;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户登录
     */
    UserResponse login(String username, String password);

    /**
     * 根据用户ID获取用户信息
     */
    UserResponse getUserById(Integer userId);

    /**
     * 验证用户（简化版）
     */
    UserResponse verify();
}