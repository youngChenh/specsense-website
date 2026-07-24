<template>
  <div class="bg-gray-50">
    <!-- Hero Section -->
    <section class="bg-gradient-to-br from-blue-50 to-indigo-100 py-20">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h1 class="text-4xl font-bold text-gray-900 mb-4">{{ $t('applications.title') }}</h1>
        <p class="text-xl text-gray-600 max-w-3xl mx-auto">{{ $t('applications.subtitle') }}</p>
      </div>
    </section>

    <!-- Applications Grid -->
    <section ref="applicationsSection" class="py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div v-if="isLoading" class="text-center py-12">
          <p class="text-gray-500">Loading...</p>
        </div>
        <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          <div
            v-for="app in applications"
            :key="app.id"
            :ref="el => { if (el) appRefs[app.type] = el }"
            :id="`app-${app.type}`"
            class="bg-white rounded-xl shadow-md overflow-hidden hover:shadow-xl transition-shadow duration-300"
            :class="{ 'ring-4 ring-blue-500': activeType === app.type }"
          >
            <div class="h-48 bg-gradient-to-br from-blue-100 to-indigo-100 flex items-center justify-center overflow-hidden">
              <img v-if="app.imageUrl" :src="getFullImageUrl(app.imageUrl)" class="w-full h-full object-cover" />
              <div v-else class="w-20 h-20 bg-white rounded-full flex items-center justify-center shadow-lg">
                <component :is="getIconComponent(app.type)" class="w-10 h-10 text-blue-600" />
              </div>
            </div>
            <div class="p-6">
              <h3 class="text-xl font-semibold text-gray-900 mb-2">{{ app.title }}</h3>
              <p class="text-gray-600 mb-4">{{ app.description }}</p>
              <NuxtLink
                :to="localePath(`/products?category=${app.linkedCategoryKeys?.split(',')[0] || ''}`)"
                class="inline-flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors text-sm font-medium"
              >
                {{ $t('common.viewAll') }}
                <svg class="w-4 h-4 ml-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                </svg>
              </NuxtLink>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Featured Products for Each Application -->
    <section v-if="featuredProducts.length > 0" class="py-16 bg-white">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h2 class="text-3xl font-bold text-center text-gray-900 mb-12">{{ $t('products.featuredProducts') }}</h2>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
          <ProductCard
            v-for="product in featuredProducts"
            :key="product.slug"
            :product="product"
          />
        </div>
        <div class="text-center mt-12">
          <NuxtLink
            :to="localePath('/products')"
            class="inline-flex items-center px-8 py-3 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 transition-colors"
          >
            {{ $t('common.viewAll') }}
            <svg class="w-5 h-5 ml-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8l4 4m0 0l-4 4m4-4H3" />
            </svg>
          </NuxtLink>
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
const route = useRoute()
const localePath = useLocalePath()
const config = useRuntimeConfig()
const { locale } = useI18n()

const activeType = ref((route.query.type as string) || '')
const appRefs = ref<Record<string, any>>({})
const isLoading = ref(true)

const applications = ref<any[]>([])
const featuredProducts = ref<any[]>([])

const fetchApplications = async () => {
  try {
    const response = await fetch(`${config.public.apiBase}/api/applications?locale=${locale.value}`)
    const result = await response.json()
    if (result.code === 200 && result.data) {
      applications.value = result.data
    }
  } catch (error) {
    console.error('Failed to fetch applications:', error)
  } finally {
    isLoading.value = false
  }
}

const fetchFeaturedProducts = async () => {
  try {
    const response = await fetch(`${config.public.apiBase}/api/products?featured=true&locale=${locale.value}&page=1&pageSize=6`)
    const result = await response.json()
    if (result.code === 200 && result.data) {
      featuredProducts.value = result.data
    }
  } catch (error) {
    console.error('Failed to fetch featured products:', error)
  }
}

const getFullImageUrl = (url: string) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return `${config.public.apiBase}${url}`
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

// Scroll to active type on mount
onMounted(() => {
  fetchApplications()
  fetchFeaturedProducts()

  if (activeType.value && appRefs.value[activeType.value]) {
    appRefs.value[activeType.value].scrollIntoView({ behavior: 'smooth', block: 'center' })
  }
})

watch(locale, () => {
  fetchApplications()
  fetchFeaturedProducts()
})
</script>
