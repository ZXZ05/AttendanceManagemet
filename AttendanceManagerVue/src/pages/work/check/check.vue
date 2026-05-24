<template>
  <section class="work-page">
    <PageHeader kicker="Attendance Report" title="我的考勤" description="按月份查看出勤、请假、异常和工资条。">
      <template #actions>
        <el-date-picker v-model="checkMonth" type="month" value-format="YYYY-MM" placeholder="选择月份" />
        <el-button type="primary" @click="loadDashboardData">查询</el-button>
      </template>
    </PageHeader>

    <div class="toolbar">
      <el-button type="primary" @click="openRecords">考勤记录</el-button>
      <el-button type="warning" @click="openRepairCreate()">补卡申请</el-button>
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

    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <strong>考勤异常中心</strong>
          <el-tag effect="light">{{ exceptionRows.length }} 条异常</el-tag>
        </div>
      </template>
      <el-table v-if="exceptionRows.length" :data="exceptionRows" border fit highlight-current-row>
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="type" label="异常类型" width="130" />
        <el-table-column prop="detail" label="异常说明" min-width="220" show-overflow-tooltip />
        <el-table-column prop="suggestion" label="建议处理" min-width="150" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.canRepair" type="warning" link @click="openRepairCreate(row)">补卡</el-button>
            <span v-else class="muted-text">无需补卡</span>
          </template>
        </el-table-column>
      </el-table>
      <EmptyState v-else description="本月暂无考勤异常" />
    </el-card>

    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <strong>补卡申请记录</strong>
          <el-button type="primary" link @click="loadRepairList">刷新</el-button>
        </div>
      </template>
      <el-table v-if="repairList.length" :data="repairList" border fit highlight-current-row>
        <el-table-column prop="repairDate" label="补卡日期" width="130" />
        <el-table-column prop="repairType" label="补卡类型" width="130" />
        <el-table-column prop="checkOnTime" label="上班时间" min-width="170" />
        <el-table-column prop="checkOffTime" label="下班时间" min-width="170" />
        <el-table-column prop="reason" label="原因" min-width="180" show-overflow-tooltip />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <StatusTag :text="row.statusName" :status="row.status" :map="statusMap" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="110" fixed="right">
          <template #default="{ row }">
            <el-button v-if="canRevokeRepair(row)" type="danger" link @click="revokeRepair(row)">撤销</el-button>
            <span v-else class="muted-text">-</span>
          </template>
        </el-table-column>
      </el-table>
      <EmptyState v-else description="暂无补卡申请" />
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

    <FormDialog v-model="repairVisible" title="补卡申请" width="720px">
      <el-form ref="repairFormRef" :model="repairForm" :rules="repairRules" label-position="top">
        <div class="form-grid">
          <el-form-item label="补卡日期" prop="repairDate">
            <el-date-picker
              v-model="repairForm.repairDate"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="选择日期"
              :disabled-date="disabledRepairDate"
            />
          </el-form-item>
          <el-form-item label="补卡类型" prop="repairType">
            <el-select v-model="repairForm.repairType" placeholder="请选择补卡类型">
              <el-option label="上班补卡" value="上班补卡" />
              <el-option label="下班补卡" value="下班补卡" />
              <el-option label="上下班补卡" value="上下班补卡" />
            </el-select>
          </el-form-item>
          <el-form-item v-if="needsCheckOn" label="上班时间" prop="checkOnClock">
            <el-time-picker v-model="repairForm.checkOnClock" value-format="HH:mm:ss" placeholder="选择上班时间" />
          </el-form-item>
          <el-form-item v-if="needsCheckOff" label="下班时间" prop="checkOffClock">
            <el-time-picker v-model="repairForm.checkOffClock" value-format="HH:mm:ss" placeholder="选择下班时间" />
          </el-form-item>
        </div>
        <el-form-item label="补卡原因" prop="reason">
          <el-input v-model.trim="repairForm.reason" type="textarea" :rows="3" placeholder="请说明忘打卡、外勤、设备异常等原因" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="repairVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRepair">提交申请</el-button>
      </template>
    </FormDialog>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '@/components/common/PageHeader.vue'
import MetricCard from '@/components/common/MetricCard.vue'
import FormDialog from '@/components/common/FormDialog.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import {
  createCheckRepair,
  findCheckByNumber,
  getCheckInfo,
  getCheckRepairList,
  revokeCheckRepair
} from '@/api/attendance'
import { findSalaryByNumberAndMonth } from '@/api/finance'
import { getLoginUsername } from '@/utils/auth'

