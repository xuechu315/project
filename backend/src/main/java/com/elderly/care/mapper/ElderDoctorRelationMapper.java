package com.elderly.care.mapper;

import com.elderly.care.entity.ElderDoctorRelation;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ElderDoctorRelationMapper {
    
    @Select("SELECT * FROM elder_doctor_relation")
    List<ElderDoctorRelation> findAll();
    
    @Select("SELECT * FROM elder_doctor_relation WHERE id = #{id}")
    ElderDoctorRelation findById(Integer id);
    
    @Select("SELECT * FROM elder_doctor_relation WHERE elder_id = #{elderId}")
    List<ElderDoctorRelation> findByElderId(Integer elderId);
    
    @Select("SELECT * FROM elder_doctor_relation WHERE doctor_id = #{doctorId}")
    List<ElderDoctorRelation> findByDoctorId(Integer doctorId);
    
    @Select("SELECT * FROM elder_doctor_relation WHERE elder_id = #{elderId} AND doctor_id = #{doctorId}")
    ElderDoctorRelation findByElderIdAndDoctorId(@Param("elderId") Integer elderId, @Param("doctorId") Integer doctorId);
    
    @Insert("INSERT INTO elder_doctor_relation(elder_id, doctor_id) VALUES(#{elderId}, #{doctorId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ElderDoctorRelation relation);
    
    @Delete("DELETE FROM elder_doctor_relation WHERE elder_id=#{elderId} AND doctor_id=#{doctorId}")
    int deleteByElderIdAndDoctorId(@Param("elderId") Integer elderId, @Param("doctorId") Integer doctorId);
}
