package com.carrental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公告实体类
 * 
 * 对应数据库表：notice
 * 功能：系统公告信息管理
 * 
 * 字段说明：
 * id - 公告ID，主键自增
 * title - 公告标题
 * content - 公告内容
 * type - 类型：1-普通公告，2-重要公告
 * status - 状态：0-已下架，1-已发布
 * publisher - 发布人
 * publishTime - 发布时间
 * createTime - 创建时间
 * updateTime - 更新时间
 * deleted - 逻辑删除：0-未删除，1-已删除
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
@Data
@TableName("notice")
public class Notice implements Serializable {

    /** 公告ID，主键自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 公告标题 */
    private String title;

    /** 公告内容 */
    private String content;

    /** 类型：1-普通公告，2-重要公告 */
    private Integer type;

    /** 状态：0-已下架，1-已发布 */
    private Integer status;

    /** 发布人 */
    private String publisher;

    /** 发布时间 */
    private LocalDateTime publishTime;

    /** 创建时间，自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间，自动填充 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除：0-未删除，1-已删除 */
    @TableLogic
    private Integer deleted;
}