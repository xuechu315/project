package com.elderly.care.mapper;

import com.elderly.care.entity.OperationLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OperationLogMapper {

    @Select("SELECT * FROM operation_log ORDER BY created_at DESC")
    List<OperationLog> findAll();

    @Insert("INSERT INTO operation_log(operator, operation, created_at) VALUES(#{operator}, #{operation}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OperationLog log);

    @Delete("DELETE FROM operation_log")
    int deleteAll();
}
