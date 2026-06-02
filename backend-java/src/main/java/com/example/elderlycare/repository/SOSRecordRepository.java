package com.example.elderlycare.repository;

import com.example.elderlycare.entity.SOSRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * SOS记录数据访问接口
 */
@Repository
public interface SOSRecordRepository extends JpaRepository<SOSRecord, Integer> {
}