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
     * 更新药品
     */
    void updateMedication(Integer id, MedicationRequest request);

    /**
     * 删除药品
     */
    void deleteMedication(Integer id);

    /**
     * 记录用药
     */
    void recordMedication(Integer medicationId);
}