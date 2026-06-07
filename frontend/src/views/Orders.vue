<template>
  <!-- 我的订单页面 -->
  <MainLayout>
    <div class="orders-page">
      <h2 class="page-title">我的订单</h2>

      <!-- 状态筛选 -->
      <div class="status-filter">
        <el-radio-group v-model="currentStatus" @change="loadOrders">
          <el-radio-button :label="null">全部</el-radio-button>
          <el-radio-button :label="0">待审核</el-radio-button>
          <el-radio-button :label="1">已确认</el-radio-button>
          <el-radio-button :label="2">已取车</el-radio-button>
          <el-radio-button :label="3">已还车</el-radio-button>
          <el-radio-button :label="4">已完成</el-radio-button>
          <el-radio-button :label="5">已取消</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 订单列表 -->
      <div class="order-list">
        <div v-for="order in orders" :key="order.id" class="order-card">
          <div class="order-header">
            <span class="order-no">订单编号：{{ order.orderNo }}</span>
            <el-tag :type="statusType(order.status)">{{ statusText(order.status) }}</el-tag>
          </div>
          <div class="order-body">
            <div class="car-info">
              <img :src="resolveImageUrl(order.carImage)" alt="车辆图片" class="car-image" @error="onImageError" />
              <div class="car-detail">
                <h3>{{ order.carBrand }} {{ order.carModel }}</h3>
                <p>车牌号：{{ order.carNumber }}</p>
              </div>
            </div>
            <div class="order-info">
              <div class="info-row">
                <span class="label">取车网点：</span>
                <span>{{ order.pickupStoreName }}</span>
              </div>
              <div class="info-row">
                <span class="label">还车网点：</span>
                <span>{{ order.returnStoreName }}</span>
              </div>
              <div class="info-row">
                <span class="label">取车时间：</span>
                <span>{{ formatDate(order.pickupTime) }}</span>
              </div>
              <div class="info-row">
                <span class="label">还车时间：</span>
                <span>{{ formatDate(order.returnTime) }}</span>
              </div>
              <div class="info-row">
                <span class="label">租赁天数：</span>
                <span>{{ order.days }}天</span>
              </div>
              <div class="info-row">
                <span class="label">订单总价：</span>
                <span class="price">￥{{ order.totalPrice }}</span>
              </div>
            </div>
          </div>
          <div class="order-footer">
            <span class="create-time">创建时间：{{ formatDate(order.createTime) }}</span>
            <div class="actions">
              <!-- 待审核状态可取消 -->
              <el-button
                v-if="order.status === 0"
                type="danger"
                size="small"
                @click="handleCancel(order.id)"
              >
                取消订单
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 空数据提示 -->
      <div v-if="orders.length === 0 && !loading" class="empty-data">
        <el-empty description="暂无订单数据" />
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-icon class="is-loading" size="40"><Loading /></el-icon>
      </div>

      <!-- 分页 -->
      <div v-if="total > 0" class="pagination-container">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          :page-sizes="[10, 20, 30]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadOrders"
          @current-change="loadOrders"
        />
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
/**
 * 我的订单页面
 * 
 * 功能说明：
 * 1. 订单状态筛选
 * 2. 订单列表展示
 * 3. 取消未审核订单
 * 4. 分页显示
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */

import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import MainLayout from '@/layouts/MainLayout.vue'
import { getUserOrders, cancelOrder } from '@/api/order'
import { Loading } from '@element-plus/icons-vue'
import { resolveImageUrl, DEFAULT_CAR_IMAGE } from '@/utils/image'

function onImageError(event) {
  event.target.src = DEFAULT_CAR_IMAGE
}

// 加载状态
const loading = ref(false)

// 当前状态筛选
const currentStatus = ref(null)

// 订单列表
const orders = ref([])

// 分页参数
const page = ref(1)
const size = ref(10)
const total = ref(0)

/**
 * 加载订单列表
 */
async function loadOrders() {
  loading.value = true

  try {
    const params = {
      page: page.value,
      size: size.value,
      status: currentStatus.value
    }

    const res = await getUserOrders(params)
    orders.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('加载订单列表失败：', error)
  } finally {
    loading.value = false
  }
}

/**
 * 取消订单
 */
async function handleCancel(id) {
  ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await cancelOrder(id)
      ElMessage.success('订单取消成功')
      loadOrders()
    } catch (error) {
      console.error('取消订单失败：', error)
    }
  }).catch(() => {})
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

// 页面加载时获取订单列表
onMounted(() => {
  loadOrders()
})
</script>

<style scoped lang="scss">
.orders-page {
  .page-title {
    font-size: 24px;
    color: #303133;
    margin-bottom: 20px;
  }

  .status-filter {
    background-color: #fff;
    padding: 15px;
    border-radius: 8px;
    margin-bottom: 20px;
  }

  .order-list {
    .order-card {
      background-color: #fff;
      border-radius: 8px;
      padding: 20px;
      margin-bottom: 20px;

      .order-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 15px;
        padding-bottom: 15px;
        border-bottom: 1px solid #ebeef5;

        .order-no {
          font-size: 16px;
          color: #303133;
        }
      }

      .order-body {
        display: flex;
        gap: 30px;

        .car-info {
          display: flex;
          gap: 15px;

          .car-image {
            width: 150px;
            height: 100px;
            object-fit: cover;
            border-radius: 4px;
          }

          .car-detail {
            h3 {
              font-size: 16px;
              color: #303133;
              margin-bottom: 10px;
            }

            p {
              color: #909399;
              font-size: 14px;
            }
          }
        }

        .order-info {
          flex: 1;

          .info-row {
            display: flex;
            margin-bottom: 10px;
            font-size: 14px;

            .label {
              width: 80px;
              color: #606266;
            }

            .price {
              color: #f56c6c;
              font-weight: bold;
            }
          }
        }
      }

      .order-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 15px;
        padding-top: 15px;
        border-top: 1px solid #ebeef5;

        .create-time {
          color: #909399;
          font-size: 14px;
        }

        .actions {
          display: flex;
          gap: 10px;
        }
      }
    }
  }

  .empty-data {
    background-color: #fff;
    border-radius: 8px;
    padding: 40px;
  }

  .loading-container {
    display: flex;
    justify-content: center;
    padding: 40px;
  }

  .pagination-container {
    background-color: #fff;
    border-radius: 8px;
    padding: 20px;
  }
}
</style>