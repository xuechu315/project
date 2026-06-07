package com.example.elderlycare.controller;

import com.example.elderlycare.dto.response.ApiResponse;
import com.example.elderlycare.entity.Doctor;
import com.example.elderlycare.entity.Elder;
import com.example.elderlycare.entity.ElderDoctorRelation;
import com.example.elderlycare.entity.User;
import com.example.elderlycare.repository.ElderDoctorRelationRepository;
import com.example.elderlycare.service.DoctorService;
import com.example.elderlycare.service.ElderService;
import com.example.elderlycare.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 老人管理控制器
 */
@RestController
@RequestMapping("/api/elders")
public class ElderController {

    private static final Logger log = LoggerFactory.getLogger(ElderController.class);

    @Autowired
    private ElderService elderService;
    @Autowired
    private UserService userService;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private ElderDoctorRelationRepository elderDoctorRelationRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAll(
            @RequestParam(required = false) Integer user_id) {

        // 如果提供了user_id参数，则返回该用户的老人信息
        if (user_id != null) {
            Optional<Elder> elderOpt = elderService.getElderByUserId(user_id);
            if (elderOpt.isPresent()) {
                Elder e = elderOpt.get();
                Map<String, Object> item = buildElderMap(e);
                return ResponseEntity.ok(ApiResponse.success(List.of(item)));
            } else {
                return ResponseEntity.ok(ApiResponse.success(List.of()));
            }
        }

        // 否则返回所有老人列表
        List<Map<String, Object>> list = elderService.getAllElders().stream()
                .map(this::buildElderMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    /**
     * 根据ID获取单个老人信息
     * GET /api/elders/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getById(@PathVariable Integer id) {
        Optional<Elder> elderOpt = elderService.getElderById(id);
        if (elderOpt.isPresent()) {
            Elder e = elderOpt.get();
            Map<String, Object> item = buildElderMap(e);
            return ResponseEntity.ok(ApiResponse.success(item));
        } else {
            return ResponseEntity.ok(ApiResponse.success(null));
        }
    }

    /**
     * 根据医生用户ID获取签约老人列表
     * 前端传入的是 doctor 的 user_id（user 表主键），后台先转为 doctor 表主键再查询
     * GET /api/elders/doctor/{doctorUserId}
     */
    @GetMapping("/doctor/{doctorUserId}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getByDoctorId(@PathVariable Integer doctorUserId) {
        // 通过 user_id 查找 doctor 记录，获取 doctor 表的主键 id
        Optional<Doctor> doctorOpt = doctorService.getDoctorByUserId(doctorUserId);
        if (doctorOpt.isEmpty()) {
            log.warn("未找到医生记录: userId={}", doctorUserId);
            return ResponseEntity.ok(ApiResponse.success(List.of()));
        }
        Integer doctorTableId = doctorOpt.get().getId();
        
        // 根据医生-老人关联表查询
        List<ElderDoctorRelation> relations = elderDoctorRelationRepository.findByDoctorId(doctorTableId);
        
        if (relations.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success(List.of()));
        }
        
        // 获取关联的老人ID列表
        List<Integer> elderIds = relations.stream()
                .map(ElderDoctorRelation::getElderId)
                .collect(Collectors.toList());
        
        // 查询这些老人的详细信息
        List<Map<String, Object>> list = elderService.getAllElders().stream()
                .filter(e -> elderIds.contains(e.getId()))
                .map(this::buildElderMap)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    /**
     * 构建老人信息Map
     */
    private Map<String, Object> buildElderMap(Elder e) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", e.getId());
        item.put("userId", e.getUserId());
        item.put("age", e.getAge());
        item.put("gender", e.getGender());
        item.put("bloodType", e.getBloodType());
        item.put("height", e.getHeight());
        item.put("weight", e.getWeight());
        try {
            com.example.elderlycare.dto.response.UserResponse u = userService.getUserById(e.getUserId());
            item.put("name", u.getName());
            item.put("username", u.getUsername());
            item.put("phone", u.getPhone());
        } catch (Exception ex) {
            item.put("name", "老人" + e.getId());
        }
        return item;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> create(@RequestBody Elder elder) {
        Elder saved = elderService.createElder(elder);
        Map<String, Object> item = new HashMap<>();
        item.put("id", saved.getId());
        return ResponseEntity.ok(ApiResponse.success(item));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> update(@PathVariable Integer id, @RequestBody Elder elder) {
        Elder updated = elderService.updateElder(id, elder);
        Map<String, Object> item = new HashMap<>();
        item.put("id", updated.getId());
        return ResponseEntity.ok(ApiResponse.success(item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        elderService.deleteElder(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
