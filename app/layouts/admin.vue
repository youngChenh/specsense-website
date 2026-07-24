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
        <el-menu :default-active="currentMenu" router class="admin-menu">
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
        <el-header height="50px" class="admin-header">
          <el-tabs v-model="activeTab" type="card" @tab-remove="removeTab" @tab-click="handleTabClick">
            <el-tab-pane
              v-for="tab in tabs"
              :key="tab.name"
              :label="tab.title"
              :name="tab.name"
              :closable="tab.name !== 'dashboard'"
            />
          </el-tabs>
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
const { locale, setLocale } = useI18n()
const route = useRoute()
const router = useRouter()
const isLoggedIn = ref(false)
const token = ref(null)
const activeTab = ref('dashboard')
const tabs = ref([
  { name: 'dashboard', title: '仪表盘', path: '/admin' }
])

const activeMenu = computed(() => route.path)

const tabNameMap = {
  '/admin': { name: 'dashboard', title: '仪表盘' },
  '/admin/categories': { name: 'categories', title: '分类管理' },
  '/admin/products': { name: 'products', title: '产品管理' },
  '/admin/news': { name: 'news', title: '新闻管理' },
  '/admin/applications': { name: 'applications', title: '应用管理' },
  '/admin/services': { name: 'services', title: '服务管理' },
  '/admin/downloads': { name: 'downloads', title: '下载管理' },
  '/admin/company': { name: 'company', title: '公司信息' },
  '/admin/inquiries': { name: 'inquiries', title: '询盘管理' },
  '/admin/brands': { name: 'brands', title: '代理品牌' },
  '/admin/menus': { name: 'menus', title: '导航菜单' }
}

const openTab = (path) => {
  const tabInfo = tabNameMap[path]
  if (!tabInfo) return

  const existing = tabs.value.find(t => t.name === tabInfo.name)
  if (!existing) {
    tabs.value.push(tabInfo)
  }
  activeTab.value = tabInfo.name
}

const handleTabClick = (tab) => {
  const tabName = typeof tab === 'string' ? tab : tab?.props?.name
  const entry = Object.entries(tabNameMap).find(([path, info]) => info.name === tabName)
  if (entry) {
    router.push(entry[0])
  }
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

// Sync tab when route changes
watch(() => route.path, (path) => {
  const tabInfo = tabNameMap[path]
  if (tabInfo) {
    const existing = tabs.value.find(t => t.name === tabInfo.name)
    if (!existing) {
      tabs.value.push(tabInfo)
    }
    activeTab.value = tabInfo.name
  }
})

onMounted(async () => {
  if (import.meta.client) {
    token.value = localStorage.getItem('admin_token')
    isLoggedIn.value = !!token.value
    await setLocale('zh')

    // Initialize tab from current route
    const tabInfo = tabNameMap[route.path]
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.15);
}
.admin-header :deep(.el-tabs) {
  width: 100%;
}
.admin-header :deep(.el-tabs__header) {
  margin-bottom: 0;
}
.admin-header :deep(.el-tabs__nav-wrap)::after {
  display: none;
}
.admin-header :deep(.el-tabs__item) {
  height: 50px;
  line-height: 50px;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
  border: none;
  font-size: 14px;
}
.admin-header :deep(.el-tabs__item.is-active) {
  color: #fff;
  font-weight: 600;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 8px 8px 0 0;
}
.admin-header :deep(.el-tabs__nav) {
  border: none;
}
.admin-header :deep(.el-tabs__content) {
  display: none;
}
.admin-main {
  background: #f5f7fa;
  padding: 24px;
  min-height: calc(100vh - 50px);
}
</style>
