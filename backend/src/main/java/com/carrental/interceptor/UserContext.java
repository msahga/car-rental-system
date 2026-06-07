package com.carrental.interceptor;

/**
 * 用户上下文类（ThreadLocal）
 * 
 * 功能说明：
 * 1. 存储当前登录用户信息
 * 2. 使用ThreadLocal保证线程安全
 * 3. 在Service层获取当前用户信息
 * 
 * 使用方法：
 * Long userId = UserContext.getUserId();
 * String username = UserContext.getUsername();
 * String role = UserContext.getRole();
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
public class UserContext {

    /** 用户ID */
    private static final ThreadLocal<Long> userIdThreadLocal = new ThreadLocal<>();

    /** 用户名 */
    private static final ThreadLocal<String> usernameThreadLocal = new ThreadLocal<>();

    /** 角色 */
    private static final ThreadLocal<String> roleThreadLocal = new ThreadLocal<>();

    /**
     * 设置用户ID
     */
    public static void setUserId(Long userId) {
        userIdThreadLocal.set(userId);
    }

    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        return userIdThreadLocal.get();
    }

    /**
     * 设置用户名
     */
    public static void setUsername(String username) {
        usernameThreadLocal.set(username);
    }

    /**
     * 获取用户名
     */
    public static String getUsername() {
        return usernameThreadLocal.get();
    }

    /**
     * 设置角色
     */
    public static void setRole(String role) {
        roleThreadLocal.set(role);
    }

    /**
     * 获取角色
     */
    public static String getRole() {
        return roleThreadLocal.get();
    }

    /**
     * 判断是否为管理员
     */
    public static boolean isAdmin() {
        String role = roleThreadLocal.get();
        return "admin".equals(role);
    }

    /**
     * 清理ThreadLocal
     * 防止内存泄漏
     */
    public static void clear() {
        userIdThreadLocal.remove();
        usernameThreadLocal.remove();
        roleThreadLocal.remove();
    }
}