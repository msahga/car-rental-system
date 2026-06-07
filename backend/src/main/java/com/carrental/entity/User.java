package com.carrental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 
 * 对应数据库表：user
 * 功能：普通用户信息管理
 * 
 * 字段说明：
 * id - 用户ID，主键自增
 * username - 用户名，登录账号
 * password - 密码，加密存储
 * realName - 真实姓名
 * phone - 手机号码
 * email - 电子邮箱
 * idCard - 身份证号码
 * avatar - 头像图片URL
 * status - 状态：0-禁用，1-启用
 * createTime - 创建时间
 * updateTime - 更新时间
 * deleted - 逻辑删除：0-未删除，1-已删除
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
@Data
@TableName("user")
public class User implements Serializable {

    /** 用户ID，主键自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 用户名，登录账号 */
    private String username;

    /** 密码，加密存储 */
    private String password;

    /** 真实姓名 */
    private String realName;

    /** 手机号码 */
    private String phone;

    /** 电子邮箱 */
    private String email;

    /** 身份证号码 */
    private String idCard;

    /** 头像图片URL */
    private String avatar;

    /** 状态：0-禁用，1-启用 */
    private Integer status;

    /** 创建时间，自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间，自动填充 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除：0-未删除，1-已删除 */
    @TableLogic
    private Integer deleted;
}