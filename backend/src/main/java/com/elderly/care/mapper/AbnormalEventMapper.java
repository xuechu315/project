package com.elderly.care.mapper;

import com.elderly.care.entity.AbnormalEvent;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AbnormalEventMapper {
    
    @Select("SELECT * FROM abnormal_event")
    List<AbnormalEvent> findAll();
    
    @Select("SELECT * FROM abnormal_event WHERE id = #{id}")
    AbnormalEvent findById(Integer id);
    
    @Select("SELECT * FROM abnormal_event WHERE elder_id = #{elderId} ORDER BY timestamp DESC")
    List<AbnormalEvent> findByElderId(Integer elderId);
    
    @Select("SELECT * FROM abnormal_event WHERE resolved = 0 AND severity = 'critical' ORDER BY timestamp DESC")
    List<AbnormalEvent> findUnresolvedCriticalEvents();
    
    @Insert("INSERT INTO abnormal_event(elder_id, type, severity, confidence, detected_by, timestamp, resolved) VALUES(#{elderId}, #{type}, #{severity}, #{confidence}, #{detectedBy}, #{timestamp}, #{resolved})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AbnormalEvent event);
    
    @Update("UPDATE abnormal_event SET resolved = 1 WHERE id=#{id}")
    int resolveEvent(Integer id);
    
    @Update("UPDATE abnormal_event SET elder_id=#{elderId}, type=#{type}, severity=#{severity}, confidence=#{confidence}, detected_by=#{detectedBy}, timestamp=#{timestamp}, resolved=#{resolved} WHERE id=#{id}")
    int update(AbnormalEvent event);
    
    @Delete("DELETE FROM abnormal_event WHERE id=#{id}")
    int deleteById(Integer id);
}
