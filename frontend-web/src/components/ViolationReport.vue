<template>
  <div class="report-container">
    <div class="report-header">
      <div class="report-filter-group">
        <div style="position:relative;">
          <button class="report-filter-btn" @click="toggleReportTypeFilter">按违规方式筛选</button>
          <div v-if="showReportTypeFilter" class="report-dropdown">
            <div v-for="type in reportTypeOptions" :key="type" class="report-dropdown-item" :class="{ active: reportFilterType === type }" @click="setReportType(type); showReportTypeFilter=false">{{ type }}</div>
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
            <span class="report-type">{{ item.type }}</span>
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
import { ref, computed } from 'vue'

// 违规通报示例数据与功能
const reportList = ref([
  { id: 1, name: '用户A', time: '2024-06-01 10:00', place: '东门', type: '超速', phone: '138****8888' },
  { id: 2, name: '用户B', time: '2024-06-02 11:30', place: '西门', type: '违停', phone: '139****6666' },
  { id: 3, name: '用户C', time: '2024-06-03 09:20', place: '南门', type: '超速', phone: '137****7777' },
  { id: 4, name: '用户D', time: '2024-06-04 14:10', place: '北门', type: '违停', phone: '136****8888' },
  { id: 5, name: '用户E', time: '2024-06-05 16:40', place: '东门', type: '违停', phone: '135****9999' },
  { id: 6, name: '用户F', time: '2024-06-06 13:00', place: '西门', type: '超速', phone: '134****0000' },
  { id: 7, name: '用户G', time: '2024-06-07 08:50', place: '南门', type: '违停', phone: '133****1111' },
  { id: 8, name: '用户H', time: '2024-06-08 12:20', place: '北门', type: '超速', phone: '132****2222' },
])

const reportPageSize = 5
const reportCurrentPage = ref(1)
const reportFilterType = ref('全部')
const reportFilterPlace = ref('全部')
const reportFilterTime = ref('')
const reportTypeOptions = ['全部', '超速', '违停']
const reportPlaceOptions = ['全部', '东门', '西门', '南门', '北门']

const showReportTypeFilter = ref(false)
const showReportPlaceFilter = ref(false)
const showReportTimeFilter = ref(false)

const filteredReportList = computed(() => {
  let list = reportList.value
  if (reportFilterType.value !== '全部') list = list.filter(r => r.type === reportFilterType.value)
  if (reportFilterPlace.value !== '全部') list = list.filter(r => r.place === reportFilterPlace.value)
  if (reportFilterTime.value.trim()) list = list.filter(r => r.time.startsWith(reportFilterTime.value.trim()))
  return list
})

const reportTotalPage = computed(() => Math.ceil(filteredReportList.value.length / reportPageSize))
const pagedReportList = computed(() => {
  const start = (reportCurrentPage.value - 1) * reportPageSize
  return filteredReportList.value.slice(start, start + reportPageSize)
})

function reportPrevPage() {
  if (reportCurrentPage.value > 1) reportCurrentPage.value--
}

function reportNextPage() {
  if (reportCurrentPage.value < reportTotalPage.value) reportCurrentPage.value++
}

function setReportType(type) {
  reportFilterType.value = type
  reportCurrentPage.value = 1
}

function setReportPlace(place) {
  reportFilterPlace.value = place
  reportCurrentPage.value = 1
}

function applyTimeFilter() {
  reportCurrentPage.value = 1
  showReportTimeFilter.value = false
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
</script>

<style scoped>
.report-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.report-header {
  margin-bottom: 18px;
  display: flex;
  justify-content: flex-start;
}

.report-filter-group {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 18px;
}

.report-filter-btn {
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

.report-filter-btn.active, .report-filter-btn:hover {
  background: linear-gradient(90deg, #4f8cff 0%, #7c3aed 100%);
  color: #fff;
  transform: scale(1.07);
  box-shadow: 0 4px 16px 0 #4f8cff33;
}

.report-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 28px;
  margin-bottom: 32px;
}

.report-item {
  background: linear-gradient(90deg, #f0f5ff 0%, #fff 100%);
  border-radius: 20px;
  border: 2.5px solid #e0e7ff;
  box-shadow: 0 4px 24px 0 #4f8cff11, 0 1.5px 6px 0 #ffb6c122;
  padding: 22px 38px;
  display: flex;
  align-items: center;
  transition: box-shadow 0.25s, transform 0.18s, border 0.25s;
  position: relative;
  overflow: hidden;
}

.report-item:hover {
  box-shadow: 0 8px 32px 0 #4f8cff33, 0 2px 8px 0 #ffb6c133;
  border: 2.5px solid #7c3aed;
  transform: translateY(-3px) scale(1.03);
  z-index: 1;
}

.report-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.report-meta {
  display: flex;
  align-items: center;
  gap: 18px;
}

.report-name {
  font-size: 20px;
  font-weight: bold;
  color: #4f8cff;
  letter-spacing: 1px;
}

.report-type {
  font-size: 16px;
  color: #7c3aed;
  background: #e0e7ff;
  border-radius: 8px;
  padding: 2px 12px;
  margin-left: 8px;
}

.report-detail {
  display: flex;
  gap: 32px;
  font-size: 16px;
  color: #333;
}

.report-time {
  color: #7c3aed;
}

.report-place {
  color: #4f8cff;
}

.report-phone {
  color: #888;
}

.report-pagination {
  display: flex;
  justify-content: flex-end;
  gap: 24px;
  margin-top: 16px;
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

.report-dropdown {
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

.report-dropdown-item {
  padding: 8px 18px;
  font-size: 15px;
  color: #4f8cff;
  cursor: pointer;
  transition: background 0.18s, color 0.18s;
}

.report-dropdown-item.active,
.report-dropdown-item:hover {
  background: #e0e7ff;
  color: #7c3aed;
}

.report-dropdown.time-filter {
  min-width: 200px;
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
</style> 