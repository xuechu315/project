package com.elderly.care.mapper;

import com.elderly.care.entity.EmergencyResponse;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface EmergencyResponseMapper {
    
    @Select("SELECT * FROM emergency_response")
    List<EmergencyResponse> findAll();
    
    @Select("SELECT * FROM emergency_response WHERE id = #{id}")
    EmergencyResponse findById(String id);
    
    @Select("SELECT * FROM emergency_response WHERE event_id = #{eventId}")
    EmergencyResponse findByEventId(Integer eventId);
    
    @Select("SELECT * FROM emergency_response WHERE status = #{status} ORDER BY dispatched_at DESC")
    List<EmergencyResponse> findByStatus(String status);
    
    @Select("SELECT * FROM emergency_response WHERE ambulance_id = #{ambulanceId}")
    List<EmergencyResponse> findByAmbulanceId(String ambulanceId);
    
    @Insert("INSERT INTO emergency_response(id, event_id, ambulance_id, eta, distance, status, dispatched_at) VALUES(#{id}, #{eventId}, #{ambulanceId}, #{eta}, #{distance}, #{status}, #{dispatchedAt})")
    int insert(EmergencyResponse response);
    
    @Update("UPDATE emergency_response SET status=#{status} WHERE id=#{id}")
    int updateStatus(@Param("id") String id, @Param("status") String status);
    
    @Update("UPDATE emergency_response SET ambulance_id=#{ambulanceId}, eta=#{eta}, distance=#{distance}, status=#{status} WHERE id=#{id}")
    int update(EmergencyResponse response);
    
    @Delete("DELETE FROM emergency_response WHERE id=#{id}")
    int deleteById(String id);
}
