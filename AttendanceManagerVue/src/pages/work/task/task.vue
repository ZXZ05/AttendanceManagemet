<template>
  <section class="work-page">
    <PageHeader kicker="Approval Queue" title="我的任务" description="处理待审批的请假、固定资产和考勤补卡申请，已完成的任务可继续查看详情。" />

    <el-card shadow="never">
      <el-table :data="tableData" border fit highlight-current-row>
        <el-table-column prop="name" label="任务名称" min-width="150" />
        <el-table-column prop="applyName" label="申请人" width="130" />
        <el-table-column prop="applyTime" label="申请时间" min-width="180" />
        <el-table-column
          prop="statusName"
          label="审批状态"
          width="130"
          :filters="statusFilters"
          :filter-method="filterStatus"
          filter-placement="bottom-end"
        >
          <template #default="{ row }">
            <StatusTag :text="row.statusName" :status="row.statusName" :map="statusMap" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button :type="row.statusName === '待审批' ? 'warning' : 'primary'" @click="openDetail(row)">
              {{ row.statusName === '待审批' ? '审批' : '详情' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <FormDialog v-model="dialogVisible" :title="dialogTitle" width="640px">
      <el-form :model="detailForm" label-position="top">
        <div class="form-grid">
          <el-form-item label="申请人">
            <el-input v-model="detailForm.applyName" readonly />
          </el-form-item>
          <el-form-item label="申请时间">
            <el-input v-model="detailForm.applyTime" readonly />
          </el-form-item>
          <el-form-item label="申请类型">
            <el-input v-model="detailForm.type" readonly />
          </el-form-item>
          <el-form-item label="审批结果">
            <el-input v-model="detailForm.statusText" readonly />
          </el-form-item>
        </div>

        <div v-if="isLeaveTask" class="form-grid">
          <el-form-item label="开始时间">
            <el-input v-model="detailForm.beginTime" readonly />
          </el-form-item>
          <el-form-item label="结束时间">
            <el-input v-model="detailForm.endTime" readonly />
          </el-form-item>
          <el-form-item label="时长">
            <el-input v-model="detailForm.duration" readonly />
          </el-form-item>
          <el-form-item label="请假事由">
            <el-input v-model="detailForm.reason" type="textarea" :rows="2" readonly />
          </el-form-item>
        </div>

        <div v-if="isFixedTask" class="form-grid">
          <el-form-item label="资产编号">
            <el-input v-model="detailForm.number" readonly />
          </el-form-item>
          <el-form-item label="资产名称">
            <el-input v-model="detailForm.name" readonly />
          </el-form-item>
          <el-form-item label="价格">
            <el-input v-model="detailForm.price" readonly />
          </el-form-item>
        </div>

        <div v-if="isRepairTask" class="form-grid">
          <el-form-item label="补卡日期">
            <el-input v-model="detailForm.repairDate" readonly />
          </el-form-item>
          <el-form-item label="补卡类型">
            <el-input v-model="detailForm.repairType" readonly />
          </el-form-item>
          <el-form-item label="上班时间">
            <el-input v-model="detailForm.checkOnTime" readonly />
          </el-form-item>
          <el-form-item label="下班时间">
            <el-input v-model="detailForm.checkOffTime" readonly />
          </el-form-item>
          <el-form-item label="补卡原因" class="form-grid-full">
            <el-input v-model="detailForm.reason" type="textarea" :rows="2" readonly />
          </el-form-item>
        </div>

        <div v-if="showApprovalInfo" class="form-grid">
          <el-form-item label="审批人">
            <el-input v-model="detailForm.approvalName" readonly />
          </el-form-item>
          <el-form-item label="审批时间">
            <el-input v-model="detailForm.approvalTime" readonly />
          </el-form-item>
        </div>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button v-if="showApprovalActions" type="danger" @click="submitApproval('no')">驳回</el-button>
        <el-button v-if="showApprovalActions" type="primary" @click="submitApproval('yes')">同意</el-button>
      </template>
    </FormDialog>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import FormDialog from '@/components/common/FormDialog.vue'
import { approveTask, findTaskByApplyID, getTaskList } from '@/api/application'
import { getLoginUsername, isAuthenticated } from '@/utils/auth'

const router = useRouter()
const tableData = ref([])
const dialogVisible = ref(false)
const detailForm = ref({})
const selectedTask = ref({})
const showApprovalActions = ref(false)
const showApprovalInfo = ref(false)

const statusFilters = [
  { text: '待审批', value: '待审批' },
  { text: '已通过', value: '已通过' },
  { text: '已驳回', value: '已驳回' }
]

const statusMap = {
  待审批: { type: 'warning' },
  已通过: { type: 'success' },
  已驳回: { type: 'danger' }
}

const dialogTitle = computed(() => (showApprovalActions.value ? '审批任务' : '任务详情'))
const isLeaveTask = computed(() => Number(detailForm.value.taskTypeID) === 1)
const isFixedTask = computed(() => [2, 3].includes(Number(detailForm.value.taskTypeID)))
const isRepairTask = computed(() => Number(detailForm.value.taskTypeID) === 4)

function statusText(status) {
  const map = {
    0: '待审批',
    1: '已通过',
    2: '已驳回',
    3: '已撤销'
  }
  return map[String(status)] || status || '未知'
}

function getCurrentDateTime() {
  const date = new Date()
  const pad = (value) => String(value).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

function filterStatus(value, row) {
  return row.statusName === value
}

async function loadData() {
  if (!isAuthenticated()) {
    ElMessage.error('未登录或登录信息过期')
    router.push('/')
    return
  }

  try {
    tableData.value = await getTaskList({ receiveNumber: getLoginUsername() })
  } catch (error) {
    ElMessage.error('获取任务列表失败')
  }
}

async function openDetail(row) {
  selectedTask.value = { ...row }
  showApprovalActions.value = row.statusName === '待审批'
  showApprovalInfo.value = !showApprovalActions.value

  try {
    const data = await findTaskByApplyID(selectedTask.value)
    detailForm.value = {
      ...data,
      statusText: statusText(data?.status)
    }
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取任务详情失败')
  }
}

async function submitApproval(advice) {
  try {
    await approveTask({
      ...selectedTask.value,
      advice,
      approvalTime: getCurrentDateTime(),
      approvalNumber: getLoginUsername()
    })
    ElMessage.success('审批成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('审批失败，请稍后重试')
  }
}

onMounted(loadData)
</script>

<style scoped>
.work-page {
  display: grid;
  gap: 16px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.form-grid-full {
  grid-column: 1 / -1;
}

@media (max-width: 720px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
