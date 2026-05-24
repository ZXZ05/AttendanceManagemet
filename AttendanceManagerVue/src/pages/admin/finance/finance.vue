<template>
  <section class="admin-page">
    <PageHeader kicker="Finance Desk" title="财务管理" description="管理固定资产购置、月度考勤汇总与工资条发放。">
      <template #actions>
        <el-date-picker v-model="checkMonth" type="month" value-format="YYYY-MM" placeholder="选择月份" />
        <el-button type="primary" @click="loadCheckList">查询</el-button>
      </template>
    </PageHeader>

    <el-tabs v-model="activeName">
      <el-tab-pane label="固定资产" name="fixed">
        <DataToolbar>
          <template #right>
            <el-button type="primary" @click="openCreateFixed">购置固定资产</el-button>
          </template>
        </DataToolbar>

        <el-card shadow="never">
          <el-table :data="fixedList" border fit highlight-current-row>
            <el-table-column prop="number" label="固定资产编号" min-width="150" />
            <el-table-column prop="name" label="名称" min-width="150" />
            <el-table-column prop="typeName" label="类别" width="130" />
            <el-table-column prop="price" label="价格" width="120" />
            <el-table-column label="操作" width="190" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" @click="openFixedDetail(row)">详情</el-button>
                <el-button type="danger" @click="retireFixed(row)">报废</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="工资发放" name="salary">
        <DataToolbar>
          <template #left>
            <el-tag effect="light">{{ checkMonth }}</el-tag>
          </template>
          <template #right>
            <el-button type="primary" @click="loadCheckList">查询</el-button>
            <el-button type="primary" @click="exportCheck">导出报表</el-button>
          </template>
        </DataToolbar>

        <el-card shadow="never">
          <el-table :data="checkList" border fit highlight-current-row>
            <el-table-column prop="employeeID" label="员工编号" width="130" />
            <el-table-column prop="workDays" label="应出勤（天）" />
            <el-table-column prop="checkDays" label="实际出勤（天）" />
            <el-table-column prop="leaveDays" label="请假（天）" />
            <el-table-column prop="lateDays" label="迟到（次）" />
            <el-table-column prop="leaveEarlyDays" label="早退（次）" />
            <el-table-column label="操作" width="140" fixed="right">
              <template #default="{ row }">
                <el-button type="success" @click="payOff(row)">发放工资条</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <FormDialog v-model="dialogVisible" :title="dialogTitle" width="680px">
      <el-form ref="formRef" :model="assetForm" :rules="rules" label-position="top">
        <div class="form-grid">
          <el-form-item label="申请人" prop="employeeName">
            <el-input v-model="assetForm.employeeName" readonly />
          </el-form-item>
          <el-form-item v-if="isUpdate" label="申请时间" prop="applyTime">
            <el-input v-model="assetForm.applyTime" readonly />
          </el-form-item>
          <el-form-item label="编号" prop="number">
            <el-input v-model.trim="assetForm.number" :readonly="isUpdate" placeholder="请输入固定资产编号" />
          </el-form-item>
          <el-form-item label="名称" prop="name">
            <el-input v-model.trim="assetForm.name" :readonly="isUpdate" placeholder="请输入固定资产名称" />
          </el-form-item>
          <el-form-item label="价格" prop="price">
            <el-input v-model.number="assetForm.price" :readonly="isUpdate" placeholder="请输入固定资产价格" />
          </el-form-item>
          <el-form-item label="类别" prop="typeID">
            <el-select v-model="assetForm.typeID" :disabled="isUpdate" placeholder="请选择固定资产类别">
              <el-option v-for="item in fixedTypeList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item v-if="showApprovalInfo" label="审批人">
            <el-input v-model="assetForm.approvalName" readonly />
          </el-form-item>
          <el-form-item v-if="showApprovalInfo" label="审批时间">
            <el-input v-model="assetForm.approvalTime" readonly />
          </el-form-item>
          <el-form-item v-if="isUpdate" label="审批状态">
            <el-input v-model="assetForm.statusName" readonly />
          </el-form-item>
        </div>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button v-if="isCreate" type="primary" @click="insertFixed">立即购置</el-button>
      </template>
    </FormDialog>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/common/PageHeader.vue'
import DataToolbar from '@/components/common/DataToolbar.vue'
import FormDialog from '@/components/common/FormDialog.vue'
import { createFixedAsset, exportCheckExcel, getCheckList, getFixedAssetList, getFixedAssetTypeList, paySalary } from '@/api/finance'
import { loadCurrentUser, useUser } from '@/stores/user'
import { getLoginUsername } from '@/utils/auth'

