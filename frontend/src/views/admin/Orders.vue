<template>
  <!-- 订单管理页面 -->
  <div class="orders-page">
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form inline>
        <el-form-item label="订单编号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单编号" clearable />
        </el-form-item>
        <el-form-item label="用户ID">
          <el-input v-model="searchForm.userId" placeholder="请输入用户ID" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="待审核" :value="0" />
            <el-option label="已确认" :value="1" />
            <el-option label="已取车" :value="2" />
            <el-option label="已还车" :value="3" />
            <el-option label="已完成" :value="4" />
            <el-option label="已取消" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 订单列表 -->
    <div class="order-list">
      <el-table :data="orders" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="订单编号" width="180" />
        <el-table-column prop="username" label="用户ID" width="100" />
        <el-table-column prop="carBrand" label="车辆品牌" />
        <el-table-column prop="carModel" label="车辆型号" />
        <el-table-column prop="days" label="租赁天数" width="80" />
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
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
            <el-button v-if="row.status === 0" type="success" link @click="handleAudit(row)">审核</el-button>
            <el-button v-if="row.status === 1" type="warning" link @click="handlePickup(row)">取车</el-button>
            <el-button v-if="row.status === 2" type="info" link @click="handleReturn(row)">还车</el-button>
            <el-button v-if="row.status === 3" type="success" link @click="handleComplete(row)">完成</el-button>
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
        @size-change="loadOrders"
        @current-change="loadOrders"
      />
    </div>

    <!-- 订单详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="订单详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ currentOrder.userId }}</el-descriptions-item>
        <el-descriptions-item label="车辆品牌">{{ currentOrder.carBrand }}</el-descriptions-item>
        <el-descriptions-item label="车辆型号">{{ currentOrder.carModel }}</el-descriptions-item>
        <el-descriptions-item label="车牌号">{{ currentOrder.carNumber }}</el-descriptions-item>
        <el-descriptions-item label="租赁天数">{{ currentOrder.days }}天</el-descriptions-item>
        <el-descriptions-item label="取车网点">{{ currentOrder.pickupStoreName }}</el-descriptions-item>
        <el-descriptions-item label="还车网点">{{ currentOrder.returnStoreName }}</el-descriptions-item>
        <el-descriptions-item label="取车时间">{{ formatDate(currentOrder.pickupTime) }}</el-descriptions-item>
        <el-descriptions-item label="还车时间">{{ formatDate(currentOrder.returnTime) }}</el-descriptions-item>
        <el-descriptions-item label="订单总价">￥{{ currentOrder.totalPrice }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="statusType(currentOrder.status)">{{ statusText(currentOrder.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDate(currentOrder.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="用户备注">{{ currentOrder.remark || '无' }}</el-descriptions-item>
        <el-descriptions-item label="管理员备注">{{ currentOrder.adminRemark || '无' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog v-model="auditDialogVisible" title="审核订单" width="400px">
      <el-form label-width="80px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditStatus">
            <el-radio :label="1">确认通过</el-radio>
            <el-radio :label="5">拒绝取消</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="adminRemark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleAuditSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 操作备注对话框 -->
    <el-dialog v-model="actionDialogVisible" :title="actionTitle" width="400px">
      <el-form label-width="80px">
        <el-form-item label="备注">
          <el-input v-model="adminRemark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="actionDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleActionSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * 订单管理页面
 * 
 * 功能说明：
 * 1. 订单列表查询
 * 2. 订单搜索筛选
 * 3. 订单详情查看
 * 4. 订单审核
 * 5. 确认取车/还车/完成
 * 6. 分页显示
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */

import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getOrderPage, getOrderDetail, auditOrder, confirmPickup, confirmReturn, completeOrder } from '@/api/order'

// 加载状态
const loading = ref(false)
const submitting = ref(false)

// 搜索表单
const searchForm = reactive({
  orderNo: '',
  userId: '',
  status: null
})

// 订单列表
const orders = ref([])

// 分页参数
const page = ref(1)
const size = ref(10)
const total = ref(0)

// 详情对话框
const detailDialogVisible = ref(false)
const currentOrder = ref({})

// 审核对话框
const auditDialogVisible = ref(false)
const auditStatus = ref(1)
const adminRemark = ref('')

// 操作对话框
const actionDialogVisible = ref(false)
const actionTitle = ref('')
const actionType = ref('')
const actionOrderId = ref(null)

/**
 * 加载订单列表
 */
async function loadOrders() {
  loading.value = true

  try {
    const params = {
      page: page.value,
      size: size.value,
      orderNo: searchForm.orderNo || undefined,
      userId: searchForm.userId || undefined,
      status: searchForm.status || undefined
    }

    const res = await getOrderPage(params)
    orders.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('加载订单列表失败：', error)
  } finally {
    loading.value = false
  }
}

/**
 * 搜索
 */
function handleSearch() {
  page.value = 1
  loadOrders()
}

/**
 * 重置搜索条件
 */
function handleReset() {
  searchForm.orderNo = ''
  searchForm.userId = ''
  searchForm.status = null
  page.value = 1
  loadOrders()
}

/**
 * 查看详情
 */
async function handleDetail(row) {
  try {
    const res = await getOrderDetail(row.id)
    currentOrder.value = res.data
    detailDialogVisible.value = true
  } catch (error) {
    console.error('获取订单详情失败：', error)
  }
}

/**
 * 审核
 */
function handleAudit(row) {
  actionOrderId.value = row.id
  auditStatus.value = 1
  adminRemark.value = ''
  auditDialogVisible.value = true
}

/**
 * 提交审核
 */
async function handleAuditSubmit() {
  submitting.value = true

  try {
    await auditOrder(actionOrderId.value, { status: auditStatus.value, adminRemark: adminRemark.value })
    ElMessage.success('审核成功')
    auditDialogVisible.value = false
    loadOrders()
  } catch (error) {
    console.error('审核失败：', error)
  } finally {
    submitting.value = false
  }
}

/**
 * 确认取车
 */
function handlePickup(row) {
  actionOrderId.value = row.id
  actionType.value = 'pickup'
  actionTitle.value = '确认取车'
  adminRemark.value = ''
  actionDialogVisible.value = true
}

/**
 * 确认还车
 */
function handleReturn(row) {
  actionOrderId.value = row.id
  actionType.value = 'return'
  actionTitle.value = '确认还车'
  adminRemark.value = ''
  actionDialogVisible.value = true
}

/**
 * 完成订单
 */
function handleComplete(row) {
  actionOrderId.value = row.id
  actionType.value = 'complete'
  actionTitle.value = '完成订单'
  adminRemark.value = ''
  actionDialogVisible.value = true
}

/**
 * 提交操作
 */
async function handleActionSubmit() {
  submitting.value = true

  try {
    if (actionType.value === 'pickup') {
      await confirmPickup(actionOrderId.value, adminRemark.value)
      ElMessage.success('取车确认成功')
    } else if (actionType.value === 'return') {
      await confirmReturn(actionOrderId.value, adminRemark.value)
      ElMessage.success('还车确认成功')
    } else if (actionType.value === 'complete') {
      await completeOrder(actionOrderId.value, adminRemark.value)
      ElMessage.success('订单完成成功')
    }
    actionDialogVisible.value = false
    loadOrders()
  } catch (error) {
    console.error('操作失败：', error)
  } finally {
    submitting.value = false
  }
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
  .search-area {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;
    gap: 41px;
  }

  .order-list {
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