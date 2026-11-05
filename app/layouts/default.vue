<template>
  <div class="min-h-screen flex flex-col">
    <AppHeader />
    <main class="flex-1">
      <slot />
    </main>
    <AppFooter />
    <ContactSidebar />
  </div>
</template>

<script setup lang="ts">
const head = useLocaleHead({ seo: { canonicalQueries: ['category', 'page', 'q'] } })
const route = useRoute()
useHead(() => ({
  htmlAttrs: head.value.htmlAttrs,
  // Product pages provide their own canonical URL (normalized filters/slugs).
  link: head.value.link?.filter(link => link.rel !== 'canonical' || !/^\/(cn\/)?products(?:\/|$)/.test(route.path)),
  meta: head.value.meta,
}))
</script>
