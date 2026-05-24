<template>
  <main class="auth-page register-page">
    <section class="auth-form am-panel">
      <div class="brand-line">
        <img src="@/assets/01.png" alt="朝夕考勤系统" />
        <div>
          <p class="eyebrow">ZhaoXi Attendance</p>
          <h1>朝夕考勤系统注册</h1>
        </div>
      </div>
      <p class="desc">填写基础信息后即可提交注册申请。</p>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="onSubmit">
        <el-form-item label="账号" prop="number">
          <el-input v-model.trim="form.number" clearable placeholder="请输入 4-20 位账号">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="姓名" prop="name">
          <el-input v-model.trim="form.name" clearable placeholder="请输入姓名">
            <template #prefix>
              <el-icon><Avatar /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model.trim="form.phone" clearable maxlength="11" placeholder="请输入手机号">
            <template #prefix>
              <el-icon><Iphone /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入 6-32 位密码">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="确认密码" prop="checkPass">
          <el-input v-model="form.checkPass" type="password" show-password placeholder="请再次输入密码">
            <template #prefix>
              <el-icon><Key /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-alert
          v-if="registerError"
          :title="registerError"
          type="error"
          show-icon
          :closable="false"
          class="error-alert"
        />

        <div class="actions">
          <el-button type="primary" :loading="loading" @click="onSubmit">
            提交注册
            <el-icon><ArrowRight /></el-icon>
          </el-button>
          <el-button @click="router.push('/')">返回登录</el-button>
        </div>
      </el-form>
    </section>

    <aside class="register-aside am-panel">
      <p class="eyebrow">Quick Start</p>
      <h2>一个账号，进入完整办公工作台</h2>
      <ol>
        <li>
          <strong>填写身份信息</strong>
          <p>账号、姓名、手机号将用于建立员工基础档案。</p>
        </li>
        <li>
          <strong>提交注册申请</strong>
          <p>系统会校验账号唯一性与密码规则，结果即时反馈。</p>
        </li>
        <li>
          <strong>登录并开始工作</strong>
          <p>注册成功后返回登录页，即可进入考勤与协同模块。</p>
        </li>
      </ol>
    </aside>
  </main>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight, Avatar, Iphone, Key, Lock, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { register } from '@/api/auth'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const registerError = ref('')

const form = reactive({
  number: '',
  name: '',
  phone: '',
  password: '',
  checkPass: ''
})

const REGISTER_MESSAGES = {
  20005: '账号已存在，请更换后重试',
  10001: '提交参数不合法，请检查输入',
  10002: '提交参数不完整，请检查输入'
}

const rules = {
  number: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 4, max: 20, message: '账号长度需在 4 到 20 个字符之间', trigger: 'blur' }
  ],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度需在 6 到 32 个字符之间', trigger: 'blur' }
  ],
  checkPass: [
    {
      validator: (_, value, callback) => {
        if (!value) {
          callback(new Error('请再次输入密码'))
          return
        }
        if (value !== form.password) {
          callback(new Error('两次输入的密码不一致'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ]
}

function getErrorMessage(error) {
  return error?.response?.data?.message || error?.response?.data || error?.message || '注册请求失败，请稍后重试'
}

async function onSubmit() {
  if (loading.value) {
    return
  }

  registerError.value = ''
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }

  loading.value = true
  try {
    const data = await register({
      number: form.number,
      name: form.name,
      phone: form.phone,
      password: form.password
    })

    if (data?.code === 200) {
      ElMessage.success('注册成功，请登录')
      router.push('/')
      return
    }

    registerError.value = REGISTER_MESSAGES[data?.code] || data?.message || '注册失败，请稍后重试'
    ElMessage.error(registerError.value)
  } catch (error) {
    registerError.value = getErrorMessage(error)
    ElMessage.error(registerError.value)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  display: grid;
  grid-template-columns: 480px minmax(0, 1fr);
  gap: clamp(20px, 4vw, 52px);
  min-height: 100vh;
  padding: clamp(20px, 5vw, 50px);
}

.auth-form,
.register-aside {
  padding: clamp(20px, 3vw, 32px);
}

.brand-line {
  display: flex;
  gap: 12px;
  align-items: center;
}

.brand-line img {
  width: 56px;
  height: 56px;
}

.eyebrow {
  margin: 0;
  color: var(--am-accent);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.brand-line h1 {
  margin: 2px 0 0;
  font-size: 30px;
}

.desc {
  margin: 14px 0 18px;
  color: var(--am-muted);
}

.auth-form :deep(.el-form-item__label) {
  font-weight: 700;
}

.auth-form :deep(.el-input__wrapper) {
  min-height: 44px;
}

.actions {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 10px;
}

.error-alert {
  margin-bottom: 10px;
}

.register-aside h2 {
  margin: 10px 0 16px;
  font-size: clamp(30px, 5vw, 48px);
  line-height: 1.1;
}

.register-aside ol {
  display: grid;
  gap: 14px;
  margin: 0;
  padding-left: 18px;
}

.register-aside li {
  padding: 12px;
  border: 1px solid var(--am-line-soft);
  border-radius: 12px;
  background: rgba(12, 20, 38, 0.5);
}

.register-aside p {
  margin: 6px 0 0;
  color: var(--am-text-soft);
}

@media (max-width: 1024px) {
  .auth-page {
    grid-template-columns: 1fr;
  }

  .register-aside {
    order: -1;
  }
}

@media (max-width: 640px) {
  .actions {
    grid-template-columns: 1fr;
  }
}
</style>
