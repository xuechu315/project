package com.example.elderlycare.service;

import com.example.elderlycare.entity.Doctor;

import java.util.List;
import java.util.Optional;

public interface DoctorService {
    List<Doctor> getAllDoctors();
    Optional<Doctor> getDoctorById(Integer id);
    List<Doctor> getDoctorsByDepartment(String department);
    List<Doctor> searchDoctorsByName(String name);
    Doctor createDoctor(Doctor doctor);
    Doctor updateDoctor(Integer id, Doctor doctor);
    void deleteDoctor(Integer id);
}
