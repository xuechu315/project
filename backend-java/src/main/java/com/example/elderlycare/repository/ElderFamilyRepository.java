package com.example.elderlycare.repository;

import com.example.elderlycare.entity.ElderFamily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElderFamilyRepository extends JpaRepository<ElderFamily, Integer> {
    List<ElderFamily> findByElderId(Integer elderId);
    List<ElderFamily> findByUserId(Integer userId);
}
