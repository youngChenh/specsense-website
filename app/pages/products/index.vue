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
            <button @click="() => fetchProducts()" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">
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
              <NuxtLink
                class="px-4 py-2 border rounded-md text-gray-600 hover:bg-gray-50"
                :class="{ 'opacity-50 pointer-events-none': currentPage === 1 }"
                :aria-disabled="currentPage === 1"
                :tabindex="currentPage === 1 ? -1 : undefined"
                :to="pageLink(Math.max(1, currentPage - 1))"
              >
                Previous
              </NuxtLink>
              <NuxtLink
                v-for="page in totalPages"
                :key="page"
                class="px-4 py-2 border rounded-md"
                :class="currentPage === page ? 'bg-blue-600 text-white' : 'text-gray-600 hover:bg-gray-50'"
                :aria-current="currentPage === page ? 'page' : undefined"
                :to="pageLink(page)"
              >
                {{ page }}
              </NuxtLink>
              <NuxtLink
                class="px-4 py-2 border rounded-md text-gray-600 hover:bg-gray-50"
                :class="{ 'opacity-50 pointer-events-none': currentPage === totalPages }"
                :aria-disabled="currentPage === totalPages"
                :tabindex="currentPage === totalPages ? -1 : undefined"
                :to="pageLink(Math.min(totalPages, currentPage + 1))"
              >
                Next
              </NuxtLink>
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
const api = useApi()

const selectedCategory = computed(() => typeof route.query.category === 'string' ? route.query.category || 'all' : 'all')
const searchQuery = computed(() => typeof route.query.q === 'string' ? route.query.q : '')
const currentPage = computed(() => {
  const page = Number(route.query.page)
  return Number.isSafeInteger(page) && page > 0 ? page : 1
})
const pageSize = 12
const productKey = computed(() => JSON.stringify(['products', selectedCategory.value, searchQuery.value.trim(), currentPage.value, locale.value]))
const { data: productPage, status, error: productError, refresh: fetchProducts } = await useAsyncData(productKey, async () => {
  const result: any = await api.fetchProducts({
    page: currentPage.value,
    pageSize,
    category: selectedCategory.value === 'all' ? undefined : selectedCategory.value,
    keyword: searchQuery.value.trim() || undefined,
    locale: locale.value,
  })
  if (result?.code !== 200 || !Array.isArray(result.data?.data)) {
    throw createError({ statusCode: 502, statusMessage: 'Failed to load products' })
  }
  return result.data
})
const { data: categories } = await useAsyncData(
  computed(() => `product-categories:${locale.value}`),
  () => api.fetchCategories(locale.value),
  { default: () => [] },
)
const products = computed<any[]>(() => productPage.value?.data || [])
const totalProducts = computed(() => productPage.value?.total || 0)
const loading = computed(() => status.value === 'pending')
const error = computed(() => productError.value ? 'Failed to load products. Please try again.' : null)
if (import.meta.server && productError.value) {
  const event = useRequestEvent()
  if (event) setResponseStatus(event, 503)
}

function pageLink(page: number) {
  return { path: route.path, query: { ...route.query, page: page > 1 ? String(page) : undefined } }
}

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
  const query: Record<string, string> = {}
  if (category !== 'all') query.category = category
  if (searchQuery.value) query.q = searchQuery.value
  navigateTo({ query })
}

function clearFilters() {
  navigateTo({ query: {} })
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

const totalPages = computed(() => Math.ceil(totalProducts.value / pageSize))
const seoTitle = computed(() => `${selectedCategory.value === 'all' ? t('products.title') : getCategoryName(selectedCategory.value)}${currentPage.value > 1 ? ` - ${currentPage.value}` : ''} | SpeSense`)
const canonical = computed(() => {
  const url = new URL('/products', config.public.siteUrl)
  if (selectedCategory.value !== 'all') url.searchParams.set('category', selectedCategory.value)
  if (searchQuery.value.trim()) url.searchParams.set('q', searchQuery.value.trim())
  if (currentPage.value > 1) url.searchParams.set('page', String(currentPage.value))
  return url.href
})
useSeoMeta({
  title: () => seoTitle.value,
  description: () => `${selectedCategory.value === 'all' ? t('products.title') : getCategoryName(selectedCategory.value)} — ${t('products.subtitle')}`,
  robots: () => searchQuery.value.trim() || productError.value ? 'noindex, follow' : 'index, follow',
})
useHead(() => ({ link: [{ rel: 'canonical', href: canonical.value }] }))
</script>
