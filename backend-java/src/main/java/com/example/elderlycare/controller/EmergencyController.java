package com.example.elderlycare.controller;

import com.example.elderlycare.agent.EmergencyResponseAgent;
import com.example.elderlycare.agent.StaffNotificationAgent;
import com.example.elderlycare.dto.request.EmergencyAssessmentRequest;
import com.example.elderlycare.dto.request.EmergencyDispatchRequest;
import com.example.elderlycare.dto.request.RoutePlanningRequest;
import com.example.elderlycare.dto.response.ApiResponse;
import com.example.elderlycare.dto.response.EmergencyDispatchResponse;
import com.example.elderlycare.dto.response.RiskAssessmentResponse;
import com.example.elderlycare.dto.response.RoutePlanningResponse;
import com.example.elderlycare.service.EmergencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 应急响应控制器
 */
@RestController
@RequestMapping("/api/emergency")
public class EmergencyController {

    private static final Logger log = LoggerFactory.getLogger(EmergencyController.class);

    @Autowired
    private EmergencyService emergencyService;

    @Autowired
    private EmergencyResponseAgent emergencyResponseAgent;

    @Autowired
    private StaffNotificationAgent staffNotificationAgent;

    /**
     * 风险评估接口
     * POST /api/emergency/assess
     */
    @PostMapping("/assess")
    public ResponseEntity<ApiResponse<RiskAssessmentResponse>> assessRisk(
            @RequestBody EmergencyAssessmentRequest request) {
        
        log.info("风险评估请求: userId={}", request.getUserId());
        
        RiskAssessmentResponse response = emergencyService.assessRisk(request);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 路线规划接口
     * POST /api/emergency/route
     */
    @PostMapping("/route")
    public ResponseEntity<ApiResponse<RoutePlanningResponse>> planRoute(
            @RequestBody RoutePlanningRequest request) {
        
        log.info("路线规划请求: origin=({},{}) dest=({},{})", 
                request.getOriginLat(), request.getOriginLng(),
                request.getDestLat(), request.getDestLng());
        
        RoutePlanningResponse response = emergencyService.planRoute(request);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 应急调度接口
     * POST /api/emergency/dispatch
     */
    @PostMapping("/dispatch")
    public ResponseEntity<ApiResponse<EmergencyDispatchResponse>> dispatchEmergency(
            @RequestBody EmergencyDispatchRequest request) {
        
        log.info("应急调度请求: userId={}, type={}, level={}", 
                request.getUserId(), request.getEmergencyType(), request.getEmergencyLevel());
        
        EmergencyDispatchResponse response = emergencyService.dispatchEmergency(request);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 更新任务状态接口
     * PUT /api/emergency/task/{taskId}/status
     */
    @PutMapping("/task/{taskId}/status")
    public ResponseEntity<ApiResponse<String>> updateTaskStatus(
            @PathVariable Integer taskId,
            @RequestParam String status) {
        
        log.info("更新任务状态: taskId={}, status={}", taskId, status);
        
        emergencyService.updateTaskStatus(taskId, status);
        
        return ResponseEntity.ok(ApiResponse.success("任务状态已更新"));
    }

    /**
     * 计算两点间距离接口
     * GET /api/emergency/distance
     */
    @GetMapping("/distance")
    public ResponseEntity<ApiResponse<Double>> calculateDistance(
            @RequestParam Double lat1,
            @RequestParam Double lng1,
            @RequestParam Double lat2,
            @RequestParam Double lng2) {
        
        double distance = emergencyService.calculateDistance(lat1, lng1, lat2, lng2);
        
        return ResponseEntity.ok(ApiResponse.success(Math.round(distance * 100.0) / 100.0));
    }

    /**
     * 估算行程时间接口
     * GET /api/emergency/eta
     */
    @GetMapping("/eta")
    public ResponseEntity<ApiResponse<Integer>> estimateTravelTime(
            @RequestParam Double distanceKm,
            @RequestParam(defaultValue = "driving") String transportMode) {
        
        int minutes = emergencyService.estimateTravelTime(distanceKm, transportMode);
        
        return ResponseEntity.ok(ApiResponse.success(minutes));
    }

    // ==================== 人工决策支持接口 ====================

    /**
     * 获取待人工决策的事件列表
     * GET /api/emergency/pending-events
     */
    @GetMapping("/pending-events")
    public ResponseEntity<ApiResponse<List<EmergencyResponseAgent.AlertEvent>>> getPendingEvents() {
        List<EmergencyResponseAgent.AlertEvent> events = emergencyResponseAgent.getPendingEvents();
        return ResponseEntity.ok(ApiResponse.success(events));
    }

    /**
     * 人工决策处置接口
     * POST /api/emergency/human-decision
     *
     * @param eventId  事件ID
     * @param decision 决策（DISPATCH_AMBULANCE / HOME_VISIT / MONITOR / DISMISS）
     * @param notes    决策备注
     */
    @PostMapping("/human-decision")
    public ResponseEntity<ApiResponse<String>> humanDecision(
            @RequestParam String eventId,
            @RequestParam String decision,
            @RequestParam(required = false) String notes) {

        log.info("人工决策请求: eventId={}, decision={}, notes={}", eventId, decision, notes);
        emergencyResponseAgent.humanDecision(eventId, decision, notes);
        return ResponseEntity.ok(ApiResponse.success("人工决策已处理"));
    }

    /**
     * 人工修正风险等级接口
     * PUT /api/emergency/correct-risk-level
     *
     * @param eventId         事件ID
     * @param correctedLevel  修正后的风险等级（MINOR / MODERATE / CRITICAL）
     * @param reason          修正原因
     */
    @PutMapping("/correct-risk-level")
    public ResponseEntity<ApiResponse<String>> correctRiskLevel(
            @RequestParam String eventId,
            @RequestParam String correctedLevel,
            @RequestParam String reason) {

        log.info("人工修正风险等级: eventId={}, correctedLevel={}, reason={}", eventId, correctedLevel, reason);
        EmergencyResponseAgent.RiskLevel level = EmergencyResponseAgent.RiskLevel.valueOf(correctedLevel);
        emergencyResponseAgent.correctRiskLevel(eventId, level, reason);
        return ResponseEntity.ok(ApiResponse.success("风险等级已修正"));
    }

    /**
     * 获取当前告警队列状态
     * GET /api/emergency/queue
     */
    @GetMapping("/queue")
    public ResponseEntity<ApiResponse<List<EmergencyResponseAgent.AlertEvent>>> getQueueSnapshot() {
        List<EmergencyResponseAgent.AlertEvent> queue = emergencyResponseAgent.getQueueSnapshot();
        return ResponseEntity.ok(ApiResponse.success(queue));
    }

    /**
     * 获取在线工作人员数量
     * GET /api/emergency/staff/count
     */
    @GetMapping("/staff/count")
    public ResponseEntity<ApiResponse<Integer>> getOnlineStaffCount() {
        int count = staffNotificationAgent.getOnlineStaffCount();
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
