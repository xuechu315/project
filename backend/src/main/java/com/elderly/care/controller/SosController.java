package com.elderly.care.controller;

import com.elderly.care.dto.Result;
import com.elderly.care.dto.SosRecordDTO;
import com.elderly.care.entity.SosRecord;
import com.elderly.care.service.SosRecordService;
import com.elderly.care.utils.DtoConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sos")
@RequiredArgsConstructor
@Tag(name = "SOS求助", description = "SOS求助记录管理接口")
public class SosController {

    private final SosRecordService sosRecordService;

    @GetMapping
    @Operation(summary = "获取所有SOS记录")
    public Result<List<SosRecordDTO>> getAllSosRecords() {
        List<SosRecord> records = sosRecordService.getAllSosRecords();
        return Result.success(DtoConverter.convertToSosRecordDTOList(records));
    }

    @GetMapping("/elder/{elderId}")
    @Operation(summary = "获取某老人的SOS记录")
    public Result<List<SosRecordDTO>> getSosRecordsByElderId(@PathVariable Integer elderId) {
        List<SosRecord> records = sosRecordService.getSosRecordsByElderId(elderId);
        return Result.success(DtoConverter.convertToSosRecordDTOList(records));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "按状态查询SOS记录")
    public Result<List<SosRecordDTO>> getSosRecordsByStatus(@PathVariable SosRecord.SosStatus status) {
        List<SosRecord> records = sosRecordService.getSosRecordsByStatus(status);
        return Result.success(DtoConverter.convertToSosRecordDTOList(records));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取SOS记录")
    public Result<SosRecordDTO> getSosRecordById(@PathVariable Integer id) {
        return sosRecordService.getSosRecordById(id)
                .map(record -> Result.success(DtoConverter.convertToSosRecordDTO(record)))
                .orElse(Result.notFound("SOS记录不存在"));
    }

    @PostMapping
    @Operation(summary = "创建SOS求助")
    public Result<SosRecordDTO> createSosRecord(@RequestBody SosRecord sosRecord) {
        SosRecord saved = sosRecordService.createSosRecord(sosRecord);
        return Result.success(DtoConverter.convertToSosRecordDTO(saved));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新SOS状态")
    public Result<SosRecordDTO> updateSosStatus(
            @PathVariable Integer id, 
            @RequestParam SosRecord.SosStatus status) {
        SosRecord updated = sosRecordService.updateSosStatus(id, status);
        return Result.success(DtoConverter.convertToSosRecordDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除SOS记录")
    public Result<Void> deleteSosRecord(@PathVariable Integer id) {
        sosRecordService.deleteSosRecord(id);
        return Result.success();
    }
}
