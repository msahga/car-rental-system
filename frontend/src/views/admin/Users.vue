<template>
  <!-- 用户管理页面 -->
  <div class="users-page">
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form inline>
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable />
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
        </el-form-item>
      </el-form>
    </div>

    <!-- 用户列表 -->
    <div class="user-list">
      <el-table :data="users" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="realName" label="真实姓名" />
        <el-table-column prop="idCard" label="身份证号" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
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
        @size-change="loadUsers"
        @current-change="loadUsers"
      />
    </div>
  </div>
</template>

<script setup>
/**
 * 用户管理页面
 * 
 * 功能说明：
 * 1. 用户列表查询
 * 2. 用户搜索筛选
 * 3. 用户状态管理（启用/禁用）
 * 4. 用户删除
 * 5. 分页显示
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */

import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserPage, updateUserStatus, deleteUser } from '@/api/user'

// 加载状态
const loading = ref(false)

// 搜索表单
const searchForm = reactive({
  username: '',
  phone: '',
  status: null
})

// 用户列表
const users = ref([])

// 分页参数
const page = ref(1)
const size = ref(10)
const total = ref(0)

/**
 * 加载用户列表
 */
async function loadUsers() {
  loading.value = true

  try {
    const params = {
      page: page.value,
      size: size.value,
      username: searchForm.username || undefined,
      phone: searchForm.phone || undefined,
      status: searchForm.status || undefined
    }

    const res = await getUserPage(params)
    users.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('加载用户列表失败：', error)
  } finally {
    loading.value = false
  }
}

/**
 * 搜索
 */
function handleSearch() {
  page.value = 1
  loadUsers()
}

/**
 * 重置搜索条件
 */
function handleReset() {
  searchForm.username = ''
  searchForm.phone = ''
  searchForm.status = null
  page.value = 1
  loadUsers()
}

/**
 * 更新用户状态
 */
async function handleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '启用' : '禁用'

  ElMessageBox.confirm(`确定要${action}该用户吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateUserStatus(row.id, newStatus)
      ElMessage.success(`${action}成功`)
      loadUsers()
    } catch (error) {
      console.error(`${action}失败：`, error)
    }
  }).catch(() => {})
}

/**
 * 删除用户
 */
async function handleDelete(row) {
  ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteUser(row.id)
      ElMessage.success('删除成功')
      loadUsers()
    } catch (error) {
      console.error('删除失败：', error)
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

// 页面加载时获取用户列表
onMounted(() => {
  loadUsers()
})
</script>

<style scoped lang="scss">
.users-page {
  .search-area {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;
  }

  .user-list {
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