package com.elderly.care.enums;

public enum EventType {
    fall("摔倒"),
    heart_rate_abnormal("心率异常"),
    blood_pressure_abnormal("血压异常"),
    prolonged_inactivity("长时间不动");

    private final String description;

    EventType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}