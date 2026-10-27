export const CRM_SOURCES = [
  { value: 'website', label: '官网' },
  { value: 'exhibition', label: '展会' },
  { value: 'outbound', label: '主动开发' },
  { value: 'existing', label: '现有客户' },
  { value: 'social', label: '社交媒体宣传' },
]

export const CRM_STAGES = [
  { value: 'NEW', label: '新询盘' },
  { value: 'FOLLOWING', label: '跟进中' },
  { value: 'QUOTED', label: '已报价' },
  { value: 'NEGOTIATING', label: '洽谈中' },
  { value: 'WON', label: '赢单' },
  { value: 'LOST', label: '输单' },
]

export const CRM_STATUS = [
  { value: 'potential', label: '潜在' },
  { value: 'active', label: '活跃' },
  { value: 'closed', label: '已关闭' },
]

export const CRM_METHODS = [
  { value: 'email', label: '邮件' },
  { value: 'phone', label: '电话' },
  { value: 'whatsapp', label: 'WhatsApp' },
  { value: 'meeting', label: '会议' },
  { value: 'other', label: '其他' },
]

export const stageLabel = (v?: string) => CRM_STAGES.find(s => s.value === v)?.label || v || '-'
export const sourceLabel = (v?: string) => CRM_SOURCES.find(s => s.value === v)?.label || v || '-'
export const methodLabel = (v?: string) => CRM_METHODS.find(s => s.value === v)?.label || v || '-'
export const statusLabel = (v?: string) => CRM_STATUS.find(s => s.value === v)?.label || v || '-'

export const parseAttachments = (raw?: string) => {
  if (!raw) return []
  try {
    const data = JSON.parse(raw)
    return Array.isArray(data) ? data : []
  } catch {
    return []
  }
}

export const stringifyAttachments = (list: any[]) => JSON.stringify(list || [])

export const useCrmApi = () => {
  const config = useRuntimeConfig()
  const base = () => `${config.public.apiBase}/api/admin/crm`

  const headers = () => {
    const token = import.meta.client ? localStorage.getItem('admin_token') : ''
    return { Authorization: `Bearer ${token}` }
  }

  const get = (url: string, params?: any) => $fetch(url, { headers: headers(), params })
  const send = (url: string, method: string, body?: any) => $fetch(url, { method, headers: headers(), body })

  return {
    headers,
    base,
    dashboard: () => get(`${base()}/dashboard`),
    countries: () => get(`${base()}/countries`),
    customers: (params: any) => get(`${base()}/customers`, params),
    customer: (id: number) => get(`${base()}/customers/${id}`),
    createCustomer: (body: any) => send(`${base()}/customers`, 'POST', body),
    updateCustomer: (id: number, body: any) => send(`${base()}/customers/${id}`, 'PUT', body),
    closeCustomer: (id: number) => send(`${base()}/customers/${id}/close`, 'PUT'),
    mergeCustomers: (fromId: number, toId: number) => send(`${base()}/customers/merge`, 'POST', { fromId, toId }),
    duplicates: (body: any) => send(`${base()}/customers/duplicates`, 'POST', body),
    contacts: (customerId: number) => get(`${base()}/customers/${customerId}/contacts`),
    createContact: (body: any) => send(`${base()}/contacts`, 'POST', body),
    updateContact: (id: number, body: any) => send(`${base()}/contacts/${id}`, 'PUT', body),
    inquiries: (params: any) => get(`${base()}/inquiries`, params),
    inquiry: (id: number) => get(`${base()}/inquiries/${id}`),
    createInquiry: (body: any) => send(`${base()}/inquiries`, 'POST', body),
    updateInquiry: (id: number, body: any) => send(`${base()}/inquiries/${id}`, 'PUT', body),
    updateStage: (id: number, stage: string) => send(`${base()}/inquiries/${id}/stage`, 'PUT', { stage }),
    assignOwner: (id: number, ownerId: number | null) => send(`${base()}/inquiries/${id}/owner`, 'PUT', { ownerId }),
    byWebsiteInquiry: (websiteInquiryId: number, email?: string) => get(`${base()}/inquiries/website/${websiteInquiryId}`, { email }),
    closeInquiry: (id: number) => send(`${base()}/inquiries/${id}/close`, 'PUT'),
    followUps: (params: any) => get(`${base()}/follow-ups`, params),
    createFollowUp: (body: any) => send(`${base()}/follow-ups`, 'POST', body),
    users: (all = true) => get(`${base()}/users`, { all }),
    createUser: (body: any) => send(`${base()}/users`, 'POST', body),
    updateUser: (id: number, body: any) => send(`${base()}/users/${id}`, 'PUT', body),
    disableUser: (id: number) => send(`${base()}/users/${id}/disable`, 'PUT'),
    enableUser: (id: number) => send(`${base()}/users/${id}/enable`, 'PUT'),
    resetPassword: (id: number, password: string) => send(`${base()}/users/${id}/reset-password`, 'PUT', { password }),
  }
}

export const getAdminRole = () => {
  if (!import.meta.client) return ''
  return localStorage.getItem('admin_role') || ''
}

export const isCrmManager = () => {
  const role = getAdminRole()
  return role === 'manager' || role === 'admin'
}

export const isCmsAdmin = () => {
  const role = getAdminRole()
  return role === 'admin' || role === 'editor'
}
