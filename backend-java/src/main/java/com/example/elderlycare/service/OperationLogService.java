package com.example.elderlycare.service;

import com.example.elderlycare.entity.OperationLog;

import java.util.List;

public interface OperationLogService {
    List<OperationLog> findAll();
    OperationLog create(OperationLog log);
    void deleteAll();
}
