package com.example.elderlycare.service.impl;

import com.example.elderlycare.service.AiAnalysisService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * AI分析服务实现
 */
@Service
public class AiAnalysisServiceImpl implements AiAnalysisService {

    // DeepSeek API配置
    private static final String API_KEY = "sk-55a524dcba5e4f55a9303acc13ad841a";
    private static final String BASE_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final String MODEL = "deepseek-v4-flash";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String analyzeHeartRate(int heartRate) {
        String prompt = String.format("请分析以下心率数据，只需要输出简洁的健康建议：\n" +
                "心率：%d bpm\n\n" +
                "要求：\n" +
                "1. 如果正常，直接说\"心率正常，继续保持。\"\n" +
                "2. 如果异常，请用一句话说明问题和建议，例如\"心率过慢，建议适当活动身体，如有不适请及时就医。\"\n" +
                "3. 语言简洁，适合老年人理解。", heartRate);

        return callDeepSeekApi(prompt);
    }

    @Override
    public String analyzeBloodPressure(int systolic, int diastolic) {
        String prompt = String.format("请分析以下血压数据，只需要输出简洁的健康建议：\n" +
                "收缩压：%d mmHg\n" +
                "舒张压：%d mmHg\n\n" +
                "要求：\n" +
                "1. 如果正常，直接说\"血压正常，继续保持。\"\n" +
                "2. 如果异常，请用一句话说明问题和建议，例如\"血压偏高，请注意休息，保持情绪稳定。\"\n" +
                "3. 语言简洁，适合老年人理解。", systolic, diastolic);

        return callDeepSeekApi(prompt);
    }

    @Override
    public String analyzeAcceleration(double x, double y, double z) {
        double magnitude = Math.sqrt(x * x + y * y + z * z);
        String prompt = String.format(
                "请分析以下老年人佩戴设备的加速度传感器数据，判断是否存在跌倒风险或异常活动：\n" +
                "X轴：%.2f m/s²\n" +
                "Y轴：%.2f m/s²\n" +
                "Z轴：%.2f m/s²\n" +
                "合成加速度：%.2f m/s²\n\n" +
                "要求：\n" +
                "1. 若数据正常（静止或日常轻微活动），直接说\"活动正常，未检测到异常。\"\n" +
                "2. 若疑似跌倒或剧烈晃动，请用一句话说明风险和建议，例如\"检测到剧烈加速度变化，疑似跌倒，建议立即查看老人状况。\"\n" +
                "3. 语言简洁，适合监护人员快速理解。",
                x, y, z, magnitude);

        return callDeepSeekApi(prompt);
    }

    /**
     * 调用DeepSeek API
     */
    private String callDeepSeekApi(String prompt) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(BASE_URL);

            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + API_KEY);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", MODEL);

            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "你是一位专业的医生助手，擅长分析老年人的健康数据并给出专业建议。请用温和、关怀的语气回答。");

            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);

            java.util.List<Map<String, String>> messages = new java.util.ArrayList<>();
            messages.add(systemMessage);
            messages.add(userMessage);
            requestBody.put("messages", messages);

            requestBody.put("temperature", 0.7);
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
            throw new RuntimeException("AI分析失败: " + e.getMessage(), e);
        }

        throw new RuntimeException("AI分析返回为空");
    }
}
