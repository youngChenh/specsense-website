import { useRouter } from 'vue-router'

export const useAuthFetch = () => {
  const router = useRouter()

  const authFetch = async <T = any>(url: string, options: any = {}): Promise<T> => {
    // 确保抛出 HTTP 错误
    const fetchOptions = {
      ...options,
      throwHttpErrors: true,
    }

    try {
      const result = await $fetch<T>(url, fetchOptions)
      return result
    } catch (error: any) {
      // 401 跳转到登录页
      if (error?.response?.status === 401 || error?.status === 401) {
        if (typeof window !== 'undefined' && window.location.pathname !== '/login') {
          router.push('/login')
        }
      }
      throw error
    }
  }

  return {
    authFetch,
  }
}
