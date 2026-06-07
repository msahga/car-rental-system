/**
 * 网点相关API接口
 * 
 * 功能说明：
 * 1. 查询网点列表接口（公开）
 * 2. 查询网点详情接口（公开）
 * 3. 分页查询网点列表接口（管理员）
 * 4. 添加网点接口（管理员）
 * 5. 更新网点接口（管理员）
 * 6. 删除网点接口（管理员）
 * 7. 更新网点状态接口（管理员）
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */

import request from '@/utils/request'

/**
 * 查询网点列表（公开）
 */
export function getStoreList() {
  return request({
    url: '/store/list',
    method: 'get'
  })
}

/**
 * 查询网点详情（公开）
 * @param id 网点ID
 */
export function getStoreDetail(id) {
  return request({
    url: `/store/${id}`,
    method: 'get'
  })
}

/**
 * 分页查询网点列表（管理员）
 * @param params 查询参数
 */
export function getStorePage(params) {
  return request({
    url: '/store/page',
    method: 'get',
    params
  })
}

/**
 * 添加网点（管理员）
 * @param data 网点信息
 */
export function addStore(data) {
  return request({
    url: '/store',
    method: 'post',
    data
  })
}

/**
 * 更新网点（管理员）
 * @param data 网点信息
 */
export function updateStore(data) {
  return request({
    url: '/store',
    method: 'put',
    data
  })
}

/**
 * 删除网点（管理员）
 * @param id 网点ID
 */
export function deleteStore(id) {
  return request({
    url: `/store/${id}`,
    method: 'delete'
  })
}

/**
 * 更新网点状态（管理员）
 * @param id 网点ID
 * @param status 状态
 */
export function updateStoreStatus(id, status) {
  return request({
    url: `/store/status/${id}`,
    method: 'put',
    params: { status }
  })
}