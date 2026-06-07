package com.carrental.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carrental.entity.Car;
import com.carrental.result.PageResult;

import java.util.List;

/**
 * 车辆服务接口
 * 
 * 功能说明：
 * 1. 车辆列表查询（公开）
 * 2. 车辆详情查询（公开）
 * 3. 车辆分页查询（管理员）
 * 4. 车辆添加（管理员）
 * 5. 车辆更新（管理员）
 * 6. 车辆删除（管理员）
 * 7. 车辆状态管理（管理员）
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
public interface CarService {

    /**
     * 查询可租车辆列表（公开）
     * 支持多条件筛选：品牌、型号、网点、座位数、挡位、燃油类型、价格区间
     * 
     * @param brand 品牌（可选）
     * @param model 型号（可选）
     * @param storeId 网点ID（可选）
     * @param seats 座位数（可选）
     * @param gearbox 挡位类型（可选）
     * @param fuelType 燃油类型（可选）
     * @param minPrice 最低价格（可选）
     * @param maxPrice 最高价格（可选）
     * @return 车辆列表
     */
    List<Car> list(String brand, String model, Long storeId, Integer seats, Integer gearbox, Integer fuelType, String minPrice, String maxPrice);

    /**
     * 分页查询车辆列表（管理员）
     * 
     * @param page 页码
     * @param size 每页大小
     * @param brand 品牌（可选）
     * @param model 型号（可选）
     * @param storeId 网点ID（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    PageResult<Car> page(Long page, Long size, String brand, String model, Long storeId, Integer status);

    /**
     * 根据ID查询车辆详情
     * 
     * @param id 车辆ID
     * @return 车辆信息
     */
    Car getById(Long id);

    /**
     * 添加车辆（管理员）
     * 
     * @param car 车辆信息
     * @return 添加后的车辆信息
     */
    Car add(Car car);

    /**
     * 更新车辆（管理员）
     * 
     * @param car 车辆信息
     * @return 更新后的车辆信息
     */
    Car update(Car car);

    /**
     * 删除车辆（管理员）
     * 
     * @param id 车辆ID
     * @return 是否成功
     */
    boolean delete(Long id);

    /**
     * 更新车辆状态（管理员）
     * 
     * @param id 车辆ID
     * @param status 状态：1-可租，2-已租，3-维修，4-报废
     * @return 是否成功
     */
    boolean updateStatus(Long id, Integer status);
}