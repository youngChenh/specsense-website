import { strict as assert } from 'node:assert'
import { readFileSync } from 'node:fs'
import vm from 'node:vm'
import { ref, reactive, computed, watch, nextTick } from 'vue'
import { parse, compileScript, compileTemplate } from '@vue/compiler-sfc'
import { transform } from 'esbuild'

const requestSource = await transform(readFileSync('app/utils/adminRequest.ts', 'utf8'), { loader: 'ts' })
function requestWith(fetch) {
  const context = vm.createContext({ $fetch: fetch })
  vm.runInContext(requestSource.code.replace(/^import .*$/gm, '').replace(/^export /gm, '') + '\nglobalThis.request = adminRequest', context)
  return context.request
}

async function loadComponent(file, bindings, expose) {
  const { descriptor, errors } = parse(readFileSync(file, 'utf8'), { filename: file })
  assert.deepEqual(errors, [])
  const script = compileScript(descriptor, { id: file })
  const template = compileTemplate({ source: descriptor.template.content, filename: file, id: file, compilerOptions: { bindingMetadata: script.bindings } })
  assert.deepEqual(template.errors, [])
  const output = await transform(descriptor.scriptSetup.content.replaceAll('import.meta.client', 'false'), { loader: descriptor.scriptSetup.lang === 'ts' ? 'ts' : 'js' })
  const context = vm.createContext({
    ref, reactive, computed, watch, nextTick,
    onMounted() {}, onBeforeUnmount() {}, definePageMeta() {}, clearNuxtData() {},
    useRuntimeConfig: () => ({ public: { apiBase: '/specsense' } }),
    useAdminI18n: () => ({ t: key => key }), useContentLocale: () => ref('en'),
    useImageUrl: () => ({ getImageUrl: value => value }),
    ...bindings,
  })
  vm.runInContext(output.code.replace(/^import .*$/gm, '') + `\nglobalThis.component = { ${expose.join(',')} };`, context)
  return context.component
}

// Exercise the actual response checker, including malformed responses and HTTP failures.
for (const response of [{ code: 401 }, { code: 500, message: '保存被拒绝' }, null, {}]) {
  await assert.rejects(requestWith(async () => response)('/test'))
}
await assert.rejects(requestWith(async () => { throw new Error('HTTP 500') })('/test'), /HTTP 500/)
assert.equal((await requestWith(async (_, options) => {
  assert.equal(options.retry, 0)
  return { code: 200 }
})('/test')).code, 200)

const pages = {
  downloads: [['save', 'saving', 'form', 'showDialog']],
  news: [['save', 'saving', 'form', 'showDialog']],
  applications: [['save', 'saving', 'form', 'showDialog']],
  services: [['save', 'saving', 'form', 'showDialog']],
  company: [['save', 'saving', 'form']],
  banners: [['save', 'saving', 'form', 'showDialog']],
  menus: [['saveMenu', 'saving', 'form', 'showDialog']],
  products: [['saveProduct', 'saving', 'form', 'showDialog']],
  categories: [['save', 'saving', 'form', 'showDialog']],
  brands: [['saveCategory', 'savingCategory', 'categoryForm', 'showCategoryDialog'], ['saveBrand', 'savingBrand', 'brandForm', 'showBrandDialog']],
}

