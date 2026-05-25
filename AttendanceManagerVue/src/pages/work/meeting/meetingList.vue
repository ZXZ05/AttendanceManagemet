<template>
  <section class="work-page">
    <PageHeader kicker="Meeting Center" title="会议记录" description="查看公司会议安排，管理员可发布新的会议。">
      <template #actions>
        <el-button v-if="isAdmin" type="primary" @click="openCreate">发起会议</el-button>
      </template>
    </PageHeader>

    <DataToolbar>
      <template #left>
        <el-input v-model.trim="query.keyword" clearable placeholder="标题/详情关键词" @keyup.enter="searchData" />
        <el-input v-model.trim="query.publisherName" clearable placeholder="发布人" @keyup.enter="searchData" />
        <el-select v-model="query.roomID" clearable placeholder="会议室">
          <el-option v-for="item in roomList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
        <el-date-picker
          v-model="query.beginDateRange"
          type="daterange"
          value-format="YYYY-MM-DD"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        />
      </template>
      <template #right>
        <el-button type="primary" @click="searchData">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </template>
    </DataToolbar>

    <el-card shadow="never">
      <el-table :data="tableData" border fit highlight-current-row @sort-change="handleSortChange">
        <el-table-column prop="title" label="标题" min-width="160" sortable="custom" />
        <el-table-column prop="publisherName" label="发布人" width="120" sortable="custom" />
        <el-table-column prop="beginTime" label="开始时间" min-width="170" sortable="custom" />
        <el-table-column prop="endTime" label="结束时间" min-width="170" sortable="custom" />
        <el-table-column prop="roomName" label="地点" width="140" sortable="custom" />
        <el-table-column prop="details" label="详情" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" @click="openDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          background
          layout="total, sizes, prev, pager, next, jumper"
          :page-sizes="[10, 20, 50, 100]"
          :total="filteredTotal"
        />
      </div>
    </el-card>

    <FormDialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="标题" prop="title">
          <el-input v-model.trim="form.title" :readonly="isUpdate" placeholder="请输入会议标题" />
        </el-form-item>

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
          <el-form-item label="会议室" prop="roomID">
            <el-select v-model="form.roomID" :disabled="isUpdate" placeholder="请选择会议室">
              <el-option v-for="item in roomList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="发布人">
            <el-input v-model="form.publisherName" readonly />
          </el-form-item>
        </div>

        <el-form-item label="详情">
          <el-input v-model="form.details" type="textarea" :rows="3" :readonly="isUpdate" placeholder="请输入会议详情" />
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
import DataToolbar from '@/components/common/DataToolbar.vue'
import FormDialog from '@/components/common/FormDialog.vue'
import { createMeetingOrNotice, getAllMeeting, getRoomList } from '@/api/meeting'
import { loadCurrentUser, useUser } from '@/stores/user'
import { getLoginUsername, hasPermission, PERMISSION } from '@/utils/auth'

const loginNumber = getLoginUsername() || ''
const { name, type } = useUser()
const sourceData = ref([])
const roomList = ref([])
const dialogVisible = ref(false)
const isCreate = ref(false)
const isUpdate = ref(false)
const isAdmin = computed(() => hasPermission(PERMISSION.ANNOUNCEMENT_MANAGE, type.value))
const formRef = ref()
const form = reactive(createForm())
const query = reactive(createQueryForm())
const pagination = reactive({
  page: 1,
  pageSize: 10
})
const sortState = reactive({
  prop: '',
  order: ''
})

const dialogTitle = computed(() => (isCreate.value ? '发布会议' : '会议详情'))
const rules = {
  title: [{ required: true, message: '标题不能为空', trigger: 'blur' }],
  date1: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  date2: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  date3: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
  date4: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  roomID: [{ required: true, message: '请选择会议室', trigger: 'change' }]
}

