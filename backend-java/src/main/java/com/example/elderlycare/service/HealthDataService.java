package com.example.elderlycare.service;

import com.example.elderlycare.dto.request.HealthDataRequest;
import com.example.elderlycare.dto.response.HealthDataResponse;

import java.util.List;

/**
 * 健康数据服务接口
 */
public interface HealthDataService {

    /**
     * 根据用户ID获取健康数据列表
     */
    List<HealthDataResponse> getHealthDataByUserId(Integer userId);

    /**
     * 添加健康数据
     */
    void addHealthData(HealthDataRequest request);

    /**
     * 获取用户最新健康数据
     */
    HealthDataResponse getLatestHealthData(Integer userId);
}