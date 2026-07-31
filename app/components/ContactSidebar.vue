<template>
  <div class="fixed right-4 top-1/2 -translate-y-1/2 z-40 flex flex-col gap-3" :style="{ marginTop: '145px' }">
    <!-- Contact Us -->
    <div class="relative">
      <button
        @click="toggleContact"
        class="w-12 h-12 bg-blue-600 hover:bg-blue-700 text-white rounded-full shadow-lg flex items-center justify-center transition-all hover:scale-110"
        :title="$t('nav.contact')"
      >
        <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
        </svg>
      </button>
      <!-- Contact Dropdown -->
      <div
        v-if="showContact"
        class="absolute right-full mr-3 top-1/2 -translate-y-1/2 bg-white rounded-xl shadow-xl p-5 w-72 border border-gray-100"
      >
        <h3 class="text-lg font-bold text-gray-900 mb-4">{{ $t('contact.title') }}</h3>
        <div class="space-y-4">
          <div>
            <p class="text-sm text-gray-500 mb-1">{{ $t('contact.info.email') }}</p>
            <a :href="`mailto:${contactEmail}`" class="text-blue-600 hover:text-blue-700 font-medium">{{ contactEmail }}</a>
          </div>
          <div>
            <p class="text-sm text-gray-500 mb-1">{{ $t('contact.info.phone') }}</p>
            <a :href="`tel:${contactPhone}`" class="text-blue-600 hover:text-blue-700 font-medium">{{ contactPhone }}</a>
          </div>
          <div>
            <p class="text-sm text-gray-500 mb-1">{{ $t('contact.info.address') }}</p>
            <p class="text-gray-700 text-sm">{{ contactAddress }}</p>
          </div>
        </div>
        <button
          @click="openInquiryModal = true; showContact = false"
          class="mt-4 w-full px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors text-sm font-medium"
        >
          {{ $t('products.inquiry') }}
        </button>
      </div>
    </div>

    <!-- WhatsApp -->
    <div class="relative">
      <button
        @click="toggleWhatsApp"
        class="w-12 h-12 bg-green-500 hover:bg-green-600 text-white rounded-full shadow-lg flex items-center justify-center transition-all hover:scale-110"
        title="WhatsApp"
      >
        <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 24 24">
          <path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347m-5.421 7.403h-.004a9.87 9.87 0 01-5.031-1.378l-.361-.214-3.741.982.998-3.648-.235-.374a9.86 9.86 0 01-1.51-5.26c.001-5.45 4.436-9.884 9.888-9.884 2.64 0 5.122 1.03 6.988 2.898a9.825 9.825 0 012.893 6.994c-.003 5.45-4.437 9.884-9.885 9.884m8.413-18.297A11.815 11.815 0 0012.05 0C5.495 0 .16 5.335.157 11.892c0 2.096.547 4.142 1.588 5.945L.057 24l6.305-1.654a11.882 11.882 0 005.683 1.448h.005c6.554 0 11.89-5.335 11.893-11.893a11.821 11.821 0 00-3.48-8.413z"/>
        </svg>
      </button>
      <!-- WhatsApp Dropdown -->
      <div
        v-if="showWhatsApp"
        class="absolute right-full mr-3 top-1/2 -translate-y-1/2 bg-white rounded-xl shadow-xl p-5 w-72 border border-gray-100"
      >
        <h3 class="text-lg font-bold text-gray-900 mb-4">WhatsApp</h3>
        <div class="space-y-3">
          <div>
            <p class="text-sm text-gray-500 mb-1">{{ $t('contact.info.phone') }}</p>
            <a :href="`https://wa.me/${whatsappNumber.replace(/[^0-9]/g, '')}`" target="_blank" class="text-green-600 hover:text-green-700 font-medium block">{{ whatsappNumber }}</a>
          </div>
          <a
            :href="`https://wa.me/${whatsappNumber.replace(/[^0-9]/g, '')}`"
            target="_blank"
            class="mt-2 inline-flex items-center gap-2 px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 transition-colors text-sm font-medium"
          >
            <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
              <path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347m-5.421 7.403h-.004a9.87 9.87 0 01-5.031-1.378l-.361-.214-3.741.982.998-3.648-.235-.374a9.86 9.86 0 01-1.51-5.26c.001-5.45 4.436-9.884 9.888-9.884 2.64 0 5.122 1.03 6.988 2.898a9.825 9.825 0 012.893 6.994c-.003 5.45-4.437 9.884-9.885 9.884m8.413-18.297A11.815 11.815 0 0012.05 0C5.495 0 .16 5.335.157 11.892c0 2.096.547 4.142 1.588 5.945L.057 24l6.305-1.654a11.882 11.882 0 005.683 1.448h.005c6.554 0 11.89-5.335 11.893-11.893a11.821 11.821 0 00-3.48-8.413z"/>
            </svg>
            Chat on WhatsApp
          </a>
        </div>
      </div>
    </div>

    <!-- WeChat -->
    <div class="relative">
      <button
        @click="toggleWeChat"
        class="w-12 h-12 bg-green-600 hover:bg-green-700 text-white rounded-full shadow-lg flex items-center justify-center transition-all hover:scale-110"
        title="WeChat"
      >
        <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 24 24">
          <path d="M8.691 2.188C3.891 2.188 0 5.476 0 9.53c0 2.212 1.17 4.203 3.002 5.55a.59.59 0 01.213.665l-.39 1.48c-.019.07-.048.141-.048.213 0 .163.13.295.29.295a.326.326 0 00.167-.054l1.903-1.114a.864.864 0 01.717-.098 10.16 10.16 0 002.837.403c.276 0 .543-.027.811-.05-.857-2.578.157-4.972 1.932-6.446 1.703-1.415 3.882-1.98 5.853-1.838-.576-3.583-4.196-6.348-8.596-6.348zM5.785 5.991c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 01-1.162 1.178A1.17 1.17 0 014.623 7.17c0-.651.52-1.18 1.162-1.18zm5.813 0c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 01-1.162 1.178 1.17 1.17 0 01-1.162-1.178c0-.651.52-1.18 1.162-1.18zm5.34 2.867c-1.797-.052-3.746.512-5.28 1.786-1.72 1.428-2.687 3.72-1.78 6.22.942 2.453 3.666 4.229 6.884 4.229.826 0 1.622-.12 2.361-.336a.722.722 0 01.598.082l1.584.926a.272.272 0 00.14.045c.134 0 .24-.111.24-.247 0-.06-.023-.12-.038-.177l-.327-1.233a.582.582 0 01-.023-.156.49.49 0 01.201-.398C23.024 18.48 24 16.82 24 14.98c0-3.21-2.931-5.837-6.656-6.088V8.89a5.718 5.718 0 00-5.813-5.813 5.718 5.718 0 00-5.813 5.813c0 .586.087 1.144.236 1.673-.392.164-.772.39-1.127.654-.432-.593-1.027-1.052-1.502-1.502.42-.354.876-.632 1.33-.864C7.006 5.759 11.97 4.6 15.784 6.03c-.167-.38-.338-.788-.33-1.172zm-2.556 2.449c-.535 0-.969.44-.969.982s.434.982.969.982c.535 0 .969-.44.969-.982s-.434-.982-.969-.982zm4.844 0c-.535 0-.969.44-.969.982s.434.982.969.982c.535 0 .969-.44.969-.982s-.434-.982-.969-.982z"/>
        </svg>
      </button>
      <!-- WeChat Dropdown -->
      <div
        v-if="showWeChat"
        class="absolute right-full mr-3 top-1/2 -translate-y-1/2 bg-white rounded-xl shadow-xl p-5 w-72 border border-gray-100"
      >
        <h3 class="text-lg font-bold text-gray-900 mb-4">WeChat</h3>
        <div class="flex flex-col items-center">
          <div class="w-40 h-40 bg-gray-100 rounded-lg flex items-center justify-center mb-4 overflow-hidden">
            <img src="/wechat.jpg" alt="WeChat QR Code" class="w-full h-full object-contain cursor-zoom-in" @click="wechatLightboxOpen = true" />
          </div>
          <div class="text-center">
            <p class="text-sm text-gray-500 mb-1">{{ $t('contact.info.phone') }}</p>
            <p class="text-gray-900 font-medium">{{ wechatPhone }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Email -->
    <div class="relative">
      <button
        @click="toggleEmail"
        class="w-12 h-12 bg-gray-700 hover:bg-gray-800 text-white rounded-full shadow-lg flex items-center justify-center transition-all hover:scale-110"
        title="Email"
      >
        <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
        </svg>
      </button>
      <!-- Email Dropdown -->
      <div
        v-if="showEmail"
        class="absolute right-full mr-3 top-1/2 -translate-y-1/2 bg-white rounded-xl shadow-xl p-5 w-72 border border-gray-100"
      >
        <h3 class="text-lg font-bold text-gray-900 mb-4">{{ $t('contact.info.email') }}</h3>
        <div class="space-y-3">
          <div>
            <p class="text-sm text-gray-500 mb-1">{{ $t('contact.info.email') }}</p>
            <a :href="`mailto:${contactEmail}`" class="text-blue-600 hover:text-blue-700 font-medium break-all">{{ contactEmail }}</a>
          </div>
          <a
            :href="`mailto:${contactEmail}`"
            class="mt-2 inline-flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors text-sm font-medium"
          >
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
            </svg>
            Send Email
          </a>
        </div>
      </div>
    </div>

    <!-- Close all dropdowns backdrop -->
    <Teleport to="body">
      <div
        v-if="showContact || showWhatsApp || showWeChat || showEmail"
        class="fixed inset-0 z-30"
        @click="closeAll"
      ></div>
    </Teleport>

    <!-- WeChat Lightbox -->
    <Teleport to="body">
      <div
        v-if="wechatLightboxOpen"
        class="fixed inset-0 z-[100] bg-black/90 flex items-center justify-center"
        @click="wechatLightboxOpen = false"
      >
        <button
          class="absolute top-4 right-4 text-white hover:text-gray-300 transition-colors z-10"
          @click="wechatLightboxOpen = false"
        >
          <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
        <img
          src="/wechat.jpg"
          alt="WeChat QR Code"
          class="max-w-[80vw] max-h-[80vh] object-contain"
          @click.stop
        />
      </div>
    </Teleport>
  </div>

  <!-- Inquiry Modal -->
  <InquiryModal
    v-if="openInquiryModal"
    product-name=""
    @close="openInquiryModal = false"
  />
</template>

<script setup lang="ts">
import InquiryModal from './InquiryModal.vue'

const { locale } = useI18n()
const config = useRuntimeConfig()

// Dropdown states
const showContact = ref(false)
const showWhatsApp = ref(false)
const showWeChat = ref(false)
const showEmail = ref(false)
const openInquiryModal = ref(false)
const wechatLightboxOpen = ref(false)

// Company info
const companyInfo = ref<any>(null)

// Contact info from company
const contactEmail = computed(() => companyInfo.value?.email || 'info@specsense.com')
const contactPhone = computed(() => companyInfo.value?.phone || '+86 400-888-8888')
const contactAddress = computed(() => {
  if (!companyInfo.value) return 'Shenzhen, China'
  return locale.value === 'zh'
    ? (companyInfo.value.addressZh || companyInfo.value.addressEn || 'Shenzhen, China')
    : (companyInfo.value.addressEn || companyInfo.value.addressZh || 'Shenzhen, China')
})

// WhatsApp - hardcoded for now
const whatsappNumber = computed(() => companyInfo.value?.whatsapp || '+86 138-8888-8888')
const wechatQrCode = computed(() => companyInfo.value?.wechatQrCode || '')
const wechatPhone = computed(() => contactPhone.value)

async function loadCompanyInfo() {
  try {
    const response = await $fetch(`${config.public.apiBase}/api/company-info?locale=${locale.value}`)
    if ((response as any).code === 200 && (response as any).data) {
      companyInfo.value = (response as any).data
    }
  } catch (e) {
    console.warn('Failed to load company info')
  }
}

function toggleContact() {
  closeAll()
  showContact.value = !showContact.value
}

function toggleWhatsApp() {
  closeAll()
  showWhatsApp.value = !showWhatsApp.value
}

function toggleWeChat() {
  closeAll()
  showWeChat.value = !showWeChat.value
}

function toggleEmail() {
  closeAll()
  showEmail.value = !showEmail.value
}

function closeAll() {
  showContact.value = false
  showWhatsApp.value = false
  showWeChat.value = false
  showEmail.value = false
}

onMounted(() => {
  loadCompanyInfo()
})

watch(locale, () => {
  loadCompanyInfo()
})

watch(wechatLightboxOpen, (open) => {
  if (open) {
    document.addEventListener('keydown', handleLightboxKeydown)
  } else {
    document.removeEventListener('keydown', handleLightboxKeydown)
  }
})

function handleLightboxKeydown(e: KeyboardEvent) {
  if (e.key === 'Escape') wechatLightboxOpen = false
}
</script>
