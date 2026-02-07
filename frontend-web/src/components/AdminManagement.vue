<template>
  <div class="admin-container">
    <div class="admin-header">
      <button class="add-btn" style="margin-right: 18px;" @click="openFilterDialog">筛选用户</button>
      <button class="add-btn" @click="openAddAdmin">新增用户</button>
    </div>
    <div class="admin-list">
      <div class="admin-item" v-for="admin in pagedAdminList" :key="admin.id">
        <div class="admin-info">
          <img class="admin-avatar" :src="admin.avatar" alt="头像" />
          <div class="admin-meta">
            <span class="admin-name">{{ admin.name }}</span>
            <span class="admin-phone">{{ admin.phone }}</span>
            <span class="admin-type" v-if="admin.type">{{ admin.type }}</span>
          </div>
        </div>
        <div class="admin-actions">
          <button class="edit-btn" @click="openEditAdmin(admin)">编辑</button>
          <button class="delete-btn" @click="openDeleteAdmin(admin.id)">删除</button>
        </div>
      </div>
    </div>
    <div class="admin-pagination">
      <button class="page-btn beautify" @click="prevPage" :disabled="currentPage === 1">上一页</button>
      <span style="margin: 0 16px; color: #888; font-size: 16px;">{{ currentPage }} / {{ totalPage }}</span>
      <button class="page-btn beautify" @click="nextPage" :disabled="currentPage === totalPage">下一页</button>
    </div>
    
    <!-- 筛选对话框 -->
    <div v-if="showFilterDialog" class="admin-dialog-mask">
      <div class="admin-dialog">
        <div class="admin-dialog-title">筛选用户</div>
        <div class="admin-dialog-row">
          <label>类型</label>
          <select v-model="filterType" style="flex:1; border:2px solid #e0e7ff; border-radius:8px; padding:6px 12px; font-size:16px;">
            <option value="全部">全部</option>
            <option value="管理员">管理员</option>
            <option value="普通用户">普通用户</option>
          </select>
        </div>
        <div class="admin-dialog-row">
          <label>姓名</label>
          <input v-model="filterName" placeholder="请输入姓名" style="flex:1; border:2px solid #e0e7ff; border-radius:8px; padding:6px 12px; font-size:16px;" />
        </div>
        <div class="admin-dialog-row">
          <label>手机号</label>
          <input v-model="filterPhone" maxlength="4" placeholder="后四位" style="flex:1; border:2px solid #e0e7ff; border-radius:8px; padding:6px 12px; font-size:16px;" />
        </div>
        <div class="admin-dialog-actions">
          <button class="admin-dialog-btn" @click="applyFilter">确定</button>
          <button class="admin-dialog-btn cancel" @click="resetFilter">重置</button>
        </div>
      </div>
    </div>
    
    <!-- 新增/编辑对话框 -->
    <div v-if="showAdminDialog" class="admin-dialog-mask">
      <div class="admin-dialog">
        <div class="admin-dialog-title">{{ dialogType === 'add' ? '新增用户' : '编辑用户' }}</div>
        <div class="admin-dialog-row">
          <label>姓名</label>
          <input v-model="dialogAdmin.name" placeholder="请输入姓名" />
        </div>
        <div class="admin-dialog-row">
          <label>类型</label>
          <select v-model="dialogAdmin.type" style="flex:1; border:2px solid #e0e7ff; border-radius:8px; padding:6px 12px; font-size:16px;">
            <option value="管理员">管理员</option>
            <option value="普通用户">普通用户</option>
          </select>
        </div>
        <div class="admin-dialog-row">
          <label>电话</label>
          <input v-model="dialogAdmin.phone" placeholder="请输入电话" />
        </div>
        <div class="admin-dialog-row">
          <label>头像</label>
          <input v-model="dialogAdmin.avatar" placeholder="头像URL（可选）" />
        </div>
        <div class="admin-dialog-actions">
          <button class="admin-dialog-btn" @click="saveAdmin">保存</button>
          <button class="admin-dialog-btn cancel" @click="showAdminDialog = false">取消</button>
        </div>
      </div>
    </div>
    
    <!-- 删除确认对话框 -->
    <div v-if="showDeleteDialog" class="admin-dialog-mask">
      <div class="admin-dialog">
        <div class="admin-dialog-title">确认删除</div>
        <div style="margin-bottom: 18px; color: #ff4f4f;">确定要删除该用户吗？</div>
        <div class="admin-dialog-actions">
          <button class="admin-dialog-btn" @click="confirmDeleteAdmin">确认</button>
          <button class="admin-dialog-btn cancel" @click="cancelDeleteAdmin">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// 管理人员示例数据