const loginNumber = getLoginUsername() || ''
const checkMonth = ref(getMonthText(new Date()))
const recordsVisible = ref(false)
const salaryVisible = ref(false)
const repairVisible = ref(false)
const tableData = ref([])
const allRecords = ref([])
const salaryTable = ref([])
const repairList = ref([])
const checkForm = reactive(createCheckForm())
const repairForm = reactive(createRepairForm())
const repairFormRef = ref()

const statusMap = {
  0: { type: 'warning' },
  1: { type: 'success' },
  2: { type: 'danger' },
  3: { type: 'info' }
}

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
const needsCheckOn = computed(() => ['上班补卡', '上下班补卡'].includes(repairForm.repairType))
const needsCheckOff = computed(() => ['下班补卡', '上下班补卡'].includes(repairForm.repairType))
const monthRecords = computed(() => allRecords.value.filter((item) => item.date?.startsWith(checkMonth.value)))
const exceptionRows = computed(() => buildExceptionRows())

const repairRules = {
  repairDate: [{ required: true, message: '请选择补卡日期', trigger: 'change' }],
  repairType: [{ required: true, message: '请选择补卡类型', trigger: 'change' }],
  checkOnClock: [
    {
      validator: (_, value, callback) => {
        if (needsCheckOn.value && !value) callback(new Error('请选择上班时间'))
        else callback()
      },
      trigger: 'change'
    }
  ],
  checkOffClock: [
    {
      validator: (_, value, callback) => {
        if (needsCheckOff.value && !value) callback(new Error('请选择下班时间'))
        else callback()
      },
      trigger: 'change'
    }
  ],
  reason: [{ required: true, message: '请输入补卡原因', trigger: 'blur' }]
}

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

function createRepairForm() {
  return {
    applyNumber: loginNumber,
    repairDate: '',
    repairType: '',
    checkOnClock: '',
    checkOffClock: '',
    reason: '',
    taskTypeID: '4',
    applyTime: getCurrentDateTime()
  }
}

function makePayload() {
  return {
    ...createCheckForm(),
    month: checkMonth.value,
    date: checkMonth.value
  }
}

