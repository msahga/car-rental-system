package com.carrental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carrental.entity.Car;
import com.carrental.exception.BusinessException;
import com.carrental.mapper.CarMapper;
import com.carrental.result.PageResult;
import com.carrental.service.CarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 车辆服务实现类
 * 
 * 功能说明：
 * 1. 实现车辆管理所有功能
 * 2. 公开接口只返回可租状态的车辆
 * 3. 管理员接口可查询所有车辆
 * 4. 支持多条件筛选查询
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
@Slf4j
@Service
public class CarServiceImpl implements CarService {

    @Autowired
    private CarMapper carMapper;

    /**
     * 查询可租车辆列表（公开）
     */
    @Override
    public List<Car> list(String brand, String model, Long storeId, Integer seats, Integer gearbox, Integer fuelType, String minPrice, String maxPrice) {
        // 创建查询条件
        LambdaQueryWrapper<Car> wrapper = new LambdaQueryWrapper<>();
        
        // 只查询可租状态的车辆
        wrapper.eq(Car::getStatus, 1);
        
        // 多条件筛选
        wrapper.like(StringUtils.hasText(brand), Car::getBrand, brand);
        wrapper.like(StringUtils.hasText(model), Car::getModel, model);
        wrapper.eq(storeId != null, Car::getStoreId, storeId);
        wrapper.eq(seats != null, Car::getSeats, seats);
        wrapper.eq(gearbox != null, Car::getGearbox, gearbox);
        wrapper.eq(fuelType != null, Car::getFuelType, fuelType);
        
        // 价格区间筛选
        if (StringUtils.hasText(minPrice)) {
            wrapper.ge(Car::getDailyPrice, new BigDecimal(minPrice));
        }
        if (StringUtils.hasText(maxPrice)) {
            wrapper.le(Car::getDailyPrice, new BigDecimal(maxPrice));
        }
        
        // 按创建时间降序排列
        wrapper.orderByDesc(Car::getCreateTime);
        
        return carMapper.selectList(wrapper);
    }

    /**
     * 分页查询车辆列表（管理员）
     */
    @Override
    public PageResult<Car> page(Long page, Long size, String brand, String model, Long storeId, Integer status) {
        // 创建分页对象
        Page<Car> pageObj = new Page<>(page, size);

        // 创建查询条件
        LambdaQueryWrapper<Car> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(brand), Car::getBrand, brand);
        wrapper.like(StringUtils.hasText(model), Car::getModel, model);
        wrapper.eq(storeId != null, Car::getStoreId, storeId);
        wrapper.eq(status != null, Car::getStatus, status);
        wrapper.orderByDesc(Car::getCreateTime);

        // 执行分页查询
        Page<Car> result = carMapper.selectPage(pageObj, wrapper);

        // 返回分页结果
        return PageResult.of(result);
    }

    /**
     * 根据ID查询车辆详情
     */
    @Override
    public Car getById(Long id) {
        return carMapper.selectById(id);
    }

    /**
     * 添加车辆（管理员）
     */
    @Override
    @Transactional
    public Car add(Car car) {
        // 检查车牌号是否已存在
        LambdaQueryWrapper<Car> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Car::getCarNumber, car.getCarNumber());
        Car existCar = carMapper.selectOne(wrapper);
        if (existCar != null) {
            throw new BusinessException("车牌号已存在");
        }

        // 设置默认状态（可租）
        car.setStatus(1);
        car.setCreateTime(LocalDateTime.now());
        car.setUpdateTime(LocalDateTime.now());

        // 插入数据库
        carMapper.insert(car);

        log.info("车辆添加成功：{}", car.getCarNumber());
        return car;
    }

    /**
     * 更新车辆（管理员）
     */
    @Override
    @Transactional
    public Car update(Car car) {
        // 检查车辆是否存在
        Car existCar = getById(car.getId());
        if (existCar == null) {
            throw new BusinessException("车辆不存在");
        }

        // 检查车牌号是否与其他车辆重复
        if (!existCar.getCarNumber().equals(car.getCarNumber())) {
            LambdaQueryWrapper<Car> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Car::getCarNumber, car.getCarNumber());
            wrapper.ne(Car::getId, car.getId());
            Car otherCar = carMapper.selectOne(wrapper);
            if (otherCar != null) {
                throw new BusinessException("车牌号已存在");
            }
        }

        // 更新车辆信息
        car.setUpdateTime(LocalDateTime.now());
        carMapper.updateById(car);

        log.info("车辆更新成功：{}", car.getCarNumber());
        return car;
    }

    /**
     * 删除车辆（管理员）
     */
    @Override
    @Transactional
    public boolean delete(Long id) {
        // 检查车辆是否存在
        Car car = getById(id);
        if (car == null) {
            throw new BusinessException("车辆不存在");
        }

        // 检查车辆状态（已租车辆不能删除）
        if (car.getStatus() == 2) {
            throw new BusinessException("已租车辆不能删除");
        }

        // 逻辑删除
        carMapper.deleteById(id);

        log.info("车辆删除成功：ID={}", id);
        return true;
    }

    /**
     * 更新车辆状态（管理员）
     */
    @Override
    @Transactional
    public boolean updateStatus(Long id, Integer status) {
        // 检查车辆是否存在
        Car car = getById(id);
        if (car == null) {
            throw new BusinessException("车辆不存在");
        }

        // 检查状态变更是否合法
        // 已租车辆只能变为已租或维修状态
        if (car.getStatus() == 2 && status != 2 && status != 3) {
            throw new BusinessException("已租车辆只能变为维修状态");
        }

        // 更新状态
        car.setStatus(status);
        car.setUpdateTime(LocalDateTime.now());
        carMapper.updateById(car);

        log.info("车辆状态更新成功：ID={}, Status={}", id, status);
        return true;
    }
}