const adminList = ref([
  { id: 1, name: '张三', role: '超级管理员', phone: '138****8888', avatar: 'https://i.pravatar.cc/150?img=1', type: '管理员' },
  { id: 2, name: '李四', role: '管理员', phone: '139****6666', avatar: 'https://i.pravatar.cc/150?img=2', type: '管理员' },
  { id: 3, name: '王五', role: '管理员', phone: '137****7777', avatar: 'https://i.pravatar.cc/150?img=3', type: '管理员' },
  { id: 4, name: '赵六', role: '管理员', phone: '136****8888', avatar: 'https://i.pravatar.cc/150?img=4', type: '管理员' },
  { id: 5, name: '钱七', role: '管理员', phone: '135****9999', avatar: 'https://i.pravatar.cc/150?img=5', type: '管理员' },
  { id: 6, name: '孙八', role: '普通用户', phone: '134****0000', avatar: 'https://i.pravatar.cc/150?img=6', type: '普通用户' },
  { id: 7, name: '周九', role: '普通用户', phone: '133****1111', avatar: 'https://i.pravatar.cc/150?img=7', type: '普通用户' },
])

const showAdminDialog = ref(false)
const dialogType = ref('add')
const dialogAdmin = ref({ id: null, name: '', phone: '', avatar: '', type: '普通用户' })
const showDeleteDialog = ref(false)
const deleteTargetId = ref(null)

// 分页相关
const pageSize = 4
const currentPage = ref(1)

// 筛选相关
const showFilterDialog = ref(false)
const filterType = ref('全部')
const filterName = ref('')
const filterPhone = ref('')

const filteredAdminList = computed(() => {
  let list = adminList.value
  if (filterType.value !== '全部') list = list.filter(a => a.type === filterType.value)
  if (filterName.value.trim()) list = list.filter(a => a.name.includes(filterName.value.trim()))
  if (filterPhone.value.trim()) list = list.filter(a => a.phone.slice(-4) === filterPhone.value.trim())
  return list
})

const totalPage = computed(() => Math.ceil(filteredAdminList.value.length / pageSize))
const pagedAdminList = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredAdminList.value.slice(start, start + pageSize)
})

function openFilterDialog() {
  showFilterDialog.value = true
}

function applyFilter() {
  currentPage.value = 1
  showFilterDialog.value = false
}

function resetFilter() {
  filterType.value = '全部'
  filterName.value = ''
  filterPhone.value = ''
  currentPage.value = 1
  showFilterDialog.value = false
}

function prevPage() {
  if (currentPage.value > 1) currentPage.value--
}

function nextPage() {
  if (currentPage.value < totalPage.value) currentPage.value++
}

function openAddAdmin() {
  dialogType.value = 'add'
  dialogAdmin.value = { id: null, name: '', phone: '', avatar: '', type: '普通用户' }
  showAdminDialog.value = true
}

function openEditAdmin(admin) {
  dialogType.value = 'edit'
  dialogAdmin.value = { ...admin }
  showAdminDialog.value = true
}

function saveAdmin() {
  if (dialogType.value === 'add') {
    const newId = Math.max(...adminList.value.map(a => a.id)) + 1
    adminList.value.push({ ...dialogAdmin.value, id: newId, avatar: dialogAdmin.value.avatar || 'https://i.pravatar.cc/150?img=7' })
    currentPage.value = totalPage.value
  } else {
    const idx = adminList.value.findIndex(a => a.id === dialogAdmin.value.id)
    if (idx !== -1) adminList.value[idx] = { ...dialogAdmin.value }
  }
  showAdminDialog.value = false
}

