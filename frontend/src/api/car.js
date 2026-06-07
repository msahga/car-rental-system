/**
 * 车辆相关API接口
 * 
 * 功能说明：
 * 1. 查询车辆列表接口（公开）
 * 2. 查询车辆详情接口（公开）
 * 3. 分页查询车辆列表接口（管理员）
 * 4. 添加车辆接口（管理员）
 * 5. 更新车辆接口（管理员）
 * 6. 删除车辆接口（管理员）
 * 7. 更新车辆状态接口（管理员）
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */

import request from '@/utils/request'

/**
 * 查询车辆列表（公开）
 * @param params 查询参数
 */
export function getCarList(params) {
  return request({
    url: '/car/list',
    method: 'get',
    params
  })
}

/**
 * 查询车辆详情（公开）
 * @param id 车辆ID
 */
export function getCarDetail(id) {
  return request({
    url: `/car/${id}`,
    method: 'get'
  })
}

/**
 * 分页查询车辆列表（管理员）
 * @param params 查询参数
 */
export function getCarPage(params) {
  return request({
    url: '/car/page',
    method: 'get',
    params
  })
}

/**
 * 添加车辆（管理员）
 * @param data 车辆信息
 */
export function addCar(data) {
  return request({
    url: '/car',
    method: 'post',
    data
  })
}

/**
 * 更新车辆（管理员）
 * @param data 车辆信息
 */
export function updateCar(data) {
  return request({
    url: '/car',
    method: 'put',
    data
  })
}

/**
 * 删除车辆（管理员）
 * @param id 车辆ID
 */
export function deleteCar(id) {
  return request({
    url: `/car/${id}`,
    method: 'delete'
  })
}

/**
 * 更新车辆状态（管理员）
 * @param id 车辆ID
 * @param status 状态
 */
export function updateCarStatus(id, status) {
  return request({
    url: `/car/status/${id}`,
    method: 'put',
    params: { status }
  })
}