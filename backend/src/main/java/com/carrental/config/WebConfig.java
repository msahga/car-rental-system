package com.carrental.config;

import com.carrental.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 
 * 功能说明：
 * 1. 注册JWT拦截器
 * 2. 配置拦截路径和排除路径
 * 3. 配置静态资源路径
 * 
 * 拦截路径说明：
 * 拦截所有路径，排除登录、注册、静态资源等路径
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                // 拦截所有路径
                .addPathPatterns("/**")
                // 排除路径（不需要JWT认证的路径）
                .excludePathPatterns(
                        // 用户登录注册
                        "/user/login",
                        "/user/register",
                        // 管理员登录
                        "/admin/login",
                        // 公开接口（车辆列表、网点列表、公告列表）
                        "/car/list",
                        "/car/detail/**",
                        "/car/get/**",
                        "/store/list",
                        "/notice/list",
                        "/notice/detail/**",
                        // 静态资源
                        "/static/**",
                        "/images/**",
                        "/uploads/**",
                        // Swagger接口文档（如果使用）
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/v2/api-docs",
                        "/v3/api-docs/**",
                        "/webjars/**",
                        "/doc.html",
                        // 错误页面
                        "/error"
                );
    }

    /**
     * 配置静态资源路径
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置图片资源路径
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
        
        // 配置上传文件路径
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/uploads/");
    }
}