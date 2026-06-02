package com.example.elderlycare.controller;

import com.example.elderlycare.dto.response.ApiResponse;
import com.example.elderlycare.entity.ElderFamilyMember;
import com.example.elderlycare.service.FamilyMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 家属管理控制器 (管理端)
 */
@RestController
@RequestMapping("/api/family-members")
public class FamilyMemberController {

    @Autowired
    private FamilyMemberService familyMemberService;

    @GetMapping("/elder/{elderId}")
    public ResponseEntity<ApiResponse<List<ElderFamilyMember>>> getFamilyMembersByElderId(@PathVariable Integer elderId) {
        return ResponseEntity.ok(ApiResponse.success(familyMemberService.getFamilyMembersByElderId(elderId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ElderFamilyMember>> getFamilyMemberById(@PathVariable Integer id) {
        return familyMemberService.getFamilyMemberById(id)
                .map(m -> ResponseEntity.ok(ApiResponse.success(m)))
                .orElse(ResponseEntity.ok(ApiResponse.error(404, "家属成员不存在")));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ElderFamilyMember>>> getAllFamilyMembers() {
        return ResponseEntity.ok(ApiResponse.success(familyMemberService.getAllFamilyMembers()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ElderFamilyMember>> createFamilyMember(@RequestBody ElderFamilyMember familyMember) {
        return ResponseEntity.ok(ApiResponse.success(familyMemberService.createFamilyMember(familyMember)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ElderFamilyMember>> updateFamilyMember(@PathVariable Integer id, @RequestBody ElderFamilyMember familyMember) {
        return ResponseEntity.ok(ApiResponse.success(familyMemberService.updateFamilyMember(id, familyMember)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFamilyMember(@PathVariable Integer id) {
        familyMemberService.deleteFamilyMember(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
