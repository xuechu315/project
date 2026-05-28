package com.elderly.care.controller;

import com.elderly.care.dto.MedicalHistoryDTO;
import com.elderly.care.dto.Result;
import com.elderly.care.entity.ElderMedicalHistory;
import com.elderly.care.service.ElderMedicalHistoryService;
import com.elderly.care.utils.DtoConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-history")
@RequiredArgsConstructor
@Tag(name = "病史管理", description = "老人病史管理接口")
public class MedicalHistoryController {

    private final ElderMedicalHistoryService medicalHistoryService;

    @GetMapping("/elder/{elderId}")
    @Operation(summary = "获取老人的病史列表")
    public Result<List<MedicalHistoryDTO>> getMedicalHistory(@PathVariable Integer elderId) {
        List<ElderMedicalHistory> histories = medicalHistoryService.getMedicalHistoryByElderId(elderId);
        return Result.success(DtoConverter.convertToMedicalHistoryDTOList(histories));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取病史详情")
    public Result<MedicalHistoryDTO> getMedicalHistoryById(@PathVariable Integer id) {
        return medicalHistoryService.getMedicalHistoryById(id)
                .map(history -> Result.success(DtoConverter.convertToMedicalHistoryDTO(history)))
                .orElse(Result.notFound("病史记录不存在"));
    }

    @GetMapping("/elder/{elderId}/search")
    @Operation(summary = "搜索老人的特定疾病")
    public Result<List<MedicalHistoryDTO>> searchMedicalHistory(
            @PathVariable Integer elderId, 
            @RequestParam String diseaseName) {
        List<ElderMedicalHistory> histories = medicalHistoryService.searchMedicalHistory(elderId, diseaseName);
        return Result.success(DtoConverter.convertToMedicalHistoryDTOList(histories));
    }

    @PostMapping
    @Operation(summary = "添加病史记录")
    public Result<MedicalHistoryDTO> createMedicalHistory(@RequestBody ElderMedicalHistory history) {
        ElderMedicalHistory saved = medicalHistoryService.createMedicalHistory(history);
        return Result.success(DtoConverter.convertToMedicalHistoryDTO(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新病史记录")
    public Result<MedicalHistoryDTO> updateMedicalHistory(
            @PathVariable Integer id, 
            @RequestBody ElderMedicalHistory history) {
        ElderMedicalHistory updated = medicalHistoryService.updateMedicalHistory(id, history);
        return Result.success(DtoConverter.convertToMedicalHistoryDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除病史记录")
    public Result<Void> deleteMedicalHistory(@PathVariable Integer id) {
        medicalHistoryService.deleteMedicalHistory(id);
        return Result.success();
    }
}
