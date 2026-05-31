package com.example.elderlycare.repository;

import com.example.elderlycare.entity.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 家属成员数据访问接口
 */
@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Integer> {

    /**
     * 根据用户ID查询家属列表
     */
    List<FamilyMember> findByUserId(Integer userId);

    /**
     * 根据家属ID查询绑定的老人
     */
    List<FamilyMember> findByFamilyId(Integer familyId);

    /**
     * 根据家属ID和老人ID查询绑定记录
     */
    List<FamilyMember> findByFamilyIdAndUserId(Integer familyId, Integer userId);
}