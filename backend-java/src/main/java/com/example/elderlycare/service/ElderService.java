package com.example.elderlycare.service;

import com.example.elderlycare.entity.Elder;

import java.util.List;
import java.util.Optional;

public interface ElderService {
    List<Elder> getAllElders();
    Optional<Elder> getElderById(Integer id);
    Optional<Elder> getElderByUserId(Integer userId);
    Elder createElder(Elder elder);
    Elder updateElder(Integer id, Elder elder);
    void deleteElder(Integer id);
}
