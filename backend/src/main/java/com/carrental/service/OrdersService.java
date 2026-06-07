package com.carrental.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carrental.dto.OrderDetailDTO;
import com.carrental.entity.Orders;
import com.carrental.result.PageResult;

/**
 * 订单服务接口
 * 
 * 功能说明：
 * 1. 创建租车订单
 * 2. 用户订单列表查询
 * 3. 订单详情查询
 * 4. 取消订单（用户）
 * 5. 管理员订单分页查询
 * 6. 审核订单（管理员）
 * 7. 确认取车（管理员）
 * 8. 确认还车（管理员）
 * 9. 完成订单（管理员）
 * 10. 取消订单（管理员）
 * 
 * 订单状态流转：
 * 0-待审核 → 1-已确认 → 2-已取车 → 3-已还车 → 4-已完成
 *           ↓
 *         5-已取消
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
public interface OrdersService {

    /**
     * 创建租车订单
     * 自动计算租赁天数和总价
     * 
     * @param userId 用户ID
     * @param carId 车辆ID
     * @param pickupStoreId 取车网点ID
     * @param returnStoreId 还车网点ID
     * @param pickupTime 取车时间
     * @param returnTime 还车时间
     * @param remark 备注
     * @return 创建后的订单信息
     */
    Orders create(Long userId, Long carId, Long pickupStoreId, Long returnStoreId, String pickupTime, String returnTime, String remark);

    /**
     * 查询用户订单列表
     * 
     * @param userId 用户ID
     * @param status 订单状态（可选）
     * @return 订单列表（包含车辆和网点信息）
     */
    PageResult<OrderDetailDTO> userOrders(Long userId, Long page, Long size, Integer status);

    /**
     * 根据ID查询订单详情
     * 
     * @param id 订单ID
     * @return 订单信息
     */
    Orders getById(Long id);

    /**
     * 根据ID查询订单详情（包含车辆和网点信息）
     * 
     * @param id 订单ID
     * @return 订单详情DTO
     */
    OrderDetailDTO getDetailById(Long id);

    /**
     * 分页查询订单列表（管理员，包含车辆和网点信息）
     * 
     * @param page 页码
     * @param size 每页大小
     * @param orderNo 订单编号（可选）
     * @param userId 用户ID（可选）
     * @param status 订单状态（可选）
     * @return 分页结果
     */
    PageResult<OrderDetailDTO> pageDetail(Long page, Long size, String orderNo, Long userId, Integer status);

    /**
     * 取消订单（用户）
     * 只能取消待审核状态的订单
     * 
     * @param id 订单ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean cancelByUser(Long id, Long userId);

    /**
     * 分页查询订单列表（管理员）
     * 
     * @param page 页码
     * @param size 每页大小
     * @param orderNo 订单编号（可选）
     * @param userId 用户ID（可选）
     * @param status 订单状态（可选）
     * @return 分页结果
     */
    PageResult<Orders> page(Long page, Long size, String orderNo, Long userId, Integer status);

    /**
     * 审核订单（管理员）
     * 将待审核订单变为已确认或已取消
     * 
     * @param id 订单ID
     * @param status 目标状态：1-已确认，5-已取消
     * @param adminRemark 管理员备注
     * @return 是否成功
     */
    boolean audit(Long id, Integer status, String adminRemark);

    /**
     * 确认取车（管理员）
     * 将已确认订单变为已取车
     * 同时更新车辆状态为已租
     * 
     * @param id 订单ID
     * @param adminRemark 管理员备注
     * @return 是否成功
     */
    boolean confirmPickup(Long id, String adminRemark);

    /**
     * 确认还车（管理员）
     * 将已取车订单变为已还车
     * 同时更新车辆状态为可租
     * 
     * @param id 订单ID
     * @param adminRemark 管理员备注
     * @return 是否成功
     */
    boolean confirmReturn(Long id, String adminRemark);

    /**
     * 完成订单（管理员）
     * 将已还车订单变为已完成
     * 
     * @param id 订单ID
     * @param adminRemark 管理员备注
     * @return 是否成功
     */
    boolean complete(Long id, String adminRemark);

    /**
     * 取消订单（管理员）
     * 可以取消任何状态的订单
     * 
     * @param id 订单ID
     * @param adminRemark 管理员备注
     * @return 是否成功
     */
    boolean cancelByAdmin(Long id, String adminRemark);
}