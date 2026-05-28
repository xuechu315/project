package com.elderly.care.service;

import com.elderly.care.entity.FamilyMember;
import com.elderly.care.mapper.FamilyMemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FamilyMemberService {

    private final FamilyMemberMapper familyMemberMapper;

    public List<FamilyMember> getFamilyMembersByElderId(Integer elderId) {
        return familyMemberMapper.findByElderId(elderId);
    }

    public Optional<FamilyMember> getFamilyMemberById(Integer id) {
        return Optional.ofNullable(familyMemberMapper.findById(id));
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
