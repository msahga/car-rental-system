package com.carrental.service;

import com.carrental.entity.Admin;

/**
 * 管理员服务接口
 * 
 * 功能说明：
 * 1. 管理员登录
 * 2. 管理员信息查询
 * 3. 管理员信息修改
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
public interface AdminService {

    /**
     * 管理员登录
     * 
     * @param username 用户名
     * @param password 密码
     * @return JWT令牌
     */
    String login(String username, String password);

    /**
     * 根据ID查询管理员信息
     * 
     * @param id 管理员ID
     * @return 管理员信息
     */
    Admin getById(Long id);

    /**
     * 根据用户名查询管理员信息
     * 
     * @param username 用户名
     * @return 管理员信息
     */
    Admin getByUsername(String username);

    /**
     * 更新管理员信息
     * 
     * @param admin 管理员信息
     * @return 更新后的管理员信息
     */
    Admin update(Admin admin);
}