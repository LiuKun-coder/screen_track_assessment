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

// 筛选选项
const filterTabs = [
  { key: 'all', label: '全部' },
  { key: 'pending', label: '待处理' },
  { key: 'processing', label: '处理中' },
  { key: 'completed', label: '已完成' },
  { key: 'appeal', label: '申诉' }
]

// 模拟数据
const allItems = ref([
  {
    id: 1,
    type: 'appeal',
    number: 'AP20240601001',
    title: '对超速违规处理结果的申诉',
    submitter: '张三',
    contact: '138****5678',
    submitTime: '2024-06-01 10:30',
    deadline: '2024-06-04 17:00',
    status: 'pending',
    priority: 'high',
    description: '认为超速检测设备存在故障，申请重新核实处理结果...',
    fullDescription: '我于2024年5月30日在东门附近被检测为超速违规，但我认为当时的检测设备可能存在故障。根据我的行车记录，当时车速并未超过限速标准。希望能够重新核实相关证据，并重新评估处理结果。',
    evidence: [
      { name: '行车记录视频.mp4', size: '25MB' },
      { name: 'GPS轨迹截图.jpg', size: '2MB' }
    ],
    processHistory: [
      {
        time: '2024-06-01 10:30',
        action: '申诉提交',
        operator: '系统',
        note: '用户提交申诉申请'
      }
    ]
  },
  {
    id: 3,
    type: 'appeal',
    number: 'AP20240602001',
    title: '申诉违停处理结果',
    submitter: '王五',
    contact: '137****9876',
    submitTime: '2024-06-02 09:15',
    deadline: '2024-06-05 17:00',
    status: 'completed',
    priority: 'low',
    description: '因紧急情况临时停车，申请撤销违停处理...',
    fullDescription: '我于2024年5月31日因家人突发疾病需要紧急送医，在南门附近临时停车约10分钟。由于情况紧急，未能及时寻找合适的停车位。希望能够考虑特殊情况，撤销相关处理决定。',
    evidence: [
      { name: '医院急诊记录.pdf', size: '1.2MB' },
      { name: '紧急情况说明.docx', size: '500KB' }
    ],
    processHistory: [
      {
        time: '2024-06-02 09:15',
        action: '申诉提交',
        operator: '系统',
        note: '用户提交申诉申请'
      },
      {
        time: '2024-06-02 11:30',
        action: '开始处理',
        operator: '管理员张三',
        note: '开始审核申诉材料'
      },
      {
        time: '2024-06-02 15:45',
        action: '处理完成',
        operator: '管理员张三',
        note: '经核实，确属紧急情况，申诉成立，撤销原处理决定'
      }
    ]
  },
  {
    id: 5,
    type: 'appeal',
    number: 'AP20240603001',
    title: '申诉身份识别错误',
    submitter: '孙七',
    contact: '135****8765',
    submitTime: '2024-06-03 11:45',
    deadline: '2024-06-06 17:00',
    status: 'processing',
    priority: 'high',
    description: '系统错误识别违规人员身份，申请更正...',
    fullDescription: '我收到一份违规通知，但经过核实，当时我并不在现场。可能是系统在识别过程中出现了错误，将其他人的违规行为误认为是我的。希望能够重新核实相关证据。',
    evidence: [
      { name: '不在场证明.pdf', size: '800KB' },
      { name: '监控截图对比.jpg', size: '3.5MB' }
    ],
    processHistory: [
      {
        time: '2024-06-03 11:45',
        action: '申诉提交',
        operator: '系统',
        note: '用户提交申诉申请'
      },
      {
        time: '2024-06-03 14:20',
        action: '开始处理',
        operator: '管理员李四',
        note: '开始核实身份识别情况'
      }
    ]
  },
  {
    id: 7,
    type: 'appeal',
    number: 'AP20240604001',
    title: '申诉设备故障误判',
    submitter: '李明',
    contact: '139****2468',
    submitTime: '2024-06-04 14:20',
    deadline: '2024-06-07 17:00',
    status: 'pending',
    priority: 'medium',
    description: '认为监控设备故障导致误判，申请重新审核...',
    fullDescription: '我于2024年6月3日收到违规通知，但当时我严格按照规定行驶，没有任何违规行为。怀疑是监控设备出现故障导致的误判，希望能够重新审核相关证据。',
    evidence: [
      { name: '当时现场照片.jpg', size: '3.2MB' },
      { name: '行驶路线说明.pdf', size: '1.5MB' }
    ],
    processHistory: [
      {
        time: '2024-06-04 14:20',
        action: '申诉提交',
        operator: '系统',
        note: '用户提交申诉申请'
      }
    ]
  },
  {
    id: 8,
    type: 'appeal',
    number: 'AP20240605001',
    title: '申诉处理程序不当',
    submitter: '陈华',
    contact: '138****1357',
    submitTime: '2024-06-05 16:30',
    deadline: '2024-06-08 17:00',
    status: 'processing',
    priority: 'low',
    description: '认为处理程序不符合规定，申请重新处理...',
    fullDescription: '我对之前的违规处理程序有异议，认为相关部门在处理过程中没有按照规定程序进行，希望能够重新按照正确程序处理此事。',
    evidence: [
      { name: '处理程序说明.docx', size: '800KB' },
      { name: '相关规定截图.jpg', size: '2.1MB' }
    ],
    processHistory: [
      {
        time: '2024-06-05 16:30',
        action: '申诉提交',
        operator: '系统',
        note: '用户提交申诉申请'
      },
      {
        time: '2024-06-05 17:45',
        action: '开始处理',
        operator: '管理员赵六',
        note: '开始审核处理程序'
      }
    ]
  }
])

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

