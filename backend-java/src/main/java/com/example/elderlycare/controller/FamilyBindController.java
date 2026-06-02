package com.example.elderlycare.controller;

import com.example.elderlycare.dto.request.FamilyBindRequest;
import com.example.elderlycare.dto.request.FamilyUnbindRequest;
import com.example.elderlycare.dto.response.ApiResponse;
import com.example.elderlycare.service.FamilyBindService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 家属绑定控制器
 */
@RestController
@RequestMapping("/api/family")
public class FamilyBindController {

    @Autowired
    private FamilyBindService familyBindService;

    /**
     * 获取已绑定的老人列表
     */
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getBoundElderlyList(
            @RequestParam(defaultValue = "1") Integer familyId) {
        List<Map<String, Object>> data = familyBindService.getBoundElderlyList(familyId);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * 绑定老人
     */
    @PostMapping("/bind")
    public ResponseEntity<ApiResponse<String>> bindElderly(
            @Valid @RequestBody FamilyBindRequest request) {
        try {
            familyBindService.bindElderly(request);
            return ResponseEntity.ok(ApiResponse.success("绑定成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 解绑老人
     */
    @PostMapping("/unbind")
    public ResponseEntity<ApiResponse<String>> unbindElderly(
            @Valid @RequestBody FamilyUnbindRequest request) {
        try {
            familyBindService.unbindElderly(request);
            return ResponseEntity.ok(ApiResponse.success("解绑成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 搜索老人
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> searchElderly(
            @RequestParam String keyword) {
        List<Map<String, Object>> data = familyBindService.searchElderly(keyword);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}