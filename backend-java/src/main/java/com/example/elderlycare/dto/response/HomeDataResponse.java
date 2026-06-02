package com.example.elderlycare.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 首页数据响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeDataResponse {

    private UserInfo user;

    private HealthInfo health;

    private String date;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String name;
        private Integer age;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HealthInfo {
        private Integer steps;
        private Integer heartRate;
    }

    /**
     * 设置当前日期
     */
    public void setCurrentDate() {
        this.date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"));
    }
}