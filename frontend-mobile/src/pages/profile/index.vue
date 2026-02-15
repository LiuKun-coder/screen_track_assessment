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

    <view v-if="showEditPopup" class="popup-mask" @click="closeEditPopup">
      <view class="popup-card" @click.stop>
        <view class="popup-header">
          <text class="popup-title">资料编辑</text>
          <text class="popup-close" @click="closeEditPopup">×</text>
        </view>

        <view class="popup-body">
          <view class="avatar-row">
            <image class="avatar large" :src="userStore.avatar" mode="aspectFill"></image>
            <view class="avatar-action">
              <text class="avatar-label">头像</text>
              <button class="btn btn-outline small">更换头像</button>
            </view>
          </view>

          <view class="form-item">
            <text class="form-label">用户名</text>
            <input class="input" type="text" v-model="editForm.name" placeholder="请输入用户名" />
          </view>

          <view class="form-item">
            <text class="form-label">绑定手机号</text>
            <input class="input" type="number" v-model="editForm.phone" placeholder="请输入手机号" maxlength="11" />
          </view>
        </view>

        <view class="popup-footer">
          <button class="btn btn-outline" @click="closeEditPopup">取消</button>
          <button class="btn btn-primary" :class="{ 'btn-disabled': !hasChanges || saving }" @click="saveProfile">保存</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/stores/user.js'
import { navigateToLogin } from '@/utils/route-guard.js'
import authApi from '@/api/auth.js'

const userStore = useUserStore()
const showEditPopup = ref(false)
const saving = ref(false)

const editForm = ref({
  name: '',
  phone: ''
})

const hasChanges = computed(() => {
  const currentName = userStore.userInfo?.name || userStore.userInfo?.nickname || ''
  const currentPhone = userStore.userInfo?.phone || ''
  return editForm.value.name !== currentName || editForm.value.phone !== currentPhone
})

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
  editForm.value = {
    name: userStore.userInfo?.name || userStore.userInfo?.nickname || '',
    phone: userStore.userInfo?.phone || ''
  }
  showEditPopup.value = true
}

function closeEditPopup() {
  if (saving.value) return
  showEditPopup.value = false
}

async function saveProfile() {
  if (!hasChanges.value || saving.value) return
  if (editForm.value.phone && !/^1[3-9]\d{9}$/.test(editForm.value.phone)) {
    uni.showToast({ title: '手机号格式不正确', icon: 'none' })
    return
  }

  saving.value = true
  try {
    await authApi.updateProfile({
      name: editForm.value.name,
      phone: editForm.value.phone
    })
    userStore.updateUserInfo({
      name: editForm.value.name,
      nickname: editForm.value.name,
      phone: editForm.value.phone
    })
    uni.showToast({ title: '保存成功', icon: 'success' })
    showEditPopup.value = false
  } catch (error) {
    uni.showToast({ title: error.message || '保存失败', icon: 'none' })
  } finally {
    saving.value = false
  }
}

function showAbout() {
  uni.showModal({
    title: '关于我们',
    content: '校园电动车违规管理系统\n版本: 1.0.0\n\n本项目为电动车校园轨迹检察大学生创新创业项目',
    showCancel: false
  })
}

function showFeedback() {
  uni.navigateTo({ url: '/pages/profile/feedback' })
}

function handleLogout() {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: async (res) => {
      if (res.confirm) {
        await userStore.logout()
        uni.showToast({ title: '已退出登录', icon: 'success' })
        setTimeout(() => {
          uni.reLaunch({ url: '/pages/auth/login' })
        }, 500)
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

.popup-mask {
  position: fixed;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.35);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  padding: 24rpx;
  z-index: 999;
}

.popup-card {
  width: 100%;
  background: #FFFFFF;
  border-radius: 28rpx;
  box-shadow: 0 12rpx 32rpx rgba(45, 52, 54, 0.16);
  overflow: hidden;
}

.popup-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28rpx 32rpx;
  border-bottom: 1rpx solid #E8E4DE;
}

.popup-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #2D3436;
}

.popup-close {
  font-size: 44rpx;
  color: #9BA4A9;
}

.popup-body {
  padding: 32rpx;
}

.avatar-row {
  display: flex;
  align-items: center;
  margin-bottom: 32rpx;
}

.avatar.large {
  width: 140rpx;
  height: 140rpx;
  border-radius: 50%;
  border: 4rpx solid rgba(125, 158, 135, 0.25);
  margin-right: 24rpx;
}

.avatar-action {
  flex: 1;
}

.avatar-label {
  display: block;
  font-size: 28rpx;
  color: #2D3436;
  margin-bottom: 16rpx;
}

.form-item {
  margin-bottom: 24rpx;
}

.form-label {
  display: block;
  font-size: 28rpx;
  color: #2D3436;
  margin-bottom: 16rpx;
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
}

.popup-footer {
  display: flex;
  gap: 20rpx;
  padding: 24rpx 32rpx 32rpx;
}

.btn.small {
  height: 72rpx;
  padding: 0 32rpx;
  font-size: 28rpx;
  border-radius: 18rpx;
}
</style>
