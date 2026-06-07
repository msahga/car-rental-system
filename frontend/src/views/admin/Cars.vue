<template>
  <!-- 车辆管理页面 -->
  <div class="cars-page">
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form inline>
        <el-form-item label="品牌">
          <el-input v-model="searchForm.brand" placeholder="请输入品牌" clearable />
        </el-form-item>
        <el-form-item label="型号">
          <el-input v-model="searchForm.model" placeholder="请输入型号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="可租" :value="1" />
            <el-option label="已租" :value="2" />
            <el-option label="维修" :value="3" />
            <el-option label="报废" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">添加车辆</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 车辆列表 -->
    <div class="car-list">
      <el-table :data="cars" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="brand" label="品牌" />
        <el-table-column prop="model" label="型号" />
        <el-table-column prop="carNumber" label="车牌号" />
        <el-table-column prop="seats" label="座位数" width="80" />
        <el-table-column prop="gearbox" label="挡位">
          <template #default="{ row }">
            {{ gearboxText(row.gearbox) }}
          </template>
        </el-table-column>
        <el-table-column prop="price" label="日租价">
          <template #default="{ row }">
            ￥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link @click="handleStatus(row)">状态</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        :page-sizes="[10, 20, 30]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadCars"
        @current-change="loadCars"
      />
    </div>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="品牌" prop="brand">
          <el-input v-model="form.brand" placeholder="请输入品牌" />
        </el-form-item>
        <el-form-item label="型号" prop="model">
          <el-input v-model="form.model" placeholder="请输入型号" />
        </el-form-item>
        <el-form-item label="车牌号" prop="carNumber">
          <el-input v-model="form.carNumber" placeholder="请输入车牌号" />
        </el-form-item>
        <el-form-item label="座位数" prop="seats">
          <el-select v-model="form.seats" placeholder="请选择">
            <el-option label="2座" :value="2" />
            <el-option label="4座" :value="4" />
            <el-option label="5座" :value="5" />
            <el-option label="7座" :value="7" />
          </el-select>
        </el-form-item>
        <el-form-item label="挡位类型" prop="gearbox">
          <el-select v-model="form.gearbox" placeholder="请选择">
            <el-option label="手动挡" :value="1" />
            <el-option label="自动挡" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="燃油类型" prop="fuelType">
          <el-select v-model="form.fuelType" placeholder="请选择">
            <el-option label="汽油" :value="1" />
            <el-option label="柴油" :value="2" />
            <el-option label="电动" :value="3" />
            <el-option label="混动" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="日租价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="所在网点" prop="storeId">
          <el-select v-model="form.storeId" placeholder="请选择网点">
            <el-option
              v-for="store in stores"
              :key="store.id"
              :label="store.name"
              :value="store.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="车辆图片" prop="image">
          <el-input v-model="form.image" placeholder="请输入图片URL" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 状态修改对话框 -->
    <el-dialog v-model="statusDialogVisible" title="修改车辆状态" width="400px">
      <el-form label-width="80px">
        <el-form-item label="当前状态">
          <el-tag :type="statusType(currentCar.status)">{{ statusText(currentCar.status) }}</el-tag>
        </el-form-item>
        <el-form-item label="新状态">
          <el-select v-model="newStatus" placeholder="请选择">
            <el-option label="可租" :value="1" />
            <el-option label="已租" :value="2" />
            <el-option label="维修" :value="3" />
            <el-option label="报废" :value="4" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleStatusSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * 车辆管理页面
 * 
 * 功能说明：
 * 1. 车辆列表查询
 * 2. 车辆搜索筛选
 * 3. 车辆添加/编辑
 * 4. 车辆状态管理
 * 5. 车辆删除
 * 6. 分页显示
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */

import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCarPage, addCar, updateCar, updateCarStatus, deleteCar } from '@/api/car'
import { getStoreList } from '@/api/store'

// 加载状态
const loading = ref(false)
const submitting = ref(false)

// 搜索表单
const searchForm = reactive({
  brand: '',
  model: '',
  status: null
})

// 车辆列表
const cars = ref([])

// 网点列表
const stores = ref([])

