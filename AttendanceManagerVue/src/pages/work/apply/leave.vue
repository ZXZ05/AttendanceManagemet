<template>
  <section class="work-page">
    <PageHeader kicker="Leave Request" title="请假申请" description="查看历史请假记录，或发起新的请假流程。">
      <template #actions>
        <el-button type="primary" @click="openCreate">发起申请</el-button>
      </template>
    </PageHeader>

    <div v-if="tableData.length" class="request-grid">
      <el-card v-for="item in tableData" :key="item.id" shadow="never" class="request-card">
        <template #header>
          <div class="card-header">
            <StatusTag :text="item.statusName" :status="item.status" :map="statusMap" />
            <el-button type="primary" link @click="openDetail(item)">查看详情</el-button>
          </div>
        </template>
        <h3>{{ item.typeName || '请假申请' }}</h3>
        <p>申请人：{{ item.applyName || name || '-' }}</p>
        <p>申请时间：{{ item.applyTime || '-' }}</p>
      </el-card>
    </div>
    <EmptyState v-else description="暂无请假记录" />

    <FormDialog v-model="dialogVisible" :title="dialogTitle" width="720px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <div class="form-grid">
          <el-form-item label="申请人" prop="applyName">
            <el-input v-model="form.applyName" readonly />
          </el-form-item>
          <el-form-item v-if="isUpdate" label="申请时间" prop="applyTime">
            <el-input v-model="form.applyTime" readonly />
          </el-form-item>
          <el-form-item label="请假类型" prop="typeID">
            <el-select v-model="form.typeID" :disabled="isUpdate" placeholder="请选择请假类型">
              <el-option v-for="item in typeList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item v-if="isUpdate" label="审批状态">
            <el-input v-model="form.statusName" readonly />
          </el-form-item>
        </div>

        <div v-if="isCreate" class="form-grid">
          <el-form-item label="开始日期" prop="date1">
            <el-date-picker v-model="form.date1" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
          </el-form-item>
          <el-form-item label="开始时间" prop="date2">
            <el-time-picker v-model="form.date2" value-format="HH:mm:ss" placeholder="选择时间" />
          </el-form-item>
          <el-form-item label="结束日期" prop="date3">
            <el-date-picker v-model="form.date3" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
          </el-form-item>
          <el-form-item label="结束时间" prop="date4">
            <el-time-picker v-model="form.date4" value-format="HH:mm:ss" placeholder="选择时间" />
          </el-form-item>
        </div>

        <div v-else class="form-grid">
          <el-form-item label="开始时间">
            <el-input v-model="form.beginTime" readonly />
          </el-form-item>
          <el-form-item label="结束时间">
            <el-input v-model="form.endTime" readonly />
          </el-form-item>
        </div>

        <div class="form-grid">
          <el-form-item label="时长（小时）">
            <el-input v-model="form.duration" readonly />
          </el-form-item>
          <el-form-item v-if="showApprovalInfo" label="审批人">
            <el-input v-model="form.approvalName" readonly />
          </el-form-item>
          <el-form-item v-if="showApprovalInfo" label="审批时间">
            <el-input v-model="form.approvalTime" readonly />
          </el-form-item>
        </div>

        <el-form-item label="请假事由" prop="reason">
          <el-input v-model="form.reason" type="textarea" :rows="3" :readonly="isUpdate" placeholder="请输入请假事由" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button v-if="isCreate" type="primary" @click="submitForm">提交</el-button>
      </template>
    </FormDialog>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import FormDialog from '@/components/common/FormDialog.vue'
import { createLeave, getLeaveList, getLeaveTypeList } from '@/api/application'
import { loadCurrentUser, useUser } from '@/stores/user'
import { getLoginUsername } from '@/utils/auth'

const loginNumber = getLoginUsername() || ''
const { name } = useUser()
const tableData = ref([])
const typeList = ref([])
const dialogVisible = ref(false)
const isCreate = ref(false)
const isUpdate = ref(false)
const formRef = ref()

const statusMap = {
  0: { type: 'warning' },
  1: { type: 'success' },
  2: { type: 'danger' },
  3: { type: 'info' }
}

