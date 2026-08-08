<template>
  <header class="bg-white shadow-sm sticky top-0 z-50">
    <nav class="flex justify-between items-center h-20 px-4 sm:px-6 lg:px-8 max-w-[1800px] mx-auto">
      <!-- Logo - Left -->
      <NuxtLink :to="localePath('/')" class="flex items-center flex-shrink-0">
        <img src="/logo.jpg" alt="SpeSense" class="h-20 w-auto object-contain" />
      </NuxtLink>

      <!-- Desktop Navigation - Center -->
      <div class="hidden md:flex items-center gap-1 flex-1 justify-center">
        <template v-for="item in navItems" :key="item.path">
          <!-- Menu items with children -->
          <div v-if="item.children && item.children.length > 0" class="relative" @mouseenter="activeMenu = item.key" @mouseleave="handleMenuLeave">
            <NuxtLink
              :to="localePath(item.path)"
              class="px-4 py-3 text-lg text-gray-600 hover:text-blue-600 font-medium transition-colors flex items-center gap-0.5"
              :class="{ 'text-blue-600': isActiveMenu(item.key) }"
            >
              {{ getLabel(item) }}
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
              </svg>
            </NuxtLink>
            <!-- Dropdown Menu -->
            <div v-if="activeMenu === item.key" class="absolute left-0 top-full mt-1 w-56 bg-white rounded-lg shadow-lg border border-gray-100 py-2 z-50" @mouseenter="cancelLeaveTimer" @mouseleave="handleMenuLeave">
              <template v-for="child in item.children" :key="child.key">
                <!-- Item with children: clickable header + dropdown on hover -->
                <div v-if="child.children && child.children.length > 0" class="relative group">
                  <NuxtLink
                    :to="localePath(child.path)"
                    class="w-full px-4 py-2 text-left text-gray-600 hover:text-blue-600 hover:bg-gray-50 flex items-center justify-between"
                  >
                    {{ getLabel(child) }}
                    <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                    </svg>
                  </NuxtLink>
                  <!-- Second Level Dropdown -->
                  <div class="absolute left-full top-0 ml-1 w-48 bg-white rounded-lg shadow-lg border border-gray-100 py-2 hidden group-hover:block">
                    <template v-for="subChild in child.children" :key="subChild.key">
                      <!-- 二级分类有三级子分类 -->
                      <div v-if="subChild.children && subChild.children.length > 0" class="relative group">
                        <NuxtLink
                          :to="localePath(subChild.path)"
                          class="w-full px-4 py-2 text-left text-gray-600 hover:text-blue-600 hover:bg-gray-50 flex items-center justify-between"
                        >
                          {{ getLabel(subChild) }}
                          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                          </svg>
                        </NuxtLink>
                        <!-- Third Level Dropdown -->
                        <div class="absolute left-full top-0 ml-1 w-48 bg-white rounded-lg shadow-lg border border-gray-100 py-2 hidden group-hover:block">
                          <NuxtLink
                            v-for="thirdChild in subChild.children"
                            :key="thirdChild.key"
                            :to="localePath(thirdChild.path)"
                            class="block px-4 py-2 text-gray-600 hover:text-blue-600 hover:bg-gray-50"
                          >
                            {{ getLabel(thirdChild) }}
                          </NuxtLink>
                        </div>
                      </div>
                      <!-- 二级分类没有三级子分类 -->
                      <NuxtLink v-else :to="localePath(subChild.path)" class="block px-4 py-2 text-gray-600 hover:text-blue-600 hover:bg-gray-50">
                        {{ getLabel(subChild) }}
                      </NuxtLink>
                    </template>
                  </div>
                </div>
                <!-- Item without children: simple link -->
                <NuxtLink v-else :to="localePath(child.path)" class="block px-4 py-2 text-gray-600 hover:text-blue-600 hover:bg-gray-50">
                  {{ getLabel(child) }}
                </NuxtLink>
              </template>
            </div>
          </div>
          <!-- Simple menu items without children -->
          <NuxtLink v-else :to="localePath(item.path)" class="px-4 py-3 text-lg text-gray-600 hover:text-blue-600 font-medium transition-colors flex items-center gap-1.5" active-class="text-blue-600">
            <svg v-if="item.key === 'contact'" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
            </svg>
            {{ getLabel(item) }}
          </NuxtLink>
        </template>

        <!-- Language Switcher -->
        <div class="flex items-center gap-1 border-l border-gray-200 pl-3 ml-2">
          <button
            v-for="locale in availableLocales"
            :key="locale.code"
            @click="switchLocale(locale.code)"
            class="px-2 py-1 text-sm rounded cursor-pointer"
            :class="currentLocale === locale.code ? 'bg-blue-100 text-blue-600' : 'text-gray-500 hover:text-gray-700'"
          >
            {{ locale.name }}
          </button>
        </div>
      </div>

      <!-- Mobile menu button -->
      <button
        @click="mobileMenuOpen = !mobileMenuOpen"
        class="md:hidden p-2 rounded-md text-gray-600 hover:text-gray-900 hover:bg-gray-100 ml-4"
      >
        <svg v-if="!mobileMenuOpen" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
        </svg>
        <svg v-else class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>

      <!-- Mobile Navigation -->
      <div v-if="mobileMenuOpen" class="md:hidden py-4 border-t">
        <div class="flex flex-col space-y-2">
          <template v-for="item in navItems" :key="item.path">
            <div v-if="item.children && item.children.length > 0">
              <button
                @click="toggleMobileMenu(item.key)"
                class="w-full px-4 py-2 text-left text-gray-600 hover:text-blue-600 hover:bg-gray-50 flex items-center justify-between"
              >
                {{ getLabel(item) }}
                <svg class="w-4 h-4" :class="{ 'rotate-180': expandedMobileMenu === item.key }" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                </svg>
              </button>
              <div v-if="expandedMobileMenu === item.key" class="pl-6 space-y-1">
                <template v-for="child in item.children" :key="child.key">
                  <div v-if="child.children && child.children.length > 0" class="space-y-1">
                    <NuxtLink
                      :to="localePath(child.path)"
                      class="block px-4 py-1 text-sm text-blue-600 font-medium hover:text-blue-700"
                      @click="mobileMenuOpen = false"
                    >
                      {{ getLabel(child) }}
                    </NuxtLink>
                    <template v-for="subChild in child.children" :key="subChild.key">
                      <!-- 二级分类有三级子分类 -->
                      <div v-if="subChild.children && subChild.children.length > 0" class="space-y-1">
                        <NuxtLink
                          :to="localePath(subChild.path)"
                          class="block px-6 py-1 text-sm text-gray-500 hover:text-blue-600"
                          @click="mobileMenuOpen = false"
                        >
                          {{ getLabel(subChild) }}
                        </NuxtLink>
                        <NuxtLink
                          v-for="thirdChild in subChild.children"
                          :key="thirdChild.key"
                          :to="localePath(thirdChild.path)"
                          class="block px-8 py-1 text-gray-400 hover:text-blue-600 text-sm"
                          @click="mobileMenuOpen = false"
                        >
                          {{ getLabel(thirdChild) }}
                        </NuxtLink>
                      </div>
                      <!-- 二级分类没有三级子分类 -->
                      <NuxtLink
                        v-else
                        :to="localePath(subChild.path)"
                        class="block px-6 py-1 text-gray-500 hover:text-blue-600 text-sm"
                        @click="mobileMenuOpen = false"
                      >
                        {{ getLabel(subChild) }}
                      </NuxtLink>
                    </template>
                  </div>
                  <NuxtLink
                    v-else
                    :to="localePath(child.path)"
                    class="block px-6 py-2 text-gray-500 hover:text-blue-600"
                    @click="mobileMenuOpen = false"
                  >
                    {{ getLabel(child) }}
                  </NuxtLink>
                </template>
              </div>
            </div>
            <NuxtLink
              v-else
              :to="localePath(item.path)"
              class="px-4 py-2 text-gray-600 hover:text-blue-600"
              @click="mobileMenuOpen = false"
            >
              {{ getLabel(item) }}
            </NuxtLink>
          </template>
          <div class="flex space-x-4 pt-4 border-t mt-2">
            <button
              v-for="locale in availableLocales"
              :key="locale.code"
              @click="switchLocale(locale.code)"
              class="px-3 py-1 text-sm rounded border"
              :class="currentLocale === locale.code ? 'border-blue-600 text-blue-600' : 'border-gray-300 text-gray-500'"
            >
              {{ locale.name }}
            </button>
          </div>
        </div>
      </div>
    </nav>
  </header>
