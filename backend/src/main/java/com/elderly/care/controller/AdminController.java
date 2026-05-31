package com.elderly.care.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elderly.care.dto.Result;
import com.elderly.care.entity.Doctor;
import com.elderly.care.entity.Elder;
import com.elderly.care.entity.FamilyMember;
import com.elderly.care.service.DoctorService;
import com.elderly.care.service.ElderDoctorRelationService;
import com.elderly.care.service.ElderService;
import com.elderly.care.service.FamilyMemberService;
import com.elderly.care.service.UserService;
import com.elderly.care.utils.DtoConverter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "管理汇总", description = "管理员聚合接口（用户列表、配对操作入口）")
public class AdminController {

    private final ElderService elderService;
    private final FamilyMemberService familyMemberService;
    private final DoctorService doctorService;
    private final ElderDoctorRelationService relationService;
    private final UserService userService;

    @GetMapping("/users")
    @Operation(summary = "获取平台用户汇总（老人/家属/医生）")
    public Result<Map<String, Object>> getUsers() {
        List<Elder> elders = elderService.getAllElders();
        List<Map<String, Object>> elderList = new ArrayList<>();

        for (Elder elder : elders) {
            Map<String, Object> item = new HashMap<>();
            item.put("elderId", elder.getId());
            // try to get linked user info
            item.put("name", elder.getUser() != null ? elder.getUser().getName() : (userService.getUserById(elder.getUserId()) != null ? userService.getUserById(elder.getUserId()).getName() : ("老人" + elder.getId())));
            // family members
            List<FamilyMember> families = familyMemberService.getFamilyMembersByElderId(elder.getId());
            item.put("familyMembers", DtoConverter.convertToFamilyMemberDTOList(families));
            // doctors
            item.put("doctorRelations", relationService.getRelationsByElderId(elder.getId()));
            elderList.add(item);
        }

        List<FamilyMember> allFamilies = familyMemberService.getAllFamilyMembers();
        List<Doctor> allDoctors = doctorService.getAllDoctors();

        Map<String, Object> resp = new HashMap<>();
        resp.put("elders", elderList);
        resp.put("families", DtoConverter.convertToFamilyMemberDTOList(allFamilies));
        resp.put("doctors", allDoctors);

        return Result.success(resp);
    }
}
