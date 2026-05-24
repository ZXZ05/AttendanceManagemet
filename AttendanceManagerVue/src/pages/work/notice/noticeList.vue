<template>
  <section class="work-page">
    <PageHeader kicker="Notice Board" title="通知公告" description="查看公司通知，管理员可发布新的公告。">
      <template #actions>
        <el-button v-if="isAdmin" type="primary" @click="openCreate">发布通知</el-button>
      </template>
    </PageHeader>

    <el-card shadow="never">
      <el-table :data="tableData" border fit highlight-current-row>
        <el-table-column prop="title" label="标题" min-width="180" />
        <el-table-column prop="publisherName" label="发布人" width="120" />
        <el-table-column prop="details" label="详情" min-width="220" show-overflow-tooltip />
        <el-table-column prop="beginDate" label="开始日期" width="130" />
        <el-table-column prop="endDate" label="结束日期" width="130" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" @click="openDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <FormDialog v-model="dialogVisible" :title="dialogTitle" width="680px">
      <el-form ref="formRef" :rules="rules" :model="form" label-position="top">
        <el-form-item label="标题" prop="title">
          <el-input v-model.trim="form.title" :readonly="isUpdate" placeholder="请输入通知标题" />
        </el-form-item>

        <div class="form-grid">
          <el-form-item label="开始日期" prop="beginDate">
            <el-date-picker v-model="form.beginDate" type="date" value-format="YYYY-MM-DD" :readonly="isUpdate" placeholder="选择开始日期" />
          </el-form-item>
          <el-form-item label="结束日期" prop="endDate">
            <el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" :readonly="isUpdate" placeholder="选择结束日期" />
          </el-form-item>
          <el-form-item label="发布人">
            <el-input v-model="form.publisherName" readonly />
          </el-form-item>
          <el-form-item v-if="isUpdate" label="发布时间">
            <el-input v-model="form.publishTime" readonly />
          </el-form-item>
        </div>

        <el-form-item label="详情">
          <el-input v-model="form.details" type="textarea" :rows="3" :readonly="isUpdate" placeholder="请输入通知详情" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button v-if="isCreate" type="primary" @click="submitData">确定</el-button>
      </template>
    </FormDialog>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/common/PageHeader.vue'
import FormDialog from '@/components/common/FormDialog.vue'
import { createMeetingOrNotice, getAllNotice } from '@/api/meeting'
import { loadCurrentUser, useUser } from '@/stores/user'
import { getLoginUsername, hasPermission, PERMISSION } from '@/utils/auth'

const loginNumber = getLoginUsername() || ''
const { name, type } = useUser()
const tableData = ref([])
const dialogVisible = ref(false)
const isCreate = ref(false)
const isUpdate = ref(false)
const isAdmin = computed(() => hasPermission(PERMISSION.ANNOUNCEMENT_MANAGE, type.value))
const formRef = ref()
const form = reactive(createForm())

const dialogTitle = computed(() => (isCreate.value ? '发布通知' : '通知详情'))
const rules = {
  title: [{ required: true, message: '标题不能为空', trigger: 'blur' }],
  beginDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }]
}

function createForm() {
  return {
    id: '',
    title: '',
    details: '',
    publisherNumber: loginNumber,
    publisherName: name.value,
    publishTime: getCurrentDateTime(),
    beginDate: '',
    endDate: '',
    type: '2'
  }
}

function resetFormData(data = createForm()) {
  Object.assign(form, data)
}

function getCurrentDateTime() {
  const date = new Date()
  const pad = (value) => String(value).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

async function loadData() {
  try {
    tableData.value = await getAllNotice()
  } catch (error) {
    ElMessage.error('获取通知失败')
  }
}

function openCreate() {
  resetFormData(createForm())
  form.publisherName = name.value
  form.publisherNumber = loginNumber
  form.publishTime = getCurrentDateTime()
  form.type = '2'
  isCreate.value = true
  isUpdate.value = false
  dialogVisible.value = true
}

function openDetail(row) {
  resetFormData({ ...createForm(), ...row })
  isCreate.value = false
  isUpdate.value = true
  dialogVisible.value = true
}

async function submitData() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  try {
    await createMeetingOrNotice({ ...form, type: '2' })
    ElMessage.success('发布成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('发布失败')
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

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

:deep(.el-date-editor.el-input) {
  width: 100%;
}

@media (max-width: 720px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
