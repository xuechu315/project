package com.elderly.care.mapper;

import com.elderly.care.entity.MedicationRecord;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MedicationRecordMapper {
    
    @Select("SELECT * FROM medication_record")
    List<MedicationRecord> findAll();
    
    @Select("SELECT * FROM medication_record WHERE id = #{id}")
    MedicationRecord findById(Integer id);
    
    @Select("SELECT * FROM medication_record WHERE medication_id = #{medicationId} ORDER BY taken_at DESC")
    List<MedicationRecord> findByMedicationId(Integer medicationId);
    
    @Select("SELECT * FROM medication_record WHERE taken_at >= #{startTime} AND taken_at <= #{endTime} ORDER BY taken_at DESC")
    List<MedicationRecord> findByTakenAtBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    @Select("SELECT * FROM medication_record WHERE medication_id = #{medicationId} AND status = #{status} ORDER BY taken_at DESC")
    List<MedicationRecord> findByMedicationIdAndStatus(@Param("medicationId") Integer medicationId, @Param("status") String status);
    
    @Insert("INSERT INTO medication_record(medication_id, taken_at, status) VALUES(#{medicationId}, #{takenAt}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MedicationRecord record);
    
    @Update("UPDATE medication_record SET status=#{status} WHERE id=#{id}")
    int update(MedicationRecord record);
    
    @Delete("DELETE FROM medication_record WHERE id=#{id}")
    int deleteById(Integer id);
}
