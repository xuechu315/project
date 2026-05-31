package com.elderly.care.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elderly.care.dto.Result;
import com.elderly.care.entity.OperationLog;
import com.elderly.care.mapper.OperationLogMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/logs")
@RequiredArgsConstructor
@Tag(name = "操作日志", description = "系统操作行为溯源审计接口（数据库持久化）")
public class OperationLogController {

    private final OperationLogMapper operationLogMapper;

    @GetMapping
    @Operation(summary = "查询操作日志（按时间倒序）")
    public Result<List<OperationLog>> list() {
        return Result.success(operationLogMapper.findAll());
    }

    @PostMapping
    @Operation(summary = "新增操作日志")
    public Result<OperationLog> create(@RequestBody OperationLog log) {
        if (log.getCreatedAt() == null) log.setCreatedAt(LocalDateTime.now());
        operationLogMapper.insert(log);
        return Result.success(log);
    }

    @DeleteMapping
    @Operation(summary = "清空所有日志（管理员）")
    public Result<Void> clear() {
        operationLogMapper.deleteAll();
        return Result.success();
    }
}
