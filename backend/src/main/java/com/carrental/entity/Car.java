package com.carrental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 车辆实体类
 * 
 * 对应数据库表：car
 * 功能：车辆信息管理
 * 
 * 字段说明：
 * id - 车辆ID，主键自增
 * brand - 品牌
 * model - 型号
 * carNumber - 车牌号
 * color - 颜色
 * seats - 座位数
 * gearbox - 挡位类型：1-自动挡，2-手动挡
 * fuelType - 燃油类型：1-汽油，2-柴油，3-电动，4-混动
 * dailyPrice - 日租金（元）
 * image - 车辆图片URL
 * storeId - 所属网点ID
 * status - 状态：1-可租，2-已租，3-维修，4-报废
 * description - 车辆描述
 * createTime - 创建时间
 * updateTime - 更新时间
 * deleted - 逻辑删除：0-未删除，1-已删除
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
@Data
@TableName("car")
public class Car implements Serializable {

    /** 车辆ID，主键自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 品牌 */
    private String brand;

    /** 型号 */
    private String model;

    /** 车牌号 */
    private String carNumber;

    /** 颜色 */
    private String color;

    /** 座位数 */
    private Integer seats;

    /** 挡位类型：1-自动挡，2-手动挡 */
    private Integer gearbox;

    /** 燃油类型：1-汽油，2-柴油，3-电动，4-混动 */
    private Integer fuelType;

    /** 日租金（元） */
    private BigDecimal dailyPrice;

    /** 车辆图片URL */
    private String image;

    /** 所属网点ID */
    private Long storeId;

    /** 状态：1-可租，2-已租，3-维修，4-报废 */
    private Integer status;

    /** 车辆描述 */
    private String description;

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