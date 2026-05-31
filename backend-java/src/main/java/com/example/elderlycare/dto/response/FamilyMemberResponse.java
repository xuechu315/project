package com.example.elderlycare.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 家属响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMemberResponse {

    private Integer id;

    private String name;

    private String relationship;

    private String phone;
}