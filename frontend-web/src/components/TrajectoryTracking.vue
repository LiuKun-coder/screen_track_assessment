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
import { ref } from 'vue'

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

// 监听 props 变化
import { watch } from 'vue'
watch(() => props.violationInfo, (newInfo) => {
  currentViolationInfo.value = newInfo
  if (newInfo) {
    selectedDate.value = newInfo.time.split(' ')[0]
    mapData.value = `正在显示 ${newInfo.time} 在 ${newInfo.detail} 的违规轨迹回溯`
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
    mapData.value = `正在显示 ${selectedDate.value} 的路线回溯轨迹`
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

function saveFence() {
  // 保存围栏功能待实现
  alert('保存围栏功能待实现')
  closeEditFenceDialog()
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
    alert('请选择用户和日期')
    return
  }
  
  // 更新地图显示
  mapData.value = `正在显示 ${selectedUser.value.name} (${selectedUser.value.id}) 在 ${userTrackDate.value} 的轨迹回溯`
  
  // 关闭对话框
  closeUserTrackDialog()
  
  // 清空违规信息面板
  currentViolationInfo.value = null
}
</script>

<style scoped>
.track-container {
  display: flex;
  height: 100%;
  gap: 32px;
}

.track-left {
  display: flex;
  flex-direction: column;
  gap: 32px;
  width: 320px;
  margin-top: 24px;
}

.track-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.track-btn {
  background: linear-gradient(90deg, #f0f5ff 0%, #fff 100%);
  border: 2.5px solid #e0e7ff;
  border-radius: 20px;
  padding: 28px 20px;
  font-size: 20px;
  font-weight: 600;
  cursor: pointer;
  margin-bottom: 0;
  box-shadow: 0 4px 24px 0 #4f8cff11, 0 1.5px 6px 0 #ffb6c122;
  transition: background 0.25s, color 0.25s, box-shadow 0.25s, transform 0.18s, border 0.25s;
  text-align: left;
  color: #4f8cff;
  letter-spacing: 1px;
  position: relative;
  overflow: hidden;
}

.track-btn:hover {
  background: linear-gradient(90deg, #4f8cff 0%, #7c3aed 100%);
  color: #fff;
  box-shadow: 0 8px 32px 0 #4f8cff33, 0 2px 8px 0 #ffb6c133;
  border: 2.5px solid #7c3aed;
  transform: translateY(-3px) scale(1.03);
}

.violation-info-panel {
  background: linear-gradient(90deg, #f0f5ff 0%, #fff 100%);
  border-radius: 20px;
  border: 2.5px solid #e0e7ff;
  box-shadow: 0 4px 24px 0 #4f8cff11, 0 1.5px 6px 0 #ffb6c122;
  padding: 20px 28px;
  transition: box-shadow 0.25s, border 0.25s;
  position: relative;
  overflow: hidden;
}

.violation-info-panel:hover {
  box-shadow: 0 8px 32px 0 #4f8cff22, 0 2px 8px 0 #ffb6c133;
  border: 2.5px solid #7c3aed;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e0e7ff;
}

.panel-title {
  font-size: 20px;
  font-weight: bold;
  color: #4f8cff;
  letter-spacing: 1px;
}

.close-panel-btn {
  background: #fff;
  border: 2px solid #ff4f4f;
  border-radius: 50%;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: bold;
  color: #ff4f4f;
  cursor: pointer;
  transition: background 0.22s, color 0.22s, transform 0.18s;
}

.close-panel-btn:hover {
  background: #ff4f4f;
  color: #fff;
  transform: scale(1.1);
}

.panel-content {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px 24px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.info-label {
  font-size: 14px;
  color: #7c3aed;
  font-weight: bold;
}

.info-value {
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.info-value.violation-type {
  color: #ff4f4f;
  font-weight: bold;
}

.info-value.highlight {
  color: #4f8cff;
  font-weight: bold;
  background: #e0e7ff;
  border-radius: 8px;
  padding: 4px 12px;
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
  background: linear-gradient(135deg, #f0f5ff 0%, #fff 100%);
  border-radius: 32px;
  border: 2.5px solid #e0e7ff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #4f8cff;
  text-align: center;
  box-shadow: 0 4px 24px 0 #4f8cff11, 0 1.5px 6px 0 #ffb6c122;
  transition: box-shadow 0.25s, border 0.25s;
  position: relative;
  overflow: hidden;
}

.track-map-placeholder:hover {
  box-shadow: 0 8px 32px 0 #4f8cff22, 0 2px 8px 0 #ffb6c133;
  border: 2.5px solid #7c3aed;
}

.map-title {
  font-weight: bold;
  margin-bottom: 16px;
  letter-spacing: 2px;
}

.map-content {
  font-size: 18px;
  color: #7c3aed;
  font-weight: 500;
}

/* 对话框样式 */
.track-dialog-mask {
  position: fixed;
  left: 0; top: 0; right: 0; bottom: 0;
  background: rgba(79,140,255,0.12);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.track-dialog {
  background: #fff;
  border-radius: 24px;
  box-shadow: 0 8px 40px 0 #4f8cff33;
  padding: 36px 48px 28px 48px;
  min-width: 400px;
  display: flex;
  flex-direction: column;
  gap: 18px;
  position: relative;
}

.track-dialog-title {
  font-size: 22px;
  font-weight: bold;
  color: #4f8cff;
  margin-bottom: 8px;
}

.track-dialog-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.track-dialog-row label {
  width: 100px;
  color: #7c3aed;
  font-weight: bold;
}

.track-dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 18px;
  margin-top: 12px;
}

.track-dialog-btn {
  background: linear-gradient(90deg, #7c3aed 0%, #4f8cff 100%);
  color: #fff;
  border: none;
  border-radius: 10px;
  padding: 8px 28px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.22s, color 0.22s, transform 0.18s;
}

.track-dialog-btn:hover {
  background: linear-gradient(90deg, #ffb6c1 0%, #4f8cff 100%);
  color: #222;
  transform: scale(1.06);
}

.track-dialog-btn.cancel {
  background: #fff;
  color: #4f8cff;
  border: 2px solid #4f8cff;
}

.track-dialog-btn.cancel:hover {
  background: #f0f5ff;
  color: #222;
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
  color: #7c3aed;
  font-weight: bold;
  min-width: 100px;
}

.fence-status.active {
  color: #4f8cff;
  background: #e0e7ff;
  border-radius: 8px;
  padding: 2px 12px;
  font-weight: bold;
}

/* 用户轨迹查看对话框样式 */
.user-track-dialog {
  width: 600px;
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
  font-weight: bold;
  color: #4f8cff;
  margin-bottom: 8px;
  font-size: 16px;
}

.search-input-container {
  position: relative;
  display: flex;
  align-items: center;
}

.user-search-input {
  flex: 1;
  padding: 12px 40px 12px 16px;
  border: 2px solid #e0e7ff;
  border-radius: 8px;
  font-size: 16px;
  outline: none;
  transition: border-color 0.3s;
}

.user-search-input:focus {
  border-color: #4f8cff;
}

.search-icon {
  position: absolute;
  right: 12px;
  color: #666;
  pointer-events: none;
}

.user-list {
  max-height: 200px;
  overflow-y: auto;
  border: 1px solid #e0e7ff;
  border-radius: 8px;
  background: #fff;
}

.user-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  cursor: pointer;
  transition: background-color 0.3s;
  border-bottom: 1px solid #f0f5ff;
}

.user-item:last-child {
  border-bottom: none;
}

.user-item:hover {
  background: #f8fafc;
}

.user-item.selected {
  background: linear-gradient(90deg, #f0f5ff 0%, #e0e7ff 100%);
  border-left: 4px solid #4f8cff;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4f8cff 0%, #7c3aed 100%);
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
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
  font-size: 16px;
}

.user-id {
  font-size: 14px;
  color: #666;
  margin-bottom: 2px;
}

.user-department {
  font-size: 12px;
  color: #999;
}

.user-type {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
  color: white;
  flex-shrink: 0;
}

.user-type.student {
  background: linear-gradient(90deg, #10b981 0%, #059669 100%);
}

.user-type.teacher {
  background: linear-gradient(90deg, #4f8cff 0%, #7c3aed 100%);
}

.user-type.staff {
  background: linear-gradient(90deg, #f59e0b 0%, #d97706 100%);
}

.empty-users {
  text-align: center;
  padding: 40px 20px;
  color: #666;
}

.empty-icon {
  color: #ccc;
  margin-bottom: 12px;
}

.empty-text {
  font-size: 16px;
}

.selected-user-section {
  margin-bottom: 20px;
  padding: 16px;
  background: #f8fafc;
  border: 1px solid #e0e7ff;
  border-radius: 8px;
}

.selected-user-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.selected-label {
  font-weight: bold;
  color: #4f8cff;
  font-size: 16px;
}

.clear-selection-btn {
  background: #ff6b6b;
  color: white;
  border: none;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  cursor: pointer;
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.3s;
}

.clear-selection-btn:hover {
  background: #ff5252;
}

.selected-user-card {
  display: flex;
  align-items: center;
  padding: 12px;
  background: white;
  border-radius: 8px;
  border: 1px solid #e0e7ff;
}

.selected-user-card .user-avatar {
  width: 48px;
  height: 48px;
  margin-right: 16px;
}

.selected-user-card .user-name {
  font-size: 18px;
  margin-bottom: 6px;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-details span {
  font-size: 14px;
  color: #666;
}

.date-selection-section {
  margin-bottom: 20px;
}

.date-input {
  flex: 1;
  padding: 12px 16px;
  border: 2px solid #e0e7ff;
  border-radius: 8px;
  font-size: 16px;
  outline: none;
  transition: border-color 0.3s;
}

.date-input:focus {
  border-color: #4f8cff;
}

.track-dialog-btn:disabled {
  background: #e0e7ff;
  color: #aaa;
  cursor: not-allowed;
  opacity: 0.6;
}

.track-dialog-btn:disabled:hover {
  background: #e0e7ff;
  transform: none;
  box-shadow: none;
}
</style> 