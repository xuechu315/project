package com.elderly.care.mapper;

import com.elderly.care.entity.Doctor;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DoctorMapper {
    
    @Select("SELECT * FROM doctor")
    List<Doctor> findAll();
    
    @Select("SELECT * FROM doctor WHERE id = #{id}")
    Doctor findById(Integer id);
    
    @Select("SELECT * FROM doctor WHERE department = #{department}")
    List<Doctor> findByDepartment(String department);
    
    @Select("SELECT * FROM doctor WHERE name LIKE CONCAT('%', #{name}, '%')")
    List<Doctor> findByNameContaining(String name);
    
    @Insert("INSERT INTO doctor(name, phone, department) VALUES(#{name}, #{phone}, #{department})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Doctor doctor);
    
    @Update("UPDATE doctor SET name=#{name}, phone=#{phone}, department=#{department} WHERE id=#{id}")
    int update(Doctor doctor);
    
    @Delete("DELETE FROM doctor WHERE id=#{id}")
    int deleteById(Integer id);
}
