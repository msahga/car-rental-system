package com.carrental.controller;

import com.carrental.entity.Admin;
import com.carrental.interceptor.UserContext;
import com.carrental.result.Result;
import com.carrental.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

/**
 * 管理员控制器
 * 
 * 功能说明：
 * 1. 管理员登录接口
 * 2. 管理员信息查询接口
 * 3. 管理员信息修改接口
 * 
 * API路径：
 * POST /admin/login - 管理员登录
 * GET /admin/info - 获取当前管理员信息
 * PUT /admin/info - 更新管理员信息
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@Valid
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 管理员登录
     * 
     * @param admin 管理员登录信息（包含username和password）
     * @return JWT令牌
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody Admin admin) {
        String username = admin.getUsername();
        String password = admin.getPassword();
        log.info("管理员登录请求：username={}", username);
        String token = adminService.login(username, password);
        return Result.success("登录成功", token);
    }

    /**
     * 获取当前管理员信息
     * 
     * @return 管理员信息
     */
    @GetMapping("/info")
    public Result<Admin> info() {
        Long adminId = UserContext.getUserId();
        log.info("获取管理员信息请求：adminId={}", adminId);
        Admin admin = adminService.getById(adminId);
        return Result.success(admin);
    }

    /**
     * 更新管理员信息
     * 
     * @param admin 管理员信息
     * @return 更新结果
     */
    @PutMapping("/info")
    public Result<Admin> updateInfo(@RequestBody Admin admin) {
        Long adminId = UserContext.getUserId();
        admin.setId(adminId);
        log.info("更新管理员信息请求：adminId={}", adminId);
        Admin updatedAdmin = adminService.update(admin);
        return Result.success("更新成功", updatedAdmin);
    }
}