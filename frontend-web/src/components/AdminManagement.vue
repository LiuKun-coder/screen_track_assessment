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
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserList, updateUser, updateUserStatus } from '@/api/user'

const adminList = ref([])
const total = ref(0)
const loading = ref(false)

const showAdminDialog = ref(false)
const dialogType = ref('edit')
const dialogAdmin = ref({ id: null, name: '', phone: '', avatar: '', type: '普通用户', status: 'active' })
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

const totalPage = computed(() => Math.max(1, Math.ceil(total.value / pageSize)))
const pagedAdminList = computed(() => adminList.value)

function mapUserToAdmin(user) {
  return {
    id: user.id,
    name: user.name || user.username || `用户${user.id}`,
    phone: user.phone || '-',
    avatar: user.avatar || 'https://i.pravatar.cc/150?img=7',
    type: user.userType === 'student' ? '普通用户' : '管理员',
    status: user.status || 'active',
    raw: user
  }
}

async function fetchUsers() {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize
    }

    if (filterName.value.trim()) {
      params.keyword = filterName.value.trim()
    }
    if (filterType.value !== '全部') {
      params.userType = filterType.value === '管理员' ? 'staff' : 'student'
    }
    if (filterPhone.value.trim()) {
      params.keyword = filterPhone.value.trim()
    }

    const result = await getUserList(params)
    adminList.value = (result.records || []).map(mapUserToAdmin)
    total.value = result.total || 0
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

function openFilterDialog() {
  showFilterDialog.value = true
}

function applyFilter() {
  currentPage.value = 1
  showFilterDialog.value = false
  fetchUsers()
}

function resetFilter() {
  filterType.value = '全部'
  filterName.value = ''
  filterPhone.value = ''
  currentPage.value = 1
  showFilterDialog.value = false
  fetchUsers()
}

function prevPage() {
  if (currentPage.value > 1) {
    currentPage.value--
    fetchUsers()
  }
}

function nextPage() {
  if (currentPage.value < totalPage.value) {
    currentPage.value++
    fetchUsers()
  }
}

function openAddAdmin() {
  ElMessage.info('当前版本暂不支持新增用户，请通过注册入口创建')
}

function openEditAdmin(admin) {
  dialogType.value = 'edit'
  dialogAdmin.value = { ...admin }
  showAdminDialog.value = true
}

async function saveAdmin() {
  try {
    await updateUser(dialogAdmin.value.id, {
      name: dialogAdmin.value.name,
      phone: dialogAdmin.value.phone,
      avatar: dialogAdmin.value.avatar,
      userType: dialogAdmin.value.type === '管理员' ? 'staff' : 'student',
      status: dialogAdmin.value.status
    })
    ElMessage.success('保存成功')
    showAdminDialog.value = false
    fetchUsers()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

function openDeleteAdmin(id) {
  deleteTargetId.value = id
  showDeleteDialog.value = true
}

async function confirmDeleteAdmin() {
  try {
    await updateUserStatus(deleteTargetId.value, 'disabled')
    ElMessage.success('已禁用用户')
    showDeleteDialog.value = false
    fetchUsers()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

function cancelDeleteAdmin() {
  showDeleteDialog.value = false
}

onMounted(() => {
  fetchUsers()
})
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