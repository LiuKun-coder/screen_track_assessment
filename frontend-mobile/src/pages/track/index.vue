<template>
  <view class="track-page">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <input 
        class="search-input" 
        v-model="vehicleNo" 
        placeholder="请输入车牌号查询"
      />
      <button class="search-btn" @click="handleSearch">查询</button>
    </view>
    
    <!-- 快捷入口 -->
    <view class="quick-actions">
      <view class="action-item" @click="goToPlayback">
        <view class="action-icon">🔄</view>
        <text class="action-text">轨迹回放</text>
      </view>
      <view class="action-item" @click="goToFence">
        <view class="action-icon">🔒</view>
        <text class="action-text">电子围栏</text>
      </view>
    </view>
    
    <!-- 历史记录 -->
    <view class="history-section">
      <view class="section-header">
        <text class="section-title">查询历史</text>
        <text class="section-action" @click="clearHistory" v-if="historyList.length">清空</text>
      </view>
      
      <view class="history-list" v-if="historyList.length">
        <view 
          class="history-item" 
          v-for="(item, index) in historyList" 
          :key="index"
          @click="selectHistory(item)"
        >
          <text class="history-value">{{ item }}</text>
          <text class="history-time">{{ formatTime(index) }}</text>
        </view>
      </view>
      
      <view class="empty-state" v-else>
        <text class="empty-text">暂无查询历史</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStorage, setStorage, removeStorage } from '@/utils/storage.js'

const vehicleNo = ref('')
const historyList = ref([])

const HISTORY_KEY = 'track_history'

function formatTime(index) {
  const times = ['刚刚', '1小时前', '昨天', '2天前']
  return times[index] || '很久前'
}

function handleSearch() {
  if (!vehicleNo.value.trim()) {
    uni.showToast({ title: '请输入车牌号', icon: 'none' })
    return
  }
  
  // 保存到历史
  const history = getStorage(HISTORY_KEY) || []
  const newHistory = [vehicleNo.value, ...history.filter(h => h !== vehicleNo.value)].slice(0, 10)
  setStorage(HISTORY_KEY, newHistory)
  historyList.value = newHistory
  
  // 跳转回放页
  uni.navigateTo({
    url: `/pages/track/playback?vehicleNo=${vehicleNo.value}`
  })
}

function selectHistory(value) {
  vehicleNo.value = value
  handleSearch()
}

function clearHistory() {
  uni.showModal({
    title: '提示',
    content: '确定清空查询历史吗？',
    success: (res) => {
      if (res.confirm) {
        removeStorage(HISTORY_KEY)
        historyList.value = []
      }
    }
  })
}

function goToPlayback() {
  if (!vehicleNo.value) {
    uni.showToast({ title: '请先输入车牌号', icon: 'none' })
    return
  }
  handleSearch()
}

function goToFence() {
  uni.navigateTo({ url: '/pages/track/fence' })
}

onMounted(() => {
  historyList.value = getStorage(HISTORY_KEY) || []
})
</script>

<style lang="scss" scoped>
.track-page {
  min-height: 100vh;
  background: #FAF7F2;
  padding: 24rpx 32rpx;
  padding-bottom: calc(120rpx + env(safe-area-inset-bottom));
}

.search-bar {
  display: flex;
  gap: 16rpx;
  margin-bottom: 24rpx;
}

.search-input {
  flex: 1;
  height: 88rpx;
  background: #FFFFFF;
  border-radius: 20rpx;
  padding: 0 24rpx;
  font-size: 30rpx;
  box-shadow: 0 2rpx 8rpx rgba(45, 52, 54, 0.04);
}

.search-btn {
  width: 160rpx;
  height: 88rpx;
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
  border-radius: 20rpx;
  color: #FFFFFF;
  font-size: 30rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
}

.quick-actions {
  display: flex;
  gap: 24rpx;
  margin-bottom: 32rpx;
}

.action-item {
  flex: 1;
  background: #FFFFFF;
  border-radius: 24rpx;
  padding: 32rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 4rpx 16rpx rgba(45, 52, 54, 0.06);
}

.action-icon {
  font-size: 60rpx;
  margin-bottom: 12rpx;
}

.action-text {
  font-size: 28rpx;
  color: #2D3436;
}

.history-section {
  background: #FFFFFF;
  border-radius: 24rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 16rpx rgba(45, 52, 54, 0.06);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #2D3436;
}

.section-action {
  font-size: 26rpx;
  color: #9BA4A9;
}

.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #E8E4DE;
  
  &:last-child {
    border-bottom: none;
  }
}

.history-value {
  font-size: 28rpx;
  color: #2D3436;
}

.history-time {
  font-size: 24rpx;
  color: #9BA4A9;
}

.empty-state {
  padding: 40rpx;
  text-align: center;
}

.empty-text {
  font-size: 28rpx;
  color: #9BA4A9;
}
</style>
