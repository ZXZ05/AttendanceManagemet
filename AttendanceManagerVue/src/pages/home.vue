<template>
  <section class="home-page am-grid">
    <PageHeader :kicker="'Today Workspace'" :title="`欢迎，${displayName}`" :description="todayText + '，现在是 ' + (currentTime || '--:--:--')">
      <template #actions>
        <el-button type="primary" @click="goTo('/check')">查看我的考勤</el-button>
      </template>
    </PageHeader>

    <div class="dashboard-grid">
      <el-card class="dashboard-card attendance-card" shadow="never">
        <template #header>
          <div class="card-header">
            <div>
              <p class="card-kicker">Attendance</p>
              <strong>签到打卡</strong>
            </div>
            <el-tag :type="attendanceTagType" effect="light">{{ attendanceStatusText }}</el-tag>
          </div>
        </template>

        <div class="time-status-list">
          <div v-for="item in workStatusItems" :key="item.label" class="time-status-item">
            <span>{{ item.label }}</span>
            <strong>{{ item.time }}</strong>
            <em :class="item.statusClass">{{ item.status }}</em>
          </div>
        </div>

        <div class="check-action-wrap">
          <el-button
            class="check-action"
            :type="checkAction.type"
            :disabled="checkAction.disabled"
            @click="checkAction.handler"
          >
            <span>{{ checkAction.label }}</span>
            <strong>{{ currentTime }}</strong>
          </el-button>
        </div>
      </el-card>

      <el-card class="dashboard-card" shadow="never">
        <template #header>
          <div class="card-header">
            <div>
              <p class="card-kicker">Notice</p>
              <strong>通知公告</strong>
            </div>
            <el-button type="primary" link @click="goTo('/notice')">更多</el-button>
          </div>
        </template>

        <div v-if="noticeList.length" class="notice-list">
          <button v-for="item in noticeList" :key="item.id || item.title" class="notice-item" @click="showAnnouncement(item)">
            <span>{{ item.title }}</span>
            <time>{{ item.date || item.publishTime || '暂无日期' }}</time>
          </button>
        </div>
        <EmptyState v-else description="暂无通知" />
      </el-card>

      <el-card class="dashboard-card calendar-card" shadow="never">
        <template #header>
          <div class="card-header">
            <div>
              <p class="card-kicker">Calendar</p>
              <strong>工作日历</strong>
            </div>
            <el-icon><Calendar /></el-icon>
          </div>
        </template>
        <el-calendar v-model="calendarDate" />
      </el-card>

      <div class="dashboard-card media-card">
        <el-carousel height="284px" indicator-position="outside">
          <el-carousel-item>
            <img src="../assets/001.jpg" alt="办公协作" />
          </el-carousel-item>
          <el-carousel-item>
            <img src="../assets/002.jpg" alt="团队管理" />
          </el-carousel-item>
        </el-carousel>
      </div>

      <el-card class="dashboard-card" shadow="never">
        <template #header>
          <div class="card-header">
            <div>
              <p class="card-kicker">Daily Quote</p>
              <strong>每日一句</strong>
            </div>
            <el-icon><Sunny /></el-icon>
          </div>
        </template>
        <blockquote>{{ dailyQuote }}</blockquote>
      </el-card>
    </div>

    <FormDialog v-model="dialogVisible" title="通知详情" width="560px">
      <el-form :model="selectedNotice" label-position="top">
        <el-form-item label="标题">
          <el-input v-model="selectedNotice.title" readonly />
        </el-form-item>

        <div class="dialog-grid">
          <el-form-item label="开始时间">
            <el-date-picker v-model="selectedNotice.beginDate" type="date" value-format="YYYY-MM-DD" readonly />
          </el-form-item>
          <el-form-item label="结束时间">
            <el-date-picker v-model="selectedNotice.endDate" type="date" value-format="YYYY-MM-DD" readonly />
          </el-form-item>
        </div>

        <div class="dialog-grid">
          <el-form-item label="发布人">
            <el-input v-model="selectedNotice.publisherName" readonly />
          </el-form-item>
          <el-form-item label="发布时间">
            <el-input v-model="selectedNotice.publishTime" readonly />
          </el-form-item>
        </div>

        <el-form-item label="详情">
          <el-input v-model="selectedNotice.details" type="textarea" :rows="4" readonly />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button type="primary" @click="dialogVisible = false">我知道了</el-button>
      </template>
    </FormDialog>
  </section>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Calendar, Sunny } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/common/PageHeader.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import FormDialog from '@/components/common/FormDialog.vue'
import { loadCurrentUser, useUser } from '@/stores/user'
import { getAttendanceTimeRange, getCheckOffStatus, getCheckOnStatus, checkOff, checkOn } from '@/api/attendance'
import { getNotice } from '@/api/meeting'
import { getLoginUsername } from '@/utils/auth'

