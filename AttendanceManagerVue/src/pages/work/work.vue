<template>
  <section class="work-shell">
    <aside class="work-nav am-panel">
      <el-menu :default-active="activePath" router>
        <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
        </el-menu-item>
      </el-menu>
    </aside>

    <main class="work-content">
      <router-view />
    </main>
  </section>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { Bell, Box, Calendar, ChatDotRound, FolderChecked, Key, Tickets, User } from '@element-plus/icons-vue'

const route = useRoute()

const menuItems = [
  { path: '/task', label: '我的任务', icon: Tickets },
  { path: '/fixedAssets', label: '资产申请', icon: Box },
  { path: '/leave', label: '请假申请', icon: FolderChecked },
  { path: '/check', label: '我的考勤', icon: User },
  { path: '/customer', label: '我的客户', icon: ChatDotRound },
  { path: '/meeting', label: '我的会议', icon: Calendar },
  { path: '/notice', label: '我的通知', icon: Bell },
  { path: '/updateData', label: '修改密码', icon: Key }
]

const activePath = computed(() => route.path)
</script>

<style scoped>
.work-shell {
  display: grid;
  grid-template-columns: 210px minmax(0, 1fr);
  gap: 16px;
}

.work-nav {
  align-self: start;
  padding: 8px;
}

.work-nav :deep(.el-menu-item) {
  justify-content: flex-start;
  margin: 4px 0;
}

.work-content {
  min-width: 0;
}

@media (max-width: 900px) {
  .work-shell {
    grid-template-columns: 1fr;
  }

  .work-nav :deep(.el-menu) {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
