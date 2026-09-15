// Run after npm run build. Uses fixture data and checks the production server's
// raw HTTP responses, without executing any browser JavaScript.
import assert from 'node:assert/strict'
import { createServer } from 'node:http'
import { spawn } from 'node:child_process'
import { once } from 'node:events'

const catalog = Array.from({ length: 105 }, (_, index) => ({
  id: index + 1,
  slug: index === 0 ? 'sm-3001b' : `spectrometer-${index + 1}`,
  nameEn: index === 0 ? 'SM-3001B High-Resolution Spectrometer' : `Spectrometer ${index + 1}`,
  nameZh: index === 0 ? 'SM-3001B 高分辨率光谱仪' : `光谱仪 ${index + 1}`,
  descriptionEn: 'Measure optical spectra with high resolution.',
  descriptionZh: '用于高分辨率光谱测量。',
  categoryKey: 'spectrometer',
  categoryName: 'Spectrometers',
  imageUrl: '/uploads/spectrometer.png',
  specs: { 'Wavelength range': '200–1100 nm' },
}))
// Real catalog slugs can contain spaces or Chinese characters.
catalog[1].slug = 'SM-3001B '
catalog[2].slug = '光栅单色仪'
let failProducts = false
const requests = []
const backend = createServer((req, res) => {
  const url = new URL(req.url, 'http://localhost')
  requests.push(url)
  res.setHeader('Content-Type', 'application/json')
  if (failProducts && url.pathname.startsWith('/specsense/api/products')) {
    res.writeHead(503).end(JSON.stringify({ code: 503 }))
    return
  }
  let data = []
  let code = 200
  if (url.pathname === '/specsense/api/products') {
    let products = catalog
    if (url.searchParams.has('category') && url.searchParams.get('category') !== 'spectrometer') products = []
    if (url.searchParams.has('keyword')) products = products.filter(p => p.nameEn.includes(url.searchParams.get('keyword')))
    const page = Number(url.searchParams.get('page') || 1)
    const pageSize = Number(url.searchParams.get('pageSize') || 12)
    data = { data: products.slice((page - 1) * pageSize, page * pageSize), total: products.length, page, pageSize }
  } else if (url.pathname.startsWith('/specsense/api/products/')) {
    data = catalog.find(p => p.slug === decodeURIComponent(url.pathname.split('/').pop())) || null
    if (!data) code = 404
  } else if (url.pathname === '/specsense/api/categories') {
    data = [{ key: 'spectrometer', nameEn: 'Spectrometers', nameZh: '光谱仪', children: [] }]
  } else if (url.pathname === '/specsense/api/news/latest') {
    data = [{ slug: 'Fiber Optic Spectrometers Explained' }]
  }
  res.end(JSON.stringify({ code, data }))
})
backend.listen(0, '127.0.0.1')
await once(backend, 'listening')
const apiPort = backend.address().port
const portProbe = createServer()
portProbe.listen(0, '127.0.0.1')
await once(portProbe, 'listening')
const port = portProbe.address().port
await new Promise(resolve => portProbe.close(resolve))
const base = `http://127.0.0.1:${port}`
let serverOutput = ''
const frontend = spawn(process.execPath, ['.output/server/index.mjs'], {
  env: {
    ...process.env,
    NODE_ENV: 'production',
    NITRO_HOST: '127.0.0.1',
    NITRO_PORT: String(port),
    NUXT_API_BASE: `http://127.0.0.1:${apiPort}/specsense`,
    NUXT_PUBLIC_API_BASE: '/specsense',
    NUXT_PUBLIC_SITE_URL: 'https://www.spesense.com',
  },
  stdio: ['ignore', 'pipe', 'pipe'],
  windowsHide: true,
})
frontend.stdout.on('data', data => { serverOutput += data })
frontend.stderr.on('data', data => { serverOutput += data })

const get = (path, locale = 'en') => fetch(`${base}${path}`, {
  headers: { cookie: `i18n_locale=${locale}` },
  signal: AbortSignal.timeout(20000),
})
const withoutScripts = html => html.replace(/<script\b[^>]*>[\s\S]*?<\/script>/gi, '')
const check = (label, run) => { run(); console.log(`PASS ${label}`) }

