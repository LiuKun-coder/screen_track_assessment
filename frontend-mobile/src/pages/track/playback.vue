<template>
  <view class="playback-page">
    <!-- 地图区域 -->
    <view class="map-container">
      <map-track 
        :track-data="trackData"
        :show-controls="true"
        @marker-tap="handleMarkerTap"
      />
    </view>
    
    <!-- 控制面板 -->
    <view class="control-panel">
      <!-- 时间选择 -->
      <view class="time-selector">
        <view class="time-item" @click="showDatePicker('start')">
          <text class="time-label">开始时间</text>
          <text class="time-value">{{ startTime || '请选择' }}</text>
        </view>
        <view class="time-divider">至</view>
        <view class="time-item" @click="showDatePicker('end')">
          <text class="time-label">结束时间</text>
          <text class="time-value">{{ endTime || '请选择' }}</text>
        </view>
      </view>
      
      <!-- 播放控制 -->
      <view class="play-controls">
        <view class="control-btn" @click="handlePlayPause">
          <text class="btn-icon">{{ isPlaying ? '⏸️' : '▶️' }}</text>
        </view>
        <view class="progress-bar">
          <view class="progress" :style="{ width: progress + '%' }"></view>
        </view>
        <text class="speed-text">{{ playSpeed }}x</text>
        <view class="control-btn" @click="changeSpeed">
          <text class="btn-icon">⚡</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { formatDate } from '@/utils/date.js'
import trackApi from '@/api/track.js'

const trackData = ref(null)
const startTime = ref('')
const endTime = ref('')
const isPlaying = ref(false)
const progress = ref(0)
const playSpeed = ref(1)

let playTimer = null
let vehicleNo = ''

// 获取轨迹数据
async function fetchTrackData() {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  vehicleNo = currentPage.options?.vehicleNo || ''
  
  try {
    const res = await trackApi.getHistory({
      vehicleNo,
      startTime: startTime.value,
      endTime: endTime.value
    })
    trackData.value = res.data
  } catch (error) {
    // 模拟数据
    trackData.value = {
      points: [
        { latitude: 39.9042, longitude: 116.4074, timestamp: Date.now() - 3600000, speed: 20 },
        { latitude: 39.9052, longitude: 116.4084, timestamp: Date.now() - 3000000, speed: 25 },
        { latitude: 39.9062, longitude: 116.4094, timestamp: Date.now() - 2400000, speed: 30 },
        { latitude: 39.9072, longitude: 116.4104, timestamp: Date.now() - 1800000, speed: 28 },
        { latitude: 39.9082, longitude: 116.4114, timestamp: Date.now() - 1200000, speed: 22 }
      ],
      totalDistance: 1500,
      maxSpeed: 30,
      violations: [
        { latitude: 39.9062, longitude: 116.4094, typeName: '超速', time: Date.now() - 2400000 }
      ]
    }
  }
}

function showDatePicker(type) {
  uni.showToast({ title: '日期选择器开发中', icon: 'none' })
}

function handlePlayPause() {
  isPlaying.value = !isPlaying.value
  
  if (isPlaying.value) {
    startPlayback()
  } else {
    stopPlayback()
  }
}

function startPlayback() {
  if (playTimer) clearInterval(playTimer)
  
  playTimer = setInterval(() => {
    progress.value += 1 * playSpeed.value
    if (progress.value >= 100) {
      progress.value = 0
      isPlaying.value = false
      clearInterval(playTimer)
    }
  }, 100)
}

function stopPlayback() {
  if (playTimer) {
    clearInterval(playTimer)
    playTimer = null
  }
}

function changeSpeed() {
  const speeds = [1, 2, 4, 8]
  const currentIndex = speeds.indexOf(playSpeed.value)
  playSpeed.value = speeds[(currentIndex + 1) % speeds.length]
}

function handleMarkerTap(markerId) {
  console.log('Marker tapped:', markerId)
}

onMounted(() => {
  const now = new Date()
  endTime.value = formatDate(now, 'YYYY-MM-DD HH:mm')
  startTime.value = formatDate(new Date(now.getTime() - 24 * 60 * 60 * 1000), 'YYYY-MM-DD HH:mm')
  
  fetchTrackData()
})

onUnmounted(() => {
  stopPlayback()
})
</script>

<style lang="scss" scoped>
.playback-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.map-container {
  flex: 1;
}

.control-panel {
  background: #FFFFFF;
  padding: 24rpx 32rpx;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.1);
}

.time-selector {
  display: flex;
  align-items: center;
  margin-bottom: 24rpx;
}

.time-item {
  flex: 1;
  background: #F5F5F5;
  border-radius: 12rpx;
  padding: 16rpx 20rpx;
}

.time-label {
  font-size: 22rpx;
  color: #999999;
  display: block;
  margin-bottom: 4rpx;
}

.time-value {
  font-size: 26rpx;
  color: #333333;
}

.time-divider {
  margin: 0 16rpx;
  font-size: 26rpx;
  color: #999999;
}

.play-controls {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.control-btn {
  width: 80rpx;
  height: 80rpx;
  background: #F5F5F5;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-icon {
  font-size: 36rpx;
}

.progress-bar {
  flex: 1;
  height: 8rpx;
  background: #E5E5E5;
  border-radius: 4rpx;
  overflow: hidden;
}

.progress {
  height: 100%;
  background: linear-gradient(90deg, #007AFF 0%, #5AC8FA 100%);
  transition: width 0.1s linear;
}

.speed-text {
  font-size: 26rpx;
  color: #666666;
  min-width: 60rpx;
  text-align: center;
}
</style>
