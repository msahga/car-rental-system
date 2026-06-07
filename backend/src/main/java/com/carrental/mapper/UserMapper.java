package com.carrental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carrental.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 * 
 * 功能说明：
 * 1. 继承MyBatis-Plus的BaseMapper
 * 2. 提供基本的CRUD操作
 * 3. 可扩展自定义SQL方法
 * 
 * BaseMapper提供的方法：
 * - insert(entity)：插入
 * - deleteById(id)：根据ID删除
 * - updateById(entity)：根据ID更新
 * - selectById(id)：根据ID查询
 * - selectList(wrapper)：条件查询列表
 * - selectPage(page, wrapper)：分页查询
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    // BaseMapper已提供基本CRUD方法
    // 如需自定义SQL，可在此添加方法并在mapper/UserMapper.xml中实现
}