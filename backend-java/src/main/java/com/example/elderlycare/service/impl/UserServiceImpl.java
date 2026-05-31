package com.example.elderlycare.service.impl;

import com.example.elderlycare.dto.response.UserResponse;
import com.example.elderlycare.entity.User;
import com.example.elderlycare.repository.UserRepository;
import com.example.elderlycare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserResponse login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        return convertToResponse(user);
    }

    @Override
    public UserResponse getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return convertToResponse(user);
    }

    @Override
    public UserResponse verify() {
        // 简化验证逻辑，返回默认用户
        UserResponse response = new UserResponse();
        response.setId(1);
        response.setName("张大爷");
        response.setUserType("老人");
        return response;
    }

    /**
     * 转换实体为响应DTO
     */
    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setUserType(user.getUserType().name());
        response.setAge(user.getAge());
        response.setGender(user.getGender());
        response.setBloodType(user.getBloodType());
        response.setHeight(user.getHeight());
        response.setWeight(user.getWeight());
        return response;
    }
}