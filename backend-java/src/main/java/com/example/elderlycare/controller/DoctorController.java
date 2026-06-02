package com.example.elderlycare.controller;

import com.example.elderlycare.dto.response.ApiResponse;
import com.example.elderlycare.entity.Doctor;
import com.example.elderlycare.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 医生管理控制器
 */
@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Doctor>>> getAllDoctors() {
        return ResponseEntity.ok(ApiResponse.success(doctorService.getAllDoctors()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Doctor>> getDoctorById(@PathVariable Integer id) {
        return doctorService.getDoctorById(id)
                .map(d -> ResponseEntity.ok(ApiResponse.success(d)))
                .orElse(ResponseEntity.ok(ApiResponse.error(404, "医生不存在")));
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<ApiResponse<List<Doctor>>> getDoctorsByDepartment(@PathVariable String department) {
        return ResponseEntity.ok(ApiResponse.success(doctorService.getDoctorsByDepartment(department)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Doctor>>> searchDoctors(@RequestParam String name) {
        return ResponseEntity.ok(ApiResponse.success(doctorService.searchDoctorsByName(name)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Doctor>> createDoctor(@RequestBody Doctor doctor) {
        return ResponseEntity.ok(ApiResponse.success(doctorService.createDoctor(doctor)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Doctor>> updateDoctor(@PathVariable Integer id, @RequestBody Doctor doctor) {
        return ResponseEntity.ok(ApiResponse.success(doctorService.updateDoctor(id, doctor)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDoctor(@PathVariable Integer id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
