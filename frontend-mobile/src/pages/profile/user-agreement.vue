<template>
  <view class="agreement-page">
    <view class="card">
      <view class="header">
        <text class="title">用户协议</text>
        <text class="subtitle">更新日期：2026-02-07</text>
      </view>

      <view class="section">
        <text class="section-title">服务说明</text>
        <text class="section-text">本应用用于校园电动车违规管理与轨迹展示，提供查询、申诉与通知等基础服务。</text>
      </view>

      <view class="section">
        <text class="section-title">用户义务</text>
        <text class="section-text">用户应提供真实信息并遵守校园管理规范，合理使用应用功能。</text>
      </view>

      <view class="section">
        <text class="section-title">使用规范</text>
        <text class="section-text">禁止以任何方式干扰系统运行或影响他人正常使用。</text>
      </view>

      <view class="section">
        <text class="section-title">联系方式</text>
        <text class="section-text">如需协助或咨询，请通过应用内意见反馈与我们联系。</text>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.agreement-page {
  min-height: 100vh;
  background: #FAF7F2;
  padding: 24rpx 32rpx;
}

.card {
  background: #FFFFFF;
  border-radius: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(45, 52, 54, 0.06);
  padding: 32rpx;
}

.header {
  margin-bottom: 24rpx;
}

.title {
  display: block;
  font-size: 34rpx;
  font-weight: 600;
  color: #2D3436;
  margin-bottom: 8rpx;
}

.subtitle {
  font-size: 24rpx;
  color: #9BA4A9;
}

.section {
  margin-bottom: 24rpx;
}

.section-title {
  display: block;
  font-size: 30rpx;
  font-weight: 600;
  color: #2D3436;
  margin-bottom: 12rpx;
}

.section-text {
  display: block;
  font-size: 28rpx;
  color: #636E72;
  line-height: 1.7;
}
</style>
