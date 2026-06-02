package com.example.elderlycare.repository;

import com.example.elderlycare.entity.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Integer> {
    List<FamilyMember> findByUserId(Integer userId);
    List<FamilyMember> findByFamilyId(Integer familyId);
    List<FamilyMember> findByFamilyIdAndUserId(Integer familyId, Integer userId);
}
