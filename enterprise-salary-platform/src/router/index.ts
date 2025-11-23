import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
// 引入 Layout
import Layout from '@/layout/index.vue'
import { useSessionStore } from '@/stores/session'

const routes: Array<RouteRecordRaw> = [
  { path: '/', redirect: '/login' },
  { 
    path: '/login', 
    name: 'Login', 
    component: () => import('@/views/Login/index.vue') 
  },
  {
    // 创建一个父路由使用 Layout
    path: '/',
    component: Layout,
    // 这里的子路由都会显示在 Layout 的 <router-view /> 里
    children: [
      { 
        path: 'dashboard', // 访问 /dashboard
        name: 'Dashboard', 
        component: () => import('@/views/Dashboard/index.vue'),
        meta: { title: '首页' }
      },
      { 
        path: 'asset/fiscal-card', 
        name: 'FiscalCard', 
        component: () => import('@/views/Asset/FiscalCard/index.vue'),
        meta: { title: '财政资产卡片' }
      },
      { 
        path: 'asset/list', 
        name: 'AssetList', 
        component: () => import('@/views/Asset/AssetList/index.vue'),
        meta: { title: '资产列表' }
      },
      { 
        path: 'asset/record', 
        name: 'AssetRecord', 
        component: () => import('@/views/Asset/Record/index.vue'),
        meta: { title: '操作记录' }
      },
      { 
        path: 'setting/department', 
        name: 'DepartmentSetting', 
        component: () => import('@/views/Setting/Department/index.vue'),
        meta: { title: '部门人员设置' }
      },
      { 
        path: 'setting/category', 
        name: 'CategorySetting', 
        component: () => import('@/views/Setting/Category/index.vue'), // 指向新建文件
        meta: { title: '分类设置' }
      },
      { 
        path: 'setting/params', 
        name: 'ParamsSetting', 
        component: () => import('@/views/Setting/Params/index.vue'), // 指向新建文件
        meta: { title: '参数设置' }
      },
      { 
        // 存放地点设置 (本次重点)
        path: 'setting/location', 
        name: 'LocationSetting', 
        component: () => import('@/views/Setting/Location/index.vue'),
        meta: { title: '存放地点设置' }
      },
      { 
        path: 'inventory/task', 
        name: 'InventoryTask', 
        component: () => import('@/views/Inventory/Check/index.vue'), // 账实核对盘点
        meta: { title: '盘点任务' }
      },
      { 
        path: 'inventory/match', // 编码匹配页
        name: 'InventoryMatch', 
        component: () => import('@/views/Inventory/Match/index.vue'),
        meta: { title: '编码匹配' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile/index.vue'),
        meta: { title: '个人中心' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 登录态守卫：未登录则跳转登录并清缓存
router.beforeEach((to, _from, next) => {
  const session = useSessionStore()
  const hasToken = !!localStorage.getItem('token')
  if (to.path !== '/login' && !hasToken) {
    session.logout()
    next('/login')
    return
  }
  next()
})

export default router
