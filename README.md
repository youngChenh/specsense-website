# Nuxt Minimal Starter

Look at the [Nuxt documentation](https://nuxt.com/docs/getting-started/introduction) to learn more.

## Setup

Make sure to install dependencies:

```bash
# npm
npm install

# pnpm
pnpm install

# yarn
yarn install

# bun
bun install
```

## Development Server

Start the development server on `http://localhost:3000`:

```bash
# npm
npm run dev

# pnpm
pnpm dev

# yarn
yarn dev

# bun
bun run dev
```

## Production

Build the application for production:

```bash
# npm
npm run build

# pnpm
pnpm build

# yarn
yarn build

# bun
bun run build
```

Locally preview production build:

```bash
# npm
npm run preview

# pnpm
pnpm preview

# yarn
yarn preview

# bun
bun run preview
```

Check out the [deployment documentation](https://nuxt.com/docs/getting-started/deployment) for more information.

## Product SEO and deployment

Product lists (including category, search and pagination URLs) and product details
fetch data during Nuxt SSR. Initial HTML contains product links, names, descriptions,
specifications, SEO metadata and Product JSON-LD. Client navigation and interactive
controls continue to use Vue. Pagination URLs preserve the selected category/search.

Use the **Node server deployment**, not the legacy static-upload `deploy.bat` or
`npm run generate`: query-dependent category pages need a running Nuxt server.
The existing `nginx-deploy.conf` proxies frontend requests to port 3000.

- `NUXT_API_BASE`: absolute backend base URL accessible **from Nuxt**. Defaults to
  `http://localhost:8080/specsense` for a host installation. Docker Compose sets
  `http://api:8080/specsense` for the frontend container.
- `NUXT_PUBLIC_API_BASE`: browser API base, normally `/specsense` in production;
  Nginx forwards `/specsense/api/` to the backend.
- `NUXT_PUBLIC_SITE_URL`: canonical site origin, defaults to `https://www.spesense.com`.
  Use the same value for building and running the server so sitemap and canonical
  URLs agree.

For an existing Docker Compose deployment with the API already running:

```bash
docker compose up -d --build --no-deps frontend
```

For a host installation, build with `npm run build`, set the variables above,
then run `node .output/server/index.mjs` under the existing process manager.

The sitemap source walks all product pages instead of limiting the catalog to
the first 1,000 results. It reports backend failures rather than silently returning
an empty product list. Product details return HTTP 404 for missing products and
503 for temporary backend failures; list backend failures also return 503.

Run `npm run build` followed by `npm run test:seo` to verify raw production HTTP
responses against an isolated fixture backend (no live database or browser JS).
The checks cover category links, pagination, search, English/Chinese details,
H1/description/specifications, metadata, JSON-LD, sitemap pagination, and errors.
After deployment, repeat checks against the real catalog with View Source or
`curl`, including `/products?category=spectrometer`, an actual product URL, and
`/sitemap.xml`.

The current locale strategy uses one URL with a language cookie and defaults to
Chinese. These changes preserve that behavior; separate indexable English and
Chinese URLs would require a separate locale-routing migration.
JSON-LD contains real catalog fields only. No prices, ratings, stock availability,
or reviews are invented; adding Product markup alone does not guarantee Google
product rich results.
