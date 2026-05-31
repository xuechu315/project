package com.example.elderlycare.controller;

import com.example.elderlycare.dto.request.FamilyMemberRequest;
import com.example.elderlycare.dto.response.ApiResponse;
import com.example.elderlycare.dto.response.ContactRecordResponse;
import com.example.elderlycare.dto.response.FamilyMemberResponse;
import com.example.elderlycare.service.FamilyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 家庭控制器
 */
@RestController
@RequestMapping("/api")
public class FamilyController {

    @Autowired
    private FamilyService familyService;

    /**
     * 获取家属列表
     */
    @GetMapping("/family-members")
    public ResponseEntity<ApiResponse<List<FamilyMemberResponse>>> getFamilyMembers(
            @RequestParam(defaultValue = "1") Integer user_id) {
        List<FamilyMemberResponse> members = familyService.getFamilyMembersByUserId(user_id);
        return ResponseEntity.ok(ApiResponse.success(members));
    }

    /**
     * 添加家属
     */
    @PostMapping("/family-members")
    public ResponseEntity<ApiResponse<String>> addFamilyMember(
            @Valid @RequestBody FamilyMemberRequest request) {
        familyService.addFamilyMember(request);
        return ResponseEntity.ok(ApiResponse.success("Family member added successfully", null));
    }

    /**
     * 获取联系记录
     */
    @GetMapping("/contact-records")
    public ResponseEntity<ApiResponse<List<ContactRecordResponse>>> getContactRecords(
            @RequestParam(defaultValue = "1") Integer user_id) {
        List<ContactRecordResponse> records = familyService.getContactRecordsByUserId(user_id);
        return ResponseEntity.ok(ApiResponse.success(records));
    }
}