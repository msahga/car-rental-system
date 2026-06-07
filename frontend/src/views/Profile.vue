<template>
  <!-- 个人中心页面 -->
  <MainLayout>
    <div class="profile-page">
      <h2 class="page-title">个人中心</h2>

      <!-- 用户信息卡片 -->
      <div class="profile-card">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
          <el-form-item label="用户名">
            <el-input v-model="form.username" disabled />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="真实姓名" prop="realName">
            <el-input v-model="form.realName" placeholder="请输入真实姓名" />
          </el-form-item>
          <el-form-item label="身份证号" prop="idCard">
            <el-input v-model="form.idCard" placeholder="请输入身份证号" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="updating" @click="handleUpdate">
              更新信息
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 修改密码卡片 -->
      <div class="password-card">
        <h3>修改密码</h3>
        <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="100px">
          <el-form-item label="旧密码" prop="oldPassword">
            <el-input
              v-model="passwordForm.oldPassword"
              type="password"
              placeholder="请输入旧密码"
              show-password
            />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input
              v-model="passwordForm.newPassword"
              type="password"
              placeholder="请输入新密码"
              show-password
            />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="passwordForm.confirmPassword"
              type="password"
              placeholder="请确认新密码"
              show-password
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="changingPassword" @click="handleChangePassword">
              修改密码
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
/**
 * 个人中心页面
 * 
 * 功能说明：
 * 1. 用户信息展示和修改
 * 2. 修改密码
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */

import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import MainLayout from '@/layouts/MainLayout.vue'
import { getUserInfo, updateUserInfo, updatePassword } from '@/api/user'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 表单引用
const formRef = ref(null)
const passwordFormRef = ref(null)

// 加载状态
const updating = ref(false)
const changingPassword = ref(false)

// 用户信息表单
const form = reactive({
  username: '',
  phone: '',
  realName: '',
  idCard: ''
})

// 用户信息验证规则
const rules = {
  phone: [
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入正确的手机号',
      trigger: 'blur'
    }
  ],
  idCard: [
    {
      pattern: /^\d{17}[\dXx]$/,
      message: '请输入正确的身份证号',
      trigger: 'blur'
    }
  ]
}

// 修改密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 修改密码验证规则
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

/**
 * 加载用户信息
 */
async function loadUserInfo() {
  try {
    const res = await getUserInfo()
    Object.assign(form, res.data)
  } catch (error) {
    console.error('加载用户信息失败：', error)
  }
}

/**
 * 更新用户信息
 */
async function handleUpdate() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  updating.value = true

  try {
    await updateUserInfo(form)
    ElMessage.success('更新成功')
    
    // 更新本地用户信息
    userStore.updateUserInfo(form)
    
    loadUserInfo()
  } catch (error) {
    console.error('更新用户信息失败：', error)
  } finally {
    updating.value = false
  }
}

/**
 * 修改密码
 */
async function handleChangePassword() {
  const valid = await passwordFormRef.value.validate().catch(() => false)
  if (!valid) return

  changingPassword.value = true

  try {
    await updatePassword(passwordForm.oldPassword, passwordForm.newPassword)
    ElMessage.success('密码修改成功')
    
    // 清空密码表单
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (error) {
    console.error('修改密码失败：', error)
  } finally {
    changingPassword.value = false
  }
}

// 页面加载时获取用户信息
onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped lang="scss">
.profile-page {
  .page-title {
    font-size: 24px;
    color: #303133;
    margin-bottom: 20px;
  }

  .profile-card {
    background-color: #fff;
    border-radius: 8px;
    padding: 20px;
    margin-bottom: 20px;
  }

  .password-card {
    background-color: #fff;
    border-radius: 8px;
    padding: 20px;

    h3 {
      font-size: 18px;
      color: #303133;
      margin-bottom: 20px;
    }
  }
}
</style>