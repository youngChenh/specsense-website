<template>
  <div class="bg-white">
    <!-- Loading State -->
    <div v-if="loading" class="bg-gradient-to-br from-blue-50 to-indigo-100 py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="animate-pulse">
          <div class="h-4 bg-gray-300 rounded w-32 mb-6"></div>
          <div class="h-10 bg-gray-300 rounded w-96 mb-4"></div>
          <div class="h-6 bg-gray-300 rounded w-64"></div>
        </div>
      </div>
    </div>

    <!-- Error State -->
    <div v-else-if="!product" class="min-h-screen flex items-center justify-center">
      <div class="text-center">
        <h1 class="text-2xl font-bold text-gray-900 mb-4">{{ $t('products.notFound') }}</h1>
        <NuxtLink :to="localePath('/products')" class="text-blue-600 hover:text-blue-800">
          {{ $t('products.backToList') }}
        </NuxtLink>
      </div>
    </div>

    <template v-else>
      <!-- Breadcrumb -->
      <section class="bg-gray-50 py-4">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <nav class="flex text-sm text-gray-500">
            <NuxtLink :to="localePath('/')" class="hover:text-blue-600">{{ $t('nav.home') }}</NuxtLink>
            <span class="mx-2">/</span>
            <NuxtLink :to="localePath('/products')" class="hover:text-blue-600">{{ $t('products.allProducts') }}</NuxtLink>
            <span class="mx-2">/</span>
            <span class="text-gray-900">{{ displayName }}</span>
          </nav>
        </div>
      </section>

      <!-- Product Details -->
      <section class="py-12">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <!-- Row 1: Image & Info side by side -->
          <div class="flex gap-8 items-start mb-8">
            <!-- Left Column - Image -->
            <div class="w-[60%]">
              <!-- Main Image -->
              <div
                class="bg-gray-100 rounded-xl overflow-hidden"
                @mouseenter="onCarouselHover(true)"
                @mouseleave="onCarouselHover(false)"
              >
                <div class="relative cursor-zoom-in" @click="lightboxOpen = true; lightboxIndex = carouselIndex; lightboxImage = activeImage">
                  <img
                    v-if="allImages.length > 0"
                    :src="getImageUrl(activeImage)"
                    :alt="displayName"
                    class="w-full h-80 object-contain bg-gray-50"
                  />
                  <div v-else class="w-full h-80 bg-gray-200 flex items-center justify-center">
                    <svg class="w-24 h-24 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                  </div>

                  <!-- Arrows -->
                  <button
                    v-if="allImages.length > 1"
                    @click.stop="prevCarousel"
                    class="absolute left-2 top-1/2 -translate-y-1/2 w-10 h-10 bg-white/70 hover:bg-white/90 backdrop-blur-sm rounded-full flex items-center justify-center text-gray-800 transition-colors shadow"
                  >
                    <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
                    </svg>
                  </button>
                  <button
                    v-if="allImages.length > 1"
                    @click.stop="nextCarousel"
                    class="absolute right-2 top-1/2 -translate-y-1/2 w-10 h-10 bg-white/70 hover:bg-white/90 backdrop-blur-sm rounded-full flex items-center justify-center text-gray-800 transition-colors shadow"
                  >
                    <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                    </svg>
                  </button>
                </div>
                <!-- Thumbnails -->
                <div v-if="allImages.length > 1" class="flex gap-2 p-3 overflow-x-auto">
                  <button
                    v-for="(img, idx) in allImages"
                    :key="idx"
                    @click="carouselIndex = idx"
                    class="flex-shrink-0 w-16 h-16 rounded-lg overflow-hidden border-2 transition-all"
                    :class="carouselIndex === idx ? 'border-blue-500' : 'border-transparent opacity-70 hover:opacity-100'"
                  >
                    <img :src="getImageUrl(img)" :alt="'Thumbnail ' + (idx + 1)" class="w-full h-full object-cover" />
                  </button>
                </div>
              </div>
            </div>

            <!-- Right Column - Product Info -->
            <div class="w-[40%]">
              <!-- Product Header -->
              <div class="mb-6">
                <span class="inline-block px-3 py-1 bg-blue-100 text-blue-600 rounded-full text-sm font-medium mb-3">
                  {{ product.categoryName || product.categoryKey }}
                </span>
                <h1 class="text-3xl font-bold text-gray-900 mb-3">{{ displayName }}</h1>
                <p class="text-gray-600 text-lg">{{ displayDescription }}</p>
              </div>

              <!-- Specifications -->
              <div v-if="product.specs && Object.keys(product.specs).length > 0" class="mb-8">
                <h3 class="text-lg font-semibold text-gray-900 mb-4 flex items-center gap-2">
                  <svg class="w-5 h-5 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                  </svg>
                  {{ $t('products.specifications') }}
                </h3>
                <div class="bg-gray-50 rounded-xl overflow-hidden">
                  <table class="w-full">
                    <tbody>
                      <tr v-for="(value, key) in product.specs" :key="key" class="border-b border-gray-200 last:border-0">
                        <td class="py-3 px-4 font-medium text-gray-600">{{ key }}</td>
                        <td class="py-3 px-4 text-gray-900 text-right">{{ value }}</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>

              <!-- Highlights -->
              <div v-if="displayHighlights" class="mb-8">
                <h3 class="text-lg font-semibold text-gray-900 mb-4 flex items-center gap-2">
                  <svg class="w-5 h-5 text-yellow-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z" />
                  </svg>
                  {{ $t('products.highlights') }}
                </h3>
                <div class="bg-yellow-50 rounded-xl p-5 border border-yellow-100">
                  <div class="prose prose-xs max-w-none text-gray-700 text-sm" v-html="displayHighlights"></div>
                </div>
              </div>

              <!-- Action Buttons -->
              <div class="flex flex-col sm:flex-row gap-4 pt-4 border-t border-gray-200">
                <button
                  @click="showInquiryModal = true"
                  class="flex-1 px-6 py-4 bg-blue-600 text-white font-semibold rounded-xl hover:bg-blue-700 transition-colors flex items-center justify-center gap-2"
                >
                  <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
                  </svg>
                  {{ $t('products.inquiry') }}
                </button>
                <a
                  v-if="downloadPdfUrl"
                  :href="getFullUrl(downloadPdfUrl)"
                  download
                  class="flex-1 px-6 py-4 bg-green-600 text-white font-semibold rounded-xl hover:bg-green-700 transition-colors flex items-center justify-center gap-2"
                >
                  <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                  </svg>
                  {{ $t('products.downloadPdf') }}
                </a>
              </div>
            </div>
          </div>

          <!-- Row 2: Module Overview Section -->
          <div v-if="productModules.length > 0">
            <div class="flex items-center gap-4 mb-6">
              <h3 class="text-lg font-semibold text-gray-700 whitespace-nowrap">{{ $t('products.overview') }}</h3>
              <div class="flex-1 h-px bg-gray-200"></div>
            </div>
            <!-- Module Renderer -->
            <div class="product-modules">
              <template v-for="module in productModules" :key="module.id">
                <!-- Heading Module -->
                <h2 v-if="module.type === 'heading' && module.level === 1" class="text-2xl font-bold text-gray-900 mb-4">{{ module.content }}</h2>
                <h3 v-else-if="module.type === 'heading' && module.level === 2" class="text-xl font-bold text-gray-900 mb-3">{{ module.content }}</h3>
                <h4 v-else-if="module.type === 'heading'" class="text-lg font-semibold text-gray-800 mb-2">{{ module.content }}</h4>

                <!-- Text Module -->
                <div v-else-if="module.type === 'text'" class="text-gray-700 mb-4 whitespace-pre-line">{{ module.content }}</div>

                <!-- Image Module -->
                <div v-else-if="module.type === 'image'" class="mb-4" :class="module.align === 'center' ? 'text-center' : module.align === 'right' ? 'text-right' : 'text-left'">
                  <a v-if="module.link" :href="module.link" target="_blank" rel="noopener noreferrer">
                    <img :src="getImageUrl(module.url)" :alt="module.alt || ''" class="max-w-full h-auto rounded-lg cursor-pointer hover:opacity-90 transition-opacity" />
                  </a>
                  <img v-else :src="getImageUrl(module.url)" :alt="module.alt || ''" class="max-w-full h-auto rounded-lg" />
                </div>

                <!-- Table Module -->
                <div v-else-if="module.type === 'table' && module.tableData && module.tableData.length > 0" class="mb-4 overflow-x-auto">
                  <table class="w-full border-collapse border border-gray-300">
                    <tbody>
                      <tr v-for="(row, ri) in module.tableData" :key="ri">
                        <td
                          v-for="(cell, ci) in row"
                          :key="ci"
                          class="border border-gray-300 px-4 py-2"
                          :class="ri === 0 ? 'bg-gray-100 font-semibold' : ''"
                        >{{ cell }}</td>
                      </tr>
                    </tbody>
                  </table>
                </div>

                <!-- Two Column Module -->
                <div v-else-if="module.type === 'two_column'" class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-4">
                  <div>
                    <img v-if="module.leftType === 'image' && module.leftImage" :src="getImageUrl(module.leftImage)" class="w-full h-auto rounded-lg" />
                    <div v-else-if="module.leftType === 'text'" class="text-gray-700">{{ module.leftText }}</div>
                  </div>
                  <div>
                    <img v-if="module.rightType === 'image' && module.rightImage" :src="getImageUrl(module.rightImage)" class="w-full h-auto rounded-lg" />
                    <div v-else-if="module.rightType === 'text'" class="text-gray-700">{{ module.rightText }}</div>
                  </div>
                </div>

                <!-- Downloads Module -->
                <div v-else-if="module.type === 'downloads'" class="mb-4 flex flex-wrap gap-3">
                  <a
                    v-for="(item, idx) in module.items"
                    :key="idx"
                    :href="getFullUrl(item.url)"
                    download
                    class="inline-flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                  >
                    <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                    </svg>
                    {{ item.label }}
                  </a>
                </div>
              </template>
            </div>
          </div>

          <!-- Row 3: Detail Sections (after Overview) -->
          <div class="space-y-10 mt-10">
          </div>
        </div>
      </section>

      <!-- Lightbox -->
      <Teleport to="body">
        <div
          v-if="lightboxOpen"
          class="fixed inset-0 z-[100] bg-black/90 flex items-center justify-center"
          @click="lightboxOpen = false"
          @keydown.left="prevLightbox"
          @keydown.right="nextLightbox"
          tabindex="0"
        >
          <button
            class="absolute top-4 right-4 text-white hover:text-gray-300 transition-colors z-10"
            @click="lightboxOpen = false"
          >
            <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
          <button
            v-if="allImages.length > 1"
            class="absolute left-4 top-1/2 -translate-y-1/2 w-12 h-12 bg-white/20 hover:bg-white/40 backdrop-blur-sm rounded-full flex items-center justify-center text-white transition-colors"
            @click.stop="prevLightbox"
          >
            <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
            </svg>
          </button>
          <button
            v-if="allImages.length > 1"
            class="absolute right-4 top-1/2 -translate-y-1/2 w-12 h-12 bg-white/20 hover:bg-white/40 backdrop-blur-sm rounded-full flex items-center justify-center text-white transition-colors"
            @click.stop="nextLightbox"
          >
            <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
            </svg>
          </button>
          <img
            :src="getImageUrl(lightboxImage)"
            :alt="displayName"
            class="max-w-[90vw] max-h-[90vh] object-contain"
            @click.stop
          />
          <div v-if="allImages.length > 1" class="absolute bottom-4 left-1/2 -translate-x-1/2 text-white text-sm">
            {{ lightboxIndex + 1 }} / {{ allImages.length }}
          </div>
        </div>
      </Teleport>

      <!-- Inquiry Modal -->
      <Teleport to="body">
        <InquiryModal
          v-if="showInquiryModal"
          :product-name="displayName"
          @close="showInquiryModal = false"
        />
      </Teleport>
    </template>
  </div>
