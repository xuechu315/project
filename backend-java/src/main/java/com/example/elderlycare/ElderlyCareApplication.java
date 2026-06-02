package com.example.elderlycare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 智慧养老服务平台后端应用主类
 */
@SpringBootApplication
@EnableScheduling
public class ElderlyCareApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElderlyCareApplication.class, args);
    }
}