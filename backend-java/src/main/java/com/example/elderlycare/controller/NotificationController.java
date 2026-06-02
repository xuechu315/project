package com.example.elderlycare.controller;

import com.example.elderlycare.agent.FamilyNotificationAgent;
import com.example.elderlycare.dto.response.ApiResponse;
import com.example.elderlycare.dto.response.ContactRecordResponse;
import com.example.elderlycare.entity.ContactRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通知控制器
 *
 * 职责：为移动端家属端 APP 提供通知查询、确认等接口
 *
 * 端点列表：
 *   GET    /api/notifications?userId={userId}&type={type}   - 获取通知列表
 *   PUT    /api/notifications/{id}/acknowledge              - 确认单条通知
 *   PUT    /api/notifications/acknowledge-all               - 批量确认通知
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private FamilyNotificationAgent familyNotificationAgent;

    /**
     * 获取通知列表
     *
     * @param userId 老人用户ID（必填）
     * @param type   通知类型（可选，emergency/alert/info/doctor_notification）
     * @return 通知记录列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ContactRecordResponse>>> getNotifications(
            @RequestParam Integer userId,
            @RequestParam(required = false) String type) {

        log.info("查询通知列表: userId={}, type={}", userId, type);

        List<ContactRecord> records;
        if (type != null && !type.isEmpty()) {
            records = familyNotificationAgent.getNotificationHistoryByType(userId, type);
        } else {
            records = familyNotificationAgent.getNotificationHistory(userId);
        }

        List<ContactRecordResponse> result = records.stream()
                .map(ContactRecordResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 确认单条通知
     *
     * @param id 通知记录ID
     * @return 操作结果
     */
    @PutMapping("/{id}/acknowledge")
    public ResponseEntity<ApiResponse<Map<String, Object>>> acknowledgeNotification(
            @PathVariable Integer id) {

        log.info("确认通知: recordId={}", id);

        familyNotificationAgent.acknowledgeNotification(id, "acknowledged");

        Map<String, Object> result = new HashMap<>();
        result.put("recordId", id);
        result.put("status", "acknowledged");

        return ResponseEntity.ok(ApiResponse.success("通知已确认", result));
    }

    /**
     * 批量确认通知
     *
     * @param userId 老人用户ID（必填）
     * @param type   通知类型（可选，为空则确认所有 sent 状态通知）
     * @return 确认数量
     */
    @PutMapping("/acknowledge-all")
    public ResponseEntity<ApiResponse<Map<String, Object>>> acknowledgeAll(
            @RequestParam Integer userId,
            @RequestParam(required = false) String type) {

        log.info("批量确认通知: userId={}, type={}", userId, type);

        int count = familyNotificationAgent.acknowledgeAllByUser(userId, type);

        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("acknowledgedCount", count);

        return ResponseEntity.ok(ApiResponse.success(
                String.format("已确认 %d 条通知", count), result));
    }
}
