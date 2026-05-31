package com.elderly.care.service;

import com.elderly.care.entity.ElderDoctorRelation;
import com.elderly.care.exception.DuplicateResourceException;
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
        // 检查是否已存在相同的关联，防止重复绑定
        if (relation.getElderId() != null && relation.getDoctorId() != null) {
            ElderDoctorRelation existing = relationMapper.findByElderIdAndDoctorId(
                    relation.getElderId(), relation.getDoctorId());
            if (existing != null) {
                throw new DuplicateResourceException("该医生已与该老人关联，请勿重复绑定");
            }
        }
        relationMapper.insert(relation);
        return relation;
    }

    @Transactional
    public void deleteRelation(Integer elderId, Integer doctorId) {
        relationMapper.deleteByElderIdAndDoctorId(elderId, doctorId);
    }
}
