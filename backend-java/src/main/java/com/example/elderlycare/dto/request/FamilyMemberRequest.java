package com.example.elderlycare.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 家属请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMemberRequest {

    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    @NotBlank(message = "家属姓名不能为空")
    private String name;

    @NotBlank(message = "关系不能为空")
    private String relationship;

    @NotBlank(message = "联系电话不能为空")
    private String phone;
}