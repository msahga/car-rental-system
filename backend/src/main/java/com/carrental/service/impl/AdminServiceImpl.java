package com.carrental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.carrental.entity.Admin;
import com.carrental.exception.BusinessException;
import com.carrental.mapper.AdminMapper;
import com.carrental.service.AdminService;
import com.carrental.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 管理员服务实现类
 * 
 * 功能说明：
 * 1. 实现管理员登录功能
 * 2. 密码使用BCrypt加密验证
 * 3. 登录成功返回JWT令牌
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 管理员登录
     */
    @Override
    public String login(String username, String password) {
        // 查询管理员信息
        Admin admin = getByUsername(username);
        if (admin == null) {
            throw new BusinessException("管理员账号不存在");
        }

        // 检查管理员状态
        if (admin.getStatus() == 0) {
            throw new BusinessException("管理员账号已被禁用");
        }

        // 验证密码
        boolean passwordValid = false;
        
        // 首先尝试BCrypt验证
        if (passwordEncoder.matches(password, admin.getPassword())) {
            passwordValid = true;
        } else {
            // 如果BCrypt验证失败，尝试明文密码比对（用于初始密码）
            if (password.equals(admin.getPassword())) {
                passwordValid = true;
                // 自动更新为加密密码
                admin.setPassword(passwordEncoder.encode(password));
                adminMapper.updateById(admin);
                log.info("管理员密码已自动更新为加密格式：{}", username);
            }
        }
        
        if (!passwordValid) {
            throw new BusinessException("密码错误");
        }

        // 生成JWT令牌
        String token = jwtUtil.generateToken(admin.getId(), admin.getUsername(), "admin");

        log.info("管理员登录成功：{}", username);
        return token;
    }

    /**
     * 根据ID查询管理员信息
     */
    @Override
    public Admin getById(Long id) {
        return adminMapper.selectById(id);
    }

    /**
     * 根据用户名查询管理员信息
     */
    @Override
    public Admin getByUsername(String username) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, username);
        return adminMapper.selectOne(wrapper);
    }

    /**
     * 更新管理员信息
     */
    @Override
    @Transactional
    public Admin update(Admin admin) {
        // 检查管理员是否存在
        Admin existAdmin = getById(admin.getId());
        if (existAdmin == null) {
            throw new BusinessException("管理员不存在");
        }

        // 更新管理员信息（不更新密码）
        admin.setPassword(existAdmin.getPassword());
        admin.setUpdateTime(LocalDateTime.now());
        adminMapper.updateById(admin);

        log.info("管理员信息更新成功：{}", admin.getUsername());
        return admin;
    }
}