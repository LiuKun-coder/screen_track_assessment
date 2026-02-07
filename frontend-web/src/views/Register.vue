<template>
  <div class="login-container">
    <div class="login-box">
      <h1 class="login-title">欢迎加入</h1>
      <p class="login-subtitle">创建您的账号</p>
      
      <el-form class="login-form" :model="registerForm" ref="registerFormRef">
        <el-input
          v-model="registerForm.username"
          placeholder="请输入用户名"
        >
          <template #prefix>
            <el-icon><User /></el-icon>
          </template>
        </el-input>
        
        <el-input
          v-model="registerForm.phone"
          placeholder="请输入手机号"
        >
          <template #prefix>
            <el-icon><Iphone /></el-icon>
          </template>
        </el-input>
        
        <div class="verify-code-container">
          <el-input
            v-model="registerForm.verifyCode"
            placeholder="请输入验证码"
          ></el-input>
          <el-button class="verify-code-btn">获取验证码</el-button>
        </div>
        
        <el-input
          v-model="registerForm.password"
          type="password"
          placeholder="请输入密码"
          show-password
        >
          <template #prefix>
            <el-icon><Lock /></el-icon>
          </template>
        </el-input>
        
        <el-input
          v-model="registerForm.confirmPassword"
          type="password"
          placeholder="请确认密码"
          show-password
        >
          <template #prefix>
            <el-icon><Lock /></el-icon>
          </template>
        </el-input>
        
        <div class="agreement-container">
          <el-checkbox v-model="registerForm.agreement">
            我已阅读并同意 <a href="javascript:;" class="agreement-link">服务条款</a>
          </el-checkbox>
        </div>
        
        <el-button type="primary" class="login-btn" @click="handleRegister">注册</el-button>
      </el-form>
      
      <div class="register-link">
        已有账号？<router-link to="/">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { User, Lock, Iphone } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'RegisterView',
  components: {
    User,
    Lock,
    Iphone
  },
  setup() {
    const registerFormRef = ref(null)
    
    const registerForm = reactive({
      username: '',
      phone: '',
      verifyCode: '',
      password: '',
      confirmPassword: '',
      agreement: false
    })
    
    const handleRegister = () => {
      if (!registerForm.agreement) {
        ElMessage.warning('请先阅读并同意服务条款')
        return
      }
      console.log('注册表单提交', registerForm)
    }
    
    return {
      registerForm,
      registerFormRef,
      handleRegister
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
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  width: 400px;
}

.login-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 16px;
}

.login-subtitle {
  font-size: 16px;
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

.agreement-container {
  margin-bottom: 24px;
}

.agreement-link {
  color: #7088ff;
}
</style> 