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
            <div
              v-for="type in mineTypeOptions"
              :key="type.value"
              class="mine-dropdown-item"
              :class="{ active: mineFilterType === type.value }"
              @click="setMineType(type); showMineTypeFilter=false"
            >
              {{ type.label }}
            </div>
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
          <span class="mine-value">{{ item.typeName }}</span>
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
            <span class="detail-value">{{ currentDetail.typeName }}</span>
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
import { ref, computed, onMounted } from 'vue'
import { getMyViolations } from '@/api/violation'
import { ElMessage } from 'element-plus'

// 定义 emit 事件
const emit = defineEmits(['switchToTrack'])

// 我的违规数据
const mineList = ref([])
const loading = ref(false)
const total = ref(0)

const minePageSize = 4
const mineCurrentPage = ref(1)
const mineFilterType = ref('')
const mineFilterPlace = ref('全部')
const mineFilterTime = ref('')
const mineTypeOptions = [
  { label: '全部', value: '' },
  { label: '超速', value: 'speeding' },
  { label: '违停', value: 'illegal_parking' },
  { label: '逆行', value: 'wrong_way' },
  { label: '闯红灯', value: 'red_light' }
]
const minePlaceOptions = ['全部', '东门', '西门', '南门', '北门']

const showMineTypeFilter = ref(false)
const showMinePlaceFilter = ref(false)
const showMineTimeFilter = ref(false)
const showDetailDialog = ref(false)
const currentDetail = ref(null)

const typeLabelMap = {
  speeding: '超速',
  illegal_parking: '违停',
  wrong_way: '逆行',
  red_light: '闯红灯',
  parking: '违停',
  'Speeding': '超速',
  'Illegal Parking': '违停',
  'Wrong Way': '逆行',
  'Red Light': '闯红灯'
}

function getTypeLabel(type) {
  return typeLabelMap[type] || type || '未知类型'
}

// 从后端获取数据
async function fetchMyViolations() {
  loading.value = true
  try {
    const params = {
      page: mineCurrentPage.value,
      pageSize: minePageSize
    }
    
    // 添加筛选条件
    if (mineFilterType.value) {
      params.type = mineFilterType.value
    }
    if (mineFilterTime.value.trim()) {
      params.startTime = mineFilterTime.value.trim()
      params.endTime = mineFilterTime.value.trim()
    }
    
    const result = await getMyViolations(params)
    
    // 转换数据格式以适配模板
    mineList.value = (result.records || []).map(item => ({
      id: item.id,
      time: item.violationTime ? item.violationTime.replace('T', ' ').substring(0, 16) : '',
      place: item.place,
      type: item.type,
      typeName: getTypeLabel(item.type),
      detail: item.detail,
      // 保留原始数据
      ...item
    }))
    total.value = result.total || 0
  } catch (error) {
    console.error('获取违规记录失败:', error)
    // 使用示例数据作为后备
    mineList.value = [
      { id: 1, time: '2024-06-01 10:00', place: '东门', type: 'speeding', typeName: '超速', detail: '东门门口红绿灯处' },
      { id: 2, time: '2024-06-02 11:30', place: '西门', type: 'illegal_parking', typeName: '违停', detail: '西门停车场入口' },
    ]
    total.value = mineList.value.length
  } finally {
    loading.value = false
  }
}

// 本地筛选（地点筛选在前端处理）
const filteredMineList = computed(() => {
  let list = mineList.value
  if (mineFilterPlace.value !== '全部') {
    list = list.filter(m => m.place === mineFilterPlace.value)
  }
  return list
})

const mineTotalPage = computed(() => Math.max(1, Math.ceil(total.value / minePageSize)))
const pagedMineList = computed(() => filteredMineList.value)

function minePrevPage() {
  if (mineCurrentPage.value > 1) {
    mineCurrentPage.value--
    fetchMyViolations()
  }
}

function mineNextPage() {
  if (mineCurrentPage.value < mineTotalPage.value) {
    mineCurrentPage.value++
    fetchMyViolations()
  }
}

function setMineType(typeOption) {
  mineFilterType.value = typeOption.value
  mineCurrentPage.value = 1
  fetchMyViolations()
}

function setMinePlace(place) {
  mineFilterPlace.value = place
  mineCurrentPage.value = 1
}

function applyMineTimeFilter() {
  mineCurrentPage.value = 1
  showMineTimeFilter.value = false
  fetchMyViolations()
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

// 组件加载时获取数据
onMounted(() => {
  fetchMyViolations()
})
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
  margin-bottom: 24px;
  padding: 0;
}

.mine-filter-group {
  display: flex;
  gap: 12px;
}

.mine-filter-btn {
  background: #FFFFFF;
  border: 1px solid #E8E4DE;
  border-radius: 10px;
  padding: 10px 20px;
  font-size: 14px;
  font-weight: 500;
  color: #636E72;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(45, 52, 54, 0.04);
}

