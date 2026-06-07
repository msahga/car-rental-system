package com.carrental.controller;

import com.carrental.entity.Notice;
import com.carrental.interceptor.UserContext;
import com.carrental.result.PageResult;
import com.carrental.result.Result;
import com.carrental.service.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 公告控制器
 * 
 * 功能说明：
 * 1. 公告列表查询接口（公开）
 * 2. 公告详情查询接口（公开）
 * 3. 公告分页查询接口（管理员）
 * 4. 公告添加接口（管理员）
 * 5. 公告更新接口（管理员）
 * 6. 公告删除接口（管理员）
 * 7. 公告发布接口（管理员）
 * 8. 公告下架接口（管理员）
 * 
 * API路径：
 * GET /notice/list - 查询公告列表（公开）
 * GET /notice/{id} - 查询公告详情（公开）
 * GET /notice/page - 分页查询公告列表（管理员）
 * POST /notice - 添加公告（管理员）
 * PUT /notice - 更新公告（管理员）
 * DELETE /notice/{id} - 删除公告（管理员）
 * PUT /notice/publish/{id} - 发布公告（管理员）
 * PUT /notice/unpublish/{id} - 下架公告（管理员）
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/notice")
@Valid
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 查询公告列表（公开）
     * 
     * @return 公告列表
     */
    @GetMapping("/list")
    public Result<List<Notice>> list() {
        log.info("查询公告列表请求");
        List<Notice> notices = noticeService.list();
        return Result.success(notices);
    }

    /**
     * 查询公告详情（公开）
     * 
     * @param id 公告ID
     * @return 公告信息
     */
    @GetMapping("/{id}")
    public Result<Notice> getById(@PathVariable Long id) {
        log.info("查询公告详情请求：id={}", id);
        Notice notice = noticeService.getById(id);
        return Result.success(notice);
    }

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
    @GetMapping("/page")
    public Result<PageResult<Notice>> page(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        // 验证管理员权限
        if (!UserContext.isAdmin()) {
            return Result.forbidden();
        }
        log.info("分页查询公告列表请求：page={}, size={}, title={}, type={}, status={}", 
                page, size, title, type, status);
        PageResult<Notice> result = noticeService.page(page, size, title, type, status);
        return Result.success(result);
    }

    /**
     * 添加公告（管理员）
     * 
     * @param notice 公告信息
     * @return 添加结果
     */
    @PostMapping
    public Result<Notice> add(@RequestBody @Valid Notice notice) {
        // 验证管理员权限
        if (!UserContext.isAdmin()) {
            return Result.forbidden();
        }
        log.info("添加公告请求：title={}", notice.getTitle());
        Notice addedNotice = noticeService.add(notice);
        return Result.success("添加成功", addedNotice);
    }

    /**
     * 更新公告（管理员）
     * 
     * @param notice 公告信息
     * @return 更新结果
     */
    @PutMapping
    public Result<Notice> update(@RequestBody @Valid Notice notice) {
        // 验证管理员权限
        if (!UserContext.isAdmin()) {
            return Result.forbidden();
        }
        log.info("更新公告请求：id={}", notice.getId());
        Notice updatedNotice = noticeService.update(notice);
        return Result.success("更新成功", updatedNotice);
    }

    /**
     * 删除公告（管理员）
     * 
     * @param id 公告ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        // 验证管理员权限
        if (!UserContext.isAdmin()) {
            return Result.forbidden();
        }
        log.info("删除公告请求：id={}", id);
        boolean result = noticeService.delete(id);
        return Result.success("删除成功", result);
    }

    /**
     * 发布公告（管理员）
     * 
     * @param id 公告ID
     * @return 发布结果
     */
    @PutMapping("/publish/{id}")
    public Result<Boolean> publish(@PathVariable Long id) {
        // 验证管理员权限
        if (!UserContext.isAdmin()) {
            return Result.forbidden();
        }
        log.info("发布公告请求：id={}", id);
        boolean result = noticeService.publish(id);
        return Result.success("发布成功", result);
    }

    /**
     * 下架公告（管理员）
     * 
     * @param id 公告ID
     * @return 下架结果
     */
    @PutMapping("/unpublish/{id}")
    public Result<Boolean> unpublish(@PathVariable Long id) {
        // 验证管理员权限
        if (!UserContext.isAdmin()) {
            return Result.forbidden();
        }
        log.info("下架公告请求：id={}", id);
        boolean result = noticeService.unpublish(id);
        return Result.success("下架成功", result);
    }
}