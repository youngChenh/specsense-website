import { strict as assert } from 'node:assert'
import { readFileSync } from 'node:fs'
import vm from 'node:vm'
import * as Vue from 'vue'
import { renderToString } from 'vue/server-renderer'
import { parse, compileScript, compileTemplate } from '@vue/compiler-sfc'
import { compile } from '@vue/compiler-dom'
import { transform } from 'esbuild'
import { createI18n } from 'vue-i18n'

const messages = Object.fromEntries(['en', 'zh'].map(locale => [locale, JSON.parse(readFileSync(`i18n/locales/${locale}.json`, 'utf8'))]))
const i18n = createI18n({ legacy: false, locale: 'en', messages })
for (const locale of ['en', 'zh']) {
  i18n.global.locale.value = locale
  for (const key of ['common.retry', 'common.loading', 'common.previous', 'common.next', 'common.unknownSize', 'products.searchLabel', 'products.emptyCategory', 'products.loadFailed', 'news.empty', 'news.loadFailed', 'services.empty', 'services.loadFailed', 'services.downloadsEmpty', 'services.downloadsFailed']) {
    assert(i18n.global.te(key), `${locale}: missing ${key}`)
  }
  assert(i18n.global.t('products.emptySearch', { query: 'soil sensor' }).includes('soil sensor'))
}
const productSource = readFileSync('app/pages/products/index.vue', 'utf8')
assert(!productSource.includes("searchQuery ? '搜索'"))
assert(productSource.includes("$t('products.emptySearch', { query: searchQuery })"))

function deferred() {
  let resolve, reject
  const promise = new Promise((yes, no) => { resolve = yes; reject = no })
  return { promise, resolve, reject }
}

async function load(file, exposed) {
  const { descriptor, errors } = parse(readFileSync(file, 'utf8'), { filename: file })
  assert.deepEqual(errors, [])
  const script = compileScript(descriptor, { id: file })
  assert.deepEqual(compileTemplate({ source: descriptor.template.content, filename: file, id: file, compilerOptions: { bindingMetadata: script.bindings } }).errors, [])
  const output = await transform(descriptor.scriptSetup.content, { loader: 'ts' })
  const locale = Vue.ref('en'), calls = [], unmount = []
  const scope = Vue.effectScope()
  const context = vm.createContext({
    ...Vue,
    useContentLocale: () => locale, useLocalePath: () => value => value,
    useRuntimeConfig: () => ({ public: { apiBase: '/specsense' } }),
    useImageUrl: () => ({ getImageUrl: value => value }),
    onMounted() {}, onBeforeUnmount: fn => unmount.push(fn),
    $fetch: (url, options) => { const pending = deferred(); calls.push({ url, options, ...pending }); return pending.promise },
  })
  scope.run(() => vm.runInContext(output.code + `\nglobalThis.page = { ${exposed.join(',')} };`, context))
  const page = context.page
  const render = new Function('Vue', compile(descriptor.template.content, { mode: 'function', prefixIdentifiers: true }).code)(Vue)
  const html = async () => {
    const app = Vue.createSSRApp({ setup: () => page, render })
    app.config.warnHandler = message => { throw new Error(message) }
    app.component('NuxtLink', { props: ['to'], setup: (props, { slots }) => () => Vue.h('a', { href: props.to }, slots.default?.()) })
    app.config.globalProperties.$t = i18n.global.t
    return renderToString(app)
  }
  return { page, locale, calls, html, close: () => { unmount.forEach(fn => fn()); scope.stop() } }
}

