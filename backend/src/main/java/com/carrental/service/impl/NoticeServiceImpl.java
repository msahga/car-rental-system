package com.carrental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carrental.entity.Notice;
import com.carrental.exception.BusinessException;
import com.carrental.mapper.NoticeMapper;
import com.carrental.result.PageResult;
import com.carrental.service.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告服务实现类
 * 
 * 功能说明：
 * 1. 实现公告管理所有功能
 * 2. 公开接口只返回已发布公告
 * 3. 管理员接口可查询所有公告
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
@Slf4j
@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    /**
     * 查询已发布公告列表（公开）
     */
    @Override
    public List<Notice> list() {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notice::getStatus, 1); // 只查询已发布状态
        wrapper.orderByDesc(Notice::getPublishTime);
        return noticeMapper.selectList(wrapper);
    }

    /**
     * 根据ID查询公告详情（公开）
     */
    @Override
    public Notice getById(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }
        // 公开接口只返回已发布公告
        if (notice.getStatus() != 1) {
            throw new BusinessException("公告未发布");
        }
        return notice;
    }

    /**
     * 分页查询公告列表（管理员）
     */
    @Override
    public PageResult<Notice> page(Long page, Long size, String title, Integer type, Integer status) {
        // 创建分页对象
        Page<Notice> pageObj = new Page<>(page, size);

        // 创建查询条件
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(title), Notice::getTitle, title);
        wrapper.eq(type != null, Notice::getType, type);
        wrapper.eq(status != null, Notice::getStatus, status);
        wrapper.orderByDesc(Notice::getCreateTime);

        // 执行分页查询
        Page<Notice> result = noticeMapper.selectPage(pageObj, wrapper);

        // 返回分页结果
        return PageResult.of(result);
    }

    /**
     * 添加公告（管理员）
     */
    @Override
    @Transactional
    public Notice add(Notice notice) {
        // 设置创建时间和更新时间
        notice.setCreateTime(LocalDateTime.now());
        notice.setUpdateTime(LocalDateTime.now());

        // 如果状态为已发布，设置发布时间和发布人
        if (notice.getStatus() == 1) {
            notice.setPublishTime(LocalDateTime.now());
        }

        // 插入数据库
        noticeMapper.insert(notice);

        log.info("公告添加成功：{}", notice.getTitle());
        return notice;
    }

    /**
     * 更新公告（管理员）
     */
    @Override
    @Transactional
    public Notice update(Notice notice) {
        // 检查公告是否存在
        Notice existNotice = noticeMapper.selectById(notice.getId());
        if (existNotice == null) {
            throw new BusinessException("公告不存在");
        }

        // 更新公告信息
        notice.setUpdateTime(LocalDateTime.now());
        noticeMapper.updateById(notice);

        log.info("公告更新成功：{}", notice.getTitle());
        return notice;
    }

    /**
     * 删除公告（管理员）
     */
    @Override
    @Transactional
    public boolean delete(Long id) {
        // 检查公告是否存在
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }

        // 逻辑删除
        noticeMapper.deleteById(id);

        log.info("公告删除成功：ID={}", id);
        return true;
    }

    /**
     * 发布公告（管理员）
     */
    @Override
    @Transactional
    public boolean publish(Long id) {
        // 检查公告是否存在
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }

        // 更新状态为已发布
        notice.setStatus(1);
        notice.setPublishTime(LocalDateTime.now());
        notice.setUpdateTime(LocalDateTime.now());
        noticeMapper.updateById(notice);

        log.info("公告发布成功：ID={}", id);
        return true;
    }

    /**
     * 下架公告（管理员）
     */
    @Override
    @Transactional
    public boolean unpublish(Long id) {
        // 检查公告是否存在
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }

        // 更新状态为已下架
        notice.setStatus(0);
        notice.setUpdateTime(LocalDateTime.now());
        noticeMapper.updateById(notice);

        log.info("公告下架成功：ID={}", id);
        return true;
    }
}