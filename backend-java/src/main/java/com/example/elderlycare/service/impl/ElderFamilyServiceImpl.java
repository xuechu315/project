package com.example.elderlycare.service.impl;

import com.example.elderlycare.entity.ElderFamily;
import com.example.elderlycare.exception.ResourceNotFoundException;
import com.example.elderlycare.repository.ElderFamilyRepository;
import com.example.elderlycare.service.ElderFamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ElderFamilyServiceImpl implements ElderFamilyService {

    @Autowired
    private ElderFamilyRepository elderFamilyRepository;

    @Override
    public List<ElderFamily> getFamilyMembersByElderId(Integer elderId) {
        return elderFamilyRepository.findByElderId(elderId);
    }

    @Override
    public List<ElderFamily> getAllFamilyMembers() {
        return elderFamilyRepository.findAll();
    }

    @Override
    public Optional<ElderFamily> getFamilyMemberById(Integer id) {
        return elderFamilyRepository.findById(id);
    }

    @Override
    public List<ElderFamily> getFamilyMemberByUserId(Integer userId) {
        return elderFamilyRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public ElderFamily createFamilyMember(ElderFamily elderFamily) {
        return elderFamilyRepository.save(elderFamily);
    }

    @Override
    @Transactional
    public ElderFamily updateFamilyMember(Integer id, ElderFamily details) {
        ElderFamily ef = elderFamilyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("家属成员", "id", id));
        if (details.getName() != null) ef.setName(details.getName());
        if (details.getRelationship() != null) ef.setRelationship(details.getRelationship());
        ef.setElderId(details.getElderId());
        return elderFamilyRepository.save(ef);
    }

    @Override
    @Transactional
    public void deleteFamilyMember(Integer id) {
        elderFamilyRepository.deleteById(id);
    }
}
