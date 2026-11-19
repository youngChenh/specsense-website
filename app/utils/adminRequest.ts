import { $fetch } from 'ofetch'

interface AdminResponse<T = any> {
  code: number
  message?: string
  data?: T
}

// Admin endpoints can return HTTP 200 with a failed business status.
export async function adminRequest<T = any>(url: string, options: Parameters<typeof $fetch>[1] = {}): Promise<AdminResponse<T>> {
  const result = await $fetch<AdminResponse<T>>(url, { ...options, retry: 0 })
  if (result?.code !== 200) {
    throw new Error(result?.code === 401
      ? '登录已过期，请重新登录后再试'
      : result?.message || '操作失败，请稍后重试')
  }
  return result
}
