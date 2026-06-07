<template>
  <!-- 车辆详情页面 -->
  <MainLayout>
    <div class="car-detail-page">
      <!-- 车辆信息 -->
      <div class="car-info-card">
        <div class="car-image">
          <img :src="resolveImageUrl(car.image)" alt="车辆图片" @error="onImageError" />
        </div>
        <div class="car-info">
          <h2>{{ car.brand }} {{ car.model }}</h2>
          <p class="car-number">车牌号：{{ car.carNumber }}</p>
          <div class="info-row">
            <span class="label">座位数：</span>
            <span>{{ car.seats }}座</span>
          </div>
          <div class="info-row">
            <span class="label">挡位类型：</span>
            <span>{{ gearboxText(car.gearbox) }}</span>
          </div>
          <div class="info-row">
            <span class="label">燃油类型：</span>
            <span>{{ fuelText(car.fuelType) }}</span>
          </div>
          <div class="info-row">
            <span class="label">所在网点：</span>
            <span>{{ car.storeName || '未知' }}</span>
          </div>
          <div class="info-row">
            <span class="label">车辆状态：</span>
            <el-tag :type="statusType(car.status)">{{ statusText(car.status) }}</el-tag>
          </div>
          <div class="price-row">
            <span class="price">￥{{ car.dailyPrice }}/天</span>
          </div>
        </div>
      </div>

      <!-- 租车下单区域（仅可租状态显示） -->
      <div v-if="car.status === 1" class="order-card">
        <h3>租车下单</h3>
        <el-form ref="orderFormRef" :model="orderForm" :rules="orderRules" label-width="100px">
          <el-form-item label="取车网点" prop="pickupStoreId">
            <el-select v-model="orderForm.pickupStoreId" placeholder="请选择取车网点">
              <el-option
                v-for="store in stores"
                :key="store.id"
                :label="store.name"
                :value="store.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="还车网点" prop="returnStoreId">
            <el-select v-model="orderForm.returnStoreId" placeholder="请选择还车网点">
              <el-option
                v-for="store in stores"
                :key="store.id"
                :label="store.name"
                :value="store.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="取车时间" prop="pickupTime">
            <el-date-picker
              v-model="orderForm.pickupTime"
              type="datetime"
              placeholder="请选择取车时间"
              format="YYYY-MM-DD HH:mm"
              value-format="YYYY-MM-DD HH:mm:ss"
              :disabled-date="disabledPickupDate"
            />
          </el-form-item>
          <el-form-item label="还车时间" prop="returnTime">
            <el-date-picker
              v-model="orderForm.returnTime"
              type="datetime"
              placeholder="请选择还车时间"
              format="YYYY-MM-DD HH:mm"
              value-format="YYYY-MM-DD HH:mm:ss"
              :disabled-date="disabledReturnDate"
            />
          </el-form-item>
          <el-form-item label="备注">
            <el-input
              v-model="orderForm.remark"
              type="textarea"
              :rows="3"
              placeholder="请输入备注信息"
            />
          </el-form-item>
          <el-form-item label="租赁天数">
            <span class="days">{{ rentalDays }}天</span>
          </el-form-item>
          <el-form-item label="订单总价">
            <span class="total-price">￥{{ totalPrice }}</span>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="submitting" @click="handleSubmit">
              提交订单
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 暂不可租提示 -->
      <div v-else class="unavailable-card">
        <el-result
          icon="warning"
          title="该车辆暂不可租"
          sub-title="请选择其他车辆"
        >
          <template #extra>
            <el-button type="primary" @click="goToCars">返回车辆列表</el-button>
          </template>
        </el-result>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
/**
 * 车辆详情页面
 * 
 * 功能说明：
 * 1. 车辆详细信息展示
 * 2. 租车下单表单
 * 3. 自动计算租赁天数和总价
 * 4. 提交订单
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */

import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import MainLayout from '@/layouts/MainLayout.vue'
import { getCarDetail } from '@/api/car'
import { getStoreList } from '@/api/store'
import { createOrder } from '@/api/order'
import { useUserStore } from '@/stores/user'
import { resolveImageUrl, DEFAULT_CAR_IMAGE } from '@/utils/image'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

function onImageError(event) {
  event.target.src = DEFAULT_CAR_IMAGE
}

// 车辆信息
const car = ref({})

// 网点列表
const stores = ref([])

// 加载状态
const submitting = ref(false)

// 订单表单引用
const orderFormRef = ref(null)

// 订单表单数据
const orderForm = reactive({
  pickupStoreId: null,
  returnStoreId: null,
  pickupTime: '',
  returnTime: '',
  remark: ''
})

// 订单表单验证规则
const orderRules = {
  pickupStoreId: [
    { required: true, message: '请选择取车网点', trigger: 'change' }
  ],
  returnStoreId: [
    { required: true, message: '请选择还车网点', trigger: 'change' }
  ],
  pickupTime: [
    { required: true, message: '请选择取车时间', trigger: 'change' }
  ],
  returnTime: [
    { required: true, message: '请选择还车时间', trigger: 'change' }
  ]
}

/**
 * 计算租赁天数
 */
