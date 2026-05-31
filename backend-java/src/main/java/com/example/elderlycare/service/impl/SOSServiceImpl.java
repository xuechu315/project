package com.example.elderlycare.service.impl;

import com.example.elderlycare.dto.request.SOSRequest;
import com.example.elderlycare.entity.ContactRecord;
import com.example.elderlycare.entity.FamilyMember;
import com.example.elderlycare.entity.SOSRecord;
import com.example.elderlycare.repository.ContactRecordRepository;
import com.example.elderlycare.repository.FamilyMemberRepository;
import com.example.elderlycare.repository.SOSRecordRepository;
import com.example.elderlycare.service.SOSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SOS服务实现类
 */
@Service
public class SOSServiceImpl implements SOSService {

    @Autowired
    private SOSRecordRepository sosRecordRepository;

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Autowired
    private ContactRecordRepository contactRecordRepository;

    @Override
    @Transactional
    public Integer sendSOS(SOSRequest request) {
        // 创建SOS记录
        SOSRecord sosRecord = new SOSRecord();
        sosRecord.setUserId(request.getUserId());
        sosRecord.setLocation(request.getLocation());
        sosRecord.setStatus(SOSRecord.SOSStatus.PENDING);
        sosRecordRepository.save(sosRecord);

        // 通知家属
        List<FamilyMember> familyMembers = familyMemberRepository.findByUserId(request.getUserId());
        for (FamilyMember member : familyMembers) {
            ContactRecord contactRecord = new ContactRecord();
            contactRecord.setUserId(request.getUserId());
            contactRecord.setFamilyMemberId(member.getId());
            contactRecord.setType("emergency");
            contactRecord.setStatus("sent");
            contactRecordRepository.save(contactRecord);
        }

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