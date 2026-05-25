# 朝夕考勤系统

朝夕考勤系统是一个前后端分离的人事考勤管理项目，包含员工、部门、岗位、考勤、补卡、请假、审批、固定资产、客户、通知、工资与统计分析等模块。

前端使用 Vue 3 + Vite，后端使用 Spring Boot + MyBatis，数据库为 MySQL。

## 项目结构

```text
AttendanceManager/
├─ AttendanceManagerApi - idea/         # 后端 Spring Boot
│  ├─ src/main/java/com/zpark/sb/
│  │  ├─ controller/                    # 接口层
│  │  ├─ service/                       # 业务层
│  │  ├─ dao/                           # MyBatis DAO 接口
│  │  ├─ entity/                        # 实体对象
│  │  └─ config/                        # 认证、跨域、异常处理、启动兼容逻辑
│  ├─ src/main/resources/
│  │  ├─ application.yaml
│  │  └─ com/zpark/sb/mapper/*.xml
│  ├─ attendance_manager.sql
│  └─ pom.xml
└─ AttendanceManagerVue/                # 前端 Vue 3 + Vite
   ├─ src/
   │  ├─ api/
   │  ├─ axios/
   │  ├─ pages/
   │  ├─ router/
   │  ├─ stores/
   │  ├─ styles/
   │  └─ utils/
   ├─ package.json
   └─ vite.config.js
```

## 技术栈

- 后端：Java 21、Spring Boot 3.5.13、MyBatis 3.0.4、MySQL 8、EasyExcel、Maven
- 前端：Vue 3、Vite 8、Vue Router 4、Element Plus、Axios、AntV G2

## 环境要求

- JDK 21
- Maven 3.9+
- MySQL 8.x
- Node.js >= 18
- npm >= 9

## 快速启动

1. 创建数据库并导入脚本

```sql
CREATE DATABASE attendance_manager DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

导入：

```text
AttendanceManagerApi - idea/attendance_manager.sql
```

2. 修改后端数据库连接（如需要）

文件：

```text
AttendanceManagerApi - idea/src/main/resources/application.yaml
```

3. 启动后端

```bash
cd "AttendanceManagerApi - idea"
mvn spring-boot:run
```

默认地址：`http://localhost:9331`

4. 启动前端

```bash
cd AttendanceManagerVue
npm install
npm run dev
```

默认地址：`http://localhost:9332`

## 构建命令

- 后端打包（跳过测试）：`mvn clean package -DskipTests`
- 后端完整构建：`mvn clean package`
- 前端构建：`npm run build`
- 前端预览：`npm run preview`

## 配置说明

- 前端 API 基地址默认取 `VITE_API_BASE_URL`，未配置时使用 `http://localhost:9331`
- OSS 配置在 `application.yaml` 的 `aliyun.oss.*`
- 默认考勤时间配置在 `attendance.on-time` 与 `attendance.off-time`

## 常用接口前缀

- `/login` 登录注册
- `/employee` 员工管理
- `/department` 部门管理
- `/position` 岗位管理
- `/check` 考勤管理
- `/checkRepair` 补卡申请
- `/leave` 请假管理
- `/task` 审批任务
- `/fixedasset` 固定资产
- `/customer` 客户管理
- `/meeting` 会议与公告
- `/salary` 薪资管理
- `/statistics` 统计分析
- `/notification` 消息通知
- `/file` 文件上传

## 已知问题与排查

1. 后端构建失败但编译通过  
通常是测试失败导致（`mvn clean package` 会跑测试），可先用 `-DskipTests` 确认主代码可打包。

2. `Unknown column 'a.avatar' in 'field list'`  
这是数据库结构落后于代码导致。脚本最新版已包含 `employee.avatar` 字段。项目启动时也加入了兼容检查，会在缺失时尝试自动补列。

3. IDEA 提示 `OSS/ObjectMetadata` 找不到符号  
通常是 Maven 依赖未刷新。执行 Maven Reimport 或命令行 `mvn -U clean compile -DskipTests`。

## 文档

详细设计与使用说明见：

- `项目详细讲解.md`

## 许可证

本项目用于学习与交流，请在使用前自行评估数据与安全要求。