</template>

<script setup lang="ts">
const localePath = useLocalePath()
const { locale, locales, setLocale } = useI18n()
const switchLocalePath = useSwitchLocalePath()
const route = useRoute()
const api = useApi()
const config = useRuntimeConfig()

const mobileMenuOpen = ref(false)
const activeMenu = ref<string | null>(null)
const expandedMobileMenu = ref<string | null>(null)
const leaveTimer = ref<ReturnType<typeof setTimeout> | null>(null)
const currentLocale = computed(() => locale.value)

const availableLocales = computed(() => {
  const allLocales = Array.isArray(locales.value) ? locales.value : []
  return allLocales
})

// Dynamic categories from API
const dynamicCategories = ref<any[]>([])
const headerMenus = ref<any[]>([])

async function loadCategories() {
  try {
    const categories = await api.fetchCategories(locale.value)
    dynamicCategories.value = categories || []
  } catch (e) {
    console.warn('Failed to load categories from API, using static data')
  }
}

async function loadHeaderMenus() {
  try {
    const response = await $fetch(`${config.public.apiBase}/api/navigation/tree/header`)
    if (response.code === 200 && response.data) {
      headerMenus.value = response.data
    }
  } catch (e) {
    console.warn('Failed to load header menus')
  }
}

// Build nav items from dynamic categories
const navItems = computed(() => {
  // If we have header menus from API, use them directly
  if (headerMenus.value.length > 0) {
    return headerMenus.value.map(menu => {
      // 产品中心使用 dynamicCategories 来获取三级分类
      if (menu.key === 'header-products' && dynamicCategories.value.length > 0) {
        return {
          key: menu.key,
          path: menu.path,
          label: locale.value === 'zh' ? menu.labelZh : menu.labelEn,
          children: dynamicCategories.value.map((cat: any) => ({
            key: cat.key,
            path: `/products?category=${cat.key}`,
            label: locale.value === 'zh' ? cat.nameZh : cat.nameEn,
            children: (cat.children || []).map((subCat: any) => ({
              key: subCat.key,
              path: `/products?category=${subCat.key}`,
              label: locale.value === 'zh' ? subCat.nameZh : subCat.nameEn,
              children: (subCat.children || []).map((thirdCat: any) => ({
                key: thirdCat.key,
                path: `/products?category=${thirdCat.key}`,
                label: locale.value === 'zh' ? thirdCat.nameZh : thirdCat.nameEn,
              })),
            })),
          })),
        }
      }
      return {
        key: menu.key,
        path: menu.path,
        label: locale.value === 'zh' ? menu.labelZh : menu.labelEn,
        children: (menu.children || []).map(child => ({
          key: child.key,
          path: child.path,
          label: locale.value === 'zh' ? child.labelZh : child.labelEn,
        })),
      }
    })
  }

  if (dynamicCategories.value.length > 0) {
    return [
      { key: 'home', path: '/', label: 'nav.home' },
      {
        key: 'products',
        path: '/products',
        label: 'nav.products',
        children: dynamicCategories.value.map((cat: any) => ({
          key: cat.key,
          path: `/products?category=${cat.key}`,
          label: locale.value === 'zh' ? cat.nameZh : cat.nameEn,
          children: (cat.children || []).map((subCat: any) => ({
            key: subCat.key,
            path: `/products?category=${subCat.key}`,
            label: locale.value === 'zh' ? subCat.nameZh : subCat.nameEn,
            children: (subCat.children || []).map((thirdCat: any) => ({
              key: thirdCat.key,
              path: `/products?category=${thirdCat.key}`,
              label: locale.value === 'zh' ? thirdCat.nameZh : thirdCat.nameEn,
            })),
          })),
        })),
      },
      { key: 'applications', path: '/applications', label: 'nav.applications', children: buildApplicationsChildren() },
      { key: 'services', path: '/services', label: 'nav.services', children: buildServicesChildren() },
      { key: 'news', path: '/news', label: 'nav.news' },
      { key: 'about', path: '/about', label: 'nav.about', children: [
        { key: 'company', path: '/about?section=company', label: 'nav.about.company' },
        { key: 'qualifications', path: '/about?section=qualifications', label: 'nav.about.qualifications' },
        { key: 'partners', path: '/about?section=partners', label: 'nav.about.partners' },
        { key: 'timeline', path: '/about?section=timeline', label: 'nav.about.timeline' },
      ]},
      { key: 'contact', path: '/contact', label: 'nav.contact' },
    ]
  }
  return staticNavItems
})

