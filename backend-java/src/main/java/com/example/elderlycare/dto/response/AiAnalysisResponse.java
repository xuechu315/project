package com.example.elderlycare.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI分析响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiAnalysisResponse {
    
    /**
     * 分析结果
     */
    private String analysis;
}