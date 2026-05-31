package com.elderly.care.controller;

import com.elderly.care.dto.LoginRequestDTO;
import com.elderly.care.dto.LoginResponseDTO;
import com.elderly.care.dto.Result;
import com.elderly.care.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    /**
     * 用户登录
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public Result<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        LoginResponseDTO user = userService.login(request);
        if (user == null) {
            return Result.error("用户名或密码错误");
        }
        return Result.success(user);
    }
}