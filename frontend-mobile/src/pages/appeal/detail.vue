<template>
  <view class="detail-page">
    <view class="detail-card" v-if="appeal">
      <!-- 状态栏 -->
      <view class="status-bar" :class="`status-${appeal.status}`">
        <text class="status-text">{{ getStatusText(appeal.status) }}</text>
      </view>

      <!-- 基本信息 -->
      <view class="section">
        <text class="section-title">申诉信息</text>
        <view class="info-row">
          <text class="label">申诉编号</text>
          <text class="value">{{ appeal.appealNumber }}</text>
        </view>
        <view class="info-row">
          <text class="label">申诉原因</text>
          <text class="value">{{ appeal.reason }}</text>
        </view>
        <view class="info-row">
          <text class="label">详细描述</text>
          <text class="value desc">{{ appeal.description }}</text>
        </view>
        <view class="info-row">
          <text class="label">联系电话</text>
          <text class="value">{{ appeal.contactPhone }}</text>
        </view>
        <view class="info-row">
          <text class="label">提交时间</text>
          <text class="value">{{ formatTime(appeal.createTime) }}</text>
        </view>
      </view>

      <!-- 证据文件 -->
      <view class="section" v-if="evidenceFiles.length">
        <text class="section-title">证据文件</text>
        <view class="image-grid">
          <image 
            v-for="(file, index) in evidenceFiles" 
            :key="index"
            :src="file"
            mode="aspectFill"
            class="evidence-image"
            @click="previewImage(index)"
          />
        </view>
      </view>

      <!-- 处理结果 -->
      <view class="section" v-if="appeal.status !== 'pending'">
        <text class="section-title">处理结果</text>
        <view class="info-row">
          <text class="label">处理时间</text>
          <text class="value">{{ formatTime(appeal.handleTime) }}</text>
        </view>
        <view class="info-row" v-if="appeal.replyContent">
          <text class="label">回复内容</text>
          <text class="value desc">{{ appeal.replyContent }}</text>
        </view>
      </view>

      <!-- 待处理提示 -->
      <view class="pending-tip" v-else>
        <text class="tip-icon">⏰</text>
        <text class="tip-text">申诉正在处理中，请耐心等待</text>
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
import { parseEvidenceFiles } from '@/utils/json-parser.js'
import appealApi from '@/api/appeal.js'

const appeal = ref(null)

const statusMap = {
  pending: '待处理',
  approved: '已通过',
  rejected: '已拒绝'
}

// 解析证据文件
const evidenceFiles = computed(() => {
  if (!appeal.value?.evidenceFiles) return []
  return parseEvidenceFiles(appeal.value.evidenceFiles)
})

function formatTime(time) {
  return formatDate(time, 'YYYY-MM-DD HH:mm')
}

function getStatusText(status) {
  return statusMap[status] || status
}

async function fetchDetail() {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const id = currentPage.options?.id

  if (!id) return

  try {
    const res = await appealApi.getAppealDetail(id)
    appeal.value = res.data
  } catch (error) {
    console.error('获取申诉详情失败:', error)
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
}

function previewImage(index) {
  uni.previewImage({
    current: index,
    urls: evidenceFiles.value
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

  &.status-approved {
    background: linear-gradient(135deg, #34C759 0%, #5AC8FA 100%);
  }

  &.status-rejected {
    background: linear-gradient(135deg, #FF3B30 0%, #FF6B66 100%);
  }
}

.status-text {
  font-size: 32rpx;
  font-weight: 600;
  color: #FFFFFF;
}

.section {
  padding: 32rpx;
  
  & + .section {
    border-top: 1rpx solid #F0F0F0;
  }
}

.section-title {
  display: block;
  font-size: 28rpx;
  font-weight: 500;
  color: #333333;
  margin-bottom: 20rpx;
}

.info-row {
  display: flex;
  margin-bottom: 16rpx;

  &:last-child {
    margin-bottom: 0;
  }

  .label {
    width: 160rpx;
    font-size: 26rpx;
    color: #999999;
    flex-shrink: 0;
  }

  .value {
    flex: 1;
    font-size: 26rpx;
    color: #333333;
    
    &.desc {
      line-height: 1.6;
    }
  }
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

.pending-tip {
  padding: 40rpx 32rpx;
  text-align: center;
  border-top: 1rpx solid #F0F0F0;
}

.tip-icon {
  display: block;
  font-size: 80rpx;
  margin-bottom: 16rpx;
}

.tip-text {
  font-size: 28rpx;
  color: #666666;
}

.loading {
  padding: 100rpx;
  text-align: center;
  font-size: 28rpx;
  color: #999999;
}
</style>
