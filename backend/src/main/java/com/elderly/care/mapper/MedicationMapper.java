package com.elderly.care.mapper;

import com.elderly.care.entity.Medication;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MedicationMapper {
    
    @Select("SELECT * FROM medication")
    List<Medication> findAll();
    
    @Select("SELECT * FROM medication WHERE id = #{id}")
    Medication findById(Integer id);
    
    @Select("SELECT * FROM medication WHERE elder_id = #{elderId}")
    List<Medication> findByElderId(Integer elderId);
    
    @Select("SELECT * FROM medication WHERE elder_id = #{elderId} AND name LIKE CONCAT('%', #{name}, '%')")
    List<Medication> findByElderIdAndNameContaining(@Param("elderId") Integer elderId, @Param("name") String name);
    
    @Insert("INSERT INTO medication(elder_id, name, description, dosage, frequency, time) VALUES(#{elderId}, #{name}, #{description}, #{dosage}, #{frequency}, #{time})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Medication medication);
    
    @Update("UPDATE medication SET name=#{name}, description=#{description}, dosage=#{dosage}, frequency=#{frequency}, time=#{time} WHERE id=#{id}")
    int update(Medication medication);
    
    @Delete("DELETE FROM medication WHERE id=#{id}")
    int deleteById(Integer id);
    
    @Delete("DELETE FROM medication WHERE elder_id=#{elderId}")
    void deleteByElderId(Integer elderId);
}
