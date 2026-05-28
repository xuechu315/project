package com.elderly.care.controller;

import com.elderly.care.dto.AbnormalEventDTO;
import com.elderly.care.dto.Result;
import com.elderly.care.entity.AbnormalEvent;
import com.elderly.care.service.AbnormalEventService;
import com.elderly.care.utils.DtoConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "异常事件管理", description = "异常事件的查询和处理接口")
public class EventController {

    private final AbnormalEventService abnormalEventService;

    /**
     * 获取未处理的紧急事件
     * GET /api/events/unresolved/critical
     */
    @GetMapping("/unresolved/critical")
    @Operation(summary = "获取未处理的紧急事件")
    public Result<List<AbnormalEventDTO>> getUnresolvedCriticalEvents() {
        List<AbnormalEvent> events = abnormalEventService.getUnresolvedCriticalEvents();
        return Result.success(DtoConverter.convertToAbnormalEventDTOList(events));
    }

    /**
     * 获取老人的异常事件列表
     * GET /api/events?elderId=1
     */
    @GetMapping
    @Operation(summary = "获取老人的异常事件列表")
    public Result<List<AbnormalEventDTO>> getEventsByElderId(@RequestParam Integer elderId) {
        List<AbnormalEvent> events = abnormalEventService.getEventsByElderId(elderId);
        return Result.success(DtoConverter.convertToAbnormalEventDTOList(events));
    }

    /**
     * 创建异常事件
     * POST /api/events
     */
    @PostMapping
    @Operation(summary = "创建异常事件")
    public Result<AbnormalEventDTO> createEvent(@RequestBody AbnormalEvent event) {
        AbnormalEvent saved = abnormalEventService.createAbnormalEvent(event);
        return Result.success(DtoConverter.convertToAbnormalEventDTO(saved));
    }

    /**
     * 标记事件已处理
     * PUT /api/events/{id}/resolve
     */
    @PutMapping("/{id}/resolve")
    @Operation(summary = "标记事件已处理")
    public Result<String> resolveEvent(@PathVariable Integer id) {
        abnormalEventService.resolveEvent(id);
        return Result.success("事件已处理");
    }
}