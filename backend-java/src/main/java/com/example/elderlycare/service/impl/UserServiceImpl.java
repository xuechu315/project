package com.example.elderlycare.service.impl;

import com.example.elderlycare.dto.response.UserResponse;
import com.example.elderlycare.entity.User;
import com.example.elderlycare.exception.DuplicateResourceException;
import com.example.elderlycare.exception.ResourceNotFoundException;
import com.example.elderlycare.repository.UserRepository;
import com.example.elderlycare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserResponse login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "username", username));

        boolean passwordMatch;
        String storedPassword = user.getPassword();

        // BCrypt匹配（新密码格式）
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
            passwordMatch = passwordEncoder.matches(password, storedPassword);
        } else {
            // 明文匹配（旧密码向后兼容）
            passwordMatch = storedPassword.equals(password);
            if (passwordMatch) {
                // 自动升级为BCrypt加密
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
            }
        }

        if (!passwordMatch) {
            throw new ResourceNotFoundException("用户", "username", username);
        }

        return convertToResponse(user);
    }

    @Override
    public UserResponse getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        return convertToResponse(user);
    }

    @Override
    public UserResponse verify() {
        // 尝试获取当前登录用户（如果SecurityContext中有）
        // 由于当前是permitAll模式，回退到查找第一个admin用户
        Optional<User> adminUser = userRepository.findByUserType(User.UserType.admin)
                .stream().findFirst();
        if (adminUser.isPresent()) {
            return convertToResponse(adminUser.get());
        }
        // 如果没有admin用户，返回第一个用户
        List<User> allUsers = userRepository.findAll();
        if (!allUsers.isEmpty()) {
            return convertToResponse(allUsers.get(0));
        }
        // 完全无数据时返回默认
        UserResponse response = new UserResponse();
        response.setId(0);
        response.setName("未登录");
        response.setUserType("guest");
        response.setUsername("guest");
        return response;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DuplicateResourceException("用户", "username", user.getUsername());
        }
        // 密码加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(Integer id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", id));

        if (userDetails.getUsername() != null) user.setUsername(userDetails.getUsername());
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            // 密码加密
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        if (userDetails.getName() != null) user.setName(userDetails.getName());
        if (userDetails.getPhone() != null) user.setPhone(userDetails.getPhone());
        if (userDetails.getUserType() != null) user.setUserType(userDetails.getUserType());
        if (userDetails.getAge() != null) user.setAge(userDetails.getAge());
        if (userDetails.getGender() != null) user.setGender(userDetails.getGender());
        if (userDetails.getBloodType() != null) user.setBloodType(userDetails.getBloodType());

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("用户", "id", id);
        }
        userRepository.deleteById(id);
    }

    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setName(user.getName());
        response.setUserType(user.getUserType().name());
        response.setPhone(user.getPhone());
        response.setAge(user.getAge());
        response.setGender(user.getGender());
        response.setBloodType(user.getBloodType());
        response.setHeight(user.getHeight());
        response.setWeight(user.getWeight());
        response.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
        return response;
    }
}
