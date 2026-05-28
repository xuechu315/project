package com.elderly.care.utils;

import com.elderly.care.dto.*;
import com.elderly.care.entity.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Entity和DTO转换工具类
 */
public class DtoConverter {

    // ==================== Doctor 转换 ====================
    
    public static DoctorDTO convertToDoctorDTO(Doctor doctor) {
        if (doctor == null) return null;
        return new DoctorDTO(
                doctor.getId(),
                doctor.getName(),
                doctor.getPhone(),
                doctor.getDepartment(),
                doctor.getCreatedAt()
        );
    }

    public static List<DoctorDTO> convertToDoctorDTOList(List<Doctor> doctors) {
        return doctors.stream().map(DtoConverter::convertToDoctorDTO).collect(Collectors.toList());
    }

    public static Doctor convertToDoctor(DoctorDTO dto) {
        if (dto == null) return null;
        Doctor doctor = new Doctor();
        doctor.setId(dto.getId());
        doctor.setName(dto.getName());
        doctor.setPhone(dto.getPhone());
        doctor.setDepartment(dto.getDepartment());
        return doctor;
    }

    // ==================== FamilyMember 转换 ====================
    
    public static FamilyMemberDTO convertToFamilyMemberDTO(FamilyMember familyMember) {
        if (familyMember == null) return null;
        return new FamilyMemberDTO(
                familyMember.getId(),
                familyMember.getElderId(),
                familyMember.getName(),
                familyMember.getRelationship(),
                familyMember.getPhone(),
                familyMember.getCreatedAt()
        );
    }

    public static List<FamilyMemberDTO> convertToFamilyMemberDTOList(List<FamilyMember> familyMembers) {
        return familyMembers.stream().map(DtoConverter::convertToFamilyMemberDTO).collect(Collectors.toList());
    }

    // ==================== MedicalHistory 转换 ====================
    
    public static MedicalHistoryDTO convertToMedicalHistoryDTO(ElderMedicalHistory history) {
        if (history == null) return null;
        return new MedicalHistoryDTO(
                history.getId(),
                history.getElderId(),
                history.getDiseaseName(),
                history.getDiagnosedAt(),
                history.getDescription(),
                history.getCreatedAt()
        );
    }

    public static List<MedicalHistoryDTO> convertToMedicalHistoryDTOList(List<ElderMedicalHistory> histories) {
        return histories.stream().map(DtoConverter::convertToMedicalHistoryDTO).collect(Collectors.toList());
    }

    // ==================== SosRecord 转换 ====================
    
    public static SosRecordDTO convertToSosRecordDTO(SosRecord sosRecord) {
        if (sosRecord == null) return null;
        return new SosRecordDTO(
                sosRecord.getId(),
                sosRecord.getElderId(),
                sosRecord.getLocation(),
                sosRecord.getStatus() != null ? sosRecord.getStatus().name() : null,
                sosRecord.getCreatedAt(),
                sosRecord.getResolvedAt()
        );
    }

    public static List<SosRecordDTO> convertToSosRecordDTOList(List<SosRecord> sosRecords) {
        return sosRecords.stream().map(DtoConverter::convertToSosRecordDTO).collect(Collectors.toList());
    }

    // ==================== Medication 转换 ====================
    
    public static MedicationDTO convertToMedicationDTO(Medication medication) {
        if (medication == null) return null;
        return new MedicationDTO(
                medication.getId(),
                medication.getElderId(),
                medication.getName(),
                medication.getDescription(),
                medication.getDosage(),
                medication.getFrequency(),
                medication.getTime(),
                medication.getCreatedAt()
        );
    }

    public static List<MedicationDTO> convertToMedicationDTOList(List<Medication> medications) {
        return medications.stream().map(DtoConverter::convertToMedicationDTO).collect(Collectors.toList());
    }

    // ==================== MedicationRecord 转换 ====================
    
    public static MedicationRecordDTO convertToMedicationRecordDTO(MedicationRecord record) {
        if (record == null) return null;
        return new MedicationRecordDTO(
                record.getId(),
                record.getMedicationId(),
                record.getMedication() != null ? record.getMedication().getName() : null,
                record.getStatus() != null ? record.getStatus().name() : null,
                record.getTakenAt()
        );
    }

    public static List<MedicationRecordDTO> convertToMedicationRecordDTOList(List<MedicationRecord> records) {
        return records.stream().map(DtoConverter::convertToMedicationRecordDTO).collect(Collectors.toList());
    }

    // ==================== EmergencyResponse 转换 ====================
    
    public static EmergencyResponseDTO convertToEmergencyResponseDTO(EmergencyResponse response) {
        if (response == null) return null;
        return new EmergencyResponseDTO(
                response.getId(),
                response.getEventId(),
                response.getAmbulanceId(),
                response.getEta(),
                response.getDistance(),
                response.getStatus() != null ? response.getStatus().name() : null,
                response.getDispatchedAt()
        );
    }

    public static List<EmergencyResponseDTO> convertToEmergencyResponseDTOList(List<EmergencyResponse> responses) {
        return responses.stream().map(DtoConverter::convertToEmergencyResponseDTO).collect(Collectors.toList());
    }

    // ==================== Elder 转换 ====================
    
    public static ElderDTO convertToElderDTO(Elder elder, User user) {
        if (elder == null) return null;
        return new ElderDTO(
                elder.getId(),
                elder.getUserId(),
                user != null ? user.getUsername() : null,
                user != null ? user.getName() : null,
                elder.getAge(),
                elder.getGender(),
                elder.getBloodType(),
                elder.getHeight(),
                elder.getWeight(),
                user != null ? user.getPhone() : null,
                elder.getEmergencyContactId(),
                elder.getCreatedAt()
        );
    }

    // ==================== HealthData 转换 ====================
    
    public static HealthDataDTO convertToHealthDataDTO(HealthData healthData) {
        if (healthData == null) return null;
        return new HealthDataDTO(
                healthData.getId(),
                healthData.getElderId(),
                healthData.getHeartRate(),
                healthData.getSystolicPressure(),
                healthData.getDiastolicPressure(),
                healthData.getSteps(),
                healthData.getAcceleration(),
                healthData.getRecordedAt()
        );
    }

    public static List<HealthDataDTO> convertToHealthDataDTOList(List<HealthData> healthDataList) {
        return healthDataList.stream().map(DtoConverter::convertToHealthDataDTO).collect(Collectors.toList());
    }

    // ==================== AbnormalEvent 转换 ====================
    
    public static AbnormalEventDTO convertToAbnormalEventDTO(AbnormalEvent event) {
        if (event == null) return null;
        return new AbnormalEventDTO(
                event.getId(),
                event.getElderId(),
                event.getType() != null ? event.getType().name() : null,
                event.getSeverity() != null ? event.getSeverity().name() : null,
                event.getConfidence(),
                event.getDetectedBy(),
                event.getTimestamp(),
                event.getResolved()
        );
    }

    public static List<AbnormalEventDTO> convertToAbnormalEventDTOList(List<AbnormalEvent> events) {
        return events.stream().map(DtoConverter::convertToAbnormalEventDTO).collect(Collectors.toList());
    }
}
