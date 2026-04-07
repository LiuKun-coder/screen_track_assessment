<template>
  <div class="report-container">
    <div class="report-header">
      <div class="report-filter-group">
        <div style="position:relative;">
          <button class="report-filter-btn" @click="toggleReportTypeFilter">按违规方式筛选</button>
          <div v-if="showReportTypeFilter" class="report-dropdown">
            <div
              v-for="type in reportTypeOptions"
              :key="type.value"
              class="report-dropdown-item"
              :class="{ active: reportFilterType === type.value }"
              @click="setReportType(type); showReportTypeFilter=false"
            >
              {{ type.label }}
            </div>
          </div>
        </div>
        <div style="position:relative;">
          <button class="report-filter-btn" @click="toggleReportPlaceFilter">按违规地点筛选</button>
          <div v-if="showReportPlaceFilter" class="report-dropdown">
            <div v-for="place in reportPlaceOptions" :key="place" class="report-dropdown-item" :class="{ active: reportFilterPlace === place }" @click="setReportPlace(place); showReportPlaceFilter=false">{{ place }}</div>
          </div>
        </div>
        <div style="position:relative;">
          <button class="report-filter-btn" @click="toggleReportTimeFilter">按违规时间筛选</button>
          <div v-if="showReportTimeFilter" class="report-dropdown time-filter">
            <div style="padding: 10px 16px;">
              <label style="display:block; margin-bottom:6px; color:#7c3aed; font-size:14px; font-weight:bold;">选择日期：</label>
              <input type="date" v-model="reportFilterTime" style="width:100%; border:2px solid #e0e7ff; border-radius:8px; padding:6px 8px; font-size:14px;" />
              <div style="margin-top:8px; display:flex; gap:8px;">
                <button class="time-filter-btn" @click="applyTimeFilter">确定</button>
                <button class="time-filter-btn cancel" @click="reportFilterTime=''; applyTimeFilter()">清空</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="report-list">
      <div class="report-item" v-for="item in pagedReportList" :key="item.id">
        <div class="report-info">
          <div class="report-meta">
            <span class="report-name">{{ item.name }}</span>
            <span class="report-type">{{ item.typeName }}</span>
          </div>
          <div class="report-detail">
            <span class="report-time">{{ item.time }}</span>
            <span class="report-place">{{ item.place }}</span>
            <span class="report-phone">{{ item.phone }}</span>
          </div>
        </div>
      </div>
    </div>
    <div class="report-pagination">
      <button class="page-btn beautify" @click="reportPrevPage" :disabled="reportCurrentPage === 1">上一页</button>
      <span style="margin: 0 16px; color: #888; font-size: 16px;">{{ reportCurrentPage }} / {{ reportTotalPage }}</span>
      <button class="page-btn beautify" @click="reportNextPage" :disabled="reportCurrentPage === reportTotalPage">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getViolationList } from '@/api/violation'

// 违规通报数据
const reportList = ref([])
const loading = ref(false)
const total = ref(0)

const reportPageSize = 5
const reportCurrentPage = ref(1)
const reportFilterType = ref('')
const reportFilterPlace = ref('全部')
const reportFilterTime = ref('')
const reportTypeOptions = [
  { label: '全部', value: '' },
  { label: '超速', value: 'speeding' },
  { label: '违停', value: 'illegal_parking' },
  { label: '逆行', value: 'wrong_way' },
  { label: '闯红灯', value: 'red_light' }
]
const reportPlaceOptions = ['全部', '东门', '西门', '南门', '北门']

const showReportTypeFilter = ref(false)
const showReportPlaceFilter = ref(false)
const showReportTimeFilter = ref(false)

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
async function fetchViolationList() {
  loading.value = true
  try {
    const params = {
      page: reportCurrentPage.value,
      pageSize: reportPageSize
    }
    
    // 添加筛选条件
    if (reportFilterType.value) {
      params.type = reportFilterType.value
    }
    if (reportFilterTime.value.trim()) {
      params.startTime = reportFilterTime.value.trim()
      params.endTime = reportFilterTime.value.trim()
    }
    
    const result = await getViolationList(params)
    
    // 转换数据格式以适配模板
    reportList.value = (result.records || []).map(item => ({
      id: item.id,
      name: item.userName || '未知用户',
      time: item.violationTime ? item.violationTime.replace('T', ' ').substring(0, 16) : '',
      place: item.place,
      type: item.type,
      typeName: getTypeLabel(item.type),
      phone: item.phone || '***',
      // 保留原始数据
      ...item
    }))
    total.value = result.total || 0
  } catch (error) {
    console.error('获取违规列表失败:', error)
    // 使用示例数据作为后备
    reportList.value = [
      { id: 1, name: '用户A', time: '2024-06-01 10:00', place: '东门', type: '超速', phone: '138****8888' },
      { id: 2, name: '用户B', time: '2024-06-02 11:30', place: '西门', type: '违停', phone: '139****6666' },
    ]
    total.value = reportList.value.length
  } finally {
    loading.value = false
  }
}

