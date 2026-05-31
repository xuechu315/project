package com.example.elderlycare.service;

import com.example.elderlycare.dto.request.ConsultationRequest;
import com.example.elderlycare.dto.response.ConsultationResponse;

import java.util.List;

/**
 * 咨询服务接口
 */
public interface ConsultationService {

    /**
     * 根据用户ID获取咨询记录
     */
    List<ConsultationResponse> getConsultationsByUserId(Integer userId);

    /**
     * 添加咨询并获取AI回复
     */
    ConsultationResponse addConsultation(ConsultationRequest request);
}