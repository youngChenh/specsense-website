export const useImageUrl = () => {
  const config = useRuntimeConfig()

  const getImageUrl = (url: string | null | undefined): string => {
    if (!url) return ''

    // If it's already an absolute URL (http:// or https://), return as is
    if (url.startsWith('http://') || url.startsWith('https://')) {
      return url
    }

    // If it's a local path starting with /uploads/, prepend the API base URL
    if (url.startsWith('/uploads/')) {
      const baseUrl = config.public.apiBase
      // Remove /api from the end if present
      const apiBase = baseUrl.replace(/\/api$/, '')
      return `${apiBase}${url}`
    }

    // For any other relative path
    if (url.startsWith('/')) {
      const baseUrl = config.public.apiBase
      const apiBase = baseUrl.replace(/\/api$/, '')
      return `${apiBase}${url}`
    }

    return url
  }

  return {
    getImageUrl,
  }
}
