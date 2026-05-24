<template>
  <section class="admin-page">
    <PageHeader kicker="Organization" title="部门职位" description="维护组织结构、岗位类别和岗位薪资基础数据。">
      <template #actions>
        <el-button type="primary" @click="openDeptCreate">新增部门</el-button>
        <el-button type="success" @click="openPosCreate">新增岗位</el-button>
      </template>
    </PageHeader>

    <el-card shadow="never">
      <el-table :data="departments" border fit highlight-current-row>
        <el-table-column prop="number" label="部门编号" min-width="130" />
        <el-table-column prop="name" label="部门名称" min-width="150" />
        <el-table-column prop="quantity" label="部门人数" width="120" />
        <el-table-column prop="posNum" label="岗位数量" width="120" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" @click="viewPositions(row)">岗位</el-button>
            <el-button type="success" @click="openDeptEdit(row)">编辑</el-button>
            <el-button type="danger" @click="deleteDept(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <FormDialog v-model="deptDialogVisible" :title="deptMode === 'create' ? '新增部门' : '编辑部门'" width="560px">
      <el-form ref="deptFormRef" :model="deptForm" :rules="deptRules" label-position="top">
        <div class="form-grid">
          <el-form-item label="编号" prop="number">
            <el-input v-model.trim="deptForm.number" placeholder="请输入部门编号" />
          </el-form-item>
          <el-form-item label="名称" prop="name">
            <el-input v-model.trim="deptForm.name" placeholder="请输入部门名称" />
          </el-form-item>
          <el-form-item v-if="deptMode === 'edit'" label="部门人数">
            <el-input v-model="deptForm.quantity" readonly />
          </el-form-item>
          <el-form-item v-if="deptMode === 'edit'" label="岗位数量">
            <el-input v-model="deptForm.posNum" readonly />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="deptDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveDept">保存</el-button>
      </template>
    </FormDialog>

    <FormDialog v-model="posDialogVisible" :title="posMode === 'create' ? '新增岗位' : '编辑岗位'" width="640px">
      <el-form ref="posFormRef" :model="posForm" :rules="posRules" label-position="top">
        <div class="form-grid">
          <el-form-item label="编号" prop="number">
            <el-input v-model.trim="posForm.number" placeholder="请输入岗位编号" />
          </el-form-item>
          <el-form-item label="类别" prop="typeID">
            <el-select v-model="posForm.typeID" placeholder="请选择岗位类别">
              <el-option v-for="item in typeList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="所属部门" prop="departmentID">
            <el-select v-model="posForm.departmentID" placeholder="请选择所属部门">
              <el-option v-for="item in departments" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="名称" prop="name">
            <el-input v-model.trim="posForm.name" placeholder="请输入岗位名称" />
          </el-form-item>
          <el-form-item label="月薪" prop="monthlySalary">
            <el-input v-model.number="posForm.monthlySalary" placeholder="请输入岗位月薪" />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="posDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="savePos">保存</el-button>
      </template>
    </FormDialog>

    <FormDialog v-model="posListVisible" title="岗位列表" width="760px">
      <el-table :data="positions" border fit highlight-current-row>
        <el-table-column prop="number" label="岗位编号" width="130" />
        <el-table-column prop="name" label="岗位名称" min-width="150" />
        <el-table-column prop="monthlySalary" label="岗位月薪" width="130" />
        <el-table-column prop="quantity" label="在岗人数" width="120" />
        <el-table-column label="操作" width="170" fixed="right">
          <template #default="{ row }">
            <el-button type="success" @click="openPosEdit(row)">编辑</el-button>
            <el-button type="danger" @click="deletePos(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button type="primary" @click="posListVisible = false">关闭</el-button>
      </template>
    </FormDialog>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '@/components/common/PageHeader.vue'
import FormDialog from '@/components/common/FormDialog.vue'
import { createDepartment, createPosition, deleteDepartmentById, deletePositionById, getDepartmentList, updateDepartment, updatePosition } from '@/api/admin'
import { findPositionsByDepartmentID, getEmployeeTypeList } from '@/api/employee'

const departments = ref([])
const positions = ref([])
const typeList = ref([])
const selectedDepartmentId = ref('')

const deptDialogVisible = ref(false)
const deptMode = ref('create')
const deptForm = reactive(emptyDept())
const deptFormRef = ref()

const posDialogVisible = ref(false)
const posMode = ref('create')
const posForm = reactive(emptyPos())
const posFormRef = ref()
const posListVisible = ref(false)

