package com.example.elderlycare.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 异常事件实体类
 */
@Entity
@Table(name = "abnormal_event", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_severity", columnList = "severity")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbnormalEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private EventType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 20)
    private Severity severity;

    @Column(name = "confidence")
    private Float confidence;

    @Column(name = "detected_by", length = 30)
    private String detectedBy;

    @Column(name = "timestamp")
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(name = "resolved")
    private Integer resolved = 0;

    /**
     * 异常类型枚举
     */
    public enum EventType {
        跌倒, 心率异常, 血压异常, 长时间不动
    }

    /**
     * 严重等级枚举
     */
    public enum Severity {
        紧急, 警告, 注意, 正常
    }
}