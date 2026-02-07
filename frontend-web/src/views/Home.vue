<template>
  <div class="home-container">
    <div class="sidebar">
      <div
        v-for="item in filteredMenuList"
        :key="item.key"
        :class="['menu-item', { active: currentMenu === item.key }]"
        @click="switchMenu(item.key)"
      >
        <div class="menu-content">
          <span class="menu-icon" v-html="getMenuIcon(item.key)"></span>
          <span>{{ item.label }}</span>
          <span v-if="item.sub">{{ item.sub }}</span>
        </div>
      </div>
    </div>
    <div class="main-content">
      <div class="content-area">
        <AdminManagement v-if="currentMenu === 'admin'" />
        <ViolationReport v-else-if="currentMenu === 'report'" />
        <MyViolations v-else-if="currentMenu === 'mine'" @switchToTrack="handleSwitchToTrack" />
        <TrajectoryTracking v-else-if="currentMenu === 'track'" :violationInfo="violationInfoForTrack" :userRole="userRole" />
        <Appeal v-else-if="currentMenu === 'appeal'" />
        <AppealComplaintHandler v-else-if="currentMenu === 'handler'" />
        <UserProfile v-else-if="currentMenu === 'profile'" />

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

// 导入组件
import AdminManagement from '../components/AdminManagement.vue'
import ViolationReport from '../components/ViolationReport.vue'
import MyViolations from '../components/MyViolations.vue'
import TrajectoryTracking from '../components/TrajectoryTracking.vue'
import Appeal from '../components/Appeal.vue'
import AppealComplaintHandler from '../components/AppealComplaintHandler.vue'
import UserProfile from '../components/UserProfile.vue'


const router = useRouter()
const currentMenu = ref('report') // 默认显示违规通报
const violationInfoForTrack = ref(null)

const userRole = ref('user') // 默认普通用户
const userInfo = ref(null)

// 完整菜单列表
const allMenuList = [
  { key: 'admin', label: '管理人员', roles: ['admin'] },
  { key: 'screen', label: '违规大屏', roles: ['admin', 'user'] },
  { key: 'report', label: '违规通报', roles: ['admin', 'user'] },
  { key: 'mine', label: '我的违规', roles: ['admin', 'user'] },
  { key: 'track', label: '轨迹回溯', roles: ['admin', 'user'] },
  { key: 'appeal', label: '我要申诉', roles: ['admin', 'user'] },
  { key: 'handler', label: '申诉处理', roles: ['admin'] },
  { key: 'profile', label: '个人信息', roles: ['admin', 'user'] },
]

// 根据用户角色过滤菜单
const filteredMenuList = computed(() => {
  return allMenuList.filter(item => 
    item.roles.includes(userRole.value)
  )
})

// 检查用户登录状态和权限
function checkUserAuth() {
  const isLoggedIn = localStorage.getItem('isLoggedIn')
  const userInfoStr = localStorage.getItem('userInfo')
  
  if (!isLoggedIn || !userInfoStr) {
    ElMessage.error('请先登录')
    router.push('/login')
    return false
  }
  
  try {
    const userData = JSON.parse(userInfoStr)
    userInfo.value = userData
    userRole.value = userData.role || 'user'
    
    // 如果当前菜单对用户角色不可见，切换到默认菜单
    const currentMenuItem = allMenuList.find(item => item.key === currentMenu.value)
    if (!currentMenuItem || !currentMenuItem.roles.includes(userRole.value)) {
      // 切换到第一个可见的菜单
      const firstVisibleMenu = filteredMenuList.value[0]
      if (firstVisibleMenu) {
        currentMenu.value = firstVisibleMenu.key
      }
    }
    
    return true
  } catch (error) {
    console.error('用户信息解析失败:', error)
    localStorage.removeItem('userInfo')
    localStorage.removeItem('isLoggedIn')
    router.push('/login')
    return false
  }
}

function switchMenu(key) {
  // 检查用户是否有权限访问该菜单
  const menuItem = allMenuList.find(item => item.key === key)
  if (!menuItem || !menuItem.roles.includes(userRole.value)) {
    ElMessage.error('您没有权限访问该功能')
    return
  }
  
  // 如果点击的是违规大屏，跳转到新页面
  if (key === 'screen') {
    router.push('/violation-screen')
    return
  }
  
  currentMenu.value = key
  // 如果不是切换到轨迹回溯页面，清空违规信息
  if (key !== 'track') {
    violationInfoForTrack.value = null
  }
}



