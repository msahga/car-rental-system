<template>
  <!-- 用户注册页面 -->
  <MainLayout>
    <div class="register-page">
      <div class="register-card">
        <h2>用户注册</h2>
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
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="请确认密码"
              show-password
            />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="handleRegister">
              注册
            </el-button>
            <router-link to="/login" class="login-link">
              已有账号？立即登录
            </router-link>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
/**
 * 用户注册页面
 * 
 * 功能说明：
 * 1. 用户注册表单
 * 2. 表单验证（用户名、密码、确认密码、手机号）
 * 3. 注册成功后跳转到登录页
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */

import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import MainLayout from '@/layouts/MainLayout.vue'
import { register } from '@/api/user'

const router = useRouter()

// 表单引用
const formRef = ref(null)

// 加载状态
const loading = ref(false)

// 表单数据
const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  phone: ''
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度3-20位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== form.password) {
          callback(new Error('两次密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  phone: [
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入正确的手机号',
      trigger: 'blur'
    }
  ]
}

/**
 * 处理注册
 */
async function handleRegister() {
  // 表单验证
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true

  try {
    // 调用注册接口
    await register(form.username, form.password, form.phone)
    
    ElMessage.success('注册成功，请登录')
    
    // 跳转到登录页
    router.push('/login')
  } catch (error) {
    console.error('注册失败：', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.register-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 140px);

  .register-card {
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

    .login-link {
      margin-left: 20px;
      color: #409eff;
      font-size: 14px;
    }
  }
}
</style>