</template>

<script setup lang="ts">
import InquiryModal from '~/components/InquiryModal.vue'

const route = useRoute()
const { locale } = useI18n()
const api = useApi()
const localePath = useLocalePath()
const config = useRuntimeConfig()
const { getImageUrl } = useImageUrl()

const showInquiryModal = ref(false)
const lightboxOpen = ref(false)
const lightboxImage = ref('')
const lightboxIndex = ref(0)
const carouselIndex = ref(0)
let carouselInterval: NodeJS.Timeout | null = null
let resumeTimeout: NodeJS.Timeout | null = null

const activeImage = computed(() => {
  if (allImages.value.length === 0) return ''
  return allImages.value[carouselIndex.value] || allImages.value[0]
})

function nextCarousel() {
  if (allImages.value.length === 0) return
  carouselIndex.value = (carouselIndex.value + 1) % allImages.value.length
}

function prevCarousel() {
  if (allImages.value.length === 0) return
  carouselIndex.value = (carouselIndex.value - 1 + allImages.value.length) % allImages.value.length
}

function nextLightbox() {
  if (allImages.value.length === 0) return
  lightboxIndex.value = (lightboxIndex.value + 1) % allImages.value.length
  lightboxImage.value = allImages.value[lightboxIndex.value]
}

function prevLightbox() {
  if (allImages.value.length === 0) return
  lightboxIndex.value = (lightboxIndex.value - 1 + allImages.value.length) % allImages.value.length
  lightboxImage.value = allImages.value[lightboxIndex.value]
}

