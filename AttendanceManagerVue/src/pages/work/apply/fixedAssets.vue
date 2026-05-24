<template>
  <section class="work-page">
    <PageHeader kicker="Asset Request" title="固定资产申请" description="发起固定资产采购或领用申请，并跟踪审批结果。">
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
        <h3>{{ item.name || '固定资产申请' }}</h3>
        <p>类型：{{ item.typeName || '-' }}</p>
        <p>申请时间：{{ item.applyTime || '-' }}</p>
      </el-card>
    </div>
    <EmptyState v-else description="暂无固定资产申请" />

    <FormDialog v-model="dialogVisible" :title="dialogTitle" width="680px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <div class="form-grid">
          <el-form-item label="申请人" prop="employeeName">
            <el-input v-model="form.employeeName" readonly />
          </el-form-item>
          <el-form-item v-if="isUpdate" label="申请时间" prop="applyTime">
            <el-input v-model="form.applyTime" readonly />
          </el-form-item>
          <el-form-item label="资产编号" prop="number">
            <el-input v-model="form.number" :readonly="isUpdate" placeholder="请输入固定资产编号" />
          </el-form-item>
          <el-form-item label="资产名称" prop="name">
            <el-input v-model="form.name" :readonly="isUpdate" placeholder="请输入固定资产名称" />
          </el-form-item>
          <el-form-item label="价格" prop="price">
            <el-input v-model.number="form.price" :readonly="isUpdate" placeholder="请输入价格" />
          </el-form-item>
          <el-form-item label="资产类别" prop="typeID">
            <el-select v-model="form.typeID" :disabled="isUpdate" placeholder="请选择固定资产类别">
              <el-option v-for="item in typeList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item v-if="showApprovalInfo" label="审批人">
            <el-input v-model="form.approvalName" readonly />
          </el-form-item>
          <el-form-item v-if="showApprovalInfo" label="审批时间">
            <el-input v-model="form.approvalTime" readonly />
          </el-form-item>
          <el-form-item v-if="isUpdate" label="审批状态">
            <el-input v-model="form.statusName" readonly />
          </el-form-item>
        </div>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button v-if="isCreate" type="primary" @click="submitForm">立即申请</el-button>
      </template>
    </FormDialog>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import FormDialog from '@/components/common/FormDialog.vue'
import { createFixedAsset, findFixedAssetByEmployeeNumber, getFixedAssetTypeList } from '@/api/application'
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
const dialogTitle = computed(() => (isCreate.value ? '发起固定资产申请' : '申请详情'))
const showApprovalInfo = computed(() => isUpdate.value && ['1', '2'].includes(String(form.status)))

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

function createForm() {
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

function getCurrentDateTime() {
  const date = new Date()
  const pad = (value) => String(value).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

async function loadData() {
  try {
    const data = await findFixedAssetByEmployeeNumber({ employeeNumber: loginNumber })
    tableData.value = (Array.isArray(data) ? data : []).map((item) => ({
      ...item,
      statusName: statusText(item.status)
    }))
  } catch (error) {
    ElMessage.error('获取固定资产申请失败')
  }
}

async function loadTypes() {
  try {
    typeList.value = await getFixedAssetTypeList()
  } catch (error) {
    ElMessage.error('获取资产类别失败')
  }
}

async function openCreate() {
  await loadTypes()
  resetFormData(createForm())
  form.employeeName = name.value
  form.employeeNumber = loginNumber
  form.applyTime = getCurrentDateTime()
  isCreate.value = true
  isUpdate.value = false
  dialogVisible.value = true
}

function openDetail(row) {
  resetFormData({ ...createForm(), ...row, employeeName: row.employeeName || name.value, statusName: statusText(row.status) })
  isCreate.value = false
  isUpdate.value = true
  dialogVisible.value = true
}

async function submitForm() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  try {
    const data = await createFixedAsset({ ...form })
    if (data?.code === 20005) {
      ElMessage.error('该编号已存在')
      return
    }
    ElMessage.success('申请成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('申请失败，请稍后重试')
  }
}

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
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
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

:deep(.el-select) {
  width: 100%;
}

@media (max-width: 720px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
