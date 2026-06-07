package com.carrental.controller;

import com.carrental.entity.User;
import com.carrental.interceptor.UserContext;
import com.carrental.result.PageResult;
import com.carrental.result.Result;
import com.carrental.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

/**
 * 用户控制器
 * 
 * 功能说明：
 * 1. 用户注册接口
 * 2. 用户登录接口
 * 3. 用户信息查询接口
 * 4. 用户信息修改接口
 * 5. 用户密码修改接口
 * 6. 用户列表查询接口（管理员）
 * 7. 用户状态管理接口（管理员）
 * 8. 用户删除接口（管理员）
 * 
 * API路径：
 * POST /user/register - 用户注册
 * POST /user/login - 用户登录
 * GET /user/info - 获取当前用户信息
 * PUT /user/info - 更新用户信息
 * PUT /user/password - 修改密码
 * GET /user/page - 分页查询用户列表（管理员）
 * PUT /user/status/{id} - 更新用户状态（管理员）
 * DELETE /user/{id} - 删除用户（管理员）
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Valid
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     * 
     * @param username 用户名
     * @param password 密码
     * @param phone 手机号
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<User> register(
            @RequestParam @NotBlank(message = "用户名不能为空") String username,
            @RequestParam @NotBlank(message = "密码不能为空") String password,
            @RequestParam(required = false) String phone) {
        log.info("用户注册请求：username={}, phone={}", username, phone);
        User user = userService.register(username, password, phone);
        return Result.success("注册成功", user);
    }

    /**
     * 用户登录
     * 
     * @param user 用户登录信息（包含username和password）
     * @return JWT令牌
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        log.info("用户登录请求：username={}", username);
        String token = userService.login(username, password);
        return Result.success("登录成功", token);
    }

    /**
     * 获取当前用户信息
     * 
     * @return 用户信息
     */
    @GetMapping("/info")
    public Result<User> info() {
        Long userId = UserContext.getUserId();
        log.info("获取用户信息请求：userId={}", userId);
        User user = userService.getById(userId);
        return Result.success(user);
    }

    /**
     * 更新用户信息
     * 
     * @param user 用户信息
     * @return 更新结果
     */
    @PutMapping("/info")
    public Result<User> updateInfo(@RequestBody User user) {
        Long userId = UserContext.getUserId();
        user.setId(userId);
        log.info("更新用户信息请求：userId={}", userId);
        User updatedUser = userService.update(user);
        return Result.success("更新成功", updatedUser);
    }

    /**
     * 修改密码
     * 
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    @PutMapping("/password")
    public Result<Boolean> updatePassword(
            @RequestParam @NotBlank(message = "旧密码不能为空") String oldPassword,
            @RequestParam @NotBlank(message = "新密码不能为空") String newPassword) {
        Long userId = UserContext.getUserId();
        log.info("修改密码请求：userId={}", userId);
        boolean result = userService.updatePassword(userId, oldPassword, newPassword);
        return Result.success("密码修改成功", result);
    }

    /**
     * 分页查询用户列表（管理员）
     * 
     * @param page 页码
     * @param size 每页大小
     * @param username 用户名（可选）
     * @param phone 手机号（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<PageResult<User>> page(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer status) {
        // 验证管理员权限
        if (!UserContext.isAdmin()) {
            return Result.forbidden();
        }
        log.info("分页查询用户列表请求：page={}, size={}, username={}, phone={}, status={}", 
                page, size, username, phone, status);
        PageResult<User> result = userService.page(page, size, username, phone, status);
        return Result.success(result);
    }

    /**
     * 更新用户状态（管理员）
     * 
     * @param id 用户ID
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
        log.info("更新用户状态请求：id={}, status={}", id, status);
        boolean result = userService.updateStatus(id, status);
        return Result.success("状态更新成功", result);
    }

    /**
     * 删除用户（管理员）
     * 
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        // 验证管理员权限
        if (!UserContext.isAdmin()) {
            return Result.forbidden();
        }
        log.info("删除用户请求：id={}", id);
        boolean result = userService.delete(id);
        return Result.success("删除成功", result);
    }
}