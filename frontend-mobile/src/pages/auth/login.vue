<template>
  <view class="login-page">
    <!-- Logo区域 -->
    <view class="logo-section">
      <image class="logo" src="/static/images/logo.png" mode="aspectFit"></image>
      <text class="app-name">校园电动车管理系统</text>
      <text class="app-desc">安全出行，文明校园</text>
    </view>
    
    <!-- 登录表单 -->
    <view class="login-form">
      <!-- Tab切换 -->
      <view class="login-tabs">
        <view 
          class="tab-item" 
          :class="{ active: loginType === 'password' }"
          @click="loginType = 'password'"
        >
          密码登录
        </view>
        <view 
          class="tab-item" 
          :class="{ active: loginType === 'code' }"
          @click="loginType = 'code'"
        >
          验证码登录
        </view>
      </view>
      
      <!-- 用户名输入 -->
      <view class="form-item">
        <input 
          class="input" 
          type="text" 
          v-model="form.username" 
          placeholder="请输入用户名"
        />
      </view>
      
      <!-- 密码输入 -->
      <view class="form-item" v-if="loginType === 'password'">
        <input 
          class="input" 
          :password="!showPassword"
          v-model="form.password" 
          placeholder="请输入密码"
        />
        <view class="input-suffix" @click="showPassword = !showPassword">
          <text>{{ showPassword ? '🙈' : '👁️' }}</text>
        </view>
      </view>
      
      <!-- 验证码输入 -->
      <view class="form-item" v-else>
        <input 
          class="input code-input" 
          type="number"
          v-model="form.code" 
          placeholder="请输入验证码"
          maxlength="6"
        />
        <view 
          class="code-btn" 
          :class="{ disabled: countdown > 0 }"
          @click="sendCode"
        >
          {{ countdown > 0 ? `${countdown}s后重发` : '获取验证码' }}
        </view>
      </view>
      
      <!-- 登录按钮 -->
      <view class="form-item mt-48">
        <button 
          class="btn btn-primary" 
          :class="{ 'btn-disabled': !canSubmit }"
          @click="handleLogin"
          :loading="loading"
        >
          登录
        </button>
      </view>
      
      <!-- 微信登录 -->
      <view class="wx-login mt-32">
        <view class="divider-text">
          <text class="line"></text>
          <text class="text">其他登录方式</text>
          <text class="line"></text>
        </view>
        <button class="wx-btn" open-type="getPhoneNumber" @click="handleWxLogin">
          <text class="wx-icon">💬</text>
          <text>微信一键登录</text>
        </button>
      </view>
    </view>
    
    <!-- 底部链接 -->
    <view class="bottom-links">
      <text class="link" @click="goToRegister">注册账号</text>
      <text class="separator">|</text>
      <text class="link" @click="goToResetPassword">忘记密码</text>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user.js'
import { isValidPhone, isValidCode } from '@/utils/validate.js'
import authApi from '@/api/auth.js'

const userStore = useUserStore()

// 登录类型
const loginType = ref('password')

// 登录成功后要跳转的页面
const redirectUrl = ref('')

// 获取 URL 参数
onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const options = currentPage?.options || {}
  if (options.redirect) {
    redirectUrl.value = decodeURIComponent(options.redirect)
  }
})

// 表单数据
const form = ref({
  username: '',
  phone: '',
  password: '',
  code: ''
})

// 状态
const loading = ref(false)
const showPassword = ref(false)
const countdown = ref(0)

// 是否可提交
const canSubmit = computed(() => {
  if (!form.value.username || form.value.username.length < 3) return false
  if (loginType.value === 'password') {
    return form.value.password.length >= 6
  }
  return isValidCode(form.value.code)
})

