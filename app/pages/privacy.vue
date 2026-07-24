<template>
  <div class="bg-white py-16">
    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
      <h1 class="text-3xl font-bold text-gray-900 mb-8">{{ $t('footer.links.privacy') }}</h1>
      <div class="prose prose-lg max-w-none text-gray-600">
        <p>{{ locale === 'zh' ? '隐私政策内容正在更新中...' : 'Privacy policy content coming soon...' }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const { locale } = useI18n()
</script>
