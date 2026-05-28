package com.elderly.care.mapper;

import com.elderly.care.entity.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.EnumTypeHandler;

import java.util.List;

@Mapper
public interface UserMapper {
    
    @Select("SELECT * FROM user")
    @Results({
        @Result(property = "userType", column = "user_type", typeHandler = EnumTypeHandler.class)
    })
    List<User> findAll();
    
    @Select("SELECT * FROM user WHERE id = #{id}")
    @Results({
        @Result(property = "userType", column = "user_type", typeHandler = EnumTypeHandler.class)
    })
    User findById(Integer id);
    
    @Select("SELECT * FROM user WHERE username = #{username}")
    @Results({
        @Result(property = "userType", column = "user_type", typeHandler = EnumTypeHandler.class)
    })
    User findByUsername(String username);
    
    @Select("SELECT * FROM user WHERE user_type = #{userType, typeHandler=org.apache.ibatis.type.EnumTypeHandler}")
    List<User> findByUserType(String userType);
    
    @Insert("INSERT INTO user(username, password, user_type, name, phone) VALUES(#{username}, #{password}, #{userType, typeHandler=org.apache.ibatis.type.EnumTypeHandler}, #{name}, #{phone})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    @Update("UPDATE user SET username=#{username}, password=#{password}, user_type=#{userType, typeHandler=org.apache.ibatis.type.EnumTypeHandler}, name=#{name}, phone=#{phone} WHERE id=#{id}")
    int update(User user);
    
    @Delete("DELETE FROM user WHERE id=#{id}")
    int deleteById(Integer id);
}
