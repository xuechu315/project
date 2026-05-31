package com.example.elderlycare.service;

import com.example.elderlycare.dto.request.FamilyBindRequest;
import com.example.elderlycare.dto.request.FamilyUnbindRequest;

import java.util.List;
import java.util.Map;

/**
 * 家属绑定服务接口
 */
public interface FamilyBindService {

    /**
     * 获取家属已绑定的老人列表
     */
    List<Map<String, Object>> getBoundElderlyList(Integer familyId);

    /**
     * 绑定老人
     */
    void bindElderly(FamilyBindRequest request);

    /**
     * 解绑老人
     */
    void unbindElderly(FamilyUnbindRequest request);

    /**
     * 搜索老人
     */
    List<Map<String, Object>> searchElderly(String keyword);
}