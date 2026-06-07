<template>
  <!-- 公告列表页面 -->
  <MainLayout>
    <div class="notice-page">
      <h2 class="page-title">系统公告</h2>

      <!-- 公告列表 -->
      <div class="notice-list">
        <div
          v-for="notice in notices"
          :key="notice.id"
          class="notice-card"
          @click="showDetail(notice)"
        >
          <div class="notice-header">
            <el-tag :type="notice.type === 1 ? 'danger' : 'info'" size="small">
              {{ notice.type === 1 ? '重要' : '普通' }}
            </el-tag>
            <span class="notice-title">{{ notice.title }}</span>
            <span class="notice-time">{{ formatDate(notice.createTime) }}</span>
          </div>
          <div class="notice-content">
            {{ notice.content }}
          </div>
        </div>
      </div>

      <!-- 空数据提示 -->
      <div v-if="notices.length === 0 && !loading" class="empty-data">
        <el-empty description="暂无公告" />
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-icon class="is-loading" size="40"><Loading /></el-icon>
      </div>

      <!-- 公告详情对话框 -->
      <el-dialog v-model="dialogVisible" title="公告详情" width="600px">
        <div class="notice-detail">
          <div class="detail-header">
            <el-tag :type="currentNotice.type === 1 ? 'danger' : 'info'" size="small">
              {{ currentNotice.type === 1 ? '重要' : '普通' }}
            </el-tag>
            <span class="detail-title">{{ currentNotice.title }}</span>
          </div>
          <div class="detail-time">
            发布时间：{{ formatDate(currentNotice.publishTime) }}
          </div>
          <div class="detail-content">
            {{ currentNotice.content }}
          </div>
        </div>
      </el-dialog>
    </div>
  </MainLayout>
</template>

<script setup>
/**
 * 公告列表页面
 * 
 * 功能说明：
 * 1. 公告列表展示
 * 2. 公告详情查看
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */

import { ref, onMounted } from 'vue'
import MainLayout from '@/layouts/MainLayout.vue'
import { getNoticeList } from '@/api/notice'
import { Loading } from '@element-plus/icons-vue'

// 加载状态
const loading = ref(false)

// 公告列表
const notices = ref([])

// 详情对话框
const dialogVisible = ref(false)
const currentNotice = ref({})

/**
 * 加载公告列表
 */
async function loadNotices() {
  loading.value = true

  try {
    const res = await getNoticeList()
    notices.value = res.data
  } catch (error) {
    console.error('加载公告列表失败：', error)
  } finally {
    loading.value = false
  }
}

/**
 * 显示公告详情
 */
function showDetail(notice) {
  currentNotice.value = notice
  dialogVisible.value = true
}

/**
 * 格式化日期
 */
function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

// 页面加载时获取公告列表
onMounted(() => {
  loadNotices()
})
</script>

<style scoped lang="scss">
.notice-page {
  .page-title {
    font-size: 24px;
    color: #303133;
    margin-bottom: 20px;
  }

  .notice-list {
    .notice-card {
      background-color: #fff;
      border-radius: 8px;
      padding: 20px;
      margin-bottom: 15px;
      cursor: pointer;
      transition: background-color 0.3s;

      &:hover {
        background-color: #f5f7fa;
      }

      .notice-header {
        display: flex;
        align-items: center;
        gap: 15px;
        margin-bottom: 15px;

        .notice-title {
          font-size: 16px;
          color: #303133;
          flex: 1;
        }

        .notice-time {
          color: #909399;
          font-size: 14px;
        }
      }

      .notice-content {
        color: #606266;
        font-size: 14px;
        line-height: 1.6;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
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

  .notice-detail {
    .detail-header {
      display: flex;
      align-items: center;
      gap: 15px;
      margin-bottom: 15px;

      .detail-title {
        font-size: 18px;
        color: #303133;
      }
    }

    .detail-time {
      color: #909399;
      font-size: 14px;
      margin-bottom: 20px;
    }

    .detail-content {
      color: #606266;
      font-size: 14px;
      line-height: 1.8;
    }
  }
}
</style>