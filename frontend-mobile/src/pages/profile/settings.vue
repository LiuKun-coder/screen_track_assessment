<template>
  <view class="settings-page">
    <view class="menu-section">
      <view class="menu-item" @click="toggleNotification">
        <text class="menu-text">消息通知</text>
        <switch :checked="notificationEnabled" color="#007AFF" />
      </view>
      
      <view class="menu-item" @click="clearCache">
        <text class="menu-text">清除缓存</text>
        <text class="menu-value">{{ cacheSize }}</text>
      </view>
    </view>
    
    <view class="menu-section">
      <view class="menu-item" @click="showPrivacy">
        <text class="menu-text">隐私政策</text>
        <text class="menu-arrow">›</text>
      </view>
      
      <view class="menu-item" @click="showUserAgreement">
        <text class="menu-text">用户协议</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>
    
    <view class="version-info">
      <text>版本 1.0.0</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const notificationEnabled = ref(true)
const cacheSize = ref('0 KB')

function toggleNotification() {
  notificationEnabled.value = !notificationEnabled.value
}

function clearCache() {
  uni.showModal({
    title: '提示',
    content: '确定清除缓存吗？',
    success: (res) => {
      if (res.confirm) {
        try {
          uni.clearStorageSync()
          cacheSize.value = '0 KB'
          uni.showToast({ title: '缓存已清除', icon: 'success' })
        } catch (error) {
          uni.showToast({ title: '清除失败', icon: 'none' })
        }
      }
    }
  })
}

function showPrivacy() {
  uni.navigateTo({ url: '/pages/profile/privacy' })
}

function showUserAgreement() {
  uni.navigateTo({ url: '/pages/profile/user-agreement' })
}

function calculateCacheSize() {
  try {
    const info = uni.getStorageInfoSync()
    const sizeKB = info.currentSize || 0
    if (sizeKB < 1024) {
      cacheSize.value = sizeKB + ' KB'
    } else {
      cacheSize.value = (sizeKB / 1024).toFixed(2) + ' MB'
    }
  } catch (error) {
    cacheSize.value = '未知'
  }
}

onMounted(() => {
  calculateCacheSize()
})
</script>

<style lang="scss" scoped>
.settings-page {
  min-height: 100vh;
  background: #FAF7F2;
  padding: 24rpx 32rpx;
}

.menu-section {
  background: #FFFFFF;
  border-radius: 24rpx;
  margin-bottom: 24rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 16rpx rgba(45, 52, 54, 0.06);
}

.menu-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx;
  border-bottom: 1rpx solid #E8E4DE;
  
  &:last-child {
    border-bottom: none;
  }
}

.menu-text {
  font-size: 30rpx;
  color: #2D3436;
}

.menu-value {
  font-size: 28rpx;
  color: #9BA4A9;
}

.menu-arrow {
  font-size: 36rpx;
  color: #9BA4A9;
}

.version-info {
  text-align: center;
  padding: 48rpx;
  font-size: 26rpx;
  color: #9BA4A9;
}
</style>
