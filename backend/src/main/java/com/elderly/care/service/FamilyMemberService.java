package com.elderly.care.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elderly.care.entity.FamilyMember;
import com.elderly.care.mapper.FamilyMemberMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FamilyMemberService {

    private final FamilyMemberMapper familyMemberMapper;

    public List<FamilyMember> getFamilyMembersByElderId(Integer elderId) {
        return familyMemberMapper.findByElderId(elderId);
    }

    public List<FamilyMember> getAllFamilyMembers() {
        return familyMemberMapper.findAll();
    }

    public Optional<FamilyMember> getFamilyMemberById(Integer id) {
        return Optional.ofNullable(familyMemberMapper.findById(id));
    }
    
    public Optional<FamilyMember> getFamilyMemberByUserId(Integer userId) {
        return Optional.ofNullable(familyMemberMapper.findByUserId(userId));
    }

    @Transactional
    public FamilyMember createFamilyMember(FamilyMember familyMember) {
        familyMemberMapper.insert(familyMember);
        return familyMember;
    }

    @Transactional
    public FamilyMember updateFamilyMember(Integer id, FamilyMember familyMemberDetails) {
        FamilyMember familyMember = familyMemberMapper.findById(id);
        if (familyMember == null) {
            throw new RuntimeException("家属成员不存在");
        }
        
        familyMember.setName(familyMemberDetails.getName());
        familyMember.setRelationship(familyMemberDetails.getRelationship());
        familyMember.setPhone(familyMemberDetails.getPhone());
        if (familyMemberDetails.getElderId() != null) {
            familyMember.setElderId(familyMemberDetails.getElderId());
        } else {
            familyMember.setElderId(null);
        }
        
        familyMemberMapper.update(familyMember);
        return familyMember;
    }

    @Transactional
    public void deleteFamilyMember(Integer id) {
        familyMemberMapper.deleteById(id);
    }

    @Transactional
    public void deleteFamilyMembersByElderId(Integer elderId) {
        familyMemberMapper.deleteByElderId(elderId);
    }
}
