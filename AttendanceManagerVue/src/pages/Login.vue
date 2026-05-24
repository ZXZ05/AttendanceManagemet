<template>
  <main class="auth-page login-page">
    <section class="auth-hero am-panel">
      <img src="@/assets/01.png" alt="朝夕考勤系统" class="hero-logo" />
      <p class="eyebrow">ZhaoXi Attendance</p>
      <h1>朝夕考勤系统</h1>
      <p class="hero-copy">
        让每日出勤、请假审批、会议通知和管理分析都更清晰可靠。
      </p>
      <div class="hero-metrics">
        <MetricCard label="实时" value="考勤状态" hint="签到签退一屏掌握" />
        <MetricCard label="统一" value="业务入口" hint="工作台与管理台联动" />
        <MetricCard label="安全" value="权限控制" hint="基于角色保护数据" />
      </div>
    </section>

    <section class="auth-form am-panel">
      <div>
        <p class="eyebrow">Secure Login</p>
        <h2>欢迎回来</h2>
        <p class="desc">请输入员工账号和密码登录系统。</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="onSubmit">
        <el-form-item label="账号" prop="number">
          <el-input v-model.trim="form.number" clearable placeholder="请输入账号">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-alert
          v-if="loginError"
          :title="loginError"
          type="error"
          show-icon
          :closable="false"
          class="error-alert"
        />

        <el-button class="submit-btn" type="primary" :loading="loading" @click="onSubmit">
          登录系统
          <el-icon><ArrowRight /></el-icon>
        </el-button>
      </el-form>

      <div class="footer-tip">
        <span>还没有账号？</span>
        <el-link type="primary" :underline="false" @click="router.push('/register')">立即注册</el-link>
      </div>
    </section>
  </main>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight, Lock, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import MetricCard from '@/components/common/MetricCard.vue'
import { login } from '@/api/auth'
import { setLoginSession } from '@/utils/auth'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const loginError = ref('')

const form = reactive({
  number: '',
  password: ''
})

const rules = {
  number: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 4, max: 20, message: '账号长度需在 4 到 20 个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度需在 6 到 32 个字符之间', trigger: 'blur' }
  ]
}

const LOGIN_MESSAGES = {
  20004: '账号不存在，请确认后重试',
  20002: '密码错误，请重新输入',
  10002: '账号和密码不能为空'
}

function getErrorMessage(error) {
  return error?.response?.data?.message || error?.response?.data || error?.message || '登录请求失败，请稍后重试'
}

async function onSubmit() {
  if (loading.value) {
    return
  }

  loginError.value = ''
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }

  loading.value = true
  try {
    const data = await login({ number: form.number, password: form.password })
    if (data?.code !== 200) {
      loginError.value = LOGIN_MESSAGES[data?.code] || data?.message || '登录失败，请稍后重试'
      ElMessage.error(loginError.value)
      return
    }

    const token = data?.data?.token
    const userType = data?.data?.type
    if (!token) {
      loginError.value = '登录返回缺少令牌，请联系管理员'
      ElMessage.error(loginError.value)
      return
    }

    setLoginSession(form.number, token, userType)
    ElMessage.success('登录成功')
    router.push('/home')
  } catch (error) {
    loginError.value = getErrorMessage(error)
    ElMessage.error(loginError.value)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 460px;
  gap: clamp(20px, 4vw, 56px);
  min-height: 100vh;
  padding: clamp(20px, 5vw, 50px);
}

.auth-hero,
.auth-form {
  padding: clamp(20px, 3vw, 32px);
}

.hero-logo {
  width: 64px;
  height: 64px;
}

.eyebrow {
  margin: 0 0 10px;
  color: var(--am-accent);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.auth-hero h1 {
  margin: 0;
  font-size: clamp(34px, 5vw, 56px);
  line-height: 1.04;
}

.hero-copy {
  margin: 14px 0 0;
  color: var(--am-text-soft);
  line-height: 1.8;
}

.hero-metrics {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 24px;
}

.auth-form {
  align-self: center;
}

.auth-form h2 {
  margin: 0;
  font-size: 32px;
}

.desc {
  margin: 8px 0 18px;
  color: var(--am-muted);
}

.auth-form :deep(.el-form-item__label) {
  font-weight: 700;
}

.auth-form :deep(.el-input__wrapper) {
  min-height: 44px;
}

.submit-btn {
  width: 100%;
  margin-top: 8px;
}

.error-alert {
  margin-bottom: 10px;
}

.footer-tip {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-top: 18px;
  color: var(--am-muted);
}

@media (max-width: 1024px) {
  .auth-page {
    grid-template-columns: 1fr;
  }

  .auth-form {
    max-width: 520px;
    margin: 0 auto;
  }
}

@media (max-width: 640px) {
  .hero-metrics {
    grid-template-columns: 1fr;
  }
}
</style>
