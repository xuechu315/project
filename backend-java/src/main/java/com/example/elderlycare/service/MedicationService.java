package com.example.elderlycare.service;

import com.example.elderlycare.dto.request.MedicationRequest;
import com.example.elderlycare.dto.response.MedicationResponse;

import java.util.List;

/**
 * 药品服务接口
 */
public interface MedicationService {

    /**
     * 根据用户ID获取药品列表
     */
    List<MedicationResponse> getMedicationsByUserId(Integer userId);

    /**
     * 添加药品
     */
    void addMedication(MedicationRequest request);

    /**
     * 记录用药
     */
    void recordMedication(Integer medicationId);
}