function startCarouselAutoplay() {
  stopCarouselAutoplay()
  carouselInterval = setInterval(nextCarousel, 4000)
}

function stopCarouselAutoplay() {
  if (carouselInterval) {
    clearInterval(carouselInterval)
    carouselInterval = null
  }
}

function onCarouselHover(entering: boolean) {
  if (entering) {
    stopCarouselAutoplay()
    if (resumeTimeout) { clearTimeout(resumeTimeout); resumeTimeout = null }
  } else {
    resumeTimeout = setTimeout(startCarouselAutoplay, 2000)
  }
}
const loading = ref(true)
const product = ref<any>(null)

const displayName = computed(() => {
  if (!product.value) return ''
  return locale.value === 'zh'
    ? (product.value.nameZh || product.value.nameEn || '')
    : (product.value.nameEn || product.value.nameZh || '')
})

const displayDescription = computed(() => {
  if (!product.value) return ''
  return locale.value === 'zh'
    ? (product.value.descriptionZh || product.value.descriptionEn || '')
    : (product.value.descriptionEn || product.value.descriptionZh || '')
})

const displayHighlights = computed(() => {
  if (!product.value) return ''
  return (product.value.highlights || '').replace(/\n/g, '<br>')
})

const productModules = computed(() => {
  if (!product.value) return []
  const val = product.value.overviewModules
  if (Array.isArray(val)) return val
  if (typeof val === 'string' && val) {
    try { return JSON.parse(val) } catch { return [] }
  }
  return []
})

