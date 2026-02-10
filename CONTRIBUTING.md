# 🚀 大学生创新创业项目 - GitHub 团队协作开发规范手册

**编写人**：项目组长 (LiuKun)
**日期**：2026年2月10日
**适用对象**：全体项目组成员

---

## 👋 前言
为了保证我们的大创项目（涵盖 SpringBoot 后端、Vue3 Web端、UniApp 移动端）能够高效推进，避免代码冲突和“把代码写炸”的情况，我制定了这份 **GitHub 协作开发规范**。

请大家务必仔细阅读，以后的开发工作将严格按照此流程进行。

---

## 🛠️ 第一阶段：准备工作 (一次性设置)

### 1. 获取“直通车”权限
我已经将大家添加为本项目的 **Collaborators (协作者)**。
* 请查收你们的 GitHub 注册邮箱，点击 **Accept Invitation**。
* **注意**：只有接受邀请后，你们才拥有直接向仓库提交代码的权限。

### 2. 克隆项目 (Clone)
请不要 Fork 项目，**直接克隆我的主仓库**，这样我们就在同一个“代码池”里工作了，方便后续合并。

打开 VS Code 终端或 Git Bash，执行：

    git clone https://github.com/LiuKun-coder/screen_track_accessment.git

### 3. 理解项目结构 (Monorepo)
我们的所有代码都在这个仓库里，请认准自己的开发目录，不要动别人的文件夹：
* 📂 `backend/`：后端代码 (Spring Boot)
* 📂 `frontend-web/`：网页端代码 (Vue3)
* 📂 `frontend-mobile/`：小程序/App代码 (UniApp)
* 📄 `.gitignore`：**重要！** 也就是那个“门卫”文件，它会自动忽略 `node_modules` 等垃圾文件，请不要随意删除它。

---

## 💻 第二阶段：日常开发流程 (每日必做)

**⚠️ 铁律：严禁直接在 `main` 分支上修改代码！所有开发必须在自己的分支上进行。**

### Step 1: 开工 - 拉取最新代码 (Pull)
每天写代码前，先确保你的本地代码是最新的（防止和队友脱节）：
1. VS Code 左下角点击分支名，切回 `main` 分支。
2. 点击同步按钮 (Sync/Pull)。
3. **如果有新功能要开发**：基于最新的 `main` 创建你的新分支。

### Step 2: 开启分支 (Branching)
点击 VS Code 左下角的分支图标 -> `Create New Branch`。
**命名规范**：
* 功能开发：`feature/功能名` (例如 `feature/login-page`)
* Bug修复：`fix/bug描述` (例如 `fix/api-error`)
* 个人实验：`dev/姓名-实验` (例如 `dev/liukun-test`)

### Step 3: 提交代码 (Commit)
在你的分支上尽情写代码。写完后：
1. 点击左侧“源代码管理” (分叉图标)。
2. 填写清晰的备注 (例如：`Feat: 完成用户注册接口`)。
3. 点击提交 (✅)。

### Step 4: 收工 - 推送分支 (Push)
1. 点击 **Publish Branch** (或者 Sync Changes)。
2. 这时，GitHub 云端就会出现一个和你同名的新分支。

---

## 🤝 第三阶段：代码合并 (Pull Request)

当你的功能写完了，想要合并进主程序 (`main`)，请按以下步骤操作：

1. **不要在本地合并！** 不要自己试图把 `main` merge 到你的分支，除非是为了解决冲突。
2. 登录 GitHub 网页，进入仓库首页。
3. 你会看到一个黄色的提示条，点击绿色按钮 **Compare & pull request**。
4. **检查方向**：确保箭头方向是 `main` <--- `你的分支`。
5. **指定审核人 (Reviewers)**：在右侧栏选择我 (LiuKun) 作为 Reviewer。
6. 点击 **Create pull request**。

**接下来交给我**：我会收到通知，检查你的代码。如果没有问题，我会点击 Merge，你的代码就正式合入主干了.

---

## 🛡️ 第四阶段：冲突解决 (Conflict)

如果你提交 PR 时提示 **"Can't automatically merge"**，或者在 VS Code 拉取代码时爆红，说明**冲突了**（通常是因为我们修改了同一行代码）。

**解决方法（在你自己电脑上修）：**
1. 在 VS Code 里，它会把冲突的文件标红。
2. 打开文件，你会看到 `<<<<<<<` 和 `>>>>>>>` 的标记。
3. 也就是做选择题：
    * 点击 `Accept Current Change` (保留你的)
    * 点击 `Accept Incoming Change` (保留传入的/别人的)
    * 或者手动修改代码，把两边的逻辑融合。
4. 修好后，保存 -> 提交 (Commit) -> 推送 (Push)。冲突就解决了。

---

## 📢 特别提醒


1. **数据库变动**：如果谁修改了数据库结构，请务必在 MySQL Workbench 中导出新的 `.sql` 文件放在 `backend/sql/` 目录下，并在群里通知大家更新。
2. **保持沟通**：遇到 Git 报错不要慌，截图发群里，**千万别随意执行 `git push -f` (强制推送)**，那会覆盖掉队友的代码。

