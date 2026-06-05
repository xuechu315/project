package com.example.elderlycare.service;

import com.example.elderlycare.entity.MedicalHistory;

import java.util.List;

/**
 * 既往病史服务接口
 */
public interface MedicalHistoryService {
    
    /**
     * 根据老人ID获取既往病史列表
     */
    List<MedicalHistory> getByElderId(Integer elderId);
    
    /**
     * 添加既往病史
     */
    MedicalHistory add(MedicalHistory medicalHistory);
    
    /**
     * 更新既往病史
     */
    MedicalHistory update(MedicalHistory medicalHistory);
    
    /**
     * 删除既往病史
     */
    void delete(Integer id);
    
    /**
     * 批量保存老人的既往病史（先删除旧的，再添加新的）
     */
    List<MedicalHistory> batchSave(Integer elderId, List<MedicalHistory> histories);
}