const rentalDays = computed(() => {
  if (!orderForm.pickupTime || !orderForm.returnTime) return 0

  const pickup = new Date(orderForm.pickupTime)
  const returnT = new Date(orderForm.returnTime)

  if (returnT <= pickup) return 0

  const diff = returnT - pickup
  const days = Math.ceil(diff / (1000 * 60 * 60 * 24))

  return days
})

/**
 * 计算订单总价
 */
const totalPrice = computed(() => {
  if (!car.value.dailyPrice || rentalDays.value === 0) return 0
  return (car.value.dailyPrice * rentalDays.value).toFixed(2)
})

/**
 * 加载车辆详情
 */
async function loadCarDetail() {
  try {
    const res = await getCarDetail(route.params.id)
    car.value = res.data
  } catch (error) {
    console.error('加载车辆详情失败：', error)
  }
}

/**
 * 加载网点列表
 */
async function loadStores() {
  try {
    const res = await getStoreList()
    stores.value = res.data
  } catch (error) {
    console.error('加载网点列表失败：', error)
  }
}

/**
 * 禁用取车日期（只能选择今天及以后的日期）
 */
function disabledPickupDate(time) {
  return time.getTime() < Date.now() - 24 * 60 * 60 * 1000
}

/**
 * 禁用还车日期（必须大于取车日期）
 */
function disabledReturnDate(time) {
  if (!orderForm.pickupTime) return time.getTime() < Date.now() - 24 * 60 * 60 * 1000

  const pickup = new Date(orderForm.pickupTime)
  return time.getTime() < pickup.getTime()
}

/**
 * 提交订单
 */
async function handleSubmit() {
  // 检查是否登录
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  // 表单验证
  const valid = await orderFormRef.value.validate().catch(() => false)
  if (!valid) return

  // 检查租赁天数
  if (rentalDays.value === 0) {
    ElMessage.warning('还车时间必须大于取车时间')
    return
  }

  submitting.value = true

  try {
    const params = {
      carId: car.value.id,
      pickupStoreId: orderForm.pickupStoreId,
      returnStoreId: orderForm.returnStoreId,
      pickupTime: orderForm.pickupTime,
      returnTime: orderForm.returnTime,
      remark: orderForm.remark
    }

    await createOrder(params)

    ElMessage.success('订单提交成功，请等待审核')

    // 跳转到我的订单页
    router.push('/orders')
  } catch (error) {
    console.error('提交订单失败：', error)
  } finally {
    submitting.value = false
  }
}

/**
 * 跳转到车辆列表页
 */
function goToCars() {
  router.push('/cars')
}

/**
 * 车辆状态文本
 */
function statusText(status) {
  const map = { 1: '可租', 2: '已租', 3: '维修', 4: '报废' }
  return map[status] || '未知'
}

/**
 * 车辆状态类型（用于Tag颜色）
 */
function statusType(status) {
  const map = { 1: 'success', 2: 'warning', 3: 'danger', 4: 'info' }
  return map[status] || 'info'
}

/**
 * 挡位类型文本
 */
function gearboxText(gearbox) {
  const map = { 1: '手动挡', 2: '自动挡' }
  return map[gearbox] || '未知'
}

/**
 * 燃油类型文本
 */
function fuelText(fuelType) {
  const map = { 1: '汽油', 2: '柴油', 3: '电动', 4: '混动' }
  return map[fuelType] || '未知'
}

// 页面加载时获取数据
onMounted(() => {
  loadCarDetail()
  loadStores()
})
</script>

<style scoped lang="scss">
.car-detail-page {
  .car-info-card {
    background-color: #fff;
    border-radius: 8px;
    padding: 20px;
    display: flex;
    gap: 30px;
    margin-bottom: 20px;

    .car-image {
      width: 400px;
      height: 300px;
      overflow: hidden;
      border-radius: 8px;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    .car-info {
      flex: 1;

      h2 {
        font-size: 24px;
        color: #303133;
        margin-bottom: 15px;
      }

      .car-number {
        color: #909399;
        font-size: 16px;
        margin-bottom: 20px;
      }

      .info-row {
        display: flex;
        margin-bottom: 15px;
        font-size: 16px;

        .label {
          width: 100px;
          color: #606266;
        }
      }

      .price-row {
        margin-top: 30px;

        .price {
          font-size: 28px;
          color: #f56c6c;
          font-weight: bold;
        }
      }
    }
  }

  .order-card {
    background-color: #fff;
    border-radius: 8px;
    padding: 20px;

    h3 {
      font-size: 18px;
      color: #303133;
      margin-bottom: 20px;
    }

    .days {
      font-size: 18px;
      color: #409eff;
      font-weight: bold;
    }

    .total-price {
      font-size: 24px;
      color: #f56c6c;
      font-weight: bold;
    }
  }

  .unavailable-card {
    background-color: #fff;
    border-radius: 8px;
    padding: 20px;
  }
}

@media screen and (max-width: 900px) {
  .car-detail-page .car-info-card {
    flex-direction: column;

    .car-image {
      width: 100%;
      height: 250px;
    }
  }
}
</style>