<template>
  <view class="my-violations-page">
    <scroll-view 
      class="violation-scroll"
      scroll-y
      @scrolltolower="loadMore"
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
    >
      <view class="violation-list" v-if="violations.length">
        <violation-card 
          v-for="item in violations" 
          :key="item.id"
          :violation="item"
          @click="goToDetail"
        />
      </view>
      
      <view class="empty-state" v-else-if="!loading">
        <view class="empty-icon">🎉</view>
        <text class="empty-text">您没有违规记录</text>
        <text class="empty-desc">请继续保持文明出行</text>
      </view>
      
      <view class="load-more" v-if="violations.length && loading">
        <text>加载中...</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import violationApi from '@/api/violation.js'

const violations = ref([])
const loading = ref(false)
const refreshing = ref(false)

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

async function fetchViolations(reset =false) {
  if (reset) {
    pagination.page = 1
    violations.value = []
  }
  
  if (loading.value) return
  loading.value = true
  
  try {
    const res = await violationApi.getMyViolations({
      page: pagination.page,
      pageSize: pagination.pageSize
    })
    
    // 适配后端 IPage 格式：{ records, total, size, current, pages }
    const { records, total } = res.data || {}
    violations.value = reset ? (records || []) : [...violations.value, ...(records || [])]
    pagination.total = total || 0
    pagination.page++
  } catch (error) {
    console.error('获取违规记录失败:', error)
    if (reset) {
      violations.value = []
      pagination.total = 0
    }
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

function onRefresh() {
  refreshing.value = true
  fetchViolations(true)
}

function loadMore() {
  if (violations.value.length < pagination.total && !loading.value) {
    fetchViolations(false)
  }
}

function goToDetail(violation) {
  uni.navigateTo({
    url: `/pages/violation/detail?id=${violation.id}`
  })
}

onMounted(() => {
  fetchViolations(true)
})
</script>

<style lang="scss" scoped>
.my-violations-page {
  min-height: 100vh;
  background: #F5F5F5;
}

.violation-scroll {
  height: 100vh;
  padding: 24rpx 32rpx;
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
  font-weight: 500;
  color: #333333;
  margin-bottom: 12rpx;
}

.empty-desc {
  font-size: 26rpx;
  color: #999999;
}

.load-more {
  padding: 32rpx;
  text-align: center;
  font-size: 26rpx;
  color: #999999;
}
</style>
