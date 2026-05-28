package com.elderly.care.mapper;

import com.elderly.care.entity.SosRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SosRecordMapper {
    
    @Select("SELECT * FROM sos_record")
    List<SosRecord> findAll();
    
    @Select("SELECT * FROM sos_record WHERE id = #{id}")
    SosRecord findById(Integer id);
    
    @Select("SELECT * FROM sos_record WHERE elder_id = #{elderId} ORDER BY created_at DESC")
    List<SosRecord> findByElderId(Integer elderId);
    
    @Select("SELECT * FROM sos_record WHERE elder_id = #{elderId} AND status = #{status} ORDER BY created_at DESC")
    List<SosRecord> findByElderIdAndStatus(@Param("elderId") Integer elderId, @Param("status") String status);
    
    @Select("SELECT * FROM sos_record WHERE status = #{status} ORDER BY created_at DESC")
    List<SosRecord> findByStatus(String status);
    
    @Insert("INSERT INTO sos_record(elder_id, location, status, created_at, resolved_at) VALUES(#{elderId}, #{location}, #{status}, #{createdAt}, #{resolvedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SosRecord sosRecord);
    
    @Update("UPDATE sos_record SET status=#{status}, resolved_at=#{resolvedAt} WHERE id=#{id}")
    int update(SosRecord sosRecord);
    
    @Delete("DELETE FROM sos_record WHERE id=#{id}")
    int deleteById(Integer id);
}
