<template>
  <!-- 404页面 -->
  <div class="not-found-page">
    <el-result
      icon="error"
      title="404"
      sub-title="页面不存在"
    >
      <template #extra>
        <el-button type="primary" @click="goHome">返回首页</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
/**
 * 404页面
 * 
 * 功能说明：
 * 1. 页面不存在提示
 * 2. 返回首页按钮
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */

import { useRouter } from 'vue-router'

const router = useRouter()

/**
 * 返回首页
 */
function goHome() {
  router.push('/')
}
</script>

<style scoped lang="scss">
.not-found-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
}
</style>