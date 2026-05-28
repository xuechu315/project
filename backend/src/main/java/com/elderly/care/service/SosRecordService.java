package com.elderly.care.service;

import com.elderly.care.entity.SosRecord;
import com.elderly.care.mapper.SosRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SosRecordService {

    private final SosRecordMapper sosRecordMapper;

    public List<SosRecord> getAllSosRecords() {
        return sosRecordMapper.findAll();
    }

    public List<SosRecord> getSosRecordsByElderId(Integer elderId) {
        return sosRecordMapper.findByElderId(elderId);
    }

    public List<SosRecord> getSosRecordsByStatus(SosRecord.SosStatus status) {
        return sosRecordMapper.findByStatus(status.name());
    }

    public Optional<SosRecord> getSosRecordById(Integer id) {
        return Optional.ofNullable(sosRecordMapper.findById(id));
    }

    @Transactional
    public SosRecord createSosRecord(SosRecord sosRecord) {
        sosRecord.setStatus(SosRecord.SosStatus.PENDING);
        sosRecord.setCreatedAt(LocalDateTime.now());
        sosRecordMapper.insert(sosRecord);
        return sosRecord;
    }

    @Transactional
    public SosRecord updateSosStatus(Integer id, SosRecord.SosStatus status) {
        SosRecord sosRecord = sosRecordMapper.findById(id);
        if (sosRecord == null) {
            throw new RuntimeException("SOS记录不存在");
        }
        
        sosRecord.setStatus(status);
        if (status == SosRecord.SosStatus.CLOSED) {
            sosRecord.setResolvedAt(LocalDateTime.now());
        }
        
        sosRecordMapper.update(sosRecord);
        return sosRecord;
    }

    @Transactional
    public void deleteSosRecord(Integer id) {
        sosRecordMapper.deleteById(id);
    }
}
