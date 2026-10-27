<template>
  <div class="admin-wrapper">
    <el-container v-if="isLoggedIn">
      <el-aside width="220px" class="admin-aside">
        <div class="aside-header">
          <div class="logo-area">
            <div class="logo-icon">
              <svg class="w-7 h-7" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z" />
              </svg>
            </div>
            <span class="logo-text">SpeSense</span>
          </div>
        </div>
        <el-menu :default-active="activeMenu" :default-openeds="cmsOpened" router class="admin-menu" @select="asideOpen = false">
          <div class="menu-section">CRM</div>
          <el-menu-item index="/admin/crm">
            <span class="menu-icon">
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 13h8V3H3v10zm10 8h8V3h-8v18zM3 21h8v-6H3v6z" />
              </svg>
            </span>
            <span>看板</span>
          </el-menu-item>
          <el-menu-item index="/admin/crm/customers">
            <span class="menu-icon">
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
            </span>
            <span>客户</span>
          </el-menu-item>
          <el-menu-item index="/admin/crm/inquiries">
            <span class="menu-icon">
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
              </svg>
            </span>
            <span>询盘跟进</span>
          </el-menu-item>
          <el-menu-item index="/admin/crm/users" v-if="showCms || isManager">
            <span class="menu-icon">
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197" />
              </svg>
            </span>
            <span>用户</span>
          </el-menu-item>

          <el-sub-menu index="cms" v-if="showCms">
            <template #title>
              <span class="menu-icon">
                <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
                </svg>
              </span>
              <span>网站内容</span>
            </template>
          <el-menu-item index="/admin">
            <span class="menu-icon">
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
              </svg>
            </span>
            <span>{{ $t('admin.dashboard') }}</span>
          </el-menu-item>
          <el-menu-item index="/admin/categories">
            <span class="menu-icon">
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
              </svg>
            </span>
            <span>{{ $t('admin.categories') }}</span>
          </el-menu-item>
          <el-menu-item index="/admin/products">
            <span class="menu-icon">
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
              </svg>
            </span>
            <span>{{ $t('admin.products') }}</span>
          </el-menu-item>
          <el-menu-item index="/admin/news">
            <span class="menu-icon">
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V7m2 13a2 2 0 002-2V9a2 2 0 00-2-2h-2m-4-3H9M7 16h6M7 8h6v4H7V8z" />
              </svg>
            </span>
            <span>{{ $t('admin.news') }}</span>
          </el-menu-item>
          <el-menu-item index="/admin/applications">
            <span class="menu-icon">
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 5a1 1 0 011-1h14a1 1 0 011 1v2a1 1 0 01-1 1H5a1 1 0 01-1-1V5zM4 13a1 1 0 011-1h6a1 1 0 011 1v6a1 1 0 01-1 1H5a1 1 0 01-1-1v-6zM16 13a1 1 0 011-1h2a1 1 0 011 1v6a1 1 0 01-1 1h-2a1 1 0 01-1-1v-6z" />
              </svg>
            </span>
            <span>{{ $t('admin.applications') }}</span>
          </el-menu-item>
          <el-menu-item index="/admin/services">
            <span class="menu-icon">
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 5.636l-3.536 3.536m0 5.656l3.536 3.536M9.172 9.172L5.636 5.636m3.536 9.192l-3.536 3.536M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-5 0a4 4 0 11-8 0 4 4 0 018 0z" />
              </svg>
            </span>
            <span>{{ $t('admin.services') }}</span>
          </el-menu-item>
          <el-menu-item index="/admin/downloads">
            <span class="menu-icon">
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
              </svg>
            </span>
            <span>{{ $t('admin.downloads') }}</span>
          </el-menu-item>
          <el-menu-item index="/admin/company">
            <span class="menu-icon">
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
              </svg>
            </span>
            <span>{{ $t('admin.company') }}</span>
          </el-menu-item>
          <el-menu-item index="/admin/inquiries">
            <span class="menu-icon">
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
              </svg>
            </span>
            <span>{{ $t('admin.inquiries') }}</span>
          </el-menu-item>
          <el-menu-item index="/admin/brands">
            <span class="menu-icon">
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
              </svg>
            </span>
            <span>{{ $t('admin.brands') }}</span>
          </el-menu-item>
          <el-menu-item index="/admin/banners">
            <span class="menu-icon">
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
            </span>
            <span>轮播图管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/menus">
            <span class="menu-icon">
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
              </svg>
            </span>
            <span>导航菜单</span>
          </el-menu-item>
          </el-sub-menu>
          <el-menu-item @click="logout" class="logout-item">
            <span class="menu-icon">
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
              </svg>
            </span>
            <span>{{ $t('admin.logout') }}</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header height="56px" class="admin-header">
          <div class="header-left">
            <div class="header-title">{{ currentTitle }}</div>
            <div class="header-tabs">
              <button
                v-for="tab in tabs"
                :key="tab.name"
                class="header-tab"
                :class="{ active: activeTab === tab.name }"
                @click="goTab(tab)"
              >
                {{ tab.title }}
                <span
                  v-if="tab.name !== 'dashboard' && tab.name !== 'crm'"
                  class="tab-close"
                  @click.stop="removeTab(tab.name)"
                >×</span>
              </button>
            </div>
          </div>
          <div class="header-right">
            <span class="header-role">{{ roleLabel }}</span>
            <span class="header-user">{{ displayName }}</span>
            <button class="header-logout" type="button" @click="logout">退出</button>
          </div>
        </el-header>
        <el-main class="admin-main">
          <NuxtPage :key="route.path" />
        </el-main>
      </el-container>
    </el-container>
    <div v-else class="flex items-center justify-center min-h-screen">
      <slot />
    </div>
  </div>
