<template>
  <view class="submit-page">
    <view class="form-card">
      <!-- 违规信息 -->
      <view class="section">
        <text class="section-title">违规信息</text>
        <view class="info-row">
          <text class="label">违规类型</text>
          <text class="value">{{ violation?.type }}</text>
        </view>
        <view class="info-row">
          <text class="label">违规地点</text>
          <text class="value">{{ violation?.place }}</text>
        </view>
        <view class="info-row">
          <text class="label">违规时间</text>
          <text class="value">{{ formatTime(violation?.violationTime) }}</text>
        </view>
      </view>

      <!-- 申诉表单 -->
      <view class="section">
        <text class="section-title">申诉信息</text>
        
        <!-- 申诉原因 -->
        <view class="form-item">
          <text class="form-label">申诉原因 <text class="required">*</text></text>
          <picker :range="reasonOptions" @change="onReasonChange">
            <view class="picker">
              <text :class="{ placeholder: !form.reason }">
                {{ form.reason || '请选择申诉原因' }}
              </text>
              <text class="arrow">›</text>
            </view>
          </picker>
        </view>

        <!-- 详细描述 -->
        <view class="form-item">
          <text class="form-label">详细描述 <text class="required">*</text></text>
          <textarea 
            class="textarea"
            v-model="form.description"
            placeholder="请详细描述您的申诉理由"
            maxlength="500"
            :show-count="true"
          />
        </view>

        <!-- 联系电话 -->
        <view class="form-item">
          <text class="form-label">联系电话 <text class="required">*</text></text>
          <input 
            class="input"
            type="number"
            v-model="form.contactPhone"
            placeholder="请输入您的联系电话"
            maxlength="11"
          />
        </view>

        <!-- 证据上传 -->
        <view class="form-item">
          <text class="form-label">证据文件</text>
          <text class="form-desc">支持上传图片、视频等证据材料，最多3个</text>
          <view class="upload-list">
            <view 
              v-for="(file, index) in evidenceFiles" 
              :key="index"
              class="upload-item"
            >
              <image :src="file" mode="aspectFill" class="upload-image" />
              <view class="delete-btn" @click="removeFile(index)">×</view>
            </view>
            <view 
              v-if="evidenceFiles.length < 3"
              class="upload-btn" 
              @click="chooseFile"
            >
              <text class="upload-icon">+</text>
              <text class="upload-text">上传图片</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 提交按钮 -->
      <view class="submit-section">
        <button 
          class="btn btn-primary"
          :class="{ 'btn-disabled': !canSubmit }"
          :loading="submitting"
          @click="handleSubmit"
        >
          提交申诉
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { formatDate } from '@/utils/date.js'
import violationApi from '@/api/violation.js'
import appealApi from '@/api/appeal.js'
import uploadApi from '@/api/upload.js'

const violationId = ref('')
const violation = ref(null)

const reasonOptions = ['记录错误', '设备故障', '紧急情况', '其他原因']

const form = ref({
  reason: '',
  description: '',
  contactPhone: '',
})

const evidenceFiles = ref([])
const submitting = ref(false)

const canSubmit = computed(() => {
  return form.value.reason && 
         form.value.description && 
         form.value.contactPhone &&
         form.value.contactPhone.length === 11
})

function formatTime(time) {
  return formatDate(time, 'YYYY-MM-DD HH:mm')
}

