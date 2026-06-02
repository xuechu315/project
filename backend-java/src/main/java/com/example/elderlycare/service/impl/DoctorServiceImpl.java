package com.example.elderlycare.service.impl;

import com.example.elderlycare.entity.Doctor;
import com.example.elderlycare.exception.ResourceNotFoundException;
import com.example.elderlycare.repository.DoctorRepository;
import com.example.elderlycare.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Override
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    @Override
    public Optional<Doctor> getDoctorById(Integer id) {
        return doctorRepository.findById(id);
    }

    @Override
    public List<Doctor> getDoctorsByDepartment(String department) {
        return doctorRepository.findByDepartment(department);
    }

    @Override
    public List<Doctor> searchDoctorsByName(String name) {
        return doctorRepository.findByNameContaining(name);
    }

    @Override
    @Transactional
    public Doctor createDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    @Override
    @Transactional
    public Doctor updateDoctor(Integer id, Doctor doctorDetails) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("医生", "id", id));
        if (doctorDetails.getName() != null) doctor.setName(doctorDetails.getName());
        if (doctorDetails.getPhone() != null) doctor.setPhone(doctorDetails.getPhone());
        if (doctorDetails.getDepartment() != null) doctor.setDepartment(doctorDetails.getDepartment());
        return doctorRepository.save(doctor);
    }

    @Override
    @Transactional
    public void deleteDoctor(Integer id) {
        doctorRepository.deleteById(id);
    }
}
