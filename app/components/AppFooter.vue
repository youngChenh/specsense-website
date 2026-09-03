<template>
  <footer class="bg-gray-900 text-white">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-8">
        <!-- Brand Column -->
        <div class="lg:col-span-1">
          <div class="flex items-center space-x-2 mb-4">
            <img src="/logo.jpg" alt="SpeSense" class="h-10 w-auto object-contain" />
            <span class="text-xl font-bold">SpeSense</span>
          </div>
          <p class="text-gray-400 text-sm mb-4">{{ footerDescription }}</p>
          <div class="flex space-x-4">
            <!-- Facebook -->
            <a href="https://www.facebook.com/SpeSenseTech" target="_blank" rel="noopener noreferrer" class="text-gray-400 hover:text-white transition-colors">
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24"><path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/></svg>
            </a>
            <!-- Instagram -->
            <a href="https://www.instagram.com/spesense_technologies_inc/" target="_blank" rel="noopener noreferrer" class="text-gray-400 hover:text-white transition-colors">
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24"><path d="M12 2.163c3.204 0 3.584.012 4.85.07 3.252.148 4.771 1.691 4.919 4.919.058 1.265.069 1.645.069 4.849 0 3.205-.012 3.584-.069 4.849-.149 3.225-1.664 4.771-4.919 4.919-1.266.058-1.644.07-4.85.07-3.204 0-3.584-.012-4.849-.07-3.26-.149-4.771-1.699-4.919-4.92-.058-1.265-.07-1.644-.07-4.849 0-3.204.013-3.583.07-4.849.149-3.227 1.664-4.771 4.919-4.919 1.266-.057 1.645-.069 4.849-.069zm0-2.163c-3.259 0-3.667.014-4.947.072-4.358.2-6.78 2.618-6.98 6.98-.059 1.281-.073 1.689-.073 4.948 0 3.259.014 3.668.072 4.948.2 4.358 2.618 6.78 6.98 6.98 1.281.058 1.689.072 4.948.072 3.259 0 3.668-.014 4.948-.072 4.354-.2 6.782-2.618 6.979-6.98.059-1.28.073-1.689.073-4.948 0-3.259-.014-3.667-.072-4.947-.196-4.354-2.617-6.78-6.979-6.98-1.281-.059-1.69-.073-4.949-.073zm0 5.838c-3.403 0-6.162 2.759-6.162 6.162s2.759 6.163 6.162 6.163 6.162-2.759 6.162-6.163c0-3.403-2.759-6.162-6.162-6.162zm0 10.162c-2.209 0-4-1.79-4-4 0-2.209 1.791-4 4-4s4 1.791 4 4c0 2.21-1.791 4-4 4zm6.406-11.845c-.796 0-1.441.645-1.441 1.44s.645 1.44 1.441 1.44c.795 0 1.439-.645 1.439-1.44s-.644-1.44-1.439-1.44z"/></svg>
            </a>
            <!-- Pinterest -->
            <a href="https://www.pinterest.com/spesense/" target="_blank" rel="noopener noreferrer" class="text-gray-400 hover:text-white transition-colors">
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24"><path d="M12 0C5.373 0 0 5.372 0 12c0 5.084 3.163 9.426 7.627 11.174-.105-.949-.2-2.405.042-3.441.218-.937 1.407-5.965 1.407-5.965s-.359-.719-.359-1.782c0-1.668.967-2.914 2.171-2.914 1.023 0 1.518.769 1.518 1.69 0 1.029-.655 2.568-.994 3.995-.283 1.194.599 2.169 1.777 2.169 2.133 0 3.772-2.249 3.772-5.495 0-2.873-2.064-4.882-5.012-4.882-3.414 0-5.418 2.561-5.418 5.207 0 1.031.397 2.138.893 2.738.098.119.112.224.083.345l-.333 1.36c-.053.22-.174.267-.402.161-1.499-.698-2.436-2.889-2.436-4.649 0-3.785 2.75-7.262 7.929-7.262 4.163 0 7.398 2.967 7.398 6.931 0 4.136-2.607 7.464-6.227 7.464-1.216 0-2.359-.631-2.75-1.378l-.748 2.853c-.271 1.043-1.002 2.35-1.492 3.146C9.57 23.812 10.763 24 12 24c6.627 0 12-5.373 12-12 0-6.628-5.373-12-12-12z"/></svg>
            </a>
            <!-- X (Twitter) -->
            <a href="https://x.com/spesense" target="_blank" rel="noopener noreferrer" class="text-gray-400 hover:text-white transition-colors">
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24"><path d="M18.244 2.25h3.308l-7.227 8.26 8.502 11.24H16.17l-5.214-6.817L4.99 21.75H1.68l7.73-8.835L1.254 2.25H8.08l4.713 6.231zm-1.161 17.52h1.833L7.084 4.126H5.117z"/></svg>
            </a>
            <!-- TikTok -->
            <a href="https://www.tiktok.com/@spesense" target="_blank" rel="noopener noreferrer" class="text-gray-400 hover:text-white transition-colors">
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24"><path d="M12.525.02c1.31-.02 2.61-.01 3.91-.02.08 1.53.63 3.09 1.75 4.17 1.12 1.11 2.7 1.62 4.24 1.79v4.03c-1.44-.05-2.89-.35-4.2-.97-.57-.26-1.1-.59-1.62-.93-.01 2.92.01 5.84-.02 8.75-.08 1.4-.54 2.79-1.35 3.94-1.31 1.92-3.58 3.17-5.91 3.21-1.43.08-2.86-.31-4.08-1.03-2.02-1.19-3.44-3.37-3.65-5.71-.02-.5-.03-1-.01-1.49.18-1.9 1.12-3.72 2.58-4.96 1.66-1.44 3.98-2.13 6.15-1.72.02 1.48-.04 2.96-.04 4.44-.99-.32-2.15-.23-3.02.37-.63.41-1.11 1.04-1.36 1.75-.21.51-.15 1.07-.14 1.61.24 1.64 1.82 3.02 3.5 2.87 1.12-.01 2.19-.66 2.77-1.61.19-.33.4-.67.41-1.06.1-1.79.06-3.57.07-5.36.01-4.03-.01-8.05.02-12.07z"/></svg>
            </a>
            <!-- YouTube -->
            <a href="https://www.youtube.com/@SpeSense" target="_blank" rel="noopener noreferrer" class="text-gray-400 hover:text-white transition-colors">
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24"><path d="M23.498 6.186a3.016 3.016 0 0 0-2.122-2.136C19.505 3.545 12 3.545 12 3.545s-7.505 0-9.377.505A3.017 3.017 0 0 0 .502 6.186C0 8.07 0 12 0 12s0 3.93.502 5.814a3.016 3.016 0 0 0 2.122 2.136c1.871.505 9.376.505 9.376.505s7.505 0 9.377-.505a3.015 3.015 0 0 0 2.122-2.136C24 15.93 24 12 24 12s0-3.93-.502-5.814zM9.545 15.568V8.432L15.818 12l-6.273 3.568z"/></svg>
            </a>
            <!-- LinkedIn -->
            <a href="https://www.linkedin.com/in/spesense/recent-activity/all/" target="_blank" rel="noopener noreferrer" class="text-gray-400 hover:text-white transition-colors">
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24"><path d="M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433c-1.144 0-2.063-.926-2.063-2.065 0-1.138.92-2.063 2.063-2.063 1.14 0 2.064.925 2.064 2.063 0 1.139-.925 2.065-2.064 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z"/></svg>
            </a>
          </div>
        </div>

        <!-- Dynamic Footer Columns (from database) -->
        <div v-for="column in footerColumns" :key="column.key">
          <h3 class="text-lg font-semibold mb-4">
            {{ locale === 'zh' ? column.labelZh : column.labelEn }}
          </h3>
          <ul v-if="column.children && column.children.length > 0" class="space-y-2">
            <li v-for="submenu in column.children" :key="submenu.key">
              <NuxtLink
                :to="localePath(submenu.path)"
                class="text-gray-400 hover:text-white transition-colors text-sm"
              >
                {{ locale === 'zh' ? submenu.labelZh : submenu.labelEn }}
              </NuxtLink>
            </li>
          </ul>
        </div>

        <!-- Contact Column -->
        <div>
          <h3 class="text-lg font-semibold mb-4">{{ $t('contact.info.title') }}</h3>
          <ul class="space-y-3 text-sm text-gray-400">
            <li class="flex items-start space-x-2">
              <svg class="w-5 h-5 mt-0.5 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
              </svg>
              <span>{{ footerEmail }}</span>
            </li>
            <li class="flex items-start space-x-2">
              <svg class="w-5 h-5 mt-0.5 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
              </svg>
              <span>{{ footerPhone }}</span>
            </li>
            <li class="flex items-start space-x-2">
              <svg class="w-5 h-5 mt-0.5 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
              <span>{{ footerAddress }}</span>
            </li>
          </ul>
        </div>
      </div>

      <div class="border-t border-gray-800 mt-10 pt-8">
        <div class="flex flex-col md:flex-row justify-between items-center">
          <p class="text-gray-400 text-sm">{{ $t('footer.copyright') }}</p>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