// 分页参数
const page = ref(1)
const size = ref(10)
const total = ref(0)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const form = reactive({
  id: null,
  brand: '',
  model: '',
  carNumber: '',
  seats: null,
  gearbox: null,
  fuelType: null,
  price: 0,
  storeId: null,
  image: ''
})

// 表单验证规则
const rules = {
  brand: [{ required: true, message: '请输入品牌', trigger: 'blur' }],
  model: [{ required: true, message: '请输入型号', trigger: 'blur' }],
  carNumber: [{ required: true, message: '请输入车牌号', trigger: 'blur' }],
  seats: [{ required: true, message: '请选择座位数', trigger: 'change' }],
  gearbox: [{ required: true, message: '请选择挡位类型', trigger: 'change' }],
  fuelType: [{ required: true, message: '请选择燃油类型', trigger: 'change' }],
  price: [{ required: true, message: '请输入日租价格', trigger: 'blur' }],
  storeId: [{ required: true, message: '请选择网点', trigger: 'change' }]
}

// 状态对话框
const statusDialogVisible = ref(false)
const currentCar = ref({})
const newStatus = ref(null)

/**
 * 加载车辆列表
 */
async function loadCars() {
  loading.value = true

  try {
    const params = {
      page: page.value,
      size: size.value,
      brand: searchForm.brand || undefined,
      model: searchForm.model || undefined,
      status: searchForm.status || undefined
    }

    const res = await getCarPage(params)
    cars.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('加载车辆列表失败：', error)
  } finally {
    loading.value = false
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
 * 搜索
 */
function handleSearch() {
  page.value = 1
  loadCars()
}

/**
 * 重置搜索条件
 */
function handleReset() {
  searchForm.brand = ''
  searchForm.model = ''
  searchForm.status = null
  page.value = 1
  loadCars()
}

/**
 * 添加车辆
 */
function handleAdd() {
  dialogTitle.value = '添加车辆'
  resetForm()
  dialogVisible.value = true
}

/**
 * 编辑车辆
 */
function handleEdit(row) {
  dialogTitle.value = '编辑车辆'
  Object.assign(form, row)
  dialogVisible.value = true
}

/**
 * 提交表单
 */
async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true

  try {
    if (form.id) {
      await updateCar(form)
      ElMessage.success('更新成功')
    } else {
      await addCar(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadCars()
  } catch (error) {
    console.error('提交失败：', error)
  } finally {
    submitting.value = false
  }
}

/**
 * 修改状态
 */
function handleStatus(row) {
  currentCar.value = row
  newStatus.value = row.status
  statusDialogVisible.value = true
}

/**
 * 提交状态修改
 */
async function handleStatusSubmit() {
  submitting.value = true

  try {
    await updateCarStatus(currentCar.value.id, newStatus.value)
    ElMessage.success('状态修改成功')
    statusDialogVisible.value = false
    loadCars()
  } catch (error) {
    console.error('状态修改失败：', error)
  } finally {
    submitting.value = false
  }
}

/**
 * 删除车辆
 */
async function handleDelete(row) {
  ElMessageBox.confirm('确定要删除该车辆吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteCar(row.id)
      ElMessage.success('删除成功')
      loadCars()
    } catch (error) {
      console.error('删除失败：', error)
    }
  }).catch(() => {})
}

/**
 * 重置表单
 */
function resetForm() {
  form.id = null
  form.brand = ''
  form.model = ''
  form.carNumber = ''
  form.seats = null
  form.gearbox = null
  form.fuelType = null
  form.price = 0
  form.storeId = null
  form.image = ''
}

/**
 * 挡位类型文本
 */
function gearboxText(gearbox) {
  const map = { 1: '手动挡', 2: '自动挡' }
  return map[gearbox] || '未知'
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

// 页面加载时获取数据
onMounted(() => {
  loadCars()
  loadStores()
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
    background-color: #fff;
    border-radius: 8px;
    padding: 20px;
    margin-bottom: 20px;
  }

  .pagination-container {
    background-color: #fff;
    border-radius: 8px;
    padding: 20px;
    display: flex;
    justify-content: center;
  }
}
</style>