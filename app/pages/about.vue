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
            <p class="text-lg text-gray-600 mb-6 whitespace-pre-wrap">
              {{ description || $t('about.company.desc') }}
            </p>
          </div>
          <div class="bg-gradient-to-br from-blue-100 to-indigo-100 rounded-2xl p-8">
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
            <ul class="space-y-2 text-gray-600">
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
            <div class="grid grid-cols-3 gap-4">
              <div class="text-center p-4 bg-gray-50 rounded-lg">
                <div class="text-2xl font-bold text-blue-600">{{ partnersStats.institutions || '50+' }}</div>
                <div class="text-sm text-gray-500">Research Institutions</div>
              </div>
              <div class="text-center p-4 bg-gray-50 rounded-lg">
                <div class="text-2xl font-bold text-blue-600">{{ partnersStats.countries || '100+' }}</div>
                <div class="text-sm text-gray-500">Countries</div>
              </div>
              <div class="text-center p-4 bg-gray-50 rounded-lg">
                <div class="text-2xl font-bold text-blue-600">{{ partnersStats.customers || '1000+' }}</div>
                <div class="text-sm text-gray-500">Customers</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Stats -->
    <section class="py-20 bg-blue-600">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-2 md:grid-cols-4 gap-8 text-center text-white">
          <div>
            <div class="text-5xl font-bold mb-2">{{ stats.years || '18+' }}</div>
            <div class="text-blue-200">Years Experience</div>
          </div>
          <div>
            <div class="text-5xl font-bold mb-2">{{ stats.countries || '100+' }}</div>
            <div class="text-blue-200">Countries Served</div>
          </div>
          <div>
            <div class="text-5xl font-bold mb-2">{{ stats.products || '200+' }}</div>
            <div class="text-blue-200">Products</div>
          </div>
          <div>
            <div class="text-5xl font-bold mb-2">{{ stats.engineers || '50+' }}</div>
            <div class="text-blue-200">R&D Engineers</div>
          </div>
        </div>
      </div>
    </section>

  </div>
</template>

<script setup lang="ts">
const localePath = useLocalePath()
const config = useRuntimeConfig()
const { locale } = useI18n()
const { getImageUrl } = useImageUrl()

const companyInfo = ref<any>(null)
const loading = ref(true)

const timeline = computed(() => {
  if (!companyInfo.value?.timelineJson) {
    return [
      { year: '2008', label: 'about.timeline.year2008' },
      { year: '2012', label: 'about.timeline.year2012' },
      { year: '2016', label: 'about.timeline.year2016' },
      { year: '2020', label: 'about.timeline.year2020' },
      { year: '2024', label: 'about.timeline.year2024' },
    ]
  }
  try {
    const parsed = JSON.parse(companyInfo.value.timelineJson)
    return parsed.map((item: any) => ({
      year: item.year,
      label: locale.value === 'zh' ? item.event_zh : item.event_en,
    }))
  } catch {
    return []
  }
})

const stats = computed(() => {
  if (!companyInfo.value?.statsJson) {
    return { years: '18+', countries: '100+', products: '200+', engineers: '50+' }
  }
  try {
    return JSON.parse(companyInfo.value.statsJson)
  } catch {
    return { years: '18+', countries: '100+', products: '200+', engineers: '50+' }
  }
})

const values = computed(() => {
  if (!companyInfo.value?.valuesJson) {
    return ['Innovation', 'Quality', 'Integrity']
  }
  try {
    return JSON.parse(companyInfo.value.valuesJson)
  } catch {
    return ['Innovation', 'Quality', 'Integrity']
  }
})

const qualifications = computed(() => {
  if (!companyInfo.value?.qualificationsJson) {
    return ['ISO 9001:2015 Certified', 'CE Marking', 'RoHS Compliance']
  }
  try {
    return JSON.parse(companyInfo.value.qualificationsJson)
  } catch {
    return ['ISO 9001:2015 Certified', 'CE Marking', 'RoHS Compliance']
  }
})

const partnersStats = computed(() => {
  if (!companyInfo.value?.partnersStatsJson) {
    return { institutions: '50+', countries: '100+', customers: '1000+' }
  }
  try {
    return JSON.parse(companyInfo.value.partnersStatsJson)
  } catch {
    return { institutions: '50+', countries: '100+', customers: '1000+' }
  }
})

const aboutText = computed(() => {
  if (!companyInfo.value) return ''
  return locale.value === 'zh'
    ? (companyInfo.value.aboutZh || companyInfo.value.aboutEn || '')
    : (companyInfo.value.aboutEn || companyInfo.value.aboutZh || '')
})

const description = computed(() => {
  if (!companyInfo.value) return ''
  return locale.value === 'zh'
    ? (companyInfo.value.descriptionZh || companyInfo.value.descriptionEn || '')
    : (companyInfo.value.descriptionEn || companyInfo.value.descriptionZh || '')
})

const mission = computed(() => {
  if (!companyInfo.value) return ''
  return locale.value === 'zh'
    ? (companyInfo.value.missionZh || companyInfo.value.missionEn || '')
    : (companyInfo.value.missionEn || companyInfo.value.missionZh || '')
})

const companyImage = computed(() => {
  if (!companyInfo.value?.imageUrl) {
    return 'https://images.unsplash.com/photo-1497366216548-37526070297c?w=600&h=400&fit=crop'
  }
  return companyInfo.value.imageUrl
})

const contactEmail = computed(() => companyInfo.value?.email || 'info@specsense.com')
const contactPhone = computed(() => companyInfo.value?.phone || '+86 400-888-8888')
const contactAddress = computed(() => {
  if (!companyInfo.value) return 'Shenzhen, China'
  return locale.value === 'zh'
    ? (companyInfo.value.addressZh || companyInfo.value.addressEn || 'Shenzhen, China')
    : (companyInfo.value.addressEn || companyInfo.value.addressZh || 'Shenzhen, China')
})

async function fetchCompanyInfo() {
  loading.value = true
  try {
    const response = await fetch(`${config.public.apiBase}/api/company-info?locale=${locale.value}`)
    const result = await response.json()
    if (result.code === 200 && result.data) {
      companyInfo.value = result.data
    }
  } catch (error) {
    console.error('Failed to fetch company info:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchCompanyInfo()
})

watch(locale, () => {
  fetchCompanyInfo()
})
</script>
