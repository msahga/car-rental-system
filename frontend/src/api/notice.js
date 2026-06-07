/**
 * 公告相关API接口
 * 
 * 功能说明：
 * 1. 查询公告列表接口（公开）
 * 2. 查询公告详情接口（公开）
 * 3. 分页查询公告列表接口（管理员）
 * 4. 添加公告接口（管理员）
 * 5. 更新公告接口（管理员）
 * 6. 删除公告接口（管理员）
 * 7. 发布公告接口（管理员）
 * 8. 下架公告接口（管理员）
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */

import request from '@/utils/request'

/**
 * 查询公告列表（公开）
 */
export function getNoticeList() {
  return request({
    url: '/notice/list',
    method: 'get'
  })
}

/**
 * 查询公告详情（公开）
 * @param id 公告ID
 */
export function getNoticeDetail(id) {
  return request({
    url: `/notice/${id}`,
    method: 'get'
  })
}

/**
 * 分页查询公告列表（管理员）
 * @param params 查询参数
 */
export function getNoticePage(params) {
  return request({
    url: '/notice/page',
    method: 'get',
    params
  })
}

/**
 * 添加公告（管理员）
 * @param data 公告信息
 */
export function addNotice(data) {
  return request({
    url: '/notice',
    method: 'post',
    data
  })
}

/**
 * 更新公告（管理员）
 * @param data 公告信息
 */
export function updateNotice(data) {
  return request({
    url: '/notice',
    method: 'put',
    data
  })
}

/**
 * 删除公告（管理员）
 * @param id 公告ID
 */
export function deleteNotice(id) {
  return request({
    url: `/notice/${id}`,
    method: 'delete'
  })
}

/**
 * 发布公告（管理员）
 * @param id 公告ID
 */
export function publishNotice(id) {
  return request({
    url: `/notice/publish/${id}`,
    method: 'put'
  })
}

/**
 * 下架公告（管理员）
 * @param id 公告ID
 */
export function unpublishNotice(id) {
  return request({
    url: `/notice/unpublish/${id}`,
    method: 'put'
  })
}