package com.example.elderlycare.service.impl;

import com.example.elderlycare.dto.response.HealthDataResponse;
import com.example.elderlycare.dto.response.HomeDataResponse;
import com.example.elderlycare.dto.response.UserResponse;
import com.example.elderlycare.service.HealthDataService;
import com.example.elderlycare.service.HomeDataService;
import com.example.elderlycare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 首页数据服务实现类
 */
@Service
public class HomeDataServiceImpl implements HomeDataService {

    @Autowired
    private UserService userService;

    @Autowired
    private HealthDataService healthDataService;

    @Override
    public HomeDataResponse getHomeData(Integer userId) {
        HomeDataResponse response = new HomeDataResponse();
        
        // 获取用户信息
        UserResponse user = null;
        try {
            user = userService.getUserById(userId);
        } catch (Exception e) {
            // 用户不存在时使用默认值
        }
        
        HomeDataResponse.UserInfo userInfo = new HomeDataResponse.UserInfo();
        userInfo.setName(user != null ? user.getName() : "张大爷");
        userInfo.setAge(user != null && user.getAge() != null ? user.getAge() : 70);
        response.setUser(userInfo);
        
        // 获取健康数据
        HealthDataResponse healthData = healthDataService.getLatestHealthData(userId);
        
        HomeDataResponse.HealthInfo healthInfo = new HomeDataResponse.HealthInfo();
        healthInfo.setSteps(healthData != null && healthData.getSteps() != null ? healthData.getSteps() : 4528);
        healthInfo.setHeartRate(healthData != null && healthData.getHeartRate() != null ? healthData.getHeartRate() : 72);
        response.setHealth(healthInfo);
        
        // 设置日期
        response.setCurrentDate();
        
        return response;
    }
}