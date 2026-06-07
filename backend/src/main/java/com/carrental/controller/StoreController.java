package com.carrental.controller;

import com.carrental.entity.Store;
import com.carrental.interceptor.UserContext;
import com.carrental.result.PageResult;
import com.carrental.result.Result;
import com.carrental.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 网点控制器
 * 
 * 功能说明：
 * 1. 网点列表查询接口（公开）
 * 2. 网点详情查询接口（公开）
 * 3. 网点分页查询接口（管理员）
 * 4. 网点添加接口（管理员）
 * 5. 网点更新接口（管理员）
 * 6. 网点删除接口（管理员）
 * 7. 网点状态管理接口（管理员）
 * 
 * API路径：
 * GET /store/list - 查询网点列表（公开）
 * GET /store/{id} - 查询网点详情（公开）
 * GET /store/page - 分页查询网点列表（管理员）
 * POST /store - 添加网点（管理员）
 * PUT /store - 更新网点（管理员）
 * DELETE /store/{id} - 删除网点（管理员）
 * PUT /store/status/{id} - 更新网点状态（管理员）
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/store")
@Valid
public class StoreController {

    @Autowired
    private StoreService storeService;

    /**
     * 查询网点列表（公开）
     * 
     * @return 网点列表
     */
    @GetMapping("/list")
    public Result<List<Store>> list() {
        log.info("查询网点列表请求");
        List<Store> stores = storeService.list();
        return Result.success(stores);
    }

    /**
     * 查询网点详情（公开）
     * 
     * @param id 网点ID
     * @return 网点信息
     */
    @GetMapping("/{id}")
    public Result<Store> getById(@PathVariable Long id) {
        log.info("查询网点详情请求：id={}", id);
        Store store = storeService.getById(id);
        return Result.success(store);
    }

    /**
     * 分页查询网点列表（管理员）
     * 
     * @param page 页码
     * @param size 每页大小
     * @param name 网点名称（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<PageResult<Store>> page(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {
        // 验证管理员权限
        if (!UserContext.isAdmin()) {
            return Result.forbidden();
        }
        log.info("分页查询网点列表请求：page={}, size={}, name={}, status={}", 
                page, size, name, status);
        PageResult<Store> result = storeService.page(page, size, name, status);
        return Result.success(result);
    }

    /**
     * 添加网点（管理员）
     * 
     * @param store 网点信息
     * @return 添加结果
     */
    @PostMapping
    public Result<Store> add(@RequestBody @Valid Store store) {
        // 验证管理员权限
        if (!UserContext.isAdmin()) {
            return Result.forbidden();
        }
        log.info("添加网点请求：name={}", store.getName());
        Store addedStore = storeService.add(store);
        return Result.success("添加成功", addedStore);
    }

    /**
     * 更新网点（管理员）
     * 
     * @param store 网点信息
     * @return 更新结果
     */
    @PutMapping
    public Result<Store> update(@RequestBody @Valid Store store) {
        // 验证管理员权限
        if (!UserContext.isAdmin()) {
            return Result.forbidden();
        }
        log.info("更新网点请求：id={}", store.getId());
        Store updatedStore = storeService.update(store);
        return Result.success("更新成功", updatedStore);
    }

    /**
     * 删除网点（管理员）
     * 
     * @param id 网点ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        // 验证管理员权限
        if (!UserContext.isAdmin()) {
            return Result.forbidden();
        }
        log.info("删除网点请求：id={}", id);
        boolean result = storeService.delete(id);
        return Result.success("删除成功", result);
    }

    /**
     * 更新网点状态（管理员）
     * 
     * @param id 网点ID
     * @param status 状态
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
        log.info("更新网点状态请求：id={}, status={}", id, status);
        boolean result = storeService.updateStatus(id, status);
        return Result.success("状态更新成功", result);
    }
}