const localePath = useLocalePath()
const config = useRuntimeConfig()
const { locale } = useI18n()

const footerMenus = ref<any[]>([])
const companyInfo = ref<any>(null)

async function loadFooterMenus() {
  try {
    const response = await $fetch(`${config.public.apiBase}/api/navigation/footer`)
    if (response.code === 200 && response.data) {
      footerMenus.value = response.data.filter((m: any) => m.enabled)
    }
  } catch (e) {
    console.warn('Failed to load footer menus')
  }
}

async function loadCompanyInfo() {
  try {
    const response = await fetch(`${config.public.apiBase}/api/company-info?locale=${locale.value}`)
    const result = await response.json()
    if (result.code === 200 && result.data) {
      companyInfo.value = result.data
    }
  } catch (e) {
    console.warn('Failed to load company info')
  }
}

onMounted(() => {
  loadFooterMenus()
  loadCompanyInfo()
})

watch(locale, () => {
  loadFooterMenus()
  loadCompanyInfo()
})

// Build tree structure for footer menus
const footerTreeData = computed(() => {
  return buildTree(footerMenus.value)
})

function buildTree(menus: any[]) {
  const map: Record<number, any> = {}
  const roots: any[] = []

  menus.forEach(menu => {
    map[menu.id] = { ...menu, children: [] }
  })

  menus.forEach(menu => {
    if (menu.parentId && map[menu.parentId]) {
      map[menu.parentId].children.push(map[menu.id])
    } else if (!menu.parentId) {
      roots.push(map[menu.id])
    }
  })

  // Sort by sortOrder
  const sortByOrder = (arr: any[]) => {
    arr.sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0))
    arr.forEach(item => {
      if (item.children && item.children.length > 0) {
        sortByOrder(item.children)
      }
    })
    return arr
  }

  return sortByOrder(roots)
}

// Get footer columns (parent menus with enabled=true)
const footerColumns = computed(() => {
  return footerTreeData.value.filter(m => m.enabled)
})

const footerEmail = computed(() => companyInfo.value?.email || '')

const footerDescription = computed(() => {
  return locale.value === 'zh'
    ? '思谱传感科技有限公司（SpeSense Technologies Inc.）是一家专业从事光电仪器和传感器研发、生产的高新技术企业。'
    : 'SpeSense Technologies Inc. is a high-tech enterprise specializing in the R&D and manufacturing of photoelectric instruments and sensors.'
})
const footerPhone = computed(() => companyInfo.value?.phone || '')
const footerAddress = computed(() => {
  if (!companyInfo.value) return ''
  return locale.value === 'zh'
    ? (companyInfo.value.addressZh || companyInfo.value.addressEn || '')
    : (companyInfo.value.addressEn || companyInfo.value.addressZh || '')
})
</script>