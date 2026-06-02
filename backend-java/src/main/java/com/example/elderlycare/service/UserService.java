package com.example.elderlycare.service;

import com.example.elderlycare.dto.response.UserResponse;
import com.example.elderlycare.entity.User;

import java.util.List;

public interface UserService {
    UserResponse login(String username, String password);
    UserResponse getUserById(Integer userId);
    UserResponse verify();
    List<User> getAllUsers();
    User createUser(User user);
    User updateUser(Integer id, User user);
    void deleteUser(Integer id);
}
