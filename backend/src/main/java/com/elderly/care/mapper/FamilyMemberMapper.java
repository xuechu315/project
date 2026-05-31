package com.elderly.care.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.elderly.care.entity.FamilyMember;

@Mapper
public interface FamilyMemberMapper {
    
    @Select("SELECT * FROM family_member")
    List<FamilyMember> findAll();
    
    @Select("SELECT * FROM family_member WHERE id = #{id}")
    FamilyMember findById(Integer id);
    
    @Select("SELECT * FROM family_member WHERE elder_id = #{elderId}")
    List<FamilyMember> findByElderId(Integer elderId);
    
    @Select("SELECT * FROM family_member WHERE user_id = #{userId}")
    FamilyMember findByUserId(Integer userId);
    
    @Insert("INSERT INTO family_member(user_id, elder_id, name, relationship, phone) VALUES(#{userId}, #{elderId}, #{name}, #{relationship}, #{phone})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(FamilyMember familyMember);
    
    @Update("UPDATE family_member SET elder_id=#{elderId}, name=#{name}, relationship=#{relationship}, phone=#{phone} WHERE id=#{id}")
    int update(FamilyMember familyMember);
    
    @Delete("DELETE FROM family_member WHERE id=#{id}")
    int deleteById(Integer id);
    
    @Delete("DELETE FROM family_member WHERE elder_id=#{elderId}")
    void deleteByElderId(Integer elderId);
}