async function fetchViolation() {
  try {
    const res = await violationApi.getViolationDetail(violationId.value)
    violation.value = res.data
  } catch (error) {
    console.error('获取违规详情失败:', error)
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
}

function onReasonChange(e) {
  form.value.reason = reasonOptions[e.detail.value]
}

async function chooseFile() {
  try {
    const res = await new Promise((resolve, reject) => {
      uni.chooseImage({
        count: 3 - evidenceFiles.value.length,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: resolve,
        fail: reject
      })
    })

    // 上传文件
    for (const filePath of res.tempFilePaths) {
      try {
        const url = await uploadApi.uploadFile(filePath)
        evidenceFiles.value.push(url)
      } catch (error) {
        console.error('上传失败:', error)
        uni.showToast({ title: '上传失败', icon: 'none' })
      }
    }
  } catch (error) {
    console.error('选择文件失败:', error)
  }
}

function removeFile(index) {
  evidenceFiles.value.splice(index, 1)
}

async function handleSubmit() {
  if (!canSubmit.value || submitting.value) return

  submitting.value = true

  try {
    await appealApi.submitAppeal({
      violationId: violationId.value,
      reason: form.value.reason,
      description: form.value.description,
      contactPhone: form.value.contactPhone,
      evidenceFiles: evidenceFiles.value
    })

    uni.showToast({ title: '申诉提交成功', icon: 'success' })
    
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (error) {
    console.error('提交申诉失败:', error)
    uni.showToast({ title: error.message || '提交失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  violationId.value = currentPage.options?.violationId

  if (violationId.value) {
    fetchViolation()
  }
})
</script>

<style lang="scss" scoped>
.submit-page {
  min-height: 100vh;
  background: #F5F5F5;
  padding: 24rpx 32rpx;
}

.form-card {
  background: #FFFFFF;
  border-radius: 20rpx;
  overflow: hidden;
}

.section {
  padding: 32rpx;
  
  & + .section {
    border-top: 1rpx solid #F0F0F0;
  }
}

.section-title {
  display: block;
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
  margin-bottom: 24rpx;
}

.info-row {
  display: flex;
  margin-bottom: 16rpx;
  
  .label {
    width: 160rpx;
    font-size: 28rpx;
    color: #999999;
    flex-shrink: 0;
  }
  
  .value {
    flex: 1;
    font-size: 28rpx;
    color: #333333;
  }
}

.form-item {
  margin-bottom: 24rpx;
  
  &:last-child {
    margin-bottom: 0;
  }
}

.form-label {
  display: block;
  font-size: 28rpx;
  color: #333333;
  margin-bottom: 16rpx;
}

.required {
  color: #FF3B30;
}

.picker {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 88rpx;
  padding: 0 24rpx;
  border: 2rpx solid #E5E5E5;
  border-radius: 12rpx;
  font-size: 28rpx;
  
  .placeholder {
    color: #CCCCCC;
  }
  
  .arrow {
    font-size: 40rpx;
    color: #CCCCCC;
    transform: rotate(90deg);
  }
}

.input {
  width: 100%;
  height: 88rpx;
  padding: 0 24rpx;
  border: 2rpx solid #E5E5E5;
  border-radius: 12rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}

.textarea {
  width: 100%;
  min-height: 200rpx;
  padding: 20rpx 24rpx;
  border: 2rpx solid #E5E5E5;
  border-radius: 12rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}

.form-desc {
  display: block;
  font-size: 24rpx;
  color: #999999;
  margin-bottom: 16rpx;
}

.upload-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.upload-item {
  position: relative;
  width: 200rpx;
  height: 200rpx;
}

.upload-image {
  width: 100%;
  height: 100%;
  border-radius: 12rpx;
}

.delete-btn {
  position: absolute;
  top: -12rpx;
  right: -12rpx;
  width: 48rpx;
  height: 48rpx;
  background: #FF3B30;
  border-radius: 50%;
  color: #FFFFFF;
  font-size: 36rpx;
  line-height: 48rpx;
  text-align: center;
}

.upload-btn {
  width: 200rpx;
  height: 200rpx;
  border: 2rpx dashed #CCCCCC;
  border-radius: 12rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.upload-icon {
  font-size: 60rpx;
  color: #CCCCCC;
  margin-bottom: 8rpx;
}

.upload-text {
  font-size: 24rpx;
  color: #999999;
}

.submit-section {
  padding: 32rpx;
}

.btn {
  width: 100%;
  height: 88rpx;
  border-radius: 12rpx;
  font-size: 32rpx;
  font-weight: 500;
  border: none;
}

.btn-primary {
  background: linear-gradient(135deg, #0066FF 0%, #0099FF 100%);
  color: #FFFFFF;
}

.btn-disabled {
  opacity: 0.5;
}
</style>
