package com.example.elderlycare.repository;

import com.example.elderlycare.entity.ElderFamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElderFamilyMemberRepository extends JpaRepository<ElderFamilyMember, Integer> {
    List<ElderFamilyMember> findByElderId(Integer elderId);
    List<ElderFamilyMember> findByUserId(Integer userId);
}
