<template>
  <view class="reset-page">
    <view class="header">
      <text class="title">重置密码</text>
      <text class="desc">请输入您的手机号，我们将发送验证码</text>
    </view>
    
    <view class="reset-form">
      <view class="form-item">
        <text class="label">手机号</text>
        <input 
          class="input" 
          type="text" 
          v-model="form.phone" 
          placeholder="请输入注册时的手机号"
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
        <text class="label">新密码</text>
        <input 
          class="input" 
          :password="true"
          v-model="form.newPassword" 
          placeholder="请设置6-20位新密码"
        />
      </view>
      
      <view class="form-item">
        <text class="label">确认密码</text>
        <input 
          class="input" 
          :password="true"
          v-model="form.confirmPassword" 
          placeholder="请再次输入新密码"
        />
      </view>
      
      <button 
        class="btn btn-primary mt-48" 
        :class="{ 'btn-disabled': !canSubmit }"
        @click="handleReset"
        :loading="loading"
      >
        重置密码
      </button>
      
      <view class="back-link mt-32" @click="goBack">
        <text>返回登录</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { isValidPhone, isValidCode, validatePassword } from '@/utils/validate.js'
import authApi from '@/api/auth.js'

const form = ref({
  phone: '',
  code: '',
  newPassword: '',
  confirmPassword: ''
})

const loading = ref(false)
const countdown = ref(0)

const canSubmit = computed(() => {
  return isValidPhone(form.value.phone) &&
    isValidCode(form.value.code) &&
    validatePassword(form.value.newPassword).valid &&
    form.value.newPassword === form.value.confirmPassword
})

async function sendCode() {
  if (countdown.value > 0) return
  
  if (!isValidPhone(form.value.phone)) {
    uni.showToast({ title: '请输入正确的手机号', icon: 'none' })
    return
  }
  
  try {
    await authApi.sendCode({ 
      phone: form.value.phone, 
      type: 'reset' 
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

async function handleReset() {
  if (!canSubmit.value || loading.value) return
  
  if (form.value.newPassword !== form.value.confirmPassword) {
    uni.showToast({ title: '两次密码输入不一致', icon: 'none' })
    return
  }
  
  loading.value = true
  
  try {
    await authApi.resetPassword({
      phone: form.value.phone,
      code: form.value.code,
      newPassword: form.value.newPassword
    })
    
    uni.showToast({ title: '密码重置成功', icon: 'success' })
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (error) {
    uni.showToast({ title: error.message || '重置失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

function goBack() {
  uni.navigateBack()
}
</script>

<style lang="scss" scoped>
.reset-page {
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

.reset-form {
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

.back-link {
  text-align: center;
  font-size: 28rpx;
  color: #007AFF;
}
</style>