</template>

<script setup>
import { getAdminRole } from '~/composables/useCrm'

const { locale, setLocale } = useI18n()
const route = useRoute()
const router = useRouter()
const isLoggedIn = ref(false)
const token = ref(null)
const adminRole = ref('')
const displayName = ref('')
const currentTitle = computed(() => resolveTab(route.path)?.title || '管理后台')
const roleLabel = computed(() => {
  const role = adminRole.value
  if (role === 'sales') return '销售'
  if (role === 'manager') return '管理层'
  if (role === 'editor') return '编辑'
  return '管理员'
})
const isManager = computed(() => {
  const role = adminRole.value
  return !role || role === 'manager' || role === 'admin'
})
const showCms = computed(() => {
  const role = adminRole.value
  return !role || role === 'admin' || role === 'editor'
})
const activeTab = ref('crm')
const tabs = ref([
  { name: 'crm', title: 'CRM 看板', path: '/admin/crm' }
])

const activeMenu = computed(() => route.path)
const cmsOpened = []
const cmsPaths = [
  '/admin', '/admin/categories', '/admin/products', '/admin/news', '/admin/applications',
  '/admin/services', '/admin/downloads', '/admin/company', '/admin/inquiries',
  '/admin/brands', '/admin/banners', '/admin/menus',
]

const tabNameMap = {
  '/admin': { name: 'dashboard', title: '仪表盘', path: '/admin' },
  '/admin/crm': { name: 'crm', title: 'CRM 看板', path: '/admin/crm' },
  '/admin/crm/customers': { name: 'crm-customers', title: '客户管理', path: '/admin/crm/customers' },
  '/admin/crm/inquiries': { name: 'crm-inquiries', title: '询盘跟进', path: '/admin/crm/inquiries' },
  '/admin/crm/users': { name: 'crm-users', title: '用户管理', path: '/admin/crm/users' },
  '/admin/categories': { name: 'categories', title: '分类管理', path: '/admin/categories' },
  '/admin/products': { name: 'products', title: '产品管理', path: '/admin/products' },
  '/admin/news': { name: 'news', title: '新闻管理', path: '/admin/news' },
  '/admin/applications': { name: 'applications', title: '应用管理', path: '/admin/applications' },
  '/admin/services': { name: 'services', title: '服务管理', path: '/admin/services' },
  '/admin/downloads': { name: 'downloads', title: '下载管理', path: '/admin/downloads' },
  '/admin/company': { name: 'company', title: '公司信息', path: '/admin/company' },
  '/admin/inquiries': { name: 'inquiries', title: '官网留言', path: '/admin/inquiries' },
  '/admin/brands': { name: 'brands', title: '代理品牌', path: '/admin/brands' },
  '/admin/banners': { name: 'banners', title: '轮播图管理', path: '/admin/banners' },
  '/admin/menus': { name: 'menus', title: '导航菜单', path: '/admin/menus' }
}

const resolveTab = (path) => {
  if (tabNameMap[path]) return tabNameMap[path]
  if (path.startsWith('/admin/crm/customers/')) return { name: 'crm-customer-detail', title: '客户详情', path }
  if (path.startsWith('/admin/crm/inquiries/')) return { name: 'crm-inquiry-detail', title: '询盘详情', path }
  return null
}

const guardRoute = (path) => {
  if (!showCms.value && cmsPaths.includes(path)) {
    navigateTo('/admin/crm')
    return false
  }
  if (!isManager.value && path.startsWith('/admin/crm/users')) {
    navigateTo('/admin/crm')
    return false
  }
  return true
}

const openTab = (path) => {
  const tabInfo = resolveTab(path)
  if (!tabInfo) return

  const existing = tabs.value.find(t => t.name === tabInfo.name)
  if (!existing) {
    tabs.value.push(tabInfo)
  }
  activeTab.value = tabInfo.name
}

const goTab = (tab) => {
  if (tab?.path) router.push(tab.path)
}

const removeTab = (name) => {
  const index = tabs.value.findIndex(t => t.name === name)
  if (index > -1) {
    tabs.value.splice(index, 1)
    if (activeTab.value === name) {
      const newTab = tabs.value[Math.max(0, index - 1)]
      if (newTab) {
        activeTab.value = newTab.name
        router.push(newTab.path)
      }
    }
  }
}