const loginNumber = getLoginUsername() || ''
const { name } = useUser()
const activeName = ref('fixed')
const fixedList = ref([])
const checkList = ref([])
const fixedTypeList = ref([])
const checkMonth = ref(getMonthText(new Date()))
const dialogVisible = ref(false)
const isCreate = ref(false)
const isUpdate = ref(false)
const formRef = ref()
const assetForm = reactive(createAssetForm())

const dialogTitle = computed(() => (isCreate.value ? '购置固定资产' : '资产详情'))
const showApprovalInfo = computed(() => isUpdate.value && ['1', '2'].includes(String(assetForm.status)))

const rules = {
  number: [
    { required: true, message: '请输入固定资产编号', trigger: 'blur' },
    { min: 3, max: 15, message: '长度需在 3 到 15 个字符之间', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入固定资产名称', trigger: 'blur' },
    { min: 2, max: 30, message: '长度需在 2 到 30 个字符之间', trigger: 'blur' }
  ],
  typeID: [{ required: true, message: '请选择固定资产类别', trigger: 'change' }],
  price: [
    { required: true, message: '价格不能为空', trigger: 'blur' },
    { type: 'number', message: '价格必须为数字', trigger: 'blur' }
  ]
}

function getMonthText(date) {
  const pad = (value) => String(value).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}`
}

function getCurrentDateTime() {
  const date = new Date()
  const pad = (value) => String(value).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

function createAssetForm() {
  return {
    employeeNumber: loginNumber,
    employeeName: name.value,
    number: '',
    name: '',
    typeID: '',
    price: '',
    applyTime: getCurrentDateTime(),
    taskTypeID: '2',
    status: '',
    statusName: '',
    approvalID: '',
    approvalName: '',
    approvalTime: ''
  }
}

function resetAssetForm(data = createAssetForm()) {
  Object.assign(assetForm, data)
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

async function loadFixedList() {
  try {
    fixedList.value = await getFixedAssetList()
  } catch (error) {
    ElMessage.error('获取固定资产列表失败')
  }
}

async function loadTypeList() {
  try {
    fixedTypeList.value = await getFixedAssetTypeList()
  } catch (error) {
    ElMessage.error('获取固定资产类别失败')
  }
}

async function loadCheckList() {
  try {
    checkList.value = await getCheckList({
      date: checkMonth.value,
      month: checkMonth.value
    })
  } catch (error) {
    ElMessage.error('获取考勤汇总失败')
  }
}

async function openCreateFixed() {
  await loadTypeList()
  resetAssetForm(createAssetForm())
  assetForm.employeeName = name.value
  assetForm.employeeNumber = loginNumber
  assetForm.applyTime = getCurrentDateTime()
  isCreate.value = true
  isUpdate.value = false
  dialogVisible.value = true
}

function openFixedDetail(row) {
  resetAssetForm({ ...createAssetForm(), ...row, employeeName: row.employeeName || name.value, statusName: statusText(row.status) })
  isCreate.value = false
  isUpdate.value = true
  dialogVisible.value = true
}

async function insertFixed() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  try {
    const data = await createFixedAsset({ ...assetForm })
    if (data?.code === 20005) {
      ElMessage.error('该编号已存在')
      return
    }
    ElMessage.success('购置成功')
    dialogVisible.value = false
    loadFixedList()
  } catch (error) {
    ElMessage.error('购置失败')
  }
}

function retireFixed() {
  ElMessage.warning('报废处理接口暂未开放')
}

async function payOff(row) {
  const now = new Date()
  const target = new Date(checkMonth.value)
  if (now.getFullYear() === target.getFullYear() && now.getMonth() === target.getMonth()) {
    ElMessage.error('无法发放当月工资条')
    return
  }

  try {
    const data = await paySalary({ ...row, month: checkMonth.value })
    if (data?.code === 200) {
      ElMessage.success('发放成功')
    } else if (data?.code === 50003) {
      ElMessage.error('请勿重复发放')
    } else {
      ElMessage.error(data?.message || '发放失败')
    }
    loadCheckList()
  } catch (error) {
    ElMessage.error('发放失败')
  }
}

async function exportCheck() {
  try {
    const blob = await exportCheckExcel({ month: `${checkMonth.value}-01` })
    const url = window.URL.createObjectURL(new Blob([blob], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    }))
    const link = document.createElement('a')
    link.href = url
    link.download = `员工考勤表${checkMonth.value}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

onMounted(async () => {
  await Promise.all([loadCurrentUser(), loadFixedList(), loadTypeList()])
  loadCheckList()
})
</script>

<style scoped>
.admin-page {
  display: grid;
  gap: 16px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

:deep(.el-select),
:deep(.el-date-editor.el-input),
:deep(.el-input) {
  width: 100%;
}

@media (max-width: 720px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
