<template>
  <view class="list-page">
    <scroll-view 
      class="scroll-view"
      scroll-y
      @scrolltolower="loadMore"
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
    >
      <view class="appeal-list" v-if="appeals.length">
        <view 
          v-for="item in appeals" 
          :key="item.id"
          class="appeal-item"
          @click="goToDetail(item.id)"
        >
          <!-- 头部 -->
          <view class="item-header">
            <view class="appeal-number">{{ item.appealNumber }}</view>
            <view class="status-tag" :class="`status-${item.status}`">
              {{ getStatusText(item.status) }}
            </view>
          </view>

          <!-- 违规信息 -->
          <view class="item-content">
            <view class="info-row">
              <text class="label">申诉原因</text>
              <text class="value">{{ item.reason }}</text>
            </view>
            <view class="info-row">
              <text class="label">提交时间</text>
              <text class="value">{{ formatTime(item.createTime) }}</text>
            </view>
            <view class="info-row" v-if="item.handleTime">
              <text class="label">处理时间</text>
              <text class="value">{{ formatTime(item.handleTime) }}</text>
            </view>
          </view>

          <!-- 箭头 -->
          <view class="arrow">›</view>
        </view>
      </view>

      <view class="empty-state" v-else-if="!loading">
        <view class="empty-icon">📋</view>
        <text class="empty-text">暂无申诉记录</text>
      </view>

      <view class="load-more" v-if="appeals.length && loading">
        <text>加载中...</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { formatDate } from '@/utils/date.js'
import appealApi from '@/api/appeal.js'

const appeals = ref([])
const loading = ref(false)
const refreshing = ref(false)

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const statusMap = {
  pending: '待处理',
  approved: '已通过',
  rejected: '已拒绝'
}

function formatTime(time) {
  return formatDate(time, 'YYYY-MM-DD HH:mm')
}

function getStatusText(status) {
  return statusMap[status] || status
}

async function fetchAppeals(reset = false) {
  if (reset) {
    pagination.page = 1
    appeals.value = []
  }

  if (loading.value) return
  loading.value = true

  try {
    const res = await appealApi.getMyAppeals({
      page: pagination.page,
      pageSize: pagination.pageSize
    })

    // 适配后端 IPage 格式
    const { records, total } = res.data || {}
    appeals.value = reset ? (records || []) : [...appeals.value, ...(records || [])]
    pagination.total = total || 0
    pagination.page++
  } catch (error) {
    console.error('获取申诉列表失败:', error)
    if (reset) {
      appeals.value = []
      pagination.total = 0
    }
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

function onRefresh() {
  refreshing.value = true
  fetchAppeals(true)
}

function loadMore() {
  if (appeals.value.length < pagination.total && !loading.value) {
    fetchAppeals(false)
  }
}

function goToDetail(id) {
  uni.navigateTo({ url: `/pages/appeal/detail?id=${id}` })
}

onMounted(() => {
  fetchAppeals(true)
})
</script>

<style lang="scss" scoped>
.list-page {
  min-height: 100vh;
  background: #F5F5F5;
}

.scroll-view {
  height: 100vh;
  padding: 24rpx 32rpx;
}

.appeal-item {
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

.appeal-number {
  font-size: 30rpx;
  font-weight: 600;
  color: #333333;
}

.status-tag {
  padding: 8rpx 20rpx;
  border-radius: 24rpx;
  font-size: 24rpx;
  
  &.status-pending {
    background: #FFF4E5;
    color: #FF9500;
  }
  
  &.status-approved {
    background: #E5F8F0;
    color: #34C759;
  }
  
  &.status-rejected {
    background: #FFE5E5;
    color: #FF3B30;
  }
}

.item-content {
  padding-right: 40rpx;
}

.info-row {
  display: flex;
  margin-bottom: 12rpx;
  
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
    color: #666666;
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
