export default defineSitemapEventHandler(async () => {
  const config = useRuntimeConfig()
  const apiBase = config.apiBase as string

  const staticUrls = [
    { loc: '/', changefreq: 'daily', priority: 1.0 },
    { loc: '/products', changefreq: 'daily', priority: 0.9 },
    { loc: '/applications', changefreq: 'weekly', priority: 0.8 },
    { loc: '/services', changefreq: 'weekly', priority: 0.7 },
    { loc: '/brands', changefreq: 'weekly', priority: 0.8 },
    { loc: '/news', changefreq: 'daily', priority: 0.8 },
    { loc: '/about', changefreq: 'monthly', priority: 0.6 },
    { loc: '/contact', changefreq: 'monthly', priority: 0.6 },
  ]

  const dynamicUrls: Array<{ loc: string; _encoded: boolean; lastmod?: string; changefreq?: string; priority?: number }> = []

  async function fetchData(path: string): Promise<any> {
    try {
      const json: any = await $fetch(`${apiBase}${path}`, { timeout: 15000, retry: 0 })
      if (json?.code !== 200 || !json.data) throw new Error('Invalid sitemap source response')
      return json.data
    } catch {
      // Let the sitemap module see the failure instead of accepting an empty catalog.
      throw createError({ statusCode: 503, statusMessage: 'Sitemap source temporarily unavailable' })
    }
  }

  const seen = new Set<string>()
  const pageSize = 100
  let loaded = 0
  for (let page = 1; ; page++) {
    const productsData = await fetchData(`/api/products?page=${page}&pageSize=${pageSize}&locale=en`)
    if (!Array.isArray(productsData.data) || !Number.isSafeInteger(productsData.total) || productsData.total < 0) {
      throw createError({ statusCode: 503, statusMessage: 'Invalid product sitemap data' })
    }
    const previousSize = seen.size
    for (const p of productsData.data) {
      if (p.slug && !seen.has(p.slug)) {
        seen.add(p.slug)
        dynamicUrls.push({
          loc: `/products/${encodeURIComponent(p.slug)}`,
          // Slugs are already encoded as path segments; prevent %20 becoming %2520.
          _encoded: true,
          changefreq: 'weekly',
          priority: 0.7,
        })
      }
    }
    loaded += productsData.data.length
    if (loaded >= productsData.total) break
    if (!productsData.data.length || seen.size === previousSize) {
      throw createError({ statusCode: 503, statusMessage: 'Incomplete product sitemap data' })
    }
  }

  const newsData = await fetchData('/api/news/latest?limit=100&locale=en')
  if (Array.isArray(newsData)) {
    for (const n of newsData) {
      if (n.slug) {
        dynamicUrls.push({
          loc: `/news/${encodeURIComponent(n.slug)}`,
          _encoded: true,
          changefreq: 'monthly',
          priority: 0.6,
        })
      }
    }
  }

  return [...staticUrls, ...dynamicUrls]
})