watch(() => route.path, (path) => {
  if (!guardRoute(path)) return
  const tabInfo = resolveTab(path)
  if (tabInfo) {
    const existing = tabs.value.find(t => t.name === tabInfo.name)
    if (!existing) {
      tabs.value.push(tabInfo)
    } else if (tabInfo.path) {
      existing.path = tabInfo.path
      existing.title = tabInfo.title
    }
    activeTab.value = tabInfo.name
  }
})

onMounted(async () => {
  if (import.meta.client) {
    token.value = localStorage.getItem('admin_token')
    adminRole.value = getAdminRole()
    displayName.value = localStorage.getItem('admin_username') || '未登录'
    isLoggedIn.value = !!token.value
    await setLocale('zh')
    if (!guardRoute(route.path)) return
    const tabInfo = resolveTab(route.path)
    if (tabInfo) {
      activeTab.value = tabInfo.name
      if (!tabs.value.find(t => t.name === tabInfo.name)) {
        tabs.value.push(tabInfo)
      }
    }
  }
})

const logout = () => {
  if (import.meta.client) {
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_role')
    localStorage.removeItem('admin_user_id')
    localStorage.removeItem('admin_username')
  }
  isLoggedIn.value = false
  navigateTo('/admin/login')
}
</script>

<style scoped>
.admin-wrapper {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
}
.admin-aside {
  background: linear-gradient(180deg, #1e1e3f 0%, #1a1a2e 100%) !important;
  box-shadow: 4px 0 20px rgba(0, 0, 0, 0.3);
  border-right: 1px solid rgba(255, 255, 255, 0.05);
  overflow-y: auto;
  height: 100vh;
}
.menu-section {
  padding: 16px 20px 6px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 1.5px;
  color: rgba(255, 255, 255, 0.38);
}
.aside-header {
  padding: 20px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}
.logo-area {
  display: flex;
  align-items: center;
  gap: 10px;
}
.logo-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}
.logo-text {
  font-size: 1.25rem;
  font-weight: 700;
  color: white;
  letter-spacing: 0.5px;
}
.admin-menu {
  background: transparent !important;
  border: none !important;
  padding-top: 12px;
}
.admin-menu:not(.el-menu--collapse) {
  width: 220px;
}
:deep(.el-menu-item) {
  height: 48px;
  line-height: 48px;
  color: rgba(255, 255, 255, 0.65);
  margin: 4px 8px;
  border-radius: 10px;
  padding-left: 16px !important;
  transition: all 0.3s ease;
}
:deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.08) !important;
  color: white !important;
}
:deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.3) 0%, rgba(118, 75, 162, 0.3) 100%) !important;
  color: white !important;
  border-left: 3px solid #667eea;
}
:deep(.el-sub-menu__title) {
  height: 48px;
  line-height: 48px;
  color: rgba(255, 255, 255, 0.65) !important;
  margin: 8px 8px 4px;
  border-radius: 10px;
  padding-left: 16px !important;
}
:deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.08) !important;
  color: white !important;
}
:deep(.el-sub-menu .el-menu) {
  background: transparent !important;
}
:deep(.el-sub-menu .el-menu-item) {
  padding-left: 28px !important;
}
.menu-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  margin-right: 12px;
}
.logout-item {
  margin-top: auto;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  border-bottom: none !important;
  margin-top: 40px;
}
.admin-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 0 20px;
  background: #fff;
  border-bottom: 1px solid #eef0f4;
  box-shadow: 0 1px 0 rgba(15, 23, 42, 0.04);
}
.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
  min-width: 0;
  flex: 1;
}
.header-title {
  flex-shrink: 0;
  font-size: 15px;
  font-weight: 650;
  color: #1f2937;
}
.header-tabs {
  display: flex;
  align-items: center;
  gap: 8px;
  overflow-x: auto;
  min-width: 0;
}
.header-tab {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 32px;
  padding: 0 12px;
  border: 1px solid transparent;
  border-radius: 999px;
  background: #f3f4f6;
  color: #6b7280;
  font-size: 13px;
  cursor: pointer;
  white-space: nowrap;
}
.header-tab:hover {
  background: #eaeef5;
  color: #374151;
}
.header-tab.active {
  background: #eef2ff;
  border-color: #c7d2fe;
  color: #4338ca;
  font-weight: 600;
}
.tab-close {
  font-style: normal;
  font-size: 16px;
  line-height: 1;
  color: #9ca3af;
}
.tab-close:hover {
  color: #ef4444;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}
.header-role {
  height: 24px;
  padding: 0 8px;
  border-radius: 999px;
  background: #eef2ff;
  color: #4338ca;
  font-size: 12px;
  line-height: 24px;
  font-weight: 600;
}
.header-user {
  font-size: 13px;
  color: #4b5563;
}
.header-logout {
  height: 32px;
  padding: 0 12px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #fff;
  color: #4b5563;
  font-size: 13px;
  cursor: pointer;
}
.header-logout:hover {
  background: #f9fafb;
  color: #111827;
}
.admin-main {
  background: #f5f7fa;
  padding: 24px;
  min-height: calc(100vh - 56px);
}
</style>