try {
  let ready = false
  for (let attempt = 0; attempt < 100; attempt++) {
    if (frontend.exitCode !== null) throw new Error(`Nuxt exited: ${serverOutput}`)
    try {
      await get('/robots.txt')
      ready = true
      break
    } catch { await new Promise(resolve => setTimeout(resolve, 200)) }
  }
  assert.ok(ready, `Nuxt failed to start: ${serverOutput}`)

  let response = await get('/products?category=spectrometer')
  assert.equal(response.status, 200)
  let html = await response.text()
  let markup = withoutScripts(html)
  check('category HTML includes product name inside a real detail link', () => {
    assert.match(markup, /<a\b[^>]*href="\/products\/sm-3001b"[^>]*>[\s\S]*?SM-3001B High-Resolution Spectrometer[\s\S]*?<\/a>/)
    assert.match(markup, /href="\/products\?category=spectrometer(?:&amp;|&)page=2"/)
    assert.match(markup, /rel="canonical"[^>]*href="https:\/\/www.spesense.com\/products\?category=spectrometer"/)
  })

  response = await get('/products?category=spectrometer&page=2')
  markup = withoutScripts(await response.text())
  check('direct pagination request renders the correct products', () => {
    assert.match(markup, /Spectrometer 13/)
    assert.doesNotMatch(markup, /SM-3001B High-Resolution Spectrometer/)
  })

  response = await get('/products?category=spectrometer&q=SM-3001B')
  markup = withoutScripts(await response.text())
  check('search query is rendered on the server', () => {
    assert.match(markup, /SM-3001B High-Resolution Spectrometer/)
    assert.doesNotMatch(markup, /href="\/products\/spectrometer-2"/)
    assert.match(markup, /name="robots"[^>]*content="noindex, follow"/)
  })

  response = await get('/products/sm-3001b?tracking=test')
  assert.equal(response.status, 200)
  html = await response.text()
  markup = withoutScripts(html)
  check('detail initial HTML includes H1, description, specs and unique metadata', () => {
    assert.match(markup, /<h1\b[^>]*>SM-3001B High-Resolution Spectrometer<\/h1>/)
    assert.match(markup, /<p\b[^>]*>Measure optical spectra with high resolution\.<\/p>/)
    assert.match(markup, /<td\b[^>]*>200–1100 nm<\/td>/)
    assert.match(markup, /<title>SM-3001B High-Resolution Spectrometer \| SpeSense<\/title>/)
    assert.match(markup, /name="description"[^>]*content="Measure optical spectra with high resolution\."/)
    assert.match(markup, /rel="canonical"[^>]*href="https:\/\/www.spesense.com\/products\/sm-3001b"/)
    assert.equal((markup.match(/rel="canonical"/g) || []).length, 1)
  })
  check('Product JSON-LD matches visible content', () => {
    const scripts = [...html.matchAll(/<script\b[^>]*type="application\/ld\+json"[^>]*>([\s\S]*?)<\/script>/g)]
    const product = scripts.map(match => JSON.parse(match[1])).find(item => item['@type'] === 'Product')
    assert.equal(product.name, catalog[0].nameEn)
    assert.equal(product.url, 'https://www.spesense.com/products/sm-3001b')
    assert.equal(product.additionalProperty[0].value, '200–1100 nm')
    assert.equal(product.image[0], 'https://www.spesense.com/specsense/uploads/spectrometer.png')
    assert.equal(product.offers, undefined)
  })

  response = await get('/products/sm-3001b', 'zh')
  markup = withoutScripts(await response.text())
  check('Chinese locale is rendered before JavaScript executes', () => {
    assert.match(markup, /<h1\b[^>]*>SM-3001B 高分辨率光谱仪<\/h1>/)
  })
  response = await get('/products/missing-product')
  check('missing product returns HTTP 404', () => assert.equal(response.status, 404))

  response = await get('/api/sitemap/urls')
  const urls = await response.json()
  check('sitemap source traverses every product API page', () => {
    assert.equal(response.status, 200)
    assert.equal(urls.filter(item => item.loc.startsWith('/products/')).length, catalog.length)
    assert.ok(urls.some(item => item.loc === '/products/spectrometer-105'))
    assert.ok(requests.some(url => url.searchParams.get('pageSize') === '100' && url.searchParams.get('page') === '2'))
  })
  response = await get('/sitemap.xml')
  let xml = await response.text()
  if (xml.includes('<sitemapindex')) {
    const locations = [...xml.matchAll(/<loc>(.*?)<\/loc>/g)].map(match => match[1])
    xml = (await Promise.all(locations.map(async loc => (await get(new URL(loc).pathname)).text()))).join('\n')
  }
  check('public sitemap XML includes first and last product URLs', () => {
    assert.match(xml, /<loc>https:\/\/www.spesense.com\/products\/sm-3001b<\/loc>/)
    assert.match(xml, /<loc>https:\/\/www.spesense.com\/products\/spectrometer-105<\/loc>/)
  })
  check('sitemap encodes spaces and Chinese slugs exactly once', () => {
    for (const product of [catalog[1], catalog[2]]) {
      assert.ok(xml.includes(`<loc>https://www.spesense.com/products/${encodeURIComponent(product.slug)}</loc>`))
    }
    assert.ok(xml.includes('<loc>https://www.spesense.com/news/Fiber%20Optic%20Spectrometers%20Explained</loc>'))
    assert.doesNotMatch(xml, /%2520|%25E5/)
  })
  for (const product of [catalog[1], catalog[2]]) {
    const path = new URL([...xml.matchAll(/<loc>(.*?)<\/loc>/g)].find(match => match[1].endsWith(encodeURIComponent(product.slug)))[1]).pathname
    response = await get(path)
    check(`sitemap URL resolves to a product: ${product.slug}`, () => assert.equal(response.status, 200))
  }

  failProducts = true
  response = await get('/products/sm-3001b')
  check('backend outage returns HTTP 503 for details', () => assert.equal(response.status, 503))
  response = await get('/products?category=spectrometer')
  check('backend outage returns HTTP 503 for the list', () => assert.equal(response.status, 503))
  response = await get('/api/sitemap/urls')
  check('sitemap source reports an outage instead of an empty catalog', () => assert.equal(response.status, 503))
} catch (error) {
  console.error(serverOutput.slice(-6000))
  throw error
} finally {
  frontend.kill()
  backend.closeAllConnections()
  await new Promise(resolve => backend.close(resolve))
}
