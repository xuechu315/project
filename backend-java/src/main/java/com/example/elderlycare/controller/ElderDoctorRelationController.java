package com.example.elderlycare.controller;

import com.example.elderlycare.dto.response.ApiResponse;
import com.example.elderlycare.entity.ElderDoctorRelation;
import com.example.elderlycare.service.ElderDoctorRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 老人-医生关联控制器
 */
@RestController
@RequestMapping("/api/elder-doctor")
public class ElderDoctorRelationController {

    @Autowired
    private ElderDoctorRelationService relationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ElderDoctorRelation>>> listByElder(@RequestParam Integer elderId) {
        return ResponseEntity.ok(ApiResponse.success(relationService.getRelationsByElderId(elderId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ElderDoctorRelation>> create(@RequestBody ElderDoctorRelation relation) {
        try {
            return ResponseEntity.ok(ApiResponse.success(relationService.createRelation(relation)));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(400, e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> delete(@RequestParam Integer elderId, @RequestParam Integer doctorId) {
        relationService.deleteRelation(elderId, doctorId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
