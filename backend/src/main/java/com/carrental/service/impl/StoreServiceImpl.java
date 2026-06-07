package com.carrental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carrental.entity.Store;
import com.carrental.exception.BusinessException;
import com.carrental.mapper.StoreMapper;
import com.carrental.result.PageResult;
import com.carrental.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 网点服务实现类
 * 
 * 功能说明：
 * 1. 实现网点管理所有功能
 * 2. 公开接口只返回正常状态的网点
 * 3. 管理员接口可查询所有网点
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
@Slf4j
@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    private StoreMapper storeMapper;

    /**
     * 查询所有正常状态的网点列表（公开）
     */
    @Override
    public List<Store> list() {
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Store::getStatus, 1); // 只查询正常状态
        wrapper.orderByDesc(Store::getCreateTime);
        return storeMapper.selectList(wrapper);
    }

    /**
     * 分页查询网点列表（管理员）
     */
    @Override
    public PageResult<Store> page(Long page, Long size, String name, Integer status) {
        // 创建分页对象
        Page<Store> pageObj = new Page<>(page, size);

        // 创建查询条件
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), Store::getName, name);
        wrapper.eq(status != null, Store::getStatus, status);
        wrapper.orderByDesc(Store::getCreateTime);

        // 执行分页查询
        Page<Store> result = storeMapper.selectPage(pageObj, wrapper);

        // 返回分页结果
        return PageResult.of(result);
    }

    /**
     * 根据ID查询网点详情
     */
    @Override
    public Store getById(Long id) {
        return storeMapper.selectById(id);
    }

    /**
     * 添加网点（管理员）
     */
    @Override
    @Transactional
    public Store add(Store store) {
        // 设置默认状态
        store.setStatus(1);
        store.setCreateTime(LocalDateTime.now());
        store.setUpdateTime(LocalDateTime.now());

        // 插入数据库
        storeMapper.insert(store);

        log.info("网点添加成功：{}", store.getName());
        return store;
    }

    /**
     * 更新网点（管理员）
     */
    @Override
    @Transactional
    public Store update(Store store) {
        // 检查网点是否存在
        Store existStore = getById(store.getId());
        if (existStore == null) {
            throw new BusinessException("网点不存在");
        }

        // 更新网点信息
        store.setUpdateTime(LocalDateTime.now());
        storeMapper.updateById(store);

        log.info("网点更新成功：{}", store.getName());
        return store;
    }

    /**
     * 删除网点（管理员）
     */
    @Override
    @Transactional
    public boolean delete(Long id) {
        // 检查网点是否存在
        Store store = getById(id);
        if (store == null) {
            throw new BusinessException("网点不存在");
        }

        // 逻辑删除
        storeMapper.deleteById(id);

        log.info("网点删除成功：ID={}", id);
        return true;
    }

    /**
     * 更新网点状态（管理员）
     */
    @Override
    @Transactional
    public boolean updateStatus(Long id, Integer status) {
        // 检查网点是否存在
        Store store = getById(id);
        if (store == null) {
            throw new BusinessException("网点不存在");
        }

        // 更新状态
        store.setStatus(status);
        store.setUpdateTime(LocalDateTime.now());
        storeMapper.updateById(store);

        log.info("网点状态更新成功：ID={}, Status={}", id, status);
        return true;
    }
}