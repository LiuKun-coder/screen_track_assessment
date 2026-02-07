<template>
  <view class="register-page">
    <!-- 头部 -->
    <view class="header">
      <text class="title">注册账号</text>
      <text class="desc">创建您的校园电动车管理账号</text>
    </view>
    
    <!-- 注册表单 -->
    <view class="register-form">
      <view class="form-item">
        <text class="label">手机号</text>
        <input 
          class="input" 
          type="text" 
          v-model="form.phone" 
          placeholder="请输入手机号"
          maxlength="11"
        />
      </view>
      
      <view class="form-item">
        <text class="label">验证码</text>
        <view class="input-group">
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
            {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
          </view>
        </view>
      </view>
      
      <view class="form-item">
        <text class="label">昵称</text>
        <input 
          class="input" 
          type="text" 
          v-model="form.nickname" 
          placeholder="请输入昵称"
          maxlength="20"
        />
      </view>
      
      <view class="form-item">
        <text class="label">密码</text>
        <input 
          class="input" 
          :password="true"
          v-model="form.password" 
          placeholder="请设置6-20位密码"
        />
      </view>
      
      <view class="form-item">
        <text class="label">确认密码</text>
        <input 
          class="input" 
          :password="true"
          v-model="form.confirmPassword" 
          placeholder="请再次输入密码"
        />
      </view>
      
      <!-- 协议 -->
      <view class="agreement">
        <view class="checkbox" :class="{ checked: agreed }" @click="agreed = !agreed">
          <text v-if="agreed">✓</text>
        </view>
        <text class="text">我已阅读并同意</text>
        <text class="link">《用户服务协议》</text>
        <text class="text">和</text>
        <text class="link">《隐私政策》</text>
      </view>
      
      <!-- 注册按钮 -->
      <button 
        class="btn btn-primary mt-48" 
        :class="{ 'btn-disabled': !canSubmit }"
        @click="handleRegister"
        :loading="loading"
      >
        注册
      </button>
      
      <!-- 登录链接 -->
      <view class="login-link mt-32">
        <text>已有账号？</text>
        <text class="link" @click="goToLogin">立即登录</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useUserStore } from '@/stores/user.js'
import { isValidPhone, isValidCode, validatePassword } from '@/utils/validate.js'
import authApi from '@/api/auth.js'

const userStore = useUserStore()

// 表单数据
const form = ref({
  phone: '',
  code: '',
  nickname: '',
  password: '',
  confirmPassword: ''
})

// 状态
const loading = ref(false)
const agreed = ref(false)
const countdown = ref(0)

// 是否可提交
const canSubmit = computed(() => {
  return isValidPhone(form.value.phone) &&
    isValidCode(form.value.code) &&
    form.value.nickname.length >= 2 &&
    validatePassword(form.value.password).valid &&
    form.value.password === form.value.confirmPassword &&
    agreed.value
})

// 发送验证码
async function sendCode() {
  if (countdown.value > 0) return
  
  if (!isValidPhone(form.value.phone)) {
    uni.showToast({ title: '请输入正确的手机号', icon: 'none' })
    return
  }
  
  try {
    await authApi.sendCode({ 
      phone: form.value.phone, 
      type: 'register' 
    })
    
    uni.showToast({ title: '验证码已发送', icon: 'success' })
    
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

// 注册
async function handleRegister() {
  if (!canSubmit.value || loading.value) return
  
  // 验证密码
  const passwordResult = validatePassword(form.value.password)
  if (!passwordResult.valid) {
    uni.showToast({ title: passwordResult.message, icon: 'none' })
    return
  }
  
  if (form.value.password !== form.value.confirmPassword) {
    uni.showToast({ title: '两次密码输入不一致', icon: 'none' })
    return
  }
  
  loading.value = true
  
  try {
    const result = await userStore.register({
      phone: form.value.phone,
      code: form.value.code,
      nickname: form.value.nickname,
      password: form.value.password
    })
    
    if (result.success) {
      uni.showToast({ title: '注册成功', icon: 'success' })
      setTimeout(() => {
        uni.switchTab({ url: '/pages/home/index' })
      }, 1500)
    } else {
      uni.showToast({ title: result.error || '注册失败', icon: 'none' })
    }
  } catch (error) {
    uni.showToast({ title: error.message || '注册失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

// 跳转登录
function goToLogin() {
  uni.navigateBack()
}
</script>

<style lang="scss" scoped>
.register-page {
  min-height: 100vh;
  background: #F5F5F5;
  padding: 0 32rpx;
}

.header {
  padding: 60rpx 0 40rpx;
}

.title {
  font-size: 48rpx;
  font-weight: 600;
  color: #333333;
  display: block;
  margin-bottom: 12rpx;
}

.desc {
  font-size: 28rpx;
  color: #666666;
}

.register-form {
  background: #FFFFFF;
  border-radius: 24rpx;
  padding: 40rpx 32rpx;
}

.form-item {
  margin-bottom: 32rpx;
}

.label {
  font-size: 28rpx;
  color: #333333;
  margin-bottom: 16rpx;
  display: block;
}

.input {
  width: 100%;
  height: 96rpx;
  border: 2rpx solid #E5E5E5;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 32rpx;
  box-sizing: border-box;
}

.input-group {
  display: flex;
  gap: 16rpx;
}

.code-input {
  flex: 1;
}

.code-btn {
  width: 200rpx;
  height: 96rpx;
  background: #007AFF;
  border-radius: 12rpx;
  color: #FFFFFF;
  font-size: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  
  &.disabled {
    background: #CCCCCC;
  }
}

.agreement {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  margin-top: 24rpx;
}

.checkbox {
  width: 36rpx;
  height: 36rpx;
  border: 2rpx solid #CCCCCC;
  border-radius: 6rpx;
  margin-right: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  color: #FFFFFF;
  
  &.checked {
    background: #007AFF;
    border-color: #007AFF;
  }
}

.text {
  font-size: 24rpx;
  color: #666666;
}

.link {
  font-size: 24rpx;
  color: #007AFF;
}

.login-link {
  text-align: center;
  font-size: 28rpx;
  color: #666666;
  
  .link {
    font-size: 28rpx;
  }
}
</style>
