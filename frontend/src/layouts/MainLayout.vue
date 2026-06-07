<template>
  <!-- 用户主布局组件 -->
  <div class="main-layout">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="logo">
        <router-link to="/">汽车租赁管理系统</router-link>
      </div>
      <div class="nav-menu">
        <router-link to="/" class="nav-item">首页</router-link>
        <router-link to="/cars" class="nav-item">车辆列表</router-link>
        <router-link to="/notice" class="nav-item">公告</router-link>
      </div>
      <div class="user-info">
        <!-- 已登录状态 -->
        <template v-if="userStore.isLoggedIn">
          <router-link to="/orders" class="nav-item">我的订单</router-link>
          <router-link to="/profile" class="nav-item">个人中心</router-link>
          <el-dropdown>
            <span class="user-name">
              {{ userStore.userInfo.username }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <!-- 未登录状态 -->
        <template v-else>
          <router-link to="/login" class="nav-item">登录</router-link>
          <router-link to="/register" class="nav-item register">注册</router-link>
        </template>
      </div>
    </el-header>

    <!-- 主内容区域 -->
    <el-main class="main">
      <slot />
    </el-main>

    <!-- 底部信息栏 -->
    <el-footer class="footer">
      <div class="footer-content">
        <p>汽车租赁管理系统 - 毕业设计项目</p>
        <p>技术栈：SpringBoot + Vue3 + MySQL</p>
      </div>
    </el-footer>
  </div>
</template>

<script setup>
/**
 * 用户主布局组件
 * 
 * 功能说明：
 * 1. 顶部导航栏（Logo、菜单、用户信息）
 * 2. 主内容区域（路由视图）
 * 3. 底部信息栏
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */

import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'

// 获取用户状态
const userStore = useUserStore()
const router = useRouter()

/**
 * 退出登录
 */
function handleLogout() {
  userStore.logout()
  ElMessage.success('退出登录成功')
  router.push('/')
}
</script>

<style scoped lang="scss">
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: #409eff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;

  .logo a {
    font-size: 20px;
    font-weight: bold;
    color: #fff;
  }

  .nav-menu {
    display: flex;
    gap: 20px;

    .nav-item {
      color: #fff;
      font-size: 16px;
      padding: 10px 15px;
      border-radius: 4px;
      transition: background-color 0.3s;

      &:hover {
        background-color: rgba(255, 255, 255, 0.2);
      }
    }
  }

  .user-info {
    display: flex;
    align-items: center;
    gap: 15px;

    .nav-item {
      color: #fff;
      font-size: 14px;
      padding: 8px 12px;
      border-radius: 4px;
      transition: background-color 0.3s;

      &:hover {
        background-color: rgba(255, 255, 255, 0.2);
      }

      &.register {
        background-color: rgba(255, 255, 255, 0.3);
      }
    }

    .user-name {
      color: #fff;
      cursor: pointer;
      display: flex;
      align-items: center;
      gap: 5px;
    }
  }
}

.main {
  flex: 1;
  background-color: #f5f5f5;
  padding: 20px;
}

.footer {
  background-color: #333;
  color: #fff;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;

  .footer-content {
    text-align: center;

    p {
      margin: 5px 0;
      font-size: 14px;
    }
  }
}
</style>