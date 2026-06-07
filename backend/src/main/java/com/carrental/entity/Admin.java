package com.carrental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理员实体类
 * 
 * 对应数据库表：admin
 * 功能：系统管理员信息管理
 * 
 * 字段说明：
 * id - 管理员ID，主键自增
 * username - 管理员账号
 * password - 密码，加密存储
 * realName - 真实姓名
 * phone - 手机号码
 * avatar - 头像图片URL
 * role - 角色：1-超级管理员，2-普通管理员
 * status - 状态：0-禁用，1-启用
 * createTime - 创建时间
 * updateTime - 更新时间
 * deleted - 逻辑删除：0-未删除，1-已删除
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
@Data
@TableName("admin")
public class Admin implements Serializable {

    /** 管理员ID，主键自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 管理员账号 */
    private String username;

    /** 密码，加密存储 */
    private String password;

    /** 真实姓名 */
    private String realName;

    /** 手机号码 */
    private String phone;

    /** 头像图片URL */
    private String avatar;

    /** 角色：1-超级管理员，2-普通管理员 */
    private Integer role;

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