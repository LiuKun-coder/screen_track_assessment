<template>
  <view class="map-track-container">
    <!-- 地图组件 -->
    <map
      id="trackMap"
      class="track-map"
      :latitude="center.latitude"
      :longitude="center.longitude"
      :scale="scale"
      :markers="markers"
      :polyline="polyline"
      :show-location="showLocation"
      @markertap="handleMarkerTap"
      @regionchange="handleRegionChange"
    ></map>
    
    <!-- 控制按钮 -->
    <view class="map-controls" v-if="showControls">
      <!-- 定位按钮 -->
      <view class="control-btn" @click="handleLocation">
        <text class="control-icon">📍</text>
      </view>
      
      <!-- 放大 -->
      <view class="control-btn" @click="handleZoomIn">
        <text class="control-icon">+</text>
      </view>
      
      <!-- 缩小 -->
      <view class="control-btn" @click="handleZoomOut">
        <text class="control-icon">-</text>
      </view>
    </view>
    
    <!-- 轨迹信息 -->
    <view class="track-info" v-if="trackData">
      <view class="info-item">
        <text class="info-label">总里程</text>
        <text class="info-value">{{ formatDistance(trackData.totalDistance) }}</text>
      </view>
      <view class="info-item">
        <text class="info-label">最高时速</text>
        <text class="info-value">{{ trackData.maxSpeed || 0 }} km/h</text>
      </view>
      <view class="info-item">
        <text class="info-label">违规点</text>
        <text class="info-value violation">{{ violationCount }}处</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'

const props = defineProps({
  // 轨迹数据
  trackData: {
    type: Object,
    default: null
  },
  // 是否显示控制按钮
  showControls: {
    type: Boolean,
    default: true
  },
  // 是否显示当前位置
  showLocation: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['markerTap', 'regionChange'])

// 地图中心点
const center = ref({
  latitude: 39.9042,
  longitude: 116.4074
})

// 缩放级别
const scale = ref(14)

// 地图标记点
const markers = computed(() => {
  if (!props.trackData?.points?.length) return []
  
  const points = props.trackData.points
  const markerList = []
  
  // 起点
  markerList.push({
    id: 0,
    latitude: points[0].latitude,
    longitude: points[0].longitude,
    iconPath: '/static/images/marker-start.png',
    width: 32,
    height: 32,
    anchor: { x: 0.5, y: 1 },
    callout: {
      content: '起点',
      display: 'ALWAYS',
      fontSize: 12,
      padding: 5,
      borderRadius: 3
    }
  })
  
  // 终点
  const lastPoint = points[points.length - 1]
  markerList.push({
    id: 1,
    latitude: lastPoint.latitude,
    longitude: lastPoint.longitude,
    iconPath: '/static/images/marker-end.png',
    width: 32,
    height: 32,
    anchor: { x: 0.5, y: 1 },
    callout: {
      content: '终点',
      display: 'ALWAYS',
      fontSize: 12,
      padding: 5,
      borderRadius: 3
    }
  })
  
  // 违规点
  if (props.trackData.violations) {
    props.trackData.violations.forEach((v, index) => {
      markerList.push({
        id: 100 + index,
        latitude: v.latitude,
        longitude: v.longitude,
        iconPath: '/static/images/marker-violation.png',
        width: 28,
        height: 28,
        anchor: { x: 0.5, y: 1 },
        callout: {
          content: v.typeName || '违规',
          display: 'BYCLICK',
          fontSize: 12,
          padding: 5,
          borderRadius: 3,
          bgColor: '#FF3B30',
          color: '#FFFFFF'
        }
      })
    })
  }
  
  return markerList
})

// 轨迹线
const polyline = computed(() => {
  if (!props.trackData?.points?.length) return []
  
  return [{
    points: props.trackData.points.map(p => ({
      latitude: p.latitude,
      longitude: p.longitude
    })),
    color: '#007AFF',
    width: 6,
    arrowLine: true
  }]
})

// 违规点数量
const violationCount = computed(() => {
  return props.trackData?.violations?.length || 0
})

// 格式化距离
function formatDistance(meters) {
  if (!meters) return '0 m'
  if (meters < 1000) {
    return `${Math.round(meters)} m`
  }
  return `${(meters / 1000).toFixed(2)} km`
}

// 监听轨迹数据变化，更新地图中心
watch(() => props.trackData, (data) => {
  if (data?.points?.length) {
    // 设置地图中心为轨迹中点
    const midIndex = Math.floor(data.points.length / 2)
    center.value = {
      latitude: data.points[midIndex].latitude,
      longitude: data.points[midIndex].longitude
    }
  }
}, { immediate: true })

// 定位到当前位置
function handleLocation() {
  uni.getLocation({
    type: 'gcj02',
    success: (res) => {
      center.value = {
        latitude: res.latitude,
        longitude: res.longitude
      }
    },
    fail: (err) => {
      uni.showToast({
        title: '获取位置失败',
        icon: 'none'
      })
    }
  })
}

// 放大
function handleZoomIn() {
  if (scale.value < 20) {
    scale.value++
  }
}

// 缩小
function handleZoomOut() {
  if (scale.value > 3) {
    scale.value--
  }
}

// 标记点点击
function handleMarkerTap(e) {
  emit('markerTap', e.markerId)
}

// 地图区域变化
function handleRegionChange(e) {
  emit('regionChange', e)
}
</script>

<style lang="scss" scoped>
.map-track-container {
  position: relative;
  width: 100%;
  height: 100%;
}

.track-map {
  width: 100%;
  height: 100%;
}

.map-controls {
  position: absolute;
  right: 24rpx;
  bottom: 200rpx;
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.control-btn {
  width: 80rpx;
  height: 80rpx;
  background: #FFFFFF;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.1);
}

.control-icon {
  font-size: 36rpx;
  color: #333333;
}

.track-info {
  position: absolute;
  left: 24rpx;
  right: 24rpx;
  bottom: 24rpx;
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 24rpx;
  display: flex;
  justify-content: space-around;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
}

.info-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.info-label {
  font-size: 24rpx;
  color: #999999;
}

.info-value {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
  
  &.violation {
    color: #FF3B30;
  }
}
</style>
