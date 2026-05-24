<template>
  <section class="statistics-page">
    <PageHeader kicker="Analytics" title="统计分析" description="按学历、年龄、入职趋势和补卡异常查看组织运行状态。">
      <template #actions>
        <el-date-picker v-model="repairMonth" type="month" value-format="YYYY-MM" placeholder="补卡统计月份" />
        <el-button type="primary" @click="loadRepairStats">刷新补卡统计</el-button>
      </template>
    </PageHeader>

    <div class="metric-grid">
      <MetricCard label="补卡申请" :value="repairStats.total" hint="次" />
      <MetricCard label="待审批" :value="repairStats.pending" hint="次" />
      <MetricCard label="已通过" :value="repairStats.approved" hint="次" />
      <MetricCard label="高频异常员工" :value="topEmployeeName" hint="TOP 1" />
    </div>

    <div class="chart-grid">
      <el-card shadow="never">
        <template #header>
          <strong>补卡类型分布</strong>
        </template>
        <div id="repairTypes" class="chart"></div>
      </el-card>

      <el-card shadow="never">
        <template #header>
          <strong>高频异常员工</strong>
        </template>
        <el-table :data="repairStats.topEmployees" border fit highlight-current-row>
          <el-table-column prop="employeeName" label="员工" min-width="120" />
          <el-table-column prop="employeeNumber" label="编号" width="110" />
          <el-table-column prop="count" label="补卡次数" width="110" />
          <el-table-column prop="pending" label="待审批" width="100" />
        </el-table>
      </el-card>

      <el-card shadow="never">
        <template #header>
          <strong>员工学历分布</strong>
        </template>
        <div id="education" class="chart"></div>
      </el-card>

      <el-card shadow="never">
        <template #header>
          <strong>员工年龄分布</strong>
        </template>
        <div id="age" class="chart"></div>
      </el-card>

      <el-card shadow="never" class="wide">
        <template #header>
          <strong>近半年入职趋势</strong>
        </template>
        <div id="newcomers" class="chart trend"></div>
      </el-card>
    </div>
  </section>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { Chart } from '@antv/g2'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/common/PageHeader.vue'
import MetricCard from '@/components/common/MetricCard.vue'
import { getAgeStats, getCheckRepairStats, getEducationStats, getNewEmployeeStats } from '@/api/statistics'

const charts = reactive({
  repairTypes: null,
  education: null,
  age: null,
  newcomers: null
})

const education = ref([])
const age = ref([])
const newcomers = ref([])
const repairMonth = ref(getMonthText(new Date()))
const repairStats = reactive(createRepairStats())
const topEmployeeName = ref('-')

function destroyChart(name) {
  if (charts[name]) {
    charts[name].destroy()
    charts[name] = null
  }
}

function renderEducation() {
  const container = document.getElementById('education')
  if (!container) return
  destroyChart('education')

  const chart = new Chart({
    container: 'education',
    autoFit: true,
    height: 330
  })
  chart.coordinate('theta', { radius: 0.75 })
  chart.data(education.value)
  chart.scale('percent', {
    formatter: (value) => `${Number(value) * 100}%`
  })
  chart.tooltip({ showTitle: false, showMarkers: false })
  chart.interval().position('percent').color('item').adjust('stack')
  chart.interaction('element-active')
  chart.render()
  charts.education = chart
}

function renderRepairTypes() {
  const container = document.getElementById('repairTypes')
  if (!container) return
  destroyChart('repairTypes')

  const chart = new Chart({
    container: 'repairTypes',
    autoFit: true,
    height: 330,
    padding: [28, 20, 42, 42]
  })
  chart.data(repairStats.typeItems)
  chart.axis('value', false)
  chart.tooltip({ showMarkers: false })
  chart.interval().position('type*value').color('type')
  chart.interaction('element-active')
  chart.render()
  charts.repairTypes = chart
}

function renderAge() {
  const container = document.getElementById('age')
  if (!container) return
  destroyChart('age')

  const chart = new Chart({
    container: 'age',
    autoFit: true,
    height: 330,
    padding: [28, 20, 42, 42]
  })
  chart.data(age.value)
  chart.axis('value', false)
  chart.tooltip({ showMarkers: false })
  chart.interval().position('type*value').color('type')
  chart.interaction('element-active')
  chart.render()
  charts.age = chart
}

function renderNewcomers() {
  const container = document.getElementById('newcomers')
  if (!container) return
  destroyChart('newcomers')

  const chart = new Chart({
    container: 'newcomers',
    autoFit: true,
    height: 320,
    padding: [28, 30, 42, 48]
  })
  chart.data(newcomers.value)
  chart.axis('time', { tickLine: null })
  chart.tooltip({ showMarkers: false })
  chart.interval().position('time*value').color('time')
  chart.interaction('active-region')
  chart.render()
  charts.newcomers = chart
}

async function loadAll() {
  try {
    const [educationData, ageData, newData, repairData] = await Promise.all([
      getEducationStats(),
      getAgeStats(),
      getNewEmployeeStats(),
      getCheckRepairStats({ month: repairMonth.value })
    ])
    education.value = Array.isArray(educationData) ? educationData : []
    age.value = Array.isArray(ageData) ? ageData : []
    newcomers.value = Array.isArray(newData) ? newData : []
    setRepairStats(repairData)
    await nextTick()
    renderRepairTypes()
    renderEducation()
    renderAge()
    renderNewcomers()
  } catch (error) {
    ElMessage.error('统计数据加载失败')
  }
}

async function loadRepairStats() {
  try {
    const data = await getCheckRepairStats({ month: repairMonth.value })
    setRepairStats(data)
    await nextTick()
    renderRepairTypes()
  } catch (error) {
    ElMessage.error('补卡统计加载失败')
  }
}

function createRepairStats() {
  return {
    total: 0,
    pending: 0,
    approved: 0,
    rejected: 0,
    revoked: 0,
    topEmployees: [],
    typeItems: []
  }
}

function setRepairStats(data = {}) {
  Object.assign(repairStats, createRepairStats(), data || {})
  topEmployeeName.value = repairStats.topEmployees?.[0]?.employeeName || '-'
}

function getMonthText(date) {
  const pad = (value) => String(value).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}`
}

onMounted(loadAll)

onBeforeUnmount(() => {
  destroyChart('repairTypes')
  destroyChart('education')
  destroyChart('age')
  destroyChart('newcomers')
})
</script>

<style scoped>
.statistics-page {
  display: grid;
  gap: 16px;
}

.chart-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
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
