/**
 * Pinia状态管理 - 用户状态
 * 
 * 功能说明：
 * 1. 存储用户登录状态
 * 2. 存储用户信息
 * 3. 存储JWT Token
 * 4. Token持久化（localStorage）
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  // 状态定义
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
  const role = ref(localStorage.getItem('role') || '')
  
  // 计算属性：是否已登录
  const isLoggedIn = computed(() => !!token.value)
  
  // 计算属性：是否为管理员
  const isAdmin = computed(() => role.value === 'admin')
  
  /**
   * 设置Token
   * @param newToken 新Token
   */
  function setToken(newToken) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }
  
  /**
   * 设置用户信息
   * @param info 用户信息
   */
  function setUserInfo(info) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }
  
  /**
   * 设置角色
   * @param newRole 角色
   */
  function setRole(newRole) {
    role.value = newRole
    localStorage.setItem('role', newRole)
  }
  
  /**
   * 登录成功后设置所有信息
   * @param loginData 登录数据（包含token、userInfo、role）
   */
  function login(loginData) {
    setToken(loginData.token)
    setUserInfo(loginData.userInfo)
    setRole(loginData.role)
  }
  
  /**
   * 退出登录，清除所有信息
   */
  function logout() {
    token.value = ''
    userInfo.value = {}
    role.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('role')
  }
  
  /**
   * 更新用户信息
   * @param info 新用户信息
   */
  function updateUserInfo(info) {
    userInfo.value = { ...userInfo.value, ...info }
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }
  
  return {
    // 状态
    token,
    userInfo,
    role,
    // 计算属性
    isLoggedIn,
    isAdmin,
    // 方法
    setToken,
    setUserInfo,
    setRole,
    login,
    logout,
    updateUserInfo
  }
})