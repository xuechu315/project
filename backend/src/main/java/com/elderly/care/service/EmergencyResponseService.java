package com.elderly.care.service;

import com.elderly.care.entity.EmergencyResponse;
import com.elderly.care.mapper.EmergencyResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmergencyResponseService {

    private final EmergencyResponseMapper emergencyResponseMapper;

    public List<EmergencyResponse> getAllResponses() {
        return emergencyResponseMapper.findAll();
    }

    public Optional<EmergencyResponse> getResponseByEventId(Integer eventId) {
        return Optional.ofNullable(emergencyResponseMapper.findByEventId(eventId));
    }

    public List<EmergencyResponse> getResponsesByStatus(EmergencyResponse.ResponseStatus status) {
        return emergencyResponseMapper.findByStatus(status.name());
    }

    public Optional<EmergencyResponse> getResponseById(String id) {
        return Optional.ofNullable(emergencyResponseMapper.findById(id));
    }

    @Transactional
    public EmergencyResponse createResponse(EmergencyResponse response) {
        if (response.getId() == null || response.getId().isEmpty()) {
            response.setId("EMP" + System.currentTimeMillis());
        }
        response.setStatus(EmergencyResponse.ResponseStatus.DISPATCH);
        emergencyResponseMapper.insert(response);
        return response;
    }

    @Transactional
    public EmergencyResponse updateResponseStatus(String id, EmergencyResponse.ResponseStatus status) {
        EmergencyResponse response = emergencyResponseMapper.findById(id);
        if (response == null) {
            throw new RuntimeException("应急响应记录不存在");
        }
        
        response.setStatus(status);
        
        emergencyResponseMapper.update(response);
        return response;
    }

    @Transactional
    public EmergencyResponse updateResponse(String id, EmergencyResponse details) {
        EmergencyResponse response = emergencyResponseMapper.findById(id);
        if (response == null) {
            throw new RuntimeException("应急响应记录不存在");
        }
        
        response.setAmbulanceId(details.getAmbulanceId());
        response.setEta(details.getEta());
        response.setDistance(details.getDistance());
        response.setStatus(details.getStatus());
        
        emergencyResponseMapper.update(response);
        return response;
    }

    @Transactional
    public void deleteResponse(String id) {
        emergencyResponseMapper.deleteById(id);
    }
}
