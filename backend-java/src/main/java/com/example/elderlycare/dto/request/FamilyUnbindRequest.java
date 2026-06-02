package com.example.elderlycare.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 家属解绑请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyUnbindRequest {

    @NotNull(message = "家属ID不能为空")
    private Integer familyId;

    @NotNull(message = "老人ID不能为空")
    private Integer elderlyId;
}