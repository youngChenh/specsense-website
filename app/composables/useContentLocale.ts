// The public Chinese URL is /cn; the content API and data fields still use zh.
export const useContentLocale = () => {
  const { locale } = useI18n()
  return computed(() => locale.value === 'cn' ? 'zh' : 'en')
}
