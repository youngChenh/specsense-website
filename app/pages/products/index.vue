<template>
  <div class="bg-gray-50 py-8">
    <!-- Page Header -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 mb-8">
      <div class="text-center">
        <h1 class="text-4xl font-bold text-gray-900 mb-4">{{ $t('products.title') }}</h1>
        <p class="text-xl text-gray-600">{{ $t('products.subtitle') }}</p>
      </div>
    </div>

    <!-- Products Layout with Sidebar -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex flex-col lg:flex-row gap-8">
        <!-- Product Sidebar -->
        <ProductSidebar
          :selected-category="selectedCategory"
          :categories="sidebarCategories"
          @category-change="handleCategoryChange"
        />

        <!-- Products Grid -->
        <div class="flex-1">
          <!-- Loading State -->
          <div v-if="loading" class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
            <div v-for="i in 6" :key="i" class="bg-white rounded-lg shadow-sm p-4 animate-pulse">
              <div class="h-48 bg-gray-200 rounded-md mb-4"></div>
              <div class="h-4 bg-gray-200 rounded w-3/4 mb-2"></div>
              <div class="h-4 bg-gray-200 rounded w-1/2"></div>
            </div>
          </div>

          <!-- Error State -->
          <div v-else-if="error" class="text-center py-20 bg-white rounded-lg">
            <p class="text-red-500 text-lg mb-4">{{ error }}</p>
            <button @click="fetchProducts" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">
              Retry
            </button>
          </div>

          <!-- Active Filter Indicator -->
          <div v-else-if="searchQuery || selectedCategory !== 'all'" class="mb-4 flex items-center justify-between bg-blue-50 rounded-lg px-4 py-2">
            <div class="flex items-center gap-2 flex-wrap">
              <span class="text-sm text-gray-600">{{ searchQuery ? '搜索' : $t('products.filterBy') }}:</span>
              <span v-if="searchQuery" class="text-sm font-medium text-blue-600">"{{ searchQuery }}"</span>
              <span v-if="searchQuery && selectedCategory !== 'all'" class="text-gray-400 text-sm">·</span>
              <span v-if="selectedCategory !== 'all'" class="text-sm font-medium text-blue-600">{{ getCategoryName(selectedCategory) }}</span>
              <span class="text-xs text-gray-500">({{ totalProducts }})</span>
            </div>
            <button
              @click="clearFilters"
              class="text-sm text-blue-600 hover:text-blue-800"
            >
              {{ $t('products.allProducts') }}
            </button>
          </div>

          <!-- Products Grid -->
          <div v-if="!loading && !error" class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
            <ProductCard
              v-for="product in products"
              :key="product.slug || product.id"
              :product="product"
            />
          </div>

          <!-- Empty State -->
          <div v-if="!loading && !error && products.length === 0" class="text-center py-20 bg-white rounded-lg">
            <svg class="w-16 h-16 text-gray-300 mx-auto mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
            </svg>
            <p class="text-gray-500 text-lg">
              {{ searchQuery ? `没有找到与 "${searchQuery}" 匹配的产品` : 'No products found in this category.' }}
            </p>
          </div>

          <!-- Pagination -->
          <div v-if="!loading && !error && totalProducts > pageSize" class="mt-12 flex justify-center">
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
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const route = useRoute()
const { t, locale } = useI18n()
const config = useRuntimeConfig()

const selectedCategory = ref((route.query.category as string) || 'all')
const searchQuery = ref((route.query.q as string) || '')
const currentPage = ref(1)
const pageSize = ref(12)
const totalProducts = ref(0)
const loading = ref(true)
const error = ref<string | null>(null)
const products = ref<any[]>([])
const categories = ref<any[]>([])

// Watch for route changes
watch(() => route.query.category, (newCategory) => {
  selectedCategory.value = (newCategory as string) || 'all'
  currentPage.value = 1
  fetchProducts()
})

watch(() => route.query.q, (newQuery) => {
  searchQuery.value = (newQuery as string) || ''
  currentPage.value = 1
  fetchProducts()
})

// Watch for page changes
watch(currentPage, () => {
  fetchProducts()
})

// Build sidebar categories from API categories
const sidebarCategories = computed(() => {
  return categories.value.map(cat => ({
    key: cat.key,
    label: locale.value === 'zh' ? cat.nameZh : cat.nameEn,
    subcategories: (cat.children || []).map((sub: any) => ({
      key: sub.key,
      label: locale.value === 'zh' ? sub.nameZh : sub.nameEn,
      // 三级分类
      subcategories: (sub.children || []).map((third: any) => ({
        key: third.key,
        label: locale.value === 'zh' ? third.nameZh : third.nameEn,
      }))
    }))
  }))
})

function handleCategoryChange(category: string) {
  selectedCategory.value = category
  currentPage.value = 1
  const query: Record<string, string> = {}
  if (category !== 'all') query.category = category
  if (searchQuery.value) query.q = searchQuery.value
  navigateTo({ query })
  fetchProducts()
}

function clearFilters() {
  selectedCategory.value = 'all'
  searchQuery.value = ''
  currentPage.value = 1
  navigateTo({ query: {} })
  fetchProducts()
}

function getCategoryName(key: string): string {
  if (key === 'all') return t('products.allProducts')

  for (const cat of categories.value) {
    if (cat.key === key) {
      return locale.value === 'zh' ? cat.nameZh : cat.nameEn
    }
    for (const sub of (cat.children || [])) {
      if (sub.key === key) {
        return locale.value === 'zh' ? sub.nameZh : sub.nameEn
      }
      // 三级分类
      for (const third of (sub.children || [])) {
        if (third.key === key) {
          return locale.value === 'zh' ? third.nameZh : third.nameEn
        }
      }
    }
  }
  return key
}

async function fetchProducts() {
  loading.value = true
  error.value = null
  try {
    const categoryParam = selectedCategory.value === 'all' ? undefined : selectedCategory.value
    const keywordParam = searchQuery.value.trim() || undefined
    const url = `${config.public.apiBase}/api/products`
    const params: Record<string, any> = {
      page: currentPage.value,
      pageSize: pageSize.value,
      locale: locale.value,
    }
    if (categoryParam) {
      params.category = categoryParam
    }
    if (keywordParam) {
      params.keyword = keywordParam
    }

    const response = await fetch(url + '?' + new URLSearchParams(params))
    const result = await response.json()

    // Handle API response structure: { code, message, data: { data: [], total, page, pageSize } }
    if (result.code === 200 && result.data) {
      products.value = result.data.data || []
      totalProducts.value = result.data.total || 0
    } else {
      products.value = []
      totalProducts.value = 0
    }
  } catch (err: any) {
    console.error('Failed to fetch products:', err)
    error.value = err.message || 'Failed to load products'
    products.value = []
  } finally {
    loading.value = false
  }
}

async function fetchCategories() {
  try {
    const url = `${config.public.apiBase}/api/categories`
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

const totalPages = computed(() => Math.ceil(totalProducts.value / pageSize.value))

onMounted(() => {
  fetchCategories()
  fetchProducts()
})
</script>