const deptRules = {
  number: [{ required: true, message: '请输入部门编号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入部门名称', trigger: 'blur' }]
}

const posRules = {
  number: [{ required: true, message: '请输入岗位编号', trigger: 'blur' }],
  typeID: [{ required: true, message: '请选择岗位类别', trigger: 'change' }],
  departmentID: [{ required: true, message: '请选择所属部门', trigger: 'change' }],
  name: [{ required: true, message: '请输入岗位名称', trigger: 'blur' }],
  monthlySalary: [{ required: true, message: '请输入岗位月薪', trigger: 'blur' }]
}

function emptyDept() {
  return {
    id: '',
    number: '',
    name: '',
    quantity: '',
    posNum: ''
  }
}

function emptyPos() {
  return {
    id: '',
    number: '',
    name: '',
    quantity: '',
    monthlySalary: '',
    departmentID: '',
    typeID: ''
  }
}

function reset(target, source) {
  Object.assign(target, source)
}

async function loadDepartments() {
  try {
    departments.value = await getDepartmentList()
  } catch (error) {
    ElMessage.error('获取部门列表失败')
  }
}

async function loadTypeList() {
  try {
    typeList.value = await getEmployeeTypeList()
  } catch (error) {
    typeList.value = []
  }
}

async function viewPositions(row) {
  selectedDepartmentId.value = row.id
  try {
    positions.value = await findPositionsByDepartmentID(row.id)
    posListVisible.value = true
  } catch (error) {
    ElMessage.error('获取岗位列表失败')
  }
}

function openDeptCreate() {
  deptMode.value = 'create'
  reset(deptForm, emptyDept())
  deptDialogVisible.value = true
}

function openDeptEdit(row) {
  deptMode.value = 'edit'
  reset(deptForm, { ...row })
  deptDialogVisible.value = true
}

async function saveDept() {
  const valid = await deptFormRef.value?.validate().catch(() => false)
  if (!valid) return

  try {
    const data = deptMode.value === 'create' ? await createDepartment({ ...deptForm }) : await updateDepartment({ ...deptForm })
    if (deptMode.value === 'create' && data?.code === 20005) {
      ElMessage.error('部门编号已存在')
      return
    }
    ElMessage.success(deptMode.value === 'create' ? '新增成功' : '修改成功')
    deptDialogVisible.value = false
    loadDepartments()
  } catch (error) {
    ElMessage.error('保存部门失败')
  }
}

async function deleteDept(row) {
  await ElMessageBox.confirm(`确定删除部门 ${row.name} 吗？`, '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  })

  try {
    const data = await deleteDepartmentById(row.id)
    if (data?.code === 50003) {
      ElMessage.error('该部门还有员工，无法删除')
      return
    }
    ElMessage.success('删除成功')
    loadDepartments()
  } catch (error) {
    ElMessage.error('删除部门失败')
  }
}

function openPosCreate() {
  posMode.value = 'create'
  reset(posForm, emptyPos())
  if (selectedDepartmentId.value) {
    posForm.departmentID = selectedDepartmentId.value
  }
  posDialogVisible.value = true
}

function openPosEdit(row) {
  posMode.value = 'edit'
  reset(posForm, { ...row })
  posDialogVisible.value = true
}

async function savePos() {
  const valid = await posFormRef.value?.validate().catch(() => false)
  if (!valid) return

  try {
    const data = posMode.value === 'create' ? await createPosition({ ...posForm }) : await updatePosition({ ...posForm })
    if (posMode.value === 'create' && data?.code === 20005) {
      ElMessage.error('岗位编号已存在')
      return
    }
    ElMessage.success(posMode.value === 'create' ? '新增成功' : '修改成功')
    posDialogVisible.value = false
    await loadDepartments()
    if (posForm.departmentID) {
      viewPositions({ id: posForm.departmentID })
    }
  } catch (error) {
    ElMessage.error('保存岗位失败')
  }
}

async function deletePos(row) {
  await ElMessageBox.confirm(`确定删除岗位 ${row.name} 吗？`, '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  })

  try {
    const data = await deletePositionById(row.id)
    if (data?.code === 50003) {
      ElMessage.error('该岗位还有员工，无法删除')
      return
    }
    ElMessage.success('删除成功')
    await loadDepartments()
    if (row.departmentID) {
      viewPositions({ id: row.departmentID })
    }
  } catch (error) {
    ElMessage.error('删除岗位失败')
  }
}

onMounted(() => {
  loadDepartments()
  loadTypeList()
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
:deep(.el-input) {
  width: 100%;
}

@media (max-width: 720px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
