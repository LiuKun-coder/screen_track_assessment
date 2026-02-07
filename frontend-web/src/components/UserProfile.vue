<template>
  <div class="profile-container">
    <div class="profile-main">
      <!-- 左侧个人信息卡片 -->
      <div class="profile-info-card">
        <div class="avatar-section">
          <div class="avatar-wrapper" @mouseenter="avatarHover = true" @mouseleave="avatarHover = false">
            <img class="avatar" :src="avatarUrl" alt="头像" />
            <div class="avatar-overlay" v-if="avatarHover" @click="changeAvatar">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="white" xmlns="http://www.w3.org/2000/svg">
                <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/>
              </svg>
              <span>更换头像</span>
            </div>
          </div>
          <div class="user-basic-info">
            <h2 class="user-name" v-if="!isEditing">{{ name }}</h2>
            <input v-else v-model="editName" class="edit-name-input" placeholder="请输入姓名" />
            <p class="user-id" v-if="!isEditing">学号：{{ id }}</p>
            <div v-else class="edit-id-wrapper">
              <span class="id-label">学号：</span>
              <input v-model="editId" class="edit-id-input" placeholder="请输入学号" />
            </div>
            <span class="user-role-badge">{{ role }}</span>
          </div>
        </div>
        
        <div class="info-details">
          <div class="detail-item">
            <div class="detail-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="#4f8cff" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
              </svg>
            </div>
            <div class="detail-content">
              <span class="detail-label">联系方式</span>
              <span v-if="!isEditing" class="detail-value">{{ phone }}</span>
              <input v-else v-model="editPhone" class="edit-detail-input" placeholder="请输入手机号" />
            </div>
          </div>
          
          <div class="detail-item">
            <div class="detail-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="#7c3aed" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
              </svg>
            </div>
            <div class="detail-content">
              <span class="detail-label">用户等级</span>
              <span class="detail-value level-value">Lv.5 活跃用户</span>
            </div>
          </div>
          
          <div class="detail-item">
            <div class="detail-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="#ffb6c1" xmlns="http://www.w3.org/2000/svg">
                <path d="M19 3h-1V1h-2v2H8V1H6v2H5c-1.11 0-1.99.9-1.99 2L3 19c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V8h14v11zM7 10h5v5H7z"/>
              </svg>
            </div>
            <div class="detail-content">
              <span class="detail-label">注册时间</span>
              <span class="detail-value">2023年9月1日</span>
            </div>
          </div>
          
          <div class="detail-item">
            <div class="detail-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="#4f8cff" xmlns="http://www.w3.org/2000/svg">
                <path d="M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z"/>
              </svg>
            </div>
            <div class="detail-content">
              <span class="detail-label">违规记录</span>
              <span class="detail-value record-value">8次违规</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 右侧操作面板 -->
      <div class="profile-action-panel">
        <div class="action-card">
          <div class="action-header">
            <h3 class="action-title">个人设置</h3>
            <div class="action-subtitle">管理您的个人信息和偏好设置</div>
          </div>
          
          <div class="action-buttons">
            <button class="action-btn primary" v-if="!isEditing" @click="isEditing = true">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="white" xmlns="http://www.w3.org/2000/svg">
                <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/>
              </svg>
              编辑信息
            </button>
            <div v-else class="edit-actions">
              <button class="action-btn success" @click="saveEdit">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="white" xmlns="http://www.w3.org/2000/svg">
                  <path d="M9 16.2L4.8 12l-1.4 1.4L9 19 21 7l-1.4-1.4L9 16.2z"/>
                </svg>
                保存修改
              </button>
              <button class="action-btn secondary" @click="cancelEdit">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
                  <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
                </svg>
                取消
              </button>
            </div>
            
            <button class="action-btn warning" @click="changePassword">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="white" xmlns="http://www.w3.org/2000/svg">
                <path d="M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm3.1-9H8.9V6c0-1.71 1.39-3.1 3.1-3.1 1.71 0 3.1 1.39 3.1 3.1v2z"/>
              </svg>
              修改密码
            </button>
          </div>
        </div>
        
        <div class="stats-card">
          <div class="stats-header">
            <h3 class="stats-title">使用统计</h3>
          </div>
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-number">127</div>
              <div class="stat-label">登录天数</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">8</div>
              <div class="stat-label">违规次数</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">15</div>
              <div class="stat-label">轨迹查询</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">98%</div>
              <div class="stat-label">安全评分</div>
            </div>
          </div>
        </div>
        
        <div class="logout-card">
          <button class="logout-btn" @click="logout">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="white" xmlns="http://www.w3.org/2000/svg">
              <path d="M17 7l-1.41 1.41L18.17 11H8v2h10.17l-2.58 2.59L17 17l5-5zM4 5h8V3H4c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h8v-2H4V5z"/>
            </svg>
            退出登录
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 个人信息相关
const avatarUrl = ref('https://i.pravatar.cc/150?img=3')
const avatarHover = ref(false)
const name = ref('张三')
const id = ref('2023123456')
const role = ref('学生')
const phone = ref('138****8888')
const isEditing = ref(false)
const editName = ref(name.value)
const editId = ref(id.value)
const editPhone = ref(phone.value)

