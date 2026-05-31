package com.example.elderlycare.controller;

import com.example.elderlycare.dto.request.LoginRequest;
import com.example.elderlycare.dto.response.ApiResponse;
import com.example.elderlycare.dto.response.LoginResponse;
import com.example.elderlycare.dto.response.UserResponse;
import com.example.elderlycare.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            UserResponse user = userService.login(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(ApiResponse.success(LoginResponse.of(user)));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(401, e.getMessage()));
        }
    }

    /**
     * 验证用户
     */
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<LoginResponse>> verify() {
        UserResponse user = userService.verify();
        return ResponseEntity.ok(ApiResponse.success(LoginResponse.of(user)));
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Integer userId) {
        try {
            UserResponse user = userService.getUserById(userId);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(404, e.getMessage()));
        }
    }
}