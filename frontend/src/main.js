/**
 * 前端入口文件
 * 
 * 功能说明：
 * 1. 创建Vue应用实例
 * 2. 注册Element Plus组件库
 * 3. 注册Element Plus图标
 * 4. 配置Pinia状态管理
 * 5. 配置Vue Router路由
 * 6. 挂载应用
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import router from './router'
import App from './App.vue'
import './styles/global.scss'

// 创建Vue应用实例
const app = createApp(App)

// 注册Element Plus图标组件
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 使用Pinia状态管理
app.use(createPinia())

// 使用Vue Router路由
app.use(router)

// 使用Element Plus组件库（配置中文语言包）
app.use(ElementPlus, { locale: zhCn })

// 挂载应用
try {
  app.mount('#app')
  console.log('汽车租赁管理系统前端启动成功！')
} catch (error) {
  console.error('Vue应用挂载失败：', error)
}