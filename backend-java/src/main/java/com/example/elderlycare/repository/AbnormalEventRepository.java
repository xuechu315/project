package com.example.elderlycare.repository;

import com.example.elderlycare.entity.AbnormalEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 异常事件数据访问接口
 */
@Repository
public interface AbnormalEventRepository extends JpaRepository<AbnormalEvent, Integer> {

    /**
     * 根据用户ID查询异常事件
     */
    List<AbnormalEvent> findByUserId(Integer userId);

    /**
     * 根据严重等级查询异常事件
     */
    List<AbnormalEvent> findBySeverity(AbnormalEvent.Severity severity);
}