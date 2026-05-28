package com.elderly.care.service;

import com.elderly.care.entity.ElderDoctorRelation;
import com.elderly.care.mapper.ElderDoctorRelationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ElderDoctorRelationService {

    private final ElderDoctorRelationMapper relationMapper;

    public List<ElderDoctorRelation> getRelationsByElderId(Integer elderId) {
        return relationMapper.findByElderId(elderId);
    }

    public List<ElderDoctorRelation> getRelationsByDoctorId(Integer doctorId) {
        return relationMapper.findByDoctorId(doctorId);
    }

    public Optional<ElderDoctorRelation> getRelation(Integer elderId, Integer doctorId) {
        return Optional.ofNullable(relationMapper.findByElderIdAndDoctorId(elderId, doctorId));
    }

    @Transactional
    public ElderDoctorRelation createRelation(ElderDoctorRelation relation) {
        relationMapper.insert(relation);
        return relation;
    }

    @Transactional
    public void deleteRelation(Integer elderId, Integer doctorId) {
        relationMapper.deleteByElderIdAndDoctorId(elderId, doctorId);
    }
}
