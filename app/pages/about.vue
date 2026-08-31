<template>
  <div class="bg-white">
    <!-- Hero -->
    <section class="relative">
      <div class="w-full h-[350px] overflow-hidden">
        <img
          src="/About_Us.png"
          alt="About Us"
          class="w-full h-full object-cover"
        />
      </div>
    </section>

    <!-- Company Section -->
    <section class="py-20">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
          <div>
            <h2 class="text-3xl font-bold text-gray-900 mb-6">{{ $t('about.company.title') }}</h2>
            <div v-if="loading" class="space-y-2 animate-pulse">
              <div class="h-4 bg-gray-200 rounded w-full"></div>
              <div class="h-4 bg-gray-200 rounded w-5/6"></div>
              <div class="h-4 bg-gray-200 rounded w-4/6"></div>
            </div>
            <p v-else-if="description" class="text-lg text-gray-600 mb-6 whitespace-pre-wrap">
              {{ description }}
            </p>
          </div>
          <div v-if="companyImage" class="bg-gradient-to-br from-blue-100 to-indigo-100 rounded-2xl p-8">
            <img
              :src="getImageUrl(companyImage)"
              alt="SpeSense Headquarters"
              class="rounded-xl w-full"
            />
          </div>
        </div>
      </div>
    </section>

    <!-- Mission & Values -->
    <section class="py-20 bg-gray-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-12">
          <!-- Mission -->
          <div class="rounded-xl overflow-hidden shadow-md">
            <img
              src="/mission.png"
              alt="Our Mission"
              class="w-full h-full object-cover"
            />
          </div>

          <!-- Values -->
          <div class="rounded-xl overflow-hidden shadow-md">
            <img
              src="/vision.png"
              alt="Our Vision"
              class="w-full h-full object-cover"
            />
          </div>
        </div>
      </div>
    </section>

    <!-- 公司生产仓库 -->
    <section class="py-20">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h2 class="text-3xl font-bold text-center text-gray-900 mb-12">{{ $t('about.companyWarehouse') }}</h2>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div class="rounded-xl overflow-hidden shadow-lg hover:shadow-xl transition-shadow duration-300">
            <img
              src="/about_us_1.jpg"
              alt="公司生产仓库 1"
              class="w-full h-72 object-cover hover:scale-105 transition-transform duration-300"
            />
          </div>
          <div class="rounded-xl overflow-hidden shadow-lg hover:shadow-xl transition-shadow duration-300">
            <img
              src="/about_us_2.png"
              alt="公司生产仓库 2"
              class="w-full h-72 object-cover hover:scale-105 transition-transform duration-300"
            />
          </div>
          <div class="rounded-xl overflow-hidden shadow-lg hover:shadow-xl transition-shadow duration-300">
            <img
              src="/about_us_3.png"
              alt="公司生产仓库 3"
              class="w-full h-72 object-cover hover:scale-105 transition-transform duration-300"
            />
          </div>
        </div>
      </div>
    </section>

    <!-- Qualifications & Partners -->
    <section class="py-20 bg-gray-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-12">
          <!-- Qualifications -->
          <div class="bg-white rounded-xl shadow-md p-8">
            <div class="w-16 h-16 bg-blue-100 rounded-lg flex items-center justify-center mb-6">
              <svg class="w-8 h-8 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4M7.835 4.697a3.42 3.42 0 001.946-.806 3.42 3.42 0 014.438 0 3.42 3.42 0 001.946.806 3.42 3.42 0 013.138 3.138 3.42 3.42 0 00.806 1.946 3.42 3.42 0 010 4.438 3.42 3.42 0 00-.806 1.946 3.42 3.42 0 01-3.138 3.138 3.42 3.42 0 00-1.946.806 3.42 3.42 0 01-4.438 0 3.42 3.42 0 00-1.946-.806 3.42 3.42 0 01-3.138-3.138 3.42 3.42 0 00-.806-1.946 3.42 3.42 0 010-4.438 3.42 3.42 0 00.806-1.946 3.42 3.42 0 013.138-3.138z" />
              </svg>
            </div>
            <h3 class="text-2xl font-bold text-gray-900 mb-4">{{ $t('about.qualifications.title') }}</h3>
            <p class="text-gray-600 mb-4">{{ $t('about.qualifications.desc') }}</p>
            <ul v-if="qualifications.length > 0" class="space-y-2 text-gray-600">
              <li v-for="q in qualifications" :key="q" class="flex items-center">
                <svg class="w-5 h-5 text-green-500 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                </svg>
                {{ q }}
              </li>
            </ul>
          </div>

          <!-- Partners -->
          <div class="bg-white rounded-xl shadow-md p-8">
            <div class="w-16 h-16 bg-blue-100 rounded-lg flex items-center justify-center mb-6">
              <svg class="w-8 h-8 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
              </svg>
            </div>
            <h3 class="text-2xl font-bold text-gray-900 mb-4">{{ $t('about.partners.title') }}</h3>
            <p class="text-gray-600 mb-4">{{ $t('about.partners.desc') }}</p>
            <div v-if="partnersStats" class="grid grid-cols-3 gap-4">
              <div class="text-center p-4 bg-gray-50 rounded-lg">
                <div class="text-2xl font-bold text-blue-600">{{ partnersStats.institutions }}</div>
                <div class="text-sm text-gray-500">Research Institutions</div>
              </div>
              <div class="text-center p-4 bg-gray-50 rounded-lg">
                <div class="text-2xl font-bold text-blue-600">{{ partnersStats.countries }}</div>
                <div class="text-sm text-gray-500">Countries</div>
              </div>
              <div class="text-center p-4 bg-gray-50 rounded-lg">
                <div class="text-2xl font-bold text-blue-600">{{ partnersStats.customers }}</div>
                <div class="text-sm text-gray-500">Customers</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Stats -->
    <section v-if="stats" class="py-20 bg-blue-600">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-2 md:grid-cols-4 gap-8 text-center text-white">
          <div>
            <div class="text-5xl font-bold mb-2">{{ stats.years }}</div>
            <div class="text-blue-200">Years Experience</div>
          </div>
          <div>
            <div class="text-5xl font-bold mb-2">{{ stats.countries }}</div>
            <div class="text-blue-200">Countries Served</div>
          </div>
          <div>
            <div class="text-5xl font-bold mb-2">{{ stats.products }}</div>
            <div class="text-blue-200">Products</div>
          </div>
          <div>
            <div class="text-5xl font-bold mb-2">{{ stats.engineers }}</div>
            <div class="text-blue-200">R&D Engineers</div>
          </div>
        </div>
      </div>
    </section>

  </div>
