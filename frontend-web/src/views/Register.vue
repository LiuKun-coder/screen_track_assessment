<template>
  <div class="login-container">
    <div class="login-box">
      <h1 class="login-title">欢迎加入</h1>
      <p class="login-subtitle">创建您的账号</p>
      
      <el-form class="login-form" :model="registerForm" :rules="rules" ref="registerFormRef">
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入手机号"
          >
            <template #prefix>
              <el-icon><Iphone /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <div class="verify-code-container">
          <el-input
            v-model="registerForm.verifyCode"
            placeholder="请输入验证码"
          ></el-input>
          <el-button class="verify-code-btn" :disabled="codeCooldown > 0" @click="handleSendCode">{{ codeCooldown > 0 ? codeCooldown + 's 后重试' : '获取验证码' }}</el-button>
        </div>
        
        <el-form-item prop="password">
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
        </el-form-item>

        <el-form-item prop="confirmPassword">
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
        </el-form-item>
        
        <div class="agreement-container">
          <el-checkbox v-model="registerForm.agreement">
            我已阅读并同意 <a href="javascript:;" class="agreement-link">服务条款</a>
          </el-checkbox>
        </div>
        
        <el-button type="primary" class="login-btn" :loading="submitting" @click="handleRegister">注册</el-button>
      </el-form>
      
      <div class="register-link">
        已有账号？<router-link to="/">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock, Iphone } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { register, sendCode } from '@/api/auth'

export default {
  name: 'RegisterView',
  components: {
    User,
    Lock,
    Iphone
  },
  setup() {
    const router = useRouter()
    const registerFormRef = ref(null)
    const submitting = ref(false)
    
    const registerForm = reactive({
      username: '',
      phone: '',
      verifyCode: '',
      password: '',
      confirmPassword: '',
      agreement: false
    })

    const codeCooldown = ref(0)
    let cooldownTimer = null

    const handleSendCode = async () => {
      if (codeCooldown.value > 0) return
      if (!/^1[3-9]\d{9}$/.test(registerForm.phone)) {
        ElMessage.warning('请先输入正确的手机号')
        return
      }
      try {
        await sendCode({ phone: registerForm.phone, type: 'register' })
        ElMessage.success('验证码已发送')
        codeCooldown.value = 60
        cooldownTimer = setInterval(() => {
          codeCooldown.value--
          if (codeCooldown.value <= 0) clearInterval(cooldownTimer)
        }, 1000)
      } catch (error) {
        ElMessage.error(error?.message || '验证码发送失败')
      }
    }

    const validatePhone = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入手机号'))
        return
      }
      if (!/^1[3-9]\d{9}$/.test(value)) {
        callback(new Error('手机号格式不正确'))
        return
      }
      callback()
    }

    const validateConfirmPassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请确认密码'))
        return
      }
      if (value !== registerForm.password) {
        callback(new Error('两次输入密码不一致'))
        return
      }
      callback()
    }

    const rules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 50, message: '用户名长度需在 3-50 位之间', trigger: 'blur' }
      ],
      phone: [
        { validator: validatePhone, trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码至少 6 位', trigger: 'blur' }
      ],
      confirmPassword: [
        { validator: validateConfirmPassword, trigger: 'blur' }
      ]
    }
    
    const handleRegister = async () => {
      try {
        if (!registerForm.agreement) {
          ElMessage.warning('请先阅读并同意服务条款')
          return
        }

        await registerFormRef.value?.validate()

        submitting.value = true
        await register({
          username: registerForm.username,
          password: registerForm.password,
          phone: registerForm.phone,
          verifyCode: registerForm.verifyCode,
          name: registerForm.username,
          userType: 'student'
        })
        ElMessage.success('注册成功，请登录')
        router.push('/')
      } catch (error) {
        if (error && error.message) {
          return
        }
      } finally {
        submitting.value = false
      }
    }
    
    return {
      registerForm,
      rules,
      submitting,
      registerFormRef,
      codeCooldown,
      handleRegister,
      handleSendCode
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

.agreement-container {
  margin-bottom: 24px;
}

.agreement-link {
  color: #6B9AC4;
  text-decoration: none;
}

.agreement-link:hover {
  text-decoration: underline;
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
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.login-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(107, 154, 196, 0.35);
}

.register-link {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: #636E72;
}

.register-link a {
  color: #C9735D;
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

:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #7D9E87;
  border-color: #7D9E87;
}
</style> 