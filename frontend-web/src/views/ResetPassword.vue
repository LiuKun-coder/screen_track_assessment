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
  background: #FAF7F2;
}

.login-box {
  background-color: #FFFFFF;
  padding: 40px;
  border-radius: 20px;
  box-shadow: 0 4px 24px rgba(45, 52, 54, 0.06);
  width: 400px;
}

.login-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #2D3436;
  text-align: center;
}

.login-subtitle {
  font-size: 14px;
  color: #636E72;
  margin-bottom: 32px;
  text-align: center;
}

.login-form {
  margin-bottom: 24px;
}

.verify-code-container {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.verify-code-container .el-input {
  margin-bottom: 0;
}

.verify-code-btn {
  width: 120px;
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
  border: none;
  color: white;
  border-radius: 10px;
  font-size: 14px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.verify-code-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(107, 154, 196, 0.35);
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 10px;
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
  border: none;
  color: white;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.login-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(107, 154, 196, 0.35);
}

.register-link {
  text-align: center;
  font-size: 14px;
  color: #636E72;
}

.register-link a {
  color: #6B9AC4;
  text-decoration: none;
  font-weight: 500;
}

.register-link a:hover {
  text-decoration: underline;
}

:deep(.el-input .el-input__wrapper) {
  border-radius: 10px;
  background-color: #FAF7F2;
  border: 1px solid #E8E4DE;
  box-shadow: none !important;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

:deep(.el-input .el-input__wrapper:hover) {
  border-color: #6B9AC4;
}

:deep(.el-input .el-input__wrapper.is-focus) {
  border-color: #6B9AC4;
  box-shadow: 0 0 0 3px rgba(107, 154, 196, 0.15) !important;
}

:deep(.el-input .el-input__inner) {
  height: 48px;
  font-size: 15px;
  color: #2D3436;
}
</style> 