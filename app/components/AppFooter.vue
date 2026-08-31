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
          <p class="text-gray-400 text-sm mb-4">思谱传感科技有限公司（SpeSense Technologies Inc.）是一家专业从事光电仪器和传感器研发、生产的高新技术企业。</p>
          <div class="flex space-x-4">
            <a href="#" class="text-gray-400 hover:text-white transition-colors"><svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24"><path d="M24 4.557c-.883.392-1.832.656-2.828.775 1.017-.609 1.798-1.574 2.165-2.724-.951.564-2.005.974-3.127 1.195-.897-.957-2.178-1.555-3.594-1.555-3.179 0-5.515 2.966-4.797 6.045-4.091-.205-7.719-2.165-10.148-5.144-1.29 2.213-.669 5.108 1.523 6.574-.806-.026-1.566-.247-2.229-.616-.054 2.281 1.581 4.415 3.949 4.89-.693.188-1.452.232-2.224.084.626 1.956 2.444 3.379 4.6 3.419-2.07 1.623-4.678 2.348-7.29 2.04 2.179 1.397 4.768 2.212 7.548 2.212 9.142 0 14.307-7.721 13.995-14.646.962-.695 1.797-1.562 2.457-2.549z"></path></svg></a>
            <a href="#" class="text-gray-400 hover:text-white transition-colors"><svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24"><path d="M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433c-1.144 0-2.063-.926-2.063-2.065 0-1.138.92-2.063 2.063-2.063 1.14 0 2.064.925 2.064 2.063 0 1.139-.925 2.065-2.064 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z"></path></svg></a>
            <a href="#" class="text-gray-400 hover:text-white transition-colors"><svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24"><path d="M12 0C5.374 0 0 5.373 0 12c0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23A11.509 11.509 0 0112 5.803c1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576C20.566 21.797 24 17.3 24 12c0-6.627-5.373-12-12-12z"></path></svg></a>
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
const footerPhone = computed(() => companyInfo.value?.phone || '')
const footerAddress = computed(() => {
  if (!companyInfo.value) return ''
  return locale.value === 'zh'
    ? (companyInfo.value.addressZh || companyInfo.value.addressEn || '')
    : (companyInfo.value.addressEn || companyInfo.value.addressZh || '')
})
</script>