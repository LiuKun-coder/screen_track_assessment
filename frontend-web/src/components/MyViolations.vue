<template>
  <div class="mine-container">
    <!-- 筛选按钮组移动到顶部 -->
    <div class="mine-filter-section">
      <div class="mine-filter-group">
        <div style="position:relative;">
          <button class="mine-filter-btn" @click="toggleMineTimeFilter">按时间筛选</button>
          <div v-if="showMineTimeFilter" class="mine-dropdown time-filter">
            <div style="padding: 10px 16px;">
              <label style="display:block; margin-bottom:6px; color:#7c3aed; font-size:14px; font-weight:bold;">选择日期：</label>
              <input type="date" v-model="mineFilterTime" style="width:100%; border:2px solid #e0e7ff; border-radius:8px; padding:6px 8px; font-size:14px;" />
              <div style="margin-top:8px; display:flex; gap:8px;">
                <button class="time-filter-btn" @click="applyMineTimeFilter">确定</button>
                <button class="time-filter-btn cancel" @click="mineFilterTime=''; applyMineTimeFilter()">清空</button>
              </div>
            </div>
          </div>
        </div>
        <div style="position:relative;">
          <button class="mine-filter-btn" @click="toggleMinePlaceFilter">按地点筛选</button>
          <div v-if="showMinePlaceFilter" class="mine-dropdown">
            <div v-for="place in minePlaceOptions" :key="place" class="mine-dropdown-item" :class="{ active: mineFilterPlace === place }" @click="setMinePlace(place); showMinePlaceFilter=false">{{ place }}</div>
          </div>
        </div>
        <div style="position:relative;">
          <button class="mine-filter-btn" @click="toggleMineTypeFilter">按类型筛选</button>
          <div v-if="showMineTypeFilter" class="mine-dropdown">
            <div v-for="type in mineTypeOptions" :key="type" class="mine-dropdown-item" :class="{ active: mineFilterType === type }" @click="setMineType(type); showMineTypeFilter=false">{{ type }}</div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="mine-grid">
      <div class="mine-card" v-for="item in pagedMineList" :key="item.id">
        <div class="mine-row">
          <span class="mine-label">违规时间</span>
          <span class="mine-value">{{ item.time }}</span>
        </div>
        <div class="mine-row">
          <span class="mine-label">违规地点</span>
          <span class="mine-value">{{ item.place }}</span>
        </div>
        <div class="mine-row">
          <span class="mine-label">违规类型</span>
          <span class="mine-value">{{ item.type }}</span>
        </div>
        <button class="mine-detail-btn" @click="viewDetail(item)">查看具体违规地点</button>
      </div>
    </div>
    
    <!-- 底部只保留分页按钮 -->
    <div class="mine-bottom-bar">
      <div class="mine-page-group">
        <button class="page-btn beautify" @click="minePrevPage" :disabled="mineCurrentPage === 1">上一页</button>
        <button class="page-btn beautify" @click="mineNextPage" :disabled="mineCurrentPage === mineTotalPage">下一页</button>
      </div>
    </div>
    
    <!-- 违规详情对话框 -->
    <div v-if="showDetailDialog" class="detail-dialog-mask">
      <div class="detail-dialog">
        <div class="detail-dialog-title">违规详情</div>
        <div class="detail-info" v-if="currentDetail">
          <div class="detail-row">
            <span class="detail-label">违规时间：</span>
            <span class="detail-value">{{ currentDetail.time }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">违规地点：</span>
            <span class="detail-value">{{ currentDetail.place }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">违规类型：</span>
            <span class="detail-value">{{ currentDetail.type }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">具体位置：</span>
            <span class="detail-value highlight">{{ currentDetail.detail }}</span>
          </div>
        </div>
        <div class="detail-dialog-actions">
          <button class="detail-dialog-btn" @click="viewTrajectory">查看轨迹回溯</button>
          <button class="detail-dialog-btn cancel" @click="closeDetailDialog">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// 定义 emit 事件
const emit = defineEmits(['switchToTrack'])

// 我的违规示例数据与功能
const mineList = ref([
  { id: 1, time: '2024-06-01 10:00', place: '东门', type: '超速', detail: '东门门口红绿灯处' },
  { id: 2, time: '2024-06-02 11:30', place: '西门', type: '违停', detail: '西门停车场入口' },
  { id: 3, time: '2024-06-03 09:20', place: '南门', type: '超速', detail: '南门主干道' },
  { id: 4, time: '2024-06-04 14:10', place: '北门', type: '违停', detail: '北门宿舍楼下' },
  { id: 5, time: '2024-06-05 16:40', place: '东门', type: '违停', detail: '东门食堂门口' },
  { id: 6, time: '2024-06-06 13:00', place: '西门', type: '超速', detail: '西门体育场附近' },
  { id: 7, time: '2024-06-07 08:50', place: '南门', type: '违停', detail: '南门图书馆前' },
  { id: 8, time: '2024-06-08 12:20', place: '北门', type: '超速', detail: '北门教学楼区域' },
])

const minePageSize = 4
const mineCurrentPage = ref(1)
const mineFilterType = ref('全部')
const mineFilterPlace = ref('全部')
const mineFilterTime = ref('')
const mineTypeOptions = ['全部', '超速', '违停']
const minePlaceOptions = ['全部', '东门', '西门', '南门', '北门']

const showMineTypeFilter = ref(false)
const showMinePlaceFilter = ref(false)
const showMineTimeFilter = ref(false)
const showDetailDialog = ref(false)
const currentDetail = ref(null)

const filteredMineList = computed(() => {
  let list = mineList.value
  if (mineFilterType.value !== '全部') list = list.filter(m => m.type === mineFilterType.value)
  if (mineFilterPlace.value !== '全部') list = list.filter(m => m.place === mineFilterPlace.value)
  if (mineFilterTime.value.trim()) list = list.filter(m => m.time.startsWith(mineFilterTime.value.trim()))
  return list
})

const mineTotalPage = computed(() => Math.ceil(filteredMineList.value.length / minePageSize))
const pagedMineList = computed(() => {
  const start = (mineCurrentPage.value - 1) * minePageSize
  return filteredMineList.value.slice(start, start + minePageSize)
})

function minePrevPage() {
  if (mineCurrentPage.value > 1) mineCurrentPage.value--
}

function mineNextPage() {
  if (mineCurrentPage.value < mineTotalPage.value) mineCurrentPage.value++
}

function setMineType(type) {
  mineFilterType.value = type
  mineCurrentPage.value = 1
}

function setMinePlace(place) {
  mineFilterPlace.value = place
  mineCurrentPage.value = 1
}

function applyMineTimeFilter() {
  mineCurrentPage.value = 1
  showMineTimeFilter.value = false
}

function toggleMineTypeFilter() {
  showMineTypeFilter.value = !showMineTypeFilter.value
  showMinePlaceFilter.value = false
  showMineTimeFilter.value = false
}

function toggleMinePlaceFilter() {
  showMinePlaceFilter.value = !showMinePlaceFilter.value
  showMineTypeFilter.value = false
  showMineTimeFilter.value = false
}

function toggleMineTimeFilter() {
  showMineTimeFilter.value = !showMineTimeFilter.value
  showMineTypeFilter.value = false
  showMinePlaceFilter.value = false
}

function viewDetail(item) {
  currentDetail.value = item
  showDetailDialog.value = true
}

function closeDetailDialog() {
  showDetailDialog.value = false
  currentDetail.value = null
}

function viewTrajectory() {
  showDetailDialog.value = false
  // 通过 emit 事件通知父组件切换到轨迹回溯页面并传递数据
  emit('switchToTrack', currentDetail.value)
}
</script>

<style scoped>
.mine-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.mine-filter-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 0 20px; /* Add some padding to the top */
}

.mine-filter-group {
  display: flex;
  gap: 32px;
}

.mine-filter-btn {
  background: #fff;
  border: 2px solid #4f8cff;
  border-radius: 10px;
  padding: 7px 22px;
  font-size: 16px;
  font-weight: bold;
  color: #4f8cff;
  cursor: pointer;
  transition: background 0.22s, color 0.22s, box-shadow 0.22s, transform 0.18s;
  box-shadow: 0 2px 8px 0 #4f8cff11;
}

.mine-filter-btn:hover {
  background: linear-gradient(90deg, #4f8cff 0%, #7c3aed 100%);
  color: #fff;
  transform: scale(1.07);
  box-shadow: 0 4px 16px 0 #4f8cff33;
}

.mine-dropdown {
  position: absolute;
  top: 40px;
  left: 0;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 24px 0 #4f8cff22;
  min-width: 120px;
  z-index: 10;
  padding: 6px 0;
  border: 1.5px solid #e0e7ff;
}

.mine-dropdown.time-filter {
  min-width: 200px;
}

.mine-dropdown-item {
  padding: 8px 18px;
  font-size: 15px;
  color: #4f8cff;
  cursor: pointer;
  transition: background 0.18s, color 0.18s;
}

.mine-dropdown-item.active,
.mine-dropdown-item:hover {
  background: #e0e7ff;
  color: #7c3aed;
}

.time-filter-btn {
  background: #4f8cff;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 4px 12px;
  font-size: 13px;
  cursor: pointer;
  transition: background 0.2s;
}

.time-filter-btn:hover {
  background: #7c3aed;
}

.time-filter-btn.cancel {
  background: #fff;
  color: #4f8cff;
  border: 1px solid #4f8cff;
}

.time-filter-btn.cancel:hover {
  background: #f0f5ff;
}

.mine-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  grid-template-rows: repeat(2, 1fr);
  gap: 40px 48px;
  margin-bottom: 40px;
  min-height: 480px;
  flex: 1;
}

.mine-card {
  background: linear-gradient(90deg, #f0f5ff 0%, #fff 100%);
  border-radius: 24px;
  border: 2.5px solid #e0e7ff;
  box-shadow: 0 4px 24px 0 #4f8cff11, 0 1.5px 6px 0 #ffb6c122;
  padding: 32px 36px 28px 36px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  font-size: 20px;
  font-weight: 500;
  transition: box-shadow 0.25s, transform 0.18s, border 0.25s;
  position: relative;
  overflow: hidden;
  min-height: 200px;
  justify-content: space-between;
}

.mine-card:hover {
  box-shadow: 0 8px 32px 0 #4f8cff33, 0 2px 8px 0 #ffb6c133;
  border: 2.5px solid #7c3aed;
  transform: translateY(-3px) scale(1.03);
  z-index: 1;
}

.mine-row {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
  width: 100%;
  align-items: center;
}

.mine-label {
  background: linear-gradient(90deg, #7c3aed 0%, #4f8cff 100%);
  color: #fff;
  border-radius: 12px;
  padding: 10px 28px;
  font-size: 16px;
  font-weight: 500;
  box-shadow: 0 2px 8px 0 #4f8cff22;
  min-width: 96px;
  text-align: center;
}

.mine-value {
  font-size: 17px;
  color: #333;
  flex: 1;
  margin-left: 8px;
  font-weight: 500;
}

.mine-detail-btn {
  margin-top: 12px;
  background: linear-gradient(90deg, #7c3aed 0%, #4f8cff 100%);
  color: #fff;
  border: none;
  border-radius: 12px;
  padding: 12px 32px;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.22s, transform 0.18s, box-shadow 0.22s;
  align-self: stretch;
  box-shadow: 0 4px 16px 0 #7c3aed22;
}

.mine-detail-btn:hover {
  background: linear-gradient(90deg, #ffb6c1 0%, #4f8cff 100%);
  color: #222;
  transform: scale(1.04);
  box-shadow: 0 8px 32px 0 #ffb6c144;
}

.mine-bottom-bar {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}

.mine-page-group {
  display: flex;
  gap: 24px;
}

.page-btn.beautify {
  background: linear-gradient(90deg, #7c3aed 0%, #4f8cff 100%);
  color: #fff;
  border: none;
  border-radius: 14px;
  padding: 10px 36px;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  box-shadow: 0 4px 16px 0 #7c3aed22;
  transition: background 0.25s, transform 0.18s, box-shadow 0.25s;
  letter-spacing: 1px;
  margin: 0 2px;
}

.page-btn.beautify:disabled {
  background: #e0e7ff;
  color: #aaa;
  cursor: not-allowed;
  box-shadow: none;
}

.page-btn.beautify:hover:not(:disabled) {
  background: linear-gradient(90deg, #ffb6c1 0%, #4f8cff 100%);
  color: #222;
  transform: translateY(-2px) scale(1.04);
  box-shadow: 0 8px 32px 0 #ffb6c144;
}

/* 对话框样式 */
.detail-dialog-mask {
  position: fixed;
  left: 0; top: 0; right: 0; bottom: 0;
  background: rgba(79,140,255,0.12);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.detail-dialog {
  background: #fff;
  border-radius: 24px;
  box-shadow: 0 8px 40px 0 #4f8cff33;
  padding: 36px 48px 28px 48px;
  min-width: 450px;
  display: flex;
  flex-direction: column;
  gap: 18px;
  position: relative;
}

.detail-dialog-title {
  font-size: 22px;
  font-weight: bold;
  color: #4f8cff;
  margin-bottom: 8px;
  text-align: center;
}

.detail-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin: 12px 0;
}

.detail-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f0f5ff;
}

.detail-row:last-child {
  border-bottom: none;
}

.detail-label {
  color: #7c3aed;
  font-weight: bold;
  min-width: 100px;
  font-size: 16px;
}

.detail-value {
  font-size: 16px;
  color: #333;
  flex: 1;
}

.detail-value.highlight {
  color: #4f8cff;
  font-weight: bold;
  background: #e0e7ff;
  border-radius: 8px;
  padding: 4px 12px;
}

.detail-dialog-actions {
  display: flex;
  justify-content: center;
  gap: 18px;
  margin-top: 12px;
}

.detail-dialog-btn {
  background: linear-gradient(90deg, #7c3aed 0%, #4f8cff 100%);
  color: #fff;
  border: none;
  border-radius: 12px;
  padding: 10px 32px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.22s, color 0.22s, transform 0.18s, box-shadow 0.22s;
  box-shadow: 0 4px 16px 0 #7c3aed22;
}

.detail-dialog-btn:hover {
  background: linear-gradient(90deg, #ffb6c1 0%, #4f8cff 100%);
  color: #222;
  transform: scale(1.06);
  box-shadow: 0 8px 32px 0 #ffb6c144;
}

.detail-dialog-btn.cancel {
  background: #fff;
  color: #4f8cff;
  border: 2px solid #4f8cff;
  box-shadow: 0 2px 8px 0 #4f8cff11;
}

.detail-dialog-btn.cancel:hover {
  background: #f0f5ff;
  color: #222;
  box-shadow: 0 4px 16px 0 #4f8cff22;
}
</style> 