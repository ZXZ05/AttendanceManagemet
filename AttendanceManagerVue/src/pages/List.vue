<template>
  <div class="shell am-shell">
    <header class="shell-header am-panel">
      <div class="brand">
        <img src="@/assets/01.png" alt="Attendance Manager" />
        <div>
          <p>Attendance Manager</p>
          <strong>考勤管理系统</strong>
        </div>
      </div>

      <div class="user-area">
        <span class="user-name">{{ displayName }}</span>
        <el-tag size="small" :type="isAdminUser ? 'warning' : 'info'" effect="light">
          {{ isAdminUser ? '管理员' : '员工' }}
        </el-tag>
      </div>
    </header>

    <el-menu :default-active="activeMenu" mode="horizontal" class="main-menu" @select="onSelect">
      <el-menu-item index="/home">
        <el-icon><House /></el-icon>
        <span>首页</span>
      </el-menu-item>
      <el-menu-item index="/task">
        <el-icon><Suitcase /></el-icon>
        <span>工作台</span>
      </el-menu-item>
      <el-menu-item v-if="isAdminUser" index="/admin">
        <el-icon><Setting /></el-icon>
        <span>管理端</span>
      </el-menu-item>
      <el-menu-item index="__logout" class="logout-menu">
        <el-icon><SwitchButton /></el-icon>
        <span>退出登录</span>
      </el-menu-item>
    </el-menu>

    <main class="page-content am-page">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { House, Setting, Suitcase, SwitchButton } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { loadCurrentUser, useUser } from '@/stores/user'
import { clearLoginSession, USER_TYPE } from '@/utils/auth'

const route = useRoute()
const router = useRouter()

const { name, type } = useUser()

const displayName = computed(() => name.value || '未命名用户')
const isAdminUser = computed(() => type.value === USER_TYPE.ADMIN)
const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/admin')) {
    return '/admin'
  }
  if (path.startsWith('/task') || path.startsWith('/work')) {
    return '/task'
  }
  return '/home'
})

function onSelect(index) {
  if (index === '__logout') {
    clearLoginSession()
    router.replace('/')
    return
  }
  router.push(index)
}

onMounted(() => {
  loadCurrentUser()
})
</script>

<style scoped>
.shell {
  display: flex;
  flex-direction: column;
}

.shell-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin: 14px 16px 12px;
  padding: 14px 16px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand img {
  width: 50px;
  height: 50px;
}

.brand p {
  margin: 0;
  color: var(--am-muted);
  font-size: 12px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.brand strong {
  font-size: 24px;
}

.user-area {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-name {
  color: var(--am-text-soft);
  font-weight: 700;
}

.main-menu {
  margin: 0 16px;
  border-radius: 12px;
}

.logout-menu {
  margin-left: auto;
}

.page-content {
  flex: 1;
}

@media (max-width: 768px) {
  .shell-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .main-menu {
    margin: 0 12px;
  }
}
</style>
