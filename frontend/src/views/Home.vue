<template>
  <!-- 首页组件 -->
  <MainLayout>
    <div class="home-page">
      <!-- Banner区域 -->
      <div class="banner">
        <h1>欢迎来到汽车租赁管理系统</h1>
        <p>提供优质、便捷、安全的汽车租赁服务</p>
        <el-button type="primary" size="large" @click="goToCars">
          立即租车
        </el-button>
      </div>

      <!-- 热门车辆推荐 -->
      <div class="section">
        <h2 class="section-title">热门车辆推荐</h2>
        <div class="car-list">
          <div
            v-for="car in hotCars"
            :key="car.id"
            class="car-item"
            @click="goToCarDetail(car.id)"
          >
            <div class="car-image">
              <img :src="resolveImageUrl(car.image)" alt="车辆图片" @error="onImageError" />
            </div>
            <div class="car-info">
              <h3>{{ car.brand }} {{ car.model }}</h3>
              <p class="price">￥{{ car.dailyPrice }}/天</p>
              <div class="tags">
                <el-tag size="small">{{ car.seats }}座</el-tag>
                <el-tag size="small" type="info">{{ gearboxText(car.gearbox) }}</el-tag>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 最新公告 -->
      <div class="section">
        <h2 class="section-title">最新公告</h2>
        <div class="notice-list">
          <div
            v-for="notice in notices"
            :key="notice.id"
            class="notice-item"
            @click="goToNoticeDetail(notice.id)"
          >
            <el-tag :type="notice.type === 1 ? 'danger' : 'info'" size="small">
              {{ notice.type === 1 ? '重要' : '普通' }}
            </el-tag>
            <span class="notice-title">{{ notice.title }}</span>
            <span class="notice-time">{{ formatDate(notice.createTime) }}</span>
          </div>
        </div>
      </div>

      <!-- 系统特点 -->
      <div class="section features">
        <h2 class="section-title">系统特点</h2>
        <div class="feature-list">
          <div class="feature-item">
            <el-icon size="40"><ShoppingCart /></el-icon>
            <h3>车辆丰富</h3>
            <p>提供多种品牌、型号的车辆供您选择</p>
          </div>
          <div class="feature-item">
            <el-icon size="40"><MapLocation /></el-icon>
            <h3>网点便捷</h3>
            <p>多个网点方便您取车和还车</p>
          </div>
          <div class="feature-item">
            <el-icon size="40"><Wallet /></el-icon>
            <h3>价格透明</h3>
            <p>日租价格清晰，自动计算总价</p>
          </div>
          <div class="feature-item">
            <el-icon size="40"><Lock /></el-icon>
            <h3>安全可靠</h3>
            <p>车辆定期维护，保障您的出行安全</p>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
/**
 * 首页组件
 * 
 * 功能说明：
 * 1. Banner展示区域
 * 2. 热门车辆推荐
 * 3. 最新公告列表
 * 4. 系统特点介绍
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */

import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'
import { getCarList } from '@/api/car'
import { getNoticeList } from '@/api/notice'
import { ShoppingCart, MapLocation, Wallet, Lock } from '@element-plus/icons-vue'
import { resolveImageUrl, DEFAULT_CAR_IMAGE } from '@/utils/image'

const router = useRouter()

function onImageError(event) {
  event.target.src = DEFAULT_CAR_IMAGE
}

// 热门车辆列表
const hotCars = ref([])

// 最新公告列表
const notices = ref([])

/**
 * 加载热门车辆
 */
async function loadHotCars() {
  try {
    const res = await getCarList({})
    // 取前4辆作为热门推荐
    hotCars.value = res.data.slice(0, 4)
  } catch (error) {
    console.error('加载热门车辆失败：', error)
  }
}

/**
 * 加载最新公告
 */
async function loadNotices() {
  try {
    const res = await getNoticeList()
    // 取前5条作为最新公告
    notices.value = res.data.slice(0, 5)
  } catch (error) {
    console.error('加载最新公告失败：', error)
  }
}

/**
 * 跳转到车辆列表页
 */
function goToCars() {
  router.push('/cars')
}

/**
 * 跳转到车辆详情页
 */
function goToCarDetail(id) {
  router.push(`/car/${id}`)
}

/**
 * 跳转到公告详情页
 */
function goToNoticeDetail(id) {
  router.push(`/notice/${id}`)
}

/**
 * 格式化日期
 */
function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

/**
 * 挡位类型文本
 */
function gearboxText(gearbox) {
  const map = { 1: '手动挡', 2: '自动挡' }
  return map[gearbox] || '未知'
}

// 页面加载时获取数据
onMounted(() => {
  loadHotCars()
  loadNotices()
})
</script>

<style scoped lang="scss">
.home-page {
  .banner {
    background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
    color: #fff;
    padding: 80px 20px;
    text-align: center;
    border-radius: 8px;
    margin-bottom: 30px;

    h1 {
      font-size: 36px;
      margin-bottom: 20px;
    }

    p {
      font-size: 18px;
      margin-bottom: 30px;
    }
  }

  .section {
    margin-bottom: 30px;

    .section-title {
      font-size: 24px;
      color: #303133;
      margin-bottom: 20px;
      padding-bottom: 10px;
      border-bottom: 2px solid #409eff;
    }
  }

  .car-list {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;

    .car-item {
      background-color: #fff;
      border-radius: 8px;
      overflow: hidden;
      cursor: pointer;
      transition: transform 0.3s, box-shadow 0.3s;

      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }

      .car-image {
        height: 150px;
        overflow: hidden;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }

      .car-info {
        padding: 15px;

        h3 {
          font-size: 16px;
          color: #303133;
          margin-bottom: 10px;
        }

        .price {
          font-size: 18px;
          color: #f56c6c;
          font-weight: bold;
          margin-bottom: 10px;
        }

        .tags {
          display: flex;
          gap: 10px;
        }
      }
    }
  }

  .notice-list {
    background-color: #fff;
    border-radius: 8px;
    padding: 20px;

    .notice-item {
      display: flex;
      align-items: center;
      gap: 15px;
      padding: 15px 0;
      border-bottom: 1px solid #ebeef5;
      cursor: pointer;

      &:last-child {
        border-bottom: none;
      }

      &:hover {
        background-color: #f5f7fa;
      }

      .notice-title {
        flex: 1;
        color: #303133;
      }

      .notice-time {
        color: #909399;
        font-size: 14px;
      }
    }
  }

  .features {
    .feature-list {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 20px;

      .feature-item {
        background-color: #fff;
        border-radius: 8px;
        padding: 30px;
        text-align: center;

        .el-icon {
          color: #409eff;
          margin-bottom: 15px;
        }

        h3 {
          font-size: 18px;
          color: #303133;
          margin-bottom: 10px;
        }

        p {
          color: #606266;
          font-size: 14px;
        }
      }
    }
  }
}

@media screen and (max-width: 1200px) {
  .home-page .car-list {
    grid-template-columns: repeat(2, 1fr);
  }

  .home-page .features .feature-list {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media screen and (max-width: 768px) {
  .home-page .car-list {
    grid-template-columns: 1fr;
  }

  .home-page .features .feature-list {
    grid-template-columns: 1fr;
  }
}
</style>