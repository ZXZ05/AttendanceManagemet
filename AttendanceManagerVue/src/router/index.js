import { createRouter, createWebHashHistory } from 'vue-router'
import List from '@/pages/List.vue'
import Login from '@/pages/Login.vue'
import Register from '@/pages/Register.vue'
import { isAuthenticated } from '@/utils/auth'

const routes = [
  {
    path: '/',
    name: 'Login',
    component: Login,
    meta: {
      title: '登录'
    },
    children: []
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: {
      title: '注册'
    }
  },
  {
    path: '/list',
    name: 'List',
    component: List,
    children: [
      {
        path: '/home',
        name: 'home',
        component: () => import('@/pages/home.vue')
      },
      {
        path: '/work',
        name: 'work',
        component: () => import('@/pages/work/work.vue'),
        children: [
          {
            path: '/task',
            name: 'task',
            component: () => import('@/pages/work/task/task.vue')
          },
          {
            path: '/fixedAssets',
            name: 'fixedAssets',
            component: () => import('@/pages/work/apply/fixedAssets.vue')
          },
          {
            path: '/leave',
            name: 'leave',
            component: () => import('@/pages/work/apply/leave.vue')
          },
          {
            path: '/check',
            name: 'check',
            component: () => import('@/pages/work/check/check.vue')
          },
          {
            path: '/customer',
            name: 'customer',
            component: () => import('@/pages/work/customer/customerList.vue')
          },
          {
            path: '/meeting',
            name: 'meeting',
            component: () => import('@/pages/work/meeting/meetingList.vue')
          },
          {
            path: '/notice',
            name: 'notice',
            component: () => import('@/pages/work/notice/noticeList.vue')
          },
          {
            path: '/updateData',
            name: 'updateData',
            component: () => import('@/pages/work/center/updateData.vue')
          }
        ]
      },
      {
        path: '/admin',
        name: 'admin',
        component: () => import('@/pages/admin/admin.vue'),
        children: [
          {
            path: '/employee',
            name: 'employee',
            component: () => import('@/pages/admin/employee/employeeList.vue'),
            meta: {
              title: '员工管理'
            }
          },
          {
            path: '/department',
            name: 'department',
            component: () => import('@/pages/admin/department/departmentList.vue')
          },
          {
            path: '/finance',
            name: 'finance',
            component: () => import('@/pages/admin/finance/finance.vue')
          },
          {
            path: '/statistics',
            name: 'statistics',
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

  if (!isPublic && !authed) {
    next('/')
    return
  }

  if (isPublic && authed) {
    next('/home')
    return
  }

  next()
})

export default router
