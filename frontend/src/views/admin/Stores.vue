<template>
  <!-- 网点管理页面 -->
  <div class="stores-page">
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form inline>
        <el-form-item label="网点名称">
          <el-input v-model="searchForm.name" placeholder="请输入网点名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">添加网点</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 网点列表 -->
    <div class="store-list">
      <el-table :data="stores" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="网点名称" />
        <el-table-column prop="address" label="网点地址" />
        <el-table-column prop="phone" label="联系电话" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button
              :type="row.status === 1 ? 'danger' : 'success'"
              link
              @click="handleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
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
        @size-change="loadStores"
        @current-change="loadStores"
      />
    </div>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="网点名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入网点名称" />
        </el-form-item>
        <el-form-item label="网点地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入网点地址" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * 网点管理页面
 * 
 * 功能说明：
 * 1. 网点列表查询
 * 2. 网点搜索筛选
 * 3. 网点添加/编辑
 * 4. 网点状态管理
 * 5. 网点删除
 * 6. 分页显示
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */

import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getStorePage, addStore, updateStore, updateStoreStatus, deleteStore } from '@/api/store'

// 加载状态
const loading = ref(false)
const submitting = ref(false)

// 搜索表单
const searchForm = reactive({
  name: '',
  status: null
})

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
  name: '',
  address: '',
  phone: ''
})

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入网点名称', trigger: 'blur' }],
  address: [{ required: true, message: '请输入网点地址', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
}

/**
 * 加载网点列表
 */
async function loadStores() {
  loading.value = true

  try {
    const params = {
      page: page.value,
      size: size.value,
      name: searchForm.name || undefined,
      status: searchForm.status || undefined
    }

    const res = await getStorePage(params)
    stores.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('加载网点列表失败：', error)
  } finally {
    loading.value = false
  }
}

/**
 * 搜索
 */
function handleSearch() {
  page.value = 1
  loadStores()
}

/**
 * 重置搜索条件
 */
function handleReset() {
  searchForm.name = ''
  searchForm.status = null
  page.value = 1
  loadStores()
}

/**
 * 添加网点
 */
function handleAdd() {
  dialogTitle.value = '添加网点'
  resetForm()
  dialogVisible.value = true
}

/**
 * 编辑网点
 */
function handleEdit(row) {
  dialogTitle.value = '编辑网点'
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
      await updateStore(form)
      ElMessage.success('更新成功')
    } else {
      await addStore(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadStores()
  } catch (error) {
    console.error('提交失败：', error)
  } finally {
    submitting.value = false
  }
}

/**
 * 更新网点状态
 */
async function handleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '启用' : '禁用'

  ElMessageBox.confirm(`确定要${action}该网点吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateStoreStatus(row.id, newStatus)
      ElMessage.success(`${action}成功`)
      loadStores()
    } catch (error) {
      console.error(`${action}失败：`, error)
    }
  }).catch(() => {})
}

/**
 * 删除网点
 */
async function handleDelete(row) {
  ElMessageBox.confirm('确定要删除该网点吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteStore(row.id)
      ElMessage.success('删除成功')
      loadStores()
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
  form.name = ''
  form.address = ''
  form.phone = ''
}

/**
 * 格式化日期
 */
function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

// 页面加载时获取网点列表
onMounted(() => {
  loadStores()
})
</script>

<style scoped lang="scss">
.stores-page {
  .search-area {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;
  }

  .store-list {
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