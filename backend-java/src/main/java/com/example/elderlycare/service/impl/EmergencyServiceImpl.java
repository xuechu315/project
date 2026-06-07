package com.example.elderlycare.service.impl;

import com.example.elderlycare.dto.request.EmergencyAssessmentRequest;
import com.example.elderlycare.dto.request.EmergencyDispatchRequest;
import com.example.elderlycare.dto.request.RoutePlanningRequest;
import com.example.elderlycare.dto.response.EmergencyDispatchResponse;
import com.example.elderlycare.dto.response.RiskAssessmentResponse;
import com.example.elderlycare.dto.response.RoutePlanningResponse;
import com.example.elderlycare.service.EmergencyService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 应急响应服务实现类
 */
@Service
public class EmergencyServiceImpl implements EmergencyService {

    private static final Logger log = LoggerFactory.getLogger(EmergencyServiceImpl.class);

    @Value("${deepseek.api-key}")
    private String apiKey;

    private static final String BASE_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final String MODEL = "deepseek-v4-flash";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AtomicInteger taskIdGenerator = new AtomicInteger(1000);
    private final Map<Integer, EmergencyDispatchResponse> taskStore = new ConcurrentHashMap<>();

    @Override
    public RiskAssessmentResponse assessRisk(EmergencyAssessmentRequest request) {
        log.info("开始风险评估: userId={}, heartRate={}, systolic={}, diastolic={}", 
                request.getUserId(), request.getHeartRate(), request.getSystolic(), request.getDiastolic());
        
        String prompt = buildRiskAssessmentPrompt(request);
        String aiResponse = callDeepSeekApi(prompt);
        
        return parseRiskAssessment(aiResponse);
    }

    @Override
    public RoutePlanningResponse planRoute(RoutePlanningRequest request) {
        log.info("开始路线规划: origin=({},{}) dest=({},{}) mode={}", 
                request.getOriginLat(), request.getOriginLng(),
                request.getDestLat(), request.getDestLng(), request.getTransportMode());
        
        double distance = calculateDistance(
                request.getOriginLat(), request.getOriginLng(),
                request.getDestLat(), request.getDestLng()
        );
        
        String transportMode = request.getTransportMode() != null ? request.getTransportMode() : "driving";
        int estimatedMinutes = estimateTravelTime(distance, transportMode);
        
        List<RoutePlanningResponse.RouteStep> steps = generateRouteSteps(distance, estimatedMinutes);
        
        RoutePlanningResponse response = new RoutePlanningResponse();
        response.setDistanceKm(Math.round(distance * 100.0) / 100.0);
        response.setEstimatedMinutes(estimatedMinutes);
        response.setTransportMode(transportMode);
        response.setSteps(steps);
        response.setPolyline(generatePolyline(request));
        
        return response;
    }

    @Override
    public EmergencyDispatchResponse dispatchEmergency(EmergencyDispatchRequest request) {
        log.info("开始应急调度: userId={}, emergencyType={}, level={}", 
                request.getUserId(), request.getEmergencyType(), request.getEmergencyLevel());
        
        Integer taskId = taskIdGenerator.incrementAndGet();
        
        double distance = calculateDistance(
                request.getUserLat(), request.getUserLng(),
                31.2304, 121.4737
        );
        
        int estimatedMinutes = estimateTravelTime(distance, "driving");
        
        EmergencyDispatchResponse response = new EmergencyDispatchResponse();
        response.setTaskId(taskId);
        response.setStatus("已派发");
        response.setEmergencyLevel(request.getEmergencyLevel());
        response.setCreatedAt(LocalDateTime.now());
        response.setEstimatedArrivalTime(LocalDateTime.now().plusMinutes(estimatedMinutes));
        response.setDistanceKm(Math.round(distance * 100.0) / 100.0);
        response.setEstimatedMinutes(estimatedMinutes);
        response.setAssignedTeam("急救先锋 " + (taskId % 10 + 1) + " 号小组");
        response.setMessage("应急任务已派发，救援小组正在赶往现场");
        
        taskStore.put(taskId, response);
        
        return response;
    }

    @Override
    public void updateTaskStatus(Integer taskId, String status) {
        log.info("更新任务状态: taskId={}, status={}", taskId, status);
        
        EmergencyDispatchResponse task = taskStore.get(taskId);
        if (task != null) {
            task.setStatus(status);
        } else {
            throw new RuntimeException("任务不存在: " + taskId);
        }
    }

