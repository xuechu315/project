package com.elderly.care.controller;

import com.elderly.care.dto.FamilyMemberDTO;
import com.elderly.care.dto.Result;
import com.elderly.care.entity.FamilyMember;
import com.elderly.care.service.FamilyMemberService;
import com.elderly.care.utils.DtoConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/family-members")
@RequiredArgsConstructor
@Tag(name = "家属管理", description = "家属成员管理接口")
public class FamilyMemberController {

    private final FamilyMemberService familyMemberService;

    @GetMapping("/elder/{elderId}")
    @Operation(summary = "获取某老人的所有家属")
    public Result<List<FamilyMemberDTO>> getFamilyMembersByElderId(@PathVariable Integer elderId) {
        List<FamilyMember> members = familyMemberService.getFamilyMembersByElderId(elderId);
        return Result.success(DtoConverter.convertToFamilyMemberDTOList(members));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取家属信息")
    public Result<FamilyMemberDTO> getFamilyMemberById(@PathVariable Integer id) {
        return familyMemberService.getFamilyMemberById(id)
                .map(member -> Result.success(DtoConverter.convertToFamilyMemberDTO(member)))
                .orElse(Result.notFound("家属成员不存在"));
    }

    @PostMapping
    @Operation(summary = "添加家属成员")
    public Result<FamilyMemberDTO> createFamilyMember(@RequestBody FamilyMember familyMember) {
        FamilyMember saved = familyMemberService.createFamilyMember(familyMember);
        return Result.success(DtoConverter.convertToFamilyMemberDTO(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新家属信息")
    public Result<FamilyMemberDTO> updateFamilyMember(@PathVariable Integer id, @RequestBody FamilyMember familyMember) {
        FamilyMember updated = familyMemberService.updateFamilyMember(id, familyMember);
        return Result.success(DtoConverter.convertToFamilyMemberDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除家属成员")
    public Result<Void> deleteFamilyMember(@PathVariable Integer id) {
        familyMemberService.deleteFamilyMember(id);
        return Result.success();
    }
}
