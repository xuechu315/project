package com.elderly.care.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMember {

    private Integer id;
    private Integer elderId;
    private String name;
    private String relationship;
    private String phone;
    private LocalDateTime createdAt;
    private Elder elder;
}
