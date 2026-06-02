package com.example.elderlycare.controller;

import com.example.elderlycare.dto.response.ApiResponse;
import com.example.elderlycare.entity.Doctor;
import com.example.elderlycare.entity.Elder;
import com.example.elderlycare.entity.ElderFamilyMember;
import com.example.elderlycare.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 管理员聚合接口
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private ElderService elderService;
    @Autowired
    private FamilyMemberService familyMemberService;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private ElderDoctorRelationService relationService;
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUsers() {
        List<Elder> elders = elderService.getAllElders();
        List<Map<String, Object>> elderList = new ArrayList<>();

        for (Elder elder : elders) {
            Map<String, Object> item = new HashMap<>();
            item.put("elderId", elder.getId());
            try {
                item.put("name", userService.getUserById(elder.getUserId()).getName());
            } catch (Exception e) {
                item.put("name", "老人" + elder.getId());
            }
            List<ElderFamilyMember> families = familyMemberService.getFamilyMembersByElderId(elder.getId());
            item.put("familyMembers", families);
            item.put("doctorRelations", relationService.getRelationsByElderId(elder.getId()));
            elderList.add(item);
        }

        List<ElderFamilyMember> allFamilies = familyMemberService.getAllFamilyMembers();
        List<Doctor> allDoctors = doctorService.getAllDoctors();

        Map<String, Object> resp = new HashMap<>();
        resp.put("elders", elderList);
        resp.put("families", allFamilies);
        resp.put("doctors", allDoctors);

        return ResponseEntity.ok(ApiResponse.success(resp));
    }
}
