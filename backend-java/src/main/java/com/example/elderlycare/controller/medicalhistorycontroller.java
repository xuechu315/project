package com.example.elderlycare.controller;

import com.example.elderlycare.dto.response.ApiResponse;
import com.example.elderlycare.entity.MedicalHistory;
import com.example.elderlycare.service.MedicalHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 既往病史管理控制器
 */
@RestController
@RequestMapping("/api/medical-history")
public class MedicalHistoryController {

    @Autowired
    private MedicalHistoryService medicalHistoryService;

    /**
     * 根据老人ID获取既往病史列表
     * GET /api/medical-history/elder/{elderId}
     */
    @GetMapping("/elder/{elderId}")
    public ResponseEntity<ApiResponse<List<MedicalHistory>>> getByElderId(@PathVariable Integer elderId) {
        List<MedicalHistory> histories = medicalHistoryService.getByElderId(elderId);
        return ResponseEntity.ok(ApiResponse.success(histories));
    }

    /**
     * 添加既往病史
     * POST /api/medical-history
     */
    @PostMapping
    public ResponseEntity<ApiResponse<MedicalHistory>> add(@RequestBody MedicalHistory medicalHistory) {
        MedicalHistory saved = medicalHistoryService.add(medicalHistory);
        return ResponseEntity.ok(ApiResponse.success(saved));
    }

    /**
     * 更新既往病史
     * PUT /api/medical-history/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicalHistory>> update(@PathVariable Integer id, @RequestBody MedicalHistory medicalHistory) {
        medicalHistory.setId(id);
        MedicalHistory updated = medicalHistoryService.update(medicalHistory);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    /**
     * 删除既往病史
     * DELETE /api/medical-history/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Integer id) {
        medicalHistoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功"));
    }

    /**
     * 批量保存老人的既往病史
     * POST /api/medical-history/batch/{elderId}
     */
    @PostMapping("/batch/{elderId}")
    public ResponseEntity<ApiResponse<List<MedicalHistory>>> batchSave(
            @PathVariable Integer elderId,
            @RequestBody List<MedicalHistory> histories) {
        List<MedicalHistory> saved = medicalHistoryService.batchSave(elderId, histories);
        return ResponseEntity.ok(ApiResponse.success(saved));
    }
}
