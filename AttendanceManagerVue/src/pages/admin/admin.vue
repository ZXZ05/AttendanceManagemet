<template>
  <section class="admin-shell">
    <aside class="admin-nav am-panel">
      <el-menu :default-active="activePath" router>
        <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
        </el-menu-item>
      </el-menu>
    </aside>

    <main class="admin-content">
      <router-view />
    </main>
  </section>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { DataAnalysis, Money, Odometer, OfficeBuilding, User } from '@element-plus/icons-vue'
import { hasPermission, PERMISSION } from '@/utils/auth'

const route = useRoute()

const allMenuItems = [
  { path: '/admin/dashboard', label: '管理仪表盘', icon: Odometer, permission: PERMISSION.ADMIN_DASHBOARD },
  { path: '/employee', label: '员工管理', icon: User, permission: PERMISSION.EMPLOYEE_MANAGE },
  { path: '/department', label: '部门职位', icon: OfficeBuilding, permission: PERMISSION.ORG_MANAGE },
  { path: '/finance', label: '财务管理', icon: Money, permission: PERMISSION.FINANCE_MANAGE },
  { path: '/statistics', label: '统计分析', icon: DataAnalysis, permission: PERMISSION.STAT_VIEW }
]

const menuItems = computed(() => allMenuItems.filter((item) => hasPermission(item.permission)))
const activePath = computed(() => route.path)
</script>

<style scoped>
.admin-shell {
  display: grid;
  grid-template-columns: 210px minmax(0, 1fr);
  gap: 16px;
}

.admin-nav {
  align-self: start;
  padding: 8px;
}

.admin-nav :deep(.el-menu-item) {
  justify-content: flex-start;
  margin: 4px 0;
}

.admin-content {
  min-width: 0;
}

@media (max-width: 900px) {
  .admin-shell {
    grid-template-columns: 1fr;
  }

  .admin-nav :deep(.el-menu) {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