function createForm() {
  return {
    id: '',
    title: '',
    roomID: '',
    participants: '',
    host: name.value,
    details: '',
    publisherNumber: loginNumber,
    publisherName: name.value,
    publishTime: getCurrentDateTime(),
    beginTime: '',
    endTime: '',
    type: '1',
    date1: '',
    date2: '',
    date3: '',
    date4: ''
  }
}

function createQueryForm() {
  return {
    keyword: '',
    publisherName: '',
    roomID: '',
    beginDateRange: []
  }
}

function resetFormData(data = createForm()) {
  Object.assign(form, data)
}

function normalizeText(value) {
  return String(value || '').trim().toLowerCase()
}

function getSortableValue(row, prop) {
  if (!row || !prop) return ''
  const value = row[prop]
  if (value == null) return ''
  if (typeof value === 'number') return value
  return String(value).trim().toLowerCase()
}

function applySort(list) {
  if (!sortState.prop || !sortState.order) return list
  const sorted = [...list]
  const direction = sortState.order === 'ascending' ? 1 : -1
  sorted.sort((a, b) => {
    const aValue = getSortableValue(a, sortState.prop)
    const bValue = getSortableValue(b, sortState.prop)
    if (aValue > bValue) return direction
    if (aValue < bValue) return -direction
    return 0
  })
  return sorted
}

const filteredData = computed(() => {
  const keyword = normalizeText(query.keyword)
  const publisherName = normalizeText(query.publisherName)
  const [beginDate, endDate] = Array.isArray(query.beginDateRange) ? query.beginDateRange : []
  const list = sourceData.value.filter((item) => {
    if (query.roomID && item.roomID !== query.roomID) return false
    if (publisherName && !normalizeText(item.publisherName).includes(publisherName)) return false
    if (keyword) {
      const text = `${item.title || ''} ${item.details || ''}`
      if (!normalizeText(text).includes(keyword)) return false
    }
    if (beginDate && endDate) {
      const meetingDate = String(item.beginTime || '').slice(0, 10)
      if (!meetingDate || meetingDate < beginDate || meetingDate > endDate) return false
    }
    return true
  })
  return applySort(list)
})

const filteredTotal = computed(() => filteredData.value.length)
const tableData = computed(() => {
  const start = (pagination.page - 1) * pagination.pageSize
  return filteredData.value.slice(start, start + pagination.pageSize)
})

function getCurrentDateTime() {
  const date = new Date()
  const pad = (value) => String(value).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

async function loadData() {
  try {
    const data = await getAllMeeting()
    sourceData.value = Array.isArray(data) ? data : []
    pagination.page = 1
  } catch (error) {
    ElMessage.error('获取会议记录失败')
  }
}

async function loadRooms() {
  try {
    roomList.value = await getRoomList()
  } catch (error) {
    ElMessage.error('获取会议室失败')
  }
}

async function openCreate() {
  await loadRooms()
  resetFormData(createForm())
  form.publisherName = name.value
  form.publisherNumber = loginNumber
  form.host = name.value
  form.publishTime = getCurrentDateTime()
  isCreate.value = true
  isUpdate.value = false
  dialogVisible.value = true
}

function searchData() {
  pagination.page = 1
}

function resetSearch() {
  Object.assign(query, createQueryForm())
  sortState.prop = ''
  sortState.order = ''
  pagination.page = 1
}

function handleSortChange({ prop, order }) {
  sortState.prop = prop || ''
  sortState.order = order || ''
  pagination.page = 1
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

  form.beginTime = `${form.date1} ${form.date2}`
  form.endTime = `${form.date3} ${form.date4}`
  form.type = '1'

  try {
    await createMeetingOrNotice({ ...form })
    ElMessage.success('发布成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('发布失败')
  }
}

onMounted(async () => {
  await Promise.all([loadCurrentUser(), loadRooms()])
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

:deep(.el-date-editor.el-input),
:deep(.el-select) {
  width: 100%;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 14px;
}

@media (max-width: 720px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
