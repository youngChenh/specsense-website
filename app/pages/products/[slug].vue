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
      <!-- Hero -->
      <section class="bg-gradient-to-br from-blue-50 to-indigo-100 py-16">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <NuxtLink :to="localePath('/products')" class="inline-flex items text-gray-600 hover:text-gray-900 mb-6">
            <svg class="w-5 h-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
            </svg>
            {{ $t('products.allProducts') }}
          </NuxtLink>
          <h1 class="text-4xl font-bold text-gray-900">{{ displayName }}</h1>
          <p class="text-xl text-gray-600 mt-2">{{ displayDescription }}</p>
        </div>
      </section>

      <!-- Product Details -->
      <section class="py-16">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div class="grid grid-cols-1 lg:grid-cols-2 gap-12">
            <!-- Images -->
            <div>
              <div class="bg-gray-100 rounded-xl overflow-hidden">
                <!-- Main Image -->
                <div class="relative">
                  <img
                    v-if="allImages.length > 0"
                    :src="getImageUrl(activeImage)"
                    :alt="displayName"
                    class="w-full h-96 object-cover"
                  />
                  <div v-else class="w-full h-96 bg-gray-200 flex items-center justify-center">
                    <svg class="w-24 h-24 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                  </div>
                </div>
                <!-- Thumbnails -->
                <div v-if="allImages.length > 1" class="flex gap-2 p-3 overflow-x-auto">
                  <button
                    v-for="(img, idx) in allImages"
                    :key="idx"
                    @click="activeImage = img"
                    class="flex-shrink-0 w-20 h-20 rounded-lg overflow-hidden border-2 transition-all"
                    :class="activeImage === img ? 'border-blue-500 ring-2 ring-blue-200' : 'border-transparent opacity-70 hover:opacity-100'"
                  >
                    <img :src="getImageUrl(img)" :alt="'Thumbnail ' + (idx + 1)" class="w-full h-full object-cover" />
                  </button>
                </div>
              </div>
            </div>

            <!-- Info -->
            <div>
              <div class="mb-8">
                <span class="inline-block px-3 py-1 bg-blue-100 text-blue-600 rounded-full text-sm font-medium mb-4">
                  {{ product.categoryName || product.categoryKey }}
                </span>
                <h2 class="text-2xl font-bold text-gray-900 mb-4">{{ displayName }}</h2>
                <p class="text-gray-600">{{ displayDescription }}</p>
              </div>

              <!-- Specifications -->
              <div v-if="product.specs && Object.keys(product.specs).length > 0" class="mb-8">
                <h3 class="text-lg font-semibold text-gray-900 mb-4">{{ $t('products.specifications') }}</h3>
                <div class="bg-gray-50 rounded-lg p-6">
                  <table class="w-full">
                    <tbody>
                      <tr v-for="(value, key) in product.specs" :key="key" class="border-b border-gray-200 last:border-0">
                        <td class="py-3 font-medium text-gray-600">{{ key }}</td>
                        <td class="py-3 text-gray-900 text-right">{{ value }}</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>

              <!-- Actions -->
              <div class="flex flex-col sm:flex-row gap-4">
                <button
                  @click="showInquiryModal = true"
                  class="flex-1 px-6 py-3 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 transition-colors"
                >
                  {{ $t('products.inquiry') }}
                </button>
                <a
                  v-if="pdfList.length > 0"
                  :href="getFullUrl(pdfList[0])"
                  download
                  class="flex-1 px-6 py-3 bg-green-600 text-white font-semibold rounded-lg hover:bg-green-700 transition-colors text-center"
                >
                  {{ $t('products.downloadPdf') || 'Download PDF' }}
                </a>
              </div>

              <!-- PDF List -->
              <div v-if="pdfList.length > 1" class="mt-4 space-y-2">
                <p class="text-sm font-medium text-gray-700">Related Downloads:</p>
                <div class="flex flex-wrap gap-2">
                  <a
                    v-for="(pdf, idx) in pdfList"
                    :key="idx"
                    :href="getFullUrl(pdf)"
                    download
                    class="inline-flex items-center gap-1 px-3 py-1.5 bg-gray-100 hover:bg-gray-200 text-gray-700 text-sm rounded-md transition-colors"
                  >
                    <svg class="w-4 h-4 text-red-500" fill="currentColor" viewBox="0 0 20 20">
                      <path fill-rule="evenodd" d="M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L15.414 6A2 2 0 0116 7.414V16a2 2 0 01-2 2H6a2 2 0 01-2-2V4z" clip-rule="evenodd" />
                    </svg>
                    PDF {{ idx + 1 }}
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- PDF Section -->
      <section v-if="pdfList.length > 0" class="pb-16">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <h3 class="text-xl font-bold text-gray-900 mb-6">Product Documentation</h3>
          <div class="space-y-6">
            <div v-for="(pdf, idx) in pdfList" :key="idx" class="bg-gray-50 rounded-xl p-4">
              <div class="flex items-center justify-between mb-3">
                <h4 class="font-medium text-gray-800">PDF {{ idx + 1 }}</h4>
                <a
                  :href="getFullUrl(pdf)"
                  download
                  class="text-sm text-green-600 hover:text-green-700 flex items-center gap-1"
                >
                  <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                  </svg>
                  Download
                </a>
              </div>
              <div class="space-y-4">
                <canvas
                  v-for="pageNum in pdfPages[idx] || 1"
                  :key="pageNum"
                  :id="`pdf-canvas-${idx}-${pageNum}`"
                  class="w-full border border-gray-200 rounded-lg bg-white"
                />
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Inquiry Modal -->
      <InquiryModal
        v-if="showInquiryModal"
        :product-name="displayName"
        @close="showInquiryModal = false"
      />
    </template>
  </div>
