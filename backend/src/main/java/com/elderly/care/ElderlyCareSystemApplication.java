// src/main/java/com/elderly/care/ElderlyCareApplication.java
package com.elderly.care;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.elderly.care.mapper")
@EnableScheduling
public class ElderlyCareSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElderlyCareSystemApplication.class, args);
        System.out.println("  多银龄守护系统启动成功！");
    }
}