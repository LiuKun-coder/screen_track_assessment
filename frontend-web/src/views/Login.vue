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
    
    // 测试账号配置
    const testAccounts = {
      'liukun': {
        password: '123456',
        role: 'admin',
        name: '管理员'
      },
      'kunliu': {
        password: '123456',
        role: 'user',
        name: '普通用户'
      }
    }
    
    const handleLogin = async () => {
      if (!loginForm.username || !loginForm.password) {
        ElMessage.error('请输入用户名和密码')
        return
      }
      
      loginLoading.value = true
      
      try {
        // 模拟登录请求延迟
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        // 验证测试账号
        const account = testAccounts[loginForm.username]
        if (!account) {
          ElMessage.error('用户名不存在')
          return
        }
        
        if (account.password !== loginForm.password) {
          ElMessage.error('密码错误')
          return
        }
        
        // 登录成功，保存用户信息
        const userInfo = {
          username: loginForm.username,
          role: account.role,
          name: account.name,
          loginTime: new Date().toISOString()
        }
        
        localStorage.setItem('userInfo', JSON.stringify(userInfo))
        localStorage.setItem('isLoggedIn', 'true')
        
        if (loginForm.remember) {
          localStorage.setItem('rememberedUser', loginForm.username)
        }
        
        ElMessage.success(`欢迎回来，${account.name}！`)
        
        // 跳转到主页
        router.push('/home')
        
      } catch (error) {
        ElMessage.error('登录失败，请重试')
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
  background: linear-gradient(135deg, #4f8cff 0%, #ffb6c1 100%);
}

.login-box {
  background-color: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  width: 400px;
}

.login-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 10px;
}

.login-subtitle {
  font-size: 14px;
  color: #606266;
  margin-bottom: 20px;
}

.tab-container {
  margin-bottom: 20px;
}

.login-form {
  margin-bottom: 20px;
}

.login-options {
  margin-bottom: 20px;
}

.login-btn {
  width: 100%;
}

.qrcode-container {
  text-align: center;
}

.qrcode-box {
  width: 120px;
  height: 120px;
  margin: 0 auto 10px;
  background-color: #f0f0f0;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.qrcode-tip {
  font-size: 14px;
  color: #606266;
}

.test-accounts {
  background: #f8fafc;
  border: 1px solid #e0e7ff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 20px;
}

.test-accounts h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #4f8cff;
  font-weight: bold;
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
  color: #333;
  min-width: 70px;
}

.account-item span:not(.account-label) {
  color: #666;
  flex: 1;
}

.register-link {
  text-align: center;
  margin-top: 20px;
}
</style> 