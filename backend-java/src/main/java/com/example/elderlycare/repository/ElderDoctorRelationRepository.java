package com.example.elderlycare.repository;

import com.example.elderlycare.entity.ElderDoctorRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ElderDoctorRelationRepository extends JpaRepository<ElderDoctorRelation, Integer> {
    List<ElderDoctorRelation> findByElderId(Integer elderId);
    List<ElderDoctorRelation> findByDoctorId(Integer doctorId);
    Optional<ElderDoctorRelation> findByElderIdAndDoctorId(Integer elderId, Integer doctorId);
    void deleteByElderIdAndDoctorId(Integer elderId, Integer doctorId);
}
