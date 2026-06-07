package com.example.elderlycare.repository;

import com.example.elderlycare.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
    Optional<Doctor> findByUserId(Integer userId);
    List<Doctor> findByDepartment(String department);
    List<Doctor> findByNameContaining(String name);
}
