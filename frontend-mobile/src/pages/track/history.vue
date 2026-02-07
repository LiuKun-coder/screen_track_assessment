<template>
  <view class="history-page">
    <!-- 日期选择 -->
    <view class="date-picker">
      <picker mode="date" :value="selectedDate" @change="onDateChange">
        <view class="picker-display">
          <text class="picker-label">选择日期</text>
          <text class="picker-value">{{ selectedDate }}</text>
          <text class="picker-arrow">›</text>
        </view>
      </picker>
    </view>

    <!-- 轨迹列表 -->
    <scroll-view
      class="scroll-view"
      scroll-y
      @scrolltolower="loadMore"
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
    >
      <view class="track-list" v-if="tracks.length">
        <view 
          v-for="item in tracks" 
          :key="item.id"
          class="track-item"
          @click="goToPlayback(item.id)"
        >
          <!-- 时间区间 -->
          <view class="item-header">
            <view class="time-range">
              <text class="time">{{ formatTime(item.startTime) }}</text>
              <text class="separator">-</text>
              <text class="time">{{ formatTime(item.endTime) }}</text>
            </view>
            <view class="duration">{{ formatDurationText(item.startTime, item.endTime) }}</view>
          </view>

          <!-- 统计信息 -->
          <view class="item-content">
            <view class="stat-item">
              <text class="stat-label">行驶距离</text>
              <text class="stat-value">{{ item.distance || 0 }} km</text>
            </view>
            <view class="stat-item">
              <text class="stat-label">记录时间</text>
              <text class="stat-value">{{ formatDate(item.date) }}</text>
            </view>
          </view>

          <view class="arrow">›</view>
        </view>
      </view>

      <view class="empty-state" v-else-if="!loading">
        <view class="empty-icon">📍</view>
        <text class="empty-text">暂无轨迹记录</text>
      </view>

      <view class="load-more" v-if="tracks.length && loading">
        <text>加载中...</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { formatDate } from '@/utils/date.js'
import { calculateDuration, formatDuration } from '@/utils/date.js'
import trackApi from '@/api/track.js'

const selectedDate = ref('')
const tracks = ref([])
const loading = ref(false)
const refreshing = ref(false)

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

function formatTime(time) {
  return formatDate(time, 'HH:mm')
}

function formatDurationText(startTime, endTime) {
  const minutes = calculateDuration(startTime, endTime)
  return formatDuration(minutes)
}

async function fetchTracks(reset = false) {
  if (reset) {
    pagination.page = 1
    tracks.value = []
  }

  if (loading.value) return
  loading.value = true

  try {
    const res = await trackApi.getTrackList({
      date: selectedDate.value,
      page: pagination.page,
      pageSize: pagination.pageSize
    })

    // 适配后端 IPage 格式
    const { records, total } = res.data || {}
    tracks.value = reset ? (records || []) : [...tracks.value, ...(records || [])]
    pagination.total = total || 0
    pagination.page++
  } catch (error) {
    console.error('获取轨迹列表失败:', error)
    if (reset) {
      tracks.value = []
      pagination.total = 0
    }
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

function onDateChange(e) {
  selectedDate.value = e.detail.value
  fetchTracks(true)
}

function onRefresh() {
  refreshing.value = true
  fetchTracks(true)
}

function loadMore() {
  if (tracks.value.length < pagination.total && !loading.value) {
    fetchTracks(false)
  }
}

function goToPlayback(trackId) {
  uni.navigateTo({ url: `/pages/track/playback?trackId=${trackId}` })
}

onMounted(() => {
  // 默认选择今天
  const today = new Date()
  selectedDate.value = formatDate(today, 'YYYY-MM-DD')
  fetchTracks(true)
})
</script>

<style lang="scss" scoped>
.history-page {
  min-height: 100vh;
  background: #F5F5F5;
}

.date-picker {
  background: #FFFFFF;
  padding: 24rpx 32rpx;
  margin-bottom: 24rpx;
}

.picker-display {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.picker-label {
  font-size: 28rpx;
  color: #666666;
}

.picker-value {
  flex: 1;
  text-align: center;
  font-size: 32rpx;
  font-weight: 500;
  color: #007AFF;
}

.picker-arrow {
  font-size: 40rpx;
  color: #CCCCCC;
  transform: rotate(90deg);
}

.scroll-view {
  height: calc(100vh - 132rpx);
  padding: 0 32rpx;
}

.track-item {
  position: relative;
  background: #FFFFFF;
  border-radius: 20rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.time-range {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.time {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
}

.separator {
  font-size: 28rpx;
  color: #CCCCCC;
}

.duration {
  padding: 6rpx 16rpx;
  background: #E5F4FF;
  border-radius: 24rpx;
  font-size: 24rpx;
  color: #007AFF;
}

.item-content {
  display: flex;
  gap: 32rpx;
  padding-right: 40rpx;
}

.stat-item {
  flex: 1;
  
  .stat-label {
    display: block;
    font-size: 24rpx;
    color: #999999;
    margin-bottom: 8rpx;
  }
  
  .stat-value {
    display: block;
    font-size: 28rpx;
    font-weight: 500;
    color: #333333;
  }
}

.arrow {
  position: absolute;
  right: 32rpx;
  top: 50%;
  transform: translateY(-50%) rotate(90deg);
  font-size: 40rpx;
  color: #CCCCCC;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 0;
}

.empty-icon {
  font-size: 120rpx;
  margin-bottom: 32rpx;
}

.empty-text {
  font-size: 32rpx;
  color: #999999;
}

.load-more {
  padding: 32rpx;
  text-align: center;
  font-size: 26rpx;
  color: #999999;
}
</style>
