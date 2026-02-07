<template>
  <view class="profile-page">
    <!-- 用户信息卡片 -->
    <view class="user-card">
      <view class="user-info" @click="editProfile">
        <image class="avatar" :src="userStore.avatar" mode="aspectFill"></image>
        <view class="user-text">
          <text class="nickname">{{ userStore.displayName }}</text>
          <text class="phone">{{ userStore.userInfo?.phone || '未绑定手机' }}</text>
        </view>
        <text class="edit-icon">›</text>
      </view>
    </view>
    
    <!-- 功能菜单 -->
    <view class="menu-section">
      <view class="menu-item" @click="goTo('/pages/violation/my-violations')">
        <text class="menu-icon">📝</text>
        <text class="menu-text">我的违规</text>
        <text class="menu-arrow">›</text>
      </view>
      
      <view class="menu-item" @click="goTo('/pages/track/index')">
        <text class="menu-icon">🗺️</text>
        <text class="menu-text">轨迹查询</text>
        <text class="menu-arrow">›</text>
      </view>
      
      <view class="menu-item" @click="goTo('/pages/profile/settings')">
        <text class="menu-icon">⚙️</text>
        <text class="menu-text">设置</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>
    
    <!-- 其他选项 -->
    <view class="menu-section">
      <view class="menu-item" @click="showAbout">
        <text class="menu-icon">ℹ️</text>
        <text class="menu-text">关于我们</text>
        <text class="menu-arrow">›</text>
      </view>
      
      <view class="menu-item" @click="showFeedback">
        <text class="menu-icon">💬</text>
        <text class="menu-text">意见反馈</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>
    
    <!-- 退出登录 -->
    <view class="logout-section" v-if="userStore.isLoggedIn">
      <button class="logout-btn" @click="handleLogout">退出登录</button>
    </view>
    
    <!-- 未登录提示 -->
    <view class="login-tip" v-else>
      <button class="login-btn" @click="goToLogin">登录/注册</button>
    </view>
  </view>
</template>

<script setup>
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/stores/user.js'
import { navigateToLogin } from '@/utils/route-guard.js'

const userStore = useUserStore()

// 页面显示时检查登录状态
onShow(() => {
  if (!userStore.isLoggedIn) {
    navigateToLogin()
  }
})

function goTo(url) {
  if (url.includes('track/index')) {
    uni.switchTab({ url })
  } else {
    uni.navigateTo({ url })
  }
}

function editProfile() {
  if (!userStore.isLoggedIn) {
    goToLogin()
    return
  }
  uni.showToast({ title: '编辑资料开发中', icon: 'none' })
}

function showAbout() {
  uni.showModal({
    title: '关于我们',
    content: '校园电动车违规管理系统\n版本: 1.0.0\n\n本项目为电动车校园轨迹检察大学生创新创业项目',
    showCancel: false
  })
}

function showFeedback() {
  uni.showToast({ title: '反馈功能开发中', icon: 'none' })
}

function handleLogout() {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: async (res) => {
      if (res.confirm) {
        await userStore.logout()
        uni.showToast({ title: '已退出登录', icon: 'success' })
      }
    }
  })
}

function goToLogin() {
  uni.navigateTo({ url: '/pages/auth/login' })
}
</script>

<style lang="scss" scoped>
.profile-page {
  min-height: 100vh;
  background: #FAF7F2;
  padding-bottom: calc(120rpx + env(safe-area-inset-bottom));
}

.user-card {
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
  padding: 60rpx 32rpx;
  margin-bottom: 24rpx;
}

.user-info {
  display: flex;
  align-items: center;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  border: 4rpx solid rgba(255, 255, 255, 0.4);
  margin-right: 24rpx;
}

.user-text {
  flex: 1;
}

.nickname {
  font-size: 36rpx;
  font-weight: 600;
  color: #FFFFFF;
  display: block;
  margin-bottom: 8rpx;
}

.phone {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.85);
}

.edit-icon {
  font-size: 40rpx;
  color: rgba(255, 255, 255, 0.85);
}

.menu-section {
  background: #FFFFFF;
  margin: 0 32rpx 24rpx;
  border-radius: 24rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 16rpx rgba(45, 52, 54, 0.06);
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 32rpx;
  border-bottom: 1rpx solid #E8E4DE;
  
  &:last-child {
    border-bottom: none;
  }
}

.menu-icon {
  font-size: 40rpx;
  margin-right: 20rpx;
}

.menu-text {
  flex: 1;
  font-size: 30rpx;
  color: #2D3436;
}

.menu-arrow {
  font-size: 36rpx;
  color: #9BA4A9;
}

.logout-section {
  padding: 48rpx 32rpx;
}

.logout-btn {
  width: 100%;
  height: 88rpx;
  background: #FFFFFF;
  border: 2rpx solid #C9735D;
  border-radius: 20rpx;
  color: #C9735D;
  font-size: 32rpx;
}

.login-tip {
  padding: 48rpx 32rpx;
}

.login-btn {
  width: 100%;
  height: 88rpx;
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
  border-radius: 20rpx;
  color: #FFFFFF;
  font-size: 32rpx;
  border: none;
}
</style>
