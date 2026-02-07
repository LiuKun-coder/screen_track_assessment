<template>
  <view class="violation-card" @click="handleClick">
    <!-- 状态标签 -->
    <view class="card-status" :class="statusClass">
      {{ statusText }}
    </view>
    
    <!-- 卡片内容 -->
    <view class="card-content">
      <!-- 违规类型和时间 -->
      <view class="card-header">
        <text class="violation-type">{{ violation.typeName || '未知违规' }}</text>
        <text class="violation-time">{{ formatTime }}</text>
      </view>
      
      <!-- 违规描述 -->
      <view class="card-body">
        <text class="violation-desc">{{ violation.description || '暂无描述' }}</text>
      </view>
      
      <!-- 违规信息 -->
      <view class="card-footer">
        <view class="info-item">
          <text class="info-label">地点：</text>
          <text class="info-value">{{ violation.place || '未知' }}</text>
        </view>
        <view class="info-item" v-if="violation.vehicleNo">
          <text class="info-label">车牌：</text>
          <text class="info-value">{{ violation.vehicleNo }}</text>
        </view>
      </view>
    </view>
    
    <!-- 箭头 -->
    <view class="card-arrow">
      <text class="arrow-icon">›</text>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'
import { formatDate, getRelativeTime } from '@/utils/date.js'
import { VIOLATION_STATUS } from '@/config/constant.js'

const props = defineProps({
  violation: {
    type: Object,
    required: true,
    default: () => ({})
  }
})

const emit = defineEmits(['click'])

// 状态样式类
const statusClass = computed(() => {
  return props.violation.status === 'processed' ? 'status-processed' : 'status-pending'
})

// 状态文本
const statusText = computed(() => {
  const status = props.violation.status || 'pending'
  return VIOLATION_STATUS[status.toUpperCase()]?.label || '待处理'
})

// 格式化时间
const formatTime = computed(() => {
  return getRelativeTime(props.violation.createTime)
})

// 点击事件
function handleClick() {
  emit('click', props.violation)
}
</script>

<style lang="scss" scoped>
.violation-card {
  display: flex;
  align-items: stretch;
  background: #FFFFFF;
  border-radius: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(45, 52, 54, 0.06);
  margin-bottom: 24rpx;
  overflow: hidden;
  position: relative;
}

.card-status {
  position: absolute;
  top: 0;
  right: 0;
  padding: 8rpx 20rpx;
  font-size: 22rpx;
  border-radius: 0 24rpx 0 16rpx;
  
  &.status-pending {
    background: #FDF3E7;
    color: #D4A574;
  }
  
  &.status-processed {
    background: #E7F2EA;
    color: #7D9E87;
  }
}

.card-content {
  flex: 1;
  padding: 32rpx;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.violation-type {
  font-size: 32rpx;
  font-weight: 600;
  color: #2D3436;
}

.violation-time {
  font-size: 24rpx;
  color: #9BA4A9;
}

.card-body {
  margin-bottom: 20rpx;
}

.violation-desc {
  font-size: 28rpx;
  color: #636E72;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.info-item {
  display: flex;
  align-items: center;
}

.info-label {
  font-size: 24rpx;
  color: #9BA4A9;
}

.info-value {
  font-size: 24rpx;
  color: #636E72;
}

.card-arrow {
  display: flex;
  align-items: center;
  padding-right: 24rpx;
}

.arrow-icon {
  font-size: 40rpx;
  color: #9BA4A9;
}
</style>
