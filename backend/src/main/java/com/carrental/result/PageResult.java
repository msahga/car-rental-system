package com.carrental.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果封装类
 * 
 * 功能说明：
 * 1. 封装分页查询结果
 * 2. 包含数据列表、总数、当前页、每页大小
 * 3. 与前端Element Plus分页组件配合使用
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
@Data
public class PageResult<T> implements Serializable {

    /** 数据列表 */
    private List<T> records;

    /** 总记录数 */
    private Long total;

    /** 当前页码 */
    private Long current;

    /** 每页大小 */
    private Long size;

    /** 总页数 */
    private Long pages;

    /**
     * 默认构造方法
     */
    public PageResult() {
    }

    /**
     * 全参数构造方法
     */
    public PageResult(List<T> records, Long total, Long current, Long size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
        this.pages = (total + size - 1) / size;
    }

    /**
     * 从MyBatis-Plus的IPage转换
     */
    public static <T> PageResult<T> of(com.baomidou.mybatisplus.core.metadata.IPage<T> page) {
        return new PageResult<>(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }
}