<template>
  <section class="statistics-page">
    <PageHeader kicker="Analytics" title="统计分析" description="查看组织结构、出勤趋势、请假占比、资产采购与薪资支出。">
      <template #actions>
        <el-date-picker v-model="query.month" type="month" value-format="YYYY-MM" placeholder="统计截止月份" />
        <el-select v-model="query.months" class="span-select" placeholder="统计月数">
          <el-option :value="3" label="近 3 个月" />
          <el-option :value="6" label="近 6 个月" />
          <el-option :value="12" label="近 12 个月" />
        </el-select>
        <el-button type="primary" @click="loadOverview">刷新统计</el-button>
      </template>
    </PageHeader>

    <div class="metric-grid">
      <MetricCard label="部门数量" :value="departmentCount" hint="个" />
      <MetricCard label="员工总人数" :value="employeeCount" hint="人" />
      <MetricCard label="总出勤人次" :value="attendanceTotal" hint="次" />
      <MetricCard label="薪资支出总额" :value="salaryTotalText" hint="元" />
    </div>

    <div class="chart-grid">
      <el-card shadow="never">
        <template #header>
          <strong>部门人数分布</strong>
        </template>
        <div id="departmentChart" class="chart"></div>
      </el-card>

      <el-card shadow="never">
        <template #header>
          <strong>请假类型占比（小时）</strong>
        </template>
        <div id="leaveRatioChart" class="chart"></div>
      </el-card>

      <el-card shadow="never" class="wide">
        <template #header>
          <strong>月度出勤趋势</strong>
        </template>
        <div id="attendanceChart" class="chart trend"></div>
      </el-card>

      <el-card shadow="never" class="wide">
        <template #header>
          <strong>迟到早退趋势</strong>
        </template>
        <div id="lateEarlyChart" class="chart trend"></div>
      </el-card>

      <el-card shadow="never" class="wide">
        <template #header>
          <strong>资产采购金额趋势</strong>
        </template>
        <div id="assetChart" class="chart trend"></div>
      </el-card>

      <el-card shadow="never" class="wide">
        <template #header>
          <strong>薪资支出趋势</strong>
        </template>
        <div id="salaryChart" class="chart trend"></div>
      </el-card>
    </div>
  </section>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, reactive } from 'vue'
import { Chart } from '@antv/g2'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/common/PageHeader.vue'
import MetricCard from '@/components/common/MetricCard.vue'
import { getStatisticsOverview } from '@/api/statistics'

const query = reactive({
  month: getMonthText(new Date()),
  months: 6
})

const charts = reactive({
  departmentChart: null,
  leaveRatioChart: null,
  attendanceChart: null,
  lateEarlyChart: null,
  assetChart: null,
  salaryChart: null
})

const overview = reactive({
  range: {
    startMonth: '',
    endMonth: '',
    months: 6
  },
  departmentDistribution: [],
  attendanceTrend: [],
  lateEarlyTrend: [],
  leaveTypeRatio: [],
  assetAmountTrend: [],
  salaryAmountTrend: []
})

const departmentCount = computed(() => overview.departmentDistribution.length)
const employeeCount = computed(() => overview.departmentDistribution.reduce((sum, item) => sum + Number(item.value || 0), 0))
const attendanceTotal = computed(() => overview.attendanceTrend.reduce((sum, item) => sum + Number(item.value || 0), 0))
const salaryTotal = computed(() => overview.salaryAmountTrend.reduce((sum, item) => sum + Number(item.value || 0), 0))
const salaryTotalText = computed(() => formatAmount(salaryTotal.value))

