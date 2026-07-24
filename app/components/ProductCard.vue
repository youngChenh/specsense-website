<template>
  <NuxtLink :to="localePath(`/products/${product.slug}`)" class="group">
    <div class="bg-white rounded-xl shadow-md overflow-hidden hover:shadow-xl transition-shadow duration-300 h-full flex flex-col">
      <div class="bg-gray-200 overflow-hidden flex-shrink-0">
        <img
          v-if="product.imageUrl"
          :src="getImageUrl(product.imageUrl)"
          :alt="displayName"
          class="w-full h-56 object-cover group-hover:scale-105 transition-transform duration-300"
        />
        <div v-else class="w-full h-56 bg-gray-200 flex items-center justify-center">
          <svg class="w-16 h-16 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
          </svg>
        </div>
      </div>
      <div class="p-6 flex flex-col flex-1">
        <span class="inline-block px-3 py-1 bg-blue-100 text-blue-600 rounded-full text-xs font-medium mb-3 self-start">
          {{ categoryLabel }}
        </span>
        <h3 class="text-lg font-semibold text-gray-900 mb-2 group-hover:text-blue-600 transition-colors">
          {{ displayName }}
        </h3>
        <p class="text-gray-600 text-sm mb-4 line-clamp-2 flex-1">
          {{ displayDescription }}
        </p>
        <div class="flex items-center text-blue-600 font-medium">
          {{ $t('common.learnMore') }}
          <svg class="w-4 h-4 ml-1 group-hover:translate-x-1 transition-transform" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
          </svg>
        </div>
      </div>
    </div>
  </NuxtLink>
</template>

<script setup lang="ts">
const { t, locale } = useI18n()
const { getImageUrl } = useImageUrl()

const props = defineProps<{
  product: {
    slug: string
    nameEn?: string
    nameZh?: string
    descriptionEn?: string
    descriptionZh?: string
    imageUrl?: string
    categoryKey?: string
    categoryName?: string
  }
}>()

const localePath = useLocalePath()

const displayName = computed(() => {
  return locale.value === 'zh' ? (props.product.nameZh || props.product.nameEn || '') : (props.product.nameEn || props.product.nameZh || '')
})

const displayDescription = computed(() => {
  return locale.value === 'zh' ? (props.product.descriptionZh || props.product.descriptionEn || '') : (props.product.descriptionEn || props.product.descriptionZh || '')
})

const categoryLabel = computed(() => {
  if (props.product.categoryName) {
    return props.product.categoryName
  }
  const categoryLabels: Record<string, string> = {
    spectrometer: 'products.categories.spectrometer',
    analyzer: 'products.categories.analyzer',
    sensor: 'products.categories.sensor',
    lightsource: 'products.categories.lightsource',
    accessory: 'products.categories.accessory',
  }
  return t(categoryLabels[props.product.categoryKey || ''] || 'products.title')
})
</script>