</template>

<script setup lang="ts">
const config = useRuntimeConfig()
const { locale } = useI18n()
const { getImageUrl } = useImageUrl()

const { data: companyInfo, pending: loading, refresh: refreshCompanyInfo } = await useAsyncData(
  'companyInfo',
  async () => {
    const response = await $fetch<any>(`${config.public.apiBase}/api/company-info?locale=${locale.value}`)
    return response?.data || null
  },
  { watch: [locale] }
)

const stats = computed(() => {
  if (!companyInfo.value?.statsJson) return null
  try {
    return JSON.parse(companyInfo.value.statsJson)
  } catch {
    return null
  }
})

const qualifications = computed(() => {
  if (!companyInfo.value?.qualificationsJson) return []
  try {
    return JSON.parse(companyInfo.value.qualificationsJson)
  } catch {
    return []
  }
})

const partnersStats = computed(() => {
  if (!companyInfo.value?.partnersStatsJson) return null
  try {
    return JSON.parse(companyInfo.value.partnersStatsJson)
  } catch {
    return null
  }
})

const description = computed(() => {
  if (!companyInfo.value) return ''
  return locale.value === 'zh'
    ? (companyInfo.value.descriptionZh || companyInfo.value.descriptionEn || '')
    : (companyInfo.value.descriptionEn || companyInfo.value.descriptionZh || '')
})

const companyImage = computed(() => companyInfo.value?.imageUrl || '')
</script>
