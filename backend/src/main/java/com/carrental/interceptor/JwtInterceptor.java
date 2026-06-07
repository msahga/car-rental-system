package com.carrental.interceptor;

import com.carrental.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT拦截器
 * 
 * 功能说明：
 * 1. 拦截所有需要认证的请求
 * 2. 验证JWT令牌有效性
 * 3. 将用户信息存入请求上下文
 * 
 * 拦截流程：
 * 1. 从请求头获取JWT令牌
 * 2. 验证令牌有效性
 * 3. 解析令牌获取用户信息
 * 4. 将用户信息存入ThreadLocal
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 请求预处理
     * 验证JWT令牌并提取用户信息
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头获取JWT令牌
        String authHeader = request.getHeader(jwtUtil.getHeader());

        // 判断请求头是否包含JWT令牌
        if (authHeader == null || !authHeader.startsWith(jwtUtil.getPrefix())) {
            log.warn("请求未携带JWT令牌：{}", request.getRequestURI());
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录或Token已过期\",\"data\":null}");
            return false;
        }

        // 提取JWT令牌（去除前缀）
        String token = authHeader.substring(jwtUtil.getPrefix().length() + 1);

        // 验证JWT令牌有效性
        if (!jwtUtil.validateToken(token)) {
            log.warn("JWT令牌验证失败：{}", token);
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"Token无效或已过期\",\"data\":null}");
            return false;
        }

        // 解析JWT令牌获取用户信息
        Long userId = jwtUtil.getUserId(token);
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        // 将用户信息存入请求上下文（ThreadLocal）
        UserContext.setUserId(userId);
        UserContext.setUsername(username);
        UserContext.setRole(role);

        log.info("JWT验证成功：用户={}, 角色={}, URI={}", username, role, request.getRequestURI());
        return true;
    }

    /**
     * 请求完成后清理ThreadLocal
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理ThreadLocal，防止内存泄漏
        UserContext.clear();
    }
}