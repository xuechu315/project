package com.example.elderlycare.service.impl;

import com.example.elderlycare.entity.MedicalHistory;
import com.example.elderlycare.repository.MedicalHistoryRepository;
import com.example.elderlycare.service.MedicalHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 既往病史服务实现类
 */
@Service
public class MedicalHistoryServiceImpl implements MedicalHistoryService {
    
    @Autowired
    private MedicalHistoryRepository medicalHistoryRepository;
    
    @Override
    public List<MedicalHistory> getByElderId(Integer elderId) {
        return medicalHistoryRepository.findByElderId(elderId);
    }
    
    @Override
    public MedicalHistory add(MedicalHistory medicalHistory) {
        medicalHistory.setCreatedAt(LocalDateTime.now());
        medicalHistory.setUpdatedAt(LocalDateTime.now());
        return medicalHistoryRepository.save(medicalHistory);
    }
    
    @Override
    public MedicalHistory update(MedicalHistory medicalHistory) {
        medicalHistory.setUpdatedAt(LocalDateTime.now());
        return medicalHistoryRepository.save(medicalHistory);
    }
    
    @Override
    public void delete(Integer id) {
        medicalHistoryRepository.deleteById(id);
    }
    
    @Override
    @Transactional
    public List<MedicalHistory> batchSave(Integer elderId, List<MedicalHistory> histories) {
        // 先删除该老人的所有既往病史
        medicalHistoryRepository.deleteByElderId(elderId);
        
        // 设置老人ID并保存新的病史
        for (MedicalHistory history : histories) {
            history.setElderId(elderId);
            history.setCreatedAt(LocalDateTime.now());
            history.setUpdatedAt(LocalDateTime.now());
        }
        
        return medicalHistoryRepository.saveAll(histories);
    }
}
