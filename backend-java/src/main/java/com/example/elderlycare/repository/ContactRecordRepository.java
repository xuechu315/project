package com.example.elderlycare.repository;

import com.example.elderlycare.entity.ContactRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 联系记录数据访问接口
 */
@Repository
public interface ContactRecordRepository extends JpaRepository<ContactRecord, Integer> {

    /**
     * 根据用户ID查询联系记录，按时间降序排列
     */
    List<ContactRecord> findTop10ByUserIdOrderByCreatedAtDesc(Integer userId);
}