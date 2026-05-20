# AttendanceManager 考勤管理系统

一个基于 **Spring Boot + Vue** 的前后端分离考勤管理系统，包含员工管理、部门管理、上下班打卡、请假申请、任务审批、会议通知、固定资产、薪资结算和考勤统计等功能。

> 仓库名为 `AttendanceManagemet`，项目主体名称为 `AttendanceManager`。

## 项目结构

```text
AttendanceManager
├── AttendanceManagerApi - idea/    # 后端 Spring Boot 项目
│   ├── src/main/java/com/rabbiter/am
│   │   ├── controller/             # 接口控制层
│   │   ├── service/                # 业务逻辑层
│   │   ├── dao/                    # MyBatis DAO
│   │   ├── entity/                 # 实体类
│   │   └── config/                 # 通用配置与返回结果封装
│   ├── src/main/resources
│   │   ├── application.yaml        # 后端配置
│   │   └── com/rabbiter/am/mapper/ # MyBatis XML 映射文件
│   ├── attendance_manager.sql      # 数据库脚本
│   └── pom.xml                     # Maven 配置
│
└── AttendanceManagerVue/           # 前端 Vue 项目
    ├── src
    │   ├── pages/                  # 页面
    │   ├── components/             # 组件
    │   ├── router/                 # 路由
    │   ├── axios/                  # Axios 请求配置
    │   └── assets/                 # 静态资源
    ├── config/                     # Webpack/Vue 配置
    └── package.json
```

## 技术栈

### 后端

- Java 8
- Spring Boot 2.4.3
- Spring MVC
- MyBatis
- MySQL
- EasyExcel
- Maven

### 前端

- Vue 2
- Vue Router
- Element UI
- Axios
- AntV G2
- Webpack
- vue-print-nb

## 功能模块

- 登录认证
- 员工信息管理
- 部门管理
- 职位管理
- 上班打卡、下班打卡
- 个人考勤查询
- 月度考勤统计
- 考勤 Excel 导出
- 请假申请
- 请假类型管理
- 审批任务管理
- 会议管理
- 通知公告
- 客户管理
- 固定资产管理
- 薪资结算
- 员工年龄、学历、新增人数等统计图表
- 个人密码修改

## 数据库说明

数据库脚本位于：

```text
AttendanceManagerApi - idea/attendance_manager.sql
```

默认数据库名：

```text
attendance_manager
```

主要数据表包括：

| 表名 | 说明 |
| --- | --- |
| `employee` | 员工信息 |
| `department` | 部门信息 |
| `position` | 职位信息 |
| `check1` | 考勤记录 |
| `leave1` | 请假记录 |
| `leavetype` | 请假类型 |
| `task` | 审批任务 |
| `tasktype` | 任务类型 |
| `meeting` | 会议与通知 |
| `fixedassets` | 固定资产 |
| `fixedassettype` | 固定资产类型 |
| `salary` | 薪资记录 |
| `customer` | 客户信息 |
| `employeetype` | 员工类型 |

## 环境要求

- JDK 1.8
- Maven 3.x
- MySQL 5.7 或 8.x
- Node.js 6+（建议使用较旧的 Node 版本运行该 Vue 2 / Webpack 3 项目）
- npm 3+

## 后端启动

1. 创建 MySQL 数据库：

```sql
CREATE DATABASE attendance_manager DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

2. 导入数据库脚本：

```text
AttendanceManagerApi - idea/attendance_manager.sql
```

3. 修改后端数据库配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/attendance_manager?allowPublicKeyRetrieval=true&allowMultiQueries=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false
    username: root
    password: 123456
```

配置文件路径：

```text
AttendanceManagerApi - idea/src/main/resources/application.yaml
```

4. 启动后端服务：

```bash
cd "AttendanceManagerApi - idea"
mvn spring-boot:run
```

后端默认地址：

```text
http://localhost:9331
```

## 前端启动

进入前端目录：

```bash
cd AttendanceManagerVue
npm install
npm run dev
```

前端默认地址：

```text
http://localhost:9332
```

前端请求后端的地址配置在：

```text
AttendanceManagerVue/src/axios/axios.js
```

默认后端地址：

```javascript
http://localhost:9331
```

## 常用接口

| 接口前缀 | 说明 |
| --- | --- |
| `/login` | 登录 |
| `/employee` | 员工管理 |
| `/employeeType` | 员工类型 |
| `/department` | 部门管理 |
| `/position` | 职位管理 |
| `/check` | 考勤管理 |
| `/leave` | 请假管理 |
| `/leaveType` | 请假类型 |
| `/task` | 审批任务 |
| `/meeting` | 会议与通知 |
| `/fixedasset` | 固定资产 |
| `/fixedassetType` | 固定资产类型 |
| `/customer` | 客户管理 |
| `/salary` | 薪资管理 |
| `/enum` | 枚举/配置数据 |

## 注意事项

- 后端默认端口为 `9331`，前端默认端口为 `9332`。
- 前端项目依赖较旧，若使用较新的 Node.js 版本安装或启动失败，建议切换到兼容旧版 Webpack 的 Node.js 环境。
- 项目中部分中文内容可能存在编码显示异常，维护时建议统一使用 UTF-8 编码。
- 当前登录逻辑较简单，主要适合学习、课程设计或本地演示场景。
- `.gitignore` 已排除 `node_modules/`、`target/`、`dist/` 等依赖和构建产物。

## 许可证

本项目仅用于学习与交流。如需用于其他用途，请自行确认代码来源、依赖许可和数据安全要求。
