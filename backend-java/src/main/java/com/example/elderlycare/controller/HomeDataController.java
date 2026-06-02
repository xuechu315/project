package com.example.elderlycare.controller;

import com.example.elderlycare.dto.response.ApiResponse;
import com.example.elderlycare.dto.response.HomeDataResponse;
import com.example.elderlycare.service.HomeDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 首页数据控制器
 */
@RestController
@RequestMapping("/api")
public class HomeDataController {

    @Autowired
    private HomeDataService homeDataService;

    /**
     * 获取首页数据
     */
    @GetMapping("/home-data")
    public ResponseEntity<ApiResponse<HomeDataResponse>> getHomeData(
            @RequestParam(defaultValue = "1") Integer user_id) {
        HomeDataResponse data = homeDataService.getHomeData(user_id);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}