package com.elderly.care.controller;

import com.elderly.care.dto.EmergencyResponseDTO;
import com.elderly.care.dto.Result;
import com.elderly.care.entity.EmergencyResponse;
import com.elderly.care.service.EmergencyResponseService;
import com.elderly.care.utils.DtoConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emergency-response")
@RequiredArgsConstructor
@Tag(name = "应急响应", description = "应急响应调度管理接口")
public class EmergencyResponseController {

    private final EmergencyResponseService emergencyResponseService;

    @GetMapping
    @Operation(summary = "获取所有应急响应记录")
    public Result<List<EmergencyResponseDTO>> getAllResponses() {
        List<EmergencyResponse> responses = emergencyResponseService.getAllResponses();
        return Result.success(DtoConverter.convertToEmergencyResponseDTOList(responses));
    }

    @GetMapping("/event/{eventId}")
    @Operation(summary = "根据事件ID获取响应记录")
    public Result<EmergencyResponseDTO> getResponseByEventId(@PathVariable Integer eventId) {
        return emergencyResponseService.getResponseByEventId(eventId)
                .map(response -> Result.success(DtoConverter.convertToEmergencyResponseDTO(response)))
                .orElse(Result.notFound("应急响应记录不存在"));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "按状态查询响应记录")
    public Result<List<EmergencyResponseDTO>> getResponsesByStatus(
            @PathVariable EmergencyResponse.ResponseStatus status) {
        List<EmergencyResponse> responses = emergencyResponseService.getResponsesByStatus(status);
        return Result.success(DtoConverter.convertToEmergencyResponseDTOList(responses));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取响应记录")
    public Result<EmergencyResponseDTO> getResponseById(@PathVariable String id) {
        return emergencyResponseService.getResponseById(id)
                .map(response -> Result.success(DtoConverter.convertToEmergencyResponseDTO(response)))
                .orElse(Result.notFound("应急响应记录不存在"));
    }

    @PostMapping
    @Operation(summary = "创建应急响应")
    public Result<EmergencyResponseDTO> createResponse(@RequestBody EmergencyResponse response) {
        EmergencyResponse saved = emergencyResponseService.createResponse(response);
        return Result.success(DtoConverter.convertToEmergencyResponseDTO(saved));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新响应状态")
    public Result<EmergencyResponseDTO> updateResponseStatus(
            @PathVariable String id, 
            @RequestParam EmergencyResponse.ResponseStatus status) {
        EmergencyResponse updated = emergencyResponseService.updateResponseStatus(id, status);
        return Result.success(DtoConverter.convertToEmergencyResponseDTO(updated));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新响应信息")
    public Result<EmergencyResponseDTO> updateResponse(
            @PathVariable String id, 
            @RequestBody EmergencyResponse response) {
        EmergencyResponse updated = emergencyResponseService.updateResponse(id, response);
        return Result.success(DtoConverter.convertToEmergencyResponseDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除响应记录")
    public Result<Void> deleteResponse(@PathVariable String id) {
        emergencyResponseService.deleteResponse(id);
        return Result.success();
    }
}
