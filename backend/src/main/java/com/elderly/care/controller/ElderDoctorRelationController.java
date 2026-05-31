package com.elderly.care.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.elderly.care.dto.Result;
import com.elderly.care.entity.ElderDoctorRelation;
import com.elderly.care.service.ElderDoctorRelationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/elder-doctor")
@RequiredArgsConstructor
@Tag(name = "老人-医生关联", description = "管理老人和医生的关联关系")
public class ElderDoctorRelationController {

    private final ElderDoctorRelationService relationService;

    @GetMapping
    @Operation(summary = "按老人ID获取关联的医生关系")
    public Result<List<ElderDoctorRelation>> listByElder(@RequestParam Integer elderId) {
        return Result.success(relationService.getRelationsByElderId(elderId));
    }

    @PostMapping
    @Operation(summary = "创建老人-医生关联（绑定）")
    public Result<ElderDoctorRelation> create(@RequestBody ElderDoctorRelation relation) {
        ElderDoctorRelation created = relationService.createRelation(relation);
        return Result.success(created);
    }

    @DeleteMapping
    @Operation(summary = "删除老人-医生关联（解绑）")
    public Result<Void> delete(@RequestParam Integer elderId, @RequestParam Integer doctorId) {
        relationService.deleteRelation(elderId, doctorId);
        return Result.success();
    }
}
