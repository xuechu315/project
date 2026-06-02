package com.example.elderlycare.repository;

import com.example.elderlycare.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 药品数据访问接口
 */
@Repository
public interface MedicationRepository extends JpaRepository<Medication, Integer> {

    /**
     * 根据用户ID查询药品列表
     */
    List<Medication> findByUserId(Integer userId);
}