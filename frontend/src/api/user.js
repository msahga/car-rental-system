/**
 * 用户相关API接口
 * 
 * 功能说明：
 * 1. 用户注册接口
 * 2. 用户登录接口
 * 3. 获取用户信息接口
 * 4. 更新用户信息接口
 * 5. 修改密码接口
 * 6. 分页查询用户列表接口（管理员）
 * 7. 更新用户状态接口（管理员）
 * 8. 删除用户接口（管理员）
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */

import request from '@/utils/request'

/**
 * 用户注册
 * @param username 用户名
 * @param password 密码
 * @param phone 手机号
 */
export function register(username, password, phone) {
  return request({
    url: '/user/register',
    method: 'post',
    params: { username, password, phone }
  })
}

/**
 * 用户登录
 * @param username 用户名
 * @param password 密码
 */
export function login(username, password) {
  return request({
    url: '/user/login',
    method: 'post',
    data: { username, password }
  })
}

/**
 * 获取当前用户信息
 */
export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

/**
 * 更新用户信息
 * @param data 用户信息
 */
export function updateUserInfo(data) {
  return request({
    url: '/user/info',
    method: 'put',
    data
  })
}

/**
 * 修改密码
 * @param oldPassword 旧密码
 * @param newPassword 新密码
 */
export function updatePassword(oldPassword, newPassword) {
  return request({
    url: '/user/password',
    method: 'put',
    params: { oldPassword, newPassword }
  })
}

/**
 * 分页查询用户列表（管理员）
 * @param params 查询参数
 */
export function getUserPage(params) {
  return request({
    url: '/user/page',
    method: 'get',
    params
  })
}

/**
 * 更新用户状态（管理员）
 * @param id 用户ID
 * @param status 状态
 */
export function updateUserStatus(id, status) {
  return request({
    url: `/user/status/${id}`,
    method: 'put',
    params: { status }
  })
}

/**
 * 删除用户（管理员）
 * @param id 用户ID
 */
export function deleteUser(id) {
  return request({
    url: `/user/${id}`,
    method: 'delete'
  })
}