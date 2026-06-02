package com.example.elderlycare.service.impl;

import com.example.elderlycare.entity.OperationLog;
import com.example.elderlycare.repository.OperationLogRepository;
import com.example.elderlycare.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    private OperationLogRepository operationLogRepository;

    @Override
    public List<OperationLog> findAll() {
        return operationLogRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional
    public OperationLog create(OperationLog log) {
        return operationLogRepository.save(log);
    }

    @Override
    @Transactional
    public void deleteAll() {
        operationLogRepository.deleteAll();
    }
}
