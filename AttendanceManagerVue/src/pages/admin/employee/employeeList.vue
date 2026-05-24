<template>
  <section class="admin-page">
    <PageHeader kicker="People Ops" title="员工管理" description="维护员工档案、部门职位与基础人事信息。">
      <template #actions>
        <el-button type="primary" @click="openCreate">新增员工</el-button>
      </template>
    </PageHeader>

    <DataToolbar>
      <template #left>
        <el-select v-model="query.departmentID" clearable placeholder="选择部门">
          <el-option v-for="item in departmentList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
        <el-input v-model.trim="query.name" clearable placeholder="输入姓名" @keyup.enter="searchData" />
      </template>
      <template #right>
        <el-button type="primary" @click="searchData">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </template>
    </DataToolbar>

    <el-card shadow="never">
      <el-table :data="tableData" border fit highlight-current-row>
        <el-table-column prop="number" label="编号" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="sex" label="性别" width="90" />
        <el-table-column prop="birthday" label="出生日期" width="130" />
        <el-table-column prop="departmentName" label="所属部门" min-width="140" />
        <el-table-column prop="positionName" label="职位" min-width="140" />
        <el-table-column prop="entryDate" label="入职日期" width="130" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" @click="openUpdate(row)">编辑</el-button>
            <el-button type="danger" @click="deleteData(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <FormDialog v-model="dialogVisible" :title="dialogTitle" width="760px">
      <el-form ref="formRef" :rules="rules" :model="form" label-position="top">
        <div class="form-grid">
          <el-form-item label="编号" prop="number">
            <el-input v-model.trim="form.number" placeholder="请输入编号" />
          </el-form-item>
          <el-form-item label="姓名" prop="name">
            <el-input v-model.trim="form.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="性别">
            <el-select v-model="form.sex" clearable placeholder="请选择">
              <el-option v-for="item in sexList" :key="item.id" :label="item.value" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="出生日期">
            <el-date-picker v-model="form.birthday" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
          </el-form-item>
          <el-form-item label="身份证号码" prop="idNumber">
            <el-input v-model.trim="form.idNumber" placeholder="请输入身份证号码" />
          </el-form-item>
          <el-form-item label="学历">
            <el-select v-model="form.education" clearable placeholder="请选择">
              <el-option v-for="item in educationList" :key="item.id" :label="item.name" :value="item.name" />
            </el-select>
          </el-form-item>
          <el-form-item label="婚姻状况">
            <el-select v-model="form.marriage" clearable placeholder="请选择">
              <el-option v-for="item in marriageList" :key="item.id" :label="item.value" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="所属部门" prop="departmentID">
            <el-select v-model="form.departmentID" clearable placeholder="请选择部门" @change="handleDepartmentChange">
              <el-option v-for="item in departmentList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="职位" prop="position">
            <el-select v-model="form.position" clearable placeholder="请选择职位">
              <el-option v-for="item in positionList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="入职日期" prop="entryDate">
            <el-date-picker v-model="form.entryDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
          </el-form-item>
          <el-form-item label="联系方式">
            <el-input v-model.trim="form.phone" placeholder="请输入联系方式" />
          </el-form-item>
          <el-form-item label="家庭住址">
            <el-input v-model.trim="form.address" placeholder="请输入家庭住址" />
          </el-form-item>
        </div>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitData">{{ isCreate ? '新增' : '保存' }}</el-button>
      </template>
    </FormDialog>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '@/components/common/PageHeader.vue'
import DataToolbar from '@/components/common/DataToolbar.vue'
import FormDialog from '@/components/common/FormDialog.vue'
import { getDepartmentList } from '@/api/admin'
import { createEmployee, deleteEmployeeById, findEmployeeByNameAndDepartment, findPositionsByDepartmentID, getEmployeeList, getEmployeeTypeList, updateEmployee } from '@/api/employee'
import { isAuthenticated } from '@/utils/auth'

const router = useRouter()
const tableData = ref([])
const departmentList = ref([])
const positionList = ref([])
const typeList = ref([])
const dialogVisible = ref(false)
const isCreate = ref(false)
const formRef = ref()

