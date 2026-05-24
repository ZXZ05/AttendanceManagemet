import { createRouter, createWebHashHistory } from 'vue-router'
import List from '@/pages/List.vue'
import Login from '@/pages/Login.vue'
import Register from '@/pages/Register.vue'
import { isAdmin, isAuthenticated } from '@/utils/auth'

const routes = [
  {
    path: '/',
    name: 'Login',
    component: Login,
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { title: '注册' }
  },
  {
    path: '/list',
    name: 'List',
    component: List,
    children: [
      {
        path: '',
        redirect: '/home'
      },
      {
        path: 'home',
        alias: '/home',
        name: 'home',
        meta: { title: '首页' },
        component: () => import('@/pages/home.vue')
      },
      {
        path: 'work',
        alias: '/work',
        name: 'work',
        meta: { title: '工作台' },
        component: () => import('@/pages/work/work.vue'),
        children: [
          {
            path: '',
            redirect: '/task'
          },
          {
            path: 'task',
            alias: '/task',
            name: 'task',
            meta: { title: '我的任务' },
            component: () => import('@/pages/work/task/task.vue')
          },
          {
            path: 'fixedAssets',
            alias: '/fixedAssets',
            name: 'fixedAssets',
            meta: { title: '固定资产' },
            component: () => import('@/pages/work/apply/fixedAssets.vue')
          },
          {
            path: 'leave',
            alias: '/leave',
            name: 'leave',
            meta: { title: '请假申请' },
            component: () => import('@/pages/work/apply/leave.vue')
          },
          {
            path: 'check',
            alias: '/check',
            name: 'check',
            meta: { title: '考勤查询' },
            component: () => import('@/pages/work/check/check.vue')
          },
          {
            path: 'customer',
            alias: '/customer',
            name: 'customer',
            meta: { title: '客户管理' },
            component: () => import('@/pages/work/customer/customerList.vue')
          },
          {
            path: 'meeting',
            alias: '/meeting',
            name: 'meeting',
            meta: { title: '会议记录' },
            component: () => import('@/pages/work/meeting/meetingList.vue')
          },
          {
            path: 'notice',
            alias: '/notice',
            name: 'notice',
            meta: { title: '通知公告' },
            component: () => import('@/pages/work/notice/noticeList.vue')
          },
          {
            path: 'updateData',
            alias: '/updateData',
            name: 'updateData',
            meta: { title: '资料更新' },
            component: () => import('@/pages/work/center/updateData.vue')
          }
        ]
      },
      {
        path: 'admin',
        alias: '/admin',
        name: 'admin',
        meta: {
          title: '管理端',
          requiresAdmin: true
        },
        component: () => import('@/pages/admin/admin.vue'),
        children: [
          {
            path: '',
            redirect: '/employee'
          },
          {
            path: 'employee',
            alias: '/employee',
            name: 'employee',
            meta: { title: '员工管理' },
            component: () => import('@/pages/admin/employee/employeeList.vue')
          },
          {
            path: 'department',
            alias: '/department',
            name: 'department',
            meta: { title: '部门职位' },
            component: () => import('@/pages/admin/department/departmentList.vue')
          },
          {
            path: 'finance',
            alias: '/finance',
            name: 'finance',
            meta: { title: '财务管理' },
            component: () => import('@/pages/admin/finance/finance.vue')
          },
          {
            path: 'statistics',
            alias: '/statistics',
            name: 'statistics',
            meta: { title: '统计分析' },
            component: () => import('@/pages/admin/statistics/statistics.vue')
          }
        ]
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

const PUBLIC_PATHS = ['/', '/register']

router.beforeEach((to, from, next) => {
  const authed = isAuthenticated()
  const isPublic = PUBLIC_PATHS.includes(to.path)
  const requiresAdmin = to.matched.some((item) => item.meta?.requiresAdmin)

  if (!isPublic && !authed) {
    next('/')
    return
  }

  if (isPublic && authed) {
    next('/home')
    return
  }

  if (requiresAdmin && !isAdmin()) {
    next('/home')
    return
  }

  if (to.meta?.title) {
    document.title = `${to.meta.title} - Attendance Manager`
  }

  next()
})

export default router
