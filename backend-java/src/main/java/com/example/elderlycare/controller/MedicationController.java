package com.example.elderlycare.controller;

import com.example.elderlycare.dto.request.MedicationRequest;
import com.example.elderlycare.dto.response.ApiResponse;
import com.example.elderlycare.dto.response.MedicationResponse;
import com.example.elderlycare.service.MedicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 药品控制器
 */
@RestController
@RequestMapping("/api")
public class MedicationController {

    @Autowired
    private MedicationService medicationService;

    /**
     * 获取药品列表
     */
    @GetMapping("/medications")
    public ResponseEntity<ApiResponse<List<MedicationResponse>>> getMedications(
            @RequestParam(defaultValue = "1") Integer user_id) {
        List<MedicationResponse> medications = medicationService.getMedicationsByUserId(user_id);
        return ResponseEntity.ok(ApiResponse.success(medications));
    }

    /**
     * 添加药品
     */
    @PostMapping("/medications")
    public ResponseEntity<ApiResponse<String>> addMedication(@Valid @RequestBody MedicationRequest request) {
        medicationService.addMedication(request);
        return ResponseEntity.ok(ApiResponse.success("Medication added successfully", null));
    }

    /**
     * 记录用药
     */
    @PostMapping("/medications/{medId}/record")
    public ResponseEntity<ApiResponse<String>> recordMedication(@PathVariable Integer medId) {
        medicationService.recordMedication(medId);
        return ResponseEntity.ok(ApiResponse.success("Medication recorded successfully", null));
    }
}