function handleSwitchToTrack(violationInfo) {
  violationInfoForTrack.value = violationInfo
  currentMenu.value = 'track'
}

function getMenuIcon(key) {
  const icons = {
    admin: `<svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
      <path d="M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4z"/>
    </svg>`,
    screen: `<svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
      <path d="M21 2H3c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h7l-2 3v1h8v-1l-2-3h7c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm0 12H3V4h18v10z"/>
    </svg>`,
    report: `<svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
      <path d="M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z"/>
    </svg>`,
    mine: `<svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
      <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6zm-1 7V3.5L18.5 9H13z"/>
    </svg>`,
    track: `<svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
      <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/>
    </svg>`,
    appeal: `<svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
      <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
    </svg>`,
    handler: `<svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
      <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 3c1.93 0 3.5 1.57 3.5 3.5S13.93 13 12 13s-3.5-1.57-3.5-3.5S10.07 6 12 6zm7 13H5v-.23c0-.62.28-1.2.76-1.58C7.47 15.82 9.64 15 12 15s4.53.82 6.24 2.19c.48.38.76.97.76 1.58V19z"/>
    </svg>`,
    profile: `<svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
      <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
    </svg>`
  }
  return icons[key] || ''
}

// 页面加载时检查用户权限
onMounted(() => {
  checkUserAuth()
})
</script>

<style scoped>
.home-container {
  display: flex;
  height: 100vh;
  background: #FAF7F2;
}

.sidebar {
  width: 240px;
  background: #FFFFFF;
  border-radius: 0 20px 20px 0;
  box-shadow: 0 2px 12px rgba(45, 52, 54, 0.06);
  display: flex;
  flex-direction: column;
  padding: 24px 16px;
  gap: 8px;
  overflow-y: auto;
  border-right: 1px solid #E8E4DE;
}

.menu-item {
  background: transparent;
  border-radius: 12px;
  border: none;
  padding: 14px 18px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 500;
  color: #636E72;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  text-align: left;
  position: relative;
  overflow: hidden;
}

/* 左侧指示条 */
.menu-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%) scaleY(0);
  width: 4px;
  height: 60%;
  background: linear-gradient(180deg, #7D9E87 0%, #5B8A67 100%);
  border-radius: 0 4px 4px 0;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 背景光晕效果 */
.menu-item::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 30% 50%, rgba(125, 158, 135, 0.1) 0%, transparent 60%);
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

.menu-item:hover {
  background: linear-gradient(135deg, #FFFCF8 0%, #F8F5F0 100%);
  color: #2D3436;
  transform: translateX(4px);
}

.menu-item:hover::after {
  opacity: 0.5;
}

.menu-item.active {
  background: linear-gradient(135deg, #F0F7F2 0%, #E8F0EA 100%);
  color: #2D3436;
  box-shadow: 0 4px 12px rgba(125, 158, 135, 0.15);
  border: 1px solid rgba(125, 158, 135, 0.3);
  transform: translateX(4px);
}

.menu-item.active::before {
  transform: translateY(-50%) scaleY(1);
}

.menu-item.active::after {
  opacity: 1;
}

/* 点击时的涟漪效果 */
.menu-item:active {
  transform: translateX(4px) scale(0.98);
}

.menu-content {
  display: flex;
  align-items: center;
  gap: 12px;
  position: relative;
  z-index: 1;
}

.menu-icon {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0.6;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 8px;
  padding: 2px;
}

.menu-item.active .menu-icon {
  opacity: 1;
  color: #7D9E87;
  background: rgba(125, 158, 135, 0.15);
  transform: scale(1.1);
  animation: iconBounce 0.4s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

.menu-item:hover .menu-icon {
  opacity: 1;
  transform: scale(1.05);
}

/* 图标弹跳动画 */
@keyframes iconBounce {
  0% {
    transform: scale(0.8);
  }
  50% {
    transform: scale(1.2);
  }
  100% {
    transform: scale(1.1);
  }
}

/* 选中状态的文字样式 */
.menu-item.active .menu-content > span:not(.menu-icon) {
  font-weight: 600;
  color: #3D5A45;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.content-area {
  flex: 1;
  background: #FFFFFF;
  border-radius: 20px;
  margin: 24px;
  box-shadow: 0 2px 12px rgba(45, 52, 54, 0.06);
  padding: 32px 40px;
  overflow-y: auto;
  border: 1px solid #E8E4DE;
}
</style> 