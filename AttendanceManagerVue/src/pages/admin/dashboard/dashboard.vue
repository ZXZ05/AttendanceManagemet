<template>
  <section class="dashboard-page">
    <PageHeader
      kicker="Admin Insight"
      title="管理端仪表盘"
      :description="`${monthText} 管理视图：人员规模、考勤健康和补卡审批总览`"
    >
      <template #actions>
        <el-date-picker v-model="monthText" type="month" value-format="YYYY-MM" placeholder="选择月份" />
        <el-button type="primary" @click="loadDashboard">刷新数据</el-button>
      </template>
    </PageHeader>

    <el-card shadow="never" class="quick-card">
      <div class="quick-wrap">
        <div>
          <p class="quick-kicker">Quick Access</p>
          <strong>补卡待办快捷入口</strong>
          <p class="quick-desc">当前待审批补卡 {{ metrics.pendingRepairs }} 条，可直接进入审批任务列表。</p>
        </div>
        <div class="quick-actions">
          <el-button type="warning" @click="goTo('/task')">进入审批待办</el-button>
          <el-button @click="goTo('/statistics')">查看补卡分析</el-button>
        </div>
      </div>
    </el-card>

    <el-card v-if="canImportExcel" shadow="never" class="import-card">
      <template #header>
        <div class="card-header">
          <strong>Excel 批量导入</strong>
          <el-tag effect="light">管理员</el-tag>
        </div>
      </template>
      <div class="import-grid">
        <article v-for="item in importItems" :key="item.key" class="import-item">
          <h4>{{ item.title }}</h4>
          <p class="import-template">{{ item.template }}</p>
          <div class="import-actions">
            <el-button link type="primary" @click="downloadTemplate(item.key, item.title)">下载模板</el-button>
            <el-upload
              :show-file-list="false"
              accept=".xlsx,.xls"
              :http-request="(options) => handleImportRequest(item.key, options)"
            >
              <el-button type="primary" plain :loading="importing[item.key]">上传并导入</el-button>
            </el-upload>
          </div>
          <p v-if="importResults[item.key]" class="import-result">
            总 {{ importResults[item.key].total }}，成功 {{ importResults[item.key].success }}，跳过 {{ importResults[item.key].skipped }}
          </p>
          <p v-if="importResults[item.key]?.errors?.length" class="import-error">
            {{ importResults[item.key].errors[0] }}
          </p>
        </article>
      </div>
    </el-card>

    <div class="metric-grid">
      <MetricCard label="在职员工" :value="metrics.employeeCount" hint="人" />
      <MetricCard label="部门数量" :value="metrics.departmentCount" hint="个" />
      <MetricCard label="月度出勤率" :value="metrics.attendanceRateText" hint="全员平均" />
      <MetricCard label="待审批补卡" :value="metrics.pendingRepairs" hint="条" />
      <MetricCard label="月度迟到次数" :value="metrics.lateTimes" hint="次" />
      <MetricCard label="月度早退次数" :value="metrics.leaveEarlyTimes" hint="次" />
    </div>

    <el-card shadow="never" class="today-card">
      <template #header>
        <div class="card-header">
          <strong>今日实时出勤（{{ todayText }}）</strong>
          <el-tag effect="light">实时快照</el-tag>
        </div>
      </template>
      <div class="today-grid">
        <article class="today-item">
          <p>已上班打卡</p>
          <strong>{{ todayStats.onChecked }}</strong>
        </article>
        <article class="today-item">
          <p>已下班打卡</p>
          <strong>{{ todayStats.offChecked }}</strong>
        </article>
        <article class="today-item">
          <p>请假/备注</p>
          <strong>{{ todayStats.onLeave }}</strong>
        </article>
        <article class="today-item is-warning">
          <p>未上班打卡</p>
          <strong>{{ todayStats.missingOn }}</strong>
        </article>
      </div>
    </el-card>

    <div class="chart-grid">
      <el-card shadow="never">
        <template #header>
          <strong>部门人数分布</strong>
        </template>
        <div id="deptHeadcountChart" class="chart"></div>
      </el-card>

      <el-card shadow="never">
        <template #header>
          <strong>近六月入职趋势</strong>
        </template>
        <div id="newcomerTrendChart" class="chart"></div>
      </el-card>
    </div>

    <div class="table-grid">
      <el-card shadow="never">
        <template #header>
          <strong>部门考勤健康排行</strong>
        </template>
        <el-table :data="departmentRanking" border fit highlight-current-row>
          <el-table-column prop="departmentName" label="部门" min-width="150" />
          <el-table-column prop="employeeCount" label="人数" width="90" />
          <el-table-column prop="attendanceRateText" label="出勤率" width="110" />
          <el-table-column prop="lateDays" label="迟到" width="90" />
          <el-table-column prop="leaveEarlyDays" label="早退" width="90" />
          <el-table-column prop="leaveDays" label="请假天数" width="110" />
        </el-table>
      </el-card>

      <el-card shadow="never">
        <template #header>
          <strong>高频补卡员工</strong>
        </template>
        <el-table :data="repairStats.topEmployees" border fit highlight-current-row>
          <el-table-column prop="employeeName" label="员工" min-width="130" />
          <el-table-column prop="employeeNumber" label="工号" width="120" />
          <el-table-column prop="count" label="补卡次数" width="110" />
          <el-table-column prop="pending" label="待审批" width="100" />
        </el-table>
      </el-card>
    </div>
  </section>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Chart } from '@antv/g2'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/common/PageHeader.vue'
