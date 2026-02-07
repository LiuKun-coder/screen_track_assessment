<template>
  <div class="login-container">
    <div class="login-box">
      <h1 class="login-title">欢迎回来</h1>
      <p class="login-subtitle">请登录您的账号</p>
      
      <div class="tab-container">
        <el-tabs v-model="activeTab" stretch>
          <el-tab-pane label="账号登录" name="account">
            <el-form class="login-form" :model="loginForm" ref="loginFormRef">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
              
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                show-password
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
              
              <div class="login-options">
                <el-checkbox v-model="loginForm.remember">记住我</el-checkbox>
                <router-link to="/reset-password">忘记密码?</router-link>
              </div>
              
              <el-button type="primary" class="login-btn" @click="handleLogin" :loading="loginLoading">
                {{ loginLoading ? '登录中...' : '登录' }}
              </el-button>
            </el-form>
          </el-tab-pane>
          
          <el-tab-pane label="扫码登录" name="qrcode">
            <div class="qrcode-container">
              <div class="qrcode-box"></div>
              <p class="qrcode-tip">请使用微信扫描二维码登录</p>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
      
      <!-- 测试账号提示 -->
      <div class="test-accounts">
        <h4>测试账号：</h4>
        <div class="account-info">
          <div class="account-item">
            <span class="account-label">管理员：</span>
            <span>liukun / 123456</span>
            <el-button size="small" @click="fillAccount('liukun', '123456')">快速填入</el-button>
          </div>
          <div class="account-item">
            <span class="account-label">普通用户：</span>
            <span>kunliu / 123456</span>
            <el-button size="small" @click="fillAccount('kunliu', '123456')">快速填入</el-button>
          </div>
        </div>
      </div>
      
      <div class="register-link">
        还没有账号？<router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { User, Lock } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth'

export default {
  name: 'LoginView',
  components: {
    User,
    Lock
  },
  setup() {
    const router = useRouter()
    const activeTab = ref('account')
    const loginFormRef = ref(null)
    const loginLoading = ref(false)
    
    const loginForm = reactive({
      username: '',
      password: '',
      remember: false
    })
    
    const handleLogin = async () => {
      if (!loginForm.username || !loginForm.password) {
        ElMessage.error('请输入用户名和密码')
        return
      }
      
      loginLoading.value = true
      
      try {
        // 调用后端登录API
        const result = await login({
          username: loginForm.username,
          password: loginForm.password
        })
        
        // 保存token和用户信息
        localStorage.setItem('token', result.token)
        localStorage.setItem('userInfo', JSON.stringify(result.userInfo))
        localStorage.setItem('isLoggedIn', 'true')
        
        if (loginForm.remember) {
          localStorage.setItem('rememberedUser', loginForm.username)
        }
        
        ElMessage.success(`欢迎回来，${result.userInfo.name}！`)
        
        // 跳转到主页
        router.push('/home')
        
      } catch (error) {
        console.error('登录失败:', error)
        // 错误已在request拦截器中处理
      } finally {
        loginLoading.value = false
      }
    }
    
    const fillAccount = (username, password) => {
      loginForm.username = username
      loginForm.password = password
    }
    
    return {
      activeTab,
      loginForm,
      loginFormRef,
      loginLoading,
      handleLogin,
      fillAccount
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

.tab-container {
  margin-bottom: 24px;
}

.login-form {
  margin-bottom: 20px;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
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

.qrcode-container {
  text-align: center;
  padding: 40px 0;
}

.qrcode-box {
  width: 180px;
  height: 180px;
  margin: 0 auto 20px;
  background-color: #FAF7F2;
  border-radius: 12px;
  border: 1px solid #E8E4DE;
  display: flex;
  align-items: center;
  justify-content: center;
}

.qrcode-tip {
  font-size: 14px;
  color: #636E72;
}

.test-accounts {
  background: #FFFCF8;
  border: 1px solid #E8E4DE;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 20px;
}

.test-accounts h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #7D9E87;
  font-weight: 600;
}

.account-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.account-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.account-label {
  font-weight: 500;
  color: #2D3436;
  min-width: 70px;
}

.account-item span:not(.account-label) {
  color: #636E72;
  flex: 1;
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

:deep(.el-input .el-input__inner::placeholder) {
  color: #9BA4A9;
}

:deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background-color: #E8E4DE;
}

:deep(.el-tabs__active-bar) {
  height: 2px;
  background: linear-gradient(90deg, #7D9E87 0%, #6B9AC4 100%);
}

:deep(.el-tabs__item) {
  font-size: 15px;
  color: #636E72;
}

:deep(.el-tabs__item.is-active) {
  color: #2D3436;
  font-weight: 500;
}

:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #7D9E87;
  border-color: #7D9E87;
}

:deep(.el-button--small) {
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
  border: none;
  color: white;
  border-radius: 6px;
  font-size: 12px;
}

:deep(.el-button--small:hover) {
  opacity: 0.9;
}
</style> 