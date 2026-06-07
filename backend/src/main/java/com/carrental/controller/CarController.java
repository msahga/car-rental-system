package com.carrental.controller;

import com.carrental.entity.Car;
import com.carrental.interceptor.UserContext;
import com.carrental.result.PageResult;
import com.carrental.result.Result;
import com.carrental.service.CarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 车辆控制器
 * 
 * 功能说明：
 * 1. 车辆列表查询接口（公开，支持多条件筛选）
 * 2. 车辆详情查询接口（公开）
 * 3. 车辆分页查询接口（管理员）
 * 4. 车辆添加接口（管理员）
 * 5. 车辆更新接口（管理员）
 * 6. 车辆删除接口（管理员）
 * 7. 车辆状态管理接口（管理员）
 * 
 * API路径：
 * GET /car/list - 查询车辆列表（公开）
 * GET /car/{id} - 查询车辆详情（公开）
 * GET /car/page - 分页查询车辆列表（管理员）
 * POST /car - 添加车辆（管理员）
 * PUT /car - 更新车辆（管理员）
 * DELETE /car/{id} - 删除车辆（管理员）
 * PUT /car/status/{id} - 更新车辆状态（管理员）
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/car")
@Valid
public class CarController {

    @Autowired
    private CarService carService;

    /**
     * 查询车辆列表（公开）
     * 支持多条件筛选
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
    @GetMapping("/list")
    public Result<List<Car>> list(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) Integer seats,
            @RequestParam(required = false) Integer gearbox,
            @RequestParam(required = false) Integer fuelType,
            @RequestParam(required = false) String minPrice,
            @RequestParam(required = false) String maxPrice) {
        log.info("查询车辆列表请求：brand={}, model={}, storeId={}, seats={}, gearbox={}, fuelType={}, minPrice={}, maxPrice={}", 
                brand, model, storeId, seats, gearbox, fuelType, minPrice, maxPrice);
        List<Car> cars = carService.list(brand, model, storeId, seats, gearbox, fuelType, minPrice, maxPrice);
        return Result.success(cars);
    }

    /**
     * 查询车辆详情（公开）
     * 
     * @param id 车辆ID
     * @return 车辆信息
     */
    @GetMapping("/{id}")
    public Result<Car> getById(@PathVariable Long id) {
        log.info("查询车辆详情请求：id={}", id);
        Car car = carService.getById(id);
        return Result.success(car);
    }

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
    @GetMapping("/page")
    public Result<PageResult<Car>> page(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) Integer status) {
        // 验证管理员权限
        if (!UserContext.isAdmin()) {
            return Result.forbidden();
        }
        log.info("分页查询车辆列表请求：page={}, size={}, brand={}, model={}, storeId={}, status={}", 
                page, size, brand, model, storeId, status);
        PageResult<Car> result = carService.page(page, size, brand, model, storeId, status);
        return Result.success(result);
    }

    /**
     * 添加车辆（管理员）
     * 
     * @param car 车辆信息
     * @return 添加结果
     */
    @PostMapping
    public Result<Car> add(@RequestBody @Valid Car car) {
        // 验证管理员权限
        if (!UserContext.isAdmin()) {
            return Result.forbidden();
        }
        log.info("添加车辆请求：carNumber={}", car.getCarNumber());
        Car addedCar = carService.add(car);
        return Result.success("添加成功", addedCar);
    }

    /**
     * 更新车辆（管理员）
     * 
     * @param car 车辆信息
     * @return 更新结果
     */
    @PutMapping
    public Result<Car> update(@RequestBody @Valid Car car) {
        // 验证管理员权限
        if (!UserContext.isAdmin()) {
            return Result.forbidden();
        }
        log.info("更新车辆请求：id={}", car.getId());
        Car updatedCar = carService.update(car);
        return Result.success("更新成功", updatedCar);
    }

    /**
     * 删除车辆（管理员）
     * 
     * @param id 车辆ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        // 验证管理员权限
        if (!UserContext.isAdmin()) {
            return Result.forbidden();
        }
        log.info("删除车辆请求：id={}", id);
        boolean result = carService.delete(id);
        return Result.success("删除成功", result);
    }

    /**
     * 更新车辆状态（管理员）
     * 
     * @param id 车辆ID
     * @param status 状态：1-可租，2-已租，3-维修，4-报废
     * @return 更新结果
     */
    @PutMapping("/status/{id}")
    public Result<Boolean> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        // 验证管理员权限
        if (!UserContext.isAdmin()) {
            return Result.forbidden();
        }
        log.info("更新车辆状态请求：id={}, status={}", id, status);
        boolean result = carService.updateStatus(id, status);
        return Result.success("状态更新成功", result);
    }
}