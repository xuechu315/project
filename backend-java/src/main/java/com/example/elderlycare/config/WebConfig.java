package com.example.elderlycare.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Web配置 - 将前端静态文件目录映射为Spring Boot可访问的静态资源
 * 用户打开 http://localhost:8080/doctor/doctor_patient_detail.html 即可访问
 * 打开 http://localhost:8080/login.html 即可访问登录页
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ============ 自动查找 frontend 目录（任何人都能用，无需改路径）============
        // 从当前工作目录开始，逐级向上找 frontend 文件夹，最多找 5 级
        String foundPath = null;
        Path startDir = Paths.get("").toAbsolutePath().normalize();

        for (int level = 0; level < 5; level++) {
            Path candidate = startDir.resolve("frontend");
            System.out.println("【WebConfig】检查路径: " + candidate);
            if (Files.isDirectory(candidate)) {
                foundPath = "file:" + candidate.toString().replace("\\", "/") + "/";
                System.out.println("【WebConfig】找到前端目录: " + foundPath);
                break;
            }
            // 没找到则向上一级
            startDir = startDir.getParent();
            if (startDir == null) break;
        }

        if (foundPath == null) {
            System.out.println("【WebConfig】未找到前端目录，静态资源映射失败");
            return;
        }

        registry.addResourceHandler("/**")
                .addResourceLocations(foundPath)
                .setCachePeriod(0);

        System.out.println("【WebConfig】前端静态文件已映射: " + foundPath);
    }
}
