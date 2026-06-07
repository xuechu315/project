package com.example.elderlycare.repository;

import com.example.elderlycare.entity.MedicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 既往病史数据访问层
 */
@Repository
public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Integer> {

    /**
     * 根据老人ID查询既往病史列表
     */
    List<MedicalHistory> findByElderId(Integer elderId);

    /**
     * 根据老人ID删除所有既往病史
     */
    void deleteByElderId(Integer elderId);
}
