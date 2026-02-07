# UniApp 项目复现指南

本文档详细描述了如何将现有的 Vue 3 Web 项目复现到 UniApp 平台。该指南包含项目总览、功能模块详情、以及详细的迁移步骤。

## 1. 项目总览

本项目是一个校园违规管理系统，包含用户端和管理端功能。主要功能包括违规通报查看、个人违规查询、申诉处理、轨迹回溯、电子围栏管理等。

### 1.1 技术栈

- **原项目**: Vue 3, Element Plus, Vue Router
- **目标项目**: UniApp (Vue 3), uni-ui (或 uView Plus), Pinia (推荐)

### 1.2 核心功能模块

| 模块名称 | 功能描述 | 权限 |
| :--- | :--- | :--- |
| **登录/注册** | 账号登录、扫码登录(模拟)、注册、重置密码 | 所有用户 |
| **违规通报** | 查看公开的违规记录，支持按类型、地点、时间筛选 | 所有用户 |
| **我的违规** | 查看个人的违规记录，支持筛选，查看详情 | 所有用户 |
| **轨迹回溯** | 查看违规轨迹，日期选择，管理员可查看指定用户轨迹 | 所有用户/管理员 |
| **电子围栏** | 查看电子围栏范围，管理员可编辑 | 所有用户/管理员 |
| **我要申诉** | 提交违规申诉 | 所有用户 |
| **申诉处理** | 处理用户的申诉请求 | 管理员 |
| **个人信息** | 查看和编辑个人资料 | 所有用户 |
| **管理人员** | 管理员专用功能模块 | 管理员 |
| **违规大屏** | 大屏展示违规数据 | 所有用户 |

---

## 2. 项目结构迁移

### 2.1 目录结构对比

| 原项目 (Vue CLI) | UniApp 项目 | 说明 |
| :--- | :--- | :--- |
| `src/main.js` | `main.js` | 入口文件，需修改挂载方式 |
| `src/App.vue` | `App.vue` | 应用生命周期管理 |
| `src/router/index.js` | `pages.json` | 路由配置改为 pages.json 配置 |
| `src/views/` | `pages/` | 页面文件存放目录 |
| `src/components/` | `components/` | 通用组件存放目录 |
| `src/assets/` | `static/` | 静态资源 (图片等) |
| `src/utils/` | `common/` 或 `utils/` | 工具函数 |

### 2.2 路由配置 (pages.json)

原项目的 `src/router/index.js` 需要转换为 `pages.json`。

```json
{
  "pages": [
    {
      "path": "pages/login/login",
      "style": { "navigationBarTitleText": "登录" }
    },
    {
      "path": "pages/register/register",
      "style": { "navigationBarTitleText": "注册" }
    },
    {
      "path": "pages/home/home",
      "style": { "navigationBarTitleText": "首页", "enablePullDownRefresh": false }
    },
    {
      "path": "pages/reset-password/reset-password",
      "style": { "navigationBarTitleText": "重置密码" }
    },
    {
      "path": "pages/violation-screen/violation-screen",
      "style": { "navigationBarTitleText": "违规大屏" }
    }
  ],
  "globalStyle": {
    "navigationBarTextStyle": "black",
    "navigationBarTitleText": "校园违规管理系统",
    "navigationBarBackgroundColor": "#F8F8F8",
    "backgroundColor": "#F8F8F8"
  }
}
```

---

## 3. 详细迁移步骤

### 3.1 环境搭建

1.  安装 HBuilderX 或使用 VS Code + UniApp CLI。
2.  创建新项目：选择 "Vue 3" 版本。
3.  安装 UI 库：推荐使用 `uni-ui` 或 `uView Plus` 替代 `Element Plus`。
    *   *注意：Element Plus 是为 PC 端设计的，不兼容 UniApp 的非 H5 端（如小程序、App）。*

### 3.2 页面与组件迁移

#### 3.2.1 登录页 (`views/Login.vue` -> `pages/login/login.vue`)
-   **模板**: 将 `el-input`, `el-button` 替换为 `uni-easyinput` (或 `u-input`), `button` (或 `u-button`)。
-   **逻辑**:
    -   `localStorage` 替换为 `uni.setStorageSync` / `uni.getStorageSync`。
    -   路由跳转 `router.push` 替换为 `uni.navigateTo` 或 `uni.reLaunch` (登录成功后跳转首页建议用 `reLaunch`)。

#### 3.2.2 首页 (`views/Home.vue` -> `pages/home/home.vue`)
-   **布局**: 原项目使用左右分栏 (Sidebar + Content)。
    -   *移动端适配建议*: 改为底部 TabBar 导航，或者使用抽屉式侧边栏 (`uni-drawer`)。
    -   如果必须保持左右分栏（如 iPad/PC 适配），可使用 `scroll-view` 实现。
-   **动态组件**: 原项目使用 `<component :is="...">` 或 `v-if` 切换组件。UniApp 中同样支持，但建议将复杂子页面拆分为独立页面，通过路由跳转，除非是简单的 Tab 切换。

