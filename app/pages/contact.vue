<template>
  <div class="bg-gray-50">
    <!-- Hero -->
    <section class="relative">
      <div class="w-full h-[350px] overflow-hidden">
        <img
          src="/Contact_Us.png"
          alt="Contact Us"
          class="w-full h-full object-cover"
        />
      </div>
    </section>

    <!-- Contact Form & Info -->
    <section class="py-20">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-12">
          <!-- Form -->
          <div class="bg-white rounded-2xl shadow-lg p-8">
            <form @submit.prevent="submitForm" class="space-y-6">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  {{ $t('contact.form.name') }} *
                </label>
                <input
                  v-model="form.name"
                  type="text"
                  required
                  class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  :placeholder="$t('contact.form.name')"
                />
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  {{ $t('contact.form.email') }} *
                </label>
                <input
                  v-model="form.email"
                  type="email"
                  required
                  class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  :placeholder="$t('contact.form.email')"
                />
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  {{ $t('contact.form.company') }}
                </label>
                <input
                  v-model="form.company"
                  type="text"
                  class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  :placeholder="$t('contact.form.company')"
                />
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  {{ $t('contact.form.product') }}
                </label>
                <input
                  v-model="form.product"
                  type="text"
                  class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  :placeholder="$t('contact.form.product')"
                />
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  {{ $t('contact.form.message') }} *
                </label>
                <textarea
                  v-model="form.message"
                  required
                  rows="5"
                  class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  :placeholder="$t('contact.form.message')"
                ></textarea>
              </div>

              <button
                type="submit"
                :disabled="isSubmitting"
                class="w-full px-6 py-4 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 transition-colors disabled:bg-blue-400"
              >
                {{ isSubmitting ? $t('contact.form.sending') : $t('contact.form.submit') }}
              </button>

              <!-- Success Message -->
              <div v-if="submitStatus === 'success'" class="p-4 bg-green-50 text-green-700 rounded-lg">
                {{ $t('contact.form.success') }}
              </div>

              <!-- Error Message -->
              <div v-if="submitStatus === 'error'" class="p-4 bg-red-50 text-red-700 rounded-lg">
                {{ $t('contact.form.error') }}
              </div>
            </form>
          </div>

          <!-- Contact Info -->
          <div>
            <div class="bg-white rounded-2xl shadow-lg p-8 mb-8">
              <h3 class="text-xl font-bold text-gray-900 mb-6">{{ $t('contact.info.title') }}</h3>
              <div class="space-y-6">
                <div class="flex items-start space-x-4">
                  <div class="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center flex-shrink-0">
                    <svg class="w-6 h-6 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                    </svg>
                  </div>
                  <div>
                    <h4 class="font-medium text-gray-900">{{ $t('contact.info.email') }}</h4>
                    <p class="text-gray-600">{{ contactEmail }}</p>
                  </div>
                </div>

                <div class="flex items-start space-x-4">
                  <div class="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center flex-shrink-0">
                    <svg class="w-6 h-6 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
                    </svg>
                  </div>
                  <div>
                    <h4 class="font-medium text-gray-900">{{ $t('contact.info.phone') }}</h4>
                    <p class="text-gray-600">{{ contactPhone }}</p>
                  </div>
                </div>

                <div class="flex items-start space-x-4">
                  <div class="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center flex-shrink-0">
                    <svg class="w-6 h-6 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                    </svg>
                  </div>
                  <div>
                    <h4 class="font-medium text-gray-900">{{ $t('contact.info.address') }}</h4>
                    <p class="text-gray-600">{{ contactAddress }}</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- Map Location -->
            <div class="bg-gray-200 rounded-2xl overflow-hidden">
              <div class="flex gap-2 p-2">
                <img src="/code_1.png" alt="code_1" class="w-1/2 object-contain" />
                <img src="/code_2.jpg" alt="code_2" class="w-1/2 object-contain" />
              </div>
              <div class="flex justify-center gap-3 p-3 bg-gray-100">
                <a
                  v-for="link in socialLinks"
                  :key="link.name"
                  :href="link.url"
                  target="_blank"
                  rel="noopener noreferrer"
                  :title="link.name"
                  class="hover:opacity-70 hover:scale-110 transition-all duration-200"
                >
                  <img :src="link.icon" :alt="link.name" class="h-8 object-contain" />
                </a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
const { t, locale } = useI18n()
const config = useRuntimeConfig()

const form = reactive({
  name: '',
  email: '',
  company: '',
  product: '',
  message: '',
})

const isSubmitting = ref(false)
const submitStatus = ref<'idle' | 'success' | 'error'>('idle')

const companyInfo = ref<any>(null)

async function loadCompanyInfo() {
  try {
    const response = await fetch(`${config.public.apiBase}/api/company-info?locale=${locale.value}`)
    const result = await response.json()
    if (result.code === 200 && result.data) {
      companyInfo.value = result.data
    }
  } catch (e) {
    console.warn('Failed to load company info')
  }
}

const contactEmail = computed(() => companyInfo.value?.email || '')
const contactPhone = computed(() => companyInfo.value?.phone || '')
const contactAddress = computed(() => {
  if (!companyInfo.value) return ''
  return locale.value === 'zh'
    ? (companyInfo.value.addressZh || companyInfo.value.addressEn || '')
    : (companyInfo.value.addressEn || companyInfo.value.addressZh || '')
})

const socialLinks = [
  { name: 'Facebook', icon: '/social_1.png', url: 'https://www.facebook.com/SpeSenseTech' },
  { name: 'Instagram', icon: '/social_2.png', url: 'https://www.instagram.com/spesense_technologies_inc/' },
  { name: 'Pinterest', icon: '/social_3.png', url: 'https://www.pinterest.com/spesense/' },
  { name: 'X', icon: '/social_4.png', url: 'https://x.com/spesense' },
  { name: 'TikTok', icon: '/social_5.png', url: 'https://www.tiktok.com/@spesense' },
  { name: 'YouTube', icon: '/social_6.png', url: 'https://www.youtube.com/@SpeSense' },
  { name: 'LinkedIn', icon: '/social_7.png', url: 'https://www.linkedin.com/in/spesense/recent-activity/all/' },
]

onMounted(() => {
  loadCompanyInfo()
})

watch(locale, () => {
  loadCompanyInfo()
})

async function submitForm() {
  isSubmitting.value = true
  submitStatus.value = 'idle'

  try {
    const response = await fetch(`${config.public.apiBase}/api/contact`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(form),
    })

    const result = await response.json()

    if (result.code === 200) {
      // Reset form
      form.name = ''
      form.email = ''
      form.company = ''
      form.product = ''
      form.message = ''
      submitStatus.value = 'success'
    } else {
      submitStatus.value = 'error'
    }
  } catch (error) {
    console.error('Submit error:', error)
    submitStatus.value = 'error'
  } finally {
    isSubmitting.value = false
  }
}
</script>