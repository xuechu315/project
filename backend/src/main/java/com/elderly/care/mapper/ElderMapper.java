package com.elderly.care.mapper;

import com.elderly.care.entity.Elder;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ElderMapper {
    
    @Select("SELECT * FROM elder")
    List<Elder> findAll();
    
    @Select("SELECT * FROM elder WHERE id = #{id}")
    Elder findById(Integer id);
    
    @Select("SELECT * FROM elder WHERE user_id = #{userId}")
    Elder findByUserId(Integer userId);
    
    @Insert("INSERT INTO elder(user_id, age, gender, blood_type, height, weight, emergency_contact_id) VALUES(#{userId}, #{age}, #{gender}, #{bloodType}, #{height}, #{weight}, #{emergencyContactId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Elder elder);
    
    @Update("UPDATE elder SET age=#{age}, gender=#{gender}, blood_type=#{bloodType}, height=#{height}, weight=#{weight}, emergency_contact_id=#{emergencyContactId} WHERE id=#{id}")
    int update(Elder elder);
    
    @Delete("DELETE FROM elder WHERE id=#{id}")
    int deleteById(Integer id);
}