import MetricCard from '@/components/common/MetricCard.vue'
import { getDepartmentList } from '@/api/admin'
import { findCheckByMonth, getCheckList, getCheckRepairStats } from '@/api/attendance'
import {
  downloadImportTemplate,
  importCustomerExcel,
  importDepartmentExcel,
  importEmployeeExcel,
  importFixedassetExcel,
  importPositionExcel
} from '@/api/excelImport'
import { getEmployeeList, getNewEmployeeStats } from '@/api/employee'
import { hasPermission, PERMISSION } from '@/utils/auth'

const router = useRouter()

const charts = reactive({
  deptHeadcountChart: null,
  newcomerTrendChart: null
})

const monthText = ref(getMonthText(new Date()))
const todayText = ref(getDateText(new Date()))
const departmentRanking = ref([])
const departmentHeadcount = ref([])
const newcomerTrend = ref([])
const repairStats = reactive(createRepairStats())
const metrics = reactive({
  employeeCount: 0,
  departmentCount: 0,
  attendanceRateText: '0%',
  pendingRepairs: 0,
  lateTimes: 0,
  leaveEarlyTimes: 0
})
const todayStats = reactive({
  onChecked: 0,
  offChecked: 0,
  onLeave: 0,
  missingOn: 0
})
const importItems = [
  {
    key: 'employee',
    title: '员工',
    template: '列顺序: 工号,姓名,性别,手机号,部门编号,岗位编号,生日,入职日期,身份证号,学历,婚姻,地址'
  },
  {
    key: 'department',
    title: '部门',
    template: '列顺序: 部门编号,部门名称'
  },
  {
    key: 'position',
    title: '岗位',
    template: '列顺序: 岗位编号,岗位名称,月薪,部门编号,岗位类别编号'
  },
  {
    key: 'customer',
    title: '客户',
    template: '列顺序: 客户编号,客户名称,类型,电话,地址,备注,归属人工号(可空)'
  },
  {
    key: 'fixedasset',
    title: '固定资产',
    template: '列顺序: 资产编号,资产名称,资产类别编号,价格,归属人工号(可空)'
  }
]
const importHandlers = {
  employee: importEmployeeExcel,
  department: importDepartmentExcel,
  position: importPositionExcel,
  customer: importCustomerExcel,
  fixedasset: importFixedassetExcel
}
const importing = reactive({
  employee: false,
  department: false,
  position: false,
  customer: false,
  fixedasset: false
})
const importResults = reactive({
  employee: null,
  department: null,
  position: null,
  customer: null,
  fixedasset: null
})
const canImportExcel = hasPermission(PERMISSION.EXCEL_IMPORT)

function createRepairStats() {
  return {
    pending: 0,
    topEmployees: []
  }
}

