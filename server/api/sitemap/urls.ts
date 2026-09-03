export default defineSitemapEventHandler(async () => {
  const config = useRuntimeConfig()
  const apiBase = config.public.apiBase as string
  const now = new Date().toISOString()

  const staticUrls = [
    { loc: '/', changefreq: 'daily', priority: 1.0, lastmod: now },
    { loc: '/products', changefreq: 'daily', priority: 0.9, lastmod: now },
    { loc: '/applications', changefreq: 'weekly', priority: 0.8, lastmod: now },
    { loc: '/services', changefreq: 'weekly', priority: 0.7, lastmod: now },
    { loc: '/downloads', changefreq: 'weekly', priority: 0.7, lastmod: now },
    { loc: '/brands', changefreq: 'weekly', priority: 0.8, lastmod: now },
    { loc: '/news', changefreq: 'daily', priority: 0.8, lastmod: now },
    { loc: '/about', changefreq: 'monthly', priority: 0.6, lastmod: now },
    { loc: '/contact', changefreq: 'monthly', priority: 0.6, lastmod: now },
  ]

  const dynamicUrls: Array<{ loc: string; lastmod?: string; changefreq?: string; priority?: number }> = []

  async function safeFetch<T = any>(path: string, timeoutMs = 15000): Promise<T | null> {
    try {
      const ctrl = new AbortController()
      const t = setTimeout(() => ctrl.abort(), timeoutMs)
      const res = await fetch(`${apiBase}${path}`, { signal: ctrl.signal })
      clearTimeout(t)
      if (!res.ok) return null
      const json: any = await res.json()
      return json?.code === 200 ? json.data : null
    } catch {
      return null
    }
  }

  const productsData: any = await safeFetch('/api/products?page=1&pageSize=1000&locale=en')
  if (productsData?.data) {
    for (const p of productsData.data) {
      if (p.slug) {
        dynamicUrls.push({
          loc: `/products/${p.slug}`,
          changefreq: 'weekly',
          priority: 0.7,
        })
      }
    }
  }

  const newsData: any = await safeFetch('/api/news/latest?limit=100&locale=en')
  if (Array.isArray(newsData)) {
    for (const n of newsData) {
      if (n.slug) {
        dynamicUrls.push({
          loc: `/news/${n.slug}`,
          changefreq: 'monthly',
          priority: 0.6,
        })
      }
    }
  }

  return [...staticUrls, ...dynamicUrls]
})
