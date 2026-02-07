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
</style>
