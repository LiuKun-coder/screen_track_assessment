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
  background: linear-gradient(90deg, #7c3aed 0%, #4f8cff 100%);
  color: #fff;
  border: none;
  border-radius: 16px;
  padding: 10px 36px;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  box-shadow: 0 4px 16px 0 #7c3aed22;
  transition: background 0.25s, transform 0.18s, box-shadow 0.25s;
  letter-spacing: 1px;
}

.add-btn:hover {
  background: linear-gradient(90deg, #ffb6c1 0%, #4f8cff 100%);
  color: #222;
  transform: translateY(-2px) scale(1.04);
  box-shadow: 0 8px 32px 0 #ffb6c144;
}

.admin-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 32px;
  margin-bottom: 32px;
}

.admin-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(90deg, #f0f5ff 0%, #fff 100%);
  border-radius: 24px;
  border: 2.5px solid #e0e7ff;
  padding: 20px 40px;
  font-size: 20px;
  font-weight: 500;
  box-shadow: 0 4px 24px 0 #4f8cff11, 0 1.5px 6px 0 #ffb6c122;
  transition: box-shadow 0.25s, transform 0.18s, border 0.25s;
  position: relative;
  overflow: hidden;
}

.admin-item:hover {
  box-shadow: 0 8px 32px 0 #4f8cff33, 0 2px 8px 0 #ffb6c133;
  border: 2.5px solid #7c3aed;
  transform: translateY(-3px) scale(1.03);
  z-index: 1;
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.admin-avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 2px 12px 0 #4f8cff22;
  margin-right: 12px;
  border: 2.5px solid #fff;
  background: #e0e7ff;
}

.admin-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.admin-meta .admin-name {
  font-size: 20px;
  font-weight: bold;
  color: #4f8cff;
  letter-spacing: 1px;
}

.admin-meta .admin-phone {
  font-size: 15px;
  color: #888;
}

.admin-actions {
  display: flex;
  gap: 18px;
}

.edit-btn, .delete-btn {
  background: linear-gradient(90deg, #fff 0%, #e0e7ff 100%);
  border: 2px solid #4f8cff;
  border-radius: 12px;
  padding: 8px 28px;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.22s, color 0.22s, box-shadow 0.22s, transform 0.18s;
  box-shadow: 0 2px 8px 0 #4f8cff11;
}

.edit-btn:hover {
  background: linear-gradient(90deg, #4f8cff 0%, #7c3aed 100%);
  color: #fff;
  transform: scale(1.07);
  box-shadow: 0 4px 16px 0 #4f8cff33;
}

.delete-btn {
  border: 2px solid #ff4f4f;
  color: #ff4f4f;
}

.delete-btn:hover {
  background: linear-gradient(90deg, #ffb6c1 0%, #ff4f4f 100%);
  color: #fff;
  transform: scale(1.07);
  box-shadow: 0 4px 16px 0 #ffb6c144;
}

.admin-pagination {
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

.admin-type {
  font-size: 14px;
  color: #fff;
  background: #7c3aed;
  border-radius: 8px;
  padding: 2px 10px;
  margin-top: 4px;
  display: inline-block;
  margin-left: 2px;
}

/* 对话框样式 */
.admin-dialog-mask {
  position: fixed;
  left: 0; top: 0; right: 0; bottom: 0;
  background: rgba(79,140,255,0.12);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.admin-dialog {
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

.admin-dialog-title {
  font-size: 22px;
  font-weight: bold;
  color: #4f8cff;
  margin-bottom: 8px;
}

.admin-dialog-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.admin-dialog-row label {
  width: 100px;
  color: #7c3aed;
  font-weight: bold;
}

.admin-dialog-row input {
  flex: 1;
  border: 2px solid #e0e7ff;
  border-radius: 8px;
  padding: 6px 12px;
  font-size: 16px;
  transition: border-color 0.2s;
}

.admin-dialog-row input:focus {
  border-color: #4f8cff;
  outline: none;
}

.admin-dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 18px;
  margin-top: 12px;
}

.admin-dialog-btn {
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

.admin-dialog-btn:hover {
  background: linear-gradient(90deg, #ffb6c1 0%, #4f8cff 100%);
  color: #222;
  transform: scale(1.06);
}

.admin-dialog-btn.cancel {
  background: #fff;
  color: #4f8cff;
  border: 2px solid #4f8cff;
}

.admin-dialog-btn.cancel:hover {
  background: #f0f5ff;
  color: #222;
}
</style> 