package com.example.elderlycare.service;

import com.example.elderlycare.entity.ElderFamilyMember;

import java.util.List;
import java.util.Optional;

public interface FamilyMemberService {
    List<ElderFamilyMember> getFamilyMembersByElderId(Integer elderId);
    List<ElderFamilyMember> getAllFamilyMembers();
    Optional<ElderFamilyMember> getFamilyMemberById(Integer id);
    List<ElderFamilyMember> getFamilyMemberByUserId(Integer userId);
    ElderFamilyMember createFamilyMember(ElderFamilyMember familyMember);
    ElderFamilyMember updateFamilyMember(Integer id, ElderFamilyMember familyMember);
    void deleteFamilyMember(Integer id);
}
