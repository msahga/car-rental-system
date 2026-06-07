-- =====================================================
-- 汽车租赁管理系统数据库设计
-- 数据库名：car_rental
-- 字符集：utf8mb4
-- 包含6张核心表：user、admin、store、car、orders、notice
-- 作者：毕业设计项目
-- 日期：2024
-- =====================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS car_rental 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;

USE car_rental;

-- =====================================================
-- 1. 用户表（user）- 普通用户信息表
-- =====================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键自增',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名，登录账号',
  `password` VARCHAR(100) NOT NULL COMMENT '密码，加密存储',
  `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号码',
  `email` VARCHAR(50) DEFAULT NULL COMMENT '电子邮箱',
  `id_card` VARCHAR(20) DEFAULT NULL COMMENT '身份证号码',
  `avatar` VARCHAR(200) DEFAULT NULL COMMENT '头像图片URL',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_phone` (`phone`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表-普通用户信息';

-- =====================================================
-- 2. 管理员表（admin）- 系统管理员信息表
-- =====================================================
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '管理员ID，主键自增',
  `username` VARCHAR(50) NOT NULL COMMENT '管理员账号',
  `password` VARCHAR(100) NOT NULL COMMENT '密码，加密存储',
  `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号码',
  `avatar` VARCHAR(200) DEFAULT NULL COMMENT '头像图片URL',
  `role` TINYINT NOT NULL DEFAULT 1 COMMENT '角色：1-超级管理员，2-普通管理员',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表-系统管理员信息';

-- =====================================================
-- 3. 网点表（store）- 租赁网点信息表
-- =====================================================
DROP TABLE IF EXISTS `store`;
CREATE TABLE `store` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '网点ID，主键自增',
  `name` VARCHAR(100) NOT NULL COMMENT '网点名称',
  `address` VARCHAR(200) NOT NULL COMMENT '网点地址',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `manager` VARCHAR(50) DEFAULT NULL COMMENT '网点负责人',
  `latitude` DECIMAL(10,6) DEFAULT NULL COMMENT '纬度坐标',
  `longitude` DECIMAL(10,6) DEFAULT NULL COMMENT '经度坐标',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-停用，1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网点表-租赁网点信息';

-- =====================================================
-- 4. 车辆表（car）- 车辆信息表
-- =====================================================
DROP TABLE IF EXISTS `car`;
CREATE TABLE `car` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '车辆ID，主键自增',
  `brand` VARCHAR(50) NOT NULL COMMENT '品牌',
  `model` VARCHAR(50) NOT NULL COMMENT '型号',
  `car_number` VARCHAR(20) NOT NULL COMMENT '车牌号',
  `color` VARCHAR(20) DEFAULT NULL COMMENT '颜色',
  `seats` INT NOT NULL DEFAULT 5 COMMENT '座位数',
  `gearbox` TINYINT NOT NULL DEFAULT 1 COMMENT '挡位类型：1-自动挡，2-手动挡',
  `fuel_type` TINYINT NOT NULL DEFAULT 1 COMMENT '燃油类型：1-汽油，2-柴油，3-电动，4-混动',
  `daily_price` DECIMAL(10,2) NOT NULL COMMENT '日租金（元）',
  `image` VARCHAR(200) DEFAULT NULL COMMENT '车辆图片URL',
  `store_id` BIGINT NOT NULL COMMENT '所属网点ID',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-可租，2-已租，3-维修，4-报废',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '车辆描述',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_car_number` (`car_number`),
  KEY `idx_brand` (`brand`),
  KEY `idx_store_id` (`store_id`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`),
  KEY `idx_daily_price` (`daily_price`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆表-车辆信息';

-- =====================================================
-- 5. 订单表（orders）- 租车订单信息表
-- =====================================================
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID，主键自增',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单编号',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `car_id` BIGINT NOT NULL COMMENT '车辆ID',
  `pickup_store_id` BIGINT NOT NULL COMMENT '取车网点ID',
  `return_store_id` BIGINT NOT NULL COMMENT '还车网点ID',
  `pickup_time` DATETIME NOT NULL COMMENT '取车时间',
  `return_time` DATETIME NOT NULL COMMENT '还车时间',
  `rent_days` INT NOT NULL COMMENT '租赁天数',
  `daily_price` DECIMAL(10,2) NOT NULL COMMENT '日租金',
  `total_price` DECIMAL(10,2) NOT NULL COMMENT '订单总价',
  `deposit` DECIMAL(10,2) DEFAULT 0 COMMENT '押金',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态：0-待审核，1-已确认，2-已取车，3-已还车，4-已完成，5-已取消',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注信息',
  `admin_remark` VARCHAR(500) DEFAULT NULL COMMENT '管理员备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_car_id` (`car_id`),
  KEY `idx_pickup_store_id` (`pickup_store_id`),
  KEY `idx_return_store_id` (`return_store_id`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表-租车订单信息';

-- =====================================================
-- 6. 公告表（notice）- 系统公告信息表
-- =====================================================
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '公告ID，主键自增',
  `title` VARCHAR(100) NOT NULL COMMENT '公告标题',
  `content` TEXT NOT NULL COMMENT '公告内容',
  `type` TINYINT NOT NULL DEFAULT 1 COMMENT '类型：1-普通公告，2-重要公告',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-已下架，1-已发布',
  `publisher` VARCHAR(50) DEFAULT NULL COMMENT '发布人',
  `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_title` (`title`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`),
  KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表-系统公告信息';

-- =====================================================
-- 初始化数据插入
-- =====================================================

-- 插入默认管理员账号（密码：123456，首次登录会自动加密）
INSERT INTO `admin` (`username`, `password`, `real_name`, `phone`, `role`, `status`) VALUES
('admin', '123456', '系统管理员', '13800138000', 1, 1);

-- 插入测试用户账号（密码：123456，首次登录会自动加密）
INSERT INTO `user` (`username`, `password`, `real_name`, `phone`, `email`, `id_card`, `status`) VALUES
('user001', '123456', '张三', '13900139001', 'zhangsan@test.com', '110101199001011234', 1),
('user002', '123456', '李四', '13900139002', 'lisi@test.com', '110101199002021234', 1);

-- 插入网点数据
INSERT INTO `store` (`name`, `address`, `phone`, `manager`, `status`) VALUES
('朝阳门店', '北京市朝阳区朝阳路100号', '010-12345678', '王经理', 1),
('海淀门店', '北京市海淀区中关村大街200号', '010-23456789', '李经理', 1),
('西城门店', '北京市西城区西单北大街300号', '010-34567890', '张经理', 1);

-- 插入车辆数据
INSERT INTO `car` (`brand`, `model`, `car_number`, `color`, `seats`, `gearbox`, `fuel_type`, `daily_price`, `image`, `store_id`, `status`, `description`) VALUES
('大众', '帕萨特', '京A12345', '黑色', 5, 1, 1, 200.00, '/images/car1.jpg', 1, 1, '舒适商务轿车，适合商务出行'),
('丰田', '凯美瑞', '京B23456', '白色', 5, 1, 1, 180.00, '/images/car2.jpg', 1, 1, '经济实惠，性价比高'),
('本田', '雅阁', '京C34567', '银色', 5, 1, 1, 190.00, '/images/car3.jpg', 2, 1, '动力强劲，操控舒适'),
('奔驰', 'E300L', '京D45678', '黑色', 5, 1, 1, 500.00, '/images/car4.jpg', 1, 1, '豪华商务轿车，尊贵体验'),
('宝马', '530Li', '京E56789', '白色', 5, 1, 1, 480.00, '/images/car5.jpg', 2, 1, '德系豪华，品质卓越'),
('别克', 'GL8', '京F67890', '黑色', 7, 1, 1, 300.00, '/images/car6.jpg', 3, 1, '商务MPV，适合团队出行'),
('特斯拉', 'Model 3', '京G78901', '红色', 5, 1, 3, 350.00, '/images/car7.jpg', 1, 1, '纯电动轿车，环保节能'),
('奥迪', 'A6L', '京H89012', '黑色', 5, 1, 1, 450.00, '/images/car8.jpg', 2, 1, '德系豪华，商务首选');

-- 插入公告数据
INSERT INTO `notice` (`title`, `content`, `type`, `status`, `publisher`, `publish_time`) VALUES
('系统上线公告', '汽车租赁管理系统正式上线，欢迎各位用户使用！', 2, 1, 'admin', NOW()),
('春节优惠活动', '春节期间租车享8折优惠，活动时间：2024年1月15日至2月15日', 1, 1, 'admin', NOW()),
('新网点开业通知', '海淀门店正式开业，欢迎前来体验！', 1, 1, 'admin', NOW());

-- =====================================================
-- 数据库设计完成
-- =====================================================