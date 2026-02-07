<template>
  <view class="detail-page">
    <view class="detail-card" v-if="violation">
      <!-- 状态 -->
      <view class="status-bar" :class="statusClass">
        <text class="status-text">{{ statusText }}</text>
      </view>
      
      <!-- 基本信息 -->
      <view class="info-section">
        <view class="info-header">
          <text class="violation-type">{{ violation.type }}</text>
          <text class="violation-time">{{ formatTime(violation.violationTime) }}</text>
        </view>
        
        <view class="info-row">
          <text class="info-label">违规地点</text>
          <text class="info-value">{{ violation.place }}</text>
        </view>
        
        <view class="info-row" v-if="violation.userName">
          <text class="info-label">违规人</text>
          <text class="info-value">{{ violation.userName }}</text>
        </view>
        
        <view class="info-row" v-if="violation.detail">
          <text class="info-label">违规描述</text>
          <text class="info-value">{{ violation.detail }}</text>
        </view>
        
        <view class="info-row" v-if="violation.penalty">
          <text class="info-label">处罚措施</text>
          <text class="info-value">{{ violation.penalty }}</text>
        </view>
      </view>
      
      <!-- 违规图片 -->
      <view class="image-section" v-if="evidenceImages.length">
        <text class="section-title">违规证据</text>
        <view class="image-grid">
          <image 
            v-for="(img, index) in evidenceImages" 
            :key="index"
            :src="img"
            mode="aspectFill"
            class="evidence-image"
            @click="previewImage(index)"
          />
        </view>
      </view>
      
      <!-- 操作按钮 -->
      <view class="action-section">
        <button 
          v-if="violation.canAppeal && violation.status === 'pending'" 
          class="btn btn-primary" 
          @click="goToAppeal"
        >
          提交申诉
        </button>
      </view>
    </view>
    
    <view class="loading" v-else>
      <text>加载中...</text>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { formatDate } from '@/utils/date.js'
import { parseEvidenceImages } from '@/utils/json-parser.js'
import { VIOLATION_STATUS } from '@/config/constant.js'
import violationApi from '@/api/violation.js'

const violation = ref(null)

const statusClass = computed(() => {
  return violation.value?.status === 'processed' ? 'status-processed' : 'status-pending'
})

const statusText = computed(() => {
  const status = violation.value?.status || 'pending'
  return VIOLATION_STATUS[status.toUpperCase()]?.label || '待处理'
})

// 解析证据图片
const evidenceImages = computed(() => {
  if (!violation.value?.evidenceImages) return []
  return parseEvidenceImages(violation.value.evidenceImages)
})

function formatTime(time) {
  return formatDate(time, 'YYYY-MM-DD HH:mm')
}

async function fetchDetail() {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const id = currentPage.options?.id
  
  if (!id) return
  
  try {
    const res = await violationApi.getViolationDetail(id)
    violation.value = res.data
  } catch (error) {
    console.error('获取违规详情失败:', error)
    uni.showToast({
      title: '加载失败',
      icon: 'none'
    })
  }
}

function previewImage(index) {
  uni.previewImage({
    current: index,
    urls: evidenceImages.value
  })
}

// 跳转到申诉页面
function goToAppeal() {
  if (!violation.value.canAppeal) {
    uni.showToast({
      title: '该违规不可申诉',
      icon: 'none'
    })
    return
  }
  
  uni.navigateTo({
    url: `/pages/appeal/submit?violationId=${violation.value.id}`
  })
}

function goToTrack() {
  uni.navigateTo({
    url: `/pages/track/playback?trackId=${violation.value.trackId}`
  })
}

onMounted(() => {
  fetchDetail()
})
</script>

<style lang="scss" scoped>
.detail-page {
  min-height: 100vh;
  background: #F5F5F5;
  padding: 24rpx 32rpx;
}

.detail-card {
  background: #FFFFFF;
  border-radius: 20rpx;
  overflow: hidden;
}

.status-bar {
  padding: 24rpx 32rpx;
  text-align: center;
  
  &.status-pending {
    background: linear-gradient(135deg, #FF9500 0%, #FFCC00 100%);
  }
  
  &.status-processed {
    background: linear-gradient(135deg, #4CD964 0%, #5AC8FA 100%);
  }
}

.status-text {
  font-size: 32rpx;
  font-weight: 600;
  color: #FFFFFF;
}

.info-section {
  padding: 32rpx;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32rpx;
  padding-bottom: 24rpx;
  border-bottom: 1rpx solid #F0F0F0;
}

.violation-type {
  font-size: 36rpx;
  font-weight: 600;
  color: #333333;
}

.violation-time {
  font-size: 26rpx;
  color: #999999;
}

.info-row {
  display: flex;
  margin-bottom: 20rpx;
}

.info-label {
  width: 160rpx;
  font-size: 28rpx;
  color: #999999;
  flex-shrink: 0;
}

.info-value {
  flex: 1;
  font-size: 28rpx;
  color: #333333;
}

.image-section {
  padding: 0 32rpx 32rpx;
}

.section-title {
  font-size: 28rpx;
  font-weight: 500;
  color: #333333;
  margin-bottom: 20rpx;
  display: block;
}

.image-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.evidence-image {
  width: 200rpx;
  height: 200rpx;
  border-radius: 12rpx;
}

.action-section {
  padding: 32rpx;
  border-top: 1rpx solid #F0F0F0;
}

.btn {
  width: 100%;
  height: 88rpx;
  border-radius: 12rpx;
  font-size: 32rpx;
  font-weight: 500;
  border: none;
  
  &.btn-primary {
    background: linear-gradient(135deg, #0066FF 0%, #0099FF 100%);
    color: #FFFFFF;
  }
  
  &.btn-outline {
    background: #FFFFFF;
    color: #0066FF;
    border: 2rpx solid #0066FF;
  }
}

.loading {
  padding: 100rpx;
  text-align: center;
  font-size: 28rpx;
  color: #999999;
}
</style>