    @Override
    public double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371;
        
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLng / 2) * Math.sin(dLng / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return earthRadius * c;
    }

    @Override
    public int estimateTravelTime(double distanceKm, String transportMode) {
        double speedKmPerHour;
        
        switch (transportMode.toLowerCase()) {
            case "walking":
                speedKmPerHour = 5.0;
                break;
            case "cycling":
                speedKmPerHour = 15.0;
                break;
            case "driving":
            default:
                speedKmPerHour = 40.0;
                break;
        }
        
        int minutes = (int) Math.ceil(distanceKm / speedKmPerHour * 60);
        return Math.max(minutes, 1);
    }

    private String buildRiskAssessmentPrompt(EmergencyAssessmentRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请根据以下老人的健康监测数据进行风险评估，判断紧急程度：\n\n");
        
        if (request.getHeartRate() != null) {
            prompt.append("心率：").append(request.getHeartRate()).append(" bpm\n");
        }
        if (request.getSystolic() != null && request.getDiastolic() != null) {
            prompt.append("血压：").append(request.getSystolic()).append("/")
                  .append(request.getDiastolic()).append(" mmHg\n");
        }
        if (request.getAccelerationX() != null && request.getAccelerationY() != null && request.getAccelerationZ() != null) {
            double magnitude = Math.sqrt(
                    request.getAccelerationX() * request.getAccelerationX() +
                    request.getAccelerationY() * request.getAccelerationY() +
                    request.getAccelerationZ() * request.getAccelerationZ()
            );
            prompt.append("加速度合成值：").append(String.format("%.2f", magnitude)).append(" m/s²\n");
        }
        if (request.getBehaviorNote() != null) {
            prompt.append("行为备注：").append(request.getBehaviorNote()).append("\n");
        }
        
        prompt.append("\n请按以下格式回复（不要添加其他内容）：\n");
        prompt.append("风险等级：轻微/紧急\n");
        prompt.append("风险评分：0-1之间的数值\n");
        prompt.append("分析：一句话说明原因\n");
        prompt.append("建议：一句话说明建议措施\n");
        prompt.append("是否需要紧急救援：是/否");
        
        return prompt.toString();
    }

    private RiskAssessmentResponse parseRiskAssessment(String aiResponse) {
        String riskLevel = "轻微";
        double riskScore = 0.3;
        String analysis = aiResponse;
        String recommendation = "建议继续监测";
        boolean needEmergency = false;
        
        if (aiResponse.contains("紧急") || aiResponse.contains("严重") || aiResponse.contains("危险")) {
            riskLevel = "紧急";
            riskScore = 0.8;
            needEmergency = true;
            recommendation = "建议立即采取应急措施";
        }
        
        try {
            String[] lines = aiResponse.split("\n");
            for (String line : lines) {
                if (line.contains("风险等级")) {
                    if (line.contains("紧急")) {
                        riskLevel = "紧急";
                        needEmergency = true;
                    }
                } else if (line.contains("风险评分")) {
                    String scoreStr = line.replaceAll("[^0-9.]", "");
                    if (!scoreStr.isEmpty()) {
                        riskScore = Double.parseDouble(scoreStr);
                    }
                } else if (line.contains("分析：")) {
                    analysis = line.replace("分析：", "").trim();
                } else if (line.contains("建议：")) {
                    recommendation = line.replace("建议：", "").trim();
                } else if (line.contains("是否需要紧急救援")) {
                    if (line.contains("是")) {
                        needEmergency = true;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("解析AI响应失败，使用默认值: {}", e.getMessage());
        }
        
        return new RiskAssessmentResponse(riskLevel, riskScore, analysis, recommendation, needEmergency);
    }

    private List<RoutePlanningResponse.RouteStep> generateRouteSteps(double distanceKm, int totalMinutes) {
        List<RoutePlanningResponse.RouteStep> steps = new ArrayList<>();
        
        steps.add(new RoutePlanningResponse.RouteStep(1, "从起点出发", 0.0, 0));
        steps.add(new RoutePlanningResponse.RouteStep(2, "沿规划路线行驶", 
                Math.round(distanceKm * 100.0) / 100.0, totalMinutes * 60));
        steps.add(new RoutePlanningResponse.RouteStep(3, "到达目的地", 0.0, 0));
        
        return steps;
    }

    private String generatePolyline(RoutePlanningRequest request) {
        return String.format("origin:(%f,%f)->dest:(%f,%f)", 
                request.getOriginLat(), request.getOriginLng(),
                request.getDestLat(), request.getDestLng());
    }

    private String callDeepSeekApi(String prompt) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(BASE_URL);

            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", MODEL);

            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "你是一位专业的应急医疗评估专家，擅长根据健康数据判断风险等级。请严格按照指定格式回复。");

            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(systemMessage);
            messages.add(userMessage);
            requestBody.put("messages", messages);

            requestBody.put("temperature", 0.3);
            requestBody.put("max_tokens", 500);

            httpPost.setEntity(new StringEntity(objectMapper.writeValueAsString(requestBody), "UTF-8"));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");

                JsonNode rootNode = objectMapper.readTree(responseBody);
                if (rootNode.has("error")) {
                    throw new RuntimeException(rootNode.get("error").toString());
                }
                if (rootNode.has("choices") && rootNode.get("choices").isArray() && !rootNode.get("choices").isEmpty()) {
                    JsonNode choiceNode = rootNode.get("choices").get(0);
                    if (choiceNode.has("message") && choiceNode.get("message").has("content")) {
                        String content = choiceNode.get("message").get("content").asText();
                        if (content != null && !content.isBlank()) {
                            return content.trim();
                        }
                    }
                }
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("AI风险评估失败: " + e.getMessage(), e);
        }

        throw new RuntimeException("AI风险评估返回为空");
    }
}
