export const useApi = () => {
  const config = useRuntimeConfig()
  const baseUrl = config.public.apiBase

  const getHeaders = () => {
    const headers = {
      'Content-Type': 'application/json',
    }
    return headers
  }

  // Categories
  const fetchCategories = async (locale = 'en') => {
    const data = await $fetch(`${baseUrl}/api/categories`, {
      params: { locale },
    })
    return (data as any)?.data || []
  }

  // Products
  const fetchProducts = async (params: {
    page?: number
    pageSize?: number
    category?: string
    featured?: boolean
    locale?: string
  } = {}) => {
    const data = await $fetch(`${baseUrl}/api/products`, {
      params: {
        page: params.page || 1,
        pageSize: params.pageSize || 12,
        category: params.category,
        featured: params.featured,
        locale: params.locale || 'en',
      },
    })
    return data
  }

  const fetchProduct = async (slug: string, locale = 'en') => {
    const data = await $fetch(`${baseUrl}/api/products/${encodeURIComponent(slug)}`, {
      params: { locale },
    })
    return (data as any)?.data
  }

  const fetchFeaturedProducts = async (limit = 4, locale = 'en') => {
    const data = await $fetch(`${baseUrl}/api/products/featured`, {
      params: { limit, locale },
    })
    return (data as any)?.data || []
  }

  // Brands
  const fetchBrandCategories = async (locale = 'en') => {
    const data = await $fetch(`${baseUrl}/api/brands/categories`, {
      params: { locale },
    })
    return (data as any)?.data || []
  }

  const fetchBrands = async (params: {
    page?: number
    pageSize?: number
    category?: string
    featured?: boolean
    locale?: string
  } = {}) => {
    const data = await $fetch(`${baseUrl}/api/brands`, {
      params: {
        page: params.page || 1,
        pageSize: params.pageSize || 50,
        category: params.category,
        featured: params.featured,
        locale: params.locale || 'en',
      },
    })
    return data
  }

  const fetchFeaturedBrands = async (limit = 8, locale = 'en') => {
    const data = await $fetch(`${baseUrl}/api/brands/featured`, {
      params: { limit, locale },
    })
    return (data as any)?.data || []
  }

  // News
  const fetchNews = async (params: {
    page?: number
    pageSize?: number
    category?: string
    locale?: string
  } = {}) => {
    const data = await $fetch(`${baseUrl}/api/news`, {
      params: {
        page: params.page || 1,
        pageSize: params.pageSize || 10,
        category: params.category,
        locale: params.locale || 'en',
      },
    })
    return data
  }

  const fetchNewsItem = async (slug: string, locale = 'en') => {
    const data = await $fetch(`${baseUrl}/api/news/${encodeURIComponent(slug)}`, {
      params: { locale },
    })
    return (data as any)?.data
  }

  const fetchLatestNews = async (limit = 3, locale = 'en') => {
    const data = await $fetch(`${baseUrl}/api/news/latest`, {
      params: { limit, locale },
    })
    return (data as any)?.data || []
  }

  // Applications
  const fetchApplications = async (locale = 'en') => {
    const data = await $fetch(`${baseUrl}/api/applications`, {
      params: { locale },
    })
    return (data as any)?.data || []
  }

  // Services
  const fetchServices = async (locale = 'en') => {
    const data = await $fetch(`${baseUrl}/api/services`, {
      params: { locale },
    })
    return (data as any)?.data || []
  }

  // Downloads
  const fetchDownloads = async (category?: string, locale = 'en') => {
    const data = await $fetch(`${baseUrl}/api/downloads`, {
      params: { category, locale },
    })
    return (data as any)?.data || []
  }

  // Company Info
  const fetchCompanyInfo = async (locale = 'en') => {
    const data = await $fetch(`${baseUrl}/api/company-info`, {
      params: { locale },
    })
    return (data as any)?.data
  }

  // Navigation
  const fetchNavigation = async (position: string) => {
    const data = await $fetch(`${baseUrl}/api/navigation/${position}`)
    return (data as any)?.data || []
  }

  // Contact
  const submitContact = async (form: {
    name: string
    email: string
    company?: string
    productInterest?: string
    message: string
  }) => {
    const data = await $fetch(`${baseUrl}/api/contact`, {
      method: 'POST',
      body: form,
    })
    return data
  }

  return {
    fetchCategories,
    fetchProducts,
    fetchProduct,
    fetchFeaturedProducts,
    fetchBrandCategories,
    fetchBrands,
    fetchFeaturedBrands,
    fetchNews,
    fetchNewsItem,
    fetchLatestNews,
    fetchApplications,
    fetchServices,
    fetchDownloads,
    fetchCompanyInfo,
    fetchNavigation,
    submitContact,
  }
}
