<template>
  <!-- 管理员控制台页面 -->
  <div class="dashboard-page">
    <!-- 统计卡片 -->
    <div class="stat-cards">
      <div class="stat-card">
        <div class="stat-icon user-icon">
          <el-icon size="40"><User /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.userCount }}</span>
          <span class="stat-label">注册用户</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon car-icon">
          <el-icon size="40"><ShoppingCart /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.carCount }}</span>
          <span class="stat-label">车辆总数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon order-icon">
          <el-icon size="40"><Document /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.orderCount }}</span>
          <span class="stat-label">订单总数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon store-icon">
          <el-icon size="40"><MapLocation /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.storeCount }}</span>
          <span class="stat-label">网点总数</span>
        </div>
      </div>
    </div>

    <!-- 订单状态统计 -->
    <div class="order-stats">
      <h3>订单状态统计</h3>
      <div class="order-status-list">
        <div class="order-status-item">
          <span class="status-label">待审核</span>
          <el-progress
            :percentage="getPercentage(stats.pendingOrders, stats.orderCount)"
            :stroke-width="20"
            status="warning"
          />
          <span class="status-value">{{ stats.pendingOrders }}个</span>
        </div>
        <div class="order-status-item">
          <span class="status-label">已确认</span>
          <el-progress
            :percentage="getPercentage(stats.confirmedOrders, stats.orderCount)"
            :stroke-width="20"
            status="success"
          />
          <span class="status-value">{{ stats.confirmedOrders }}个</span>
        </div>
        <div class="order-status-item">
          <span class="status-label">已完成</span>
          <el-progress
            :percentage="getPercentage(stats.completedOrders, stats.orderCount)"
            :stroke-width="20"
          />
          <span class="status-value">{{ stats.completedOrders }}个</span>
        </div>
        <div class="order-status-item">
          <span class="status-label">已取消</span>
          <el-progress
            :percentage="getPercentage(stats.cancelledOrders, stats.orderCount)"
            :stroke-width="20"
            color="#909399"
          />
          <span class="status-value">{{ stats.cancelledOrders }}个</span>
        </div>
      </div>
    </div>

    <!-- 最近订单 -->
    <div class="recent-orders">
      <h3>最近订单</h3>
      <el-table :data="recentOrders" stripe>
        <el-table-column prop="orderNo" label="订单编号" width="180" />
        <el-table-column prop="carBrand" label="车辆品牌" />
        <el-table-column prop="carModel" label="车辆型号" />
        <el-table-column prop="totalPrice" label="订单总价">
          <template #default="{ row }">
            ￥{{ row.totalPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
/**
 * 管理员控制台页面
 * 
 * 功能说明：
 * 1. 数据统计展示（用户数、车辆数、订单数、网点数）
 * 2. 订单状态统计
 * 3. 最近订单列表
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */

import { ref, reactive, onMounted } from 'vue'
import { User, ShoppingCart, Document, MapLocation } from '@element-plus/icons-vue'
import { getUserPage } from '@/api/user'
import { getCarPage } from '@/api/car'
import { getOrderPage } from '@/api/order'
import { getStorePage } from '@/api/store'

// 统计数据
const stats = reactive({
  userCount: 0,
  carCount: 0,
  orderCount: 0,
  storeCount: 0,
  pendingOrders: 0,
  confirmedOrders: 0,
  completedOrders: 0,
  cancelledOrders: 0
})

// 最近订单
const recentOrders = ref([])

/**
 * 加载统计数据
 */
async function loadStats() {
  try {
    // 获取用户总数
    const userRes = await getUserPage({ page: 1, size: 1 })
    stats.userCount = userRes.data.total

    // 获取车辆总数
    const carRes = await getCarPage({ page: 1, size: 1 })
    stats.carCount = carRes.data.total

    // 获取订单总数和各状态数量
    const orderRes = await getOrderPage({ page: 1, size: 1 })
    stats.orderCount = orderRes.data.total

    // 获取网点总数
    const storeRes = await getStorePage({ page: 1, size: 1 })
    stats.storeCount = storeRes.data.total

    // 获取各状态订单数量
    const pendingRes = await getOrderPage({ page: 1, size: 1, status: 0 })
    stats.pendingOrders = pendingRes.data.total

    const confirmedRes = await getOrderPage({ page: 1, size: 1, status: 1 })
    stats.confirmedOrders = confirmedRes.data.total

    const completedRes = await getOrderPage({ page: 1, size: 1, status: 4 })
    stats.completedOrders = completedRes.data.total

    const cancelledRes = await getOrderPage({ page: 1, size: 1, status: 5 })
    stats.cancelledOrders = cancelledRes.data.total

    // 获取最近订单
    const recentRes = await getOrderPage({ page: 1, size: 5 })
    recentOrders.value = recentRes.data.records
  } catch (error) {
    console.error('加载统计数据失败：', error)
  }
}

/**
 * 计算百分比
 */
function getPercentage(value, total) {
  if (total === 0) return 0
  return Math.round((value / total) * 100)
}

/**
 * 格式化日期
 */
function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

/**
 * 订单状态文本
 */
function statusText(status) {
  const map = {
    0: '待审核',
    1: '已确认',
    2: '已取车',
    3: '已还车',
    4: '已完成',
    5: '已取消'
  }
  return map[status] || '未知'
}

/**
 * 订单状态类型（用于Tag颜色）
 */
function statusType(status) {
  const map = {
    0: 'warning',
    1: 'primary',
    2: 'success',
    3: 'success',
    4: 'success',
    5: 'info'
  }
  return map[status] || 'info'
}

// 页面加载时获取统计数据
onMounted(() => {
  loadStats()
})
</script>

<style scoped lang="scss">
.dashboard-page {
  .stat-cards {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 20px;

    .stat-card {
      background-color: #fff;
      border-radius: 8px;
      padding: 20px;
      display: flex;
      align-items: center;
      gap: 20px;

      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;

        &.user-icon {
          background-color: #409eff;
          color: #fff;
        }

        &.car-icon {
          background-color: #67c23a;
          color: #fff;
        }

        &.order-icon {
          background-color: #e6a23c;
          color: #fff;
        }

        &.store-icon {
          background-color: #f56c6c;
          color: #fff;
        }
      }

      .stat-info {
        .stat-value {
          font-size: 28px;
          font-weight: bold;
          color: #303133;
        }

        .stat-label {
          font-size: 14px;
          color: #909399;
          margin-top: 5px;
        }
      }
    }
  }

  .order-stats {
    background-color: #fff;
    border-radius: 8px;
    padding: 20px;
    margin-bottom: 20px;

    h3 {
      font-size: 18px;
      color: #303133;
      margin-bottom: 20px;
    }

    .order-status-list {
      .order-status-item {
        display: flex;
        align-items: center;
        margin-bottom: 20px;

        .status-label {
          width: 80px;
          font-size: 14px;
          color: #606266;
        }

        .el-progress {
          flex: 1;
          margin-right: 20px;
        }

        .status-value {
          width: 60px;
          font-size: 14px;
          color: #606266;
        }
      }
    }
  }

  .recent-orders {
    background-color: #fff;
    border-radius: 8px;
    padding: 20px;

    h3 {
      font-size: 18px;
      color: #303133;
      margin-bottom: 20px;
    }
  }
}

@media screen and (max-width: 1200px) {
  .dashboard-page .stat-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media screen and (max-width: 600px) {
  .dashboard-page .stat-cards {
    grid-template-columns: 1fr;
  }
}
</style>