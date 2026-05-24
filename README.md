# 朝夕考勤系统

朝夕考勤系统是一个基于 Spring Boot + Vue 3 的前后端分离考勤管理系统，覆盖员工档案、部门职位、考勤打卡、考勤异常/补卡审批、请假审批、固定资产、客户、会议通知、财务工资和统计分析等办公场景。

本仓库当前前端已重构为 Vue 3 `<script setup>` + Composition API，并统一为深色科技后台风格。后端接口、字段含义、路由地址和权限规则保持兼容。

## 项目结构

```text
AttendanceManager
├─ AttendanceManagerApi - idea/       # 后端 Spring Boot 项目
│  ├─ src/main/java/com/zpark/sb/
│  │  ├─ controller/                  # 接口控制器
│  │  ├─ service/                     # 业务逻辑
│  │  ├─ entity/                      # 实体类
│  │  └─ config/                      # 跨域、鉴权等配置
│  ├─ src/main/resources/
│  │  ├─ application.yaml             # 后端配置
│  │  └─ com/zpark/sb/mapper/         # MyBatis XML 映射
│  ├─ attendance_manager.sql          # 数据库脚本
│  └─ pom.xml                         # Maven 配置
└─ AttendanceManagerVue/              # 前端 Vue 3 + Vite 项目
   ├─ src/
   │  ├─ api/                         # 前端接口 service 层
   │  ├─ axios/                       # 唯一 HTTP client
   │  ├─ components/common/           # 通用 UI 组件
   │  ├─ pages/                       # 路由页面
   │  ├─ router/                      # 路由与权限守卫
   │  ├─ stores/                      # 轻量共享状态
   │  ├─ styles/                      # 全局主题样式
   │  ├─ utils/                       # 登录态工具
   │  └─ assets/                      # 静态资源
   ├─ package.json
   └─ vite.config.js
```

## 技术栈

后端：
- Java
- Spring Boot
- Spring MVC
- MyBatis
- MySQL
- EasyExcel
- Maven

前端：
- Vue 3
- Vite
- Vue Router 4
- Element Plus
- Axios
- AntV G2

## 前端重构状态

已完成：
- 登录、注册、主框架、首页重构为 Vue 3 `<script setup>`。
- 用户端页面重构：任务、请假、固定资产、考勤、客户、会议、通知、修改密码。
- 管理端页面重构：员工管理、部门/职位、财务管理、统计分析。
- 新增 `src/api/*`，页面不再直接散落 `axios` 调用。
- 新增 `src/components/common/*`，复用页面标题、工具栏、状态标签、指标卡、弹窗、空状态等组件。
- 新增 `src/stores/user.js`，缓存当前登录用户基础信息。
- 重写 `src/styles/app.css`，统一深色科技后台主题。
- 已删除未引用旧组件文件：`src/components/HelloWorld.vue`、`src/components/Login.vue`、`src/components/List.vue`、`src/components/admin/*`。

保留兼容：
- `src/axios/axios.js` 仍是唯一 HTTP client。
- 继续处理 `Authorization` token 和 401 登录态清理。
- 后端接口路径、请求参数、响应兼容逻辑集中在 `src/api/*`。
- 路由地址保持兼容，例如 `/home`、`/task`、`/employee`、`/finance`。

## 功能模块

- 登录、注册、退出登录
- 员工管理
- 部门管理
- 职位管理
- 上班打卡、下班打卡
- 个人考勤查询
- 考勤异常中心
- 考勤异常/补卡申请
- 补卡申请撤销、重复申请拦截和未来日期限制
- 管理端补卡统计、高频异常员工排行
- 月度考勤统计
- 考勤 Excel 导出
- 请假申请
- 固定资产申请与购置
- 审批任务管理
- 客户管理
- 会议管理
- 通知公告
- 工资条发放
- 员工年龄、学历、新增人数统计图表
- 个人密码修改

## 环境要求

- JDK 8 或兼容版本
- Maven 3.x
- MySQL 5.7 或 8.x
- Node.js >= 18
- npm >= 9

## 数据库

数据库脚本：

```text
AttendanceManagerApi - idea/attendance_manager.sql
```

默认数据库名：

```text
attendance_manager
```

创建数据库示例：

```sql
CREATE DATABASE attendance_manager DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

后端数据库配置文件：

```text
AttendanceManagerApi - idea/src/main/resources/application.yaml
```

## 后端启动

```bash
cd "AttendanceManagerApi - idea"
mvn spring-boot:run
```

默认后端地址：

```text
http://localhost:9331
```

## 前端启动

```bash
cd AttendanceManagerVue
npm install
npm run dev
```

默认前端地址：

```text
http://localhost:9332
```

生产构建：

```bash
npm run build
```

预览构建：

```bash
npm run preview
```

前端请求后端地址配置：

```text
AttendanceManagerVue/src/axios/axios.js
```

默认后端地址：

```javascript
http://localhost:9331
```

## 常用接口前缀

| 接口前缀 | 说明 |
| --- | --- |
| `/login` | 登录、注册 |
| `/employee` | 员工管理 |
| `/employeeType` | 员工类型 |
| `/department` | 部门管理 |
| `/position` | 职位管理 |
| `/check` | 考勤管理 |
| `/checkRepair` | 考勤补卡申请 |
| `/leave` | 请假管理 |
| `/leaveType` | 请假类型 |
| `/task` | 审批任务 |
| `/meeting` | 会议与通知 |
| `/fixedasset` | 固定资产 |
| `/fixedassetType` | 固定资产类型 |
| `/customer` | 客户管理 |
| `/salary` | 工资管理 |
| `/enum` | 枚举/配置数据 |

## 验证记录

已执行：

```bash
npm run build
```

当前构建通过。Vite/Rolldown 仍会输出两个非阻塞警告：
当前构建通过，已通过 Vite 配置处理依赖拆包和 `@vueuse/core` 注释兼容问题，构建过程无新增警告。

## 注意事项

- 后端默认端口为 `9331`，前端默认端口为 `9332`。
- 前端登录态存储在 `sessionStorage`，包括用户名、token、用户类型。
- 管理端路由需要用户类型为 `3`，非管理员访问会跳回首页。
- 统计接口已逐步统一为 `Result` 响应格式，前端 API 层负责兼容解包。
- 补卡统计接口仅管理员可访问；补卡审批只允许处理待审批任务。
- 本项目面向学习、课程设计和本地演示场景；用于生产前需要补充更完整的权限、安全、审计和异常处理。
- 项目规则禁止批量删除文件或目录；删除文件时应逐个明确路径删除。

## 许可说明

本项目仅用于学习与交流。如需用于其他用途，请自行确认代码来源、依赖许可和数据安全要求。
