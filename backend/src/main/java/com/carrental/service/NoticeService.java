package com.carrental.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carrental.entity.Notice;
import com.carrental.result.PageResult;

import java.util.List;

/**
 * 公告服务接口
 * 
 * 功能说明：
 * 1. 公告列表查询（公开）
 * 2. 公告详情查询（公开）
 * 3. 公告分页查询（管理员）
 * 4. 公告添加（管理员）
 * 5. 公告更新（管理员）
 * 6. 公告删除（管理员）
 * 7. 公告发布/下架（管理员）
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
public interface NoticeService {

    /**
     * 查询已发布公告列表（公开）
     * 
     * @return 公告列表
     */
    List<Notice> list();

    /**
     * 根据ID查询公告详情（公开）
     * 
     * @param id 公告ID
     * @return 公告信息
     */
    Notice getById(Long id);

    /**
     * 分页查询公告列表（管理员）
     * 
     * @param page 页码
     * @param size 每页大小
     * @param title 标题（可选）
     * @param type 类型（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    PageResult<Notice> page(Long page, Long size, String title, Integer type, Integer status);

    /**
     * 添加公告（管理员）
     * 
     * @param notice 公告信息
     * @return 添加后的公告信息
     */
    Notice add(Notice notice);

    /**
     * 更新公告（管理员）
     * 
     * @param notice 公告信息
     * @return 更新后的公告信息
     */
    Notice update(Notice notice);

    /**
     * 删除公告（管理员）
     * 
     * @param id 公告ID
     * @return 是否成功
     */
    boolean delete(Long id);

    /**
     * 发布公告（管理员）
     * 
     * @param id 公告ID
     * @return 是否成功
     */
    boolean publish(Long id);

    /**
     * 下架公告（管理员）
     * 
     * @param id 公告ID
     * @return 是否成功
     */
    boolean unpublish(Long id);
}