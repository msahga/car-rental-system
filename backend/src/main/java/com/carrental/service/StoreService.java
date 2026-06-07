package com.carrental.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carrental.entity.Store;
import com.carrental.result.PageResult;

import java.util.List;

/**
 * 网点服务接口
 * 
 * 功能说明：
 * 1. 网点列表查询（公开）
 * 2. 网点分页查询（管理员）
 * 3. 网点详情查询
 * 4. 网点添加（管理员）
 * 5. 网点更新（管理员）
 * 6. 网点删除（管理员）
 * 7. 网点状态管理（管理员）
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
public interface StoreService {

    /**
     * 查询所有正常状态的网点列表（公开）
     * 
     * @return 网点列表
     */
    List<Store> list();

    /**
     * 分页查询网点列表（管理员）
     * 
     * @param page 页码
     * @param size 每页大小
     * @param name 网点名称（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    PageResult<Store> page(Long page, Long size, String name, Integer status);

    /**
     * 根据ID查询网点详情
     * 
     * @param id 网点ID
     * @return 网点信息
     */
    Store getById(Long id);

    /**
     * 添加网点（管理员）
     * 
     * @param store 网点信息
     * @return 添加后的网点信息
     */
    Store add(Store store);

    /**
     * 更新网点（管理员）
     * 
     * @param store 网点信息
     * @return 更新后的网点信息
     */
    Store update(Store store);

    /**
     * 删除网点（管理员）
     * 
     * @param id 网点ID
     * @return 是否成功
     */
    boolean delete(Long id);

    /**
     * 更新网点状态（管理员）
     * 
     * @param id 网点ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Long id, Integer status);
}