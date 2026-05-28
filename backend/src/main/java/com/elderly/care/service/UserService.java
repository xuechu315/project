package com.elderly.care.service;

import com.elderly.care.dto.LoginRequestDTO;
import com.elderly.care.dto.LoginResponseDTO;
import com.elderly.care.entity.User;
import com.elderly.care.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserMapper userMapper;

    /**
     * 用户登录
     */
    public LoginResponseDTO login(LoginRequestDTO request) {
        try {
            log.info("用户登录请求: {}", request.getUsername());

            User user = userMapper.findByUsername(request.getUsername());

            if (user == null) {
                log.warn("用户不存在: {}", request.getUsername());
                return null;
            }

            // 密码验证 (实际应使用加密)
            if (!request.getPassword().equals(user.getPassword())) {
                log.warn("密码错误: {}", request.getUsername());
                return null;
            }

            log.info("登录成功: {}, 用户类型: {}", user.getUsername(), user.getUserType());

            LoginResponseDTO response = new LoginResponseDTO();
            response.setId(user.getId());
            response.setUsername(user.getUsername());
            response.setName(user.getName());
            response.setUserType(user.getUserType().name());
            response.setPhone(user.getPhone());

            return response;
        } catch (Exception e) {
            log.error("登录过程中发生异常: {}", e.getMessage(), e);
            throw new RuntimeException("登录失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据ID获取用户
     */
    public User getUserById(Integer id) {
        return userMapper.findById(id);
    }
}