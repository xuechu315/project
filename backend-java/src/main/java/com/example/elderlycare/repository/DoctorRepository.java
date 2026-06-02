package com.example.elderlycare.repository;

import com.example.elderlycare.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
    List<Doctor> findByDepartment(String department);
    List<Doctor> findByNameContaining(String name);
}
