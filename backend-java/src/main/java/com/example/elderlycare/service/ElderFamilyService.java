package com.example.elderlycare.service;

import com.example.elderlycare.entity.ElderFamily;

import java.util.List;
import java.util.Optional;

public interface ElderFamilyService {
    List<ElderFamily> getFamilyMembersByElderId(Integer elderId);
    List<ElderFamily> getAllFamilyMembers();
    Optional<ElderFamily> getFamilyMemberById(Integer id);
    List<ElderFamily> getFamilyMemberByUserId(Integer userId);
    ElderFamily createFamilyMember(ElderFamily elderFamily);
    ElderFamily updateFamilyMember(Integer id, ElderFamily elderFamily);
    void deleteFamilyMember(Integer id);
}
