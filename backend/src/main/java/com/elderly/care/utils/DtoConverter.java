package com.elderly.care.utils;

import com.elderly.care.dto.DoctorDTO;
import com.elderly.care.dto.ElderDTO;
import com.elderly.care.dto.FamilyMemberDTO;
import com.elderly.care.entity.Doctor;
import com.elderly.care.entity.Elder;
import com.elderly.care.entity.FamilyMember;
import com.elderly.care.entity.User;

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
                familyMember.getUserId(),
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
}
