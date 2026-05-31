package com.example.elderlycare.repository;

import com.example.elderlycare.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 咨询数据访问接口
 */
@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Integer> {

    /**
     * 根据用户ID查询咨询记录，按时间降序排列
     */
    List<Consultation> findByUserIdOrderByCreatedAtDesc(Integer userId);
}