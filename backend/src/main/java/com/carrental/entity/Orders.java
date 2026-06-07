package com.carrental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 * 
 * 对应数据库表：orders
 * 功能：租车订单信息管理
 * 
 * 字段说明：
 * id - 订单ID，主键自增
 * orderNo - 订单编号
 * userId - 用户ID
 * carId - 车辆ID
 * pickupStoreId - 取车网点ID
 * returnStoreId - 还车网点ID
 * pickupTime - 取车时间
 * returnTime - 还车时间
 * rentDays - 租赁天数
 * dailyPrice - 日租金
 * totalPrice - 订单总价
 * deposit - 押金
 * status - 订单状态：0-待审核，1-已确认，2-已取车，3-已还车，4-已完成，5-已取消
 * remark - 备注
 * adminRemark - 管理员备注
 * createTime - 创建时间
 * updateTime - 更新时间
 * deleted - 逻辑删除：0-未删除，1-已删除
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
@Data
@TableName("orders")
public class Orders implements Serializable {

    /** 订单ID，主键自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 订单编号 */
    private String orderNo;

    /** 用户ID */
    private Long userId;

    /** 车辆ID */
    private Long carId;

    /** 取车网点ID */
    private Long pickupStoreId;

    /** 还车网点ID */
    private Long returnStoreId;

    /** 取车时间 */
    private LocalDateTime pickupTime;

    /** 还车时间 */
    private LocalDateTime returnTime;

    /** 租赁天数 */
    private Integer rentDays;

    /** 日租金 */
    private BigDecimal dailyPrice;

    /** 订单总价 */
    private BigDecimal totalPrice;

    /** 押金 */
    private BigDecimal deposit;

    /** 订单状态：0-待审核，1-已确认，2-已取车，3-已还车，4-已完成，5-已取消 */
    private Integer status;

    /** 备注 */
    private String remark;

    /** 管理员备注 */
    private String adminRemark;

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