#### 3.2.3 组件迁移
所有 `src/components/` 下的组件需要进行 UI 替换。

| 原组件 | UniApp 替代方案 | 备注 |
| :--- | :--- | :--- |
| `el-table` | `uni-table` 或 `<view>` 列表布局 | 移动端推荐使用列表布局 (List Item) |
| `el-dialog` | `uni-popup` | 弹窗组件 |
| `el-form` | `uni-forms` | 表单组件 |
| `el-date-picker` | `uni-datetime-picker` | 日期选择器 |
| `el-tabs` | `uni-segmented-control` 或 自定义 Tab | 分段器 |
| `el-icon` | `uni-icons` | 图标组件 |

**重点组件逻辑复现：**

1.  **MyViolations.vue (我的违规)**
    -   使用 `uni-list` 展示违规列表。
    -   筛选功能使用 `uni-popup` 制作下拉筛选框。
    -   数据目前是 Mock 的，建议封装 API 请求。

2.  **TrajectoryTracking.vue (轨迹回溯)**
    -   **地图**: 使用 `<map>` 组件。
    -   **功能**: 需要获取经纬度数据并在 map 组件上绘制 `polyline` (路线) 和 `markers` (标记点)。
    -   **电子围栏**: 使用 `<map>` 组件的 `polygons` 属性绘制区域。

3.  **ViolationReport.vue (违规通报)**
    -   类似 `MyViolations`，使用列表展示。

### 3.3 数据与 API

原项目使用 Mock 数据和 `localStorage`。

1.  **Mock 数据**: 暂时可以将原组件中的 `ref` 数据直接复制过去。
2.  **API 请求**:
    -   创建 `utils/request.js` 封装 `uni.request`。
    -   示例：
        ```javascript
        const baseUrl = 'https://api.example.com';
        export const request = (options) => {
            return new Promise((resolve, reject) => {
                uni.request({
                    url: baseUrl + options.url,
                    method: options.method || 'GET',
                    data: options.data || {},
                    header: {
                        'Authorization': uni.getStorageSync('token')
                    },
                    success: (res) => resolve(res.data),
                    fail: (err) => reject(err)
                });
            });
        };
        ```

### 3.4 样式适配

-   **单位转换**: 原项目使用 `px` 单位。UniApp 开发移动端推荐使用 `rpx` (responsive px)，通常 1px (Web) ≈ 2rpx (750px 设计稿)。
-   **全局样式**: 原 `src/assets/main.css` 中包含大量针对 `el-` 类名的样式（如 `.login-form .el-input`）。迁移时需要：
    1.  将这些样式迁移到 `App.vue` 的 `<style>` 中，或者新建 `common/common.css` 并引入。
    2.  **重构选择器**: 由于不再使用 Element Plus，需将 `.el-input` 等选择器修改为 UniApp 组件对应的类名（如 `.uni-easyinput`）或自定义类名。
-   **背景与渐变**: CSS 渐变 (`linear-gradient`) 在 UniApp 中支持良好，但需注意在 nvue 页面中的兼容性（如果使用 nvue）。

---

## 4. 关键代码片段参考

### 4.1 登录逻辑 (UniApp 版)

```javascript
// pages/login/login.vue
<script setup>
import { ref, reactive } from 'vue'

const loginForm = reactive({
  username: '',
  password: ''
})

const handleLogin = () => {
  if (!loginForm.username || !loginForm.password) {
    uni.showToast({ title: '请输入用户名和密码', icon: 'none' })
    return
  }

  // 模拟登录
  if (loginForm.username === 'kunliu' && loginForm.password === '123456') {
    uni.setStorageSync('userInfo', { name: '普通用户', role: 'user' })
    uni.setStorageSync('isLoggedIn', true)
    
    uni.showToast({ title: '登录成功' })
    setTimeout(() => {
      uni.reLaunch({ url: '/pages/home/home' })
    }, 1500)
  } else {
    uni.showToast({ title: '账号或密码错误', icon: 'none' })
  }
}
</script>
```

### 4.2 路由跳转

```javascript
// 原 Vue: router.push('/violation-screen')
// UniApp:
uni.navigateTo({
  url: '/pages/violation-screen/violation-screen'
})
```

## 5. 注意事项

1.  **DOM 操作**: UniApp 非 H5 端不支持 `document` 和 `window` 对象，避免直接操作 DOM。
2.  **生命周期**:
    -   页面生命周期: `onLoad`, `onShow`, `onReady`。
    -   组件生命周期: Vue 3 的 `onMounted` 等依然可用。
3.  **地图组件**: 小程序和 App 端的 `<map>` 组件是原生组件，层级最高，需要注意覆盖问题 (使用 `cover-view` 或 `nvue`)。

---

**总结**: 重建工作的核心在于将 Element Plus 组件替换为 UniApp 兼容的组件库，并调整路由和存储的 API 调用。业务逻辑部分 (JavaScript) 大部分可以直接复用。