function changeAvatar() {
  // 创建文件选择器
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.style.display = 'none'
  
  input.onchange = (event) => {
    const file = event.target.files[0]
    if (file) {
      // 验证文件类型
      if (!file.type.startsWith('image/')) {
        alert('请选择图片文件！')
        return
      }
      
      // 验证文件大小（限制为5MB）
      if (file.size > 5 * 1024 * 1024) {
        alert('图片文件大小不能超过5MB！')
        return
      }
      
      // 创建FileReader来读取文件
      const reader = new FileReader()
      reader.onload = (e) => {
        avatarUrl.value = e.target.result
      }
      reader.readAsDataURL(file)
    }
  }
  
  // 触发文件选择
  document.body.appendChild(input)
  input.click()
  document.body.removeChild(input)
}

function saveEdit() {
  name.value = editName.value
  id.value = editId.value
  phone.value = editPhone.value
  isEditing.value = false
}

function cancelEdit() {
  editName.value = name.value
  editId.value = id.value
  editPhone.value = phone.value
  isEditing.value = false
}

function changePassword() {
  router.push('/reset-password')
}

function logout() {
  router.push('/')
}
</script>

<style scoped>
.profile-container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  height: 100%;
  padding: 0;
}

.profile-main {
  display: flex;
  gap: 24px;
  width: 100%;
  height: 100%;
  align-items: flex-start;
}

.profile-info-card {
  flex: 1;
  background: #FFFFFF;
  border-radius: 20px;
  border: 1px solid #E8E4DE;
  box-shadow: 0 2px 12px rgba(45, 52, 54, 0.06);
  padding: 32px;
  transition: all 0.2s ease;
  min-height: 520px;
}

.profile-info-card:hover {
  box-shadow: 0 4px 20px rgba(45, 52, 54, 0.08);
  border-color: #6B9AC4;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 32px;
  text-align: center;
}

.avatar-wrapper {
  position: relative;
  margin-bottom: 20px;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 4px 16px rgba(45, 52, 54, 0.1);
  border: 4px solid #FFFFFF;
  transition: all 0.2s ease;
}

.avatar-wrapper:hover .avatar {
  transform: scale(1.05);
  box-shadow: 0 6px 24px rgba(45, 52, 54, 0.15);
}

.avatar-overlay {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: linear-gradient(135deg, rgba(125, 158, 135, 0.9) 0%, rgba(107, 154, 196, 0.9) 100%);
  color: #FFFFFF;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  cursor: pointer;
  transition: opacity 0.2s ease;
  gap: 6px;
}

.avatar-overlay span {
  font-size: 13px;
  font-weight: 500;
}

.user-basic-info {
  width: 100%;
}

.user-name {
  font-size: 24px;
  font-weight: 600;
  color: #2D3436;
  margin: 0 0 8px 0;
}

.edit-name-input {
  font-size: 24px;
  font-weight: 600;
  color: #2D3436;
  border: 1px solid #6B9AC4;
  border-radius: 10px;
  padding: 8px 16px;
  text-align: center;
  background: #FAF7F2;
  width: 80%;
  margin-bottom: 8px;
}

.edit-name-input:focus {
  outline: none;
  border-color: #7D9E87;
  box-shadow: 0 0 0 3px rgba(125, 158, 135, 0.15);
}

.user-id {
  font-size: 15px;
  color: #636E72;
  margin: 0 0 12px 0;
}

.edit-id-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin: 0 0 12px 0;
}

.id-label {
  font-size: 15px;
  color: #636E72;
  font-weight: 500;
}

.edit-id-input {
  font-size: 15px;
  color: #2D3436;
  border: 1px solid #6B9AC4;
  border-radius: 8px;
  padding: 6px 12px;
  background: #FAF7F2;
  width: 140px;
  text-align: center;
  font-weight: 500;
}

