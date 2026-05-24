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
import { DataAnalysis, Money, OfficeBuilding, User } from '@element-plus/icons-vue'

const route = useRoute()

const menuItems = [
  { path: '/employee', label: '员工管理', icon: User },
  { path: '/department', label: '部门职位', icon: OfficeBuilding },
  { path: '/finance', label: '财务管理', icon: Money },
  { path: '/statistics', label: '统计分析', icon: DataAnalysis }
]

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
