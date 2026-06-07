package com.carrental.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carrental.dto.OrderDetailDTO;
import com.carrental.entity.Car;
import com.carrental.entity.Orders;
import com.carrental.entity.Store;
import com.carrental.entity.User;
import com.carrental.exception.BusinessException;
import com.carrental.mapper.CarMapper;
import com.carrental.mapper.OrdersMapper;
import com.carrental.mapper.StoreMapper;
import com.carrental.mapper.UserMapper;
import com.carrental.result.PageResult;
import com.carrental.service.CarService;
import com.carrental.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * 订单服务实现类
 * 
 * 功能说明：
 * 1. 实现订单管理所有功能
 * 2. 自动计算租赁天数和总价
 * 3. 订单状态与车辆状态联动
 * 4. 生成唯一订单编号
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
@Slf4j
@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private CarMapper carMapper;

    @Autowired
    private StoreMapper storeMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CarService carService;

    /**
     * 创建租车订单
     */
    @Override
    @Transactional
    public Orders create(Long userId, Long carId, Long pickupStoreId, Long returnStoreId, String pickupTime, String returnTime, String remark) {
        // 查询车辆信息
        Car car = carMapper.selectById(carId);
        if (car == null) {
            throw new BusinessException("车辆不存在");
        }

        // 检查车辆状态
        if (car.getStatus() != 1) {
            throw new BusinessException("车辆当前不可租");
        }

        // 解析取车时间和还车时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime pickupDateTime = LocalDateTime.parse(pickupTime, formatter);
        LocalDateTime returnDateTime = LocalDateTime.parse(returnTime, formatter);

        // 验证时间合法性
        if (pickupDateTime.isAfter(returnDateTime)) {
            throw new BusinessException("取车时间不能晚于还车时间");
        }

        if (pickupDateTime.isBefore(LocalDateTime.now())) {
            throw new BusinessException("取车时间不能早于当前时间");
        }

        // 计算租赁天数（按天计算，不足一天按一天计算）
        long days = ChronoUnit.DAYS.between(pickupDateTime, returnDateTime);
        if (days == 0) {
            days = 1; // 不足一天按一天计算
        }

        // 计算订单总价
        BigDecimal dailyPrice = car.getDailyPrice();
        BigDecimal totalPrice = dailyPrice.multiply(BigDecimal.valueOf(days));

        // 创建订单对象
        Orders order = new Orders();
        order.setOrderNo(generateOrderNo()); // 生成订单编号
        order.setUserId(userId);
        order.setCarId(carId);
        order.setPickupStoreId(pickupStoreId);
        order.setReturnStoreId(returnStoreId);
        order.setPickupTime(pickupDateTime);
        order.setReturnTime(returnDateTime);
        order.setRentDays((int) days);
        order.setDailyPrice(dailyPrice);
        order.setTotalPrice(totalPrice);
        order.setDeposit(BigDecimal.ZERO); // 押金暂设为0
        order.setStatus(0); // 待审核
        order.setRemark(remark);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        // 插入数据库
        ordersMapper.insert(order);

        log.info("订单创建成功：订单编号={}, 用户ID={}, 车辆ID={}, 天数={}, 总价={}", 
                order.getOrderNo(), userId, carId, days, totalPrice);
        return order;
    }

    /**
     * 生成唯一订单编号
     * 格式：ORD + 时间戳 + 随机数
     */
    private String generateOrderNo() {
        return "ORD" + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss") + IdUtil.randomUUID().substring(0, 4);
    }

    /**
     * 查询用户订单列表（包含车辆和网点信息）
     */
    @Override
    public PageResult<OrderDetailDTO> userOrders(Long userId, Long page, Long size, Integer status) {
        // 创建分页对象
        Page<Orders> pageObj = new Page<>(page, size);

        // 创建查询条件
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId, userId);
        wrapper.eq(status != null, Orders::getStatus, status);
        wrapper.orderByDesc(Orders::getCreateTime);

        // 执行分页查询
        Page<Orders> result = ordersMapper.selectPage(pageObj, wrapper);

        // 转换为OrderDetailDTO列表
        java.util.List<OrderDetailDTO> detailList = result.getRecords().stream()
                .map(this::buildOrderDetailDTO)
                .collect(java.util.stream.Collectors.toList());

        // 返回分页结果
        PageResult<OrderDetailDTO> pageResult = new PageResult<>();
        pageResult.setRecords(detailList);
        pageResult.setTotal(result.getTotal());
        pageResult.setCurrent(result.getCurrent());
        pageResult.setSize(result.getSize());
        pageResult.setPages(result.getPages());
        
        return pageResult;
    }

    /**
     * 根据ID查询订单详情
     */
    @Override
    public Orders getById(Long id) {
        return ordersMapper.selectById(id);
    }

    /**
     * 根据ID查询订单详情（包含车辆和网点信息）
     */
    @Override
    public OrderDetailDTO getDetailById(Long id) {
        Orders order = ordersMapper.selectById(id);
        if (order == null) {
            return null;
        }
        return buildOrderDetailDTO(order);
    }

    /**
     * 构建订单详情DTO（补充用户、车辆和网点信息）
     */
    private OrderDetailDTO buildOrderDetailDTO(Orders order) {
        OrderDetailDTO dto = OrderDetailDTO.fromOrders(order);
        
        // 查询用户信息
        if (order.getUserId() != null) {
            User user = userMapper.selectById(order.getUserId());
            if (user != null) {
                dto.setUsername(user.getUsername());
            }
        }
        
        // 查询车辆信息
        if (order.getCarId() != null) {
            Car car = carMapper.selectById(order.getCarId());
            if (car != null) {
                dto.setCarBrand(car.getBrand());
                dto.setCarModel(car.getModel());
                dto.setCarNumber(car.getCarNumber());
                dto.setCarImage(car.getImage());
            }
        }
        
        // 查询取车网点信息
        if (order.getPickupStoreId() != null) {
            Store store = storeMapper.selectById(order.getPickupStoreId());
            if (store != null) {
                dto.setPickupStoreName(store.getName());
            }
        }
        
        // 查询还车网点信息
        if (order.getReturnStoreId() != null) {
            Store store = storeMapper.selectById(order.getReturnStoreId());
            if (store != null) {
                dto.setReturnStoreName(store.getName());
            }
        }
        
        return dto;
    }

    /**
     * 分页查询订单列表（管理员，包含车辆和网点信息）
     */
    @Override
    public PageResult<OrderDetailDTO> pageDetail(Long page, Long size, String orderNo, Long userId, Integer status) {
        // 创建分页对象
        Page<Orders> pageObj = new Page<>(page, size);

        // 创建查询条件
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(orderNo), Orders::getOrderNo, orderNo);
        wrapper.eq(userId != null, Orders::getUserId, userId);
        wrapper.eq(status != null, Orders::getStatus, status);
        wrapper.orderByDesc(Orders::getCreateTime);

        // 执行分页查询
        Page<Orders> result = ordersMapper.selectPage(pageObj, wrapper);

        // 转换为OrderDetailDTO列表
        java.util.List<OrderDetailDTO> detailList = result.getRecords().stream()
                .map(this::buildOrderDetailDTO)
                .collect(java.util.stream.Collectors.toList());

        // 返回分页结果
        PageResult<OrderDetailDTO> pageResult = new PageResult<>();
        pageResult.setRecords(detailList);
        pageResult.setTotal(result.getTotal());
        pageResult.setCurrent(result.getCurrent());
        pageResult.setSize(result.getSize());
        pageResult.setPages(result.getPages());
        
        return pageResult;
    }

    /**
     * 取消订单（用户）
     */
    @Override
    @Transactional
    public boolean cancelByUser(Long id, Long userId) {
        // 查询订单信息
        Orders order = getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 验证订单所属用户
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }

        // 只能取消待审核状态的订单
        if (order.getStatus() != 0) {
            throw new BusinessException("只能取消待审核状态的订单");
        }

        // 更新订单状态为已取消
        order.setStatus(5);
        order.setUpdateTime(LocalDateTime.now());
        ordersMapper.updateById(order);

        log.info("用户取消订单成功：订单ID={}, 用户ID={}", id, userId);
        return true;
    }

    /**
     * 分页查询订单列表（管理员）
     */
    @Override
    public PageResult<Orders> page(Long page, Long size, String orderNo, Long userId, Integer status) {
        // 创建分页对象
        Page<Orders> pageObj = new Page<>(page, size);

        // 创建查询条件
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(orderNo), Orders::getOrderNo, orderNo);
        wrapper.eq(userId != null, Orders::getUserId, userId);
        wrapper.eq(status != null, Orders::getStatus, status);
        wrapper.orderByDesc(Orders::getCreateTime);

        // 执行分页查询
        Page<Orders> result = ordersMapper.selectPage(pageObj, wrapper);

        // 返回分页结果
        return PageResult.of(result);
    }

    /**
     * 审核订单（管理员）
     */
    @Override
    @Transactional
    public boolean audit(Long id, Integer status, String adminRemark) {
        // 查询订单信息
        Orders order = getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 只能审核待审核状态的订单
        if (order.getStatus() != 0) {
            throw new BusinessException("只能审核待审核状态的订单");
        }

        // 验证目标状态（只能变为已确认或已取消）
        if (status != 1 && status != 5) {
            throw new BusinessException("审核结果只能是已确认或已取消");
        }

        // 如果审核通过，检查车辆是否仍然可租
        if (status == 1) {
            Car car = carMapper.selectById(order.getCarId());
            if (car == null || car.getStatus() != 1) {
                throw new BusinessException("车辆当前不可租，无法确认订单");
            }
        }

        // 更新订单状态
        order.setStatus(status);
        order.setAdminRemark(adminRemark);
        order.setUpdateTime(LocalDateTime.now());
        ordersMapper.updateById(order);

        log.info("订单审核成功：订单ID={}, 状态={}, 备注={}", id, status, adminRemark);
        return true;
    }

    /**
     * 确认取车（管理员）
     */
    @Override
    @Transactional
    public boolean confirmPickup(Long id, String adminRemark) {
        // 查询订单信息
        Orders order = getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 只能确认已确认状态的订单
        if (order.getStatus() != 1) {
            throw new BusinessException("只能确认已确认状态的订单");
        }

        // 更新订单状态为已取车
        order.setStatus(2);
        order.setAdminRemark(adminRemark);
        order.setUpdateTime(LocalDateTime.now());
        ordersMapper.updateById(order);

        // 更新车辆状态为已租
        carService.updateStatus(order.getCarId(), 2);

        log.info("确认取车成功：订单ID={}, 车辆ID={}", id, order.getCarId());
        return true;
    }

    /**
     * 确认还车（管理员）
     */
    @Override
    @Transactional
    public boolean confirmReturn(Long id, String adminRemark) {
        // 查询订单信息
        Orders order = getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 只能确认已取车状态的订单
        if (order.getStatus() != 2) {
            throw new BusinessException("只能确认已取车状态的订单");
        }

        // 更新订单状态为已还车
        order.setStatus(3);
        order.setAdminRemark(adminRemark);
        order.setUpdateTime(LocalDateTime.now());
        ordersMapper.updateById(order);

        // 更新车辆状态为可租
        carService.updateStatus(order.getCarId(), 1);

        log.info("确认还车成功：订单ID={}, 车辆ID={}", id, order.getCarId());
        return true;
    }

    /**
     * 完成订单（管理员）
     */
    @Override
    @Transactional
    public boolean complete(Long id, String adminRemark) {
        // 查询订单信息
        Orders order = getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 只能完成已还车状态的订单
        if (order.getStatus() != 3) {
            throw new BusinessException("只能完成已还车状态的订单");
        }

        // 更新订单状态为已完成
        order.setStatus(4);
        order.setAdminRemark(adminRemark);
        order.setUpdateTime(LocalDateTime.now());
        ordersMapper.updateById(order);

        log.info("订单完成成功：订单ID={}", id);
        return true;
    }

    /**
     * 取消订单（管理员）
     */
    @Override
    @Transactional
    public boolean cancelByAdmin(Long id, String adminRemark) {
        // 查询订单信息
        Orders order = getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 已完成的订单不能取消
        if (order.getStatus() == 4) {
            throw new BusinessException("已完成的订单不能取消");
        }

        // 如果订单已取车，需要更新车辆状态为可租
        if (order.getStatus() == 2) {
            carService.updateStatus(order.getCarId(), 1);
        }

        // 更新订单状态为已取消
        order.setStatus(5);
        order.setAdminRemark(adminRemark);
        order.setUpdateTime(LocalDateTime.now());
        ordersMapper.updateById(order);

        log.info("管理员取消订单成功：订单ID={}", id);
        return true;
    }
}