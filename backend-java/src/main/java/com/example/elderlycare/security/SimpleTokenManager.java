package com.example.elderlycare.security;

import com.example.elderlycare.entity.User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简单Token实现 — UUID + 内存ConcurrentHashMap
 * 后续切换到JWT只需：实现TokenManager接口，替换@Component即可
 */
@Component
public class SimpleTokenManager implements TokenManager {

    private final Map<String, User> tokenStore = new ConcurrentHashMap<>();

    @Override
    public String createToken(User user) {
        String token = UUID.randomUUID().toString().replace("-", "");
        tokenStore.put(token, user);
        return token;
    }

    @Override
    public User getUserByToken(String token) {
        return tokenStore.get(token);
    }

    @Override
    public void removeToken(String token) {
        tokenStore.remove(token);
    }
}
