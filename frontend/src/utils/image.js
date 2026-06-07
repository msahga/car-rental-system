const DEFAULT_CAR_IMAGE = '/images/default-car.svg'

/**
 * 解析车辆图片 URL
 * 数据库存储格式：/images/car1.jpg
 * 开发环境通过 Vite 代理到后端：/api/images/car1.jpg
 */
export function resolveImageUrl(url) {
  if (!url) return DEFAULT_CAR_IMAGE
  if (url.startsWith('http://') || url.startsWith('https://')) return url
  if (url.startsWith('/images/') || url.startsWith('/uploads/')) {
    return url
  }
  return url
}

export { DEFAULT_CAR_IMAGE }
