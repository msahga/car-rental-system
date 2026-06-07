<template>
  <!-- 公告管理页面 -->
  <div class="notices-page">
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form inline>
        <el-form-item label="标题">
          <el-input v-model="searchForm.title" placeholder="请输入标题" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="请选择" clearable>
            <el-option label="普通" :value="0" />
            <el-option label="重要" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="已发布" :value="1" />
            <el-option label="未发布" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">发布公告</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 公告列表 -->
    <div class="notice-list">
      <el-table :data="notices" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="type" label="类型">
          <template #default="{ row }">
            <el-tag :type="row.type === 1 ? 'danger' : 'info'">
              {{ row.type === 1 ? '重要' : '普通' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '已发布' : '未发布' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间">
          <template #default="{ row }">
            {{ formatDate(row.publishTime) }}
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
              :type="row.status === 1 ? 'warning' : 'success'"
              link
              @click="handlePublish(row)"
            >
              {{ row.status === 1 ? '下架' : '发布' }}
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
        @size-change="loadNotices"
        @current-change="loadNotices"
      />
    </div>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择公告类型">
            <el-option label="普通公告" :value="0" />
            <el-option label="重要公告" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="5" placeholder="请输入公告内容" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="立即发布" :value="1" />
            <el-option label="暂不发布" :value="0" />
          </el-select>
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
 * 公告管理页面
 * 
 * 功能说明：
 * 1. 公告列表查询
 * 2. 公告搜索筛选
 * 3. 公告添加/编辑
 * 4. 公告发布/下架
 * 5. 公告删除
 * 6. 分页显示
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */

import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getNoticePage, addNotice, updateNotice, publishNotice, unpublishNotice, deleteNotice } from '@/api/notice'

// 加载状态
const loading = ref(false)
const submitting = ref(false)

// 搜索表单
const searchForm = reactive({
  title: '',
  type: null,
  status: null
})

// 公告列表
const notices = ref([])

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
  title: '',
  type: 0,
  content: '',
  status: 0
})

// 表单验证规则
const rules = {
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择公告类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }]
}

/**
 * 加载公告列表
 */
async function loadNotices() {
  loading.value = true

  try {
    const params = {
      page: page.value,
      size: size.value,
      title: searchForm.title || undefined,
      type: searchForm.type || undefined,
      status: searchForm.status || undefined
    }

    const res = await getNoticePage(params)
    notices.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('加载公告列表失败：', error)
  } finally {
    loading.value = false
  }
}

/**
 * 搜索
 */
function handleSearch() {
  page.value = 1
  loadNotices()
}

/**
 * 重置搜索条件
 */
function handleReset() {
  searchForm.title = ''
  searchForm.type = null
  searchForm.status = null
  page.value = 1
  loadNotices()
}

/**
 * 添加公告
 */
function handleAdd() {
  dialogTitle.value = '发布公告'
  resetForm()
  dialogVisible.value = true
}

/**
 * 编辑公告
 */
function handleEdit(row) {
  dialogTitle.value = '编辑公告'
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
      await updateNotice(form)
      ElMessage.success('更新成功')
    } else {
      await addNotice(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadNotices()
  } catch (error) {
    console.error('提交失败：', error)
  } finally {
    submitting.value = false
  }
}

/**
 * 发布/下架公告
 */
async function handlePublish(row) {
  const action = row.status === 1 ? '下架' : '发布'

  ElMessageBox.confirm(`确定要${action}该公告吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      if (row.status === 1) {
        await unpublishNotice(row.id)
      } else {
        await publishNotice(row.id)
      }
      ElMessage.success(`${action}成功`)
      loadNotices()
    } catch (error) {
      console.error(`${action}失败：`, error)
    }
  }).catch(() => {})
}

/**
 * 删除公告
 */
async function handleDelete(row) {
  ElMessageBox.confirm('确定要删除该公告吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteNotice(row.id)
      ElMessage.success('删除成功')
      loadNotices()
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
  form.title = ''
  form.type = 0
  form.content = ''
  form.status = 0
}

/**
 * 格式化日期
 */
function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

// 页面加载时获取公告列表
onMounted(() => {
  loadNotices()
})
</script>

<style scoped lang="scss">
.notices-page {
  .search-area {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;
  }

  .notice-list {
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