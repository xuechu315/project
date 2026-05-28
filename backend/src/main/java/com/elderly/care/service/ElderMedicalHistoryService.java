package com.elderly.care.service;

import com.elderly.care.entity.ElderMedicalHistory;
import com.elderly.care.mapper.ElderMedicalHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ElderMedicalHistoryService {

    private final ElderMedicalHistoryMapper medicalHistoryMapper;

    public List<ElderMedicalHistory> getMedicalHistoryByElderId(Integer elderId) {
        return medicalHistoryMapper.findByElderId(elderId);
    }

    public Optional<ElderMedicalHistory> getMedicalHistoryById(Integer id) {
        return Optional.ofNullable(medicalHistoryMapper.findById(id));
    }

    public List<ElderMedicalHistory> searchMedicalHistory(Integer elderId, String diseaseName) {
        return medicalHistoryMapper.findByElderIdAndDiseaseNameContaining(elderId, diseaseName);
    }

    @Transactional
    public ElderMedicalHistory createMedicalHistory(ElderMedicalHistory medicalHistory) {
        medicalHistoryMapper.insert(medicalHistory);
        return medicalHistory;
    }

    @Transactional
    public ElderMedicalHistory updateMedicalHistory(Integer id, ElderMedicalHistory details) {
        ElderMedicalHistory history = medicalHistoryMapper.findById(id);
        if (history == null) {
            throw new RuntimeException("病史记录不存在");
        }
        
        history.setDiseaseName(details.getDiseaseName());
        history.setDiagnosedAt(details.getDiagnosedAt());
        history.setDescription(details.getDescription());
        
        medicalHistoryMapper.update(history);
        return history;
    }

    @Transactional
    public void deleteMedicalHistory(Integer id) {
        medicalHistoryMapper.deleteById(id);
    }

    @Transactional
    public void deleteMedicalHistoryByElderId(Integer elderId) {
        medicalHistoryMapper.deleteByElderId(elderId);
    }
}
