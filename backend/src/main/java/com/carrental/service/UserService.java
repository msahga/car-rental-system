package com.carrental.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carrental.entity.User;
import com.carrental.result.PageResult;

/**
 * 用户服务接口
 * 
 * 功能说明：
 * 1. 用户注册
 * 2. 用户登录
 * 3. 用户信息查询
 * 4. 用户信息修改
 * 5. 用户列表查询（管理员）
 * 6. 用户状态管理（管理员）
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
public interface UserService {

    /**
     * 用户注册
     * 
     * @param username 用户名
     * @param password 密码
     * @param phone 手机号
     * @return 注册成功的用户信息
     */
    User register(String username, String password, String phone);

    /**
     * 用户登录
     * 
     * @param username 用户名
     * @param password 密码
     * @return JWT令牌
     */
    String login(String username, String password);

    /**
     * 根据ID查询用户信息
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    User getById(Long id);

    /**
     * 根据用户名查询用户信息
     * 
     * @param username 用户名
     * @return 用户信息
     */
    User getByUsername(String username);

    /**
     * 更新用户信息
     * 
     * @param user 用户信息
     * @return 更新后的用户信息
     */
    User update(User user);

    /**
     * 修改密码
     * 
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean updatePassword(Long userId, String oldPassword, String newPassword);

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
    PageResult<User> page(Long page, Long size, String username, String phone, Integer status);

    /**
     * 更新用户状态（管理员）
     * 
     * @param id 用户ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 删除用户（管理员）
     * 
     * @param id 用户ID
     * @return 是否成功
     */
    boolean delete(Long id);
}