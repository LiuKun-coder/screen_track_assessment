<template>
  <view class="home-page">
    <!-- 头部背景 -->
    <view class="header-bg">
      <view class="header-content">
        <view class="user-info" @click="goToProfile">
          <image class="avatar" :src="userStore.avatar" mode="aspectFill"></image>
          <view class="user-text">
            <text class="greeting">{{ greeting }}</text>
            <text class="username">{{ userStore.displayName }}</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 统计卡片 -->
    <view class="stats-card">
      <view class="stat-item">
        <text class="stat-value">{{ stats.totalViolations }}</text>
        <text class="stat-label">总违规数</text>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item">
        <text class="stat-value">{{ stats.myViolations }}</text>
        <text class="stat-label">我的违规</text>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item">
        <text class="stat-value text-warning">{{ stats.pending }}</text>
        <text class="stat-label">待处理</text>
      </view>
    </view>
    
    <!-- 快捷入口 -->
    <view class="quick-entry">
      <view class="entry-title">快捷服务</view>
      <view class="entry-grid">
        <view class="entry-item" @click="goTo('/pages/violation/report')">
          <view class="entry-icon violation">📋</view>
          <text class="entry-text">违规通报</text>
        </view>
        <view class="entry-item" @click="goTo('/pages/violation/my-violations')">
          <view class="entry-icon my">📝</view>
          <text class="entry-text">我的违规</text>
        </view>
        <view class="entry-item" @click="goTo('/pages/track/index')">
          <view class="entry-icon track">🗺️</view>
          <text class="entry-text">轨迹查询</text>
        </view>
        <view class="entry-item" @click="goTo('/pages/track/fence')">
          <view class="entry-icon fence">🔒</view>
          <text class="entry-text">电子围栏</text>
        </view>
      </view>
    </view>
    
    <!-- 最新通报 -->
    <view class="latest-section">
      <view class="section-header">
        <text class="section-title">最新通报</text>
        <text class="section-more" @click="goTo('/pages/violation/report')">查看更多 ›</text>
      </view>
      
      <view class="violation-list" v-if="latestViolations.length">
        <violation-card 
          v-for="item in latestViolations" 
          :key="item.id"
          :violation="item"
          @click="goToDetail"
        />
      </view>
      
      <view class="empty-state" v-else>
        <text class="empty-text">暂无违规通报</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user.js'
import violationApi from '@/api/violation.js'

const userStore = useUserStore()

// 统计数据
const stats = ref({
  totalViolations: 0,
  myViolations: 0,
  pending: 0
})

// 最新违规
const latestViolations = ref([])

// 问候语
const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '凌晨好'
  if (hour < 12) return '早上好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

// 获取统计数据
async function fetchStats() {
  try {
    const res = await violationApi.getStatistics()
    stats.value = res.data || {
      totalViolations: 128,
      myViolations: 3,
      pending: 2
    }
  } catch (error) {
    // 使用模拟数据
    stats.value = {
      totalViolations: 128,
      myViolations: 3,
      pending: 2
    }
  }
}

// 获取最新违规
async function fetchLatestViolations() {
  try {
    const res = await violationApi.getViolations({ page: 1, pageSize: 3 })
    latestViolations.value = res.data?.list || []
  } catch (error) {
    // 使用模拟数据
    latestViolations.value = [
      {
        id: 1,
        typeName: '超速行驶',
        description: '在教学区路段行驶速度超过规定限速',
        place: '东门',
        status: 'pending',
        createTime: Date.now() - 3600000
      },
      {
        id: 2,
        typeName: '违规停放',
        description: '在非停车区域停放电动车',
        place: '宿舍区',
        status: 'processed',
        createTime: Date.now() - 7200000
      }
    ]
  }
}

// 页面跳转
function goTo(url) {
  if (url.includes('violation/report') || url.includes('track/index') || url.includes('profile/index')) {
    uni.switchTab({ url })
  } else {
    uni.navigateTo({ url })
  }
}

// 跳转详情
function goToDetail(violation) {
  uni.navigateTo({
    url: `/pages/violation/detail?id=${violation.id}`
  })
}

// 跳转个人中心
function goToProfile() {
  uni.switchTab({ url: '/pages/profile/index' })
}

onMounted(() => {
  fetchStats()
  fetchLatestViolations()
})
</script>

<style lang="scss" scoped>
.home-page {
  min-height: 100vh;
  background: #FAF7F2;
  padding-bottom: calc(120rpx + env(safe-area-inset-bottom));
}

.header-bg {
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
  padding: 60rpx 32rpx 100rpx;
  border-radius: 0 0 48rpx 48rpx;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.user-info {
  display: flex;
  align-items: center;
}

.avatar {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  border: 4rpx solid rgba(255, 255, 255, 0.4);
  margin-right: 20rpx;
}

.user-text {
  display: flex;
  flex-direction: column;
}

.greeting {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.85);
  margin-bottom: 4rpx;
}

.username {
  font-size: 34rpx;
  font-weight: 600;
  color: #FFFFFF;
}

.stats-card {
  display: flex;
  background: #FFFFFF;
  margin: -60rpx 32rpx 24rpx;
  border-radius: 24rpx;
  padding: 32rpx;
  box-shadow: 0 8rpx 24rpx rgba(45, 52, 54, 0.08);
}

.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 48rpx;
  font-weight: 700;
  color: #2D3436;
  margin-bottom: 8rpx;
  
  &.text-warning {
    color: #D4A574;
  }
}

.stat-label {
  font-size: 24rpx;
  color: #9BA4A9;
}

.stat-divider {
  width: 1rpx;
  background: #E8E4DE;
}

.quick-entry {
  background: #FFFFFF;
  margin: 0 32rpx 24rpx;
  border-radius: 24rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 16rpx rgba(45, 52, 54, 0.06);
}

.entry-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #2D3436;
  margin-bottom: 24rpx;
}

.entry-grid {
  display: flex;
  justify-content: space-between;
}

.entry-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 25%;
}

.entry-icon {
  width: 96rpx;
  height: 96rpx;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 44rpx;
  margin-bottom: 12rpx;
  
  &.violation {
    background: #FDEAE9;
  }
  
  &.my {
    background: #FDF3E7;
  }
  
  &.track {
    background: #E7F2EA;
  }
  
  &.fence {
    background: #E9F3FC;
  }
}

.entry-text {
  font-size: 24rpx;
  color: #636E72;
}

.latest-section {
  margin: 0 32rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #2D3436;
}

.section-more {
  font-size: 26rpx;
  color: #6B9AC4;
}

.empty-state {
  background: #FFFFFF;
  border-radius: 24rpx;
  padding: 60rpx;
  text-align: center;
  box-shadow: 0 4rpx 16rpx rgba(45, 52, 54, 0.06);
}

.empty-text {
  font-size: 28rpx;
  color: #9BA4A9;
}
</style>
