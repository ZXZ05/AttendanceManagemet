<template>
  <div class="shell am-shell">
    <header class="shell-header am-panel">
      <div class="brand">
        <img src="@/assets/01.png" alt="朝夕考勤系统" />
        <div>
          <p>ZhaoXi Attendance</p>
          <strong>朝夕考勤系统</strong>
        </div>
      </div>

      <div class="user-area">
        <el-badge :value="unreadCount > 99 ? '99+' : unreadCount" :hidden="unreadCount <= 0" class="notice-badge">
          <el-button circle @click="openNotificationDrawer">
            <el-icon><Bell /></el-icon>
          </el-button>
        </el-badge>
        <span class="user-name">{{ displayName }}</span>
        <el-tag size="small" :type="isAdminUser ? 'warning' : 'info'" effect="light">
          {{ roleLabel }}
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
      <el-menu-item v-if="canOpenAdmin" index="/admin">
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

    <el-drawer v-model="notificationDrawerVisible" title="站内通知" size="420px">
      <template #header>
        <div class="drawer-header">
          <strong>站内通知</strong>
          <el-button text type="primary" :disabled="unreadCount <= 0" @click="handleReadAll">全部已读</el-button>
        </div>
      </template>

      <div class="drawer-body">
        <el-empty v-if="!notificationList.length" description="暂无通知" />
        <div v-else class="notice-list">
          <article
            v-for="item in notificationList"
            :key="item.id"
            class="notice-item"
            :class="{ unread: item.status === '0' }"
          >
            <div class="notice-title-row">
              <strong>{{ item.title || '系统提醒' }}</strong>
              <el-tag v-if="item.status === '0'" size="small" type="danger" effect="plain">未读</el-tag>
            </div>
            <p>{{ item.content }}</p>
            <div class="notice-actions">
              <time>{{ item.createdTime || '-' }}</time>
              <el-button
                v-if="item.status === '0'"
                text
                type="primary"
                @click="handleRead(item)"
              >
                标记已读
              </el-button>
              <el-button
                v-if="resolveActionPath(item)"
                text
                type="primary"
                @click="goTo(resolveActionPath(item))"
              >
                去处理
              </el-button>
            </div>
          </article>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Bell, House, Setting, Suitcase, SwitchButton } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getNotificationList, getUnreadNotificationCount, markAllNotificationsRead, markNotificationRead } from '@/api/notification'
import { loadCurrentUser, useUser } from '@/stores/user'
import { canAccessAdminPortal, clearLoginSession, getRoleLabel, isAdmin } from '@/utils/auth'

const route = useRoute()
const router = useRouter()

const { name, type } = useUser()
const notificationDrawerVisible = ref(false)
const notificationList = ref([])
const unreadCount = ref(0)
const refreshTimer = ref()

const displayName = computed(() => name.value || '未命名用户')
const roleLabel = computed(() => getRoleLabel(type.value))
const isAdminUser = computed(() => isAdmin(type.value))
const canOpenAdmin = computed(() => canAccessAdminPortal(type.value))
const activeMenu = computed(() => {
  const path = route.path
  if (
    path.startsWith('/admin')
    || path.startsWith('/employee')
    || path.startsWith('/department')
    || path.startsWith('/finance')
    || path.startsWith('/statistics')
  ) {
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

function goTo(path) {
  if (!path) return
  router.push(path)
}

function resolveActionPath(item) {
  const relatedType = item?.relatedType || ''
  if (relatedType === 'TASK') return '/task'
  if (relatedType === 'SALARY') return '/check'
  if (relatedType === 'MEETING') return '/meeting'
  return ''
}

async function loadUnreadCount() {
  try {
    const data = await getUnreadNotificationCount()
    unreadCount.value = Number(data?.count || 0)
  } catch (error) {
    unreadCount.value = 0
  }
}

async function loadNotifications() {
  try {
    notificationList.value = await getNotificationList({ limit: 40 })
  } catch (error) {
    ElMessage.error('通知加载失败')
  }
}

async function openNotificationDrawer() {
  notificationDrawerVisible.value = true
  await loadNotifications()
  await loadUnreadCount()
}

async function handleRead(item) {
  if (!item?.id) return
  try {
    await markNotificationRead(item.id)
    await Promise.all([loadNotifications(), loadUnreadCount()])
  } catch (error) {
    ElMessage.error('标记已读失败')
  }
}

async function handleReadAll() {
  try {
    await markAllNotificationsRead()
    await Promise.all([loadNotifications(), loadUnreadCount()])
    ElMessage.success('已全部标记为已读')
  } catch (error) {
    ElMessage.error('操作失败，请稍后重试')
  }
}

onMounted(async () => {
  await loadCurrentUser()
  await loadUnreadCount()
  refreshTimer.value = window.setInterval(loadUnreadCount, 60000)
})

onBeforeUnmount(() => {
  if (refreshTimer.value) {
    clearInterval(refreshTimer.value)
  }
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

.notice-badge :deep(.el-badge__content) {
  border: 0;
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

.drawer-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.drawer-body {
  display: grid;
  gap: 12px;
}

.notice-list {
  display: grid;
  gap: 10px;
}

.notice-item {
  padding: 12px;
  border: 1px solid var(--am-line-soft);
  border-radius: 10px;
  background: rgba(12, 21, 40, 0.56);
}

.notice-item.unread {
  border-color: rgba(245, 185, 79, 0.5);
}

.notice-title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.notice-item p {
  margin: 8px 0;
  color: var(--am-text-soft);
  line-height: 1.6;
}

.notice-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.notice-actions time {
  color: var(--am-muted);
  font-size: 12px;
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