const router = useRouter()
const loginNumber = getLoginUsername() || ''
const { name } = useUser()

const QUOTES = [
  '行动是打破焦虑最有效的方法。',
  '把复杂任务拆小，执行就会变简单。',
  '今天比昨天多推进一点，就很了不起。',
  '先完成，再完美，持续迭代就是成长。'
]

const noticeList = ref([])
const dialogVisible = ref(false)
const selectedNotice = ref(createNoticeDetail())
const currentTime = ref('')
const timer = ref()
const calendarDate = ref(new Date())
const quoteIndex = ref(Math.floor(Math.random() * QUOTES.length))

const onTime = ref('08:00')
const offTime = ref('17:30')
const isOn = ref(false)
const isOff = ref(false)
const isLeave = ref(false)

const displayName = computed(() => name.value || loginNumber || '同事')
const todayText = computed(() => new Intl.DateTimeFormat('zh-CN', {
  year: 'numeric',
  month: 'long',
  day: 'numeric',
  weekday: 'long'
}).format(calendarDate.value))
const dailyQuote = computed(() => QUOTES[quoteIndex.value])

const attendanceStatusText = computed(() => {
  if (isLeave.value) {
    return '今日请假'
  }
  if (isOn.value && isOff.value) {
    return '打卡完成'
  }
  if (isOn.value) {
    return '等待下班打卡'
  }
  return '等待上班打卡'
})

const attendanceTagType = computed(() => {
  if (isLeave.value) {
    return 'warning'
  }
  if (isOn.value && isOff.value) {
    return 'success'
  }
  return 'info'
})

const workStatusItems = computed(() => [
  {
    label: '上班',
    time: onTime.value,
    status: isLeave.value ? '请假' : isOn.value ? '已打卡' : '未打卡',
    statusClass: isLeave.value ? 'is-warning' : isOn.value ? 'is-success' : 'is-danger'
  },
  {
    label: '下班',
    time: offTime.value,
    status: isLeave.value ? '请假' : isOff.value ? '已打卡' : '未打卡',
    statusClass: isLeave.value ? 'is-warning' : isOff.value ? 'is-success' : 'is-danger'
  }
])

const checkAction = computed(() => {
  if (!isOn.value) {
    return {
      label: '上班打卡',
      type: 'success',
      disabled: false,
      handler: handleCheckOn
    }
  }

  if (!isOff.value) {
    return {
      label: '下班打卡',
      type: 'danger',
      disabled: false,
      handler: handleCheckOff
    }
  }

  return {
    label: '打卡结束',
    type: 'info',
    disabled: true,
    handler: () => {}
  }
})

function createNoticeDetail() {
  return {
    title: '',
    beginDate: '',
    endDate: '',
    publisherName: '',
    publishTime: '',
    details: ''
  }
}

function createCheckPayload() {
  return {
    employeeID: loginNumber,
    checkOnTime: '',
    checkOffTime: '',
    checkOnStatus: '',
    checkOffStatus: ''
  }
}

function formatTime(value) {
  return value < 10 ? `0${value}` : String(value)
}

function getCurrentDateTime() {
  const date = new Date()
  const year = date.getFullYear()
  const month = formatTime(date.getMonth() + 1)
  const day = formatTime(date.getDate())
  const hour = formatTime(date.getHours())
  const minute = formatTime(date.getMinutes())
  const second = formatTime(date.getSeconds())
  return `${year}-${month}-${day} ${hour}:${minute}:${second}`
}

function updateClock() {
  const date = new Date()
  currentTime.value = `${formatTime(date.getHours())}:${formatTime(date.getMinutes())}:${formatTime(date.getSeconds())}`
}

function goTo(path) {
  router.push(path)
}

function showAnnouncement(row) {
  selectedNotice.value = { ...createNoticeDetail(), ...row }
  dialogVisible.value = true
}

async function loadNotice() {
  try {
    const data = await getNotice()
    noticeList.value = Array.isArray(data) ? data : []
  } catch (error) {
    noticeList.value = []
    ElMessage.error('获取通知失败')
  }
}

async function loadAttendanceTime() {
  try {
    const data = await getAttendanceTimeRange()
    const [start, end] = String(data || '').split('-')
    if (start) {
      onTime.value = start
    }
    if (end) {
      offTime.value = end
    }
  } catch (error) {
    ElMessage.warning('获取打卡时间配置失败，已使用默认值')
  }
}

async function loadCheckOnStatus() {
  const payload = createCheckPayload()
  payload.checkOnTime = getCurrentDateTime()

  try {
    const data = await getCheckOnStatus(payload)
    if (data === '1') {
      isOn.value = true
    } else if (data === '2') {
      isLeave.value = true
    }
  } catch (error) {
    ElMessage.error('读取上班打卡状态失败')
  }
}

