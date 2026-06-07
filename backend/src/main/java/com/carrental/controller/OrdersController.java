package com.carrental.controller;

import com.carrental.entity.Orders;
import com.carrental.interceptor.UserContext;
import com.carrental.result.PageResult;
import com.carrental.result.Result;
import com.carrental.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 订单控制器
 * 
 * 功能说明：
 * 1. 创建租车订单接口
 * 2. 用户订单列表查询接口
 * 3. 订单详情查询接口
 * 4. 取消订单接口（用户）
 * 5. 订单分页查询接口（管理员）
 * 6. 审核订单接口（管理员）
 * 7. 确认取车接口（管理员）
 * 8. 确认还车接口（管理员）
 * 9. 完成订单接口（管理员）
 * 10. 取消订单接口（管理员）
 * 
 * API路径：
 * POST /orders - 创建订单
 * GET /orders/user - 查询用户订单列表
 * GET /orders/{id} - 查询订单详情
 * PUT /orders/cancel/{id} - 取消订单（用户）
 * GET /orders/page - 分页查询订单列表（管理员）
 * PUT /orders/audit/{id} - 审核订单（管理员）
 * PUT /orders/pickup/{id} - 确认取车（管理员）
 * PUT /orders/return/{id} - 确认还车（管理员）
 * PUT /orders/complete/{id} - 完成订单（管理员）
 * PUT /orders/cancel-admin/{id} - 取消订单（管理员）
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/orders")
@Valid
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    /**
     * 创建租车订单
     * 
     * @param carId 车辆ID
     * @param pickupStoreId 取车网点ID
     * @param returnStoreId 还车网点ID
     * @param pickupTime 取车时间
     * @param returnTime 还车时间
     * @param remark 备注
     * @return 创建结果
     */
    @PostMapping
    public Result<Orders> create(
            @RequestParam @NotNull(message = "车辆ID不能为空") Long carId,
            @RequestParam @NotNull(message = "取车网点ID不能为空") Long pickupStoreId,
            @RequestParam @NotNull(message = "还车网点ID不能为空") Long returnStoreId,
            @RequestParam @NotBlank(message = "取车时间不能为空") String pickupTime,
            @RequestParam @NotBlank(message = "还车时间不能为空") String returnTime,
            @RequestParam(required = false) String remark) {
        Long userId = UserContext.getUserId();
        log.info("创建订单请求：userId={}, carId={}, pickupStoreId={}, returnStoreId={}, pickupTime={}, returnTime={}", 
                userId, carId, pickupStoreId, returnStoreId, pickupTime, returnTime);
        Orders order = ordersService.create(userId, carId, pickupStoreId, returnStoreId, pickupTime, returnTime, remark);
        return Result.success("订单创建成功", order);
    }

    /**
     * 查询用户订单列表
     * 
     * @param page 页码
     * @param size 每页大小
     * @param status 订单状态（可选）
     * @return 分页结果
     */
    @GetMapping("/user")
    public Result<PageResult<Orders>> userOrders(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Integer status) {
        Long userId = UserContext.getUserId();
        log.info("查询用户订单列表请求：userId={}, page={}, size={}, status={}", userId, page, size, status);
        PageResult<Orders> result = ordersService.userOrders(userId, page, size, status);
        return Result.success(result);
    }

    /**
     * 查询订单详情
     * 
     * @param id 订单ID
     * @return 订单信息
     */
    @GetMapping("/{id}")
    public Result<Orders> getById(@PathVariable Long id) {
        log.info("查询订单详情请求：id={}", id);
        Orders order = ordersService.getById(id);
        return Result.success(order);
    }

    /**
     * 取消订单（用户）
     * 
     * @param id 订单ID
     * @return 取消结果
     */
    @PutMapping("/cancel/{id}")
    public Result<Boolean> cancelByUser(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        log.info("用户取消订单请求：id={}, userId={}", id, userId);
        boolean result = ordersService.cancelByUser(id, userId);
        return Result.success("订单取消成功", result);
    }

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
    @GetMapping("/page")
    public Result<PageResult<Orders>> page(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status) {
        // 验证管理员权限
        if (!UserContext.isAdmin()) {
            return Result.forbidden();
        }
        log.info("分页查询订单列表请求：page={}, size={}, orderNo={}, userId={}, status={}", 
                page, size, orderNo, userId, status);
        PageResult<Orders> result = ordersService.page(page, size, orderNo, userId, status);
        return Result.success(result);
    }

    /**
     * 审核订单（管理员）
     * 
     * @param id 订单ID
     * @param status 目标状态：1-已确认，5-已取消
     * @param adminRemark 管理员备注
     * @return 审核结果
     */
    @PutMapping("/audit/{id}")
    public Result<Boolean> audit(
            @PathVariable Long id,
            @RequestParam Integer status,
            @RequestParam(required = false) String adminRemark) {
        // 验证管理员权限
        if (!UserContext.isAdmin()) {
            return Result.forbidden();
        }
        log.info("审核订单请求：id={}, status={}, adminRemark={}", id, status, adminRemark);
        boolean result = ordersService.audit(id, status, adminRemark);
        return Result.success("审核成功", result);
    }

    /**
     * 确认取车（管理员）
     * 
     * @param id 订单ID
     * @param adminRemark 管理员备注
     * @return 确认结果
     */
    @PutMapping("/pickup/{id}")
    public Result<Boolean> confirmPickup(
            @PathVariable Long id,
            @RequestParam(required = false) String adminRemark) {
        // 验证管理员权限
        if (!UserContext.isAdmin()) {
            return Result.forbidden();
        }
        log.info("确认取车请求：id={}, adminRemark={}", id, adminRemark);
        boolean result = ordersService.confirmPickup(id, adminRemark);
        return Result.success("取车确认成功", result);
    }

    /**
     * 确认还车（管理员）
     * 
     * @param id 订单ID
     * @param adminRemark 管理员备注
     * @return 确认结果
     */
    @PutMapping("/return/{id}")
    public Result<Boolean> confirmReturn(
            @PathVariable Long id,
            @RequestParam(required = false) String adminRemark) {
        // 验证管理员权限
        if (!UserContext.isAdmin()) {
            return Result.forbidden();
        }
        log.info("确认还车请求：id={}, adminRemark={}", id, adminRemark);
        boolean result = ordersService.confirmReturn(id, adminRemark);
        return Result.success("还车确认成功", result);
    }

    /**
     * 完成订单（管理员）
     * 
     * @param id 订单ID
     * @param adminRemark 管理员备注
     * @return 完成结果
     */
    @PutMapping("/complete/{id}")
    public Result<Boolean> complete(
            @PathVariable Long id,
            @RequestParam(required = false) String adminRemark) {
        // 验证管理员权限
        if (!UserContext.isAdmin()) {
            return Result.forbidden();
        }
        log.info("完成订单请求：id={}, adminRemark={}", id, adminRemark);
        boolean result = ordersService.complete(id, adminRemark);
        return Result.success("订单完成成功", result);
    }

    /**
     * 取消订单（管理员）
     * 
     * @param id 订单ID
     * @param adminRemark 管理员备注
     * @return 取消结果
     */
    @PutMapping("/cancel-admin/{id}")
    public Result<Boolean> cancelByAdmin(
            @PathVariable Long id,
            @RequestParam(required = false) String adminRemark) {
        // 验证管理员权限
        if (!UserContext.isAdmin()) {
            return Result.forbidden();
        }
        log.info("管理员取消订单请求：id={}, adminRemark={}", id, adminRemark);
        boolean result = ordersService.cancelByAdmin(id, adminRemark);
        return Result.success("订单取消成功", result);
    }
}