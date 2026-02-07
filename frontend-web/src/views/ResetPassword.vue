<template>
  <div class="login-container">
    <div class="login-box">
      <h1 class="login-title">重置密码</h1>
      <p class="login-subtitle">请验证手机号后重置密码</p>
      
      <el-form class="login-form" :model="resetForm" ref="resetFormRef">
        <el-input
          v-model="resetForm.phone"
          placeholder="请输入手机号"
        >
          <template #prefix>
            <el-icon><Iphone /></el-icon>
          </template>
        </el-input>
        
        <div class="verify-code-container">
          <el-input
            v-model="resetForm.verifyCode"
            placeholder="请输入验证码"
          ></el-input>
          <el-button class="verify-code-btn">获取验证码</el-button>
        </div>
        
        <el-input
          v-model="resetForm.newPassword"
          type="password"
          placeholder="请输入新密码"
          show-password
        >
          <template #prefix>
            <el-icon><Lock /></el-icon>
          </template>
        </el-input>
        
        <el-input
          v-model="resetForm.confirmPassword"
          type="password"
          placeholder="请确认新密码"
          show-password
        >
          <template #prefix>
            <el-icon><Lock /></el-icon>
          </template>
        </el-input>
        
        <el-button type="primary" class="login-btn" @click="handleReset">重置密码</el-button>
      </el-form>
      
      <div class="register-link">
        想起密码了？<router-link to="/">返回登录</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { Lock, Iphone } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'ResetPasswordView',
  components: {
    Lock,
    Iphone
  },
  setup() {
    const resetFormRef = ref(null)
    
    const resetForm = reactive({
      phone: '',
      verifyCode: '',
      newPassword: '',
      confirmPassword: ''
    })
    
    const handleReset = () => {
      if (resetForm.newPassword !== resetForm.confirmPassword) {
        ElMessage.error('两次输入密码不一致')
        return
      }
      console.log('重置密码表单提交', resetForm)
    }
    
    return {
      resetForm,
      resetFormRef,
      handleReset
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #4f8cff 0%, #ffb6c1 100%);
}

.login-box {
  background-color: white;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  width: 360px;
}

.login-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 12px;
}

.login-subtitle {
  font-size: 14px;
  color: #606266;
  margin-bottom: 24px;
}

.login-form {
  margin-bottom: 24px;
}

.verify-code-container {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.verify-code-container .el-input {
  margin-bottom: 0;
}

.verify-code-btn {
  width: 120px;
  background-color: #8ca5ff;
  border: none;
  color: white;
}

.verify-code-btn:hover {
  background-color: #7088ff;
}

.login-btn {
  width: 100%;
  background-color: #8ca5ff;
  border: none;
  color: white;
  padding: 12px;
  font-size: 16px;
  font-weight: bold;
  border-radius: 4px;
  cursor: pointer;
}

.login-btn:hover {
  background-color: #7088ff;
}

.register-link {
  text-align: center;
  font-size: 14px;
  color: #606266;
}

.register-link a {
  color: #8ca5ff;
  text-decoration: none;
}

.register-link a:hover {
  text-decoration: underline;
}
</style> 