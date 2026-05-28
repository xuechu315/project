package com.elderly.care.mapper;

import com.elderly.care.entity.HealthData;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface HealthDataMapper {
    
    @Select("SELECT * FROM health_data")
    List<HealthData> findAll();
    
    @Select("SELECT * FROM health_data WHERE id = #{id}")
    HealthData findById(Integer id);
    
    @Select("SELECT * FROM health_data WHERE elder_id = #{elderId} ORDER BY recorded_at DESC LIMIT 1")
    HealthData findLatestByElderId(Integer elderId);
    
    @Select("SELECT * FROM health_data WHERE elder_id = #{elderId} AND recorded_at >= #{startTime} AND recorded_at <= #{endTime} ORDER BY recorded_at DESC")
    List<HealthData> findByElderIdAndTimeRange(@Param("elderId") Integer elderId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    @Select("SELECT * FROM health_data WHERE elder_id = #{elderId} ORDER BY recorded_at DESC")
    List<HealthData> findByElderId(Integer elderId);
    
    @Insert("INSERT INTO health_data(elder_id, heart_rate, systolic_pressure, diastolic_pressure, steps, acceleration, recorded_at) VALUES(#{elderId}, #{heartRate}, #{systolicPressure}, #{diastolicPressure}, #{steps}, #{acceleration}, #{recordedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(HealthData healthData);
    
    @Delete("DELETE FROM health_data WHERE id=#{id}")
    int deleteById(Integer id);
}
