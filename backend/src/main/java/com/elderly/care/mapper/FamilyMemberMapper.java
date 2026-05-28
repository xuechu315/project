package com.elderly.care.mapper;

import com.elderly.care.entity.FamilyMember;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FamilyMemberMapper {
    
    @Select("SELECT * FROM family_member")
    List<FamilyMember> findAll();
    
    @Select("SELECT * FROM family_member WHERE id = #{id}")
    FamilyMember findById(Integer id);
    
    @Select("SELECT * FROM family_member WHERE elder_id = #{elderId}")
    List<FamilyMember> findByElderId(Integer elderId);
    
    @Insert("INSERT INTO family_member(elder_id, name, relationship, phone) VALUES(#{elderId}, #{name}, #{relationship}, #{phone})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(FamilyMember familyMember);
    
    @Update("UPDATE family_member SET name=#{name}, relationship=#{relationship}, phone=#{phone} WHERE id=#{id}")
    int update(FamilyMember familyMember);
    
    @Delete("DELETE FROM family_member WHERE id=#{id}")
    int deleteById(Integer id);
    
    @Delete("DELETE FROM family_member WHERE elder_id=#{elderId}")
    void deleteByElderId(Integer elderId);
}
