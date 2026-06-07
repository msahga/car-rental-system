# 汽车租赁管理系统 - 项目启动和部署文档

## 一、项目概述

### 1.1 项目简介
本项目是一个完整的汽车租赁管理系统，采用前后端分离架构，适用于毕业设计、课程项目等场景。系统包含用户前台和管理员后台两大模块，实现了完整的汽车租赁业务流程。

### 1.2 技术栈
**后端技术栈：**
- SpringBoot 2.7.x
- Java 8
- Maven
- MyBatis-Plus
- Lombok
- JWT令牌认证
- BCrypt密码加密

**前端技术栈：**
- Vue3
- Vite
- Element Plus
- Axios
- Vue Router
- Pinia

**数据库：**
- MySQL 8.0
- 字符集：utf8mb4

**部署：**
- 支持外置Tomcat打包部署（WAR包）

### 1.3 项目结构
```
car-rental-system/
├── database/                 # 数据库脚本
│   └── car_rental.sql        # 数据库建表SQL
├── backend/                  # 后端项目
│   ├── pom.xml               # Maven配置文件
│   └── src/                  # 源代码目录
│       └── main/
│           ├── java/         # Java源代码
│           └── resources/    # 配置文件
└── frontend/                 # 前端项目
    ├── package.json          # npm配置文件
    ├── vite.config.js        # Vite配置文件
    ├── index.html            # 入口HTML
    └── src/                  # 源代码目录
        ├── api/              # API接口封装
        ├── router/           # 路由配置
        ├── stores/           # Pinia状态管理
        ├── utils/            # 工具函数
        ├── layouts/          # 布局组件
        └── views/            # 页面组件
```

---

## 二、环境准备

### 2.1 开发环境要求
- JDK 1.8 或以上
- Maven 3.6+ 
- MySQL 8.0
- Node.js 16+ 
- npm 8+ 或 yarn 1.22+
- IDE：IntelliJ IDEA（推荐）或 Eclipse
- 浏览器：Chrome、Edge、Firefox等现代浏览器

### 2.2 安装JDK
1. 下载JDK 8：https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html
2. 安装JDK，配置环境变量：
   - JAVA_HOME：JDK安装路径
   - Path：添加 %JAVA_HOME%\bin
3. 验证安装：打开命令行，执行 `java -version`

### 2.3 安装Maven
1. 下载Maven：https://maven.apache.org/download.cgi
2. 解压到指定目录，配置环境变量：
   - MAVEN_HOME：Maven解压路径
   - Path：添加 %MAVEN_HOME%\bin
3. 验证安装：打开命令行，执行 `mvn -version`

### 2.4 安装MySQL
1. 下载MySQL 8.0：https://dev.mysql.com/downloads/mysql/
2. 安装MySQL，设置root密码（建议：root123456）
3. 启动MySQL服务
4. 验证安装：打开命令行，执行 `mysql -u root -p`

### 2.5 安装Node.js
1. 下载Node.js：https://nodejs.org/
2. 安装Node.js（npm会自动安装）
3. 验证安装：
   - 打开命令行，执行 `node -v`
   - 执行 `npm -v`

---

## 三、数据库初始化

### 3.1 创建数据库
1. 打开MySQL命令行或Navicat等工具
2. 执行以下SQL创建数据库：
```sql
CREATE DATABASE car_rental CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

### 3.2 执行建表脚本
1. 打开项目目录下的 `database/car_rental.sql` 文件
2. 使用Navicat或MySQL命令行执行该SQL文件
3. 确认创建以下6张表：
   - user（用户表）
   - admin（管理员表）
   - store（网点表）
   - car（车辆表）
   - orders（订单表）
   - notice（公告表）

### 3.3 初始化数据
SQL脚本中已包含初始数据：
- 管理员账号：admin / admin123
- 测试用户：user1 / user123
- 测试网点、车辆、公告数据

---

## 四、后端项目启动

### 4.1 导入后端项目
1. 打开IntelliJ IDEA
2. 选择 File -> Open
3. 选择 `backend` 目录
4. 等待Maven自动下载依赖（首次导入需要较长时间）

### 4.2 配置数据库连接
1. 打开 `backend/src/main/resources/application.yml`
2. 修改数据库连接配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/car_rental?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai
    username: root
    password: root123456  # 修改为你的MySQL密码
```

### 4.3 启动后端项目
**方式一：IDE启动**
1. 找到 `CarRentalApplication.java` 文件
2. 右键选择 Run 'CarRentalApplication'
3. 等待启动成功，看到 "Started CarRentalApplication" 日志

**方式二：命令行启动**
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### 4.4 验证后端启动
1. 打开浏览器，访问：http://localhost:8080/api/car/list
2. 如果返回车辆列表数据，说明后端启动成功
3. API文档访问：http://localhost:8080/doc.html

---

## 五、前端项目启动

### 5.1 导入前端项目
1. 打开VS Code或其他编辑器
2. 打开 `frontend` 目录

### 5.2 安装前端依赖
```bash
cd frontend
npm install
```
或使用yarn：
```bash
cd frontend
yarn install
```

### 5.3 启动前端项目
```bash
npm run dev
```
或使用yarn：
```bash
yarn dev
```

### 5.4 验证前端启动
1. 打开浏览器，访问：http://localhost:3000
2. 看到首页界面，说明前端启动成功

---

## 六、系统使用说明

### 6.1 用户前台使用
**访问地址：** http://localhost:3000