.edit-id-input:focus {
  outline: none;
  border-color: #7D9E87;
  box-shadow: 0 0 0 3px rgba(125, 158, 135, 0.15);
}

.user-role-badge {
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
  color: #FFFFFF;
  padding: 6px 18px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  display: inline-block;
}

.info-details {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  background: #FAF7F2;
  border-radius: 12px;
  border: 1px solid #E8E4DE;
  transition: all 0.2s ease;
}

.detail-item:hover {
  background: #FFFCF8;
  transform: translateX(4px);
}

.detail-icon {
  width: 40px;
  height: 40px;
  background: #FFFFFF;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(45, 52, 54, 0.06);
}

.detail-icon svg {
  fill: #7D9E87;
}

.detail-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-label {
  font-size: 13px;
  color: #7D9E87;
  font-weight: 600;
}

.detail-value {
  font-size: 15px;
  color: #2D3436;
  font-weight: 500;
}

.level-value {
  color: #6B9AC4;
  font-weight: 600;
}

.record-value {
  color: #C9735D;
  font-weight: 600;
}

.edit-detail-input {
  border: 1px solid #E8E4DE;
  border-radius: 8px;
  padding: 6px 12px;
  font-size: 15px;
  background: #FFFFFF;
  transition: all 0.2s ease;
}

.edit-detail-input:focus {
  outline: none;
  border-color: #6B9AC4;
  box-shadow: 0 0 0 3px rgba(107, 154, 196, 0.15);
}

.profile-action-panel {
  width: 340px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.action-card, .stats-card, .logout-card {
  background: #FFFFFF;
  border-radius: 16px;
  border: 1px solid #E8E4DE;
  box-shadow: 0 2px 12px rgba(45, 52, 54, 0.06);
  transition: all 0.2s ease;
}

.action-card:hover, .stats-card:hover {
  box-shadow: 0 4px 20px rgba(45, 52, 54, 0.08);
  border-color: #6B9AC4;
}

.action-card {
  padding: 24px;
}

.action-header {
  margin-bottom: 20px;
  text-align: center;
}

.action-title {
  font-size: 18px;
  font-weight: 600;
  color: #2D3436;
  margin: 0 0 8px 0;
}

.action-subtitle {
  font-size: 14px;
  color: #636E72;
  line-height: 1.4;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.edit-actions {
  display: flex;
  gap: 12px;
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 12px 20px;
  border: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  flex: 1;
}

.action-btn.primary {
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
  color: #FFFFFF;
  box-shadow: 0 2px 8px rgba(107, 154, 196, 0.2);
}

.action-btn.primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(107, 154, 196, 0.35);
}

.action-btn.success {
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
  color: #FFFFFF;
  box-shadow: 0 2px 8px rgba(125, 158, 135, 0.2);
}

.action-btn.success:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(125, 158, 135, 0.35);
}

.action-btn.secondary {
  background: #FFFFFF;
  color: #636E72;
  border: 1px solid #E8E4DE;
}

.action-btn.secondary:hover {
  background: #FAF7F2;
  border-color: #6B9AC4;
  color: #2D3436;
}

.action-btn.warning {
  background: linear-gradient(135deg, #D4A574 0%, #C9735D 100%);
  color: #FFFFFF;
  box-shadow: 0 2px 8px rgba(212, 165, 116, 0.2);
}

.action-btn.warning:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(212, 165, 116, 0.35);
}

.stats-card {
  padding: 20px;
}

.stats-header {
  margin-bottom: 16px;
  text-align: center;
}

.stats-title {
  font-size: 16px;
  font-weight: 600;
  color: #2D3436;
  margin: 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.stat-item {
  text-align: center;
  padding: 14px 12px;
  background: #FAF7F2;
  border-radius: 10px;
  border: 1px solid #E8E4DE;
  transition: all 0.2s ease;
}

.stat-item:hover {
  background: #FFFCF8;
  transform: scale(1.02);
}

.stat-number {
  font-size: 22px;
  font-weight: 600;
  color: #7D9E87;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #636E72;
  font-weight: 500;
}

.logout-card {
  padding: 16px;
}

.logout-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 14px 20px;
  background: linear-gradient(135deg, #C9735D 0%, #b86350 100%);
  color: #FFFFFF;
  border: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(201, 115, 93, 0.2);
}

.logout-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(201, 115, 93, 0.35);
}

.logout-card:hover {
  border-color: #C9735D;
}
</style> 