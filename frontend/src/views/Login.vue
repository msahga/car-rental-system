<template>
  <!-- 用户登录页面 -->
  <MainLayout>
    <div class="login-page">
      <div class="login-card">
        <h2>用户登录</h2>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" />
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
            <router-link to="/register" class="register-link">
              还没有账号？立即注册
            </router-link>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
/**
 * 用户登录页面
 * 
 * 功能说明：
 * 1. 用户登录表单
 * 2. 表单验证
 * 3. 登录成功后保存Token和用户信息
 * 4. 跳转到首页或个人中心
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */

import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import MainLayout from '@/layouts/MainLayout.vue'
import { login, getUserInfo } from '@/api/user'
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
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
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
    // 调用登录接口
    const res = await login(form.username, form.password)
    
    // 保存Token
    userStore.setToken(res.data)
    
    // 获取用户信息
    const userInfoRes = await getUserInfo()
    
    // 保存用户信息和角色
    userStore.setUserInfo(userInfoRes.data)
    userStore.setRole('user')
    
    ElMessage.success('登录成功')
    
    // 跳转到首页
    router.push('/')
  } catch (error) {
    console.error('登录失败：', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 140px);

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

    .register-link {
      margin-left: 20px;
      color: #409eff;
      font-size: 14px;
    }
  }
}
</style>