function getMonthText(date) {
  const pad = (value) => String(value).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}`
}

function formatAmount(value) {
  return Number(value || 0).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

function destroyChart(name) {
  if (charts[name]) {
    charts[name].destroy()
    charts[name] = null
  }
}

function destroyAllCharts() {
  destroyChart('departmentChart')
  destroyChart('leaveRatioChart')
  destroyChart('attendanceChart')
  destroyChart('lateEarlyChart')
  destroyChart('assetChart')
  destroyChart('salaryChart')
}

function renderDepartmentChart() {
  const container = document.getElementById('departmentChart')
  if (!container) return
  destroyChart('departmentChart')

  const chart = new Chart({
    container: 'departmentChart',
    autoFit: true,
    height: 320,
    padding: [28, 24, 42, 48]
  })
  chart.data(overview.departmentDistribution)
  chart.axis('value', { grid: { line: { style: { stroke: 'rgba(255,255,255,0.1)' } } } })
  chart.tooltip({ showMarkers: false })
  chart.interval().position('type*value').color('type')
  chart.interaction('element-active')
  chart.render()
  charts.departmentChart = chart
}

function renderLeaveRatioChart() {
  const container = document.getElementById('leaveRatioChart')
  if (!container) return
  destroyChart('leaveRatioChart')

  const chart = new Chart({
    container: 'leaveRatioChart',
    autoFit: true,
    height: 320
  })
  chart.coordinate('theta', { radius: 0.75 })
  chart.data(overview.leaveTypeRatio)
  chart.scale('percent', {
    formatter: (value) => `${Math.round(Number(value || 0) * 100)}%`
  })
  chart.tooltip({ showTitle: false, showMarkers: false })
  chart.interval().position('percent').color('type').adjust('stack')
  chart.interaction('element-active')
  chart.render()
  charts.leaveRatioChart = chart
}

function renderAttendanceChart() {
  const container = document.getElementById('attendanceChart')
  if (!container) return
  destroyChart('attendanceChart')

  const chart = new Chart({
    container: 'attendanceChart',
    autoFit: true,
    height: 320,
    padding: [28, 30, 42, 48]
  })
  chart.data(overview.attendanceTrend)
  chart.axis('value', { grid: { line: { style: { stroke: 'rgba(255,255,255,0.1)' } } } })
  chart.tooltip({ showMarkers: true })
  chart.line().position('time*value').color('#5b8ff9')
  chart.point().position('time*value').color('#5b8ff9').shape('circle')
  chart.interaction('active-region')
  chart.render()
  charts.attendanceChart = chart
}

function renderLateEarlyChart() {
  const container = document.getElementById('lateEarlyChart')
  if (!container) return
  destroyChart('lateEarlyChart')

  const data = overview.lateEarlyTrend.flatMap((item) => [
    {
      time: item.time,
      type: '迟到',
      value: Number(item.lateValue || 0)
    },
    {
      time: item.time,
      type: '早退',
      value: Number(item.earlyValue || 0)
    }
  ])

  const chart = new Chart({
    container: 'lateEarlyChart',
    autoFit: true,
    height: 320,
    padding: [28, 30, 42, 48]
  })
  chart.data(data)
  chart.axis('value', { grid: { line: { style: { stroke: 'rgba(255,255,255,0.1)' } } } })
  chart.tooltip({ showMarkers: true, shared: true })
  chart.line().position('time*value').color('type')
  chart.point().position('time*value').color('type')
  chart.interaction('active-region')
  chart.render()
  charts.lateEarlyChart = chart
}

function renderAmountChart(containerId, chartKey, titleData, colorHex) {
  const container = document.getElementById(containerId)
  if (!container) return
  destroyChart(chartKey)

  const chart = new Chart({
    container: containerId,
    autoFit: true,
    height: 320,
    padding: [28, 30, 42, 48]
  })
  chart.data(titleData)
  chart.axis('value', { grid: { line: { style: { stroke: 'rgba(255,255,255,0.1)' } } } })
  chart.tooltip({ showMarkers: true })
  chart.area().position('time*value').color(colorHex).shape('smooth')
  chart.line().position('time*value').color(colorHex).shape('smooth')
  chart.point().position('time*value').color(colorHex)
  chart.interaction('active-region')
  chart.render()
  charts[chartKey] = chart
}

function renderAllCharts() {
  renderDepartmentChart()
  renderLeaveRatioChart()
  renderAttendanceChart()
  renderLateEarlyChart()
  renderAmountChart('assetChart', 'assetChart', overview.assetAmountTrend, '#61d9a2')
  renderAmountChart('salaryChart', 'salaryChart', overview.salaryAmountTrend, '#f6bd16')
}

async function loadOverview() {
  try {
    const data = await getStatisticsOverview({
      month: query.month,
      months: query.months
    })
    Object.assign(overview, {
      range: data?.range || overview.range,
      departmentDistribution: Array.isArray(data?.departmentDistribution) ? data.departmentDistribution : [],
      attendanceTrend: Array.isArray(data?.attendanceTrend) ? data.attendanceTrend : [],
      lateEarlyTrend: Array.isArray(data?.lateEarlyTrend) ? data.lateEarlyTrend : [],
      leaveTypeRatio: Array.isArray(data?.leaveTypeRatio) ? data.leaveTypeRatio : [],
      assetAmountTrend: Array.isArray(data?.assetAmountTrend) ? data.assetAmountTrend : [],
      salaryAmountTrend: Array.isArray(data?.salaryAmountTrend) ? data.salaryAmountTrend : []
    })
    await nextTick()
    renderAllCharts()
  } catch (error) {
    ElMessage.error('统计数据加载失败')
  }
}

onMounted(loadOverview)

onBeforeUnmount(() => {
  destroyAllCharts()
})
</script>

<style scoped>
.statistics-page {
  display: grid;
  gap: 16px;
}

.span-select {
  width: 120px;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
}

.chart-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.wide {
  grid-column: 1 / -1;
}

.chart {
  width: 100%;
  min-height: 320px;
}

.trend {
  min-height: 300px;
}

@media (max-width: 900px) {
  .chart-grid {
    grid-template-columns: 1fr;
  }
}
</style>

