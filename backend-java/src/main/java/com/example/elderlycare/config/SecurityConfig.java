package com.example.elderlycare.config;

import com.example.elderlycare.security.TokenAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security配置类
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private TokenAuthFilter tokenAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 登录接口允许匿名访问
                .requestMatchers("/api/login", "/api/verify").permitAll()
                // 管理端API — 仅admin角色（增/删/查）
                .requestMatchers("/api/admin/**").hasRole("admin")
                .requestMatchers(HttpMethod.POST, "/api/users/**").hasRole("admin")
                .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("admin")
                .requestMatchers(HttpMethod.GET, "/api/users").hasRole("admin")
                // 允许医生等角色修改自己的用户信息（如手机号）
                .requestMatchers(HttpMethod.PUT, "/api/users/**").authenticated()
                // 静态资源（支持传统HTML页面）
                .requestMatchers("/js/**", "/css/**", "/fonts/**", "/images/**").permitAll()
                // 其他所有 /api/** 需要登录
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )
            // 在 UsernamePasswordAuthenticationFilter 之前插入 Token 过滤器
            .addFilterBefore(tokenAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of("*"));
        config.addAllowedOrigin("null");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setExposedHeaders(List.of("Content-Type", "Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