// 发送验证码
async function sendCode() {
  if (countdown.value > 0) return

  const phone = form.value.phone || form.value.username
  if (!isValidPhone(phone)) {
    uni.showToast({ title: '请输入正确的手机号', icon: 'none' })
    return
  }

  try {
    await authApi.sendCode({ 
      phone,
      type: 'login' 
    })
    
    uni.showToast({ title: '验证码已发送', icon: 'success' })
    
    // 开始倒计时
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (error) {
    uni.showToast({ title: error.message || '发送失败', icon: 'none' })
  }
}

// 登录
async function handleLogin() {
  if (!canSubmit.value || loading.value) return

  if (loginType.value === 'code') {
    uni.showToast({ title: '验证码登录暂未开通，请使用密码登录', icon: 'none' })
    return
  }
  
  loading.value = true
  
  try {
    const result = await userStore.loginByPassword(form.value.username, form.value.password)
    
    if (result.success) {
      uni.showToast({ title: '登录成功', icon: 'success' })
      setTimeout(() => {
        // 如果有 redirect 参数,跳转到指定页面
        if (redirectUrl.value) {
          uni.reLaunch({ url: redirectUrl.value })
        } else {
          // 否则跳转到首页
          uni.switchTab({ url: '/pages/home/index' })
        }
      }, 1500)
    } else {
      uni.showToast({ title: result.error || '登录失败', icon: 'none' })
    }
  } catch (error) {
    uni.showToast({ title: error.message || '登录失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

// 微信登录
async function handleWxLogin() {
  loading.value = true
  
  try {
    const result = await userStore.loginByWechat()
    
    if (result.success) {
      uni.showToast({ title: '登录成功', icon: 'success' })
      setTimeout(() => {
        // 如果有 redirect 参数,跳转到指定页面
        if (redirectUrl.value) {
          uni.reLaunch({ url: redirectUrl.value })
        } else {
          // 否则跳转到首页
          uni.switchTab({ url: '/pages/home/index' })
        }
      }, 1500)
    } else {
      uni.showToast({ title: result.error || '登录失败', icon: 'none' })
    }
  } catch (error) {
    uni.showToast({ title: error.message || '登录失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

// 跳转注册
function goToRegister() {
  uni.navigateTo({ url: '/pages/auth/register' })
}

// 跳转重置密码
function goToResetPassword() {
  uni.navigateTo({ url: '/pages/auth/reset-password' })
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  background: #FAF7F2;
  padding: 0 48rpx;
  display: flex;
  flex-direction: column;
}

.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 0 60rpx;
}

.logo {
  width: 160rpx;
  height: 160rpx;
  margin-bottom: 24rpx;
}

.app-name {
  font-size: 40rpx;
  font-weight: 600;
  color: #2D3436;
  margin-bottom: 12rpx;
}

.app-desc {
  font-size: 26rpx;
  color: #636E72;
}

.login-form {
  background: #FFFFFF;
  border-radius: 32rpx;
  padding: 48rpx 32rpx;
  box-shadow: 0 8rpx 32rpx rgba(45, 52, 54, 0.08);
}

.login-tabs {
  display: flex;
  margin-bottom: 40rpx;
}

.tab-item {
  flex: 1;
  text-align: center;
  font-size: 30rpx;
  color: #9BA4A9;
  padding-bottom: 20rpx;
  border-bottom: 4rpx solid transparent;
  
  &.active {
    color: #7D9E87;
    font-weight: 500;
    border-bottom-color: #7D9E87;
  }
}

.form-item {
  position: relative;
  margin-bottom: 24rpx;
}

.input {
  width: 100%;
  height: 96rpx;
  border: 2rpx solid #E8E4DE;
  border-radius: 20rpx;
  padding: 0 24rpx;
  font-size: 30rpx;
  box-sizing: border-box;
  background: #FAF7F2;
  
  &:focus {
    border-color: #6B9AC4;
    background: #FFFFFF;
  }
}

.input-suffix {
  position: absolute;
  right: 24rpx;
  top: 50%;
  transform: translateY(-50%);
  font-size: 36rpx;
}

.code-input {
  padding-right: 220rpx;
}

.code-btn {
  position: absolute;
  right: 24rpx;
  top: 50%;
  transform: translateY(-50%);
  font-size: 26rpx;
  color: #6B9AC4;
  
  &.disabled {
    color: #9BA4A9;
  }
}

.wx-login {
  margin-top: 48rpx;
}

.divider-text {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 32rpx;
  
  .line {
    width: 80rpx;
    height: 1rpx;
    background: #E8E4DE;
  }
  
  .text {
    margin: 0 24rpx;
    font-size: 26rpx;
    color: #9BA4A9;
  }
}

.wx-btn {
  width: 100%;
  height: 88rpx;
  background: #07C160;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FFFFFF;
  font-size: 32rpx;
  border: none;
}

.wx-icon {
  margin-right: 12rpx;
  font-size: 36rpx;
}

.bottom-links {
  margin-top: auto;
  padding: 48rpx 0;
  display: flex;
  justify-content: center;
  align-items: center;
}

.link {
  font-size: 28rpx;
  color: #6B9AC4;
}

.separator {
  margin: 0 24rpx;
  color: #E8E4DE;
}
</style>