function getCurrentDateTime(type = 'datetime') {
  const date = new Date()
  const pad = (value) => String(value).padStart(2, '0')
  const dateText = `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
  const timeText = `${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
  if (type === 'date') return dateText
  if (type === 'time') return timeText
  return `${dateText} ${timeText}`
}

function statusText(status) {
  const map = {
    0: '待审批',
    1: '已通过',
    2: '已驳回',
    3: '已撤销'
  }
  return map[String(status)] || '未知'
}

function disabledRepairDate(date) {
  const today = new Date(getCurrentDateTime('date').replace(/-/g, '/'))
  return date.getTime() > today.getTime()
}

async function loadCheckInfo() {
  try {
    const data = await getCheckInfo(makePayload())
    Object.assign(checkForm, createCheckForm(), data || {})
  } catch (error) {
    ElMessage.error('查询考勤信息失败')
  }
}

async function loadCheckRecords() {
  try {
    allRecords.value = await findCheckByNumber(makePayload())
  } catch (error) {
    ElMessage.error('获取考勤记录失败')
  }
}

function loadDashboardData() {
  loadCheckInfo()
  loadCheckRecords()
}

async function openRecords() {
  try {
    if (!allRecords.value.length) {
      await loadCheckRecords()
    }
    tableData.value = monthRecords.value
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

function openRepairCreate(exceptionRow = null) {
  Object.assign(repairForm, createRepairForm())
  if (exceptionRow) {
    repairForm.repairDate = exceptionRow.date
    repairForm.repairType = exceptionRow.repairType
    repairForm.reason = `${exceptionRow.type}：${exceptionRow.detail}`
  }
  repairVisible.value = true
}

async function loadRepairList() {
  try {
    const data = await getCheckRepairList({ applyNumber: loginNumber })
    repairList.value = (Array.isArray(data) ? data : []).map((item) => ({
      ...item,
      statusName: item.statusName || statusText(item.status)
    }))
  } catch (error) {
    ElMessage.error('获取补卡申请记录失败')
  }
}

async function submitRepair() {
  const valid = await repairFormRef.value?.validate().catch(() => false)
  if (!valid) return
  if (repairForm.repairDate > getCurrentDateTime('date')) {
    ElMessage.error('不能提交未来日期的补卡申请')
    return
  }

  const payload = {
    ...repairForm,
    checkOnTime: needsCheckOn.value ? `${repairForm.repairDate} ${repairForm.checkOnClock}` : null,
    checkOffTime: needsCheckOff.value ? `${repairForm.repairDate} ${repairForm.checkOffClock}` : null,
    applyTime: getCurrentDateTime()
  }

  try {
    const data = await createCheckRepair(payload)
    if (data?.code && data.code !== 200) {
      ElMessage.error(data.message || '补卡申请提交失败')
      return
    }
    ElMessage.success('补卡申请已提交')
    repairVisible.value = false
    loadRepairList()
  } catch (error) {
    ElMessage.error('补卡申请提交失败')
  }
}

function canRevokeRepair(row) {
  return String(row.status) === '0'
}

async function revokeRepair(row) {
  try {
    await ElMessageBox.confirm(`确定撤销 ${row.repairDate} 的${row.repairType}吗？`, '撤销补卡申请', {
      type: 'warning'
    })
    const data = await revokeCheckRepair({ id: row.id })
    if (data?.code && data.code !== 200) {
      ElMessage.error(data.message || '撤销失败')
      return
    }
    ElMessage.success('撤销成功')
    loadRepairList()
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error('撤销失败')
    }
  }
}

function buildExceptionRows() {
  const recordsByDate = new Map(monthRecords.value.map((item) => [item.date, item]))
  const todayText = getCurrentDateTime('date')
  const rows = []

  getWorkdaysInMonth(checkMonth.value).forEach((date) => {
    if (date > todayText) return
    const record = recordsByDate.get(date)
    if (!record) {
      rows.push(createExceptionRow(date, '缺勤', '当天没有签到/签退记录', '上下班补卡'))
      return
    }
    if (record.remarks) return

    const missingOn = !record.checkOnTime
    const missingOff = !record.checkOffTime
    if (missingOn && missingOff) {
      rows.push(createExceptionRow(date, '缺卡', '缺少上班和下班打卡记录', '上下班补卡'))
      return
    }
    if (missingOn) {
      rows.push(createExceptionRow(date, '上班缺卡', '缺少上班打卡记录', '上班补卡'))
    }
    if (missingOff) {
      rows.push(createExceptionRow(date, '下班缺卡', '缺少下班打卡记录', '下班补卡'))
    }
    if (record.checkOnStatus === '迟到') {
      rows.push({
        date,
        type: '迟到',
        detail: `签到时间 ${record.checkOnTime || '-'}`,
        suggestion: '如有外勤或设备异常，可发起补卡',
        canRepair: true,
        repairType: '上班补卡'
      })
    }
    if (record.checkOffStatus === '早退') {
      rows.push({
        date,
        type: '早退',
        detail: `签退时间 ${record.checkOffTime || '-'}`,
        suggestion: '如有外勤或设备异常，可发起补卡',
        canRepair: true,
        repairType: '下班补卡'
      })
    }
  })

  return rows
}

function createExceptionRow(date, type, detail, repairType) {
  return {
    date,
    type,
    detail,
    suggestion: '建议提交补卡申请',
    canRepair: true,
    repairType
  }
}

function getWorkdaysInMonth(month) {
  if (!month) return []
  const [year, monthNumber] = month.split('-').map(Number)
  const lastDay = new Date(year, monthNumber, 0).getDate()
  const days = []
  for (let day = 1; day <= lastDay; day += 1) {
    const date = new Date(year, monthNumber - 1, day)
    const week = date.getDay()
    if (week === 0 || week === 6) continue
    days.push(`${year}-${String(monthNumber).padStart(2, '0')}-${String(day).padStart(2, '0')}`)
  }
  return days
}

onMounted(() => {
  loadDashboardData()
  loadRepairList()
})
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
  gap: 10px;
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

.muted-text {
  color: var(--am-muted);
}

:deep(.el-date-editor.el-input) {
  width: 160px;
}

:deep(.form-grid .el-date-editor.el-input),
:deep(.form-grid .el-select) {
  width: 100%;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

@media (max-width: 720px) {
  .toolbar {
    flex-direction: column;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
