<template>
  <div class="bg-gray-50">
    <!-- Loading State -->
    <div v-if="loading" class="py-16">
      <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="animate-pulse space-y-6">
          <div class="h-4 bg-gray-200 rounded w-1/4"></div>
          <div class="h-10 bg-gray-200 rounded w-3/4"></div>
          <div class="h-4 bg-gray-200 rounded w-1/2"></div>
          <div class="h-96 bg-gray-200 rounded"></div>
        </div>
      </div>
    </div>

    <!-- Article -->
    <article v-else-if="article" class="py-16">
      <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        <!-- Breadcrumb -->
        <nav class="mb-8">
          <ol class="flex items-center space-x-2 text-sm">
            <li>
              <NuxtLink :to="localePath('/')" class="text-gray-500 hover:text-gray-700">{{ $t('nav.home') }}</NuxtLink>
            </li>
            <li class="text-gray-400">/</li>
            <li>
              <NuxtLink :to="localePath('/news')" class="text-gray-500 hover:text-gray-700">{{ $t('news.title') }}</NuxtLink>
            </li>
            <li class="text-gray-400">/</li>
            <li class="text-blue-600">{{ article?.title }}</li>
          </ol>
        </nav>

        <!-- Article Header -->
        <header class="mb-12">
          <span class="inline-block px-3 py-1 bg-blue-100 text-blue-600 rounded-full text-sm font-medium mb-4">
            {{ article ? $t(article.categoryLabel) : '' }}
          </span>
          <h1 class="text-4xl font-bold text-gray-900 mb-4">{{ article?.title }}</h1>
          <div class="flex items-center text-gray-500 text-sm">
            <svg class="w-5 h-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
            <span>{{ $t('news.publishedOn') }}: {{ article?.date }}</span>
          </div>
        </header>

        <!-- Featured Image -->
        <div v-if="article" class="mb-12 rounded-xl overflow-hidden">
          <img
            :src="getImageUrl(article.image)"
            :alt="article.title"
            class="w-full h-96 object-cover"
          />
        </div>

        <!-- Article Content -->
        <div v-if="article" class="prose prose-lg max-w-none">
          <p class="text-xl text-gray-600 mb-8 leading-relaxed" v-html="article.excerpt"></p>
          <div class="text-gray-700 space-y-6" v-html="article.content"></div>
        </div>

        <!-- Not Found -->
        <div v-else class="text-center py-20">
          <p class="text-gray-500 text-lg">Article not found.</p>
          <NuxtLink
            :to="localePath('/news')"
            class="inline-flex items-center mt-4 text-blue-600 hover:text-blue-800 font-medium"
          >
            {{ $t('news.backToList') }}
          </NuxtLink>
        </div>

        <!-- Back to News -->
        <div class="mt-16 pt-8 border-t">
          <NuxtLink
            :to="localePath('/news')"
            class="inline-flex items-center text-blue-600 hover:text-blue-800 font-medium"
          >
            <svg class="w-4 h-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
            </svg>
            {{ $t('news.backToList') }}
          </NuxtLink>
        </div>
      </div>
    </article>

    <!-- Related News -->
    <section class="py-16 bg-white">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h2 class="text-2xl font-bold text-gray-900 mb-8">{{ $t('newsPreview.title') }}</h2>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
          <NuxtLink
            v-for="related in relatedNews"
            :key="related.slug"
            :to="localePath(`/news/${related.slug}`)"
            class="group"
          >
            <div class="rounded-lg overflow-hidden mb-3">
              <img
                :src="getImageUrl(related.image)"
                :alt="related.title"
                class="w-full h-40 object-cover group-hover:scale-105 transition-transform duration-300"
              />
            </div>
            <span class="text-sm text-gray-500">{{ related.date }}</span>
            <h3 class="text-lg font-semibold text-gray-900 group-hover:text-blue-600 transition-colors line-clamp-2 mt-1">
              {{ related.title }}
            </h3>
          </NuxtLink>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
const route = useRoute()
const localePath = useLocalePath()
const api = useApi()
const { locale } = useI18n()
const { getImageUrl } = useImageUrl()

const article = ref<any>(null)
const relatedNews = ref<any[]>([])
const loading = ref(true)

async function fetchArticle() {
  loading.value = true
  try {
    const slug = route.params.slug as string
    const [articleData, latestNews] = await Promise.all([
      api.fetchNewsItem(slug, locale.value),
      api.fetchLatestNews(4, locale.value),
    ])

    if (articleData) {
      // Transform API data to match template
      article.value = {
        slug: articleData.slug,
        title: locale.value === 'zh' ? (articleData.titleZh || articleData.titleEn) : (articleData.titleEn || articleData.titleZh),
        category: articleData.category,
        categoryLabel: `news.${articleData.category}`,
        date: articleData.publishedAt ? new Date(articleData.publishedAt).toLocaleDateString() : '',
        image: articleData.imageUrl || '',
        excerpt: ((locale.value === 'zh' ? (articleData.excerptZh || articleData.excerptEn) : (articleData.excerptEn || articleData.excerptZh)) || '').replace(/\n/g, '<br>'),
        content: (locale.value === 'zh' ? (articleData.contentZh || articleData.contentEn) : (articleData.contentEn || articleData.contentZh) || '').replace(/\n/g, '<br>'),
      }
    }

    // Filter out current article from related news
    relatedNews.value = latestNews
      .filter((item: any) => item.slug !== slug)
      .slice(0, 3)
      .map((item: any) => ({
        slug: item.slug,
        title: locale.value === 'zh' ? (item.titleZh || item.titleEn) : (item.titleEn || item.titleZh),
        category: item.category,
        date: item.publishedAt ? new Date(item.publishedAt).toLocaleDateString() : '',
        image: item.imageUrl || '',
      }))
  } catch (error) {
    console.error('Failed to fetch article:', error)
    article.value = null
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchArticle()
})

watch(locale, () => {
  fetchArticle()
})
</script>
