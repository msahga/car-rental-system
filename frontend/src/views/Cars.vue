<template>
  <!-- 车辆列表页面 -->
  <MainLayout>
    <div class="cars-page">
      <!-- 搜索筛选区域 -->
      <div class="search-area">
        <el-form inline>
          <el-form-item label="品牌">
            <el-input v-model="searchForm.brand" placeholder="请输入品牌" clearable />
          </el-form-item>
          <el-form-item label="座位数">
            <el-select v-model="searchForm.seats" placeholder="请选择" clearable>
              <el-option label="2座" :value="2" />
              <el-option label="4座" :value="4" />
              <el-option label="5座" :value="5" />
              <el-option label="7座" :value="7" />
            </el-select>
          </el-form-item>
          <el-form-item label="挡位">
            <el-select v-model="searchForm.gearbox" placeholder="请选择" clearable>
              <el-option label="手动挡" :value="1" />
              <el-option label="自动挡" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="价格范围">
            <el-input v-model="searchForm.minPrice" placeholder="最低价" style="width: 100px" />
            <span style="margin: 0 5px">-</span>
            <el-input v-model="searchForm.maxPrice" placeholder="最高价" style="width: 100px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 车辆列表 -->
      <div class="car-list">
        <div
          v-for="car in cars"
          :key="car.id"
          class="car-card"
          @click="goToDetail(car.id)"
        >
          <div class="car-image">
            <img :src="resolveImageUrl(car.image)" alt="车辆图片" @error="onImageError" />
            <el-tag v-if="car.status !== 1" type="warning" class="status-tag">
              {{ statusText(car.status) }}
            </el-tag>
          </div>
          <div class="car-info">
            <h3>{{ car.brand }} {{ car.model }}</h3>
            <p class="car-number">车牌号：{{ car.carNumber }}</p>
            <div class="tags">
              <el-tag size="small">{{ car.seats }}座</el-tag>
              <el-tag size="small" type="info">{{ gearboxText(car.gearbox) }}</el-tag>
              <el-tag size="small" type="warning">{{ fuelText(car.fuelType) }}</el-tag>
            </div>
            <div class="price-row">
              <span class="price">￥{{ car.dailyPrice }}/天</span>
              <el-button
                v-if="car.status === 1"
                type="primary"
                size="small"
                @click.stop="goToDetail(car.id)"
              >
                立即租车
              </el-button>
              <el-button v-else type="info" size="small" disabled>
                暂不可租
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 空数据提示 -->
      <div v-if="cars.length === 0 && !loading" class="empty-data">
        <el-empty description="暂无符合条件的车辆" />
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-icon class="is-loading" size="40"><Loading /></el-icon>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
/**
 * 车辆列表页面
 * 
 * 功能说明：
 * 1. 车辆多条件筛选搜索
 * 2. 车辆列表展示
 * 3. 车辆状态标识
 * 4. 点击跳转到车辆详情页
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */

import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'
import { getCarList } from '@/api/car'
import { Loading } from '@element-plus/icons-vue'
import { resolveImageUrl, DEFAULT_CAR_IMAGE } from '@/utils/image'

const router = useRouter()

function onImageError(event) {
  event.target.src = DEFAULT_CAR_IMAGE
}

// 加载状态
const loading = ref(false)

// 搜索表单
const searchForm = reactive({
  brand: '',
  seats: null,
  gearbox: null,
  minPrice: '',
  maxPrice: ''
})

// 车辆列表
const cars = ref([])

/**
 * 加载车辆列表
 */
async function loadCars() {
  loading.value = true

  try {
    const params = {
      brand: searchForm.brand || undefined,
      seats: searchForm.seats || undefined,
      gearbox: searchForm.gearbox || undefined,
      minPrice: searchForm.minPrice || undefined,
      maxPrice: searchForm.maxPrice || undefined
    }

    const res = await getCarList(params)
    cars.value = res.data
  } catch (error) {
    console.error('加载车辆列表失败：', error)
  } finally {
    loading.value = false
  }
}

/**
 * 搜索
 */
function handleSearch() {
  loadCars()
}

/**
 * 重置搜索条件
 */
function handleReset() {
  searchForm.brand = ''
  searchForm.seats = null
  searchForm.gearbox = null
  searchForm.minPrice = ''
  searchForm.maxPrice = ''
  loadCars()
}

/**
 * 跳转到车辆详情页
 */
function goToDetail(id) {
  router.push(`/car/${id}`)
}

/**
 * 车辆状态文本
 */
function statusText(status) {
  const map = { 1: '可租', 2: '已租', 3: '维修', 4: '报废' }
  return map[status] || '未知'
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

// 页面加载时获取车辆列表
onMounted(() => {
  loadCars()
})
</script>

<style scoped lang="scss">
.cars-page {
  .search-area {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;
  }

  .car-list {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;

    .car-card {
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
        height: 180px;
        position: relative;
        overflow: hidden;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .status-tag {
          position: absolute;
          top: 10px;
          right: 10px;
        }
      }

      .car-info {
        padding: 15px;

        h3 {
          font-size: 18px;
          color: #303133;
          margin-bottom: 10px;
        }

        .car-number {
          color: #909399;
          font-size: 14px;
          margin-bottom: 10px;
        }

        .tags {
          display: flex;
          gap: 10px;
          margin-bottom: 15px;
        }

        .price-row {
          display: flex;
          justify-content: space-between;
          align-items: center;

          .price {
            font-size: 20px;
            color: #f56c6c;
            font-weight: bold;
          }
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
}

@media screen and (max-width: 1200px) {
  .cars-page .car-list {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media screen and (max-width: 900px) {
  .cars-page .car-list {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media screen and (max-width: 600px) {
  .cars-page .car-list {
    grid-template-columns: 1fr;
  }
}
</style>