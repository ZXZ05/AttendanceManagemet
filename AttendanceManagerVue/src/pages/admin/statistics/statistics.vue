<template>
  <section class="statistics-page">
    <PageHeader kicker="Analytics" title="统计分析" description="按学历、年龄和入职趋势查看员工结构变化。" />

    <div class="chart-grid">
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
import { getAgeStats, getEducationStats, getNewEmployeeStats } from '@/api/statistics'

const charts = reactive({
  education: null,
  age: null,
  newcomers: null
})

const education = ref([])
const age = ref([])
const newcomers = ref([])

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
    const [educationData, ageData, newData] = await Promise.all([
      getEducationStats(),
      getAgeStats(),
      getNewEmployeeStats()
    ])
    education.value = Array.isArray(educationData) ? educationData : []
    age.value = Array.isArray(ageData) ? ageData : []
    newcomers.value = Array.isArray(newData) ? newData : []
    await nextTick()
    renderEducation()
    renderAge()
    renderNewcomers()
  } catch (error) {
    ElMessage.error('统计数据加载失败')
  }
}

onMounted(loadAll)

onBeforeUnmount(() => {
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