const query = reactive(createEmployeeForm())
const form = reactive(createEmployeeForm())
const dialogTitle = computed(() => (isCreate.value ? '新增员工' : '编辑员工'))

const sexList = [
  { id: '1', value: '男' },
  { id: '2', value: '女' }
]

const marriageList = [
  { id: '1', value: '未婚' },
  { id: '2', value: '已婚' }
]

const educationList = [
  { id: '1', name: '初中' },
  { id: '2', name: '高中' },
  { id: '3', name: '中专' },
  { id: '4', name: '大专' },
  { id: '5', name: '本科' },
  { id: '6', name: '硕士' },
  { id: '7', name: '博士及以上' }
]

const rules = {
  number: [
    { required: true, message: '请输入编号', trigger: 'blur' },
    { min: 3, max: 15, message: '长度需在 3 到 15 个字符之间', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 15, message: '长度需在 2 到 15 个字符之间', trigger: 'blur' }
  ],
  entryDate: [{ required: true, message: '请选择入职日期', trigger: 'change' }],
  idNumber: [{ required: true, message: '身份证号码不能为空', trigger: 'blur' }],
  departmentID: [{ required: true, message: '部门不能为空', trigger: 'change' }],
  position: [{ required: true, message: '职位不能为空', trigger: 'change' }]
}

function createEmployeeForm() {
  return {
    id: '',
    number: '',
    name: '',
    sex: '',
    birthday: '',
    departmentID: '',
    position: '',
    type: '',
    entryDate: '',
    phone: '',
    address: '',
    idNumber: '',
    education: '',
    employeeType: '',
    marriage: '',
    workStatus: ''
  }
}

function resetFormData(target, source = createEmployeeForm()) {
  Object.assign(target, source)
}

async function loadData() {
  if (!isAuthenticated()) {
    ElMessage.error('未登录或登录信息过期')
    router.push('/')
    return
  }

  try {
    tableData.value = await getEmployeeList()
  } catch (error) {
    ElMessage.error('获取员工列表失败')
  }
}

async function loadDepartments() {
  try {
    departmentList.value = await getDepartmentList()
  } catch (error) {
    ElMessage.error('获取部门列表失败')
  }
}

async function loadTypes() {
  try {
    typeList.value = await getEmployeeTypeList()
  } catch (error) {
    typeList.value = []
  }
}

async function handleDepartmentChange(value) {
  form.position = ''
  if (!value) {
    positionList.value = []
    return
  }
  try {
    positionList.value = await findPositionsByDepartmentID(value)
  } catch (error) {
    ElMessage.error('获取职位列表失败')
  }
}

async function searchData() {
  try {
    tableData.value = await findEmployeeByNameAndDepartment({ ...query })
  } catch (error) {
    ElMessage.error('查询员工失败')
  }
}

function resetSearch() {
  resetFormData(query)
  loadData()
}

function openCreate() {
  resetFormData(form)
  positionList.value = []
  isCreate.value = true
  dialogVisible.value = true
}

async function openUpdate(row) {
  resetFormData(form, { ...row })
  isCreate.value = false
  dialogVisible.value = true
  if (form.departmentID) {
    try {
      positionList.value = await findPositionsByDepartmentID(form.departmentID)
    } catch (error) {
      positionList.value = []
    }
  }
}

async function submitData() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  try {
    const data = isCreate.value ? await createEmployee({ ...form }) : await updateEmployee({ ...form })
    if (data?.code === 20005) {
      ElMessage.error('该编号已存在')
      return
    }
    if (!isCreate.value && data !== '1' && data?.code && data.code !== 200) {
      ElMessage.error(data?.message || '保存失败')
      return
    }
    ElMessage.success(isCreate.value ? '新增成功' : '保存成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error(isCreate.value ? '新增失败' : '保存失败')
  }
}

async function deleteData(row) {
  await ElMessageBox.confirm(`确定删除 ${row.name}？`, '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  })

  try {
    const data = await deleteEmployeeById(row.id)
    if (data === 200 || data?.code === 200 || data === '200') {
      ElMessage.success('删除成功')
      loadData()
      return
    }
    ElMessage.error(data?.message || '删除失败')
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadData()
  loadDepartments()
  loadTypes()
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
