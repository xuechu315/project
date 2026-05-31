package com.example.elderlycare.repository;

import com.example.elderlycare.entity.MedicationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 用药记录数据访问接口
 */
@Repository
public interface MedicationRecordRepository extends JpaRepository<MedicationRecord, Integer> {
}