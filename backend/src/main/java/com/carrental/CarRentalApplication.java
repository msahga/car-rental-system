package com.carrental;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 汽车租赁管理系统主启动类
 * 
 * 功能说明：
 * 1. SpringBoot应用启动入口
 * 2. 支持WAR包部署到外置Tomcat
 * 3. 扫描Mapper接口
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
@SpringBootApplication
@MapperScan("com.carrental.mapper")
public class CarRentalApplication extends SpringBootServletInitializer {

    /**
     * WAR包部署配置
     * 继承SpringBootServletInitializer，支持外置Tomcat部署
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(CarRentalApplication.class);
    }

    /**
     * 主启动方法
     * 开发环境直接运行，生产环境打包WAR部署
     */
    public static void main(String[] args) {
        SpringApplication.run(CarRentalApplication.class, args);
        System.out.println("========================================");
        System.out.println("汽车租赁管理系统启动成功！");

        System.out.println("========================================");
    }
}