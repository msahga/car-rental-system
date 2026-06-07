package com.carrental.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置类
 * 
 * 功能说明：
 * 1. 配置跨域访问规则
 * 2. 允许前端Vue项目访问后端API
 * 3. 配置允许的请求方法、请求头、请求源
 * 
 * 跨域配置说明：
 * 1. 允许所有来源访问（生产环境建议指定具体域名）
 * 2. 允许所有请求方法（GET、POST、PUT、DELETE等）
 * 3. 允许所有请求头（包括Authorization）
 * 4. 允许携带Cookie
 * 5. 预检请求缓存时间：1小时
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
@Configuration
public class CorsConfig {

    /**
     * 创建跨域过滤器
     */
    @Bean
    public CorsFilter corsFilter() {
        // 创建跨域配置对象
        CorsConfiguration config = new CorsConfiguration();

        // 允许所有来源访问（开发环境）
        // 生产环境建议改为具体的域名，如：config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOriginPattern("*");

        // 允许携带Cookie
        config.setAllowCredentials(true);

        // 允许所有请求方法
        config.addAllowedMethod("*");

        // 允许所有请求头
        config.addAllowedHeader("*");

        // 设置预检请求的缓存时间（秒）
        config.setMaxAge(3600L);

        // 创建URL映射配置源
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // 对所有路径应用跨域配置
        source.registerCorsConfiguration("/**", config);

        // 返回跨域过滤器
        return new CorsFilter(source);
    }
}