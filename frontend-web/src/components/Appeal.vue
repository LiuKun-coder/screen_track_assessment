<template>
  <div class="appeal-container">
    <div class="appeal-header">
      <h2 class="appeal-title">我要申诉</h2>
      <p class="appeal-subtitle">对违规处理结果有异议？请提交申诉申请，我们将认真审核处理</p>
    </div>

    <!-- 申诉步骤指示器 -->
    <div class="appeal-steps">
      <div class="step-item" :class="{ active: currentStep >= 1, completed: currentStep > 1 }">
        <div class="step-number">1</div>
        <div class="step-label">选择违规记录</div>
      </div>
      <div class="step-line" :class="{ active: currentStep > 1 }"></div>
      <div class="step-item" :class="{ active: currentStep >= 2, completed: currentStep > 2 }">
        <div class="step-number">2</div>
        <div class="step-label">填写申诉信息</div>
      </div>
      <div class="step-line" :class="{ active: currentStep > 2 }"></div>
      <div class="step-item" :class="{ active: currentStep >= 3 }">
        <div class="step-number">3</div>
        <div class="step-label">提交申诉</div>
      </div>
    </div>

    <!-- 步骤1：选择违规记录 -->
    <div v-if="currentStep === 1" class="appeal-step-content">
      <div class="step-title">请选择要申诉的违规记录</div>
      <div class="violation-list">
        <div 
          v-for="violation in violationList" 
          :key="violation.id"
          class="violation-item"
          :class="{ selected: selectedViolation?.id === violation.id }"
          @click="selectViolation(violation)"
        >
          <div class="violation-info">
            <div class="violation-row">
              <span class="violation-label">违规时间</span>
              <span class="violation-value">{{ violation.time }}</span>
            </div>
            <div class="violation-row">
              <span class="violation-label">违规地点</span>
              <span class="violation-value">{{ violation.place }}</span>
            </div>
            <div class="violation-row">
              <span class="violation-label">违规类型</span>
              <span class="violation-value">{{ violation.type }}</span>
            </div>
            <div class="violation-row">
              <span class="violation-label">处理结果</span>
              <span class="violation-value penalty">{{ violation.penalty }}</span>
            </div>
          </div>
          <div class="selection-indicator" v-if="selectedViolation?.id === violation.id">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
              <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
            </svg>
          </div>
        </div>
      </div>
      <div class="step-actions">
        <button class="next-btn" @click="nextStep" :disabled="!selectedViolation">下一步</button>
      </div>
    </div>

    <!-- 步骤2：填写申诉信息 -->
    <div v-if="currentStep === 2" class="appeal-step-content">
      <div class="step-title">填写申诉信息</div>
      
      <!-- 违规记录摘要 -->
      <div class="selected-violation-summary" v-if="selectedViolation">
        <h4>申诉违规记录</h4>
        <div class="summary-content">
          <span>{{ selectedViolation.time }} - {{ selectedViolation.place }} - {{ selectedViolation.type }}</span>
          <span class="penalty-text">处理：{{ selectedViolation.penalty }}</span>
        </div>
      </div>

      <!-- 申诉理由选择 -->
      <div class="appeal-reason-section">
        <h4>申诉理由</h4>
        <div class="reason-options">
          <div 
            v-for="reason in appealReasons" 
            :key="reason.value"
            class="reason-option"
            :class="{ selected: selectedReason === reason.value }"
            @click="selectedReason = reason.value"
          >
            <div class="reason-radio">
              <div class="radio-dot" v-if="selectedReason === reason.value"></div>
            </div>
            <div class="reason-content">
              <div class="reason-title">{{ reason.label }}</div>
              <div class="reason-desc">{{ reason.description }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 详细说明 -->
      <div class="appeal-description-section">
        <h4>详细说明</h4>
        <textarea 
          v-model="appealDescription"
          class="appeal-textarea"
          placeholder="请详细说明您的申诉理由，包括具体情况、时间、地点等信息..."
          rows="6"
        ></textarea>
        <div class="char-count">{{ appealDescription.length }}/500</div>
      </div>

      <!-- 证据上传 -->
      <div class="evidence-section">
        <h4>证据材料（可选）</h4>
        <div class="evidence-upload">
          <div class="upload-area" @click="triggerFileUpload">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="currentColor">
              <path d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20Z"/>
            </svg>
            <p>点击上传证据文件</p>
            <p class="upload-hint">支持图片、视频、文档等格式，单个文件不超过10MB</p>
          </div>
          <input 
            ref="fileInput" 
            type="file" 
            multiple 
            accept="image/*,video/*,.pdf,.doc,.docx"
            @change="handleFileUpload"
            style="display: none"
          >
        </div>
        <div class="uploaded-files" v-if="uploadedFiles.length > 0">
          <div v-for="(file, index) in uploadedFiles" :key="index" class="file-item">
            <div class="file-info">
              <span class="file-name">{{ file.name }}</span>
              <span class="file-size">{{ formatFileSize(file.size) }}</span>
            </div>
            <button class="remove-file-btn" @click="removeFile(index)">×</button>
          </div>
        </div>
      </div>

      <!-- 联系方式 -->
      <div class="contact-section">
        <h4>联系方式</h4>
        <div class="contact-inputs">
          <div class="input-group">
            <label>手机号码</label>
            <input 
              v-model="contactPhone" 
              type="tel" 
              placeholder="请输入您的手机号码"
              class="contact-input"
            >
          </div>
          <div class="input-group">
            <label>邮箱地址</label>
            <input 
              v-model="contactEmail" 
              type="email" 
              placeholder="请输入您的邮箱地址"
              class="contact-input"
            >
          </div>
        </div>
      </div>

      <div class="step-actions">
        <button class="prev-btn" @click="prevStep">上一步</button>
        <button class="next-btn" @click="nextStep" :disabled="!canProceedToStep3">下一步</button>
      </div>
    </div>

    <!-- 步骤3：提交申诉 -->
    <div v-if="currentStep === 3" class="appeal-step-content">
      <div class="step-title">确认申诉信息</div>
      
      <div class="appeal-summary">
        <div class="summary-section">
          <h4>违规记录</h4>
          <div class="summary-item">
            <span>{{ selectedViolation.time }} - {{ selectedViolation.place }}</span>
            <span>{{ selectedViolation.type }} - {{ selectedViolation.penalty }}</span>
          </div>
        </div>

        <div class="summary-section">
          <h4>申诉理由</h4>
          <div class="summary-item">
            {{ getReasonLabel(selectedReason) }}
          </div>
        </div>

        <div class="summary-section">
          <h4>详细说明</h4>
          <div class="summary-item description">
            {{ appealDescription }}
          </div>
        </div>

        <div class="summary-section" v-if="uploadedFiles.length > 0">
          <h4>证据材料</h4>
          <div class="summary-item">
            <span v-for="(file, index) in uploadedFiles" :key="index" class="file-tag">
              {{ file.name }}
            </span>
          </div>
        </div>

        <div class="summary-section">
          <h4>联系方式</h4>
          <div class="summary-item">
            <span>手机：{{ contactPhone }}</span>
            <span>邮箱：{{ contactEmail }}</span>
          </div>
        </div>
      </div>

      <div class="agreement-section">
        <label class="agreement-checkbox">
          <input type="checkbox" v-model="agreeToTerms">
          <span class="checkmark"></span>
          我确认以上信息真实有效，并同意相关处理条款
        </label>
      </div>

      <div class="step-actions">
        <button class="prev-btn" @click="prevStep">上一步</button>
        <button class="submit-btn" @click="submitAppeal" :disabled="!agreeToTerms">提交申诉</button>
      </div>
    </div>

    <!-- 提交成功对话框 -->
    <div v-if="showSuccessDialog" class="success-dialog-mask">
      <div class="success-dialog">
        <div class="success-icon">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
          </svg>
        </div>
        <div class="success-title">申诉提交成功！</div>
        <div class="success-message">
          <p>您的申诉申请已成功提交，申诉编号：<strong>{{ appealNumber }}</strong></p>
          <p>我们将在3-5个工作日内完成审核，请保持手机畅通。</p>
          <p>您可以通过申诉编号查询处理进度。</p>
        </div>
        <div class="success-actions">
          <button class="success-btn" @click="resetForm">继续申诉</button>
          <button class="success-btn primary" @click="closeSuccessDialog">确定</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// 响应式数据
const currentStep = ref(1)
const selectedViolation = ref(null)
const selectedReason = ref('')
const appealDescription = ref('')
const uploadedFiles = ref([])
const contactPhone = ref('')
const contactEmail = ref('')
const agreeToTerms = ref(false)
const showSuccessDialog = ref(false)
const appealNumber = ref('')

// 文件上传引用
const fileInput = ref(null)

// 违规记录数据
const violationList = ref([
  { 
    id: 1, 
    time: '2024-06-01 10:00', 
    place: '东门', 
    type: '超速', 
    penalty: '警告处理',
    status: '可申诉'
  },
  { 
    id: 2, 
    time: '2024-06-02 11:30', 
    place: '西门', 
    type: '违停', 
    penalty: '通报批评',
    status: '可申诉'
  },
  { 
    id: 3, 
    time: '2024-06-03 09:20', 
    place: '南门', 
    type: '超速', 
    penalty: '警告处理',
    status: '可申诉'
  }
])

// 申诉理由选项
const appealReasons = [
  {
    value: 'wrong_identification',
    label: '身份识别错误',
    description: '系统错误识别了违规人员身份'
  },
  {
    value: 'equipment_malfunction',
    label: '设备故障',
    description: '监控设备或检测设备存在故障'
  },
  {
    value: 'emergency_situation',
    label: '紧急情况',
    description: '因紧急情况不得已产生的违规行为'
  },
  {
    value: 'rule_misunderstanding',
    label: '规则理解偏差',
    description: '对相关规定理解有误导致的违规'
  },
  {
    value: 'other',
    label: '其他原因',
    description: '以上原因都不符合的其他情况'
  }
]

// 计算属性
const canProceedToStep3 = computed(() => {
  return selectedReason.value && 
         appealDescription.value.trim() && 
         contactPhone.value.trim() && 
         contactEmail.value.trim()
})

// 方法
function selectViolation(violation) {
  selectedViolation.value = violation
}

function nextStep() {
  if (currentStep.value < 3) {
    currentStep.value++
  }
}

function prevStep() {
  if (currentStep.value > 1) {
    currentStep.value--
  }
}

function triggerFileUpload() {
  fileInput.value.click()
}

function handleFileUpload(event) {
  const files = Array.from(event.target.files)
  uploadedFiles.value.push(...files)
}

function removeFile(index) {
  uploadedFiles.value.splice(index, 1)
}

function formatFileSize(bytes) {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

function getReasonLabel(value) {
  const reason = appealReasons.find(r => r.value === value)
  return reason ? reason.label : ''
}

function submitAppeal() {
  // 生成申诉编号
  appealNumber.value = 'AP' + Date.now().toString().slice(-8)
  
  // 显示成功对话框
  showSuccessDialog.value = true
}

function resetForm() {
  currentStep.value = 1
  selectedViolation.value = null
  selectedReason.value = ''
  appealDescription.value = ''
  uploadedFiles.value = []
  contactPhone.value = ''
  contactEmail.value = ''
  agreeToTerms.value = false
  showSuccessDialog.value = false
}

function closeSuccessDialog() {
  showSuccessDialog.value = false
  resetForm()
}
</script>

<style scoped>
.appeal-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.appeal-header {
  text-align: center;
  margin-bottom: 40px;
}

.appeal-title {
  font-size: 32px;
  font-weight: bold;
  color: #4f8cff;
  margin-bottom: 12px;
}

.appeal-subtitle {
  font-size: 18px;
  color: #666;
  margin: 0;
}

/* 步骤指示器 */
.appeal-steps {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 40px;
  padding: 0 20px;
}

.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.step-number {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #e0e7ff;
  color: #666;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 16px;
  transition: all 0.3s;
}

.step-item.active .step-number {
  background: #4f8cff;
  color: white;
}

.step-item.completed .step-number {
  background: #7c3aed;
  color: white;
}

.step-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.step-item.active .step-label {
  color: #4f8cff;
  font-weight: bold;
}

.step-line {
  width: 80px;
  height: 2px;
  background: #e0e7ff;
  margin: 0 20px;
  transition: all 0.3s;
}

.step-line.active {
  background: #4f8cff;
}

/* 步骤内容 */
.appeal-step-content {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 4px 24px rgba(79, 140, 255, 0.1);
  border: 1px solid #e0e7ff;
}

.step-title {
  font-size: 24px;
  font-weight: bold;
  color: #4f8cff;
  margin-bottom: 30px;
  text-align: center;
}

/* 违规记录列表 */
.violation-list {
  display: grid;
  gap: 20px;
  margin-bottom: 30px;
}

.violation-item {
  background: linear-gradient(90deg, #f8fafc 0%, #fff 100%);
  border: 2px solid #e0e7ff;
  border-radius: 16px;
  padding: 24px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.violation-item:hover {
  border-color: #4f8cff;
  box-shadow: 0 4px 16px rgba(79, 140, 255, 0.2);
}

.violation-item.selected {
  border-color: #7c3aed;
  background: linear-gradient(90deg, #f0f5ff 0%, #fff 100%);
  box-shadow: 0 4px 24px rgba(124, 58, 237, 0.2);
}

.violation-info {
  flex: 1;
}

.violation-row {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
}

.violation-row:last-child {
  margin-bottom: 0;
}

.violation-label {
  background: linear-gradient(90deg, #7c3aed 0%, #4f8cff 100%);
  color: white;
  padding: 6px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  min-width: 80px;
  text-align: center;
}

.violation-value {
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.violation-value.penalty {
  color: #ff6b6b;
  font-weight: bold;
}

.selection-indicator {
  color: #7c3aed;
  font-size: 24px;
}

/* 申诉理由选择 */
.appeal-reason-section,
.appeal-description-section,
.evidence-section,
.contact-section {
  margin-bottom: 30px;
}

.appeal-reason-section h4,
.appeal-description-section h4,
.evidence-section h4,
.contact-section h4 {
  font-size: 18px;
  font-weight: bold;
  color: #4f8cff;
  margin-bottom: 16px;
}

.reason-options {
  display: grid;
  gap: 12px;
}

.reason-option {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  border: 2px solid #e0e7ff;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.reason-option:hover {
  border-color: #4f8cff;
  background: #f8fafc;
}

.reason-option.selected {
  border-color: #7c3aed;
  background: #f0f5ff;
}

.reason-radio {
  width: 20px;
  height: 20px;
  border: 2px solid #ccc;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 2px;
}

.reason-option.selected .reason-radio {
  border-color: #7c3aed;
}

.radio-dot {
  width: 10px;
  height: 10px;
  background: #7c3aed;
  border-radius: 50%;
}

.reason-content {
  flex: 1;
}

.reason-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.reason-desc {
  font-size: 14px;
  color: #666;
}

/* 文本域 */
.appeal-textarea {
  width: 100%;
  padding: 16px;
  border: 2px solid #e0e7ff;
  border-radius: 12px;
  font-size: 16px;
  font-family: inherit;
  resize: vertical;
  min-height: 120px;
  transition: border-color 0.3s;
}

.appeal-textarea:focus {
  outline: none;
  border-color: #4f8cff;
}

.char-count {
  text-align: right;
  font-size: 14px;
  color: #666;
  margin-top: 8px;
}

/* 文件上传 */
.upload-area {
  border: 2px dashed #e0e7ff;
  border-radius: 12px;
  padding: 40px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.upload-area:hover {
  border-color: #4f8cff;
  background: #f8fafc;
}

.upload-area svg {
  color: #4f8cff;
  margin-bottom: 16px;
}

.upload-area p {
  margin: 8px 0;
  color: #333;
  font-weight: 500;
}

.upload-hint {
  font-size: 14px;
  color: #666;
}

.uploaded-files {
  margin-top: 16px;
}

.file-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 8px;
  margin-bottom: 8px;
}

.file-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.file-name {
  font-weight: 500;
  color: #333;
}

.file-size {
  font-size: 14px;
  color: #666;
}

.remove-file-btn {
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
}

/* 联系方式 */
.contact-inputs {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.input-group label {
  font-weight: 500;
  color: #333;
}

.contact-input {
  padding: 12px 16px;
  border: 2px solid #e0e7ff;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s;
}

.contact-input:focus {
  outline: none;
  border-color: #4f8cff;
}

/* 违规记录摘要 */
.selected-violation-summary {
  background: #f0f5ff;
  border: 1px solid #e0e7ff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 30px;
}

.selected-violation-summary h4 {
  font-size: 16px;
  color: #4f8cff;
  margin-bottom: 12px;
}

.summary-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.penalty-text {
  color: #ff6b6b;
  font-weight: bold;
}

/* 申诉摘要 */
.appeal-summary {
  background: #f8fafc;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 30px;
}

.summary-section {
  margin-bottom: 24px;
}

.summary-section:last-child {
  margin-bottom: 0;
}

.summary-section h4 {
  font-size: 16px;
  color: #4f8cff;
  margin-bottom: 12px;
}

.summary-item {
  background: white;
  padding: 16px;
  border-radius: 8px;
  border: 1px solid #e0e7ff;
}

.summary-item.description {
  white-space: pre-wrap;
  line-height: 1.6;
}

.file-tag {
  display: inline-block;
  background: #e0e7ff;
  color: #4f8cff;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 14px;
  margin-right: 8px;
  margin-bottom: 8px;
}

/* 协议复选框 */
.agreement-section {
  margin-bottom: 30px;
}

.agreement-checkbox {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  font-size: 16px;
  color: #333;
}

.agreement-checkbox input {
  display: none;
}

.checkmark {
  width: 20px;
  height: 20px;
  border: 2px solid #e0e7ff;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.agreement-checkbox input:checked + .checkmark {
  background: #4f8cff;
  border-color: #4f8cff;
}

.agreement-checkbox input:checked + .checkmark::after {
  content: '✓';
  color: white;
  font-weight: bold;
}

/* 按钮 */
.step-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
}

.prev-btn,
.next-btn,
.submit-btn {
  padding: 12px 32px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
}

.prev-btn {
  background: white;
  color: #4f8cff;
  border: 2px solid #4f8cff;
}

.prev-btn:hover {
  background: #f0f5ff;
}

.next-btn,
.submit-btn {
  background: linear-gradient(90deg, #4f8cff 0%, #7c3aed 100%);
  color: white;
}

.next-btn:hover,
.submit-btn:hover {
  background: linear-gradient(90deg, #7c3aed 0%, #4f8cff 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(79, 140, 255, 0.3);
}

.next-btn:disabled,
.submit-btn:disabled {
  background: #e0e7ff;
  color: #aaa;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 成功对话框 */
.success-dialog-mask {
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
}

.success-dialog {
  background: white;
  border-radius: 16px;
  padding: 40px;
  max-width: 500px;
  width: 90%;
  text-align: center;
}

.success-icon {
  color: #4f8cff;
  margin-bottom: 20px;
}

.success-title {
  font-size: 24px;
  font-weight: bold;
  color: #4f8cff;
  margin-bottom: 20px;
}

.success-message {
  color: #666;
  line-height: 1.6;
  margin-bottom: 30px;
}

.success-message p {
  margin: 12px 0;
}

.success-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.success-btn {
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid #4f8cff;
  background: white;
  color: #4f8cff;
}

.success-btn.primary {
  background: #4f8cff;
  color: white;
}

.success-btn:hover {
  background: #f0f5ff;
}

.success-btn.primary:hover {
  background: #7c3aed;
}

@media (max-width: 768px) {
  .contact-inputs {
    grid-template-columns: 1fr;
  }
  
  .appeal-steps {
    flex-direction: column;
    gap: 20px;
  }
  
  .step-line {
    width: 2px;
    height: 40px;
    margin: 0;
  }
}
</style> 