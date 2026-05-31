package com.example.elderlycare.service.impl;

import com.example.elderlycare.dto.request.FamilyBindRequest;
import com.example.elderlycare.dto.request.FamilyUnbindRequest;
import com.example.elderlycare.entity.FamilyMember;
import com.example.elderlycare.entity.User;
import com.example.elderlycare.repository.FamilyMemberRepository;
import com.example.elderlycare.repository.UserRepository;
import com.example.elderlycare.service.FamilyBindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 家属绑定服务实现类
 */
@Service
public class FamilyBindServiceImpl implements FamilyBindService {

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Map<String, Object>> getBoundElderlyList(Integer familyId) {
        // 获取当前家属绑定的老人列表
        List<FamilyMember> familyMembers = familyMemberRepository.findByFamilyId(familyId);
        
        // 获取所有用户信息
        List<Integer> userIds = familyMembers.stream()
                .map(FamilyMember::getUserId)
                .distinct()
                .collect(Collectors.toList());
        
        Map<Integer, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        
        // 构建返回结果
        List<Map<String, Object>> result = new ArrayList<>();
        for (FamilyMember member : familyMembers) {
            User user = userMap.get(member.getUserId());
            if (user != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", user.getId());
                item.put("name", user.getName());
                item.put("relationship", member.getRelationship());
                item.put("age", user.getAge());
                item.put("gender", user.getGender());
                item.put("bloodType", user.getBloodType());
                item.put("height", user.getHeight());
                item.put("weight", user.getWeight());
                item.put("phone", member.getPhone());
                item.put("username", user.getUsername());
                result.add(item);
            }
        }
        return result;
    }

    @Override
    @Transactional
    public void bindElderly(FamilyBindRequest request) {
        // 检查当前家属是否已绑定该老人
        List<FamilyMember> existing = familyMemberRepository.findByFamilyIdAndUserId(
                request.getFamilyId(), request.getElderlyId());
        if (!existing.isEmpty()) {
            throw new RuntimeException("您已绑定该老人");
        }

        // 创建绑定记录
        FamilyMember familyMember = new FamilyMember();
        familyMember.setUserId(request.getElderlyId());
        familyMember.setFamilyId(request.getFamilyId());
        familyMember.setName("家属");
        familyMember.setRelationship(request.getRelationship());
        familyMember.setPhone("12345678900"); // 默认手机号
        familyMemberRepository.save(familyMember);
    }

    @Override
    @Transactional
    public void unbindElderly(FamilyUnbindRequest request) {
        // 删除当前家属与该老人的绑定记录
        List<FamilyMember> familyMembers = familyMemberRepository.findByFamilyIdAndUserId(
                request.getFamilyId(), request.getElderlyId());
        if (familyMembers.isEmpty()) {
            throw new RuntimeException("未找到绑定记录");
        }
        familyMemberRepository.deleteAll(familyMembers);
    }

    @Override
    public List<Map<String, Object>> searchElderly(String keyword) {
        // 搜索老人用户
        List<User> users = userRepository.findAll().stream()
                .filter(u -> u.getUserType() == User.UserType.elder)
                .filter(u -> 
                    (u.getUsername() != null && u.getUsername().contains(keyword)) ||
                    (u.getName() != null && u.getName().contains(keyword))
                )
                .collect(Collectors.toList());

        // 构建返回结果
        List<Map<String, Object>> result = new ArrayList<>();
        for (User user : users) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", user.getId());
            item.put("name", user.getName());
            item.put("age", user.getAge());
            item.put("gender", user.getGender());
            item.put("username", user.getUsername());
            item.put("phone", "");
            result.add(item);
        }
        return result;
    }
}