/**
 * 管理员相关API接口
 * 
 * 功能说明：
 * 1. 管理员登录接口
 * 2. 获取管理员信息接口
 * 3. 更新管理员信息接口
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */

import request from '@/utils/request'

/**
 * 管理员登录
 * @param username 用户名
 * @param password 密码
 */
export function adminLogin(username, password) {
  return request({
    url: '/admin/login',
    method: 'post',
    data: { username, password }
  })
}

/**
 * 获取当前管理员信息
 */
export function getAdminInfo() {
  return request({
    url: '/admin/info',
    method: 'get'
  })
}

/**
 * 更新管理员信息
 * @param data 管理员信息
 */
export function updateAdminInfo(data) {
  return request({
    url: '/admin/info',
    method: 'put',
    data
  })
}