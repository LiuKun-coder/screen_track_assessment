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
  gap: 32px;
  width: 100%;
  height: 100%;
  align-items: flex-start;
}

.profile-info-card {
  flex: 1;
  background: linear-gradient(135deg, #f0f5ff 0%, #fff 100%);
  border-radius: 28px;
  border: 2.5px solid #e0e7ff;
  box-shadow: 0 4px 32px 0 #4f8cff11, 0 2px 8px 0 #ffb6c122;
  padding: 40px 36px;
  transition: box-shadow 0.3s, border 0.3s;
  position: relative;
  overflow: hidden;
  min-height: 560px;
}

.profile-info-card:hover {
  box-shadow: 0 8px 48px 0 #4f8cff22, 0 4px 16px 0 #ffb6c133;
  border: 2.5px solid #7c3aed;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 36px;
  text-align: center;
}

.avatar-wrapper {
  position: relative;
  margin-bottom: 20px;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 4px 24px 0 #4f8cff22;
  border: 4px solid #fff;
  transition: transform 0.3s, box-shadow 0.3s;
}

.avatar-wrapper:hover .avatar {
  transform: scale(1.05);
  box-shadow: 0 8px 32px 0 #4f8cff33;
}

.avatar-overlay {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: linear-gradient(135deg, rgba(79,140,255,0.9) 0%, rgba(124,58,237,0.9) 100%);
  color: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  cursor: pointer;
  transition: opacity 0.3s;
  gap: 6px;
}

.avatar-overlay span {
  font-size: 14px;
  font-weight: bold;
}

.user-basic-info {
  width: 100%;
}

.user-name {
  font-size: 28px;
  font-weight: bold;
  color: #4f8cff;
  margin: 0 0 8px 0;
  letter-spacing: 1px;
}

.edit-name-input {
  font-size: 28px;
  font-weight: bold;
  color: #4f8cff;
  border: 2px solid #4f8cff;
  border-radius: 12px;
  padding: 8px 16px;
  text-align: center;
  background: transparent;
  width: 80%;
  margin-bottom: 8px;
}

.edit-name-input:focus {
  outline: none;
  border-color: #7c3aed;
  box-shadow: 0 0 0 3px #7c3aed22;
}

.user-id {
  font-size: 16px;
  color: #888;
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
  font-size: 16px;
  color: #888;
  font-weight: 500;
}

.edit-id-input {
  font-size: 16px;
  color: #4f8cff;
  border: 2px solid #4f8cff;
  border-radius: 8px;
  padding: 6px 12px;
  background: transparent;
  width: 140px;
  text-align: center;
  font-weight: 500;
}

.edit-id-input:focus {
  outline: none;
  border-color: #7c3aed;
  box-shadow: 0 0 0 3px #7c3aed22;
}

.user-role-badge {
  background: linear-gradient(90deg, #7c3aed 0%, #4f8cff 100%);
  color: #fff;
  padding: 6px 20px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: bold;
  display: inline-block;
}

.info-details {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  background: rgba(255,255,255,0.6);
  border-radius: 16px;
  border: 1px solid #e0e7ff;
  transition: background 0.3s, transform 0.3s;
}

.detail-item:hover {
  background: rgba(255,255,255,0.9);
  transform: translateX(8px);
}

.detail-icon {
  width: 44px;
  height: 44px;
  background: rgba(255,255,255,0.8);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px 0 rgba(79,140,255,0.1);
}

.detail-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-label {
  font-size: 14px;
  color: #7c3aed;
  font-weight: bold;
}

.detail-value {
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.level-value {
  color: #4f8cff;
  font-weight: bold;
}

.record-value {
  color: #ff4f4f;
  font-weight: bold;
}

.edit-detail-input {
  border: 2px solid #e0e7ff;
  border-radius: 8px;
  padding: 6px 12px;
  font-size: 16px;
  background: #fff;
  transition: border-color 0.3s;
}

.edit-detail-input:focus {
  outline: none;
  border-color: #4f8cff;
}

.profile-action-panel {
  width: 360px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.action-card, .stats-card, .logout-card {
  background: linear-gradient(135deg, #f0f5ff 0%, #fff 100%);
  border-radius: 24px;
  border: 2.5px solid #e0e7ff;
  box-shadow: 0 4px 24px 0 #4f8cff11, 0 1.5px 6px 0 #ffb6c122;
  transition: box-shadow 0.3s, border 0.3s, transform 0.3s;
  position: relative;
  overflow: hidden;
}

.action-card:hover, .stats-card:hover {
  box-shadow: 0 8px 32px 0 #4f8cff22, 0 2px 8px 0 #ffb6c133;
  border: 2.5px solid #7c3aed;
  transform: translateY(-2px);
}

.action-card {
  padding: 28px 24px;
}

.action-header {
  margin-bottom: 24px;
  text-align: center;
}

.action-title {
  font-size: 20px;
  font-weight: bold;
  color: #4f8cff;
  margin: 0 0 8px 0;
}

.action-subtitle {
  font-size: 14px;
  color: #888;
  line-height: 1.4;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 16px;
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
  padding: 14px 24px;
  border: none;
  border-radius: 16px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
  letter-spacing: 0.5px;
  flex: 1;
}

.action-btn.primary {
  background: linear-gradient(90deg, #4f8cff 0%, #7c3aed 100%);
  color: #fff;
  box-shadow: 0 4px 16px 0 #4f8cff33;
}

.action-btn.primary:hover {
  background: linear-gradient(90deg, #7c3aed 0%, #ffb6c1 100%);
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 8px 24px 0 #7c3aed44;
}

.action-btn.success {
  background: linear-gradient(90deg, #10b981 0%, #059669 100%);
  color: #fff;
  box-shadow: 0 4px 16px 0 #10b98133;
}

.action-btn.success:hover {
  background: linear-gradient(90deg, #059669 0%, #047857 100%);
  transform: translateY(-2px) scale(1.02);
}

.action-btn.secondary {
  background: #fff;
  color: #666;
  border: 2px solid #e0e7ff;
  box-shadow: 0 2px 8px 0 #00000011;
}

.action-btn.secondary:hover {
  background: #f8fafc;
  border-color: #4f8cff;
  color: #4f8cff;
  transform: translateY(-2px) scale(1.02);
}

.action-btn.warning {
  background: linear-gradient(90deg, #f59e0b 0%, #d97706 100%);
  color: #fff;
  box-shadow: 0 4px 16px 0 #f59e0b33;
}

.action-btn.warning:hover {
  background: linear-gradient(90deg, #d97706 0%, #b45309 100%);
  transform: translateY(-2px) scale(1.02);
}

.stats-card {
  padding: 24px;
}

.stats-header {
  margin-bottom: 20px;
  text-align: center;
}

.stats-title {
  font-size: 18px;
  font-weight: bold;
  color: #4f8cff;
  margin: 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.stat-item {
  text-align: center;
  padding: 16px 12px;
  background: rgba(255,255,255,0.7);
  border-radius: 12px;
  border: 1px solid #e0e7ff;
  transition: background 0.3s, transform 0.3s;
}

.stat-item:hover {
  background: rgba(255,255,255,0.9);
  transform: scale(1.05);
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #7c3aed;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #888;
  font-weight: 500;
}

.logout-card {
  padding: 20px;
}

.logout-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 16px 24px;
  background: linear-gradient(90deg, #ef4444 0%, #dc2626 100%);
  color: #fff;
  border: none;
  border-radius: 16px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
  letter-spacing: 0.5px;
  box-shadow: 0 4px 16px 0 #ef444433;
}

.logout-btn:hover {
  background: linear-gradient(90deg, #dc2626 0%, #b91c1c 100%);
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 8px 24px 0 #dc262644;
}

.logout-card:hover {
  border-color: #ef4444;
  box-shadow: 0 8px 32px 0 #ef444422;
}
</style> 