// Helper functions to build children
function buildProductChildren() {
  if (dynamicCategories.value.length > 0) {
    return dynamicCategories.value.map((cat: any) => ({
      key: cat.key,
      label: cat.name,
      children: (cat.children || []).map((subCat: any) => ({
        key: subCat.key,
        label: subCat.name,
        path: `/products?category=${subCat.key}`,
      })),
    }))
  }
  return [
    { key: 'spectrometer', i18nKey: 'products.categories.spectrometer', path: '/products?category=spectrometer' },
    { key: 'analyzer', i18nKey: 'products.categories.analyzer', path: '/products?category=analyzer' },
    { key: 'sensor', i18nKey: 'products.categories.sensor', path: '/products?category=sensor' },
    { key: 'lightsource', i18nKey: 'products.categories.lightsource', path: '/products?category=lightsource' },
    { key: 'accessory', i18nKey: 'products.categories.accessory', path: '/products?category=accessory' },
  ]
}

function buildServicesChildren() {
  return [
    { key: 'technical', i18nKey: 'services.technical.title', path: '/services?type=technical' },
    { key: 'aftersales', i18nKey: 'services.afterSales.title', path: '/services?type=aftersales' },
    { key: 'downloads', i18nKey: 'services.downloads.title', path: '/services?type=downloads' },
    { key: 'training', i18nKey: 'services.training.title', path: '/services?type=training' },
  ]
}

