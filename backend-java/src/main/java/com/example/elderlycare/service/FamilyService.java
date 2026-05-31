package com.example.elderlycare.service;

import com.example.elderlycare.dto.request.FamilyMemberRequest;
import com.example.elderlycare.dto.response.ContactRecordResponse;
import com.example.elderlycare.dto.response.FamilyMemberResponse;

import java.util.List;

/**
 * 家庭服务接口
 */
public interface FamilyService {

    /**
     * 根据用户ID获取家属列表
     */
    List<FamilyMemberResponse> getFamilyMembersByUserId(Integer userId);

    /**
     * 添加家属
     */
    void addFamilyMember(FamilyMemberRequest request);

    /**
     * 根据用户ID获取联系记录
     */
    List<ContactRecordResponse> getContactRecordsByUserId(Integer userId);
}