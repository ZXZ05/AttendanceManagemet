<template>
  <section class="profile-page">
    <PageHeader kicker="Profile Center" title="个人中心" description="维护个人资料、修改密码并查看最近登录记录。" />

    <el-row :gutter="16">
      <el-col :xs="24" :lg="16">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <strong>个人资料</strong>
              <el-button type="primary" text @click="loadProfile">刷新</el-button>
            </div>
          </template>

          <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-position="top">
            <div class="profile-grid">
              <el-form-item label="头像">
                <div class="avatar-block">
                  <el-avatar :size="72" :src="profileForm.avatarUrl || undefined">
                    {{ profileForm.name?.slice(0, 1) || 'U' }}
                  </el-avatar>
                  <div class="avatar-actions">
                    <el-upload
                      :show-file-list="false"
                      :auto-upload="false"
                      :before-upload="handleBeforeUpload"
                      accept="image/png,image/jpeg,image/jpg,image/webp"
                    >
                      <el-button type="primary" :loading="uploadingAvatar">上传头像</el-button>
                    </el-upload>
                    <el-button @click="clearAvatar">清空头像</el-button>
                  </div>
                </div>
              </el-form-item>

              <el-form-item label="工号">
                <el-input v-model="profileForm.number" readonly />
              </el-form-item>
              <el-form-item label="姓名">
                <el-input v-model="profileForm.name" readonly />
              </el-form-item>
              <el-form-item label="角色">
                <el-input :model-value="roleLabel" readonly />
              </el-form-item>
              <el-form-item label="所属部门">
                <el-input v-model="profileForm.departmentName" readonly />
              </el-form-item>
              <el-form-item label="职位">
                <el-input v-model="profileForm.positionName" readonly />
              </el-form-item>
              <el-form-item label="入职日期">
                <el-input v-model="profileForm.entryDate" readonly />
              </el-form-item>
              <el-form-item label="联系电话" prop="phone">
                <el-input v-model.trim="profileForm.phone" placeholder="请输入联系电话" />
              </el-form-item>
              <el-form-item label="联系地址" prop="address" class="address-item">
                <el-input v-model.trim="profileForm.address" type="textarea" :rows="3" placeholder="请输入联系地址" />
              </el-form-item>
            </div>
            <el-form-item>
              <el-button type="primary" @click="saveProfile">保存资料</el-button>
              <el-button @click="loadProfile">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="8">
        <el-card shadow="never">
          <template #header>
            <strong>修改密码</strong>
          </template>
          <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-position="top">
            <el-form-item label="旧密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" show-password autocomplete="off" placeholder="请输入旧密码" />
            </el-form-item>
            <el-form-item label="新密码" prop="password">
              <el-input v-model="passwordForm.password" type="password" show-password autocomplete="off" placeholder="请输入新密码" />
            </el-form-item>
            <el-form-item label="确认密码" prop="checkPass">
              <el-input v-model="passwordForm.checkPass" type="password" show-password autocomplete="off" placeholder="请再次输入新密码" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitPassword">提交</el-button>
              <el-button @click="resetPasswordForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <strong>最近登录记录</strong>
          <el-button type="primary" text @click="loadLoginRecords">刷新</el-button>
        </div>
      </template>
      <el-table :data="loginRecords" border fit highlight-current-row>
        <el-table-column prop="loginTime" label="登录时间" min-width="180" />
        <el-table-column prop="ipAddress" label="IP 地址" width="150" />
        <el-table-column prop="loginStatus" label="状态" width="100" />
        <el-table-column prop="userAgent" label="设备信息" min-width="280" show-overflow-tooltip />
      </el-table>
    </el-card>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/common/PageHeader.vue'
import { getEmployeeProfile, getLoginRecords, updateEmployeeProfile, updatePassword } from '@/api/employee'
import { uploadAvatar } from '@/api/file'
import { getLoginUsername, getRoleLabel } from '@/utils/auth'

const profileFormRef = ref()
const passwordFormRef = ref()
const profileForm = reactive(createProfileForm())
const passwordForm = reactive(createPasswordForm())
const loginRecords = ref([])
const uploadingAvatar = ref(false)

const roleLabel = computed(() => getRoleLabel(profileForm.type))