**功能模块：**
1. **首页**：系统介绍、热门车辆推荐、最新公告
2. **车辆列表**：多条件筛选车辆、查看车辆详情
3. **租车下单**：选择取还车网点和时间、自动计算价格
4. **我的订单**：查看订单状态、取消未审核订单
5. **个人中心**：修改个人信息、修改密码
6. **公告列表**：查看系统公告

**用户账号：**
- 测试用户：user1 / user123
- 或自行注册新用户

### 6.2 管理员后台使用
**访问地址：** http://localhost:3000/admin/login

**功能模块：**
1. **控制台**：数据统计、订单状态统计、最近订单
2. **用户管理**：用户列表、启用/禁用用户、删除用户
3. **车辆管理**：车辆列表、添加/编辑车辆、状态管理
4. **订单管理**：订单列表、审核订单、确认取车/还车/完成
5. **网点管理**：网点列表、添加/编辑网点、状态管理
6. **公告管理**：公告列表、发布/编辑公告、发布/下架

**管理员账号：**
- 用户名：admin
- 密码：admin123

---

## 七、Tomcat打包部署

### 7.1 后端WAR包打包

#### 7.1.1 修改pom.xml
确保 `backend/pom.xml` 中有以下配置：
```xml
<packaging>war</packaging>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>
</dependency>
```

#### 7.1.2 修改启动类
确保 `CarRentalApplication.java` 继承 `SpringBootServletInitializer`：
```java
@SpringBootApplication
public class CarRentalApplication extends SpringBootServletInitializer {
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(CarRentalApplication.class);
    }
    
    public static void main(String[] args) {
        SpringApplication.run(CarRentalApplication.class, args);
    }
}
```

#### 7.1.3 执行打包命令
```bash
cd backend
mvn clean package
```

打包完成后，在 `backend/target` 目录下生成 `car-rental-backend.war` 文件。

### 7.2 前端打包部署

#### 7.2.1 修改API地址
修改 `frontend/vite.config.js`，将API代理地址改为实际后端地址：
```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://你的服务器IP:8080', // 修改为实际后端地址
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, '/api')
    }
  }
}
```

或修改 `frontend/src/utils/request.js`：
```javascript
const request = axios.create({
  baseURL: 'http://你的服务器IP:8080/api', // 修改为实际后端地址
  timeout: 10000
})
```

#### 7.2.2 执行打包命令
```bash
cd frontend
npm run build
```

打包完成后，在 `frontend/dist` 目录下生成静态文件。

### 7.3 Tomcat部署步骤

#### 7.3.1 安装Tomcat
1. 下载Tomcat 9：https://tomcat.apache.org/download-90.cgi
2. 解压到指定目录，如：`D:\Tomcat`

#### 7.3.2 部署后端WAR包
1. 将 `car-rental-backend.war` 复制到 `Tomcat/webapps` 目录
2. 启动Tomcat：
   - Windows：执行 `Tomcat/bin/startup.bat`
   - Linux：执行 `Tomcat/bin/startup.sh`
3. Tomcat会自动解压WAR包，生成 `car-rental-backend` 目录
4. 访问：http://localhost:8080/car-rental-backend/api/car/list

#### 7.3.3 部署前端静态文件
**方式一：部署到Tomcat**
1. 将 `frontend/dist` 目录内容复制到 `Tomcat/webapps ROOT` 目录
2. 访问：http://localhost:8080

**方式二：部署到Nginx（推荐）**
1. 安装Nginx
2. 修改Nginx配置文件 `nginx.conf`：
```nginx
server {
    listen 80;
    server_name localhost;
    
    location / {
        root /path/to/frontend/dist;  # 前端静态文件路径
        index index.html;
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://localhost:8080/car-rental-backend/api;  # 后端API地址
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```
3. 重启Nginx：`nginx -s reload`
4. 访问：http://localhost

---

## 八、常见问题解决

### 8.1 后端启动失败
**问题：数据库连接失败**
- 检查MySQL是否启动
- 检查数据库连接配置是否正确
- 检查数据库是否存在

**问题：端口8080被占用**
- 修改 `application.yml` 中的端口配置：
```yaml
server:
  port: 8081
```

### 8.2 前端启动失败
**问题：依赖安装失败**
- 清除npm缓存：`npm cache clean --force`
- 重新安装：`npm install`

**问题：API请求失败**
- 检查后端是否启动
- 检查API代理配置是否正确
- 检查浏览器控制台错误信息

### 8.3 登录失败
**问题：用户登录失败**
- 检查用户账号是否存在
- 检查密码是否正确
- 检查数据库中用户状态是否为正常（status=1）

**问题：管理员登录失败**
- 检查管理员账号是否存在
- 检查密码是否正确
- 确认使用的是管理员登录入口（/admin/login）

---

## 九、项目扩展建议

### 9.1 功能扩展
- 添加支付功能（支付宝、微信支付）
- 添加车辆评价功能
- 添加优惠券功能
- 添加会员等级功能
- 添加车辆保险功能

### 9.2 技术扩展
- 添加Redis缓存
- 添加消息队列（RabbitMQ）
- 添加分布式部署
- 添加监控告警（Prometheus + Grafana）
- 添加日志收集（ELK）

---

## 十、联系与支持

本项目为毕业设计示例项目，如有问题可通过以下方式联系：
- 查看项目源码注释
- 查看API文档：http://localhost:8080/doc.html

---

**祝您使用顺利！**