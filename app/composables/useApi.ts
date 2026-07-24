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
    const { data } = await useFetch(`${baseUrl}/api/categories`, {
      params: { locale },
    })
    return data.value?.data || []
  }

  // Products
  const fetchProducts = async (params: {
    page?: number
    pageSize?: number
    category?: string
    featured?: boolean
    locale?: string
  } = {}) => {
    const { data } = await useFetch(`${baseUrl}/api/products`, {
      params: {
        page: params.page || 1,
        pageSize: params.pageSize || 12,
        category: params.category,
        featured: params.featured,
        locale: params.locale || 'en',
      },
    })
    return data.value
  }

  const fetchProduct = async (slug: string, locale = 'en') => {
    const { data } = await useFetch(`${baseUrl}/api/products/${slug}`, {
      params: { locale },
    })
    return data.value?.data
  }

  const fetchFeaturedProducts = async (limit = 4, locale = 'en') => {
    const { data } = await useFetch(`${baseUrl}/api/products/featured`, {
      params: { limit, locale },
    })
    return data.value?.data || []
  }

  // Brands
  const fetchBrandCategories = async (locale = 'en') => {
    const { data } = await useFetch(`${baseUrl}/api/brands/categories`, {
      params: { locale },
    })
    return data.value?.data || []
  }

  const fetchBrands = async (params: {
    page?: number
    pageSize?: number
    category?: string
    featured?: boolean
    locale?: string
  } = {}) => {
    const { data } = await useFetch(`${baseUrl}/api/brands`, {
      params: {
        page: params.page || 1,
        pageSize: params.pageSize || 50,
        category: params.category,
        featured: params.featured,
        locale: params.locale || 'en',
      },
    })
    return data.value
  }

  const fetchFeaturedBrands = async (limit = 8, locale = 'en') => {
    const { data } = await useFetch(`${baseUrl}/api/brands/featured`, {
      params: { limit, locale },
    })
    return data.value?.data || []
  }

  // News
  const fetchNews = async (params: {
    page?: number
    pageSize?: number
    category?: string
    locale?: string
  } = {}) => {
    const { data } = await useFetch(`${baseUrl}/api/news`, {
      params: {
        page: params.page || 1,
        pageSize: params.pageSize || 10,
        category: params.category,
        locale: params.locale || 'en',
      },
    })
    return data.value
  }

  const fetchNewsItem = async (slug: string, locale = 'en') => {
    const { data } = await useFetch(`${baseUrl}/api/news/${slug}`, {
      params: { locale },
    })
    return data.value?.data
  }

  const fetchLatestNews = async (limit = 3, locale = 'en') => {
    const { data } = await useFetch(`${baseUrl}/api/news/latest`, {
      params: { limit, locale },
    })
    return data.value?.data || []
  }

  // Applications
  const fetchApplications = async (locale = 'en') => {
    const { data } = await useFetch(`${baseUrl}/api/applications`, {
      params: { locale },
    })
    return data.value?.data || []
  }

  // Services
  const fetchServices = async (locale = 'en') => {
    const { data } = await useFetch(`${baseUrl}/api/services`, {
      params: { locale },
    })
    return data.value?.data || []
  }

  // Downloads
  const fetchDownloads = async (category?: string, locale = 'en') => {
    const { data } = await useFetch(`${baseUrl}/api/downloads`, {
      params: { category, locale },
    })
    return data.value?.data || []
  }

  // Company Info
  const fetchCompanyInfo = async (locale = 'en') => {
    const { data } = await useFetch(`${baseUrl}/api/company-info`, {
      params: { locale },
    })
    return data.value?.data
  }

  // Navigation
  const fetchNavigation = async (position: string) => {
    const { data } = await useFetch(`${baseUrl}/api/navigation/${position}`)
    return data.value?.data || []
  }

  // Contact
  const submitContact = async (form: {
    name: string
    email: string
    company?: string
    productInterest?: string
    message: string
  }) => {
    const { data, error } = await useFetch(`${baseUrl}/api/contact`, {
      method: 'POST',
      body: form,
    })
    if (error.value) {
      throw new Error(error.value.message || 'Failed to submit')
    }
    return data.value
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
