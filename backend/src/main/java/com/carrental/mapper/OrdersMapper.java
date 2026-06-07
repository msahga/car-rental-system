package com.carrental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carrental.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单Mapper接口
 * 
 * 功能说明：
 * 1. 继承MyBatis-Plus的BaseMapper
 * 2. 提供基本的CRUD操作
 * 3. 可扩展自定义SQL方法
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

    // BaseMapper已提供基本CRUD方法
}