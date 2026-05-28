package com.elderly.care.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMemberDTO {
    private Integer id;
    
    @NotNull(message = "老人ID不能为空")
    private Integer elderId;
    
    @NotBlank(message = "家属姓名不能为空")
    private String name;
    
    private String relationship;
    
    @NotBlank(message = "联系电话不能为空")
    private String phone;
    
    private LocalDateTime createdAt;
}