</template>

<script setup lang="ts">
const route = useRoute()
const { locale } = useI18n()
const api = useApi()
const localePath = useLocalePath()
const config = useRuntimeConfig()
const { getImageUrl } = useImageUrl()

const showInquiryModal = ref(false)
const activeImage = ref('')
const loading = ref(true)
const product = ref<any>(null)
const pdfPages = ref<number[]>([])
const pdfDocMap = ref<any[]>([])
// use plain variable to avoid Vue proxy wrapping pdf.js objects
const plainDocMap: any[] = []
let pdfRendering = false

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

const allImages = computed(() => {
  if (!product.value) return []
  const imgs = product.value.imageUrls || []
  if (product.value.imageUrl && !imgs.includes(product.value.imageUrl)) {
    return [product.value.imageUrl, ...imgs]
  }
  return imgs
})

const getFullUrl = (url) => {
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

async function fetchProduct() {
  loading.value = true
  const slug = route.params.slug as string
  try {
    product.value = await api.fetchProduct(slug, locale.value)
    if (product.value?.imageUrls?.length > 0) {
      activeImage.value = product.value.imageUrls[0]
    } else if (product.value?.imageUrl) {
      activeImage.value = product.value.imageUrl
    }
  } catch (error) {
    console.error('Failed to fetch product:', error)
    product.value = null
  } finally {
    loading.value = false
  }
}

const loadPdfJs = () => {
  return new Promise<void>((resolve) => {
    if ((window as any).pdfjsLib) { resolve(); return }
    const script = document.createElement('script')
    script.src = 'https://cdnjs.cloudflare.com/ajax/libs/pdf.js/3.11.174/pdf.min.js'
    script.onload = () => {
      ;(window as any).pdfjsLib.GlobalWorkerOptions.workerSrc =
        'https://cdnjs.cloudflare.com/ajax/libs/pdf.js/3.11.174/pdf.worker.min.js'
      resolve()
    }
    script.onerror = () => { console.warn('pdf.js failed to load'); resolve() }
    document.head.appendChild(script)
  })
}

const renderAllPdfs = async () => {
  if (typeof window === 'undefined') return
  if (pdfRendering) return
  pdfRendering = true
  try {
    const list = pdfList.value
    if (!list || list.length === 0) return

    await loadPdfJs()
    if (!(window as any).pdfjsLib) return

    await nextTick()
    await new Promise(r => setTimeout(r, 300))

    pdfPages.value = []
    pdfDocMap.value = []
    plainDocMap.length = 0
    for (let i = 0; i < list.length; i++) {
      pdfDocMap.value.push(null)
      plainDocMap.push(null)
      await loadPdfDoc(i)
      for (let p = 1; p <= (pdfPages.value[i] || 1); p++) {
        await renderPdfPage(i, p)
      }
    }
  } finally {
    pdfRendering = false
  }
}

const loadPdfDoc = async (idx: number) => {
  const list = pdfList.value
  if (!list || !list[idx]) return
  if (plainDocMap[idx]) return
  const url = getFullUrl(list[idx])
  try {
    const doc = await (window as any).pdfjsLib.getDocument(url).promise
    plainDocMap[idx] = doc
    pdfDocMap.value[idx] = doc.numPages
    pdfPages.value[idx] = doc.numPages
  } catch (e) {
    console.error('[PDF] failed to load doc:', e)
  }
}

const renderPdfPage = async (pdfIdx: number, pageNum: number) => {
  const doc = plainDocMap[pdfIdx]
  if (!doc) return
  const canvas = document.getElementById(`pdf-canvas-${pdfIdx}-${pageNum}`) as HTMLCanvasElement
  if (!canvas) return
  try {
    const page = await doc.getPage(pageNum)
    const viewport = page.getViewport({ scale: 1.5 })
    canvas.height = viewport.height
    canvas.width = viewport.width
    const ctx = canvas.getContext('2d')
    if (!ctx) return
    await page.render({ canvasContext: ctx, viewport }).promise
  } catch (e) {
    console.error('[PDF] render error:', e)
  }
}

watch(locale, () => {
  fetchProduct()
})

watch(pdfList, async (newList) => {
  if (!newList || newList.length === 0) return
  await nextTick()
  await new Promise(r => setTimeout(r, 200))
  renderAllPdfs()
}, { deep: true })

onMounted(() => {
  fetchProduct()
})
</script>
