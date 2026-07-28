<template>
  <div>
    <!-- Hero Carousel Section -->
    <section class="relative bg-gray-100">
      <!-- Carousel -->
      <div
        class="relative h-[500px] lg:h-[600px] overflow-hidden"
        @mouseenter="onCarouselHover(true)"
        @mouseleave="onCarouselHover(false)"
      >
        <div
          v-for="(slide, index) in carouselSlides"
          :key="index"
          class="absolute inset-0 transition-opacity duration-1000"
          :class="currentSlide === index ? 'opacity-100' : 'opacity-0'"
        >
          <img
            :src="slide.image"
            :alt="slide.title"
            class="w-full h-full object-cover"
          />
          <div class="absolute inset-0 bg-gradient-to-t from-black/20 via-black/10 to-transparent"></div>
          <div class="absolute inset-0 flex flex-col items-center justify-end pb-24">
            <div class="text-center px-4">
              <h1 class="text-3xl md:text-5xl font-bold text-white mb-4 drop-shadow-lg">
                {{ slide.title }}
              </h1>
              <p class="text-lg md:text-xl text-white/90 max-w-2xl mx-auto drop-shadow-md">
                {{ slide.subtitle }}
              </p>
            </div>
          </div>
        </div>

        <!-- Carousel Indicators -->
        <div class="absolute bottom-4 left-1/2 transform -translate-x-1/2 flex gap-2 z-10">
          <button
            v-for="(_, index) in carouselSlides"
            :key="index"
            @click="currentSlide = index"
            class="w-3 h-3 rounded-full transition-all duration-300"
            :class="currentSlide === index ? 'bg-white w-8' : 'bg-white/50 hover:bg-white/80'"
          ></button>
        </div>

        <!-- Carousel Arrows -->
        <button
          @click="prevSlide"
          class="absolute left-4 top-1/2 -translate-y-1/2 w-12 h-12 bg-white/20 hover:bg-white/40 backdrop-blur-sm rounded-full flex items-center justify-center text-white transition-colors z-10"
        >
          <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
          </svg>
        </button>
        <button
          @click="nextSlide"
          class="absolute right-4 top-1/2 -translate-y-1/2 w-12 h-12 bg-white/20 hover:bg-white/40 backdrop-blur-sm rounded-full flex items-center justify-center text-white transition-colors z-10"
        >
          <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
          </svg>
        </button>
      </div>

      <!-- CTA Buttons below Carousel -->
      <div class="bg-gradient-to-br from-blue-50 to-indigo-100 py-12">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div class="flex flex-col sm:flex-row gap-4 justify-center">
            <NuxtLink
              :to="localePath('/products')"
              class="inline-flex items-center justify-center px-8 py-4 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 transition-colors shadow-lg"
            >
              {{ $t('home.hero.cta') }}
              <svg class="ml-2 w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8l4 4m0 0l-4 4m4-4H3" />
              </svg>
            </NuxtLink>
            <NuxtLink
              :to="localePath('/contact')"
              class="inline-flex items-center justify-center px-8 py-4 bg-white text-blue-600 font-semibold rounded-lg hover:bg-gray-50 transition-colors shadow-lg border border-gray-200"
            >
              {{ $t('home.cta.button') }}
            </NuxtLink>
          </div>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="py-20 bg-white">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h2 class="text-3xl font-bold text-center text-gray-900 mb-12">
          {{ $t('home.features.title') }}
        </h2>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div class="text-center p-8 rounded-xl bg-gray-50 hover:shadow-lg transition-shadow">
            <div class="w-20 h-20 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-6">
              <svg class="w-10 h-10 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <h3 class="text-xl font-semibold mb-3">{{ $t('home.features.precision.title') }}</h3>
            <p class="text-gray-600">{{ $t('home.features.precision.desc') }}</p>
          </div>
          <div class="text-center p-8 rounded-xl bg-gray-50 hover:shadow-lg transition-shadow">
            <div class="w-20 h-20 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-6">
              <svg class="w-10 h-10 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
              </svg>
            </div>
            <h3 class="text-xl font-semibold mb-3">{{ $t('home.features.reliability.title') }}</h3>
            <p class="text-gray-600">{{ $t('home.features.reliability.desc') }}</p>
          </div>
          <div class="text-center p-8 rounded-xl bg-gray-50 hover:shadow-lg transition-shadow">
            <div class="w-20 h-20 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-6">
              <svg class="w-10 h-10 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <h3 class="text-xl font-semibold mb-3">{{ $t('home.features.support.title') }}</h3>
            <p class="text-gray-600">{{ $t('home.features.support.desc') }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Products Preview -->
    <section class="py-20 bg-gray-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center mb-12">
          <h2 class="text-3xl font-bold text-gray-900 mb-4">{{ $t('home.products.title') }}</h2>
          <p class="text-gray-600 max-w-2xl mx-auto">{{ $t('products.subtitle') }}</p>
        </div>
        <div v-if="loadingProducts" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <div v-for="i in 4" :key="i" class="bg-white rounded-xl shadow-sm p-4 animate-pulse">
            <div class="h-48 bg-gray-200 rounded-md mb-4"></div>
            <div class="h-4 bg-gray-200 rounded w-3/4 mb-2"></div>
            <div class="h-4 bg-gray-200 rounded w-1/2"></div>
          </div>
        </div>
        <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <ProductCard
            v-for="product in featuredProducts"
            :key="product.slug || product.id"
            :product="product"
          />
        </div>
        <div class="text-center mt-12">
          <NuxtLink
            :to="localePath('/products')"
            class="inline-flex items-center px-8 py-3 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 transition-colors"
          >
            {{ $t('home.products.viewAll') }}
            <svg class="ml-2 w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8l4 4m0 0l-4 4m4-4H3" />
            </svg>
          </NuxtLink>
        </div>
      </div>
    </section>

    <!-- Applications Preview -->
    <section class="py-20 bg-white">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center mb-12">
          <h2 class="text-3xl font-bold text-gray-900 mb-4">{{ $t('home.applicationsPreview.title') }}</h2>
          <p class="text-gray-600 max-w-2xl mx-auto">{{ $t('home.applicationsPreview.desc') }}</p>
        </div>
        <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-6">
          <NuxtLink
            v-for="app in applicationsData"
            :key="app.id"
            :to="localePath(`/applications?type=${app.type}`)"
            class="group relative overflow-hidden rounded-2xl aspect-square"
          >
            <img
              v-if="app.imageUrl"
              :src="getImageUrl(app.imageUrl)"
              :alt="app.title"
              class="absolute inset-0 w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
            />
            <div v-else class="absolute inset-0 bg-gradient-to-br from-blue-500 to-indigo-600"></div>
            <div class="absolute inset-0 bg-gradient-to-t from-black/70 via-black/20 to-transparent"></div>
            <div class="absolute inset-0 flex flex-col items-center justify-end p-4">
              <component :is="getIconComponent(app.type)" class="w-8 h-8 text-white mb-2 opacity-90" />
              <h3 class="text-white font-semibold text-center text-sm">{{ app.title }}</h3>
            </div>
          </NuxtLink>
        </div>

        <!-- Featured Products for Applications -->
        <div v-if="applicationProducts.length > 0" class="mt-16">
          <h3 class="text-2xl font-bold text-center text-gray-900 mb-8">{{ $t('home.products.featured') }}</h3>
          <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
            <ProductCard
              v-for="product in applicationProducts"
              :key="product.slug || product.id"
              :product="product"
            />
          </div>
          <div class="text-center mt-8">
            <NuxtLink
              :to="localePath('/products')"
              class="inline-flex items-center px-6 py-3 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 transition-colors"
            >
              {{ $t('home.products.viewAll') }}
              <svg class="ml-2 w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8l4 4m0 0l-4 4m4-4H3" />
              </svg>
            </NuxtLink>
          </div>
        </div>

        <div class="text-center mt-12">
          <NuxtLink
            :to="localePath('/applications')"
            class="inline-flex items-center text-blue-600 font-semibold hover:text-blue-800"
          >
            {{ $t('common.viewAll') }}
            <svg class="ml-2 w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8l4 4m0 0l-4 4m4-4H3" />
            </svg>
          </NuxtLink>
        </div>
      </div>
    </section>

    <!-- About Preview -->
    <section class="py-20 bg-gray-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
          <div>
            <h2 class="text-3xl font-bold text-gray-900 mb-6">{{ $t('home.about.title') }}</h2>
            <p class="text-lg text-gray-600 mb-8">
              {{ $t('home.about.desc') }}
            </p>
            <div class="grid grid-cols-2 gap-6 mb-8">
              <div class="text-center p-4 bg-white rounded-lg shadow-sm">
                <div class="text-4xl font-bold text-blue-600">18+</div>
                <div class="text-gray-600 mt-1">Years Experience</div>
              </div>
              <div class="text-center p-4 bg-white rounded-lg shadow-sm">
                <div class="text-4xl font-bold text-blue-600">100+</div>
                <div class="text-gray-600 mt-1">Countries Served</div>
              </div>
            </div>
            <NuxtLink
              :to="localePath('/about')"
              class="inline-flex items-center px-6 py-3 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 transition-colors"
            >
              {{ $t('home.about.learnMore') }}
            </NuxtLink>
          </div>
          <div class="bg-gradient-to-br from-blue-100 to-indigo-100 rounded-2xl p-8 lg:p-12">
            <div class="grid grid-cols-2 gap-6 text-center">
              <div>
                <div class="text-4xl font-bold text-blue-600">200+</div>
                <div class="text-gray-600 mt-2">Products</div>
              </div>
              <div>
                <div class="text-4xl font-bold text-blue-600">1000+</div>
                <div class="text-gray-600 mt-2">Customers</div>
              </div>
              <div>
                <div class="text-4xl font-bold text-blue-600">50+</div>
                <div class="text-gray-600 mt-2">R&D Engineers</div>
              </div>
              <div>
                <div class="text-4xl font-bold text-blue-600">ISO</div>
                <div class="text-gray-600 mt-2">Certified</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- News Preview -->
    <section class="py-20 bg-white">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center mb-12">
          <div>
            <h2 class="text-3xl font-bold text-gray-900 mb-2">{{ $t('home.newsPreview.title') }}</h2>
          </div>
          <NuxtLink
            :to="localePath('/news')"
            class="inline-flex items-center text-blue-600 font-semibold hover:text-blue-800"
          >
            {{ $t('home.newsPreview.viewAll') }}
            <svg class="ml-2 w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8l4 4m0 0l-4 4m4-4H3" />
            </svg>
          </NuxtLink>
        </div>
        <div v-if="loadingNews" class="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div v-for="i in 3" :key="i" class="animate-pulse">
            <div class="rounded-xl overflow-hidden mb-4">
              <div class="w-full h-48 bg-gray-200"></div>
            </div>
            <div class="h-4 bg-gray-200 rounded w-1/4 mb-2"></div>
            <div class="h-6 bg-gray-200 rounded w-3/4"></div>
          </div>
        </div>
        <div v-else class="grid grid-cols-1 md:grid-cols-3 gap-8">
          <NuxtLink
            v-for="item in latestNews"
            :key="item.slug || item.id"
            :to="localePath(`/news/${item.slug}`)"
            class="group"
          >
            <div class="rounded-xl overflow-hidden mb-4">
              <img
                :src="getImageUrl(item.imageUrl || item.image)"
                :alt="getNewsTitle(item)"
                class="w-full h-48 object-cover group-hover:scale-105 transition-transform duration-300"
              />
            </div>
            <span class="text-sm text-gray-500">{{ item.publishedAt || item.date }}</span>
            <h3 class="text-lg font-semibold text-gray-900 group-hover:text-blue-600 transition-colors mt-1 line-clamp-2">
              {{ getNewsTitle(item) }}
            </h3>
          </NuxtLink>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="py-20 bg-blue-600">
      <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h2 class="text-3xl font-bold text-white mb-6">{{ $t('home.cta.title') }}</h2>
        <p class="text-xl text-blue-100 mb-8">
          {{ $t('home.cta.desc') }}
        </p>
        <NuxtLink
          :to="localePath('/contact')"
          class="inline-flex items-center px-8 py-4 bg-white text-blue-600 font-semibold rounded-lg hover:bg-gray-100 transition-colors"
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
const api = useApi()
const { getImageUrl } = useImageUrl()

const featuredProducts = ref<any[]>([])
const latestNews = ref<any[]>([])
const applicationsData = ref<any[]>([])
const applicationProducts = ref<any[]>([])
const loadingProducts = ref(true)
const loadingNews = ref(true)

// Carousel data
const carouselSlides = ref<any[]>([])
const currentSlide = ref(0)
let autoPlayInterval: NodeJS.Timeout | null = null
let resumeTimeout: NodeJS.Timeout | null = null
const isHovering = ref(false)

async function fetchBanners() {
  try {
    const response = await fetch(`${config.public.apiBase}/api/banners?locale=${locale.value}`)
    const result = await response.json()
    if (result.code === 200 && result.data) {
      carouselSlides.value = result.data.map((banner: any) => ({
        image: getImageUrl(banner.imageUrl),
        title: banner.title,
        subtitle: banner.subtitle,
        linkUrl: banner.linkUrl
      }))
    }
  } catch (error) {
    console.error('Failed to fetch banners:', error)
  }
}

function nextSlide() {
  if (carouselSlides.value.length === 0) return
  currentSlide.value = (currentSlide.value + 1) % carouselSlides.value.length
}

function prevSlide() {
  if (carouselSlides.value.length === 0) return
  currentSlide.value = (currentSlide.value - 1 + carouselSlides.value.length) % carouselSlides.value.length
}

function startAutoPlay() {
  autoPlayInterval = setInterval(() => {
    currentSlide.value = (currentSlide.value + 1) % carouselSlides.value.length
  }, 3000)
}

function stopAutoPlay() {
  if (autoPlayInterval) {
    clearInterval(autoPlayInterval)
    autoPlayInterval = null
  }
}

function onCarouselHover(hovering: boolean) {
  isHovering.value = hovering
  if (hovering) {
    // Mouse enter: stop auto-play immediately
    stopAutoPlay()
    // Clear any pending resume
    if (resumeTimeout) {
      clearTimeout(resumeTimeout)
      resumeTimeout = null
    }
  } else {
    // Mouse leave: wait 3 seconds then resume
    resumeTimeout = setTimeout(() => {
      startAutoPlay()
    }, 2000)
  }
}

function getNewsTitle(item: any): string {
  return locale.value === 'zh' ? (item.titleZh || item.titleEn || '') : (item.titleEn || item.titleZh || '')
}

async function fetchFeaturedProducts() {
  loadingProducts.value = true
  try {
    const response = await fetch(`${config.public.apiBase}/api/products/featured?limit=4&locale=${locale.value}`)
    const result = await response.json()
    featuredProducts.value = result.data || []
  } catch (error) {
    console.error('Failed to fetch featured products:', error)
    featuredProducts.value = []
  } finally {
    loadingProducts.value = false
  }
}

async function fetchLatestNews() {
  loadingNews.value = true
  try {
    const data = await api.fetchLatestNews(3, locale.value)
    latestNews.value = data || []
  } catch (error) {
    console.error('Failed to fetch latest news:', error)
    latestNews.value = []
  } finally {
    loadingNews.value = false
  }
}

async function fetchApplications() {
  try {
    const response = await fetch(`${config.public.apiBase}/api/applications?locale=${locale.value}`)
    const result = await response.json()
    if (result.code === 200 && result.data) {
      applicationsData.value = result.data
    }
  } catch (error) {
    console.error('Failed to fetch applications:', error)
  }
}

async function fetchApplicationProducts() {
  try {
    const response = await fetch(`${config.public.apiBase}/api/products?featured=true&locale=${locale.value}&page=1&pageSize=4`)
    const result = await response.json()
    if (result.code === 200 && result.data) {
      applicationProducts.value = result.data.data || []
    }
  } catch (error) {
    console.error('Failed to fetch application products:', error)
  }
}

const getIconComponent = (type: string) => {
  const icons: Record<string, any> = {
    industrial: IndustrialIcon,
    research: ResearchIcon,
    medical: MedicalIcon,
    environment: EnvironmentIcon,
    agriculture: AgricultureIcon,
    food: FoodIcon,
  }
  return icons[type] || IndustrialIcon
}

watch(locale, () => {
  fetchBanners()
  fetchFeaturedProducts()
  fetchLatestNews()
  fetchApplications()
  fetchApplicationProducts()
})

onMounted(() => {
  fetchBanners()
  fetchFeaturedProducts()
  fetchLatestNews()
  fetchApplications()
  fetchApplicationProducts()
  startAutoPlay()
})

onUnmounted(() => {
  stopAutoPlay()
  if (resumeTimeout) {
    clearTimeout(resumeTimeout)
    resumeTimeout = null
  }
})

// Icon components
const IndustrialIcon = {
  template: `<svg fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" /></svg>`
}
const ResearchIcon = {
  template: `<svg fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" /></svg>`
}
const MedicalIcon = {
  template: `<svg fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" /></svg>`
}
const EnvironmentIcon = {
  template: `<svg fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>`
}
const AgricultureIcon = {
  template: `<svg fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>`
}
const FoodIcon = {
  template: `<svg fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" /></svg>`
}
</script>
