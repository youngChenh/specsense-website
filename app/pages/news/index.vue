<template>
  <div class="bg-gray-50">
    <!-- Hero Section -->
    <section class="bg-gradient-to-br from-blue-50 to-indigo-100 py-20">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h1 class="text-4xl font-bold text-gray-900 mb-4">{{ $t('news.title') }}</h1>
        <p class="text-xl text-gray-600 max-w-3xl mx-auto">{{ $t('news.subtitle') }}</p>
      </div>
    </section>

    <!-- News Grid -->
    <section class="py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <!-- Filter Tabs -->
        <div class="flex flex-wrap justify-center gap-4 mb-12">
          <button
            v-for="tab in tabs"
            :key="tab.key"
            @click="activeTab = tab.key"
            class="px-6 py-2 rounded-full font-medium transition-colors"
            :class="activeTab === tab.key ? 'bg-blue-600 text-white' : 'bg-white text-gray-600 hover:bg-gray-100'"
          >
            {{ $t(tab.label) }}
          </button>
        </div>

        <!-- Loading State -->
        <div v-if="loading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          <div v-for="i in 6" :key="i" class="bg-white rounded-xl shadow-md overflow-hidden animate-pulse">
            <div class="h-48 bg-gray-200"></div>
            <div class="p-6">
              <div class="h-4 bg-gray-200 rounded w-1/4 mb-3"></div>
              <div class="h-6 bg-gray-200 rounded w-3/4 mb-2"></div>
              <div class="h-4 bg-gray-200 rounded w-full mb-2"></div>
              <div class="h-4 bg-gray-200 rounded w-2/3"></div>
            </div>
          </div>
        </div>

        <!-- News List -->
        <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          <NuxtLink
            v-for="item in filteredNews"
            :key="item.slug || item.id"
            :to="localePath(`/news/${item.slug}`)"
            class="bg-white rounded-xl shadow-md overflow-hidden hover:shadow-xl transition-shadow duration-300 group"
          >
            <div class="bg-gray-200 overflow-hidden">
              <img
                v-if="item.imageUrl"
                :src="getImageUrl(item.imageUrl)"
                :alt="getTitle(item)"
                class="w-full h-48 object-cover group-hover:scale-105 transition-transform duration-300"
              />
              <div v-else class="w-full h-48 bg-gray-200"></div>
            </div>
            <div class="p-6">
              <span class="inline-block px-3 py-1 bg-blue-100 text-blue-600 rounded-full text-xs font-medium mb-3">
                {{ item.category || activeTab }}
              </span>
              <h3 class="text-lg font-semibold text-gray-900 mb-2 group-hover:text-blue-600 transition-colors line-clamp-2">
                {{ getTitle(item) }}
              </h3>
              <p class="text-gray-600 text-sm mb-4 line-clamp-2">
                {{ getExcerpt(item) }}
              </p>
              <div class="flex items-center justify-between">
                <span class="text-sm text-gray-500">{{ item.publishedAt || item.date }}</span>
                <span class="text-blue-600 font-medium text-sm flex items-center">
                  {{ $t('news.readMore') }}
                  <svg class="w-4 h-4 ml-1 group-hover:translate-x-1 transition-transform" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                  </svg>
                </span>
              </div>
            </div>
          </NuxtLink>
        </div>

        <!-- Empty State -->
        <div v-if="!loading && filteredNews.length === 0" class="text-center py-20">
          <p class="text-gray-500 text-lg">No news in this category yet.</p>
        </div>

        <!-- Pagination -->
        <div v-if="!loading && filteredNews.length > 0" class="mt-12 flex justify-center">
          <nav class="flex items-center gap-2">
            <button
              class="px-4 py-2 border rounded-md text-gray-600 hover:bg-gray-50 disabled:opacity-50"
              :disabled="currentPage === 1"
              @click="currentPage--"
            >
              Previous
            </button>
            <button
              v-for="page in totalPages"
              :key="page"
              class="px-4 py-2 border rounded-md"
              :class="currentPage === page ? 'bg-blue-600 text-white' : 'text-gray-600 hover:bg-gray-50'"
              @click="currentPage = page"
            >
              {{ page }}
            </button>
            <button
              class="px-4 py-2 border rounded-md text-gray-600 hover:bg-gray-50 disabled:opacity-50"
              :disabled="currentPage === totalPages"
              @click="currentPage++"
            >
              Next
            </button>
          </nav>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
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
const route = useRoute()
const config = useRuntimeConfig()
const { locale, t } = useI18n()
const { getImageUrl } = useImageUrl()

const activeTab = ref('all')
const currentPage = ref(1)
const pageSize = ref(9)
const totalNews = ref(0)
const loading = ref(true)
const news = ref<any[]>([])

const tabs = [
  { key: 'all', label: 'news.title' },
  { key: 'company', label: 'news.company' },
  { key: 'industry', label: 'news.industry' },
  { key: 'technology', label: 'news.technology' },
]

function getTitle(item: any): string {
  return locale.value === 'zh' ? (item.titleZh || item.titleEn || '') : (item.titleEn || item.titleZh || '')
}

function getExcerpt(item: any): string {
  return locale.value === 'zh' ? (item.excerptZh || item.excerptEn || '') : (item.excerptEn || item.excerptZh || '')
}

const filteredNews = computed(() => {
  if (activeTab.value === 'all') {
    return news.value
  }
  return news.value.filter(item => item.category === activeTab.value)
})

const totalPages = computed(() => Math.ceil(totalNews.value / pageSize.value))

async function fetchNews() {
  loading.value = true
  try {
    const params: Record<string, any> = {
      page: currentPage.value,
      pageSize: pageSize.value,
      locale: locale.value,
    }
    if (activeTab.value !== 'all') {
      params.category = activeTab.value
    }

    const url = `${config.public.apiBase}/api/news`
    const response = await fetch(url + '?' + new URLSearchParams(params))
    const result = await response.json()

    // Handle API response: { code, message, data: { data: [], total, page, pageSize } }
    if (result.code === 200 && result.data) {
      news.value = result.data.data || []
      totalNews.value = result.data.total || 0
    } else {
      news.value = []
      totalNews.value = 0
    }
  } catch (error) {
    console.error('Failed to fetch news:', error)
    news.value = []
    totalNews.value = 0
  } finally {
    loading.value = false
  }
}

watch(activeTab, () => {
  currentPage.value = 1
  fetchNews()
})

watch(currentPage, () => {
  fetchNews()
})

// Refetch when route changes (e.g., navigating from another page)
watch(() => route.path, () => {
  fetchNews()
})

onMounted(() => {
  fetchNews()
})
</script>