// 本地筛选（地点筛选在前端处理）
const filteredReportList = computed(() => {
  let list = reportList.value
  if (reportFilterPlace.value !== '全部') {
    list = list.filter(r => r.place === reportFilterPlace.value)
  }
  return list
})

const reportTotalPage = computed(() => Math.max(1, Math.ceil(total.value / reportPageSize)))
const pagedReportList = computed(() => filteredReportList.value)

function reportPrevPage() {
  if (reportCurrentPage.value > 1) {
    reportCurrentPage.value--
    fetchViolationList()
  }
}

function reportNextPage() {
  if (reportCurrentPage.value < reportTotalPage.value) {
    reportCurrentPage.value++
    fetchViolationList()
  }
}

function setReportType(typeOption) {
  reportFilterType.value = typeOption.value
  reportCurrentPage.value = 1
  fetchViolationList()
}

function setReportPlace(place) {
  reportFilterPlace.value = place
  reportCurrentPage.value = 1
}

function applyTimeFilter() {
  reportCurrentPage.value = 1
  showReportTimeFilter.value = false
  fetchViolationList()
}

function toggleReportTypeFilter() {
  showReportTypeFilter.value = !showReportTypeFilter.value
  showReportPlaceFilter.value = false
  showReportTimeFilter.value = false
}

function toggleReportPlaceFilter() {
  showReportPlaceFilter.value = !showReportPlaceFilter.value
  showReportTypeFilter.value = false
  showReportTimeFilter.value = false
}

function toggleReportTimeFilter() {
  showReportTimeFilter.value = !showReportTimeFilter.value
  showReportTypeFilter.value = false
  showReportPlaceFilter.value = false
}

// 组件加载时获取数据
onMounted(() => {
  fetchViolationList()
})
</script>

<style scoped>
.report-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.report-header {
  margin-bottom: 24px;
  display: flex;
  justify-content: flex-start;
}

.report-filter-group {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.report-filter-btn {
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

.report-filter-btn.active, .report-filter-btn:hover {
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
  color: #FFFFFF;
  border-color: transparent;
  box-shadow: 0 4px 12px rgba(107, 154, 196, 0.25);
}

.report-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 24px;
}

.report-item {
  background: #FFFFFF;
  border-radius: 16px;
  border: 1px solid #E8E4DE;
  box-shadow: 0 2px 12px rgba(45, 52, 54, 0.04);
  padding: 20px 28px;
  display: flex;
  align-items: center;
  transition: all 0.2s ease;
}

.report-item:hover {
  box-shadow: 0 4px 20px rgba(45, 52, 54, 0.08);
  transform: translateY(-2px);
  border-color: #6B9AC4;
}

.report-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.report-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.report-name {
  font-size: 16px;
  font-weight: 600;
  color: #2D3436;
}

.report-type {
  font-size: 13px;
  color: #7D9E87;
  background: #E7F2EA;
  border-radius: 6px;
  padding: 4px 12px;
  font-weight: 500;
}

.report-detail {
  display: flex;
  gap: 24px;
  font-size: 14px;
  color: #636E72;
}

.report-time {
  color: #6B9AC4;
}

.report-place {
  color: #D4A574;
}

.report-phone {
  color: #9BA4A9;
}

.report-pagination {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 16px;
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

.report-dropdown {
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

.report-dropdown-item {
  padding: 10px 18px;
  font-size: 14px;
  color: #636E72;
  cursor: pointer;
  transition: all 0.2s ease;
}

.report-dropdown-item.active,
.report-dropdown-item:hover {
  background: #FAF7F2;
  color: #2D3436;
}

.report-dropdown.time-filter {
  min-width: 220px;
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
</style> 