/**
 * Vue Router路由配置
 * 
 * 功能说明：
 * 1. 配置前端路由
 * 2. 配置路由守卫（Token验证）
 * 3. 区分用户路由和管理员路由
 * 
 * 路由结构：
 * / - 首页
 * /login - 用户登录页
 * /register - 用户注册页
 * /cars - 车辆列表页
 * /car/:id - 车辆详情页
 * /orders - 我的订单页
 * /profile - 个人中心页
 * /notice - 公告列表页
 * /admin/login - 管理员登录页
 * /admin/dashboard - 管理员控制台
 * /admin/users - 用户管理页
 * /admin/cars - 车辆管理页
 * /admin/orders - 订单管理页
 * /admin/stores - 网点管理页
 * /admin/notices - 公告管理页
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */

import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

// 路由配置
const routes = [
  // 用户路由
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页', requiresAuth: false }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册', requiresAuth: false }
  },
  {
    path: '/cars',
    name: 'Cars',
    component: () => import('@/views/Cars.vue'),
    meta: { title: '车辆列表', requiresAuth: false }
  },
  {
    path: '/car/:id',
    name: 'CarDetail',
    component: () => import('@/views/CarDetail.vue'),
    meta: { title: '车辆详情', requiresAuth: false }
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('@/views/Orders.vue'),
    meta: { title: '我的订单', requiresAuth: true, role: 'user' }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { title: '个人中心', requiresAuth: true, role: 'user' }
  },
  {
    path: '/notice',
    name: 'Notice',
    component: () => import('@/views/Notice.vue'),
    meta: { title: '公告列表', requiresAuth: false }
  },
  
  // 管理员路由
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/admin/Login.vue'),
    meta: { title: '管理员登录', requiresAuth: false }
  },
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    meta: { requiresAuth: true, role: 'admin' },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '控制台', requiresAuth: true, role: 'admin' }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/Users.vue'),
        meta: { title: '用户管理', requiresAuth: true, role: 'admin' }
      },
      {
        path: 'cars',
        name: 'AdminCars',
        component: () => import('@/views/admin/Cars.vue'),
        meta: { title: '车辆管理', requiresAuth: true, role: 'admin' }
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('@/views/admin/Orders.vue'),
        meta: { title: '订单管理', requiresAuth: true, role: 'admin' }
      },
      {
        path: 'stores',
        name: 'AdminStores',
        component: () => import('@/views/admin/Stores.vue'),
        meta: { title: '网点管理', requiresAuth: true, role: 'admin' }
      },
      {
        path: 'notices',
        name: 'AdminNotices',
        component: () => import('@/views/admin/Notices.vue'),
        meta: { title: '公告管理', requiresAuth: true, role: 'admin' }
      }
    ]
  },
  
  // 404页面
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '页面不存在' }
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

/**
 * 路由守卫
 * 功能：验证Token、权限检查
 */
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 汽车租赁管理系统` : '汽车租赁管理系统'
  
  // 不需要认证的页面直接放行
  if (!to.meta.requiresAuth) {
    next()
    return
  }
  
  // Pinia 已在 main.js 中初始化，此处安全调用
  const userStore = useUserStore()
  
  // 需要认证的页面，检查Token
  if (!userStore || !userStore.token) {
    // 根据角色跳转到对应的登录页
    if (to.meta.role === 'admin') {
      next('/admin/login')
    } else {
      next('/login')
    }
    return
  }
  
  // 检查角色权限
  if (to.meta.role && userStore.role !== to.meta.role) {
    // 权限不足，跳转到对应页面
    if (userStore.role === 'admin') {
      next('/admin/dashboard')
    } else {
      next('/')
    }
    return
  }
  
  // 放行
  next()
})

export default router