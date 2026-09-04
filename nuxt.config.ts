// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',
  devtools: { enabled: true },

  modules: [
    '@nuxtjs/tailwindcss',
    '@nuxtjs/i18n',
    '@element-plus/nuxt',
    '@nuxtjs/sitemap',
    '@nuxtjs/robots',
  ],

  elementPlus: {
    importStyle: 'css',
  },

  site: {
    url: process.env.NUXT_PUBLIC_SITE_URL || 'https://www.spesense.com',
    name: 'SpeSense',
  },

  i18n: {
    locales: [
      { code: 'en', name: 'English', file: 'en.json' },
      { code: 'zh', name: '中文', file: 'zh.json' },
    ],
    defaultLocale: 'zh',
    lazy: true,
    langDir: 'locales/',
    strategy: 'no_prefix',
    detectBrowserLanguage: {
      useCookie: true,
      cookieKey: 'i18n_locale',
      redirectOn: 'root',
    },
  },

  sitemap: {
    exclude: [
      '/admin/**',
      '/admin',
      '/admin/login',
      '/admin/*',
      '/privacy',
      '/terms',
    ],
    sources: [
      '/api/sitemap/urls',
    ],
  },

  robots: {
    blockNonSeoBots: true,
  },

  app: {
    head: {
      title: 'SpeSense - Photoelectric Instrument Solutions',
      htmlAttrs: {
        lang: 'zh',
      },
      meta: [
        { charset: 'utf-8' },
        { name: 'viewport', content: 'width=device-width, initial-scale=1' },
        { name: 'description', content: 'SpeSense Technologies - Professional photoelectric instrument manufacturer' },
      ],
      link: [
        { rel: 'icon', type: 'image/svg+xml', href: '/favicon.svg' },
      ],
    },
  },

  tailwindcss: {
    cssPath: '~/assets/css/main.css',
  },

  runtimeConfig: {
    public: {
      apiBase: process.env.NUXT_PUBLIC_API_BASE || (process.env.NODE_ENV === 'production' ? '/specsense' : 'http://localhost:8080/specsense'),
    },
  },

  nitro: {
    preset: 'node-server',
  },
})