package com.carrental.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义业务异常类
 * 
 * 功能说明：
 * 1. 封装业务逻辑异常
 * 2. 支持自定义错误码和错误消息
 * 3. 全局异常处理器统一捕获处理
 * 
 * 使用示例：
 * throw new BusinessException("用户名已存在");
 * throw new BusinessException(400, "参数错误");
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

    /** 错误码 */
    private Integer code;

    /** 错误消息 */
    private String message;

    /**
     * 默认构造方法
     */
    public BusinessException() {
        super();
        this.code = 500;
        this.message = "业务异常";
    }

    /**
     * 只带消息的构造方法
     */
    public BusinessException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
    }

    /**
     * 带错误码和消息的构造方法
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 带消息和原因的构造方法
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
        this.message = message;
    }

    /**
     * 全参数构造方法
     */
    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
}