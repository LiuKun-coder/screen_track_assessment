<template>
  <view class="report-page">
    <!-- 筛选栏 -->
    <view class="filter-bar">
      <view class="filter-btn" @click="showFilter = true">
        <text>筛选</text>
        <text class="filter-icon">▼</text>
      </view>
      <view class="filter-tags" v-if="hasFilters">
        <view class="tag" v-if="filters.type">{{ getTypeName(filters.type) }}</view>
        <view class="tag" v-if="filters.place">{{ getPlaceName(filters.place) }}</view>
        <view class="clear-btn" @click="clearFilters">清除</view>
      </view>
    </view>
    
    <!-- 违规列表 -->
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
        <text class="empty-text">暂无违规记录</text>
      </view>
      
      <view class="load-more" v-if="violations.length && hasMore">
        <text>{{ loading ? '加载中...' : '上拉加载更多' }}</text>
      </view>
      
      <view class="load-more" v-if="violations.length && !hasMore">
        <text>没有更多了</text>
      </view>
    </scroll-view>
    
    <!-- 筛选面板 -->
    <filter-panel 
      :show="showFilter"
      :default-filters="filters"
      @close="showFilter = false"
      @confirm="handleFilter"
      @reset="handleReset"
    />
  </view>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { VIOLATION_TYPES, PLACE_OPTIONS } from '@/config/constant.js'
import violationApi from '@/api/violation.js'

const violations = ref([])
const loading = ref(false)
const refreshing = ref(false)
const showFilter = ref(false)

const filters = reactive({
  type: '',
  place: '',
  time: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const hasMore = computed(() => violations.value.length < pagination.total)
const hasFilters = computed(() => filters.type || filters.place || filters.time || filters.status)

// 获取类型名称
function getTypeName(code) {
  return VIOLATION_TYPES[code.toUpperCase()]?.label || code
}

// 获取地点名称
function getPlaceName(code) {
  return PLACE_OPTIONS.find(p => p.code === code)?.label || code
}

// 获取违规列表
async function fetchViolations(reset = false) {
  if (reset) {
    pagination.page = 1
    violations.value = []
  }
  
  if (loading.value) return
  loading.value = true
  
  try {
    const res = await violationApi.getViolations({
      ...filters,
      page: pagination.page,
      pageSize: pagination.pageSize
    })
    
    const { list, total } = res.data || {}
    violations.value = reset ? (list || []) : [...violations.value, ...(list || [])]
    pagination.total = total || 0
    pagination.page++
  } catch (error) {
    // 模拟数据
    const mockData = [
      { id: 1, typeName: '超速行驶', description: '在教学区路段超速行驶', place: '东门', status: 'pending', createTime: Date.now() - 3600000 },
      { id: 2, typeName: '违规停放', description: '在非停车区域停放', place: '宿舍区', status: 'processed', createTime: Date.now() - 7200000 },
      { id: 3, typeName: '闯红灯', description: '闯红灯通过路口', place: '西门', status: 'pending', createTime: Date.now() - 10800000 }
    ]
    violations.value = mockData
    pagination.total = 3
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

// 下拉刷新
function onRefresh() {
  refreshing.value = true
  fetchViolations(true)
}

// 加载更多
function loadMore() {
  if (hasMore.value && !loading.value) {
    fetchViolations(false)
  }
}

// 筛选确认
function handleFilter(newFilters) {
  Object.assign(filters, newFilters)
  showFilter.value = false
  fetchViolations(true)
}

// 重置筛选
function handleReset() {
  clearFilters()
}

// 清除筛选
function clearFilters() {
  filters.type = ''
  filters.place = ''
  filters.time = ''
  filters.status = ''
  fetchViolations(true)
}

// 跳转详情
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
.report-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #FAF7F2;
}

.filter-bar {
  display: flex;
  align-items: center;
  padding: 20rpx 32rpx;
  background: #FFFFFF;
  gap: 16rpx;
  box-shadow: 0 2rpx 8rpx rgba(45, 52, 54, 0.04);
}

.filter-btn {
  display: flex;
  align-items: center;
  padding: 16rpx 24rpx;
  background: #FAF7F2;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: #2D3436;
}

.filter-icon {
  margin-left: 8rpx;
  font-size: 20rpx;
  color: #9BA4A9;
}

.filter-tags {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12rpx;
}

.tag {
  padding: 8rpx 16rpx;
  background: #E7F2EA;
  border-radius: 10rpx;
  font-size: 24rpx;
  color: #7D9E87;
}

.clear-btn {
  font-size: 24rpx;
  color: #9BA4A9;
}

.violation-scroll {
  flex: 1;
  padding: 24rpx 32rpx;
}

.empty-state {
  padding: 100rpx 0;
  text-align: center;
}

.empty-text {
  font-size: 28rpx;
  color: #9BA4A9;
}

.load-more {
  padding: 32rpx;
  text-align: center;
  font-size: 26rpx;
  color: #9BA4A9;
}
</style>
