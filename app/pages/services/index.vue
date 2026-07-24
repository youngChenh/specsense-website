<template>
  <div class="bg-gray-50">
    <!-- Hero Section -->
    <section class="bg-gradient-to-br from-blue-50 to-indigo-100 py-20">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h1 class="text-4xl font-bold text-gray-900 mb-4">{{ $t('services.title') }}</h1>
        <p class="text-xl text-gray-600 max-w-3xl mx-auto">{{ $t('services.subtitle') }}</p>
      </div>
    </section>

    <!-- Services Grid -->
    <section class="py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
          <div
            v-for="service in services"
            :key="service.id"
            class="bg-white rounded-xl shadow-md p-8"
          >
            <div class="w-16 h-16 bg-blue-100 rounded-lg flex items-center justify-center mb-6">
              <svg v-if="service.type === 'technical'" class="w-8 h-8 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 5.636l-3.536 3.536m0 5.656l3.536 3.536M9.172 9.172L5.636 5.636m3.536 9.192l-3.536 3.536M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-5 0a4 4 0 11-8 0 4 4 0 018 0z" />
              </svg>
              <svg v-else-if="service.type === 'aftersales'" class="w-8 h-8 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
              </svg>
              <svg v-else-if="service.type === 'training'" class="w-8 h-8 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
              </svg>
              <svg v-else class="w-8 h-8 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
              </svg>
            </div>
            <h3 class="text-2xl font-bold text-gray-900 mb-4">{{ service.title }}</h3>
            <p class="text-gray-600 mb-4">{{ service.description }}</p>
            <NuxtLink
              v-if="service.type === 'downloads'"
              to="#downloads"
              class="inline-flex items-center px-6 py-3 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 transition-colors"
            >
              {{ $t('common.viewAll') }}
              <svg class="w-5 h-5 ml-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 14l-7 7m0 0l-7-7m7 7V3" />
              </svg>
            </NuxtLink>
          </div>
        </div>
      </div>
    </section>

    <!-- Downloads Center -->
    <section id="downloads" class="py-16 bg-white">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h2 class="text-3xl font-bold text-center text-gray-900 mb-8">{{ $t('services.downloads.title') }}</h2>

        <!-- Category Tabs -->
        <div class="flex flex-wrap justify-center gap-3 mb-10">
          <button
            v-for="cat in downloadCategories"
            :key="cat.key"
            @click="activeCategory = cat.key"
            class="px-5 py-2 rounded-full font-medium transition-colors"
            :class="activeCategory === cat.key ? 'bg-blue-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'"
          >
            {{ $t(cat.label) }}
          </button>
        </div>

        <!-- Downloads Table -->
        <div class="bg-white rounded-xl shadow-md overflow-hidden">
          <!-- Table Header -->
          <div class="hidden md:grid grid-cols-12 gap-4 px-6 py-4 bg-gray-50 border-b border-gray-200 text-sm font-medium text-gray-600">
            <div class="col-span-5">{{ $t('services.downloads.fileName') || 'File Name' }}</div>
            <div class="col-span-4">{{ $t('services.downloads.description') || 'Description' }}</div>
            <div class="col-span-2 text-center">{{ $t('services.downloads.fileSize') || 'Size' }}</div>
            <div class="col-span-1 text-center">{{ $t('services.downloads.download') || 'Download' }}</div>
          </div>

          <!-- Table Body -->
          <div class="divide-y divide-gray-100">
            <div
              v-for="(file, index) in paginatedDownloads"
              :key="file.id"
              class="px-6 py-4 hover:bg-gray-50 transition-colors"
            >
              <!-- Mobile Layout -->
              <div class="md:hidden space-y-3">
                <div class="flex items-start justify-between">
                  <div class="flex items-center gap-3">
                    <div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center flex-shrink-0">
                      <svg class="w-5 h-5 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 21h10a2 2 0 002-2V9.414a1 1 0 00-.293-.707l-5.414-5.414A1 1 0 0012.586 3H7a2 2 0 00-2 2v14a2 2 0 002 2z" />
                      </svg>
                    </div>
                    <div>
                      <p class="font-medium text-gray-900">{{ file.name }}</p>
                      <p class="text-sm text-gray-500">{{ file.description }}</p>
                    </div>
                  </div>
                  <a
                    :href="getFullDownloadUrl(file.fileUrl, file.originalFilename)"
                    :download="file.originalFilename"
                    class="px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 transition-colors flex-shrink-0"
                  >
                    <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                    </svg>
                  </a>
                </div>
                <div class="flex items-center gap-4 text-sm text-gray-500 pl-13">
                  <span>{{ file.fileSize }}</span>
                </div>
              </div>

              <!-- Desktop Layout -->
              <div class="hidden md:grid grid-cols-12 gap-4 items-center">
                <div class="col-span-5 flex items-center gap-3">
                  <div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center flex-shrink-0">
                    <svg class="w-5 h-5 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 21h10a2 2 0 002-2V9.414a1 1 0 00-.293-.707l-5.414-5.414A1 1 0 0012.586 3H7a2 2 0 00-2 2v14a2 2 0 002 2z" />
                    </svg>
                  </div>
                  <div>
                    <p class="font-medium text-gray-900">{{ file.name }}</p>
                    <p class="text-xs text-gray-400">{{ file.category }}</p>
                  </div>
                </div>
                <div class="col-span-4">
                  <p class="text-sm text-gray-600 line-clamp-1">{{ file.description }}</p>
                </div>
                <div class="col-span-2 text-center text-sm text-gray-500">
                  {{ file.fileSize }}
                </div>
                <div class="col-span-1 flex justify-center">
                  <a
                    :href="getFullDownloadUrl(file.fileUrl, file.originalFilename)"
                    :download="file.originalFilename"
                    class="w-10 h-10 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors flex items-center justify-center"
                    title="Download"
                  >
                    <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                    </svg>
                  </a>
                </div>
              </div>
            </div>
          </div>

          <!-- Empty State -->
          <div v-if="filteredDownloads.length === 0" class="text-center py-12">
            <svg class="w-16 h-16 text-gray-300 mx-auto mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <p class="text-gray-500">No files in this category.</p>
          </div>
        </div>

        <!-- Pagination -->
        <div v-if="totalPages > 1" class="mt-8 flex justify-center">
          <nav class="flex items-center gap-2">
            <button
              class="px-4 py-2 border rounded-md text-gray-600 hover:bg-gray-50 disabled:opacity-50"
              :disabled="currentPage === 1"
              @click="goToPage(currentPage - 1)"
            >
              Previous
            </button>
            <button
              v-for="page in totalPages"
              :key="page"
              class="px-4 py-2 border rounded-md hover:bg-gray-50"
              :class="currentPage === page ? 'bg-blue-600 text-white' : 'text-gray-600'"
              @click="goToPage(page)"
            >
              {{ page }}
            </button>
            <button
              class="px-4 py-2 border rounded-md text-gray-600 hover:bg-gray-50 disabled:opacity-50"
              :disabled="currentPage === totalPages"
              @click="goToPage(currentPage + 1)"
            >
              Next
            </button>
          </nav>
        </div>
      </div>
    </section>

    <!-- Contact CTA -->
    <section class="py-16 bg-blue-600">
      <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h2 class="text-3xl font-bold text-white mb-4">{{ $t('home.cta.title') }}</h2>
        <p class="text-xl text-blue-100 mb-8">{{ $t('home.cta.desc') }}</p>
        <NuxtLink
          :to="localePath('/contact')"
          class="inline-flex items-center px-8 py-3 bg-white text-blue-600 font-semibold rounded-lg hover:bg-gray-100 transition-colors"
        >
          {{ $t('home.cta.button') }}
        </NuxtLink>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
