package com.example.elderlycare.service.impl;

import com.example.elderlycare.dto.request.ConsultationRequest;
import com.example.elderlycare.dto.response.ConsultationResponse;
import com.example.elderlycare.entity.Consultation;
import com.example.elderlycare.repository.ConsultationRepository;
import com.example.elderlycare.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 咨询服务实现类
 */
@Service
public class ConsultationServiceImpl implements ConsultationService {

    @Autowired
    private ConsultationRepository consultationRepository;

    @Override
    public List<ConsultationResponse> getConsultationsByUserId(Integer userId) {
        List<Consultation> consultations = consultationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return consultations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ConsultationResponse addConsultation(ConsultationRequest request) {
        Consultation consultation = new Consultation();
        consultation.setUserId(request.getUserId());
        consultation.setMessage(request.getMessage());
        consultation.setType(Consultation.ConsultationType.valueOf(request.getType()));
        
        // 简单的AI回复逻辑
        consultation.setResponse(generateAIResponse(request.getMessage()));
        
        consultationRepository.save(consultation);
        return convertToResponse(consultation);
    }

    /**
     * 生成AI回复
     */
    private String generateAIResponse(String message) {
        if (message.contains("头晕")) {
            return "我监测到您的血压稍微有点高，最近有没有按时吃药呀？";
        } else if (message.contains("吃药") || message.contains("服药")) {
            return "请您按时服药，这对您的健康非常重要。";
        } else if (message.contains("心跳") || message.contains("心慌")) {
            return "建议您坐下休息片刻，我已经为您记录了心率数据。";
        } else {
            return "您好，我是您的健康助手，有什么可以帮助您的吗？";
        }
    }

    /**
     * 转换实体为响应DTO
     */
    private ConsultationResponse convertToResponse(Consultation consultation) {
        ConsultationResponse response = new ConsultationResponse();
        response.setId(consultation.getId());
        response.setMessage(consultation.getMessage());
        response.setResponse(consultation.getResponse());
        response.setType(consultation.getType().name());
        response.setCreatedAt(consultation.getCreatedAt());
        return response;
    }
}