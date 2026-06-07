package com.carrental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 网点实体类
 * 
 * 对应数据库表：store
 * 功能：租赁网点信息管理
 * 
 * 字段说明：
 * id - 网点ID，主键自增
 * name - 网点名称
 * address - 网点地址
 * phone - 联系电话
 * manager - 网点负责人
 * latitude - 纬度坐标
 * longitude - 经度坐标
 * status - 状态：0-停用，1-正常
 * createTime - 创建时间
 * updateTime - 更新时间
 * deleted - 逻辑删除：0-未删除，1-已删除
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
@Data
@TableName("store")
public class Store implements Serializable {

    /** 网点ID，主键自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 网点名称 */
    private String name;

    /** 网点地址 */
    private String address;

    /** 联系电话 */
    private String phone;

    /** 网点负责人 */
    private String manager;

    /** 纬度坐标 */
    private BigDecimal latitude;

    /** 经度坐标 */
    private BigDecimal longitude;

    /** 状态：0-停用，1-正常 */
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