package com.example.elderlycare.controller;

import com.example.elderlycare.dto.request.SOSRequest;
import com.example.elderlycare.dto.response.ApiResponse;
import com.example.elderlycare.service.SOSService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * SOS控制器
 */
@RestController
@RequestMapping("/api")
public class SOSController {

    @Autowired
    private SOSService sosService;

    /**
     * 发送SOS求助
     */
    @PostMapping("/sos")
    public ResponseEntity<ApiResponse<Map<String, Object>>> sendSOS(@Valid @RequestBody SOSRequest request) {
        Integer sosId = sosService.sendSOS(request);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "SOS sent successfully");
        result.put("sos_id", sosId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 取消SOS求助
     */
    @PostMapping("/sos/{sosId}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelSOS(@PathVariable Integer sosId) {
        try {
            sosService.cancelSOS(sosId);
            return ResponseEntity.ok(ApiResponse.success("SOS cancelled successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(404, e.getMessage()));
        }
    }
}