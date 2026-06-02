package com.example.elderlycare.service.impl;

import com.example.elderlycare.entity.ElderFamilyMember;
import com.example.elderlycare.exception.ResourceNotFoundException;
import com.example.elderlycare.repository.ElderFamilyMemberRepository;
import com.example.elderlycare.service.FamilyMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FamilyMemberServiceImpl implements FamilyMemberService {

    @Autowired
    private ElderFamilyMemberRepository familyMemberRepository;

    @Override
    public List<ElderFamilyMember> getFamilyMembersByElderId(Integer elderId) {
        return familyMemberRepository.findByElderId(elderId);
    }

    @Override
    public List<ElderFamilyMember> getAllFamilyMembers() {
        return familyMemberRepository.findAll();
    }

    @Override
    public Optional<ElderFamilyMember> getFamilyMemberById(Integer id) {
        return familyMemberRepository.findById(id);
    }

    @Override
    public List<ElderFamilyMember> getFamilyMemberByUserId(Integer userId) {
        return familyMemberRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public ElderFamilyMember createFamilyMember(ElderFamilyMember familyMember) {
        return familyMemberRepository.save(familyMember);
    }

    @Override
    @Transactional
    public ElderFamilyMember updateFamilyMember(Integer id, ElderFamilyMember details) {
        ElderFamilyMember fm = familyMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("家属成员", "id", id));
        if (details.getName() != null) fm.setName(details.getName());
        if (details.getRelationship() != null) fm.setRelationship(details.getRelationship());
        if (details.getPhone() != null) fm.setPhone(details.getPhone());
        fm.setElderId(details.getElderId());
        return familyMemberRepository.save(fm);
    }

    @Override
    @Transactional
    public void deleteFamilyMember(Integer id) {
        familyMemberRepository.deleteById(id);
    }
}
