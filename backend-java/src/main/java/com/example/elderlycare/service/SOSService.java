package com.example.elderlycare.service;

import com.example.elderlycare.dto.request.SOSRequest;

/**
 * SOS服务接口
 */
public interface SOSService {

    /**
     * 发送SOS求助
     */
    Integer sendSOS(SOSRequest request);

    /**
     * 取消SOS求助
     */
    void cancelSOS(Integer sosId);
}