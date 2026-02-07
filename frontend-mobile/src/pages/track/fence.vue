<template>
  <view class="fence-page">
    <!-- 地图 -->
    <view class="map-container">
      <map
        id="fenceMap"
        class="fence-map"
        :latitude="center.latitude"
        :longitude="center.longitude"
        :scale="14"
        :polygons="polygons"
        :markers="markers"
      ></map>
    </view>
    
    <!-- 围栏列表 -->
    <view class="fence-list">
      <view class="list-header">
        <text class="list-title">电子围栏列表</text>
      </view>
      
      <scroll-view class="list-scroll" scroll-y>
        <view 
          class="fence-item" 
          v-for="fence in fenceList" 
          :key="fence.id"
          :class="{ active: selectedFence === fence.id }"
          @click="selectFence(fence)"
        >
          <view class="fence-info">
            <text class="fence-name">{{ fence.name }}</text>
            <text class="fence-desc">{{ fence.rangeDesc }}</text>
          </view>
          <view class="fence-status" :class="fence.status">
            {{ fence.status === 'active' ? '已启用' : '已禁用' }}
          </view>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { parseCoordinates } from '@/utils/json-parser.js'
import fenceApi from '@/api/fence.js'

const center = ref({
  latitude: 39.9042,
  longitude: 116.4074
})

const fenceList = ref([])
const selectedFence = ref(null)
const polygons = ref([])
const markers = ref([])

async function fetchFenceList() {
  try {
    const res = await fenceApi.getFenceList()
    const list = res.data || []
    
    // 解析每个围栏的坐标
    fenceList.value = list.map(fence => ({
      ...fence,
      points: parseCoordinates(fence.coordinates)
    }))
    
    // 生成多边形
    updatePolygons()
  } catch (error) {
    console.error('获取围栏列表失败:', error)
    fenceList.value = []
  }
}

function updatePolygons() {
  polygons.value = fenceList.value
    .filter(f => f.points?.length >= 3)
    .map(fence => ({
      points: fence.points,
      strokeColor: fence.id === selectedFence.value ? '#007AFF' : '#FF9500',
      strokeWidth: 3,
      fillColor: fence.id === selectedFence.value ? 'rgba(0,122,255,0.2)' : 'rgba(255,149,0,0.2)'
    }))
}

function selectFence(fence) {
  selectedFence.value = fence.id
  
  if (fence.points?.length) {
    // 移动到围栏中心
    const latSum = fence.points.reduce((sum, p) => sum + p.latitude, 0)
    const lngSum = fence.points.reduce((sum, p) => sum + p.longitude, 0)
    center.value = {
      latitude: latSum / fence.points.length,
      longitude: lngSum / fence.points.length
    }
  }
  
  updatePolygons()
}

onMounted(() => {
  fetchFenceList()
})
</script>

<style lang="scss" scoped>
.fence-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.map-container {
  height: 50vh;
}

.fence-map {
  width: 100%;
  height: 100%;
}

.fence-list {
  flex: 1;
  background: #FFFFFF;
  border-radius: 24rpx 24rpx 0 0;
  margin-top: -24rpx;
  display: flex;
  flex-direction: column;
}

.list-header {
  padding: 32rpx;
  border-bottom: 1rpx solid #F0F0F0;
}

.list-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
}

.list-scroll {
  flex: 1;
}

.fence-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 32rpx;
  border-bottom: 1rpx solid #F0F0F0;
  
  &.active {
    background: #F0F8FF;
  }
}

.fence-info {
  flex: 1;
}

.fence-name {
  font-size: 30rpx;
  font-weight: 500;
  color: #333333;
  display: block;
  margin-bottom: 8rpx;
}

.fence-desc {
  font-size: 24rpx;
  color: #999999;
}

.fence-status {
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
  
  &.active {
    background: #E8F5E9;
    color: #4CD964;
  }
  
  &.inactive {
    background: #F5F5F5;
    color: #999999;
  }
}
</style>
