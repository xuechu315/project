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
     * 更新药品
     */
    @PutMapping("/medications/{id}")
    public ResponseEntity<ApiResponse<String>> updateMedication(
            @PathVariable Integer id,
            @Valid @RequestBody MedicationRequest request) {
        medicationService.updateMedication(id, request);
        return ResponseEntity.ok(ApiResponse.success("Medication updated successfully", null));
    }

    /**
     * 删除药品
     */
    @DeleteMapping("/medications/{id}")
    public ResponseEntity<ApiResponse<String>> deleteMedication(@PathVariable Integer id) {
        medicationService.deleteMedication(id);
        return ResponseEntity.ok(ApiResponse.success("Medication deleted successfully", null));
    }

    /**
     * 记录用药
     */
    @PostMapping("/medications/{medId}/record")
    public ResponseEntity<ApiResponse<String>> recordMedication(@PathVariable Integer medId) {
        medicationService.recordMedication(medId);
        return ResponseEntity.ok(ApiResponse.success("Medication recorded successfully", null));
    }

    /**
     * 后端连通性检查 / 返回后端配置
     */
    @GetMapping("/config")
    public ResponseEntity<ApiResponse<Object>> getConfig() {
        return ResponseEntity.ok(ApiResponse.success(java.util.Map.of(
            "status", "connected",
            "serverTime", java.time.LocalDateTime.now().toString()
        )));
    }
}