function getMonthText(date) {
  const pad = (value) => String(value).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}`
}

function getDateText(date) {
  const pad = (value) => String(value).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
}

function safeNumber(value) {
  const number = Number(value)
  return Number.isFinite(number) ? number : 0
}

function goTo(path) {
  router.push(path)
}

async function downloadTemplate(type, title) {
  try {
    const blob = await downloadImportTemplate(type)
    const url = window.URL.createObjectURL(blob)
    const anchor = document.createElement('a')
    anchor.href = url
    anchor.download = `${title}导入模板.xlsx`
    document.body.appendChild(anchor)
    anchor.click()
    document.body.removeChild(anchor)
    window.URL.revokeObjectURL(url)
  } catch (error) {
    ElMessage.error('模板下载失败，请稍后重试')
  }
}

async function handleImportRequest(type, options) {
  const request = importHandlers[type]
  if (!request) {
    ElMessage.error('暂不支持该导入类型')
    options.onError?.(new Error('unsupported import type'))
    return
  }
  importing[type] = true
  try {
    const result = await request(options.file)
    importResults[type] = result
    ElMessage.success(`导入完成：成功 ${result.success} 条，跳过 ${result.skipped} 条`)
    await loadDashboard()
    options.onSuccess?.(result)
  } catch (error) {
    ElMessage.error(error?.message || '导入失败，请检查模板和数据')
    options.onError?.(error || new Error('import failed'))
  } finally {
    importing[type] = false
  }
}

function buildDepartmentRanking(employeeList = [], checkList = []) {
  const checkMap = new Map(
    (Array.isArray(checkList) ? checkList : []).map((item) => [item.employeeID, item])
  )
  const departmentMap = new Map()

  ;(Array.isArray(employeeList) ? employeeList : []).forEach((employee) => {
    const departmentName = employee.departmentName || '未分配部门'
    const summary = checkMap.get(employee.number) || {}
    const record = departmentMap.get(departmentName) || {
      departmentName,
      employeeCount: 0,
      totalWorkDays: 0,
      totalCheckDays: 0,
      lateDays: 0,
      leaveEarlyDays: 0,
      leaveDays: 0
    }

    record.employeeCount += 1
    record.totalWorkDays += safeNumber(summary.workDays)
    record.totalCheckDays += safeNumber(summary.checkDays)
    record.lateDays += safeNumber(summary.lateDays)
    record.leaveEarlyDays += safeNumber(summary.leaveEarlyDays)
    record.leaveDays += safeNumber(summary.leaveDays)
    departmentMap.set(departmentName, record)
  })

  return Array.from(departmentMap.values())
    .map((item) => {
      const rate = item.totalWorkDays > 0 ? (item.totalCheckDays / item.totalWorkDays) * 100 : 0
      return {
        ...item,
        attendanceRate: rate,
        attendanceRateText: `${rate.toFixed(1)}%`
      }
    })
    .sort((left, right) => {
      if (right.attendanceRate !== left.attendanceRate) {
        return right.attendanceRate - left.attendanceRate
      }
      return left.lateDays - right.lateDays
    })
}

function calcTodayStats(employeeList = [], checkRecords = []) {
  const employeeCount = Array.isArray(employeeList) ? employeeList.length : 0
  const records = (Array.isArray(checkRecords) ? checkRecords : []).filter((item) => item.date === todayText.value)

  const onChecked = records.filter((item) => item.checkOnTime).length
  const offChecked = records.filter((item) => item.checkOffTime).length
  const onLeave = records.filter((item) => item.remarks).length
  const missingOn = Math.max(employeeCount - onChecked - onLeave, 0)

  todayStats.onChecked = onChecked
  todayStats.offChecked = offChecked
  todayStats.onLeave = onLeave
  todayStats.missingOn = missingOn
}

function setSummaryMetrics(employeeList, departmentList, checkList, repairData) {
  metrics.employeeCount = Array.isArray(employeeList) ? employeeList.length : 0
  metrics.departmentCount = Array.isArray(departmentList) ? departmentList.length : 0

  const checks = Array.isArray(checkList) ? checkList : []
  const totalWorkDays = checks.reduce((sum, item) => sum + safeNumber(item.workDays), 0)
  const totalCheckDays = checks.reduce((sum, item) => sum + safeNumber(item.checkDays), 0)
  const attendanceRate = totalWorkDays > 0 ? (totalCheckDays / totalWorkDays) * 100 : 0
  metrics.attendanceRateText = `${attendanceRate.toFixed(1)}%`
  metrics.lateTimes = checks.reduce((sum, item) => sum + safeNumber(item.lateDays), 0)
  metrics.leaveEarlyTimes = checks.reduce((sum, item) => sum + safeNumber(item.leaveEarlyDays), 0)
  metrics.pendingRepairs = safeNumber(repairData?.pending)
}

function destroyChart(name) {
  if (charts[name]) {
    charts[name].destroy()
    charts[name] = null
  }
}

function renderDepartmentChart() {
  const container = document.getElementById('deptHeadcountChart')
  if (!container) return
  destroyChart('deptHeadcountChart')

  const chart = new Chart({
    container: 'deptHeadcountChart',
    autoFit: true,
    height: 320,
    padding: [28, 20, 42, 48]
  })
  chart.data(departmentHeadcount.value)
  chart.axis('value', false)
  chart.tooltip({ showMarkers: false })
  chart.interval().position('name*value').color('name')
  chart.interaction('element-active')
  chart.render()
  charts.deptHeadcountChart = chart
}

function renderNewcomerTrendChart() {
  const container = document.getElementById('newcomerTrendChart')
  if (!container) return
  destroyChart('newcomerTrendChart')

  const chart = new Chart({
    container: 'newcomerTrendChart',
    autoFit: true,
    height: 320,
    padding: [28, 30, 42, 50]
  })
  chart.data(newcomerTrend.value)
  chart.axis('time', { tickLine: null })
  chart.line().position('time*value').color('#41d9c2')
  chart.point().position('time*value').color('#3c82ff').size(4)
  chart.interaction('active-region')
  chart.render()
  charts.newcomerTrendChart = chart
}

async function loadDashboard() {
  try {
    todayText.value = getDateText(new Date())
    const payload = { month: monthText.value, date: monthText.value }
    const [employeeList, departmentList, checkList, repairData, newcomerData, monthRecords] = await Promise.all([
      getEmployeeList(),
      getDepartmentList(),
      getCheckList(payload),
      getCheckRepairStats({ month: monthText.value }),
      getNewEmployeeStats(),
      findCheckByMonth(payload)
    ])

    const ranking = buildDepartmentRanking(employeeList, checkList)
    departmentRanking.value = ranking
    departmentHeadcount.value = ranking
      .map((item) => ({ name: item.departmentName, value: item.employeeCount }))
      .sort((left, right) => right.value - left.value)

    newcomerTrend.value = (Array.isArray(newcomerData) ? newcomerData : [])
      .map((item) => ({ time: item.time, value: safeNumber(item.value) }))
      .sort((left, right) => (left.time || '').localeCompare(right.time || ''))

    Object.assign(repairStats, createRepairStats(), repairData || {})
    setSummaryMetrics(employeeList, departmentList, checkList, repairData)
    calcTodayStats(employeeList, monthRecords)

    await nextTick()
    renderDepartmentChart()
    renderNewcomerTrendChart()
  } catch (error) {
    ElMessage.error('管理仪表盘数据加载失败')
  }
}

onMounted(loadDashboard)

onBeforeUnmount(() => {
  destroyChart('deptHeadcountChart')
  destroyChart('newcomerTrendChart')
})
</script>

<style scoped>
.dashboard-page {
  display: grid;
  gap: 16px;
}

.quick-card {
  overflow: hidden;
}

.quick-wrap {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.quick-kicker {
  margin: 0;
  color: var(--am-accent);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.quick-wrap strong {
  display: block;
  margin-top: 6px;
  font-size: 20px;
}

.quick-desc {
  margin: 8px 0 0;
  color: var(--am-muted);
}

.quick-actions {
  display: flex;
  gap: 10px;
}

.import-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(230px, 1fr));
  gap: 12px;
}

.import-item {
  padding: 12px;
  border: 1px solid var(--am-line-soft);
  border-radius: 12px;
  background: rgba(12, 21, 40, 0.5);
}

.import-item h4 {
  margin: 0;
  font-size: 16px;
}

.import-template {
  min-height: 44px;
  margin: 8px 0 10px;
  color: var(--am-muted);
  font-size: 12px;
  line-height: 1.5;
}

.import-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.import-result,
.import-error {
  margin: 8px 0 0;
  font-size: 12px;
}

.import-result {
  color: var(--am-success);
}

.import-error {
  color: var(--am-warning);
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
}

.today-card {
  border-color: rgba(65, 217, 194, 0.4) !important;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.today-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.today-item {
  padding: 14px;
  border: 1px solid var(--am-line-soft);
  border-radius: 12px;
  background: rgba(12, 21, 40, 0.55);
}

.today-item p {
  margin: 0;
  color: var(--am-muted);
  font-size: 13px;
}

.today-item strong {
  display: block;
  margin-top: 8px;
  font-size: 30px;
}

.today-item.is-warning {
  border-color: rgba(245, 185, 79, 0.42);
  background: rgba(245, 185, 79, 0.1);
}

.chart-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.table-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.chart {
  width: 100%;
  min-height: 320px;
}

@media (max-width: 1100px) {
  .quick-wrap,
  .chart-grid,
  .table-grid,
  .today-grid {
    grid-template-columns: 1fr;
  }

  .quick-wrap {
    display: grid;
  }
}

@media (max-width: 720px) {
  :deep(.el-date-editor.el-input) {
    width: 100%;
  }

  .quick-actions {
    flex-direction: column;
  }

  .import-actions {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