async function loadCheckOffStatus() {
  const payload = createCheckPayload()
  payload.checkOffTime = getCurrentDateTime()

  try {
    const data = await getCheckOffStatus(payload)
    if (data === '1') {
      isOff.value = true
    }
  } catch (error) {
    ElMessage.error('读取下班打卡状态失败')
  }
}

async function handleCheckOn() {
  if (isLeave.value) {
    ElMessage.error('请假无需打卡')
    return
  }
  if (isOn.value) {
    ElMessage.error('请勿重复打卡')
    return
  }

  const payload = createCheckPayload()
  payload.checkOnTime = getCurrentDateTime()
  try {
    await checkOn(payload)
    isOn.value = true
    ElMessage.success('上班打卡成功')
  } catch (error) {
    ElMessage.error('上班打卡失败，请稍后重试')
  }
}

async function handleCheckOff() {
  if (isOff.value) {
    ElMessage.error('请勿重复打卡')
    return
  }

  const payload = createCheckPayload()
  payload.checkOffTime = getCurrentDateTime()
  try {
    await checkOff(payload)
    isOff.value = true
    ElMessage.success('下班打卡成功')
  } catch (error) {
    ElMessage.error('下班打卡失败，请稍后重试')
  }
}

onMounted(async () => {
  await Promise.all([loadCurrentUser(), loadNotice(), loadAttendanceTime(), loadCheckOnStatus(), loadCheckOffStatus()])
  updateClock()
  timer.value = window.setInterval(updateClock, 1000)
})

onBeforeUnmount(() => {
  if (timer.value) {
    clearInterval(timer.value)
  }
})
</script>

<style scoped>
.home-page {
  gap: 16px;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: minmax(320px, 0.95fr) minmax(360px, 1.25fr);
  gap: 16px;
}

.dashboard-card {
  border-radius: 14px;
}

.attendance-card {
  min-height: 340px;
}

.card-kicker {
  margin: 0;
  color: var(--am-accent);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-header strong {
  display: block;
  margin-top: 6px;
  font-size: 20px;
}

.time-status-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.time-status-item {
  padding: 14px;
  border: 1px solid var(--am-line-soft);
  border-radius: 12px;
  background: rgba(12, 21, 40, 0.6);
}

.time-status-item span,
.time-status-item strong,
.time-status-item em {
  display: block;
}

.time-status-item span {
  color: var(--am-muted);
  font-size: 13px;
}

.time-status-item strong {
  margin-top: 8px;
  font-size: 26px;
}

.time-status-item em {
  margin-top: 8px;
  font-style: normal;
  font-weight: 700;
}

.is-success {
  color: var(--am-success);
}

.is-warning {
  color: var(--am-warning);
}

.is-danger {
  color: var(--am-danger);
}

.check-action-wrap {
  display: grid;
  place-items: center;
  padding: 24px 0 8px;
}

.check-action {
  width: 160px;
  height: 160px;
  border-radius: 50% !important;
}

.check-action span,
.check-action strong {
  display: block;
}

.check-action span {
  font-size: 20px;
  font-weight: 800;
}

.check-action strong {
  margin-top: 8px;
}

.notice-list {
  display: grid;
  gap: 10px;
}

.notice-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  width: 100%;
  padding: 12px;
  color: var(--am-text);
  border: 1px solid var(--am-line-soft);
  border-radius: 10px;
  background: rgba(12, 21, 40, 0.5);
  cursor: pointer;
}

.notice-item:hover {
  border-color: var(--am-brand);
}

.notice-item span {
  overflow: hidden;
  text-align: left;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notice-item time {
  color: var(--am-muted);
  font-size: 12px;
}

.calendar-card {
  grid-row: span 2;
}

.media-card {
  overflow: hidden;
  border: 1px solid var(--am-line);
  border-radius: 14px;
  background: rgba(12, 21, 39, 0.85);
}

.media-card img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

blockquote {
  margin: 0;
  padding: 14px 16px;
  font-size: 22px;
  font-weight: 700;
  line-height: 1.7;
  border-left: 4px solid var(--am-accent);
  border-radius: 10px;
  background: rgba(65, 217, 194, 0.1);
}

.dialog-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

:deep(.el-date-editor.el-input) {
  width: 100%;
}

@media (max-width: 1180px) {
  .dashboard-grid {
    grid-template-columns: 1fr;
  }

  .calendar-card {
    grid-row: auto;
  }
}

@media (max-width: 720px) {
  .time-status-list,
  .dialog-grid {
    grid-template-columns: 1fr;
  }

  .notice-item {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
