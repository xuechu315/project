package com.example.elderlycare.service.impl;

import com.example.elderlycare.entity.Elder;
import com.example.elderlycare.exception.ResourceNotFoundException;
import com.example.elderlycare.repository.ElderRepository;
import com.example.elderlycare.service.ElderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ElderServiceImpl implements ElderService {

    @Autowired
    private ElderRepository elderRepository;

    @Override
    public List<Elder> getAllElders() {
        return elderRepository.findAll();
    }

    @Override
    public Optional<Elder> getElderById(Integer id) {
        return elderRepository.findById(id);
    }

    @Override
    public Optional<Elder> getElderByUserId(Integer userId) {
        return elderRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Elder createElder(Elder elder) {
        return elderRepository.save(elder);
    }

    @Override
    @Transactional
    public Elder updateElder(Integer id, Elder elderDetails) {
        Elder elder = elderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("老人", "id", id));
        if (elderDetails.getAge() != null) elder.setAge(elderDetails.getAge());
        if (elderDetails.getGender() != null) elder.setGender(elderDetails.getGender());
        if (elderDetails.getBloodType() != null) elder.setBloodType(elderDetails.getBloodType());
        if (elderDetails.getHeight() != null) elder.setHeight(elderDetails.getHeight());
        if (elderDetails.getWeight() != null) elder.setWeight(elderDetails.getWeight());
        return elderRepository.save(elder);
    }

    @Override
    @Transactional
    public void deleteElder(Integer id) {
        elderRepository.deleteById(id);
    }
}
