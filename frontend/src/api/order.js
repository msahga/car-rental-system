/**
 * 订单相关API接口
 * 
 * 功能说明：
 * 1. 创建订单接口
 * 2. 查询用户订单列表接口
 * 3. 查询订单详情接口
 * 4. 取消订单接口（用户）
 * 5. 分页查询订单列表接口（管理员）
 * 6. 审核订单接口（管理员）
 * 7. 确认取车接口（管理员）
 * 8. 确认还车接口（管理员）
 * 9. 完成订单接口（管理员）
 * 10. 取消订单接口（管理员）
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */

import request from '@/utils/request'

/**
 * 创建订单
 * @param params 订单参数
 */
export function createOrder(params) {
  return request({
    url: '/orders',
    method: 'post',
    params
  })
}

/**
 * 查询用户订单列表
 * @param params 查询参数
 */
export function getUserOrders(params) {
  return request({
    url: '/orders/user',
    method: 'get',
    params
  })
}

/**
 * 查询订单详情
 * @param id 订单ID
 */
export function getOrderDetail(id) {
  return request({
    url: `/orders/${id}`,
    method: 'get'
  })
}

/**
 * 取消订单（用户）
 * @param id 订单ID
 */
export function cancelOrder(id) {
  return request({
    url: `/orders/cancel/${id}`,
    method: 'put'
  })
}

/**
 * 分页查询订单列表（管理员）
 * @param params 查询参数
 */
export function getOrderPage(params) {
  return request({
    url: '/orders/page',
    method: 'get',
    params
  })
}

/**
 * 审核订单（管理员）
 * @param id 订单ID
 * @param params 审核参数
 */
export function auditOrder(id, params) {
  return request({
    url: `/orders/audit/${id}`,
    method: 'put',
    params
  })
}

/**
 * 确认取车（管理员）
 * @param id 订单ID
 * @param adminRemark 管理员备注
 */
export function confirmPickup(id, adminRemark) {
  return request({
    url: `/orders/pickup/${id}`,
    method: 'put',
    params: { adminRemark }
  })
}

/**
 * 确认还车（管理员）
 * @param id 订单ID
 * @param adminRemark 管理员备注
 */
export function confirmReturn(id, adminRemark) {
  return request({
    url: `/orders/return/${id}`,
    method: 'put',
    params: { adminRemark }
  })
}

/**
 * 完成订单（管理员）
 * @param id 订单ID
 * @param adminRemark 管理员备注
 */
export function completeOrder(id, adminRemark) {
  return request({
    url: `/orders/complete/${id}`,
    method: 'put',
    params: { adminRemark }
  })
}

/**
 * 取消订单（管理员）
 * @param id 订单ID
 * @param adminRemark 管理员备注
 */
export function cancelOrderByAdmin(id, adminRemark) {
  return request({
    url: `/orders/cancel-admin/${id}`,
    method: 'put',
    params: { adminRemark }
  })
}