const paginatedItems = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredItems.value.slice(start, start + pageSize)
})

const totalPages = computed(() => {
  return Math.ceil(filteredItems.value.length / pageSize)
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

const totalCount = computed(() => allItems.value.length)

// 方法
function setFilter(key) {
  currentFilter.value = key
  currentPage.value = 1
  selectedItems.value = []
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

function submitProcess() {
  // 更新项目状态
  const item = allItems.value.find(i => i.id === currentItem.value.id)
  if (item) {
    item.status = 'completed'
    
    // 添加处理历史
    const now = new Date()
    const timeStr = now.toLocaleString('zh-CN')
    item.processHistory.push({
      time: timeStr,
      action: '处理完成',
      operator: '当前管理员',
      note: `处理结果：${processResult.value === 'approved' ? '成立' : processResult.value === 'rejected' ? '不成立' : '部分成立'}。${processNote.value}`
    })
  }
  
  closeProcessDialog()
  closeDetailDialog()
  
  // 显示成功提示
  alert('处理完成！')
}

function batchProcess() {
  if (selectedItems.value.length === 0) return
  
  const confirmMsg = `确定要批量处理选中的 ${selectedItems.value.length} 个项目吗？`
  if (confirm(confirmMsg)) {
    // 批量处理逻辑
    selectedItems.value.forEach(id => {
      const item = allItems.value.find(i => i.id === id)
      if (item && item.status === 'pending') {
        item.status = 'processing'
        
        // 添加处理历史
        const now = new Date()
        const timeStr = now.toLocaleString('zh-CN')
        item.processHistory.push({
          time: timeStr,
          action: '开始处理',
          operator: '当前管理员',
          note: '批量处理开始'
        })
      }
    })
    
    selectedItems.value = []
    alert('批量处理已开始！')
  }
}

function refreshList() {
  // 刷新列表
  selectedItems.value = []
  currentPage.value = 1
  alert('列表已刷新！')
}

// 生命周期
onMounted(() => {
  // 初始化数据
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
  font-size: 32px;
  font-weight: bold;
  color: #4f8cff;
  margin-bottom: 12px;
}

.handler-subtitle {
  font-size: 18px;
  color: #666;
  margin: 0;
}

/* 统计概览 */
.stats-overview {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: linear-gradient(135deg, #fff 0%, #f8fafc 100%);
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 16px rgba(79, 140, 255, 0.1);
  border: 1px solid #e0e7ff;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(79, 140, 255, 0.2);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-icon.pending {
  background: linear-gradient(135deg, #ff9500 0%, #ff6b35 100%);
}

.stat-icon.processing {
  background: linear-gradient(135deg, #4f8cff 0%, #7c3aed 100%);
}

.stat-icon.completed {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.stat-icon.total {
  background: linear-gradient(135deg, #6b7280 0%, #4b5563 100%);
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

/* 筛选和搜索 */
.filter-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 30px;
  box-shadow: 0 4px 16px rgba(79, 140, 255, 0.1);
  border: 1px solid #e0e7ff;
}

.filter-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.filter-tab {
  padding: 8px 16px;
  border-radius: 8px;
  border: 2px solid #e0e7ff;
  background: white;
  color: #4f8cff;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.filter-tab:hover {
  background: #f0f5ff;
  border-color: #4f8cff;
}

.filter-tab.active {
  background: linear-gradient(90deg, #4f8cff 0%, #7c3aed 100%);
  color: white;
  border-color: #7c3aed;
}

.search-section {
  display: flex;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
}

.search-input-group {
  display: flex;
  flex: 1;
  min-width: 300px;
}

.search-input {
  flex: 1;
  padding: 12px 16px;
  border: 2px solid #e0e7ff;
  border-radius: 8px 0 0 8px;
  font-size: 16px;
  outline: none;
  transition: border-color 0.3s;
}

.search-input:focus {
  border-color: #4f8cff;
}

.search-btn {
  padding: 12px 16px;
  background: linear-gradient(90deg, #4f8cff 0%, #7c3aed 100%);
  color: white;
  border: none;
  border-radius: 0 8px 8px 0;
  cursor: pointer;
  transition: all 0.3s;
}

.search-btn:hover {
  background: linear-gradient(90deg, #7c3aed 0%, #4f8cff 100%);
}

.priority-filter {
  min-width: 150px;
}

.priority-select {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e0e7ff;
  border-radius: 8px;
  font-size: 16px;
  outline: none;
  transition: border-color 0.3s;
}

.priority-select:focus {
  border-color: #4f8cff;
}

/* 处理列表 */
.handler-list {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(79, 140, 255, 0.1);
  border: 1px solid #e0e7ff;
  overflow: hidden;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: linear-gradient(90deg, #f8fafc 0%, #f1f5f9 100%);
  border-bottom: 1px solid #e0e7ff;
}

.list-title {
  font-size: 20px;
  font-weight: bold;
  color: #4f8cff;
}

.list-actions {
  display: flex;
  gap: 12px;
}

.batch-btn,
.refresh-btn {
  padding: 8px 16px;
  border-radius: 8px;
  border: 2px solid #4f8cff;
  background: white;
  color: #4f8cff;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.batch-btn:hover,
.refresh-btn:hover {
  background: #f0f5ff;
}

.batch-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.refresh-btn {
  padding: 8px 12px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

.empty-icon {
  color: #ccc;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 18px;
}

/* 项目网格 */
.items-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
  padding: 24px;
}

.item-card {
  background: linear-gradient(135deg, #fff 0%, #f8fafc 100%);
  border-radius: 12px;
  border: 2px solid #e0e7ff;
  padding: 20px;
  transition: all 0.3s;
  position: relative;
}

.item-card:hover {
  border-color: #4f8cff;
  box-shadow: 0 4px 16px rgba(79, 140, 255, 0.2);
  transform: translateY(-2px);
}

.item-card.selected {
  border-color: #7c3aed;
  background: linear-gradient(135deg, #f0f5ff 0%, #f8fafc 100%);
}

.item-card.urgent {
  border-left: 4px solid #ff6b35;
}

.item-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.item-checkbox {
  display: flex;
  align-items: center;
}

.checkbox-input {
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.item-type {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
  color: white;
}

.item-type.appeal {
  background: linear-gradient(90deg, #4f8cff 0%, #7c3aed 100%);
}

.item-priority {
  padding: 4px 8px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: bold;
}

.item-priority.high {
  background: #fee2e2;
  color: #dc2626;
}

.item-priority.medium {
  background: #fef3c7;
  color: #d97706;
}

.item-priority.low {
  background: #dcfce7;
  color: #16a34a;
}

.item-status {
  padding: 4px 8px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: bold;
  margin-left: auto;
}

.item-status.pending {
  background: #fef3c7;
  color: #d97706;
}

.item-status.processing {
  background: #dbeafe;
  color: #2563eb;
}

.item-status.completed {
  background: #dcfce7;
  color: #16a34a;
}

.item-content {
  margin-bottom: 16px;
}

.item-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 12px;
}

.item-info {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
  margin-bottom: 12px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
  min-width: 60px;
}

.info-value {
  font-size: 14px;
  color: #333;
  flex: 1;
}

.info-value.overdue {
  color: #dc2626;
  font-weight: bold;
}

.item-description {
  font-size: 14px;
  color: #666;
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
  border-radius: 6px;
  border: none;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.action-btn.view {
  background: #f3f4f6;
  color: #4b5563;
}

.action-btn.view:hover {
  background: #e5e7eb;
}

.action-btn.process {
  background: linear-gradient(90deg, #4f8cff 0%, #7c3aed 100%);
  color: white;
}

.action-btn.process:hover {
  background: linear-gradient(90deg, #7c3aed 0%, #4f8cff 100%);
}

.action-btn.complete {
  background: linear-gradient(90deg, #10b981 0%, #059669 100%);
  color: white;
}

.action-btn.complete:hover {
  background: linear-gradient(90deg, #059669 0%, #10b981 100%);
}

.action-btn.review {
  background: linear-gradient(90deg, #6b7280 0%, #4b5563 100%);
  color: white;
}

.action-btn.review:hover {
  background: linear-gradient(90deg, #4b5563 0%, #6b7280 100%);
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  padding: 20px;
  background: #f8fafc;
  border-top: 1px solid #e0e7ff;
}

.page-btn {
  padding: 8px 16px;
  border-radius: 8px;
  border: 2px solid #e0e7ff;
  background: white;
  color: #4f8cff;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.page-btn:hover:not(:disabled) {
  background: #f0f5ff;
  border-color: #4f8cff;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #666;
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
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.detail-dialog,
.process-dialog {
  background: white;
  border-radius: 16px;
  max-width: 800px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e0e7ff;
  background: linear-gradient(90deg, #f8fafc 0%, #f1f5f9 100%);
}

.dialog-header h3 {
  font-size: 20px;
  font-weight: bold;
  color: #4f8cff;
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #666;
  cursor: pointer;
  padding: 4px;
  line-height: 1;
}

.close-btn:hover {
  color: #333;
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
  font-size: 16px;
  font-weight: bold;
  color: #4f8cff;
  margin-bottom: 12px;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.detail-label {
  font-weight: 500;
  color: #666;
  min-width: 80px;
}

.detail-value {
  color: #333;
  flex: 1;
}

.content-box {
  background: #f8fafc;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e0e7ff;
}

.content-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.content-description {
  color: #666;
  line-height: 1.6;
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
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e0e7ff;
}

.evidence-icon {
  color: #4f8cff;
}

.evidence-name {
  flex: 1;
  color: #333;
}

.evidence-download {
  padding: 4px 12px;
  border-radius: 4px;
  border: 1px solid #4f8cff;
  background: white;
  color: #4f8cff;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.evidence-download:hover {
  background: #f0f5ff;
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
  background: #e0e7ff;
}

.history-item {
  position: relative;
  padding-bottom: 20px;
  margin-bottom: 16px;
}

.history-item::before {
  content: '';
  position: absolute;
  left: -12px;
  top: 6px;
  width: 8px;
  height: 8px;
  background: #4f8cff;
  border-radius: 50%;
  border: 2px solid white;
  box-shadow: 0 0 0 2px #4f8cff;
}

.history-item:last-child {
  margin-bottom: 0;
}

.history-time {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.history-content {
  background: #f8fafc;
  border-radius: 8px;
  padding: 12px;
  border: 1px solid #e0e7ff;
}

.history-action {
  font-weight: bold;
  color: #4f8cff;
  margin-bottom: 4px;
}

.history-operator {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
}

.history-note {
  font-size: 14px;
  color: #333;
  line-height: 1.5;
}

.result-options {
  display: grid;
  gap: 12px;
}

.result-option {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border: 2px solid #e0e7ff;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.result-option:hover {
  background: #f8fafc;
  border-color: #4f8cff;
}

.result-option input {
  margin: 0;
}

.process-textarea {
  width: 100%;
  padding: 12px;
  border: 2px solid #e0e7ff;
  border-radius: 8px;
  font-size: 14px;
  font-family: inherit;
  resize: vertical;
  min-height: 120px;
  outline: none;
  transition: border-color 0.3s;
}

.process-textarea:focus {
  border-color: #4f8cff;
}

.measures-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.measure-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border: 1px solid #e0e7ff;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.measure-item:hover {
  background: #f8fafc;
}

.measure-item input {
  margin: 0;
}

.dialog-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e0e7ff;
  background: #f8fafc;
}

.dialog-btn {
  padding: 10px 24px;
  border-radius: 8px;
  border: 2px solid #e0e7ff;
  background: white;
  color: #666;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.dialog-btn:hover {
  background: #f3f4f6;
}

.dialog-btn.primary {
  background: linear-gradient(90deg, #4f8cff 0%, #7c3aed 100%);
  color: white;
  border-color: #4f8cff;
}

.dialog-btn.primary:hover {
  background: linear-gradient(90deg, #7c3aed 0%, #4f8cff 100%);
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