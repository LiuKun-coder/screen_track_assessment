<template>
  <view class="policy-page">
    <view class="card">
      <view class="header">
        <text class="title">隐私政策</text>
        <text class="subtitle">更新日期：2026-02-07</text>
      </view>

      <view class="section">
        <text class="section-title">我们收集的信息</text>
        <text class="section-text">为提供账号登录、违规查询与轨迹展示等基础服务，我们会收集必要的账号与使用信息。</text>
      </view>

      <view class="section">
        <text class="section-title">信息的使用方式</text>
        <text class="section-text">收集的信息仅用于提供核心功能、优化体验与保障安全，不会用于与服务无关的目的。</text>
      </view>

      <view class="section">
        <text class="section-title">信息的保护</text>
        <text class="section-text">我们采用合理的安全措施保护数据，减少信息被未经授权访问、泄露或滥用的风险。</text>
      </view>

      <view class="section">
        <text class="section-title">您的权利</text>
        <text class="section-text">您可以通过应用内功能查看或更新个人信息，并可联系我们处理隐私相关诉求。</text>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.policy-page {
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