i18n.global.locale.value = 'en'
const news = await load('app/pages/news/index.vue', ['localePath', 'activeTab', 'tabs', 'loading', 'loadFailed', 'filteredNews', 'getImageUrl', 'getTitle', 'getExcerpt', 'currentPage', 'totalPages', 'fetchNews', 'news'])
assert((await news.html()).includes('Loading...'))
assert(!(await news.html()).includes('No news in this category yet.'))
let task = news.page.fetchNews()
news.calls.at(-1).resolve({ code: 500 })
await task
assert((await news.html()).includes('Failed to load news'))
assert(!(await news.html()).includes('No news in this category yet.'))
task = news.page.fetchNews()
assert.equal(news.page.loadFailed.value, false)
news.calls.at(-1).resolve({ code: 200, data: { data: [], total: 0 } })
await task
assert((await news.html()).includes('No news in this category yet.'))
const oldNews = news.page.fetchNews(), oldCall = news.calls.at(-1)
const freshNews = news.page.fetchNews(), freshCall = news.calls.at(-1)
freshCall.resolve({ code: 200, data: { data: [{ id: 2, slug: 'new', titleEn: 'Latest news' }], total: 1 } })
await freshNews
oldCall.reject(new Error('stale failure'))
await oldNews
assert.equal(news.page.news.value[0].id, 2)
assert.equal(news.page.loadFailed.value, false)
assert((await news.html()).includes('Latest news'))
news.page.currentPage.value = 3
await Vue.nextTick()
const count = news.calls.length
news.page.activeTab.value = 'company'
await Vue.nextTick()
assert.equal(news.calls.length, count + 1, 'category change fetches only once')
assert.equal(news.calls.at(-1).options.query.page, 1)
assert.equal(news.calls.at(-1).options.query.category, 'company')
news.close()
console.log('PASS news: loading/error/empty/results render separately; retry; stale responses; category reset')

const service = await load('app/pages/services/index.vue', ['localePath', 'services', 'servicesLoading', 'servicesFailed', 'fetchServices', 'downloadCategories', 'activeCategory', 'isLoading', 'downloadsFailed', 'fetchDownloads', 'paginatedDownloads', 'filteredDownloads', 'getFullDownloadUrl', 'totalPages', 'currentPage', 'goToPage', 'downloads'])
assert((await service.html()).includes('Loading...'))
assert(!(await service.html()).includes('No files in this category.'))
for (const [handler, loading, failed, errorText, emptyText] of [
  ['fetchDownloads', 'isLoading', 'downloadsFailed', 'Failed to load downloads', 'No files in this category.'],
  ['fetchServices', 'servicesLoading', 'servicesFailed', 'Failed to load services', 'No services available yet.'],
]) {
  for (const result of [null, { code: 500 }, { code: 200, data: {} }]) {
    task = service.page[handler]()
    service.calls.at(-1).resolve(result)
    await task
    assert.equal(service.page[loading].value, false)
    assert.equal(service.page[failed].value, true)
    const html = await service.html()
    assert(html.includes(errorText))
    assert(!html.includes(emptyText))
  }
  task = service.page[handler]()
  service.calls.at(-1).resolve({ code: 200, data: [] })
  await task
  assert((await service.html()).includes(emptyText))
}
const older = service.page.fetchDownloads(), olderCall = service.calls.at(-1)
const newer = service.page.fetchDownloads(), newerCall = service.calls.at(-1)
newerCall.resolve({ code: 200, data: [{ id: 1, name: 'Legacy file', category: 'manuals', fileUrl: '/uploads/old.pdf', fileSize: '59 MB' }, { id: 2, name: 'External', fileSize: '' }] })
await newer
olderCall.resolve({ code: 200, data: [] })
await older
let html = await service.html()
assert(html.includes('59 MB'))
assert(html.includes('Unknown'))
assert.equal(service.page.downloads.value.length, 2)
i18n.global.locale.value = 'zh'
html = await service.html()
assert(html.includes('未知'))
task = service.page.fetchDownloads()
service.calls.at(-1).reject(new Error('network timeout'))
await task
assert((await service.html()).includes('下载文件列表加载失败'))
assert(!(await service.html()).includes('该分类下暂无文件'))
service.close()
console.log('PASS services/downloads: independent loading/failure/retry; empty states; sizes; translated fallback; stale responses')
