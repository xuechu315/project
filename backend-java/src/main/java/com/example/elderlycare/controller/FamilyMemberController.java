package com.example.elderlycare.controller;

import com.example.elderlycare.dto.response.ApiResponse;
import com.example.elderlycare.entity.ElderFamily;
import com.example.elderlycare.service.ElderFamilyService;
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
    private ElderFamilyService elderFamilyService;

    @GetMapping("/elder/{elderId}")
    public ResponseEntity<ApiResponse<List<ElderFamily>>> getFamilyMembersByElderId(@PathVariable Integer elderId) {
        return ResponseEntity.ok(ApiResponse.success(elderFamilyService.getFamilyMembersByElderId(elderId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ElderFamily>> getFamilyMemberById(@PathVariable Integer id) {
        return elderFamilyService.getFamilyMemberById(id)
                .map(m -> ResponseEntity.ok(ApiResponse.success(m)))
                .orElse(ResponseEntity.ok(ApiResponse.error(404, "家属成员不存在")));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ElderFamily>>> getAllFamilyMembers() {
        return ResponseEntity.ok(ApiResponse.success(elderFamilyService.getAllFamilyMembers()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ElderFamily>> createFamilyMember(@RequestBody ElderFamily familyMember) {
        return ResponseEntity.ok(ApiResponse.success(elderFamilyService.createFamilyMember(familyMember)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ElderFamily>> updateFamilyMember(@PathVariable Integer id, @RequestBody ElderFamily familyMember) {
        return ResponseEntity.ok(ApiResponse.success(elderFamilyService.updateFamilyMember(id, familyMember)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFamilyMember(@PathVariable Integer id) {
        elderFamilyService.deleteFamilyMember(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
