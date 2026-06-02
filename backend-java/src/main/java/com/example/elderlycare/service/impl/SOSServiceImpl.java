package com.example.elderlycare.service.impl;

import com.example.elderlycare.agent.FamilyNotificationAgent;
import com.example.elderlycare.dto.request.SOSRequest;
import com.example.elderlycare.entity.SOSRecord;
import com.example.elderlycare.repository.SOSRecordRepository;
import com.example.elderlycare.service.SOSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * SOS服务实现类
 */
@Service
public class SOSServiceImpl implements SOSService {

    @Autowired
    private SOSRecordRepository sosRecordRepository;

    @Autowired
    private FamilyNotificationAgent familyNotificationAgent;

    @Override
    @Transactional
    public Integer sendSOS(SOSRequest request) {
        // 创建SOS记录
        SOSRecord sosRecord = new SOSRecord();
        sosRecord.setUserId(request.getUserId());
        sosRecord.setLocation(request.getLocation());
        sosRecord.setStatus(SOSRecord.SOSStatus.PENDING);
        sosRecordRepository.save(sosRecord);

        // 通过 FamilyNotificationAgent 通知所有家属和社区医生（SOS属于最高优先级）
        String message = String.format("【SOS紧急求助】老人 %d 正在通过 SOS 按钮发起紧急求助%s",
                request.getUserId(),
                request.getLocation() != null ? "，位置：" + request.getLocation() : "，位置未知");
        familyNotificationAgent.notifyAll(request.getUserId(), message, "emergency");

        return sosRecord.getId();
    }

    @Override
    public void cancelSOS(Integer sosId) {
        SOSRecord sosRecord = sosRecordRepository.findById(sosId)
                .orElseThrow(() -> new RuntimeException("SOS记录不存在"));
        sosRecord.setStatus(SOSRecord.SOSStatus.已闭环);
        sosRecord.setResolvedAt(LocalDateTime.now());
        sosRecordRepository.save(sosRecord);
    }
}