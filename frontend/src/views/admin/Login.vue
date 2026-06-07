<template>
  <!-- 管理员登录页面 -->
  <div class="admin-login-page">
    <div class="login-card">
      <h2>管理员登录</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入管理员用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin">
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
/**
 * 管理员登录页面
 * 
 * 功能说明：
 * 1. 管理员登录表单
 * 2. 表单验证
 * 3. 登录成功后保存Token和管理员信息
 * 4. 跳转到管理后台控制台
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */

import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { adminLogin, getAdminInfo } from '@/api/admin'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

// 表单引用
const formRef = ref(null)

// 加载状态
const loading = ref(false)

// 表单数据
const form = reactive({
  username: '',
  password: ''
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

/**
 * 处理登录
 */
async function handleLogin() {
  // 表单验证
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true

  try {
    // 调用管理员登录接口
    const res = await adminLogin(form.username, form.password)
    
    // 保存Token
    userStore.setToken(res.data)
    
    // 获取管理员信息
    const adminInfoRes = await getAdminInfo()
    
    // 保存管理员信息和角色
    userStore.setUserInfo(adminInfoRes.data)
    userStore.setRole('admin')
    
    ElMessage.success('登录成功')
    
    // 跳转到管理后台控制台
    router.push('/admin/dashboard')
  } catch (error) {
    console.error('登录失败：', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.admin-login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #304156 0%, #263445 100%);

  .login-card {
    background-color: #fff;
    border-radius: 8px;
    padding: 30px;
    width: 400px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

    h2 {
      text-align: center;
      color: #303133;
      margin-bottom: 30px;
    }
  }
}
</style>