<template>
  <view class="filter-panel" :class="{ 'is-show': show }">
    <!-- 遮罩层 -->
    <view class="filter-mask" @click="handleClose"></view>
    
    <!-- 筛选内容 -->
    <view class="filter-content">
      <view class="filter-header">
        <text class="header-title">筛选条件</text>
        <text class="header-reset" @click="handleReset">重置</text>
      </view>
      
      <!-- 筛选选项 -->
      <scroll-view class="filter-body" scroll-y>
        <!-- 时间筛选 -->
        <view class="filter-group">
          <text class="group-title">时间范围</text>
          <view class="group-options">
            <view
              class="option-item"
              :class="{ 'is-active': filters.time === item.code }"
              v-for="item in timeOptions"
              :key="item.code"
              @click="selectFilter('time', item.code)"
            >
              {{ item.label }}
            </view>
          </view>
        </view>
        
        <!-- 地点筛选 -->
        <view class="filter-group">
          <text class="group-title">违规地点</text>
          <view class="group-options">
            <view
              class="option-item"
              :class="{ 'is-active': filters.place === item.code }"
              v-for="item in placeOptions"
              :key="item.code"
              @click="selectFilter('place', item.code)"
            >
              {{ item.label }}
            </view>
          </view>
        </view>
        
        <!-- 类型筛选 -->
        <view class="filter-group">
          <text class="group-title">违规类型</text>
          <view class="group-options">
            <view
              class="option-item"
              :class="{ 'is-active': filters.type === item.code }"
              v-for="item in typeOptions"
              :key="item.code"
              @click="selectFilter('type', item.code)"
            >
              {{ item.label }}
            </view>
          </view>
        </view>
        
        <!-- 状态筛选 -->
        <view class="filter-group">
          <text class="group-title">处理状态</text>
          <view class="group-options">
            <view
              class="option-item"
              :class="{ 'is-active': filters.status === item.code }"
              v-for="item in statusOptions"
              :key="item.code"
              @click="selectFilter('status', item.code)"
            >
              {{ item.label }}
            </view>
          </view>
        </view>
      </scroll-view>
      
      <!-- 底部按钮 -->
      <view class="filter-footer">
        <view class="btn btn-cancel" @click="handleClose">取消</view>
        <view class="btn btn-confirm" @click="handleConfirm">确定</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { TIME_OPTIONS, PLACE_OPTIONS, VIOLATION_TYPES, VIOLATION_STATUS } from '@/config/constant.js'

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  defaultFilters: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['close', 'confirm', 'reset'])

// 筛选条件
const filters = reactive({
  time: '',
  place: '',
  type: '',
  status: ''
})

// 选项数据
const timeOptions = TIME_OPTIONS
const placeOptions = PLACE_OPTIONS
const typeOptions = Object.values(VIOLATION_TYPES)
const statusOptions = Object.values(VIOLATION_STATUS)

// 监听默认值变化
watch(() => props.defaultFilters, (val) => {
  Object.assign(filters, val)
}, { immediate: true })

// 选择筛选项
function selectFilter(key, value) {
  // 点击已选中的选项则取消选中
  filters[key] = filters[key] === value ? '' : value
}

// 重置筛选
function handleReset() {
  filters.time = ''
  filters.place = ''
  filters.type = ''
  filters.status = ''
  emit('reset')
}

// 关闭面板
function handleClose() {
  emit('close')
}

// 确认筛选
function handleConfirm() {
  emit('confirm', { ...filters })
}
</script>

<style lang="scss" scoped>
.filter-panel {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
  display: none;
  
  &.is-show {
    display: block;
  }
}

.filter-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
}

.filter-content {
  position: absolute;
  right: 0;
  top: 0;
  bottom: 0;
  width: 80%;
  max-width: 600rpx;
  background: #FFFFFF;
  display: flex;
  flex-direction: column;
  animation: slideIn 0.3s ease;
}

@keyframes slideIn {
  from {
    transform: translateX(100%);
  }
  to {
    transform: translateX(0);
  }
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx;
  border-bottom: 1rpx solid #F0F0F0;
}

.header-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
}

.header-reset {
  font-size: 28rpx;
  color: #007AFF;
}

.filter-body {
  flex: 1;
  padding: 24rpx 32rpx;
}

.filter-group {
  margin-bottom: 32rpx;
}

.group-title {
  font-size: 28rpx;
  color: #333333;
  font-weight: 500;
  margin-bottom: 20rpx;
  display: block;
}

.group-options {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.option-item {
  padding: 16rpx 28rpx;
  background: #F5F5F5;
  border-radius: 8rpx;
  font-size: 26rpx;
  color: #666666;
  
  &.is-active {
    background: #E3F2FD;
    color: #007AFF;
  }
}

.filter-footer {
  display: flex;
  padding: 24rpx 32rpx;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
  border-top: 1rpx solid #F0F0F0;
  gap: 24rpx;
}

.btn {
  flex: 1;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12rpx;
  font-size: 32rpx;
  font-weight: 500;
}

.btn-cancel {
  background: #F5F5F5;
  color: #666666;
}

.btn-confirm {
  background: linear-gradient(135deg, #007AFF 0%, #5AC8FA 100%);
  color: #FFFFFF;
}
</style>
