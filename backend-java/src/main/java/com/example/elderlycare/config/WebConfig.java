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
        // 尝试多个可能的路径
        String[] possiblePaths = {
            "../frontend/",                    // 从 backend-java 目录运行
            "../../frontend/",                 // 从 target 目录运行
            "c:/Users/yangjingyi/Desktop/three/threetwo/软件工程/project-feature-all-system-integrate/frontend/"  // 绝对路径
        };
        
        String foundPath = null;
        for (String p : possiblePaths) {
            Path path = Paths.get(p).toAbsolutePath().normalize();
            System.out.println("【WebConfig】检查路径: " + path);
            if (Files.isDirectory(path)) {
                foundPath = "file:" + path.toString().replace("\\", "/") + "/";
                System.out.println("【WebConfig】找到前端目录: " + foundPath);
                break;
            }
        }
        
        if (foundPath == null) {
            System.out.println("【WebConfig】未找到前端目录，静态资源映射失败");
            return;
        }

        // 注册静态资源处理器
        registry.addResourceHandler("/doctor/doctor_dashboard.html", "/doctor/doctor_patient_detail.html")
                .addResourceLocations(foundPath)
                .resourceChain(false);
        
        registry.addResourceHandler("/doctor/**")
                .addResourceLocations(foundPath)
                .resourceChain(false);
        
        registry.addResourceHandler("/login.html")
                .addResourceLocations(foundPath)
                .resourceChain(false);

        System.out.println("【WebConfig】前端静态文件已映射: " + foundPath);
    }
}