for (const [page, handlers] of Object.entries(pages)) {
  const successes = [], errors = [], writes = []
  let reply = { code: 500, message: '保存被拒绝' }
  let pending
  let confirmation = 'cancel'
  let confirmations = 0
  const fetch = async (url, options) => {
    if (!options.method || options.method === 'GET') return { code: 200, data: [] }
    writes.push({ url, options })
    return pending || reply
  }
  const deletable = ['downloads', 'news'].includes(page)
  const expose = [...new Set(handlers.flat().filter(Boolean)), ...(deletable ? ['remove', 'deletingId'] : [])]
  const component = await loadComponent(`app/pages/admin/${page}.vue`, {
    adminRequest: requestWith(fetch),
    ElMessage: { success: text => successes.push(text), error: text => errors.push(text), warning: text => { throw new Error(text) } },
    ElMessageBox: { confirm: async () => { confirmations++; if (confirmation !== 'confirm') throw confirmation } },
  }, expose)
  for (const [handler, flag, form, dialog] of handlers) {
    Object.assign(component[form], { nameEn: 'Keep my input', key: 'test', slug: 'test', labelEn: 'Test', labelZh: '测试' })
    if (dialog) component[dialog].value = true
    const successCount = successes.length
    const errorCount = errors.length
    await component[handler]()
    assert.equal(successes.length, successCount, `${page}: failure must not report success`)
    assert.equal(errors.length, errorCount + 1)
    assert.match(errors.at(-1), /保存被拒绝/)
    assert.equal(component[form].nameEn, 'Keep my input')
    if (dialog) assert.equal(component[dialog].value, true)
    assert.equal(component[flag].value, false)

    let resolve
    pending = new Promise(done => { resolve = done })
    const before = writes.length
    const first = component[handler]()
    assert.equal(component[flag].value, true)
    await component[handler]()
    assert.equal(writes.length, before + 1, `${page}: duplicate submission blocked`)
    resolve({ code: 200 })
    await first
    pending = null
    assert.equal(component[flag].value, false)
    assert.equal(successes.length, successCount + 1)
    if (dialog) assert.equal(component[dialog].value, false)
  }
  if (deletable) {
    for (const cancel of ['cancel', 'close']) {
      confirmation = cancel
      const before = writes.length, errorCount = errors.length
      await component.remove(123)
      assert.equal(writes.length, before)
      assert.equal(errors.length, errorCount)
      assert.equal(component.deletingId.value, null)
    }
    confirmation = 'confirm'
    const successCount = successes.length
    await component.remove(123)
    assert.equal(successes.length, successCount, 'failed delete must not report success')
    let resolve
    pending = new Promise(done => { resolve = done })
    const before = writes.length, confirmationCount = confirmations
    const first = component.remove(123)
    await Promise.resolve()
    await component.remove(123)
    assert.equal(writes.length, before + 1)
    assert.equal(confirmations, confirmationCount + 1)
    resolve({ code: 200 })
    await first
    pending = null
    assert.equal(component.deletingId.value, null)
    assert.equal(successes.length, successCount + 1)
  }
  console.log(`PASS admin/${page}: failed saves retain input; successful saves; duplicate prevention${deletable ? '; delete confirmation/cancellation/failure' : ''}`)
}

const listeners = new Set()
let unmount
const sidebar = await loadComponent('app/components/ContactSidebar.vue', {
  document: { addEventListener: (_, handler) => listeners.add(handler), removeEventListener: (_, handler) => listeners.delete(handler) },
  onBeforeUnmount: callback => { unmount = callback },
}, ['toggleContact', 'toggleWhatsApp', 'toggleWeChat', 'toggleEmail', 'showContact', 'showWhatsApp', 'showWeChat', 'showEmail', 'wechatLightboxOpen', 'whatsappLightboxOpen', 'handleLightboxKeydown'])
const names = ['Contact', 'WhatsApp', 'WeChat', 'Email']
for (const name of names) {
  sidebar[`toggle${name}`]()
  assert.equal(sidebar[`show${name}`].value, true)
  sidebar[`toggle${name}`]()
  assert.equal(sidebar[`show${name}`].value, false)
  sidebar[`toggle${name}`]()
  assert.equal(names.filter(other => sidebar[`show${other}`].value).length, 1)
}
sidebar.wechatLightboxOpen.value = true
await nextTick()
assert.equal(listeners.size, 1)
sidebar.whatsappLightboxOpen.value = true
sidebar.wechatLightboxOpen.value = false
await nextTick()
assert.equal(listeners.size, 1, 'listener remains while either lightbox is open')
sidebar.handleLightboxKeydown({ key: 'Escape' })
await nextTick()
assert.equal(sidebar.whatsappLightboxOpen.value, false)
assert.equal(sidebar.wechatLightboxOpen.value, false)
assert.equal(listeners.size, 0)
sidebar.wechatLightboxOpen.value = true
await nextTick()
unmount()
assert.equal(listeners.size, 0)
console.log('PASS contact sidebar: toggle, exclusive panels, Escape close, listener cleanup')
