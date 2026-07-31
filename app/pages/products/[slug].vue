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
        <h1 class="text-2xl font-bold text-gray-900 mb-4">Product Not Found</h1>
        <NuxtLink :to="localePath('/products')" class="text-blue-600 hover:text-blue-800">
          Back to Products
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

      <!-- Product Details - Two Column Layout -->
      <section class="py-12">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div class="flex gap-8">
            <!-- Left Column - Image & PDF (bigger, natural flow) -->
            <div class="w-[60%]">
              <!-- Main Image -->
              <div
                class="bg-gray-100 rounded-xl overflow-hidden mb-6"
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

              <!-- PDF Section -->
              <div v-if="pdfList.length > 0" class="mb-4">
                <!-- PDF Canvas Preview -->
                <div ref="pdfContainerRef" class="flex flex-col items-center">
                  <div v-if="pdfLoading" class="flex items-center justify-center h-64">
                    <span class="text-gray-500">Loading PDF...</span>
                  </div>
                  <div v-else-if="pdfError" class="flex items-center justify-center h-64">
                    <span class="text-red-500">Failed to load PDF</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Right Column - Product Info (fixed, does not scroll) -->
            <div class="w-[40%] sticky top-0 h-fit overflow-y-auto" style="max-height: calc(100vh - 100px);">
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
                  Product Highlights
                </h3>
                <div class="bg-yellow-50 rounded-xl p-5 border border-yellow-100">
                  <div class="prose prose-xs max-w-none text-gray-700 text-sm" v-html="displayHighlights"></div>
                </div>
              </div>

              <!-- Applications -->
              <div v-if="displayApplications" class="mb-8">
                <h3 class="text-lg font-semibold text-gray-900 mb-4 flex items-center gap-2">
                  <svg class="w-5 h-5 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 13.255A23.985 23.985 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                  </svg>
                  Application scope
                </h3>
                <div class="bg-green-50 rounded-xl p-5 border border-green-100">
                  <div class="prose prose-xs max-w-none text-gray-700 text-sm" v-html="displayApplications"></div>
                </div>
              </div>

              <!-- Action Buttons -->
              <div class="flex flex-col sm:flex-row gap-4 pt-4 border-t border-gray-200 relative z-[60]" style="pointer-events: auto;">
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
                  v-if="pdfList.length > 0"
                  :href="getFullUrl(pdfList[0])"
                  download
                  class="flex-1 px-6 py-4 bg-green-600 text-white font-semibold rounded-xl hover:bg-green-700 transition-colors flex items-center justify-center gap-2"
                >
                  <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                  </svg>
                  {{ $t('products.downloadPdf') || 'Download PDF' }}
                </a>
              </div>
            </div>
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

// Load PDF.js from CDN
useHead({
  script: [
    {
      src: 'https://cdnjs.cloudflare.com/ajax/libs/pdf.js/3.11.174/pdf.min.js',
      async: true,
    },
  ],
})

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
const pdfLoading = ref(false)
const pdfError = ref(false)
const pdfPageCount = ref(0)
const pdfContainerRef = ref<HTMLDivElement | null>(null)

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

const displayApplications = computed(() => {
  if (!product.value) return ''
  return (product.value.applications || '').replace(/\n/g, '<br>')
})

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

const pdfList = computed(() => {
  if (!product.value) return []
  const val = product.value.pdfUrls
  if (Array.isArray(val)) return val
  if (typeof val === 'string' && val) {
    try { return JSON.parse(val) } catch { return [] }
  }
  return []
})

async function waitForPdfJs(): Promise<boolean> {
  // @ts-ignore
  const pdfjs = window.pdfjsLib
  if (pdfjs) return true

  return new Promise((resolve) => {
    const checkInterval = setInterval(() => {
      // @ts-ignore
      if (window.pdfjsLib) {
        clearInterval(checkInterval)
        resolve(true)
      }
    }, 100)

    // Timeout after 10 seconds
    setTimeout(() => {
      clearInterval(checkInterval)
      resolve(false)
    }, 10000)
  })
}

async function renderPdf(url: string) {
  pdfLoading.value = true
  pdfError.value = false
  try {
    const loaded = await waitForPdfJs()
    if (!loaded) {
      pdfError.value = true
      return
    }

    const pdfUrl = getFullUrl(url)
    console.log('Loading PDF from:', pdfUrl)
    // @ts-ignore
    const pdfjs = window.pdfjsLib
    pdfjs.GlobalWorkerOptions.workerSrc = 'https://cdnjs.cloudflare.com/ajax/libs/pdf.js/3.11.174/pdf.worker.min.js'
    const loadingTask = pdfjs.getDocument(pdfUrl)
    const pdf = await loadingTask.promise
    console.log('PDF loaded, pages:', pdf.numPages)
    pdfPageCount.value = pdf.numPages

    // Wait for next tick to ensure container is rendered
    await nextTick()
    await nextTick()

    if (!pdfContainerRef.value) {
      console.error('Container ref not found')
      pdfError.value = true
      return
    }

    // Clear previous canvases (keep loading/error divs)
    const existingCanvases = pdfContainerRef.value.querySelectorAll('canvas')
    existingCanvases.forEach(c => c.remove())

    const scale = 4.0
    for (let i = 1; i <= pdf.numPages; i++) {
      const page = await pdf.getPage(i)
      const viewport = page.getViewport({ scale })

      // Create canvas
      const canvas = document.createElement('canvas')
      canvas.width = viewport.width
      canvas.height = viewport.height
      canvas.className = 'w-full mb-2 block'
      pdfContainerRef.value.appendChild(canvas)

      const context = canvas.getContext('2d')
      if (!context) continue
      await page.render({ canvasContext: context, viewport }).promise
      console.log(`Page ${i} rendered`)
    }
  } catch (e) {
    console.error('PDF render error:', e)
    pdfError.value = true
  } finally {
    pdfLoading.value = false
  }
}

async function fetchProduct() {
  loading.value = true
  pdfPageCount.value = 0
  // Clear previous PDF canvases
  if (pdfContainerRef.value) {
    pdfContainerRef.value.querySelectorAll('canvas').forEach(c => c.remove())
  }
  const slug = route.params.slug as string
  try {
    product.value = await api.fetchProduct(slug, locale.value)
    carouselIndex.value = 0
    if (allImages.value.length > 1) {
      startCarouselAutoplay()
    }
    // Render PDF if available
    if (product.value?.pdfUrls?.length > 0) {
      await nextTick()
      renderPdf(product.value.pdfUrls[0])
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
