package com.example.elderlycare.service.impl;

import com.example.elderlycare.dto.request.MedicationRequest;
import com.example.elderlycare.dto.response.MedicationResponse;
import com.example.elderlycare.entity.Medication;
import com.example.elderlycare.entity.MedicationRecord;
import com.example.elderlycare.repository.MedicationRecordRepository;
import com.example.elderlycare.repository.MedicationRepository;
import com.example.elderlycare.service.MedicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 药品服务实现类
 */
@Service
public class MedicationServiceImpl implements MedicationService {

    @Autowired
    private MedicationRepository medicationRepository;

    @Autowired
    private MedicationRecordRepository medicationRecordRepository;

    @Override
    public List<MedicationResponse> getMedicationsByUserId(Integer userId) {
        List<Medication> medications = medicationRepository.findByUserId(userId);
        return medications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void addMedication(MedicationRequest request) {
        Medication medication = new Medication();
        medication.setUserId(request.getUserId());
        medication.setName(request.getName());
        medication.setDescription(request.getDescription());
        medication.setDosage(request.getDosage());
        medication.setFrequency(request.getFrequency());
        medication.setTime(request.getTime());
        medicationRepository.save(medication);
    }

    @Override
    public void recordMedication(Integer medicationId) {
        MedicationRecord record = new MedicationRecord();
        record.setMedicationId(medicationId);
        record.setStatus(MedicationRecord.MedicationStatus.已服用);
        medicationRecordRepository.save(record);
    }

    /**
     * 转换实体为响应DTO
     */
    private MedicationResponse convertToResponse(Medication medication) {
        MedicationResponse response = new MedicationResponse();
        response.setId(medication.getId());
        response.setName(medication.getName());
        response.setDescription(medication.getDescription());
        response.setDosage(medication.getDosage());
        response.setFrequency(medication.getFrequency());
        response.setTime(medication.getTime());
        return response;
    }
}