.mine-filter-btn:hover {
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
  color: #FFFFFF;
  border-color: transparent;
  box-shadow: 0 4px 12px rgba(107, 154, 196, 0.25);
}

.mine-dropdown {
  position: absolute;
  top: 45px;
  left: 0;
  background: #FFFFFF;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(45, 52, 54, 0.1);
  min-width: 140px;
  z-index: 10;
  padding: 8px 0;
  border: 1px solid #E8E4DE;
}

.mine-dropdown.time-filter {
  min-width: 220px;
}

.mine-dropdown-item {
  padding: 10px 18px;
  font-size: 14px;
  color: #636E72;
  cursor: pointer;
  transition: all 0.2s ease;
}

.mine-dropdown-item.active,
.mine-dropdown-item:hover {
  background: #FAF7F2;
  color: #2D3436;
}

.time-filter-btn {
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
  color: #FFFFFF;
  border: none;
  border-radius: 6px;
  padding: 6px 14px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.time-filter-btn:hover {
  opacity: 0.9;
}

.time-filter-btn.cancel {
  background: #FFFFFF;
  color: #636E72;
  border: 1px solid #E8E4DE;
}

.time-filter-btn.cancel:hover {
  background: #FAF7F2;
}

.mine-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  grid-template-rows: repeat(2, 1fr);
  gap: 24px;
  margin-bottom: 24px;
  min-height: 400px;
  flex: 1;
}

.mine-card {
  background: #FFFFFF;
  border-radius: 16px;
  border: 1px solid #E8E4DE;
  box-shadow: 0 2px 12px rgba(45, 52, 54, 0.04);
  padding: 24px 28px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.2s ease;
  min-height: 180px;
  justify-content: space-between;
}

.mine-card:hover {
  box-shadow: 0 4px 20px rgba(45, 52, 54, 0.08);
  transform: translateY(-2px);
  border-color: #6B9AC4;
}

.mine-row {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
  width: 100%;
  align-items: center;
}

.mine-label {
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
  color: #FFFFFF;
  border-radius: 8px;
  padding: 8px 16px;
  font-size: 13px;
  font-weight: 500;
  min-width: 80px;
  text-align: center;
}

.mine-value {
  font-size: 14px;
  color: #2D3436;
  flex: 1;
  margin-left: 8px;
  font-weight: 500;
}

.mine-detail-btn {
  margin-top: 12px;
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
  color: #FFFFFF;
  border: none;
  border-radius: 10px;
  padding: 10px 24px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  align-self: stretch;
  box-shadow: 0 2px 8px rgba(107, 154, 196, 0.2);
}

.mine-detail-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(107, 154, 196, 0.35);
}

.mine-bottom-bar {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}

.mine-page-group {
  display: flex;
  gap: 16px;
}

.page-btn.beautify {
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
  color: #FFFFFF;
  border: none;
  border-radius: 10px;
  padding: 10px 28px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(107, 154, 196, 0.2);
  transition: all 0.2s ease;
}

.page-btn.beautify:disabled {
  background: #E8E4DE;
  color: #9BA4A9;
  cursor: not-allowed;
  box-shadow: none;
}

.page-btn.beautify:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(107, 154, 196, 0.35);
}

/* 对话框样式 */
.detail-dialog-mask {
  position: fixed;
  left: 0; top: 0; right: 0; bottom: 0;
  background: rgba(45, 52, 54, 0.15);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.detail-dialog {
  background: #FFFFFF;
  border-radius: 20px;
  box-shadow: 0 8px 40px rgba(45, 52, 54, 0.15);
  padding: 32px 40px;
  min-width: 420px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-dialog-title {
  font-size: 20px;
  font-weight: 600;
  color: #2D3436;
  margin-bottom: 8px;
  text-align: center;
}

.detail-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin: 12px 0;
}

.detail-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #FAF7F2;
}

.detail-row:last-child {
  border-bottom: none;
}

.detail-label {
  color: #7D9E87;
  font-weight: 600;
  min-width: 100px;
  font-size: 14px;
}

.detail-value {
  font-size: 14px;
  color: #2D3436;
  flex: 1;
}

.detail-value.highlight {
  color: #6B9AC4;
  font-weight: 600;
  background: #E9F3FC;
  border-radius: 6px;
  padding: 4px 12px;
}

.detail-dialog-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 12px;
}

.detail-dialog-btn {
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
  color: #FFFFFF;
  border: none;
  border-radius: 10px;
  padding: 10px 28px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(107, 154, 196, 0.2);
}

.detail-dialog-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(107, 154, 196, 0.35);
}

.detail-dialog-btn.cancel {
  background: #FFFFFF;
  color: #636E72;
  border: 1px solid #E8E4DE;
  box-shadow: none;
}

.detail-dialog-btn.cancel:hover {
  background: #FAF7F2;
}
</style> 