package com.elderly.care.service;

import com.elderly.care.entity.Medication;
import com.elderly.care.mapper.MedicationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicationService {

    private final MedicationMapper medicationMapper;

    public List<Medication> getMedicationsByElderId(Integer elderId) {
        return medicationMapper.findByElderId(elderId);
    }

    public Optional<Medication> getMedicationById(Integer id) {
        return Optional.ofNullable(medicationMapper.findById(id));
    }

    public List<Medication> searchMedications(Integer elderId, String name) {
        return medicationMapper.findByElderIdAndNameContaining(elderId, name);
    }

    @Transactional
    public Medication createMedication(Medication medication) {
        medicationMapper.insert(medication);
        return medication;
    }

    @Transactional
    public Medication updateMedication(Integer id, Medication medicationDetails) {
        Medication medication = medicationMapper.findById(id);
        if (medication == null) {
            throw new RuntimeException("药品信息不存在");
        }
        
        medication.setName(medicationDetails.getName());
        medication.setDescription(medicationDetails.getDescription());
        medication.setDosage(medicationDetails.getDosage());
        medication.setFrequency(medicationDetails.getFrequency());
        medication.setTime(medicationDetails.getTime());
        
        medicationMapper.update(medication);
        return medication;
    }

    @Transactional
    public void deleteMedication(Integer id) {
        medicationMapper.deleteById(id);
    }

    @Transactional
    public void deleteMedicationsByElderId(Integer elderId) {
        medicationMapper.deleteByElderId(elderId);
    }
}
