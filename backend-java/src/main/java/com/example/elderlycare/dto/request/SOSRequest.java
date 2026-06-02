package com.example.elderlycare.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SOS请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SOSRequest {

    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    @NotBlank(message = "位置信息不能为空")
    private String location;
}