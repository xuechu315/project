package com.example.elderlycare.controller;

import com.example.elderlycare.dto.response.ApiResponse;
import com.example.elderlycare.entity.OperationLog;
import com.example.elderlycare.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志控制器
 */
@RestController
@RequestMapping("/api/admin/logs")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<OperationLog>>> list() {
        return ResponseEntity.ok(ApiResponse.success(operationLogService.findAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OperationLog>> create(@RequestBody OperationLog log) {
        if (log.getCreatedAt() == null) log.setCreatedAt(LocalDateTime.now());
        return ResponseEntity.ok(ApiResponse.success(operationLogService.create(log)));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clear() {
        operationLogService.deleteAll();
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
