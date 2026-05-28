package com.elderly.care.service;

import com.elderly.care.entity.HealthData;
import com.elderly.care.mapper.HealthDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthDataService {

    private final HealthDataMapper healthDataMapper;

    /**
     * 上报健康数据
     */
    public HealthData reportHealthData(HealthData healthData) {
        log.info("上报健康数据: elderId={}, heartRate={}",
                healthData.getElderId(), healthData.getHeartRate());
        healthDataMapper.insert(healthData);
        return healthData;
    }

    /**
     * 获取最新健康数据
     */
    public HealthData getLatestHealthData(Integer elderId) {
        return healthDataMapper.findLatestByElderId(elderId);
    }

    /**
     * 获取历史健康数据
     */
    public List<HealthData> getHistoryHealthData(Integer elderId,
                                                 LocalDateTime start,
                                                 LocalDateTime end) {
        if (start != null && end != null) {
            return healthDataMapper.findByElderIdAndTimeRange(elderId, start, end);
        }
        return healthDataMapper.findByElderId(elderId);
    }
}