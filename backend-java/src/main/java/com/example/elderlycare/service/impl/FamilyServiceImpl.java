package com.example.elderlycare.service.impl;

import com.example.elderlycare.dto.request.FamilyMemberRequest;
import com.example.elderlycare.dto.response.ContactRecordResponse;
import com.example.elderlycare.dto.response.FamilyMemberResponse;
import com.example.elderlycare.entity.ContactRecord;
import com.example.elderlycare.entity.FamilyMember;
import com.example.elderlycare.repository.ContactRecordRepository;
import com.example.elderlycare.repository.FamilyMemberRepository;
import com.example.elderlycare.service.FamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 家庭服务实现类
 */
@Service
public class FamilyServiceImpl implements FamilyService {

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Autowired
    private ContactRecordRepository contactRecordRepository;

    @Override
    public List<FamilyMemberResponse> getFamilyMembersByUserId(Integer userId) {
        List<FamilyMember> members = familyMemberRepository.findByUserId(userId);
        return members.stream()
                .map(this::convertToFamilyResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void addFamilyMember(FamilyMemberRequest request) {
        FamilyMember member = new FamilyMember();
        member.setUserId(request.getUserId());
        member.setName(request.getName());
        member.setRelationship(request.getRelationship());
        member.setPhone(request.getPhone());
        familyMemberRepository.save(member);
    }

    @Override
    public List<ContactRecordResponse> getContactRecordsByUserId(Integer userId) {
        List<ContactRecord> records = contactRecordRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId);
        
        // 获取所有相关家属ID
        List<Integer> familyMemberIds = records.stream()
                .map(ContactRecord::getFamilyMemberId)
                .distinct()
                .collect(Collectors.toList());
        
        // 批量查询家属信息
        Map<Integer, String> familyMemberNameMap = familyMemberRepository.findAllById(familyMemberIds)
                .stream()
                .collect(Collectors.toMap(FamilyMember::getId, FamilyMember::getName));
        
        // 转换为响应DTO
        return records.stream()
                .map(record -> convertToContactResponse(record, familyMemberNameMap))
                .collect(Collectors.toList());
    }

    /**
     * 转换家属实体为响应DTO
     */
    private FamilyMemberResponse convertToFamilyResponse(FamilyMember member) {
        FamilyMemberResponse response = new FamilyMemberResponse();
        response.setId(member.getId());
        response.setName(member.getName());
        response.setRelationship(member.getRelationship());
        response.setPhone(member.getPhone());
        return response;
    }

    /**
     * 转换联系记录实体为响应DTO
     */
    private ContactRecordResponse convertToContactResponse(ContactRecord record, Map<Integer, String> nameMap) {
        ContactRecordResponse response = new ContactRecordResponse();
        response.setId(record.getId());
        response.setFamilyMemberName(nameMap.getOrDefault(record.getFamilyMemberId(), "Unknown"));
        response.setType(record.getType());
        response.setStatus(record.getStatus());
        response.setCreatedAt(record.getCreatedAt());
        return response;
    }
}