const downloadPdfUrl = computed(() => product.value?.downloadPdfUrl || '')

const allImages = computed(() => {
  if (!product.value) return []
  const imgs = product.value.imageUrls || []
  if (product.value.imageUrl && !imgs.includes(product.value.imageUrl)) {
    return [product.value.imageUrl, ...imgs]
  }
  return imgs
})

const getFullUrl = (url: string) => {
  if (!url) return ''
  const strUrl = String(url)
  if (strUrl.startsWith('http://') || strUrl.startsWith('https://')) return strUrl
  const base = config.public.apiBase?.replace('/api', '') || ''
  return `${base}${strUrl}`
}

async function fetchProduct() {
  loading.value = true
  const slug = route.params.slug as string
  try {
    product.value = await api.fetchProduct(slug, locale.value)
    carouselIndex.value = 0
    if (allImages.value.length > 1) {
      startCarouselAutoplay()
    }
  } catch (error) {
    console.error('Failed to fetch product:', error)
    product.value = null
  } finally {
    loading.value = false
  }
}

watch(locale, () => {
  stopCarouselAutoplay()
  if (resumeTimeout) { clearTimeout(resumeTimeout); resumeTimeout = null }
  fetchProduct()
})

watch(lightboxOpen, (open) => {
  if (open) {
    document.addEventListener('keydown', handleLightboxKeydown)
  } else {
    document.removeEventListener('keydown', handleLightboxKeydown)
  }
})

function handleLightboxKeydown(e: KeyboardEvent) {
  if (e.key === 'ArrowLeft') prevLightbox()
  if (e.key === 'ArrowRight') nextLightbox()
  if (e.key === 'Escape') lightboxOpen = false
}

onMounted(() => {
  fetchProduct()
})

onUnmounted(() => {
  stopCarouselAutoplay()
  if (resumeTimeout) { clearTimeout(resumeTimeout); resumeTimeout = null }
  document.removeEventListener('keydown', handleLightboxKeydown)
})
</script>
