<script>
import { useUserStore } from '@/stores/user.js'
import { useSystemStore } from '@/stores/system.js'
import { isLoggedIn, navigateToLogin } from '@/utils/route-guard.js'

export default {
  onLaunch: function () {
    console.log('App Launch')
    
    // 初始化系统状态
    const systemStore = useSystemStore()
    systemStore.init()
    
    // 初始化用户状态
    const userStore = useUserStore()
    userStore.init()
    
    // 检查登录状态
    // 延迟执行,确保页面加载完成
    setTimeout(() => {
      if (!isLoggedIn()) {
        console.log('用户未登录,跳转到登录页')
        navigateToLogin()
      } else {
        console.log('用户已登录')
      }
    }, 100)
  },
  onShow: function () {
    console.log('App Show')
  },
  onHide: function () {
    console.log('App Hide')
  }
}
</script>

<style lang="scss">
/* ========================================
   温暖自然的设计风格
   - 米白背景 #FAF7F2
   - 鼠尾草绿 #7D9E87
   - 陶土红 #C9735D
   - 天空蓝 #6B9AC4
   - 暖杏色 #D4A574
======================================== */

/* 页面公共样式 */
page {
  background-color: #FAF7F2;
  font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'Helvetica Neue', sans-serif;
  font-size: 28rpx;
  color: #2D3436;
  line-height: 1.6;
}

/* 容器 */
.container {
  width: 100%;
  min-height: 100vh;
  padding: 0 32rpx;
  box-sizing: border-box;
}

.page-container {
  min-height: 100vh;
  background-color: #FAF7F2;
}

/* Flex 布局 */
.flex {
  display: flex;
}

.flex-center {
  display: flex;
  justify-content: center;
  align-items: center;
}

.flex-between {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.flex-column {
  display: flex;
  flex-direction: column;
}

.flex-1 {
  flex: 1;
}

/* 按钮样式 */
.btn {
  height: 88rpx;
  border-radius: 20rpx;
  font-size: 32rpx;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  outline: none;
}

.btn-primary {
  background: linear-gradient(135deg, #7D9E87 0%, #6B9AC4 100%);
  color: #FFFFFF;
}

.btn-outline {
  border: 2rpx solid #7D9E87;
  background: transparent;
  color: #7D9E87;
}

.btn-disabled {
  opacity: 0.5;
  pointer-events: none;
}

/* 卡片样式 */
.card {
  background: #FFFFFF;
  border-radius: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(45, 52, 54, 0.06);
  padding: 32rpx;
  margin-bottom: 24rpx;
}

/* 输入框样式 */
.input {
  width: 100%;
  height: 96rpx;
  border: 2rpx solid #E8E4DE;
  border-radius: 20rpx;
  padding: 0 24rpx;
  font-size: 30rpx;
  box-sizing: border-box;
  background: #FAF7F2;
}

.input-focus {
  border-color: #6B9AC4;
  background: #FFFFFF;
}

/* 间距工具类 */
.mt-8 { margin-top: 8rpx; }
.mt-16 { margin-top: 16rpx; }
.mt-24 { margin-top: 24rpx; }
.mt-32 { margin-top: 32rpx; }
.mt-48 { margin-top: 48rpx; }

.mb-8 { margin-bottom: 8rpx; }
.mb-16 { margin-bottom: 16rpx; }
.mb-24 { margin-bottom: 24rpx; }
.mb-32 { margin-bottom: 32rpx; }
.mb-48 { margin-bottom: 48rpx; }

.ml-8 { margin-left: 8rpx; }
.ml-16 { margin-left: 16rpx; }
.ml-24 { margin-left: 24rpx; }

.mr-8 { margin-right: 8rpx; }
.mr-16 { margin-right: 16rpx; }
.mr-24 { margin-right: 24rpx; }

.p-16 { padding: 16rpx; }
.p-24 { padding: 24rpx; }
.p-32 { padding: 32rpx; }

/* 文字样式 */
.text-primary { color: #7D9E87; }
.text-success { color: #7D9E87; }
.text-warning { color: #D4A574; }
.text-danger { color: #C9735D; }
.text-muted { color: #9BA4A9; }

.text-center { text-align: center; }
.text-right { text-align: right; }

.text-bold { font-weight: 600; }

.text-sm { font-size: 24rpx; }
.text-base { font-size: 28rpx; }
.text-lg { font-size: 32rpx; }
.text-xl { font-size: 36rpx; }
.text-2xl { font-size: 40rpx; }

/* 分割线 */
.divider {
  height: 1rpx;
  background-color: #E8E4DE;
  margin: 24rpx 0;
}

/* 安全区域 */
.safe-area-bottom {
  padding-bottom: env(safe-area-inset-bottom);
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 32rpx;
  
  .empty-icon {
    width: 200rpx;
    height: 200rpx;
    margin-bottom: 32rpx;
  }
  
  .empty-text {
    font-size: 28rpx;
    color: #9BA4A9;
  }
}

/* 加载更多 */
.load-more {
  padding: 32rpx;
  text-align: center;
  font-size: 26rpx;
  color: #9BA4A9;
}
</style>
