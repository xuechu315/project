package com.elderly.care.service;

import com.elderly.care.entity.MedicationRecord;
import com.elderly.care.mapper.MedicationRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicationRecordService {

    private final MedicationRecordMapper medicationRecordMapper;

    public List<MedicationRecord> getRecordsByMedicationId(Integer medicationId) {
        return medicationRecordMapper.findByMedicationId(medicationId);
    }

    public List<MedicationRecord> getRecordsByDateRange(LocalDateTime start, LocalDateTime end) {
        return medicationRecordMapper.findByTakenAtBetween(start, end);
    }

    public Optional<MedicationRecord> getRecordById(Integer id) {
        return Optional.ofNullable(medicationRecordMapper.findById(id));
    }

    @Transactional
    public MedicationRecord createRecord(MedicationRecord record) {
        medicationRecordMapper.insert(record);
        return record;
    }

    @Transactional
    public MedicationRecord updateRecord(Integer id, MedicationRecord details) {
        MedicationRecord record = medicationRecordMapper.findById(id);
        if (record == null) {
            throw new RuntimeException("用药记录不存在");
        }
        
        record.setStatus(details.getStatus());
        
        medicationRecordMapper.update(record);
        return record;
    }

    @Transactional
    public void deleteRecord(Integer id) {
        medicationRecordMapper.deleteById(id);
    }
}
