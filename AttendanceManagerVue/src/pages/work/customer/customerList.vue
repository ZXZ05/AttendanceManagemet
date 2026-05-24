<template>
  <section class="work-page">
    <PageHeader kicker="Customer Desk" title="客户管理" description="维护客户档案，管理员可查看全部客户，普通员工查看本人客户。">
      <template #actions>
        <el-button type="primary" @click="openCreate">新增客户</el-button>
      </template>
    </PageHeader>

    <DataToolbar>
      <template #left>
        <el-select v-model="query.type" clearable placeholder="客户类型">
          <el-option v-for="item in customerTypeList" :key="item.id" :label="item.value" :value="item.value" />
        </el-select>
        <el-input v-model.trim="query.name" clearable placeholder="客户名称" @keyup.enter="searchData" />
      </template>
      <template #right>
        <el-button type="primary" @click="searchData">查询</el-button>
        <el-button @click="loadData">重置</el-button>
      </template>
    </DataToolbar>

    <el-card shadow="never">
      <el-table :data="tableData" border fit highlight-current-row>
        <el-table-column prop="number" label="客户编号" width="130" />
        <el-table-column prop="name" label="客户名称" min-width="150" />
        <el-table-column prop="phone" label="联系电话" width="140" />
        <el-table-column prop="address" label="地址" min-width="180" />
        <el-table-column prop="type" label="类型" width="120" />
        <el-table-column prop="remarks" label="备注" min-width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" @click="openUpdate(row)">编辑</el-button>
            <el-button type="danger" @click="deleteData(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <FormDialog v-model="dialogVisible" :title="dialogTitle" width="640px">
      <el-form ref="formRef" :rules="rules" :model="form" label-position="top">
        <div class="form-grid">
          <el-form-item label="客户编号" prop="number">
            <el-input v-model.trim="form.number" placeholder="请输入客户编号" />
          </el-form-item>
          <el-form-item label="客户名称" prop="name">
            <el-input v-model.trim="form.name" placeholder="请输入客户名称" />
          </el-form-item>
          <el-form-item label="客户类型" prop="type">
            <el-select v-model="form.type" clearable placeholder="请选择客户类型">
              <el-option v-for="item in customerTypeList" :key="item.id" :label="item.value" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="联系电话">
            <el-input v-model.trim="form.phone" placeholder="请输入联系电话" />
          </el-form-item>
          <el-form-item label="地址">
            <el-input v-model.trim="form.address" placeholder="请输入地址" />
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model.trim="form.remarks" placeholder="请输入备注" />
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
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '@/components/common/PageHeader.vue'
import DataToolbar from '@/components/common/DataToolbar.vue'
import FormDialog from '@/components/common/FormDialog.vue'
import { createCustomer, deleteCustomerById, findCustomerByApplyNumber, findCustomerByNameAndType, getCustomerList, updateCustomer } from '@/api/customer'
import { loadCurrentUser, useUser } from '@/stores/user'
import { canAccessAdminPortal, getLoginUsername } from '@/utils/auth'

const loginNumber = getLoginUsername() || ''
const { type } = useUser()
const tableData = ref([])
const dialogVisible = ref(false)
const isCreate = ref(false)
const isAdmin = computed(() => canAccessAdminPortal(type.value))
const formRef = ref()

const customerTypeList = [
  { id: '1', value: '潜在客户' },
  { id: '2', value: '合作客户' }
]

const query = reactive(createCustomerForm())
const form = reactive(createCustomerForm())
const dialogTitle = computed(() => (isCreate.value ? '新增客户' : '编辑客户'))

const rules = {
  number: [{ required: true, message: '请输入客户编号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入客户名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择客户类型', trigger: 'change' }]
}

function createCustomerForm() {
  return {
    id: '',
    number: '',
    name: '',
    type: '',
    phone: '',
    address: '',
    remarks: '',
    applyNumber: ''
  }
}

function resetFormData(target, source = createCustomerForm()) {
  Object.assign(target, source)
}

async function loadData() {
  resetFormData(query)
  try {
    tableData.value = isAdmin.value ? await getCustomerList() : await findCustomerByApplyNumber(loginNumber)
  } catch (error) {
    ElMessage.error('获取客户列表失败')
  }
}

async function searchData() {
  try {
    tableData.value = await findCustomerByNameAndType({ ...query })
  } catch (error) {
    ElMessage.error('查询客户失败')
  }
}

function openCreate() {
  resetFormData(form)
  form.applyNumber = loginNumber
  isCreate.value = true
  dialogVisible.value = true
}

function openUpdate(row) {
  resetFormData(form, { ...row })
  isCreate.value = false
  dialogVisible.value = true
}

async function submitData() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  try {
    const data = isCreate.value ? await createCustomer({ ...form, applyNumber: loginNumber }) : await updateCustomer({ ...form })
    if (data?.code === 20005) {
      ElMessage.error('该客户编号已存在')
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
  await ElMessageBox.confirm(`确认删除客户 ${row.name || row.id}？`, '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  })
  try {
    await deleteCustomerById(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    ElMessage.error('删除失败')
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

:deep(.el-select),
:deep(.el-input) {
  min-width: 180px;
}

@media (max-width: 720px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
