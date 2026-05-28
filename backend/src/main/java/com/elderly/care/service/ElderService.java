package com.elderly.care.service;

import com.elderly.care.entity.Elder;
import com.elderly.care.mapper.ElderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ElderService {

    private final ElderMapper elderMapper;

    /**
     * 获取所有老人
     */
    public List<Elder> getAllElders() {
        return elderMapper.findAll();
    }

    /**
     * 根据ID获取老人
     */
    public Optional<Elder> getElderById(Integer id) {
        return Optional.ofNullable(elderMapper.findById(id));
    }

    /**
     * 根据用户ID获取老人
     */
    public Optional<Elder> getElderByUserId(Integer userId) {
        return Optional.ofNullable(elderMapper.findByUserId(userId));
    }

    /**
     * 创建老人
     */
    public Elder createElder(Elder elder) {
        elderMapper.insert(elder);
        return elder;
    }

    /**
     * 更新老人信息
     */
    public Elder updateElder(Integer id, Elder elder) {
        elder.setId(id);
        elderMapper.update(elder);
        return elder;
    }

    /**
     * 删除老人
     */
    public void deleteElder(Integer id) {
        elderMapper.deleteById(id);
    }
}
