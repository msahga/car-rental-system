package com.carrental.dto;

import com.carrental.entity.Orders;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单详情DTO
 * 
 * 功能：用于返回订单详细信息，包含关联的车辆信息和网点信息
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderDetailDTO extends Orders {

    /** 用户名（登录账号） */
    private String username;

    /** 车辆品牌 */
    private String carBrand;

    /** 车辆型号 */
    private String carModel;

    /** 车牌号 */
    private String carNumber;

    /** 车辆图片 */
    private String carImage;

    /** 取车网点名称 */
    private String pickupStoreName;

    /** 还车网点名称 */
    private String returnStoreName;

    /** 租赁天数（用于前端展示，与rentDays对应） */
    private Integer days;

    /**
     * 从Orders实体创建OrderDetailDTO
     */
    public static OrderDetailDTO fromOrders(Orders order) {
        OrderDetailDTO dto = new OrderDetailDTO();
        // 复制Orders的所有字段
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setUserId(order.getUserId());
        dto.setCarId(order.getCarId());
        dto.setPickupStoreId(order.getPickupStoreId());
        dto.setReturnStoreId(order.getReturnStoreId());
        dto.setPickupTime(order.getPickupTime());
        dto.setReturnTime(order.getReturnTime());
        dto.setRentDays(order.getRentDays());
        dto.setDailyPrice(order.getDailyPrice());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setDeposit(order.getDeposit());
        dto.setStatus(order.getStatus());
        dto.setRemark(order.getRemark());
        dto.setAdminRemark(order.getAdminRemark());
        dto.setCreateTime(order.getCreateTime());
        dto.setUpdateTime(order.getUpdateTime());
        dto.setDeleted(order.getDeleted());
        
        // 设置days字段（用于前端展示）
        dto.setDays(order.getRentDays());
        
        return dto;
    }
}