package com.example.elderlycare.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 家属绑定请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyBindRequest {

    @NotNull(message = "家属ID不能为空")
    private Integer familyId;

    @NotNull(message = "老人ID不能为空")
    private Integer elderlyId;

    @NotBlank(message = "关系不能为空")
    private String relationship;
}