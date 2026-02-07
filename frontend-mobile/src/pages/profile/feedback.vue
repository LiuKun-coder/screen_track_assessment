<template>
  <view class="feedback-page">
    <view class="card">
      <view class="section">
        <text class="section-title">反馈邮箱</text>
        <view class="info-row">
          <text class="label">邮箱地址</text>
          <text class="value">04223697@cumt.edu.cn</text>
        </view>
      </view>

      <view class="section">
        <text class="section-title">反馈内容</text>
        <view class="form-item">
          <text class="form-label">问题类型</text>
          <view class="picker">
            <text class="placeholder">请选择问题类型</text>
            <text class="arrow">›</text>
          </view>
        </view>

        <view class="form-item">
          <text class="form-label">详细描述</text>
          <textarea
            class="textarea"
            placeholder="请描述您的问题或建议"
            maxlength="500"
          />
        </view>

        <view class="form-item">
          <text class="form-label">联系方式</text>
          <input
            class="input"
            type="text"
            placeholder="手机号或邮箱（选填）"
          />
        </view>
      </view>

      <view class="submit-section">
        <button class="btn btn-primary btn-disabled">提交反馈</button>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.feedback-page {
  min-height: 100vh;
  background: #FAF7F2;
  padding: 24rpx 32rpx;
}

.card {
  background: #FFFFFF;
  border-radius: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(45, 52, 54, 0.06);
  overflow: hidden;
}

.section {
  padding: 32rpx;
}

.section + .section {
  border-top: 1rpx solid #E8E4DE;
}

.section-title {
  display: block;
  font-size: 32rpx;
  font-weight: 600;
  color: #2D3436;
  margin-bottom: 20rpx;
}

.info-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.label {
  font-size: 28rpx;
  color: #9BA4A9;
}

.value {
  font-size: 28rpx;
  color: #2D3436;
}

.form-item {
  margin-bottom: 24rpx;
}

.form-label {
  display: block;
  font-size: 28rpx;
  color: #2D3436;
  margin-bottom: 16rpx;
}

.picker {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 96rpx;
  padding: 0 24rpx;
  border: 2rpx solid #E8E4DE;
  border-radius: 20rpx;
  font-size: 28rpx;
  background: #FAF7F2;
}

.placeholder {
  color: #9BA4A9;
}

.arrow {
  font-size: 40rpx;
  color: #9BA4A9;
  transform: rotate(90deg);
}

.input {
  width: 100%;
  height: 96rpx;
  padding: 0 24rpx;
  border: 2rpx solid #E8E4DE;
  border-radius: 20rpx;
  font-size: 28rpx;
  box-sizing: border-box;
  background: #FAF7F2;
}

.textarea {
  width: 100%;
  min-height: 200rpx;
  padding: 20rpx 24rpx;
  border: 2rpx solid #E8E4DE;
  border-radius: 20rpx;
  font-size: 28rpx;
  box-sizing: border-box;
  background: #FAF7F2;
}

.submit-section {
  padding: 32rpx;
}

.btn {
  width: 100%;
  height: 88rpx;
  border-radius: 20rpx;
  font-size: 32rpx;
  font-weight: 500;
  border: none;
}

.btn-primary {
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
  color: #FFFFFF;
}

.btn-disabled {
  opacity: 0.5;
}
</style>