const localePath = useLocalePath()
const config = useRuntimeConfig()
const { locale } = useI18n()

const activeCategory = ref('all')
const services = ref<any[]>([])

const downloadCategories = [
  { key: 'all', label: 'services.downloads.all' },
  { key: 'manuals', label: 'services.downloads.manuals' },
  { key: 'software', label: 'services.downloads.software' },
  { key: 'datasheets', label: 'services.downloads.datasheets' },
  { key: 'catalogs', label: 'services.downloads.catalogs' },
]

const downloads = ref<any[]>([])
const isLoading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)

const fetchServices = async () => {
  try {
    const response = await fetch(`${config.public.apiBase}/api/services?locale=${locale.value}`)
    const result = await response.json()
    if (result.code === 200 && result.data) {
      services.value = result.data
    }
  } catch (error) {
    console.error('Failed to fetch services:', error)
  }
}

const fetchDownloads = async () => {
  isLoading.value = true
  try {
    const response = await fetch(`${config.public.apiBase}/api/downloads?locale=${locale.value}`)
    const result = await response.json()
    if (result.code === 200 && result.data) {
      downloads.value = result.data
    }
  } catch (error) {
    console.error('Failed to fetch downloads:', error)
  } finally {
    isLoading.value = false
  }
}

const filteredDownloads = computed(() => {
  let result = downloads.value
  if (activeCategory.value !== 'all') {
    result = result.filter(d => d.category === activeCategory.value)
  }
  return result
})

const totalItems = computed(() => filteredDownloads.value.length)
const totalPages = computed(() => Math.ceil(totalItems.value / pageSize.value))
const paginatedDownloads = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredDownloads.value.slice(start, end)
})

function goToPage(page: number) {
  if (page < 1 || page > totalPages.value) return
  currentPage.value = page
}

watch(activeCategory, () => {
  currentPage.value = 1
})

const getFullDownloadUrl = (url: string, filename?: string) => {
  if (!url) return '#'
  if (url.startsWith('http')) return url
  let downloadUrl = `${config.public.apiBase}${url}`
  if (filename) {
    const encodedName = encodeURIComponent(filename)
    downloadUrl += `?name=${encodedName}`
  }
  return downloadUrl
}

onMounted(() => {
  fetchServices()
  fetchDownloads()
})

watch(locale, () => {
  fetchServices()
  fetchDownloads()
})
</script>