const profileRules = {
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { min: 6, max: 20, message: '联系电话长度需在 6 到 20 之间', trigger: 'blur' }
  ],
  address: [{ max: 255, message: '地址不能超过 255 个字符', trigger: 'blur' }]
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度需在 6 到 32 位之间', trigger: 'blur' }
  ],
  checkPass: [
    {
      validator: (_, value, callback) => {
        if (!value) {
          callback(new Error('请再次输入密码'))
          return
        }
        if (value !== passwordForm.password) {
          callback(new Error('两次输入密码不一致'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ]
}

function createProfileForm() {
  return {
    number: '',
    name: '',
    type: '',
    departmentName: '',
    positionName: '',
    entryDate: '',
    phone: '',
    address: '',
    avatar: '',
    avatarUrl: ''
  }
}

function createPasswordForm() {
  return {
    number: getLoginUsername(),
    oldPassword: '',
    password: '',
    checkPass: ''
  }
}

function resetProfileForm(data = createProfileForm()) {
  Object.assign(profileForm, data)
}

async function loadProfile() {
  try {
    const data = await getEmployeeProfile()
    resetProfileForm({
      number: data?.number || '',
      name: data?.name || '',
      type: data?.type || '',
      departmentName: data?.departmentName || '',
      positionName: data?.positionName || '',
      entryDate: data?.entryDate || '',
      phone: data?.phone || '',
      address: data?.address || '',
      avatar: data?.avatar || '',
      avatarUrl: data?.avatarUrl || ''
    })
  } catch (error) {
    ElMessage.error('加载个人资料失败')
  }
}

async function saveProfile() {
  const valid = await profileFormRef.value?.validate().catch(() => false)
  if (!valid) return

  try {
    const data = await updateEmployeeProfile({
      phone: profileForm.phone,
      address: profileForm.address,
      avatar: profileForm.avatar
    })
    resetProfileForm({
      ...profileForm,
      phone: data?.phone ?? profileForm.phone,
      address: data?.address ?? profileForm.address,
      avatar: data?.avatar ?? profileForm.avatar,
      avatarUrl: data?.avatarUrl ?? profileForm.avatarUrl
    })
    ElMessage.success('资料更新成功')
  } catch (error) {
    ElMessage.error(error?.message || '资料更新失败')
  }
}

async function handleBeforeUpload(file) {
  const isImage = /^image\//.test(file.type)
  if (!isImage) {
    ElMessage.error('仅支持上传图片文件')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB')
    return false
  }
  uploadingAvatar.value = true
  try {
    const data = await uploadAvatar(file)
    profileForm.avatar = data?.key || ''
    profileForm.avatarUrl = data?.url || ''
    ElMessage.success('头像上传成功')
  } catch (error) {
    ElMessage.error(error?.message || '头像上传失败')
  } finally {
    uploadingAvatar.value = false
  }
  return false
}

function clearAvatar() {
  profileForm.avatar = ''
  profileForm.avatarUrl = ''
}

async function submitPassword() {
  const valid = await passwordFormRef.value?.validate().catch(() => false)
  if (!valid) return

  try {
    const data = await updatePassword({
      number: passwordForm.number,
      oldPassword: passwordForm.oldPassword,
      password: passwordForm.password
    })
    if (data?.code === 200) {
      ElMessage.success('密码修改成功')
      resetPasswordForm()
      return
    }
    ElMessage.error(data?.message || '密码修改失败')
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || '密码修改失败')
  }
}

function resetPasswordForm() {
  passwordFormRef.value?.resetFields()
}

async function loadLoginRecords() {
  try {
    const data = await getLoginRecords({ limit: 30 })
    loginRecords.value = Array.isArray(data?.records) ? data.records : []
  } catch (error) {
    ElMessage.error('加载登录记录失败')
  }
}

onMounted(async () => {
  await Promise.all([loadProfile(), loadLoginRecords()])
})
</script>

<style scoped>
.profile-page {
  display: grid;
  gap: 16px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.profile-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.avatar-block {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar-actions {
  display: flex;
  gap: 8px;
}

.address-item {
  grid-column: 1 / -1;
}

@media (max-width: 900px) {
  .profile-grid {
    grid-template-columns: 1fr;
  }

  .address-item {
    grid-column: auto;
  }
}
</style>
