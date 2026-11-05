import { useI18n } from 'vue-i18n'
import zhMessages from '~~/i18n/locales/zh.json'

// Admin stays Chinese without changing the public locale or triggering navigation.
export const useAdminI18n = () => useI18n({
  useScope: 'local',
  inheritLocale: false,
  locale: 'zh',
  messages: { zh: { admin: zhMessages.admin } },
})
