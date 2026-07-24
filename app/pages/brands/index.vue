<template>
  <div class="bg-gray-50 min-h-screen">
    <!-- Page Header -->
    <section class="bg-gradient-to-br from-blue-50 to-indigo-100 py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h1 class="text-4xl font-bold text-gray-900 mb-4">{{ $t('brands.title') }}</h1>
        <p class="text-xl text-gray-600 max-w-3xl mx-auto">{{ $t('brands.subtitle') }}</p>
      </div>
    </section>

    <!-- Brands Content -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div class="flex flex-col lg:flex-row gap-8">
        <!-- Category Sidebar -->
        <div class="lg:w-64 flex-shrink-0">
          <div class="bg-white rounded-xl shadow-sm p-5 sticky top-24">
            <h3 class="text-lg font-bold text-gray-800 mb-4 flex items-center gap-2">
              <svg class="w-5 h-5 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
              </svg>
              {{ $t('brands.categories') }}
            </h3>
            <div class="space-y-1">
              <button
                v-for="cat in categories"
                :key="cat.key"
                @click="selectedCategory = cat.key"
                class="w-full text-left px-4 py-2.5 rounded-lg transition-all duration-200 flex items-center justify-between group"
                :class="selectedCategory === cat.key
                  ? 'bg-blue-600 text-white shadow-md'
                  : 'text-gray-600 hover:bg-gray-100'"
              >
                <span>{{ locale === 'zh' ? cat.nameZh : cat.nameEn }}</span>
                <span
                  v-if="selectedCategory === cat.key"
                  class="text-xs opacity-75"
                >
                  {{ getBrandsByCategory(cat.key).length }}
                </span>
              </button>
            </div>
          </div>
        </div>

        <!-- Brands Grid -->
        <div class="flex-1">
          <!-- Loading State -->
          <div v-if="loading" class="grid grid-cols-2 md:grid-cols-3 xl:grid-cols-4 gap-6">
            <div v-for="i in 8" :key="i" class="bg-white rounded-xl shadow-sm p-6 animate-pulse">
              <div class="h-40 bg-gray-200 rounded-lg mb-4"></div>
              <div class="h-5 bg-gray-200 rounded w-3/4 mx-auto mb-2"></div>
              <div class="h-3 bg-gray-200 rounded w-1/2 mx-auto"></div>
            </div>
          </div>

          <!-- Error State -->
          <div v-else-if="error" class="text-center py-20 bg-white rounded-xl shadow-sm">
            <svg class="w-16 h-16 text-red-400 mx-auto mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
            <p class="text-red-500 text-lg mb-4">{{ error }}</p>
            <button @click="fetchBrands" class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
              {{ $t('admin.retry') || 'Retry' }}
            </button>
          </div>

          <!-- Active Filter Indicator -->
          <div v-else-if="selectedCategory !== 'all'" class="mb-6 flex items-center justify-between bg-gradient-to-r from-blue-50 to-indigo-50 rounded-xl px-5 py-3 border border-blue-100">
            <div class="flex items-center gap-3">
              <span class="text-sm text-gray-600">{{ $t('brands.filterBy') }}:</span>
              <span class="text-sm font-bold text-blue-600">{{ getCategoryName(selectedCategory) }}</span>
              <span class="text-xs text-gray-500">({{ brands.length }} {{ $t('brands.title') || 'brands' }})</span>
            </div>
            <button
              @click="selectedCategory = 'all'"
              class="text-sm text-blue-600 hover:text-blue-800 font-medium flex items-center gap-1"
            >
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
              {{ $t('brands.allBrands') }}
            </button>
          </div>

          <!-- Brands by Category -->
          <div v-if="!loading && !error">
            <div v-for="cat in displayCategories" :key="cat.key" class="mb-10">
              <div class="flex items-center gap-3 mb-5">
                <div class="w-1 h-6 bg-blue-600 rounded-full"></div>
                <h2 class="text-xl font-bold text-gray-800">
                  {{ locale === 'zh' ? cat.nameZh : cat.nameEn }}
                </h2>
                <span class="text-sm text-gray-400">({{ getBrandsByCategory(cat.key).length }})</span>
              </div>

              <div v-if="getBrandsByCategory(cat.key).length > 0" class="grid grid-cols-2 md:grid-cols-3 xl:grid-cols-4 gap-8">
                <div
                  v-for="brand in getBrandsByCategory(cat.key)"
                  :key="brand.slug || brand.id"
                  class="group bg-white rounded-2xl shadow-md hover:shadow-2xl transition-all duration-300 overflow-hidden border border-gray-100 hover:border-blue-300 hover:-translate-y-1"
                >
                  <!-- Logo Area -->
                  <div class="aspect-square bg-gradient-to-br from-gray-50 to-gray-100 relative overflow-hidden">
                    <!-- Featured Badge -->
                    <div
                      v-if="brand.featured"
                      class="absolute top-3 right-3 bg-gradient-to-r from-amber-400 to-orange-500 text-white text-xs font-bold px-3 py-1.5 rounded-full shadow-lg z-10"
                    >
                      {{ locale === 'zh' ? '精选' : 'Featured' }}
                    </div>
                    <img
                      v-if="brand.logoUrl"
                      :src="getFullImageUrl(brand.logoUrl)"
                      :alt="locale === 'zh' ? brand.nameZh : brand.nameEn"
                      class="absolute inset-0 w-full h-full object-contain p-0 group-hover:scale-105 transition-transform duration-500"
                    />
                    <div v-else class="absolute inset-0 flex items-center justify-center text-gray-300">
                      <svg class="w-16 h-16" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                      </svg>
                    </div>
                  </div>

                  <!-- Info Area -->
                  <div class="p-3">
                    <h3 class="font-bold text-gray-800 text-center group-hover:text-blue-600 transition-colors">
                      {{ locale === 'zh' ? brand.nameZh : brand.nameEn }}
                    </h3>
                  </div>
                </div>
              </div>

              <div v-else class="text-center py-12 bg-white rounded-xl border border-dashed border-gray-200">
                <svg class="w-12 h-12 text-gray-300 mx-auto mb-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                </svg>
                <p class="text-gray-400">{{ $t('brands.noBrands') }}</p>
              </div>
            </div>

            <!-- Show all brands when no category selected -->
            <div v-if="selectedCategory === 'all' && brands.length === 0 && !loading" class="text-center py-20 bg-white rounded-xl shadow-sm">
              <svg class="w-16 h-16 text-gray-300 mx-auto mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
              </svg>
              <p class="text-gray-500 text-lg">{{ $t('brands.noBrands') }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const route = useRoute()
const { t, locale } = useI18n()
const config = useRuntimeConfig()

const loading = ref(true)
const error = ref<string | null>(null)
const brands = ref<any[]>([])
const categories = ref<any[]>([])
const selectedCategory = ref('all')

watch(selectedCategory, () => {
  fetchBrands()
})

function getCategoryName(key: string): string {
  for (const cat of categories.value) {
    if (cat.key === key) {
      return locale.value === 'zh' ? cat.nameZh : cat.nameEn
    }
  }
  return key
}

function getBrandsByCategory(categoryKey: string): any[] {
  return brands.value.filter(b => b.categoryKey === categoryKey)
}

const displayCategories = computed(() => {
  if (selectedCategory.value !== 'all') {
    return categories.value.filter(c => c.key === selectedCategory.value)
  }
  return categories.value
})

function getFullImageUrl(url: string): string {
  if (!url) return ''
  const strUrl = String(url)
  if (strUrl.startsWith('http://') || strUrl.startsWith('https://')) {
    return strUrl
  }
  return `${config.public.apiBase.replace('/api', '')}${url}`
}

async function fetchBrands() {
  loading.value = true
  error.value = null
  try {
    const url = `${config.public.apiBase}/api/brands`
    const params: Record<string, any> = {
      page: 1,
      pageSize: 100,
      locale: locale.value,
    }
    if (selectedCategory.value !== 'all') {
      params.category = selectedCategory.value
    }

    const response = await fetch(url + '?' + new URLSearchParams(params))
    const result = await response.json()

    if (result.code === 200 && result.data) {
      brands.value = result.data.data || []
    } else {
      brands.value = []
    }
  } catch (err: any) {
    console.error('Failed to fetch brands:', err)
    error.value = err.message || 'Failed to load brands'
    brands.value = []
  } finally {
    loading.value = false
  }
}

async function fetchCategories() {
  try {
    const url = `${config.public.apiBase}/api/brands/categories`
    const params = { locale: locale.value }

    const response = await fetch(url + '?' + new URLSearchParams(params))
    const result = await response.json()

    if (result.code === 200 && result.data) {
      categories.value = result.data || []
    } else {
      categories.value = []
    }
  } catch (err) {
    console.error('Failed to fetch categories:', err)
    categories.value = []
  }
}

onMounted(() => {
  fetchCategories()
  fetchBrands()
})
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
