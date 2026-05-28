package com.elderly.care.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SosRecord {

    private Integer id;
    private Integer elderId;
    private String location;
    private SosStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private Elder elder;

    public enum SosStatus {
        PENDING("待处理"),
        RESPONDED("已响应"),
        CLOSED("已关闭");

        private final String description;

        SosStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
