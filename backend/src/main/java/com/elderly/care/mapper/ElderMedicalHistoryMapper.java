package com.elderly.care.mapper;

import com.elderly.care.entity.ElderMedicalHistory;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ElderMedicalHistoryMapper {
    
    @Select("SELECT * FROM elder_medical_history")
    List<ElderMedicalHistory> findAll();
    
    @Select("SELECT * FROM elder_medical_history WHERE id = #{id}")
    ElderMedicalHistory findById(Integer id);
    
    @Select("SELECT * FROM elder_medical_history WHERE elder_id = #{elderId} ORDER BY created_at DESC")
    List<ElderMedicalHistory> findByElderId(Integer elderId);
    
    @Select("SELECT * FROM elder_medical_history WHERE elder_id = #{elderId} AND disease_name LIKE CONCAT('%', #{diseaseName}, '%')")
    List<ElderMedicalHistory> findByElderIdAndDiseaseNameContaining(@Param("elderId") Integer elderId, @Param("diseaseName") String diseaseName);
    
    @Insert("INSERT INTO elder_medical_history(elder_id, disease_name, diagnosed_at, description) VALUES(#{elderId}, #{diseaseName}, #{diagnosedAt}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ElderMedicalHistory history);
    
    @Update("UPDATE elder_medical_history SET disease_name=#{diseaseName}, diagnosed_at=#{diagnosedAt}, description=#{description} WHERE id=#{id}")
    int update(ElderMedicalHistory history);
    
    @Delete("DELETE FROM elder_medical_history WHERE id=#{id}")
    int deleteById(Integer id);
    
    @Delete("DELETE FROM elder_medical_history WHERE elder_id=#{elderId}")
    void deleteByElderId(Integer elderId);
}