const form = reactive(createForm())
const dialogTitle = computed(() => (isCreate.value ? '发起请假申请' : '请假详情'))
const showApprovalInfo = computed(() => isUpdate.value && ['1', '2'].includes(String(form.status)))

const rules = {
  typeID: [{ required: true, message: '请选择请假类型', trigger: 'change' }],
  date1: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  date2: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  date3: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
  date4: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  reason: [{ required: true, message: '请输入请假事由', trigger: 'blur' }]
}

function createForm() {
  return {
    applyNumber: loginNumber,
    applyName: name.value,
    beginTime: '',
    endTime: '',
    duration: '',
    reason: '',
    typeID: '',
    status: '',
    approvalID: '',
    taskTypeID: '1',
    date1: '',
    date2: '',
    date3: '',
    date4: '',
    applyTime: getCurrentDateTime(),
    statusName: '',
    approvalName: '',
    approvalTime: '',
    typeName: ''
  }
}

function resetFormData(data = createForm()) {
  Object.assign(form, data)
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

function getCurrentDateTime(type = 'datetime') {
  const date = new Date()
  const pad = (value) => String(value).padStart(2, '0')
  const dateText = `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
  const timeText = `${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
  if (type === 'date') return dateText
  if (type === 'time') return timeText
  return `${dateText} ${timeText}`
}

function calculateDuration() {
  if (!form.date1 || !form.date2 || !form.date3 || !form.date4) {
    form.duration = ''
    return
  }

  const start = new Date(`${form.date1} ${form.date2}`.replace(/-/g, '/'))
  const end = new Date(`${form.date3} ${form.date4}`.replace(/-/g, '/'))
  const diff = end.getTime() - start.getTime()
  form.duration = Number.isFinite(diff) ? Math.max(Math.floor(diff / 3600000), 0) : ''
}

async function loadData() {
  try {
    const data = await getLeaveList({ applyNumber: loginNumber })
    tableData.value = (Array.isArray(data) ? data : []).map((item) => ({
      ...item,
      statusName: statusText(item.status)
    }))
  } catch (error) {
    ElMessage.error('获取请假记录失败')
  }
}

async function loadTypes() {
  try {
    typeList.value = await getLeaveTypeList()
  } catch (error) {
    ElMessage.error('获取请假类型失败')
  }
}

async function openCreate() {
  await loadTypes()
  resetFormData(createForm())
  form.applyName = name.value
  form.applyNumber = loginNumber
  form.applyTime = getCurrentDateTime()
  isCreate.value = true
  isUpdate.value = false
  dialogVisible.value = true
}

function openDetail(row) {
  resetFormData({ ...createForm(), ...row, applyName: row.applyName || name.value, statusName: statusText(row.status) })
  calculateDurationFromRange()
  isCreate.value = false
  isUpdate.value = true
  dialogVisible.value = true
}

function calculateDurationFromRange() {
  if (!form.beginTime || !form.endTime) return
  const start = new Date(form.beginTime.replace(/-/g, '/'))
  const end = new Date(form.endTime.replace(/-/g, '/'))
  const diff = end.getTime() - start.getTime()
  form.duration = Number.isFinite(diff) ? Math.max(Math.floor(diff / 3600000), 0) : ''
}

async function submitForm() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  form.beginTime = `${form.date1} ${form.date2}`
  form.endTime = `${form.date3} ${form.date4}`
  calculateDuration()

  try {
    await createLeave({ ...form })
    ElMessage.success('提交成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('提交失败，请稍后重试')
  }
}

watch(() => [form.date1, form.date2, form.date3, form.date4], calculateDuration)

onMounted(async () => {
  await loadCurrentUser()
  loadData()
})
</script>

<style scoped>
.work-page {
  display: grid;
  gap: 16px;
}

.request-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 14px;
}

.request-card h3 {
  margin: 0 0 10px;
  font-size: 18px;
}

.request-card p {
  margin: 6px 0;
  color: var(--am-text-soft);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

:deep(.el-date-editor.el-input),
:deep(.el-select) {
  width: 100%;
}

@media (max-width: 720px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
