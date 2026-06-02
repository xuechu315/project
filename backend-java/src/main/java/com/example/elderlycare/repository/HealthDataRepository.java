package com.example.elderlycare.repository;

import com.example.elderlycare.entity.HealthData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 健康数据访问接口
 */
@Repository
public interface HealthDataRepository extends JpaRepository<HealthData, Integer> {

    /**
     * 根据用户ID查询健康数据，按时间降序排列
     */
    List<HealthData> findByUserIdOrderByRecordedAtDesc(Integer userId);

    /**
     * 根据用户ID查询最近N条健康数据
     */
    List<HealthData> findTop10ByUserIdOrderByRecordedAtDesc(Integer userId);

    /**
     * 查询用户最新的健康数据
     */
    HealthData findFirstByUserIdOrderByRecordedAtDesc(Integer userId);
}