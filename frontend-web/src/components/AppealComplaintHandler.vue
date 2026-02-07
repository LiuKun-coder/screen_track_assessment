<template>
  <div class="handler-container">
    <div class="handler-header">
      <h2 class="handler-title">申诉处理</h2>
      <p class="handler-subtitle">管理员工作台 - 处理用户提交的申诉事项</p>
    </div>

    <!-- 统计概览 -->
    <div class="stats-overview">
      <div class="stat-card">
        <div class="stat-icon pending">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ pendingCount }}</div>
          <div class="stat-label">待处理</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon processing">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 4V2A10 10 0 0 0 2 12h2a8 8 0 0 1 8-8z"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ processingCount }}</div>
          <div class="stat-label">处理中</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon completed">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
            <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ completedCount }}</div>
          <div class="stat-label">已完成</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon total">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
            <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 3c1.93 0 3.5 1.57 3.5 3.5S13.93 13 12 13s-3.5-1.57-3.5-3.5S10.07 6 12 6zm7 13H5v-.23c0-.62.28-1.2.76-1.58C7.47 15.82 9.64 15 12 15s4.53.82 6.24 2.19c.48.38.76.97.76 1.58V19z"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ totalCount }}</div>
          <div class="stat-label">总计</div>
        </div>
      </div>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <div class="filter-tabs">
        <button 
          v-for="tab in filterTabs" 
          :key="tab.key"
          class="filter-tab"
          :class="{ active: currentFilter === tab.key }"
          @click="setFilter(tab.key)"
        >
          {{ tab.label }}
        </button>
      </div>
      <div class="search-section">
        <div class="search-input-group">
          <input 
            v-model="searchKeyword"
            type="text"
            placeholder="搜索编号、用户、关键词..."
            class="search-input"
          >
          <button class="search-btn" @click="handleSearch">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
              <path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
            </svg>
          </button>
        </div>
        <div class="priority-filter">
          <select v-model="priorityFilter" class="priority-select">
            <option value="">所有优先级</option>
            <option value="high">高优先级</option>
            <option value="medium">中优先级</option>
            <option value="low">低优先级</option>
          </select>
        </div>
      </div>
    </div>

    <!-- 处理列表 -->
    <div class="handler-list">
      <div class="list-header">
        <div class="list-title">{{ getListTitle() }}</div>
        <div class="list-actions">
          <button class="batch-btn" @click="batchProcess" :disabled="selectedItems.length === 0">
            批量处理 ({{ selectedItems.length }})
          </button>
          <button class="refresh-btn" @click="refreshList">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor">
              <path d="M17.65 6.35C16.2 4.9 14.21 4 12 4c-4.42 0-7.99 3.58-7.99 8s3.57 8 7.99 8c3.73 0 6.84-2.55 7.73-6h-2.08c-.82 2.33-3.04 4-5.65 4-3.31 0-6-2.69-6-6s2.69-6 6-6c1.66 0 3.14.69 4.22 1.78L13 11h7V4l-2.35 2.35z"/>
            </svg>
          </button>
        </div>
      </div>

      <div class="list-content">
        <div v-if="filteredItems.length === 0" class="empty-state">
          <div class="empty-icon">
            <svg width="64" height="64" viewBox="0 0 24 24" fill="currentColor">
              <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 3c1.93 0 3.5 1.57 3.5 3.5S13.93 13 12 13s-3.5-1.57-3.5-3.5S10.07 6 12 6zm7 13H5v-.23c0-.62.28-1.2.76-1.58C7.47 15.82 9.64 15 12 15s4.53.82 6.24 2.19c.48.38.76.97.76 1.58V19z"/>
            </svg>
          </div>
          <div class="empty-text">暂无{{ getListTitle() }}申诉事项</div>
        </div>

        <div v-else class="items-grid">
          <div 
            v-for="item in paginatedItems" 
            :key="item.id"
            class="item-card"
            :class="{ 
              selected: selectedItems.includes(item.id),
              urgent: item.priority === 'high'
            }"
          >
            <div class="item-header">
              <div class="item-checkbox">
                <input 
                  type="checkbox" 
                  :value="item.id"
                  v-model="selectedItems"
                  class="checkbox-input"
                >
              </div>
              <div class="item-type" :class="item.type">
                申诉
              </div>
              <div class="item-priority" :class="item.priority">
                {{ getPriorityText(item.priority) }}
              </div>
              <div class="item-status" :class="item.status">
                {{ getStatusText(item.status) }}
              </div>
            </div>

            <div class="item-content">
              <div class="item-title">{{ item.title }}</div>
              <div class="item-info">
                <div class="info-row">
                  <span class="info-label">编号:</span>
                  <span class="info-value">{{ item.number }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">提交人:</span>
                  <span class="info-value">{{ item.submitter }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">提交时间:</span>
                  <span class="info-value">{{ item.submitTime }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">处理期限:</span>
                  <span class="info-value" :class="{ overdue: isOverdue(item.deadline) }">
                    {{ item.deadline }}
                  </span>
                </div>
              </div>
              <div class="item-description">
                {{ item.description }}
              </div>
            </div>

            <div class="item-actions">
              <button class="action-btn view" @click="viewDetail(item)">
                查看详情
              </button>
              <button 
                v-if="item.status === 'pending'"
                class="action-btn process" 
                @click="startProcess(item)"
              >
                开始处理
              </button>
              <button 
                v-if="item.status === 'processing'"
                class="action-btn complete" 
                @click="completeProcess(item)"
              >
                完成处理
              </button>
              <button 
                v-if="item.status === 'completed'"
                class="action-btn review" 
                @click="reviewProcess(item)"
              >
                查看结果
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="totalPages > 1">
        <button 
          class="page-btn"
          @click="currentPage = 1"
          :disabled="currentPage === 1"
        >
          首页
        </button>
        <button 
          class="page-btn"
          @click="currentPage--"
          :disabled="currentPage === 1"
        >
          上一页
        </button>
        <span class="page-info">
          第 {{ currentPage }} 页，共 {{ totalPages }} 页
        </span>
        <button 
          class="page-btn"
          @click="currentPage++"
          :disabled="currentPage === totalPages"
        >
          下一页
        </button>
        <button 
          class="page-btn"
          @click="currentPage = totalPages"
          :disabled="currentPage === totalPages"
        >
          末页
        </button>
      </div>
    </div>

    <!-- 详情对话框 -->
    <div v-if="showDetailDialog" class="detail-dialog-mask">
      <div class="detail-dialog">
        <div class="dialog-header">
          <h3>申诉详情</h3>
          <button class="close-btn" @click="closeDetailDialog">×</button>
        </div>
        <div class="dialog-content">
          <div class="detail-section">
            <h4>基本信息</h4>
            <div class="detail-grid">
              <div class="detail-item">
                <span class="detail-label">编号:</span>
                <span class="detail-value">{{ currentItem.number }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">类型:</span>
                <span class="detail-value">申诉</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">优先级:</span>
                <span class="detail-value">{{ getPriorityText(currentItem.priority) }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">状态:</span>
                <span class="detail-value">{{ getStatusText(currentItem.status) }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">提交人:</span>
                <span class="detail-value">{{ currentItem.submitter }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">联系方式:</span>
                <span class="detail-value">{{ currentItem.contact }}</span>
              </div>
            </div>
          </div>

          <div class="detail-section">
            <h4>申诉内容</h4>
            <div class="content-box">
              <div class="content-title">{{ currentItem.title }}</div>
              <div class="content-description">{{ currentItem.fullDescription }}</div>
            </div>
          </div>

          <div class="detail-section" v-if="currentItem.evidence && currentItem.evidence.length > 0">
            <h4>证据材料</h4>
            <div class="evidence-list">
              <div v-for="(evidence, index) in currentItem.evidence" :key="index" class="evidence-item">
                <div class="evidence-icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20Z"/>
                  </svg>
                </div>
                <span class="evidence-name">{{ evidence.name }}</span>
                <button class="evidence-download">下载</button>
              </div>
            </div>
          </div>

          <div class="detail-section" v-if="currentItem.processHistory && currentItem.processHistory.length > 0">
            <h4>处理历史</h4>
            <div class="history-timeline">
              <div v-for="(history, index) in currentItem.processHistory" :key="index" class="history-item">
                <div class="history-time">{{ history.time }}</div>
                <div class="history-content">
                  <div class="history-action">{{ history.action }}</div>
                  <div class="history-operator">操作人: {{ history.operator }}</div>
                  <div class="history-note" v-if="history.note">{{ history.note }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="dialog-actions">
          <button 
            v-if="currentItem.status === 'pending'"
            class="dialog-btn primary" 
            @click="startProcessFromDialog"
          >
            开始处理
          </button>
          <button 
            v-if="currentItem.status === 'processing'"
            class="dialog-btn primary" 
            @click="completeProcessFromDialog"
          >
            完成处理
          </button>
          <button class="dialog-btn" @click="closeDetailDialog">关闭</button>
        </div>
      </div>
    </div>

    <!-- 处理对话框 -->
    <div v-if="showProcessDialog" class="process-dialog-mask">
      <div class="process-dialog">
        <div class="dialog-header">
          <h3>处理申诉</h3>
          <button class="close-btn" @click="closeProcessDialog">×</button>
        </div>
        <div class="dialog-content">
          <div class="process-section">
            <h4>处理结果</h4>
            <div class="result-options">
              <label class="result-option">
                <input type="radio" v-model="processResult" value="approved">
                <span>申诉成立</span>
              </label>
              <label class="result-option">
                <input type="radio" v-model="processResult" value="rejected">
                <span>申诉不成立</span>
              </label>
              <label class="result-option">
                <input type="radio" v-model="processResult" value="partial">
                <span>部分成立</span>
              </label>
            </div>
          </div>

          <div class="process-section">
            <h4>处理说明</h4>
            <textarea 
              v-model="processNote"
              class="process-textarea"
              placeholder="请详细说明处理过程、依据和结果..."
              rows="6"
            ></textarea>
          </div>

          <div class="process-section">
            <h4>后续措施</h4>
            <div class="measures-list">
              <label class="measure-item">
                <input type="checkbox" v-model="followUpMeasures" value="warning">
                <span>警告处理</span>
              </label>
              <label class="measure-item">
                <input type="checkbox" v-model="followUpMeasures" value="education">
                <span>教育培训</span>
              </label>
              <label class="measure-item">
                <input type="checkbox" v-model="followUpMeasures" value="rectification">
                <span>整改要求</span>
              </label>
              <label class="measure-item">
                <input type="checkbox" v-model="followUpMeasures" value="notification">
                <span>通报批评</span>
              </label>
            </div>
          </div>
        </div>
        <div class="dialog-actions">
          <button class="dialog-btn" @click="closeProcessDialog">取消</button>
          <button 
            class="dialog-btn primary" 
            @click="submitProcess"
            :disabled="!processResult || !processNote.trim()"
          >
            提交处理结果
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getAppealList, handleAppeal, getAppealDetail } from '@/api/appeal'
import { ElMessage } from 'element-plus'

// 响应式数据
const currentFilter = ref('all')
const searchKeyword = ref('')
const priorityFilter = ref('')
const selectedItems = ref([])
const currentPage = ref(1)
const pageSize = 6
const showDetailDialog = ref(false)
const showProcessDialog = ref(false)
const currentItem = ref(null)
const processResult = ref('')
const processNote = ref('')
const followUpMeasures = ref([])
const loading = ref(false)
const total = ref(0)

// 筛选选项
const filterTabs = [
  { key: 'all', label: '全部' },
  { key: 'pending', label: '待处理' },
  { key: 'processing', label: '处理中' },
  { key: 'completed', label: '已完成' },
  { key: 'appeal', label: '申诉' }
]

// 申诉列表数据
const allItems = ref([])

// 从后端获取申诉列表
async function fetchAppealList() {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize
    }
    
    // 根据筛选条件添加status参数
    if (['pending', 'processing', 'completed'].includes(currentFilter.value)) {
      // 将状态映射到后端格式
      const statusMap = {
        'pending': 'pending',
        'processing': 'pending', // 后端可能没有processing，暂时归为pending
        'completed': 'completed'
      }
      // 后端状态：pending, approved, rejected
      if (currentFilter.value === 'completed') {
        // completed 不传status，或者传 approved/rejected
      } else if (currentFilter.value === 'pending') {
        params.status = 'pending'
      }
    }
    
    const result = await getAppealList(params)
    
    // 转换数据格式以适配模板
    allItems.value = (result.records || []).map(item => {
      // 解析JSON字段
      let evidenceFiles = []
      try {
        evidenceFiles = item.evidenceFiles ? JSON.parse(item.evidenceFiles) : []
      } catch (e) {
        evidenceFiles = []
      }
      
      // 根据后端状态映射到前端状态
      let frontStatus = 'pending'
      if (item.status === 'pending') {
        frontStatus = 'pending'
      } else if (item.status === 'approved' || item.status === 'rejected') {
        frontStatus = 'completed'
      }
      
      return {
        id: item.id,
        type: 'appeal',
        number: item.appealNumber || `AP${item.id}`,
        title: `申诉违规记录 #${item.violationId}`,
        submitter: `用户${item.userId}`,
        contact: item.contactPhone || '***',
        submitTime: item.createTime ? item.createTime.replace('T', ' ').substring(0, 16) : '',
        deadline: getDeadline(item.createTime),
        status: frontStatus,
        priority: 'medium',
        description: item.description ? item.description.substring(0, 50) + '...' : '',
        fullDescription: item.description || '',
        reason: item.reason,
        evidence: evidenceFiles.map((url, idx) => ({ name: `证据${idx + 1}`, url })),
        replyContent: item.replyContent,
        handleTime: item.handleTime,
        processHistory: buildProcessHistory(item),
        // 保留原始数据
        ...item
      }
    })
    total.value = result.total || 0
  } catch (error) {
    console.error('获取申诉列表失败:', error)
    // 使用示例数据作为后备
    allItems.value = [
      {
        id: 1, type: 'appeal', number: 'AP20240601001', title: '对超速违规处理结果的申诉',
        submitter: '张三', contact: '138****5678', submitTime: '2024-06-01 10:30',
        deadline: '2024-06-04 17:00', status: 'pending', priority: 'high',
        description: '认为超速检测设备存在故障...', fullDescription: '详细描述...',
        evidence: [], processHistory: []
      }
    ]
    total.value = allItems.value.length
  } finally {
    loading.value = false
  }
}

// 构建处理历史
function buildProcessHistory(item) {
  const history = []
  if (item.createTime) {
    history.push({
      time: item.createTime.replace('T', ' ').substring(0, 16),
      action: '申诉提交',
      operator: '系统',
      note: '用户提交申诉申请'
    })
  }
  if (item.handleTime && item.status !== 'pending') {
    history.push({
      time: item.handleTime.replace('T', ' ').substring(0, 16),
      action: '处理完成',
      operator: '管理员',
      note: item.replyContent || '已处理'
    })
  }
  return history
}

// 计算处理期限（提交后3天）
function getDeadline(createTime) {
  if (!createTime) return ''
  const date = new Date(createTime)
  date.setDate(date.getDate() + 3)
  return date.toISOString().replace('T', ' ').substring(0, 16)
}

// 计算属性
const filteredItems = computed(() => {
  let items = allItems.value

  // 按类型和状态筛选
  if (currentFilter.value !== 'all') {
    if (['pending', 'processing', 'completed'].includes(currentFilter.value)) {
      items = items.filter(item => item.status === currentFilter.value)
    } else if (currentFilter.value === 'appeal') {
      items = items.filter(item => item.type === currentFilter.value)
    }
  }

  // 按优先级筛选
  if (priorityFilter.value) {
    items = items.filter(item => item.priority === priorityFilter.value)
  }

  // 按关键词搜索
  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.toLowerCase()
    items = items.filter(item => 
      item.number.toLowerCase().includes(keyword) ||
      item.title.toLowerCase().includes(keyword) ||
      item.submitter.toLowerCase().includes(keyword) ||
      item.description.toLowerCase().includes(keyword)
    )
  }

  return items
})

const paginatedItems = computed(() => filteredItems.value)

const totalPages = computed(() => {
  return Math.max(1, Math.ceil(total.value / pageSize))
})

// 统计数据
const pendingCount = computed(() => 
  allItems.value.filter(item => item.status === 'pending').length
)

const processingCount = computed(() => 
  allItems.value.filter(item => item.status === 'processing').length
)

const completedCount = computed(() => 
  allItems.value.filter(item => item.status === 'completed').length
)

const totalCount = computed(() => total.value)

// 方法
function setFilter(key) {
  currentFilter.value = key
  currentPage.value = 1
  selectedItems.value = []
  fetchAppealList()
}

function handleSearch() {
  currentPage.value = 1
}

function getListTitle() {
  const filterMap = {
    'all': '全部',
    'pending': '待处理',
    'processing': '处理中',
    'completed': '已完成',
    'appeal': '申诉'
  }
  return filterMap[currentFilter.value] || '全部'
}

function getPriorityText(priority) {
  const priorityMap = {
    'high': '高',
    'medium': '中',
    'low': '低'
  }
  return priorityMap[priority] || ''
}

function getStatusText(status) {
  const statusMap = {
    'pending': '待处理',
    'processing': '处理中',
    'completed': '已完成'
  }
  return statusMap[status] || ''
}

function isOverdue(deadline) {
  return new Date(deadline) < new Date()
}

function viewDetail(item) {
  currentItem.value = item
  showDetailDialog.value = true
}

function closeDetailDialog() {
  showDetailDialog.value = false
  currentItem.value = null
}

function startProcess(item) {
  currentItem.value = item
  showProcessDialog.value = true
}

function startProcessFromDialog() {
  closeDetailDialog()
  showProcessDialog.value = true
}

function completeProcess(item) {
  currentItem.value = item
  showProcessDialog.value = true
}

function completeProcessFromDialog() {
  closeDetailDialog()
  showProcessDialog.value = true
}

function reviewProcess(item) {
  viewDetail(item)
}

function closeProcessDialog() {
  showProcessDialog.value = false
  processResult.value = ''
  processNote.value = ''
  followUpMeasures.value = []
}

// 提交处理结果到后端
async function submitProcess() {
  if (!currentItem.value) return
  
  try {
    // 将处理结果映射到后端格式
    const statusMap = {
      'approved': 'approved',
      'rejected': 'rejected',
      'partial': 'approved' // 部分成立按成立处理
    }
    
    const resultText = processResult.value === 'approved' ? '申诉成立' : 
                       processResult.value === 'rejected' ? '申诉不成立' : '部分成立'
    
    await handleAppeal(currentItem.value.id, {
      status: statusMap[processResult.value] || 'rejected',
      replyContent: `${resultText}。${processNote.value}`
    })
    
    ElMessage.success('处理完成！')
    closeProcessDialog()
    closeDetailDialog()
    
    // 刷新列表
    fetchAppealList()
  } catch (error) {
    console.error('处理申诉失败:', error)
  }
}

function batchProcess() {
  if (selectedItems.value.length === 0) return
  ElMessage.warning('批量处理功能暂未实现')
}

function refreshList() {
  selectedItems.value = []
  currentPage.value = 1
  fetchAppealList()
  ElMessage.success('列表已刷新！')
}

// 分页
function goToPage(page) {
  currentPage.value = page
  fetchAppealList()
}

// 生命周期
onMounted(() => {
  fetchAppealList()
})
</script>

<style scoped>
.handler-container {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.handler-header {
  text-align: center;
  margin-bottom: 30px;
}

.handler-title {
  font-size: 28px;
  font-weight: 600;
  color: #2D3436;
  margin-bottom: 12px;
}

.handler-subtitle {
  font-size: 16px;
  color: #636E72;
  margin: 0;
}

/* 统计概览 */
.stats-overview {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 30px;
}

.stat-card {
  background: #FFFFFF;
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(45, 52, 54, 0.06);
  border: 1px solid #E8E4DE;
  transition: all 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(45, 52, 54, 0.1);
  border-color: #6B9AC4;
}

.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-icon.pending {
  background: linear-gradient(135deg, #D4A574 0%, #C9735D 100%);
}

.stat-icon.processing {
  background: linear-gradient(135deg, #6B9AC4 0%, #7D9E87 100%);
}

.stat-icon.completed {
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
}

.stat-icon.total {
  background: linear-gradient(135deg, #636E72 0%, #2D3436 100%);
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: 600;
  color: #2D3436;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 13px;
  color: #636E72;
  font-weight: 500;
}

/* 筛选和搜索 */
.filter-section {
  background: #FFFFFF;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 30px;
  box-shadow: 0 2px 12px rgba(45, 52, 54, 0.06);
  border: 1px solid #E8E4DE;
}

.filter-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.filter-tab {
  padding: 8px 16px;
  border-radius: 10px;
  border: 1px solid #E8E4DE;
  background: #FFFFFF;
  color: #636E72;
  font-weight: 500;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.filter-tab:hover {
  background: #FAF7F2;
  border-color: #6B9AC4;
  color: #2D3436;
}

.filter-tab.active {
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
  color: #FFFFFF;
  border-color: transparent;
}

.search-section {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.search-input-group {
  display: flex;
  flex: 1;
  min-width: 280px;
}

.search-input {
  flex: 1;
  padding: 10px 14px;
  border: 1px solid #E8E4DE;
  border-radius: 10px 0 0 10px;
  font-size: 15px;
  outline: none;
  transition: all 0.2s ease;
  background: #FAF7F2;
}

.search-input:focus {
  border-color: #6B9AC4;
  box-shadow: 0 0 0 3px rgba(107, 154, 196, 0.15);
}

.search-btn {
  padding: 10px 14px;
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
  color: #FFFFFF;
  border: none;
  border-radius: 0 10px 10px 0;
  cursor: pointer;
  transition: all 0.2s ease;
}

.search-btn:hover {
  box-shadow: 0 4px 12px rgba(107, 154, 196, 0.25);
}

.priority-filter {
  min-width: 140px;
}

.priority-select {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #E8E4DE;
  border-radius: 10px;
  font-size: 15px;
  outline: none;
  transition: all 0.2s ease;
  background: #FAF7F2;
}

.priority-select:focus {
  border-color: #6B9AC4;
}

/* 处理列表 */
.handler-list {
  background: #FFFFFF;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(45, 52, 54, 0.06);
  border: 1px solid #E8E4DE;
  overflow: hidden;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #FAF7F2;
  border-bottom: 1px solid #E8E4DE;
}

.list-title {
  font-size: 18px;
  font-weight: 600;
  color: #2D3436;
}

.list-actions {
  display: flex;
  gap: 10px;
}

.batch-btn,
.refresh-btn {
  padding: 8px 14px;
  border-radius: 10px;
  border: 1px solid #E8E4DE;
  background: #FFFFFF;
  color: #636E72;
  font-weight: 500;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.batch-btn:hover,
.refresh-btn:hover {
  background: #FAF7F2;
  border-color: #6B9AC4;
  color: #2D3436;
}

.batch-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.refresh-btn {
  padding: 8px 10px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #636E72;
}

.empty-icon {
  color: #E8E4DE;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 16px;
}

/* 项目网格 */
.items-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 16px;
  padding: 20px;
}

.item-card {
  background: #FFFFFF;
  border-radius: 12px;
  border: 1px solid #E8E4DE;
  padding: 18px;
  transition: all 0.2s ease;
}

.item-card:hover {
  border-color: #6B9AC4;
  box-shadow: 0 4px 16px rgba(45, 52, 54, 0.08);
  transform: translateY(-2px);
}

.item-card.selected {
  border-color: #7D9E87;
  background: #F7FBF8;
}

.item-card.urgent {
  border-left: 3px solid #C9735D;
}

.item-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
}

.item-checkbox {
  display: flex;
  align-items: center;
}

.checkbox-input {
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.item-type {
  padding: 4px 10px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 500;
  color: white;
}

.item-type.appeal {
  background: linear-gradient(135deg, #6B9AC4 0%, #7D9E87 100%);
}

.item-priority {
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

.item-priority.high {
  background: #FCE8E6;
  color: #C9735D;
}

.item-priority.medium {
  background: #FEF3E2;
  color: #D4A574;
}

.item-priority.low {
  background: #E7F2EA;
  color: #7D9E87;
}

.item-status {
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  margin-left: auto;
}

.item-status.pending {
  background: #FEF3E2;
  color: #D4A574;
}

.item-status.processing {
  background: #E9F3FC;
  color: #6B9AC4;
}

.item-status.completed {
  background: #E7F2EA;
  color: #7D9E87;
}

.item-content {
  margin-bottom: 14px;
}

.item-title {
  font-size: 15px;
  font-weight: 600;
  color: #2D3436;
  margin-bottom: 10px;
}

.item-info {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 6px;
  margin-bottom: 10px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 6px;
}

.info-label {
  font-size: 13px;
  color: #636E72;
  font-weight: 500;
  min-width: 50px;
}

.info-value {
  font-size: 13px;
  color: #2D3436;
  flex: 1;
}

.info-value.overdue {
  color: #C9735D;
  font-weight: 600;
}

.item-description {
  font-size: 13px;
  color: #636E72;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 6px 12px;
  border-radius: 8px;
  border: none;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-btn.view {
  background: #FAF7F2;
  color: #636E72;
  border: 1px solid #E8E4DE;
}

.action-btn.view:hover {
  background: #E8E4DE;
}

.action-btn.process {
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
  color: #FFFFFF;
}

.action-btn.process:hover {
  box-shadow: 0 4px 12px rgba(107, 154, 196, 0.25);
}

.action-btn.complete {
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
  color: #FFFFFF;
}

.action-btn.complete:hover {
  box-shadow: 0 4px 12px rgba(125, 158, 135, 0.25);
}

.action-btn.review {
  background: linear-gradient(135deg, #636E72 0%, #2D3436 100%);
  color: #FFFFFF;
}

.action-btn.review:hover {
  box-shadow: 0 4px 12px rgba(45, 52, 54, 0.25);
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  padding: 16px;
  background: #FAF7F2;
  border-top: 1px solid #E8E4DE;
}

.page-btn {
  padding: 8px 14px;
  border-radius: 8px;
  border: 1px solid #E8E4DE;
  background: #FFFFFF;
  color: #636E72;
  font-weight: 500;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.page-btn:hover:not(:disabled) {
  background: #FAF7F2;
  border-color: #6B9AC4;
  color: #2D3436;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 13px;
  color: #636E72;
  margin: 0 8px;
}

/* 对话框 */
.detail-dialog-mask,
.process-dialog-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(45, 52, 54, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.detail-dialog,
.process-dialog {
  background: #FFFFFF;
  border-radius: 20px;
  max-width: 800px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 8px 40px rgba(45, 52, 54, 0.2);
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 24px;
  border-bottom: 1px solid #E8E4DE;
  background: #FAF7F2;
}

.dialog-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #2D3436;
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 22px;
  color: #636E72;
  cursor: pointer;
  padding: 4px;
  line-height: 1;
}

.close-btn:hover {
  color: #2D3436;
}

.dialog-content {
  padding: 24px;
}

.detail-section,
.process-section {
  margin-bottom: 24px;
}

.detail-section:last-child,
.process-section:last-child {
  margin-bottom: 0;
}

.detail-section h4,
.process-section h4 {
  font-size: 15px;
  font-weight: 600;
  color: #7D9E87;
  margin-bottom: 12px;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.detail-label {
  font-weight: 500;
  color: #636E72;
  min-width: 70px;
  font-size: 14px;
}

.detail-value {
  color: #2D3436;
  flex: 1;
  font-size: 14px;
}

.content-box {
  background: #FAF7F2;
  border-radius: 10px;
  padding: 16px;
  border: 1px solid #E8E4DE;
}

.content-title {
  font-size: 15px;
  font-weight: 600;
  color: #2D3436;
  margin-bottom: 8px;
}

.content-description {
  color: #636E72;
  line-height: 1.6;
  font-size: 14px;
}

.evidence-list {
  display: grid;
  gap: 8px;
}

.evidence-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #FAF7F2;
  border-radius: 10px;
  border: 1px solid #E8E4DE;
}

.evidence-icon {
  color: #6B9AC4;
}

.evidence-name {
  flex: 1;
  color: #2D3436;
  font-size: 14px;
}

.evidence-download {
  padding: 4px 12px;
  border-radius: 6px;
  border: 1px solid #6B9AC4;
  background: #FFFFFF;
  color: #6B9AC4;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.evidence-download:hover {
  background: #E9F3FC;
}

.history-timeline {
  position: relative;
  padding-left: 20px;
}

.history-timeline::before {
  content: '';
  position: absolute;
  left: 8px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #E8E4DE;
}

.history-item {
  position: relative;
  padding-bottom: 18px;
  margin-bottom: 14px;
}

.history-item::before {
  content: '';
  position: absolute;
  left: -12px;
  top: 6px;
  width: 8px;
  height: 8px;
  background: #7D9E87;
  border-radius: 50%;
  border: 2px solid #FFFFFF;
  box-shadow: 0 0 0 2px #7D9E87;
}

.history-item:last-child {
  margin-bottom: 0;
}

.history-time {
  font-size: 12px;
  color: #636E72;
  margin-bottom: 4px;
}

.history-content {
  background: #FAF7F2;
  border-radius: 10px;
  padding: 12px;
  border: 1px solid #E8E4DE;
}

.history-action {
  font-weight: 600;
  color: #7D9E87;
  margin-bottom: 4px;
  font-size: 14px;
}

.history-operator {
  font-size: 13px;
  color: #636E72;
  margin-bottom: 4px;
}

.history-note {
  font-size: 13px;
  color: #2D3436;
  line-height: 1.5;
}

.result-options {
  display: grid;
  gap: 10px;
}

.result-option {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border: 1px solid #E8E4DE;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.result-option:hover {
  background: #FAF7F2;
  border-color: #6B9AC4;
}

.result-option input {
  margin: 0;
}

.process-textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #E8E4DE;
  border-radius: 10px;
  font-size: 14px;
  font-family: inherit;
  resize: vertical;
  min-height: 120px;
  outline: none;
  transition: all 0.2s ease;
  background: #FAF7F2;
}

.process-textarea:focus {
  border-color: #6B9AC4;
  box-shadow: 0 0 0 3px rgba(107, 154, 196, 0.15);
}

.measures-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.measure-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border: 1px solid #E8E4DE;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
}

.measure-item:hover {
  background: #FAF7F2;
}

.measure-item input {
  margin: 0;
}

.dialog-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  padding: 16px 24px;
  border-top: 1px solid #E8E4DE;
  background: #FAF7F2;
}

.dialog-btn {
  padding: 10px 24px;
  border-radius: 10px;
  border: 1px solid #E8E4DE;
  background: #FFFFFF;
  color: #636E72;
  font-weight: 500;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.dialog-btn:hover {
  background: #E8E4DE;
}

.dialog-btn.primary {
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
  color: #FFFFFF;
  border-color: transparent;
}

.dialog-btn.primary:hover {
  box-shadow: 0 4px 16px rgba(107, 154, 196, 0.3);
}

.dialog-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (max-width: 1024px) {
  .stats-overview {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .items-grid {
    grid-template-columns: 1fr;
  }
  
  .detail-grid {
    grid-template-columns: 1fr;
  }
  
  .measures-list {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .stats-overview {
    grid-template-columns: 1fr;
  }
  
  .search-section {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-input-group {
    min-width: auto;
  }
  
  .filter-tabs {
    justify-content: center;
  }
  
  .list-header {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .list-actions {
    justify-content: center;
  }
}
</style> 