<template>
  <section class="password-page">
    <PageHeader kicker="Security" title="修改密码" description="更新当前账号密码，提交成功后可继续使用新密码登录。" />

    <el-card shadow="never" class="password-card">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="form.oldPassword" type="password" show-password autocomplete="off" placeholder="请输入旧密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="password">
          <el-input v-model="form.password" type="password" show-password autocomplete="off" placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="checkPass">
          <el-input v-model="form.checkPass" type="password" show-password autocomplete="off" placeholder="请再次输入新密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm">提交</el-button>
          <el-button type="warning" @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </section>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/common/PageHeader.vue'
import { updatePassword } from '@/api/employee'
import { getLoginUsername } from '@/utils/auth'

const formRef = ref()
const form = reactive({
  number: getLoginUsername(),
  oldPassword: '',
  password: '',
  checkPass: ''
})

const rules = {
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
        if (value !== form.password) {
          callback(new Error('两次输入密码不一致'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ]
}

async function submitForm() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  try {
    const data = await updatePassword({
      number: form.number,
      oldPassword: form.oldPassword,
      password: form.password
    })

    if (data?.code === 200) {
      ElMessage.success('修改成功')
      resetForm()
      return
    }

    ElMessage.error(data?.message || '修改失败')
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || '修改请求失败')
  }
}

function resetForm() {
  formRef.value?.resetFields()
}
</script>

<style scoped>
.password-page {
  display: grid;
  gap: 16px;
}

.password-card {
  max-width: 520px;
}
</style>
