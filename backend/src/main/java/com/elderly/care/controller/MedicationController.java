package com.elderly.care.controller;

import com.elderly.care.dto.MedicationDTO;
import com.elderly.care.dto.Result;
import com.elderly.care.entity.Medication;
import com.elderly.care.service.MedicationService;
import com.elderly.care.utils.DtoConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medications")
@RequiredArgsConstructor
@Tag(name = "药品管理", description = "老人用药管理接口")
public class MedicationController {

    private final MedicationService medicationService;

    @GetMapping("/elder/{elderId}")
    @Operation(summary = "获取老人的药品列表")
    public Result<List<MedicationDTO>> getMedications(@PathVariable Integer elderId) {
        List<Medication> medications = medicationService.getMedicationsByElderId(elderId);
        return Result.success(DtoConverter.convertToMedicationDTOList(medications));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取药品信息")
    public Result<MedicationDTO> getMedicationById(@PathVariable Integer id) {
        return medicationService.getMedicationById(id)
                .map(medication -> Result.success(DtoConverter.convertToMedicationDTO(medication)))
                .orElse(Result.notFound("药品信息不存在"));
    }

    @GetMapping("/elder/{elderId}/search")
    @Operation(summary = "搜索药品")
    public Result<List<MedicationDTO>> searchMedications(
            @PathVariable Integer elderId, 
            @RequestParam String name) {
        List<Medication> medications = medicationService.searchMedications(elderId, name);
        return Result.success(DtoConverter.convertToMedicationDTOList(medications));
    }

    @PostMapping
    @Operation(summary = "添加药品")
    public Result<MedicationDTO> createMedication(@RequestBody Medication medication) {
        Medication saved = medicationService.createMedication(medication);
        return Result.success(DtoConverter.convertToMedicationDTO(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新药品信息")
    public Result<MedicationDTO> updateMedication(
            @PathVariable Integer id, 
            @RequestBody Medication medication) {
        Medication updated = medicationService.updateMedication(id, medication);
        return Result.success(DtoConverter.convertToMedicationDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除药品")
    public Result<Void> deleteMedication(@PathVariable Integer id) {
        medicationService.deleteMedication(id);
        return Result.success();
    }
}
