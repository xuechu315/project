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

    /**
     * 按通知类型和状态查询未确认的通知记录
     *
     * @param type   通知类型
     * @param status 状态（如 "sent"）
     * @return 符合条件的记录列表
     */
    List<ContactRecord> findByTypeAndStatusAndAcknowledgedAtIsNull(String type, String status);

    /**
     * 查询用户所有通知记录（用于管理后台）
     */
    List<ContactRecord> findByUserIdOrderByCreatedAtDesc(Integer userId);
}