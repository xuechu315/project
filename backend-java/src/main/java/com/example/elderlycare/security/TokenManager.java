package com.example.elderlycare.security;

import com.example.elderlycare.entity.User;

/**
 * Token管理器接口 — 支持从 SimpleToken 切换到 JWT，前端零改动
 */
public interface TokenManager {
    String createToken(User user);
    User getUserByToken(String token);
    void removeToken(String token);
}
