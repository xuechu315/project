package com.example.elderlycare.service;

import com.example.elderlycare.entity.ElderDoctorRelation;

import java.util.List;

public interface ElderDoctorRelationService {
    List<ElderDoctorRelation> getRelationsByElderId(Integer elderId);
    ElderDoctorRelation createRelation(ElderDoctorRelation relation);
    void deleteRelation(Integer elderId, Integer doctorId);
}
