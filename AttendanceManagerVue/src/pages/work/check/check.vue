<template>
  <section class="work-page">
    <PageHeader kicker="Attendance Report" title="我的考勤" description="按月份查看出勤、请假、异常和工资条。">
      <template #actions>
        <el-date-picker v-model="checkMonth" type="month" value-format="YYYY-MM" placeholder="选择月份" />
        <el-button type="primary" @click="loadCheckInfo">查询</el-button>
      </template>
    </PageHeader>

    <div class="toolbar">
      <el-button type="primary" @click="openRecords">考勤记录</el-button>
      <el-button type="primary" @click="openSalary">查看工资条</el-button>
    </div>

    <div class="metric-grid">
      <MetricCard label="应出勤" :value="checkForm.workDays ?? 0" hint="天" />
      <MetricCard label="实际出勤" :value="checkForm.checkDays ?? 0" hint="天" />
      <MetricCard label="缺勤" :value="absenceDays" hint="天" />
      <MetricCard label="迟到" :value="checkForm.lateDays ?? 0" hint="次" />
      <MetricCard label="早退" :value="checkForm.leaveEarlyDays ?? 0" hint="次" />
      <MetricCard label="请假合计" :value="leaveHours" hint="小时" />
    </div>

    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <strong>请假明细（小时）</strong>
          <el-tag effect="light">{{ checkMonth }}</el-tag>
        </div>
      </template>
      <div class="detail-grid">
        <div v-for="item in leaveItems" :key="item.label" class="detail-item">
          <span>{{ item.label }}</span>
          <strong>{{ item.value }}</strong>
        </div>
      </div>
    </el-card>

    <FormDialog v-model="recordsVisible" title="全部考勤记录" width="820px">
      <el-table :data="tableData" border fit highlight-current-row>
        <el-table-column prop="date" label="日期" width="130" />
        <el-table-column prop="checkOnTime" label="签到时间" min-width="180" />
        <el-table-column prop="checkOnStatus" label="签到状态" width="120" />
        <el-table-column prop="checkOffTime" label="签退时间" min-width="180" />
        <el-table-column prop="checkOffStatus" label="签退状态" width="120" />
      </el-table>
      <template #footer>
        <el-button type="primary" @click="recordsVisible = false">关闭</el-button>
      </template>
    </FormDialog>

    <FormDialog v-model="salaryVisible" title="工资条" width="860px">
      <el-table :data="salaryTable" border fit highlight-current-row>
        <el-table-column prop="month" label="月份" width="110" />
        <el-table-column prop="monthlySalary" label="应发工资" />
        <el-table-column prop="workDays" label="应工作天数" />
        <el-table-column prop="checkDays" label="实际工作天数" />
        <el-table-column prop="leaveDays" label="请假天数" />
        <el-table-column prop="lateTimes" label="迟到次数" />
        <el-table-column prop="leaveEarlyTimes" label="早退次数" />
        <el-table-column prop="salary" label="实发工资" />
      </el-table>
      <template #footer>
        <el-button type="primary" @click="salaryVisible = false">关闭</el-button>
      </template>
    </FormDialog>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/common/PageHeader.vue'
import MetricCard from '@/components/common/MetricCard.vue'
import FormDialog from '@/components/common/FormDialog.vue'
import { findCheckByNumber, getCheckInfo } from '@/api/attendance'
import { findSalaryByNumberAndMonth } from '@/api/finance'
import { getLoginUsername } from '@/utils/auth'

const loginNumber = getLoginUsername() || ''
const checkMonth = ref(getMonthText(new Date()))
const recordsVisible = ref(false)
const salaryVisible = ref(false)
const tableData = ref([])
const salaryTable = ref([])
const checkForm = reactive(createCheckForm())

const leaveItems = computed(() => [
  { label: '事假', value: checkForm.leaveDays1 || 0 },
  { label: '婚假', value: checkForm.leaveDays2 || 0 },
  { label: '丧假', value: checkForm.leaveDays3 || 0 },
  { label: '产假', value: checkForm.leaveDays4 || 0 },
  { label: '陪产假', value: checkForm.leaveDays5 || 0 },
  { label: '病假', value: checkForm.leaveDays6 || 0 }
])

const leaveHours = computed(() => leaveItems.value.reduce((sum, item) => sum + Number(item.value || 0), 0))
const absenceDays = computed(() => Math.max(Number(checkForm.workDays || 0) - Number(checkForm.checkDays || 0), 0))

function getMonthText(date) {
  const pad = (value) => String(value).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}`
}

function createCheckForm() {
  return {
    employeeID: loginNumber,
    date: '',
    month: '',
    workDays: 0,
    checkDays: 0,
    lateDays: 0,
    leaveEarlyDays: 0,
    leaveDays1: 0,
    leaveDays2: 0,
    leaveDays3: 0,
    leaveDays4: 0,
    leaveDays5: 0,
    leaveDays6: 0
  }
}

function makePayload() {
  return {
    ...createCheckForm(),
    month: checkMonth.value,
    date: checkMonth.value
  }
}

async function loadCheckInfo() {
  try {
    const data = await getCheckInfo(makePayload())
    Object.assign(checkForm, createCheckForm(), data || {})
  } catch (error) {
    ElMessage.error('查询考勤信息失败')
  }
}

async function openRecords() {
  try {
    tableData.value = await findCheckByNumber(makePayload())
    recordsVisible.value = true
  } catch (error) {
    ElMessage.error('获取考勤记录失败')
  }
}

async function openSalary() {
  try {
    const data = await findSalaryByNumberAndMonth({
      employeeID: loginNumber,
      month: checkMonth.value
    })
    salaryTable.value = data ? [data] : []
    salaryVisible.value = true
  } catch (error) {
    ElMessage.error('获取工资条失败')
  }
}

onMounted(loadCheckInfo)
</script>

<style scoped>
.work-page {
  display: grid;
  gap: 16px;
}

.toolbar {
  display: flex;
  gap: 10px;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 12px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 10px;
}

.detail-item {
  padding: 14px;
  border: 1px solid var(--am-line-soft);
  border-radius: 10px;
  background: rgba(12, 21, 40, 0.55);
}

.detail-item span,
.detail-item strong {
  display: block;
}

.detail-item span {
  color: var(--am-muted);
}

.detail-item strong {
  margin-top: 6px;
  font-size: 22px;
}

:deep(.el-date-editor.el-input) {
  width: 160px;
}

@media (max-width: 720px) {
  .toolbar {
    flex-direction: column;
  }
}
</style>
