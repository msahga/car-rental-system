<template>
  <!-- 管理员后台布局组件 -->
  <div class="admin-layout">
    <!-- 左侧菜单栏 -->
    <el-aside class="aside" width="200px">
      <div class="logo">
        <h2>管理后台</h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="menu"
        router
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>控制台</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/cars">
          <el-icon><ShoppingCart /></el-icon>
          <span>车辆管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/orders">
          <el-icon><Document /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/stores">
          <el-icon><MapLocation /></el-icon>
          <span>网点管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/notices">
          <el-icon><Bell /></el-icon>
          <span>公告管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 右侧内容区域 -->
    <el-container class="right-container">
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-left">
          <span class="page-title">{{ pageTitle }}</span>
        </div>
        <div class="header-right">
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
        </div>
      </el-header>

      <!-- 主内容区域 -->
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
/**
 * 管理员后台布局组件
 * 
 * 功能说明：
 * 1. 左侧菜单栏（导航菜单）
 * 2. 顶部导航栏（页面标题、用户信息）
 * 3. 主内容区域（路由视图）
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */

import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { DataAnalysis, User, ShoppingCart, Document, MapLocation, Bell, ArrowDown } from '@element-plus/icons-vue'

// 获取路由和用户状态
const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 当前激活菜单
const activeMenu = computed(() => route.path)

// 页面标题
const pageTitle = computed(() => route.meta.title || '管理后台')

/**
 * 退出登录
 */
function handleLogout() {
  userStore.logout()
  ElMessage.success('退出登录成功')
  router.push('/admin/login')
}
</script>

<style scoped lang="scss">
.admin-layout {
  height: 100vh;
  display: flex;
}

.aside {
  background-color: #304156;
  color: #fff;

  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #263445;

    h2 {
      color: #fff;
      font-size: 18px;
    }
  }

  .menu {
    border-right: none;
    background-color: #304156;

    .el-menu-item {
      color: #bfcbd9;

      &:hover {
        background-color: #263445;
      }

      &.is-active {
        color: #409eff;
        background-color: #263445;
      }
    }
  }
}

.right-container {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;

  .header-left {
    .page-title {
      font-size: 18px;
      font-weight: bold;
      color: #303133;
    }
  }

  .header-right {
    .user-name {
      cursor: pointer;
      display: flex;
      align-items: center;
      gap: 5px;
      color: #606266;
    }
  }
}

.main {
  background-color: #f5f5f5;
  padding: 20px;
  overflow-y: auto;
}
</style>