function buildApplicationsChildren() {
  return [
    { key: 'industrial', i18nKey: 'applications.industrial.title', path: '/applications?type=industrial' },
    { key: 'research', i18nKey: 'applications.research.title', path: '/applications?type=research' },
    { key: 'medical', i18nKey: 'applications.medical.title', path: '/applications?type=medical' },
    { key: 'environment', i18nKey: 'applications.environment.title', path: '/applications?type=environment' },
    { key: 'agriculture', i18nKey: 'applications.agriculture.title', path: '/applications?type=agriculture' },
    { key: 'food', i18nKey: 'applications.food.title', path: '/applications?type=food' },
  ]
}

const staticNavItems = [
  { key: 'home', path: '/', label: 'nav.home' },
  {
    key: 'products',
    path: '/products',
    label: 'nav.products',
    children: [
      {
        key: 'spectrometer',
        label: 'products.categories.spectrometer',
        children: [
          { key: 'spectrometer-uv', path: '/products?category=spectrometer-uv', label: 'products.subcategories.spectrometer.uv' },
          { key: 'spectrometer-nir', path: '/products?category=spectrometer-nir', label: 'products.subcategories.spectrometer.nir' },
          { key: 'spectrometer-raman', path: '/products?category=spectrometer-raman', label: 'products.subcategories.spectrometer.raman' },
          { key: 'spectrometer-mini', path: '/products?category=spectrometer-mini', label: 'products.subcategories.spectrometer.mini' },
        ]
      },
      {
        key: 'analyzer',
        label: 'products.categories.analyzer',
        children: [
          { key: 'analyzer-photoelectric', path: '/products?category=analyzer-photoelectric', label: 'products.subcategories.analyzer.photoelectric' },
          { key: 'analyzer-photometer', path: '/products?category=analyzer-photometer', label: 'products.subcategories.analyzer.photometer' },
          { key: 'analyzer-color', path: '/products?category=analyzer-color', label: 'products.subcategories.analyzer.color' },
        ]
      },
      {
        key: 'sensor',
        label: 'products.categories.sensor',
        children: [
          { key: 'sensor-fiber', path: '/products?category=sensor-fiber', label: 'products.subcategories.sensor.fiber' },
          { key: 'sensor-photoelectric', path: '/products?category=sensor-photoelectric', label: 'products.subcategories.sensor.photoelectric' },
          { key: 'sensor-displacement', path: '/products?category=sensor-displacement', label: 'products.subcategories.sensor.displacement' },
        ]
      },
      {
        key: 'lightsource',
        label: 'products.categories.lightsource',
        children: [
          { key: 'lightsource-laser', path: '/products?category=lightsource-laser', label: 'products.subcategories.lightsource.laser' },
          { key: 'lightsource-led', path: '/products?category=lightsource-led', label: 'products.subcategories.lightsource.led' },
          { key: 'lightsource-halogen', path: '/products?category=lightsource-halogen', label: 'products.subcategories.lightsource.halogen' },
        ]
      },
      {
        key: 'accessory',
        label: 'products.categories.accessory',
        children: [
          { key: 'accessory-fiberprobe', path: '/products?category=accessory-fiberprobe', label: 'products.subcategories.accessory.fiberProbe' },
          { key: 'accessory-holder', path: '/products?category=accessory-holder', label: 'products.subcategories.accessory.holder' },
          { key: 'accessory-coupler', path: '/products?category=accessory-coupler', label: 'products.subcategories.accessory.coupler' },
        ]
      },
    ]
  },
  {
    key: 'applications',
    path: '/applications',
    label: 'nav.applications',
    children: [
      { key: 'industrial', path: '/applications?type=industrial', label: 'applications.industrial.title' },
      { key: 'research', path: '/applications?type=research', label: 'applications.research.title' },
      { key: 'medical', path: '/applications?type=medical', label: 'applications.medical.title' },
      { key: 'environment', path: '/applications?type=environment', label: 'applications.environment.title' },
      { key: 'agriculture', path: '/applications?type=agriculture', label: 'applications.agriculture.title' },
      { key: 'food', path: '/applications?type=food', label: 'applications.food.title' },
    ]
  },
  {
    key: 'services',
    path: '/services',
    label: 'nav.services',
    children: [
      { key: 'technical', path: '/services?type=technical', label: 'services.technical.title' },
      { key: 'aftersales', path: '/services?type=aftersales', label: 'services.afterSales.title' },
      { key: 'downloads', path: '/services?type=downloads', label: 'services.downloads.title' },
      { key: 'training', path: '/services?type=training', label: 'services.training.title' },
    ]
  },
  { key: 'news', path: '/news', label: 'nav.news' },
  {
    key: 'about',
    path: '/about',
    label: 'nav.about',
    children: [
      { key: 'company', path: '/about?section=company', label: 'about.company.title' },
      { key: 'qualifications', path: '/about?section=qualifications', label: 'about.qualifications.title' },
      { key: 'partners', path: '/about?section=partners', label: 'about.partners.title' },
      { key: 'timeline', path: '/about?section=timeline', label: 'about.timeline.title' },
    ]
  },
  { key: 'contact', path: '/contact', label: 'nav.contact' },
]

// Helper function to get label with i18n support
function getLabel(item: any) {
  if (item.i18nKey) {
    return $t(item.i18nKey)
  }
  // If label contains dots and lowercase, treat as i18n key
  if (item.label && item.label.includes('.') && item.label === item.label.toLowerCase()) {
    return $t(item.label)
  }
  return item.label
}

// Load categories on mount
onMounted(() => {
  loadCategories()
  loadHeaderMenus()
})

// Reload categories when locale changes
watch(locale, () => {
  loadCategories()
  loadHeaderMenus()
})

function isActiveMenu(key: string) {
  return route.path.includes(key)
}

function toggleMobileMenu(key: string) {
  expandedMobileMenu.value = expandedMobileMenu.value === key ? null : key
}

function handleMenuLeave() {
  leaveTimer.value = setTimeout(() => {
    activeMenu.value = null
  }, 100)
}

function cancelLeaveTimer() {
  if (leaveTimer.value) {
    clearTimeout(leaveTimer.value)
    leaveTimer.value = null
  }
}

function switchLocale(code: string) {
  setLocale(code)
  mobileMenuOpen.value = false
}
</script>
