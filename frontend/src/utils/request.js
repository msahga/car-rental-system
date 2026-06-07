/**
 * Axios请求封装
 * 
 * 功能说明：
 * 1. 创建Axios实例
 * 2. 配置请求拦截器（添加Token）
 * 3. 配置响应拦截器（统一处理错误）
 * 4. 统一返回格式处理
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */

import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { useUserStore } from '@/stores/user'

// 创建Axios实例
const request = axios.create({
  baseURL: '/api', // API基础路径
  timeout: 10000, // 请求超时时间：10秒
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})

/**
 * 请求拦截器
 * 功能：在请求头中添加JWT Token
 */
request.interceptors.request.use(
  config => {
    // Pinia 已在 main.js 中初始化，此处安全调用
    const userStore = useUserStore()

    // 如果存在Token，添加到请求头
    if (userStore && userStore.token) {
      config.headers['Authorization'] = `Bearer ${userStore.token}`
    }

    return config
  },
  error => {
    console.error('请求拦截器错误：', error)
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 * 功能：统一处理响应和错误
 */
request.interceptors.response.use(
  response => {
    // 获取响应数据
    const res = response.data

    // 判断响应状态码
    if (res.code === 200) {
      // 成功，返回数据
      return res
    } else if (res.code === 401) {
      // 未登录或Token过期
      ElMessage.error('未登录或Token已过期，请重新登录')

      // 清除用户状态
      const userStore = useUserStore()
      if (userStore) {
        userStore.logout()
      }

      // 跳转到登录页
      router.push('/login')

      return Promise.reject(new Error(res.msg || '未登录'))
    } else if (res.code === 403) {
      // 无权限
      ElMessage.error('无权限访问')
      return Promise.reject(new Error(res.msg || '无权限'))
    } else {
      // 其他错误
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
  },
  error => {
    console.error('响应拦截器错误：', error)

    // 处理HTTP错误
    if (error.response) {
      switch (error.response.status) {
        case 401:
          ElMessage.error('未登录或Token已过期')
          const userStore = useUserStore()
          if (userStore) {
            userStore.logout()
          }
          router.push('/login')
          break
        case 403:
          ElMessage.error('无权限访问')
          break
        case 404:
          ElMessage.error('请求地址不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(`请求失败：${error.message}`)
      }
    } else {
      // 网络错误或请求被取消
      if (error.message.includes('timeout')) {
        ElMessage.error('请求超时')
      } else if (error.message.includes('Network Error')) {
        ElMessage.error('网络错误')
      } else {
        ElMessage.error('请求失败')
      }
    }

    return Promise.reject(error)
  }
)

// 导出Axios实例
export default request