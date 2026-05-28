package com.elderly.care.service;

import com.elderly.care.entity.Doctor;
import com.elderly.care.mapper.DoctorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DoctorService {

    private final DoctorMapper doctorMapper;

    public List<Doctor> getAllDoctors() {
        return doctorMapper.findAll();
    }

    public Optional<Doctor> getDoctorById(Integer id) {
        return Optional.ofNullable(doctorMapper.findById(id));
    }

    public List<Doctor> getDoctorsByDepartment(String department) {
        return doctorMapper.findByDepartment(department);
    }

    public List<Doctor> searchDoctorsByName(String name) {
        return doctorMapper.findByNameContaining(name);
    }

    @Transactional
    public Doctor createDoctor(Doctor doctor) {
        doctorMapper.insert(doctor);
        return doctor;
    }

    @Transactional
    public Doctor updateDoctor(Integer id, Doctor doctorDetails) {
        Doctor doctor = doctorMapper.findById(id);
        if (doctor == null) {
            throw new RuntimeException("医生不存在");
        }
        
        doctor.setName(doctorDetails.getName());
        doctor.setPhone(doctorDetails.getPhone());
        doctor.setDepartment(doctorDetails.getDepartment());
        doctorMapper.update(doctor);
        
        return doctor;
    }

    @Transactional
    public void deleteDoctor(Integer id) {
        doctorMapper.deleteById(id);
    }
}
