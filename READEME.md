# 📱 Screen Track Assessment - 大学生屏幕使用行为评估系统

## 📋 项目背景

本项目是一个**大学生创新创业项目**，旨在通过记录和分析大学生的屏幕使用行为，帮助用户了解自身的数字设备使用习惯，提供科学的评估与建议，促进健康的屏幕使用方式。

系统支持 **Web 端**和**移动端**双平台，用户可以随时随地查看自己的屏幕使用数据与评估报告。

---

## 🛠️ 技术栈

| 层级 | 技术 | 说明 |
|------|------|------|
| **后端** | Spring Boot | 提供 RESTful API 服务 |
| **前端 Web** | Vue 3 + Vite | Web 端管理与数据展示 |
| **前端移动端** | UniApp | 跨平台移动应用（微信小程序 / Android / iOS） |
| **数据库** | MySQL | 数据持久化存储 |

---

## 📁 项目结构

```
screen_track_assessment/
├── backend/                # 后端服务 (Spring Boot)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/       # Java 源代码
│   │   │   └── resources/  # 配置文件
│   │   └── test/           # 测试代码
│   └── pom.xml             # Maven 依赖管理
│
├── frontend-web/           # Web 前端 (Vue 3)
│   ├── src/
│   │   ├── assets/         # 静态资源
│   │   ├── components/     # 公共组件
│   │   ├── views/          # 页面视图
│   │   ├── router/         # 路由配置
│   │   ├── stores/         # 状态管理
│   │   ├── App.vue         # 根组件
│   │   └── main.js         # 入口文件
│   ├── package.json
│   └── vite.config.js
│
├── frontend-mobile/        # 移动端 (UniApp)
│   ├── pages/              # 页面
│   ├── components/         # 组件
│   ├── static/             # 静态资源
│   ├── App.vue             # 根组件
│   ├── main.js             # 入口文件
│   ├── manifest.json       # UniApp 配置
│   ├── pages.json          # 页面路由配置
│   └── uni.scss            # 全局样式
│
└── README.md               # 项目说明文档
```

---

## 📦 模块说明

### 🔧 Backend（后端服务）

- 基于 **Spring Boot** 构建的 RESTful API 服务
- 负责用户管理、屏幕使用数据采集、行为分析与评估报告生成
- 提供数据接口供 Web 端和移动端调用

### 🖥️ Frontend-Web（Web 前端）

- 基于 **Vue 3 + Vite** 构建
- 面向管理员 / 用户的数据可视化展示平台
- 支持屏幕使用数据的图表展示与评估报告查看

### 📱 Frontend-Mobile（移动端）

- 基于 **UniApp** 构建，支持多平台编译
- 面向用户的移动端应用，支持数据记录与实时查看
- 可编译为微信小程序、Android App、iOS App 等

---

## 🚀 快速启动

### 环境要求

- **JDK** 17+
- **Maven** 3.6+
- **Node.js** 16+
- **MySQL** 8.0+
- **HBuilderX**（用于 UniApp 开发，可选）

### 1️⃣ 启动后端服务

```bash
cd backend

# 配置数据库连接（修改 application.yml）
# spring.datasource.url / username / password

# 安装依赖并启动
mvn clean install
mvn spring-boot:run
```

后端服务默认运行在 `http://localhost:8080`

### 2️⃣ 启动 Web 前端

```bash
cd frontend-web

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

Web 前端默认运行在 `http://localhost:5173`

### 3️⃣ 启动移动端

```bash
cd frontend-mobile

# 安装依赖
npm install

# 方式一：使用命令行启动（H5 模式）
npm run dev:h5

# 方式二：使用 HBuilderX 打开项目，选择目标平台运行
```

---

## 👥 团队信息

大学生创新创业项目团队

---

## 📄 License

本项目仅用于学习与学术研究用途。