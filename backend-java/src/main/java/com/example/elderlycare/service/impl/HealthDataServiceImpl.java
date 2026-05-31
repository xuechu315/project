package com.example.elderlycare.service.impl;

import com.example.elderlycare.dto.request.HealthDataRequest;
import com.example.elderlycare.dto.response.HealthDataResponse;
import com.example.elderlycare.entity.HealthData;
import com.example.elderlycare.repository.HealthDataRepository;
import com.example.elderlycare.service.HealthDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 健康数据服务实现类
 */
@Service
public class HealthDataServiceImpl implements HealthDataService {

    @Autowired
    private HealthDataRepository healthDataRepository;

    @Override
    public List<HealthDataResponse> getHealthDataByUserId(Integer userId) {
        List<HealthData> dataList = healthDataRepository.findTop10ByUserIdOrderByRecordedAtDesc(userId);
        return dataList.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void addHealthData(HealthDataRequest request) {
        HealthData healthData = new HealthData();
        healthData.setUserId(request.getUserId());
        healthData.setHeartRate(request.getHeartRate());
        healthData.setSystolicPressure(request.getSystolicPressure());
        healthData.setDiastolicPressure(request.getDiastolicPressure());
        healthData.setSteps(request.getSteps() != null ? request.getSteps() : 0);
        healthDataRepository.save(healthData);
    }

    @Override
    public HealthDataResponse getLatestHealthData(Integer userId) {
        HealthData data = healthDataRepository.findFirstByUserIdOrderByRecordedAtDesc(userId);
        return data != null ? convertToResponse(data) : null;
    }

    /**
     * 转换实体为响应DTO
     */
    private HealthDataResponse convertToResponse(HealthData data) {
        HealthDataResponse response = new HealthDataResponse();
        response.setId(data.getId());
        response.setHeartRate(data.getHeartRate());
        response.setSystolicPressure(data.getSystolicPressure());
        response.setDiastolicPressure(data.getDiastolicPressure());
        response.setSteps(data.getSteps());
        response.setRecordedAt(data.getRecordedAt());
        return response;
    }
}