function openDeleteAdmin(id) {
  deleteTargetId.value = id
  showDeleteDialog.value = true
}

function confirmDeleteAdmin() {
  adminList.value = adminList.value.filter(a => a.id !== deleteTargetId.value)
  if (currentPage.value > totalPage.value) currentPage.value = totalPage.value
  showDeleteDialog.value = false
}

function cancelDeleteAdmin() {
  showDeleteDialog.value = false
}
</script>

<style scoped>
.admin-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.admin-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 24px;
}

.add-btn {
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
  color: #FFFFFF;
  border: none;
  border-radius: 10px;
  padding: 10px 28px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(107, 154, 196, 0.2);
  transition: all 0.2s ease;
}

.add-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(107, 154, 196, 0.35);
}

.admin-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 24px;
}

.admin-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #FFFFFF;
  border-radius: 16px;
  border: 1px solid #E8E4DE;
  padding: 20px 28px;
  font-size: 15px;
  font-weight: 500;
  box-shadow: 0 2px 12px rgba(45, 52, 54, 0.04);
  transition: all 0.2s ease;
}

.admin-item:hover {
  box-shadow: 0 4px 20px rgba(45, 52, 54, 0.08);
  border-color: #6B9AC4;
  transform: translateY(-2px);
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.admin-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 2px 8px rgba(45, 52, 54, 0.1);
  margin-right: 8px;
  border: 2px solid #FFFFFF;
  background: #FAF7F2;
}

.admin-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.admin-meta .admin-name {
  font-size: 16px;
  font-weight: 600;
  color: #2D3436;
}

.admin-meta .admin-phone {
  font-size: 14px;
  color: #636E72;
}

.admin-actions {
  display: flex;
  gap: 12px;
}

.edit-btn, .delete-btn {
  background: #FFFFFF;
  border: 1px solid #E8E4DE;
  border-radius: 10px;
  padding: 8px 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  color: #636E72;
}

.edit-btn:hover {
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
  color: #FFFFFF;
  border-color: transparent;
  box-shadow: 0 4px 12px rgba(107, 154, 196, 0.25);
}

.delete-btn {
  border-color: #C9735D;
  color: #C9735D;
}

.delete-btn:hover {
  background: linear-gradient(135deg, #C9735D 0%, #b86350 100%);
  color: #FFFFFF;
  border-color: transparent;
  box-shadow: 0 4px 12px rgba(201, 115, 93, 0.25);
}

.admin-pagination {
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

.admin-type {
  font-size: 12px;
  color: #FFFFFF;
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
  border-radius: 6px;
  padding: 2px 10px;
  margin-top: 4px;
  display: inline-block;
}

/* 对话框样式 */
.admin-dialog-mask {
  position: fixed;
  left: 0; top: 0; right: 0; bottom: 0;
  background: rgba(45, 52, 54, 0.15);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.admin-dialog {
  background: #FFFFFF;
  border-radius: 20px;
  box-shadow: 0 8px 40px rgba(45, 52, 54, 0.15);
  padding: 32px 40px;
  min-width: 400px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.admin-dialog-title {
  font-size: 20px;
  font-weight: 600;
  color: #2D3436;
  margin-bottom: 8px;
  text-align: center;
}

.admin-dialog-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.admin-dialog-row label {
  width: 80px;
  color: #7D9E87;
  font-weight: 600;
  font-size: 14px;
}

.admin-dialog-row input {
  flex: 1;
  border: 1px solid #E8E4DE;
  border-radius: 10px;
  padding: 10px 14px;
  font-size: 15px;
  transition: all 0.2s ease;
  background: #FAF7F2;
}

.admin-dialog-row input:focus {
  border-color: #6B9AC4;
  outline: none;
  box-shadow: 0 0 0 3px rgba(107, 154, 196, 0.15);
}

.admin-dialog-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 12px;
}

.admin-dialog-btn {
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

.admin-dialog-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(107, 154, 196, 0.35);
}

.admin-dialog-btn.cancel {
  background: #FFFFFF;
  color: #636E72;
  border: 1px solid #E8E4DE;
  box-shadow: none;
}

.admin-dialog-btn.cancel:hover {
  background: #FAF7F2;
}
</style> 