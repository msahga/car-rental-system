package com.carrental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carrental.entity.User;
import com.carrental.exception.BusinessException;
import com.carrental.mapper.UserMapper;
import com.carrental.result.PageResult;
import com.carrental.service.UserService;
import com.carrental.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 * 
 * 功能说明：
 * 1. 实现用户注册、登录、信息管理等功能
 * 2. 密码使用BCrypt加密存储
 * 3. 登录成功返回JWT令牌
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 用户注册
     */
    @Override
    @Transactional
    public User register(String username, String password, String phone) {
        // 检查用户名是否已存在
        User existUser = getByUsername(username);
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // 创建用户对象
        User user = new User();
        user.setUsername(username);
        // 密码加密存储
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setStatus(1); // 默认启用
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 插入数据库
        userMapper.insert(user);

        log.info("用户注册成功：{}", username);
        return user;
    }

    /**
     * 用户登录
     */
    @Override
    public String login(String username, String password) {
        // 查询用户信息
        User user = getByUsername(username);
        if (user == null) {
            throw new BusinessException("用户名不存在");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("用户已被禁用");
        }

        // 验证密码
        boolean passwordValid = false;
        
        // 首先尝试BCrypt验证
        if (passwordEncoder.matches(password, user.getPassword())) {
            passwordValid = true;
        } else {
            // 如果BCrypt验证失败，尝试明文密码比对（用于初始密码）
            if (password.equals(user.getPassword())) {
                passwordValid = true;
                // 自动更新为加密密码
                user.setPassword(passwordEncoder.encode(password));
                userMapper.updateById(user);
                log.info("用户密码已自动更新为加密格式：{}", username);
            }
        }
        
        if (!passwordValid) {
            throw new BusinessException("密码错误");
        }

        // 生成JWT令牌
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), "user");

        log.info("用户登录成功：{}", username);
        return token;
    }

    /**
     * 根据ID查询用户信息
     */
    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    /**
     * 根据用户名查询用户信息
     */
    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectOne(wrapper);
    }

    /**
     * 更新用户信息
     */
    @Override
    @Transactional
    public User update(User user) {
        // 检查用户是否存在
        User existUser = getById(user.getId());
        if (existUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 更新用户信息（不更新密码）
        user.setPassword(existUser.getPassword());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("用户信息更新成功：{}", user.getUsername());
        return user;
    }

    /**
     * 修改密码
     */
    @Override
    @Transactional
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        // 查询用户信息
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("用户密码修改成功：{}", user.getUsername());
        return true;
    }

    /**
     * 分页查询用户列表（管理员）
     */
    @Override
    public PageResult<User> page(Long page, Long size, String username, String phone, Integer status) {
        // 创建分页对象
        Page<User> pageObj = new Page<>(page, size);

        // 创建查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(username), User::getUsername, username);
        wrapper.like(StringUtils.hasText(phone), User::getPhone, phone);
        wrapper.eq(status != null, User::getStatus, status);
        wrapper.orderByDesc(User::getCreateTime);

        // 执行分页查询
        Page<User> result = userMapper.selectPage(pageObj, wrapper);

        // 返回分页结果
        return PageResult.of(result);
    }

    /**
     * 更新用户状态（管理员）
     */
    @Override
    @Transactional
    public boolean updateStatus(Long id, Integer status) {
        // 查询用户信息
        User user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 更新状态
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("用户状态更新成功：ID={}, Status={}", id, status);
        return true;
    }

    /**
     * 删除用户（管理员）
     */
    @Override
    @Transactional
    public boolean delete(Long id) {
        // 查询用户信息
        User user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 逻辑删除
        userMapper.deleteById(id);

        log.info("用户删除成功：ID={}", id);
        return true;
    }
}