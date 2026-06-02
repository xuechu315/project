package com.example.elderlycare.service.impl;

import com.example.elderlycare.entity.ElderDoctorRelation;
import com.example.elderlycare.exception.DuplicateResourceException;
import com.example.elderlycare.repository.ElderDoctorRelationRepository;
import com.example.elderlycare.service.ElderDoctorRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ElderDoctorRelationServiceImpl implements ElderDoctorRelationService {

    @Autowired
    private ElderDoctorRelationRepository relationRepository;

    @Override
    public List<ElderDoctorRelation> getRelationsByElderId(Integer elderId) {
        return relationRepository.findByElderId(elderId);
    }

    @Override
    @Transactional
    public ElderDoctorRelation createRelation(ElderDoctorRelation relation) {
        if (relation.getElderId() != null && relation.getDoctorId() != null) {
            // 如果已存在则直接返回，避免重复绑定报错
            return relationRepository.findByElderIdAndDoctorId(relation.getElderId(), relation.getDoctorId())
                    .orElseGet(() -> relationRepository.save(relation));
        }
        return relationRepository.save(relation);
    }

    @Override
    @Transactional
    public void deleteRelation(Integer elderId, Integer doctorId) {
        relationRepository.deleteByElderIdAndDoctorId(elderId, doctorId);
    }
}
