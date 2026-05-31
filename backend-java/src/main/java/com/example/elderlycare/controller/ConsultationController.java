package com.example.elderlycare.controller;

import com.example.elderlycare.dto.request.ConsultationRequest;
import com.example.elderlycare.dto.response.ApiResponse;
import com.example.elderlycare.dto.response.ConsultationResponse;
import com.example.elderlycare.service.ConsultationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 咨询控制器
 */
@RestController
@RequestMapping("/api")
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    /**
     * 获取咨询记录
     */
    @GetMapping("/consultations")
    public ResponseEntity<ApiResponse<List<ConsultationResponse>>> getConsultations(
            @RequestParam(defaultValue = "1") Integer user_id) {
        List<ConsultationResponse> consultations = consultationService.getConsultationsByUserId(user_id);
        return ResponseEntity.ok(ApiResponse.success(consultations));
    }

    /**
     * 添加咨询
     */
    @PostMapping("/consultations")
    public ResponseEntity<ApiResponse<ConsultationResponse>> addConsultation(
            @Valid @RequestBody ConsultationRequest request) {
        ConsultationResponse response = consultationService.addConsultation(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}