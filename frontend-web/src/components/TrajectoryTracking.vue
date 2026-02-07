<template>
  <div class="track-container">
    <div class="track-left">
      <button class="track-btn" @click="openDatePicker">选择要查看的回溯日期</button>
      <button v-if="userRole === 'admin'" class="track-btn" @click="openUserTrackDialog">查看用户的回溯轨迹</button>
      <button class="track-btn" @click="viewFence">查看电子围栏</button>
      <button v-if="userRole === 'admin'" class="track-btn" @click="editFence">更改电子围栏</button>
    </div>
    <div class="track-right">
      <!-- 违规信息显示面板 -->
      <div v-if="currentViolationInfo" class="violation-info-panel">
        <div class="panel-header">
          <span class="panel-title">违规信息详情</span>
          <button class="close-panel-btn" @click="clearViolationInfo">×</button>
        </div>
        <div class="panel-content">
          <div class="info-item">
            <span class="info-label">违规时间</span>
            <span class="info-value">{{ currentViolationInfo.time }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">违规地点</span>
            <span class="info-value">{{ currentViolationInfo.place }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">违规类型</span>
            <span class="info-value violation-type">{{ currentViolationInfo.type }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">具体位置</span>
            <span class="info-value highlight">{{ currentViolationInfo.detail }}</span>
          </div>
        </div>
      </div>
      <div class="track-map">
        <div class="track-map-placeholder">
          <div class="map-title">用于展示路线回溯的学校地图</div>
          <div class="map-content">{{ mapData }}</div>
        </div>
      </div>
    </div>
    
    <!-- 日期选择对话框 -->
    <div v-if="showDatePicker" class="track-dialog-mask">
      <div class="track-dialog">
        <div class="track-dialog-title">选择回溯日期</div>
        <div class="track-dialog-row">
          <label>选择日期：</label>
          <input type="date" v-model="selectedDate" style="flex:1; border:2px solid #e0e7ff; border-radius:8px; padding:8px 12px; font-size:16px;" />
        </div>
        <div class="track-dialog-actions">
          <button class="track-dialog-btn" @click="selectDate">确定</button>
          <button class="track-dialog-btn cancel" @click="showDatePicker = false">取消</button>
        </div>
      </div>
    </div>
    
    <!-- 查看电子围栏对话框 -->
    <div v-if="showFenceDialog" class="track-dialog-mask">
      <div class="track-dialog">
        <div class="track-dialog-title">电子围栏信息</div>
        <div class="fence-info">
          <div class="fence-item">
            <span class="fence-label">围栏名称：</span>
            <span>主校区安全围栏</span>
          </div>
          <div class="fence-item">
            <span class="fence-label">围栏范围：</span>
            <span>东门-西门-南门-北门</span>
          </div>
          <div class="fence-item">
            <span class="fence-label">围栏状态：</span>
            <span class="fence-status active">启用中</span>
          </div>
          <div class="fence-item">
            <span class="fence-label">创建时间：</span>
            <span>2024-01-01 00:00:00</span>
          </div>
        </div>
        <div class="track-dialog-actions">
          <button class="track-dialog-btn" @click="closeFenceDialog">确定</button>
        </div>
      </div>
    </div>
    
    <!-- 查看用户回溯轨迹对话框 -->
    <div v-if="showUserTrackDialog" class="track-dialog-mask">
      <div class="track-dialog user-track-dialog">
        <div class="track-dialog-title">查看用户的回溯轨迹</div>
        
        <!-- 用户搜索部分 -->
        <div class="user-search-section">
          <div class="search-header">
            <span class="search-label">搜索用户：</span>
            <div class="search-input-container">
              <input 
                v-model="userSearchKeyword" 
                @input="handleUserSearch"
                placeholder="输入姓名、工号或学号搜索..." 
                class="user-search-input"
              />
              <div class="search-icon">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
                </svg>
              </div>
            </div>
          </div>
          
          <!-- 用户列表 -->
          <div class="user-list" v-if="filteredUsers.length > 0">
            <div 
              v-for="user in filteredUsers" 
              :key="user.id"
              class="user-item"
              :class="{ selected: selectedUser?.id === user.id }"
              @click="selectUser(user)"
            >
              <div class="user-avatar">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
                </svg>
              </div>
              <div class="user-info">
                <div class="user-name">{{ user.name }}</div>
                <div class="user-id">{{ user.type === 'student' ? '学号' : '工号' }}: {{ user.id }}</div>
                <div class="user-department">{{ user.department }}</div>
              </div>
              <div class="user-type" :class="user.type">
                {{ user.type === 'student' ? '学生' : user.type === 'teacher' ? '教师' : '职工' }}
              </div>
            </div>
          </div>
          
          <!-- 空状态 -->
          <div v-else-if="userSearchKeyword" class="empty-users">
            <div class="empty-icon">
              <svg width="48" height="48" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
              </svg>
            </div>
            <div class="empty-text">未找到匹配的用户</div>
          </div>
        </div>
        
        <!-- 已选择用户显示 -->
        <div v-if="selectedUser" class="selected-user-section">
          <div class="selected-user-header">
            <span class="selected-label">已选择用户：</span>
            <button class="clear-selection-btn" @click="clearUserSelection">×</button>
          </div>
          <div class="selected-user-card">
            <div class="user-avatar">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
              </svg>
            </div>
            <div class="user-info">
              <div class="user-name">{{ selectedUser.name }}</div>
              <div class="user-details">
                <span>{{ selectedUser.type === 'student' ? '学号' : '工号' }}: {{ selectedUser.id }}</span>
                <span>{{ selectedUser.department }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 日期选择部分 -->
        <div class="date-selection-section">
          <div class="track-dialog-row">
            <label>选择查看日期：</label>
            <input 
              type="date" 
              v-model="userTrackDate" 
              class="date-input"
            />
          </div>
        </div>
        
        <div class="track-dialog-actions">
          <button 
            class="track-dialog-btn" 
            @click="viewUserTrack"
            :disabled="!selectedUser || !userTrackDate"
          >
            查看轨迹
          </button>
          <button class="track-dialog-btn cancel" @click="closeUserTrackDialog">取消</button>
        </div>
      </div>
    </div>

    <!-- 更改电子围栏对话框 -->
    <div v-if="showEditFenceDialog" class="track-dialog-mask">
      <div class="track-dialog">
        <div class="track-dialog-title">更改电子围栏</div>
        <div class="track-dialog-row">
          <label>围栏名称：</label>
          <input v-model="fenceName" placeholder="请输入围栏名称" style="flex:1; border:2px solid #e0e7ff; border-radius:8px; padding:8px 12px; font-size:16px;" />
        </div>
        <div class="track-dialog-row">
          <label>围栏范围：</label>
          <input v-model="fenceRange" placeholder="请输入围栏范围" style="flex:1; border:2px solid #e0e7ff; border-radius:8px; padding:8px 12px; font-size:16px;" />
        </div>
        <div class="track-dialog-row">
          <label>围栏状态：</label>
          <select v-model="fenceStatus" style="flex:1; border:2px solid #e0e7ff; border-radius:8px; padding:8px 12px; font-size:16px;">
            <option value="启用">启用</option>
            <option value="禁用">禁用</option>
          </select>
        </div>
        <div class="track-dialog-actions">
          <button class="track-dialog-btn" @click="saveFence">保存</button>
          <button class="track-dialog-btn cancel" @click="closeEditFenceDialog">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getTrackByDate } from '@/api/track'
import { getFenceList, addFence, updateFence } from '@/api/fence'
import { ElMessage } from 'element-plus'

// Props 接收违规信息和用户角色
const props = defineProps({
  violationInfo: {
    type: Object,
    default: null
  },
  userRole: {
    type: String,
    default: 'user'
  }
})

// 轨迹回溯功能
const showDatePicker = ref(false)
const selectedDate = ref('')
const showFenceDialog = ref(false)
const showEditFenceDialog = ref(false)
const showUserTrackDialog = ref(false)
const mapData = ref('学校地图加载中...')
const currentViolationInfo = ref(props.violationInfo)
const userRole = ref(props.userRole)
const loading = ref(false)

// 当前轨迹数据
const trackPoints = ref([])

// 电子围栏数据
const currentFence = ref(null)

// 用户轨迹查看相关数据
const userSearchKeyword = ref('')
const selectedUser = ref(null)
const userTrackDate = ref('')
const allUsers = ref([
  { id: '2021001', name: '张三', type: 'student', department: '计算机学院' },
  { id: '2021002', name: '李四', type: 'student', department: '电子工程学院' },
  { id: '2021003', name: '王五', type: 'student', department: '机械工程学院' },
  { id: '2021004', name: '赵六', type: 'student', department: '化学工程学院' },
  { id: '2021005', name: '钱七', type: 'student', department: '物理学院' },
  { id: 'T001', name: '刘老师', type: 'teacher', department: '计算机学院' },
  { id: 'T002', name: '陈老师', type: 'teacher', department: '电子工程学院' },
  { id: 'T003', name: '黄老师', type: 'teacher', department: '机械工程学院' },
  { id: 'S001', name: '孙职工', type: 'staff', department: '后勤服务中心' },
  { id: 'S002', name: '周职工', type: 'staff', department: '图书馆' },
  { id: '2021006', name: '吴八', type: 'student', department: '数学学院' },
  { id: '2021007', name: '郑九', type: 'student', department: '外语学院' },
  { id: 'T004', name: '马老师', type: 'teacher', department: '数学学院' },
  { id: 'T005', name: '朱老师', type: 'teacher', department: '外语学院' },
  { id: 'S003', name: '许职工', type: 'staff', department: '保卫处' }
])

// 过滤用户列表
const filteredUsers = ref([])

// 围栏编辑数据
const fenceName = ref('主校区安全围栏')
const fenceRange = ref('东门-西门-南门-北门')
const fenceStatus = ref('启用')
const fenceId = ref(null)

// 获取电子围栏列表
async function fetchFenceList() {
  try {
    const result = await getFenceList({ page: 1, pageSize: 1 })
    if (result.records && result.records.length > 0) {
      const fence = result.records[0]
      currentFence.value = fence
      fenceId.value = fence.id
      fenceName.value = fence.name
      fenceRange.value = fence.description || '东门-西门-南门-北门'
      fenceStatus.value = fence.status === 'active' ? '启用' : '禁用'
    }
  } catch (error) {
    console.error('获取电子围栏失败:', error)
  }
}

// 从后端获取轨迹数据
async function fetchTrackByDate(date) {
  loading.value = true
  try {
    const result = await getTrackByDate({ date })
    if (result && result.trackPoints) {
      // 解析JSON轨迹点
      try {
        trackPoints.value = JSON.parse(result.trackPoints)
      } catch (e) {
        trackPoints.value = []
      }
      mapData.value = `正在显示 ${date} 的路线回溯轨迹（共 ${trackPoints.value.length} 个轨迹点）`
    } else {
      mapData.value = `${date} 没有轨迹数据`
      trackPoints.value = []
    }
  } catch (error) {
    console.error('获取轨迹数据失败:', error)
    mapData.value = `正在显示 ${date} 的路线回溯轨迹`
    trackPoints.value = []
  } finally {
    loading.value = false
  }
}

// 监听 props 变化
import { watch } from 'vue'
watch(() => props.violationInfo, (newInfo) => {
  currentViolationInfo.value = newInfo
  if (newInfo) {
    selectedDate.value = newInfo.time.split(' ')[0]
    mapData.value = `正在显示 ${newInfo.time} 在 ${newInfo.detail} 的违规轨迹回溯`
    // 尝试从后端获取轨迹
    fetchTrackByDate(selectedDate.value)
  }
}, { immediate: true })

watch(() => props.userRole, (newRole) => {
  userRole.value = newRole
}, { immediate: true })

function openDatePicker() {
  showDatePicker.value = true
}

function selectDate() {
  showDatePicker.value = false
  if (selectedDate.value) {
    fetchTrackByDate(selectedDate.value)
  }
}

function viewFence() {
  showFenceDialog.value = true
}

function editFence() {
  showEditFenceDialog.value = true
}

function closeFenceDialog() {
  showFenceDialog.value = false
}

function closeEditFenceDialog() {
  showEditFenceDialog.value = false
}

function clearViolationInfo() {
  currentViolationInfo.value = null
  mapData.value = '学校地图加载中...'
}

// 保存电子围栏
async function saveFence() {
  try {
    const fenceData = {
      name: fenceName.value,
      description: fenceRange.value,
      status: fenceStatus.value === '启用' ? 'active' : 'inactive'
    }
    
    if (fenceId.value) {
      await updateFence(fenceId.value, fenceData)
    } else {
      await addFence(fenceData)
    }
    
    ElMessage.success('保存成功')
    closeEditFenceDialog()
    fetchFenceList()
  } catch (error) {
    console.error('保存围栏失败:', error)
  }
}

// 用户轨迹查看相关函数
function openUserTrackDialog() {
  showUserTrackDialog.value = true
  userSearchKeyword.value = ''
  selectedUser.value = null
  userTrackDate.value = ''
  filteredUsers.value = []
}

function closeUserTrackDialog() {
  showUserTrackDialog.value = false
  userSearchKeyword.value = ''
  selectedUser.value = null
  userTrackDate.value = ''
  filteredUsers.value = []
}

function handleUserSearch() {
  const keyword = userSearchKeyword.value.toLowerCase().trim()
  if (!keyword) {
    filteredUsers.value = []
    return
  }
  
  filteredUsers.value = allUsers.value.filter(user => 
    user.name.toLowerCase().includes(keyword) ||
    user.id.toLowerCase().includes(keyword) ||
    user.department.toLowerCase().includes(keyword)
  )
}

function selectUser(user) {
  selectedUser.value = user
  userSearchKeyword.value = ''
  filteredUsers.value = []
}

function clearUserSelection() {
  selectedUser.value = null
}

function viewUserTrack() {
  if (!selectedUser.value || !userTrackDate.value) {
    ElMessage.warning('请选择用户和日期')
    return
  }
  
  // 更新地图显示
  mapData.value = `正在显示 ${selectedUser.value.name} (${selectedUser.value.id}) 在 ${userTrackDate.value} 的轨迹回溯`
  
  // 关闭对话框
  closeUserTrackDialog()
  
  // 清空违规信息面板
  currentViolationInfo.value = null
}

// 组件加载时获取电子围栏
onMounted(() => {
  fetchFenceList()
})
</script>

<style scoped>
.track-container {
  display: flex;
  height: 100%;
  gap: 24px;
}

.track-left {
  display: flex;
  flex-direction: column;
  gap: 16px;
  width: 280px;
  margin-top: 24px;
}

.track-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.track-btn {
  background: #FFFFFF;
  border: 1px solid #E8E4DE;
  border-radius: 12px;
  padding: 20px 18px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(45, 52, 54, 0.04);
  transition: all 0.2s ease;
  text-align: left;
  color: #2D3436;
}

.track-btn:hover {
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
  color: #FFFFFF;
  border-color: transparent;
  box-shadow: 0 4px 16px rgba(107, 154, 196, 0.25);
  transform: translateY(-2px);
}

.violation-info-panel {
  background: #FFFFFF;
  border-radius: 16px;
  border: 1px solid #E8E4DE;
  box-shadow: 0 2px 12px rgba(45, 52, 54, 0.06);
  padding: 20px 24px;
  transition: all 0.2s ease;
}

.violation-info-panel:hover {
  box-shadow: 0 4px 20px rgba(45, 52, 54, 0.08);
  border-color: #6B9AC4;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #E8E4DE;
}

.panel-title {
  font-size: 18px;
  font-weight: 600;
  color: #2D3436;
}

.close-panel-btn {
  background: #FFFFFF;
  border: 1px solid #C9735D;
  border-radius: 50%;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 600;
  color: #C9735D;
  cursor: pointer;
  transition: all 0.2s ease;
}

.close-panel-btn:hover {
  background: #C9735D;
  color: #FFFFFF;
}

.panel-content {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 13px;
  color: #7D9E87;
  font-weight: 600;
}

.info-value {
  font-size: 15px;
  color: #2D3436;
  font-weight: 500;
}

.info-value.violation-type {
  color: #C9735D;
  font-weight: 600;
}

.info-value.highlight {
  color: #6B9AC4;
  font-weight: 600;
  background: #E9F3FC;
  border-radius: 6px;
  padding: 4px 10px;
  display: inline-block;
}

.track-map {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.track-map-placeholder {
  width: 100%;
  height: 100%;
  min-height: 360px;
  background: #FFFCF8;
  border-radius: 20px;
  border: 1px solid #E8E4DE;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  color: #2D3436;
  text-align: center;
  box-shadow: 0 2px 12px rgba(45, 52, 54, 0.06);
  transition: all 0.2s ease;
}

.track-map-placeholder:hover {
  box-shadow: 0 4px 20px rgba(45, 52, 54, 0.08);
  border-color: #6B9AC4;
}

.map-title {
  font-weight: 600;
  margin-bottom: 16px;
}

.map-content {
  font-size: 16px;
  color: #7D9E87;
  font-weight: 500;
}

/* 对话框样式 */
.track-dialog-mask {
  position: fixed;
  left: 0; top: 0; right: 0; bottom: 0;
  background: rgba(45, 52, 54, 0.15);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.track-dialog {
  background: #FFFFFF;
  border-radius: 20px;
  box-shadow: 0 8px 40px rgba(45, 52, 54, 0.15);
  padding: 32px 40px;
  min-width: 400px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.track-dialog-title {
  font-size: 20px;
  font-weight: 600;
  color: #2D3436;
  margin-bottom: 8px;
  text-align: center;
}

.track-dialog-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.track-dialog-row label {
  width: 100px;
  color: #7D9E87;
  font-weight: 600;
  font-size: 14px;
}

.track-dialog-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 12px;
}

.track-dialog-btn {
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

.track-dialog-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(107, 154, 196, 0.35);
}

.track-dialog-btn.cancel {
  background: #FFFFFF;
  color: #636E72;
  border: 1px solid #E8E4DE;
  box-shadow: none;
}

.track-dialog-btn.cancel:hover {
  background: #FAF7F2;
}

.fence-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin: 12px 0;
}

.fence-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.fence-label {
  color: #7D9E87;
  font-weight: 600;
  min-width: 100px;
  font-size: 14px;
}

.fence-status.active {
  color: #6B9AC4;
  background: #E9F3FC;
  border-radius: 6px;
  padding: 2px 12px;
  font-weight: 600;
}

/* 用户轨迹查看对话框样式 */
.user-track-dialog {
  width: 560px;
  max-width: 90vw;
  max-height: 80vh;
  overflow-y: auto;
}

.user-search-section {
  margin-bottom: 20px;
}

.search-header {
  margin-bottom: 12px;
}

.search-label {
  display: block;
  font-weight: 600;
  color: #2D3436;
  margin-bottom: 8px;
  font-size: 14px;
}

.search-input-container {
  position: relative;
  display: flex;
  align-items: center;
}

.user-search-input {
  flex: 1;
  padding: 12px 40px 12px 16px;
  border: 1px solid #E8E4DE;
  border-radius: 10px;
  font-size: 15px;
  outline: none;
  transition: all 0.2s ease;
  background: #FAF7F2;
}

.user-search-input:focus {
  border-color: #6B9AC4;
  box-shadow: 0 0 0 3px rgba(107, 154, 196, 0.15);
}

.search-icon {
  position: absolute;
  right: 12px;
  color: #9BA4A9;
  pointer-events: none;
}

.user-list {
  max-height: 200px;
  overflow-y: auto;
  border: 1px solid #E8E4DE;
  border-radius: 10px;
  background: #FFFFFF;
}

.user-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  border-bottom: 1px solid #FAF7F2;
}

.user-item:last-child {
  border-bottom: none;
}

.user-item:hover {
  background: #FAF7F2;
}

.user-item.selected {
  background: #E7F2EA;
  border-left: 3px solid #7D9E87;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 12px;
  flex-shrink: 0;
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-weight: 600;
  color: #2D3436;
  margin-bottom: 2px;
  font-size: 15px;
}

.user-id {
  font-size: 13px;
  color: #636E72;
  margin-bottom: 2px;
}

.user-department {
  font-size: 12px;
  color: #9BA4A9;
}

.user-type {
  padding: 3px 8px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 500;
  color: white;
  flex-shrink: 0;
}

.user-type.student {
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
}

.user-type.teacher {
  background: linear-gradient(135deg, #6B9AC4 0%, #7D9E87 100%);
}

.user-type.staff {
  background: linear-gradient(135deg, #D4A574 0%, #C9735D 100%);
}

.empty-users {
  text-align: center;
  padding: 40px 20px;
  color: #636E72;
}

.empty-icon {
  color: #E8E4DE;
  margin-bottom: 12px;
}

.empty-text {
  font-size: 15px;
}

.selected-user-section {
  margin-bottom: 20px;
  padding: 16px;
  background: #FAF7F2;
  border: 1px solid #E8E4DE;
  border-radius: 12px;
}

.selected-user-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.selected-label {
  font-weight: 600;
  color: #7D9E87;
  font-size: 14px;
}

.clear-selection-btn {
  background: #C9735D;
  color: white;
  border: none;
  border-radius: 50%;
  width: 22px;
  height: 22px;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.clear-selection-btn:hover {
  background: #b86350;
}

.selected-user-card {
  display: flex;
  align-items: center;
  padding: 12px;
  background: #FFFFFF;
  border-radius: 10px;
  border: 1px solid #E8E4DE;
}

.selected-user-card .user-avatar {
  width: 44px;
  height: 44px;
  margin-right: 14px;
}

.selected-user-card .user-name {
  font-size: 16px;
  margin-bottom: 6px;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-details span {
  font-size: 13px;
  color: #636E72;
}

.date-selection-section {
  margin-bottom: 20px;
}

.date-input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #E8E4DE;
  border-radius: 10px;
  font-size: 15px;
  outline: none;
  transition: all 0.2s ease;
  background: #FAF7F2;
}

.date-input:focus {
  border-color: #6B9AC4;
  box-shadow: 0 0 0 3px rgba(107, 154, 196, 0.15);
}

.track-dialog-btn:disabled {
  background: #E8E4DE;
  color: #9BA4A9;
  cursor: not-allowed;
  opacity: 0.7;
  box-shadow: none;
}

.track-dialog-btn:disabled:hover {
  background: #E8E4DE;
  transform: none;
  box-shadow: none;
}
</style> 