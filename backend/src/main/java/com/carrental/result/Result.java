package com.carrental.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果类
 * 
 * 功能说明：
 * 1. 统一封装API接口返回结果
 * 2. 包含状态码、消息、数据三个部分
 * 3. 提供成功和失败的静态工厂方法
 * 
 * 返回格式示例：
 * {
 *   "code": 200,
 *   "msg": "操作成功",
 *   "data": { ... }
 * }
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
@Data
public class Result<T> implements Serializable {

    /** 成功状态码 */
    public static final int SUCCESS_CODE = 200;
    
    /** 失败状态码 */
    public static final int FAIL_CODE = 500;
    
    /** 未授权状态码 */
    public static final int UNAUTHORIZED_CODE = 401;
    
    /** 禁止访问状态码 */
    public static final int FORBIDDEN_CODE = 403;

    /** 状态码 */
    private Integer code;

    /** 返回消息 */
    private String msg;

    /** 返回数据 */
    private T data;

    /** 时间戳 */
    private Long timestamp;

    /**
     * 默认构造方法
     */
    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 全参数构造方法
     */
    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功返回（无数据）
     */
    public static <T> Result<T> success() {
        return new Result<>(SUCCESS_CODE, "操作成功", null);
    }

    /**
     * 成功返回（有数据）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(SUCCESS_CODE, "操作成功", data);
    }

    /**
     * 成功返回（自定义消息）
     */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(SUCCESS_CODE, msg, data);
    }

    /**
     * 失败返回（默认消息）
     */
    public static <T> Result<T> fail() {
        return new Result<>(FAIL_CODE, "操作失败", null);
    }

    /**
     * 失败返回（自定义消息）
     */
    public static <T> Result<T> fail(String msg) {
        return new Result<>(FAIL_CODE, msg, null);
    }

    /**
     * 失败返回（自定义状态码和消息）
     */
    public static <T> Result<T> fail(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    /**
     * 未授权返回
     */
    public static <T> Result<T> unauthorized() {
        return new Result<>(UNAUTHORIZED_CODE, "未登录或Token已过期", null);
    }

    /**
     * 禁止访问返回
     */
    public static <T> Result<T> forbidden() {
        return new Result<>(FORBIDDEN_CODE, "无权限访问", null);
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return SUCCESS_CODE == this.code;
    }
}