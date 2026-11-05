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
      { code: 'en', language: 'en', name: 'English', file: 'en.json' },
      { code: 'cn', language: 'zh-CN', name: '中文', file: 'zh.json' },
    ],
    defaultLocale: 'en',
    baseUrl: process.env.NUXT_PUBLIC_SITE_URL || 'https://www.spesense.com',
    lazy: true,
    langDir: 'locales/',
    strategy: 'prefix_except_default',
    detectBrowserLanguage: false,
    customRoutes: 'config',
    pages: Object.fromEntries([
      'admin/index', 'admin/categories', 'admin/brands', 'admin/banners',
      'admin/applications', 'admin/downloads', 'admin/login', 'admin/company',
      'admin/inquiries', 'admin/menus', 'admin/news', 'admin/services', 'admin/products',
      'admin/crm/index', 'admin/crm/users', 'admin/crm/orders/index', 'admin/crm/inquiries/index',
      'admin/crm/inquiries/[id]', 'admin/crm/customers/index', 'admin/crm/customers/[id]',
      'admin/crm/after-sales/index',
    ].map(path => [path, false])),
  },

  sitemap: {
    exclude: [
      '/admin/**',
      '/admin',
      '/admin/login',
      '/admin/*',
      '/privacy',
      '/terms',
      '/cn/privacy',
      '/cn/terms',
      '/cn/admin/**',
      '/cn/admin',
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
      title: 'Spectrometer & Sensor Manufacturer | SpeSense Technologies',
      htmlAttrs: {
        lang: 'en',
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
    // Absolute backend URL for SSR; Docker overrides this with http://api:8080/specsense.
    apiBase: 'http://localhost:8080/specsense',
    public: {
      siteUrl: process.env.NUXT_PUBLIC_SITE_URL || 'https://www.spesense.com',
      apiBase: process.env.NUXT_PUBLIC_API_BASE || (process.env.NODE_ENV === 'production' ? '/specsense' : 'http://localhost:8080/specsense'),
    },
  },

  nitro: {
    preset: 'node-server',
  },
})
