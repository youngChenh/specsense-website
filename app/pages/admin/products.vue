<template>
  <div class="page-container">
    <!-- Page Header -->
    <div class="page-header mb-6">
      <div class="header-content">
        <div class="header-icon">
          <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
          </svg>
        </div>
        <div class="header-text">
          <h1 class="text-2xl font-bold text-gray-800">{{ $t('admin.products') }}</h1>
          <p class="text-gray-500 text-sm mt-1">管理您的产品分类和列表</p>
        </div>
      </div>
      <el-button type="primary" @click="addProduct" class="add-btn">
        <svg class="w-4 h-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        {{ $t('admin.add') }}
      </el-button>
    </div>

    <el-card shadow="never" class="main-card">
      <el-tree
        v-if="treeData.length > 0"
        :data="treeData"
        :props="treeProps"
        node-key="id"
        default-expand-all
        :expand-on-click-node="false"
        class="product-tree"
      >
        <template #default="{ node, data }">
          <span class="tree-node">
            <span class="node-content">
              <!-- Category Node -->
              <template v-if="data.isCategory">
                <span class="category-icon">
                  <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z" />
                  </svg>
                </span>
                <span class="category-label">
                  <span class="font-medium text-gray-800">{{ data.nameEn }}</span>
                  <span class="text-gray-400 mx-2">/</span>
                  <span class="text-gray-500">{{ data.nameZh }}</span>
                </span>
                <el-tag size="small" type="info" effect="light" class="ml-3">{{ data.productCount }} {{ $t('admin.products').toLowerCase() }}</el-tag>
              </template>
              <!-- Product Node -->
              <template v-else>
                <span class="product-label">
                  <span class="text-gray-700">{{ data.nameEn }}</span>
                  <span class="text-gray-400 mx-2">/</span>
                  <span class="text-gray-500">{{ data.nameZh }}</span>
                </span>
                <el-tag v-if="data.featured" size="small" type="success" effect="light" class="ml-3">精选</el-tag>
              </template>
            </span>
            <span class="node-actions">
              <template v-if="data.isCategory">
                <el-button size="small" text type="primary" @click="addProductUnderCategory(data.id)">
                  <span class="text-xs">添加产品</span>
                </el-button>
              </template>
              <template v-else>
                <el-button size="small" text type="primary" @click="editProduct(data)">编辑</el-button>
                <el-button size="small" text type="danger" @click="removeProduct(data.id)">删除</el-button>
              </template>
            </span>
          </span>
        </template>
      </el-tree>

      <!-- Empty State -->
      <div v-else class="empty-state">
        <svg class="w-16 h-16 text-gray-300 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
        </svg>
        <p class="text-gray-500">暂无产品，请添加您的第一个产品</p>
      </div>
    </el-card>

    <!-- Product Dialog -->
    <el-dialog
      v-model="showDialog"
      :title="isEdit ? $t('admin.edit') + ' ' + $t('admin.product') : $t('admin.add') + ' ' + $t('admin.product')"
      width="800px"
    >
      <el-form :model="form" label-width="120px">
        <el-form-item :label="$t('admin.category')">
          <el-select v-model="form.categoryId" :placeholder="$t('admin.pleaseSelect')" class="w-full">
            <el-option
              v-for="cat in flatCategories"
              :key="cat.id"
              :label="cat.nameEn + ' / ' + cat.nameZh"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('admin.nameEn')">
          <el-input v-model="form.nameEn" :placeholder="$t('admin.pleaseEnter') + $t('admin.nameEn')" />
        </el-form-item>
        <el-form-item :label="$t('admin.nameZh')">
          <el-input v-model="form.nameZh" :placeholder="$t('admin.pleaseEnter') + $t('admin.nameZh')" />
        </el-form-item>
        <el-form-item :label="$t('admin.slug')">
          <el-input v-model="form.slug" :placeholder="$t('admin.pleaseEnter') + $t('admin.slug')" />
        </el-form-item>
        <el-form-item :label="$t('admin.descriptionEn')">
          <el-input v-model="form.descriptionEn" type="textarea" rows="3" :placeholder="$t('admin.pleaseEnter') + $t('admin.descriptionEn')" />
        </el-form-item>
        <el-form-item :label="$t('admin.descriptionZh')">
          <el-input v-model="form.descriptionZh" type="textarea" rows="3" :placeholder="$t('admin.pleaseEnter') + $t('admin.descriptionZh')" />
        </el-form-item>
        <el-form-item :label="$t('admin.imageUrl')">
          <div class="flex items-center gap-4">
            <el-upload
              :action="`${config.public.apiBase}/api/admin/upload`"
              :headers="headers"
              :show-file-list="false"
              :on-success="handleImageUploadSuccess"
              :on-progress="(evt, file, fileList) => handleUploadProgress(evt, file, fileList, 'image')"
              :before-upload="beforeImageUpload"
              accept="image/*"
            >
              <el-button type="primary" plain :disabled="uploading">{{ $t('admin.uploadImage') }}</el-button>
            </el-upload>
            <el-input v-model="form.imageUrl" :placeholder="$t('admin.imageUrlPlaceholder')" class="flex-1" />
          </div>
          <div v-if="uploading && uploadType === 'image'" class="mt-2">
            <el-progress :percentage="uploadPercentage" :stroke-width="6" />
          </div>
          <div v-if="form.imageUrl" class="mt-2">
            <img :src="getFullImageUrl(form.imageUrl)" class="w-32 h-32 object-cover rounded border" />
          </div>
        </el-form-item>

        <!-- Multi-image Upload -->
        <el-form-item label="产品轮播图片">
          <el-upload
            :action="`${config.public.apiBase}/api/admin/upload`"
            :headers="headers"
            :on-success="handleMultiImageSuccess"
            :on-progress="(evt, file, fileList) => handleUploadProgress(evt, file, fileList, 'multi')"
            :before-upload="beforeImageUpload"
            accept="image/*"
            multiple
            :show-file-list="false"
            class="mb-2"
          >
            <el-button type="primary" plain size="small" :disabled="uploading">
              <svg class="w-4 h-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
              </svg>
              上传图片
            </el-button>
          </el-upload>
          <div v-if="uploading && uploadType === 'multi'" class="mt-2">
            <el-progress :percentage="uploadPercentage" :stroke-width="6" />
          </div>
          <div v-if="form.imageUrls.length > 0" class="flex flex-wrap gap-2 mt-2">
            <div v-for="(url, idx) in form.imageUrls" :key="idx" class="relative w-20 h-20 rounded border overflow-hidden group">
              <img :src="getFullImageUrl(url)" class="w-full h-full object-cover" />
              <button
                @click="removeImageUrl(idx)"
                class="absolute top-0 right-0 bg-red-500 text-white w-5 h-5 flex items-center justify-center text-xs opacity-0 group-hover:opacity-100 transition-opacity"
              >×</button>
            </div>
          </div>
          <p class="text-xs text-gray-400 mt-1">支持多张图片上传，点击×删除</p>
        </el-form-item>

        <!-- PDF Upload -->
        <el-form-item label="产品PDF（展示）">
          <el-upload
            :action="`${config.public.apiBase}/api/admin/upload`"
            :headers="headers"
            :on-success="handlePdfSuccess"
            :on-progress="(evt, file, fileList) => handleUploadProgress(evt, file, fileList, 'pdf')"
            accept=".pdf"
            multiple
            :show-file-list="false"
            class="mb-2"
          >
            <el-button type="primary" plain size="small" :disabled="uploading">
              <svg class="w-4 h-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
              </svg>
              上传PDF（展示）
            </el-button>
          </el-upload>
          <div v-if="uploading && uploadType === 'pdf'" class="mt-2">
            <el-progress :percentage="uploadPercentage" :stroke-width="6" />
          </div>
          <div v-if="form.pdfUrls.length > 0" class="flex flex-wrap gap-2 mt-2">
            <div v-for="(url, idx) in form.pdfUrls" :key="idx" class="flex items-center gap-1 px-2 py-1 bg-gray-100 rounded text-sm">
              <svg class="w-4 h-4 text-red-500" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L15.414 6A2 2 0 0116 7.414V16a2 2 0 01-2 2H6a2 2 0 01-2-2V4z" clip-rule="evenodd" />
              </svg>
              <span class="text-gray-600 truncate max-w-32">{{ getFileName(url) }}</span>
              <button @click="removePdfUrl(idx)" class="text-red-500 hover:text-red-700 ml-1">×</button>
            </div>
          </div>
          <p class="text-xs text-gray-400 mt-1">这些PDF会在详情页内嵌展示</p>
        </el-form-item>

        <!-- Download PDF (single) -->
        <el-form-item label="下载PDF（独立）">
          <div class="flex items-center gap-4">
            <el-upload
              :action="`${config.public.apiBase}/api/admin/upload`"
              :headers="headers"
              :show-file-list="false"
              :on-success="handleDownloadPdfSuccess"
              :on-progress="(evt, file, fileList) => handleUploadProgress(evt, file, fileList, 'downloadPdf')"
              :before-upload="beforePdfUpload"
              accept=".pdf"
            >
              <el-button type="primary" plain :disabled="uploading">上传下载PDF</el-button>
            </el-upload>
            <el-input v-model="form.downloadPdfUrl" placeholder="下载PDF URL（与上方展示PDF不同）" class="flex-1" />
          </div>
          <div v-if="uploading && uploadType === 'downloadPdf'" class="mt-2">
            <el-progress :percentage="uploadPercentage" :stroke-width="6" />
          </div>
          <div v-if="form.downloadPdfUrl" class="mt-2 flex items-center gap-2 px-2 py-1 bg-gray-100 rounded text-sm">
            <svg class="w-4 h-4 text-red-500" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L15.414 6A2 2 0 0116 7.414V16a2 2 0 01-2 2H6a2 2 0 01-2-2V4z" clip-rule="evenodd" />
            </svg>
            <span class="text-gray-600 truncate flex-1">{{ getFileName(form.downloadPdfUrl) }}</span>
            <button @click="form.downloadPdfUrl = ''" class="text-red-500 hover:text-red-700">×</button>
          </div>
        </el-form-item>

        <!-- Detail description text -->
        <el-form-item label="产品描述文字 (EN)">
          <el-input v-model="form.detailDescEn" type="textarea" :rows="4" placeholder="PDF 之后展示的产品描述英文（独立字段）" />
        </el-form-item>
        <el-form-item label="产品描述文字 (ZH)">
          <el-input v-model="form.detailDescZh" type="textarea" :rows="4" placeholder="PDF 之后展示的产品描述中文" />
        </el-form-item>

        <!-- Detailed specs (separate from top specs) -->
        <el-form-item label="详细参数 (Detailed Specs)">
          <div class="specs-editor w-full">
            <div v-for="(spec, index) in form.detailedSpecs" :key="index" class="flex gap-2 mb-2">
              <el-input v-model="spec.key" placeholder="参数名" class="flex-1" />
              <el-input v-model="spec.value" placeholder="参数值" class="flex-1" />
              <el-button type="danger" @click="removeDetailedSpec(index)" :icon="Delete" circle />
            </div>
            <el-button type="primary" plain @click="addDetailedSpec" :icon="Plus">添加详细参数</el-button>
          </div>
        </el-form-item>

        <!-- Alibaba images (max 10) -->
        <el-form-item label="阿里10图 (最多10张)">
          <el-upload
            :action="`${config.public.apiBase}/api/admin/upload`"
            :headers="headers"
            :on-success="handleAlibabaImageSuccess"
            :on-progress="(evt, file, fileList) => handleUploadProgress(evt, file, fileList, 'alibaba')"
            :before-upload="beforeImageUpload"
            accept="image/*"
            :show-file-list="false"
            class="mb-2"
          >
            <el-button type="primary" plain size="small" :disabled="uploading || form.alibabaImages.length >= 10">
              上传图片（{{ form.alibabaImages.length }}/10）
            </el-button>
          </el-upload>
          <div v-if="uploading && uploadType === 'alibaba'" class="mt-2">
            <el-progress :percentage="uploadPercentage" :stroke-width="6" />
          </div>
          <div v-if="form.alibabaImages.length > 0" class="flex flex-wrap gap-2 mt-2">
            <div v-for="(url, idx) in form.alibabaImages" :key="idx" class="relative w-20 h-20 rounded border overflow-hidden group">
              <img :src="getFullImageUrl(url)" class="w-full h-full object-cover" />
              <button
                @click="removeAlibabaImage(idx)"
                class="absolute top-0 right-0 bg-red-500 text-white w-5 h-5 flex items-center justify-center text-xs opacity-0 group-hover:opacity-100 transition-opacity"
              >×</button>
            </div>
          </div>
        </el-form-item>

        <!-- External images with links -->
        <el-form-item label="带外链的图片">
          <div class="w-full space-y-2">
            <div v-for="(item, idx) in form.externalImages" :key="idx" class="flex gap-2 items-center p-2 bg-gray-50 rounded">
              <el-upload
                :action="`${config.public.apiBase}/api/admin/upload`"
                :headers="headers"
                :show-file-list="false"
                :on-success="(res) => handleExternalImageUploadSuccess(res, idx)"
                :before-upload="beforeImageUpload"
                accept="image/*"
              >
                <div class="w-20 h-20 border rounded overflow-hidden cursor-pointer hover:border-blue-400">
                  <img v-if="item.url" :src="getFullImageUrl(item.url)" class="w-full h-full object-cover" />
                  <div v-else class="w-full h-full flex items-center justify-center text-xs text-gray-400">上传图</div>
                </div>
              </el-upload>
              <el-input v-model="item.url" placeholder="图片URL" class="flex-1" />
              <el-input v-model="item.link" placeholder="外链URL（可选）" class="flex-1" />
              <el-button type="danger" @click="removeExternalImage(idx)" :icon="Delete" circle />
            </div>
            <el-button type="primary" plain @click="addExternalImage" :icon="Plus">添加带外链图片</el-button>
          </div>
        </el-form-item>

        <el-form-item :label="$t('admin.featured')">
          <el-switch v-model="form.featured" />
        </el-form-item>
        <el-form-item :label="$t('admin.sortOrder')">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item :label="$t('admin.specs')">
          <div class="specs-editor w-full">
            <div v-for="(spec, index) in form.specs" :key="index" class="flex gap-2 mb-2">
              <el-input v-model="spec.key" :placeholder="$t('admin.specKey')" class="flex-1" />
              <el-input v-model="spec.value" :placeholder="$t('admin.specValue')" class="flex-1" />
              <el-button type="danger" @click="removeSpec(index)" :icon="Delete" circle />
            </div>
            <el-button type="primary" plain @click="addSpec" :icon="Plus">{{ $t('admin.addSpec') }}</el-button>
          </div>
        </el-form-item>
        <el-form-item label="亮点 (Highlights)">
          <el-input v-model="form.highlights" type="textarea" :rows="3" placeholder="输入产品亮点，多个亮点用换行分隔" />
        </el-form-item>
        <el-form-item label="应用范围 (Applications)">
          <el-input v-model="form.applications" type="textarea" :rows="3" placeholder="输入产品应用范围" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">{{ $t('admin.cancel') }}</el-button>
        <el-button type="primary" @click="saveProduct">{{ $t('admin.save') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { Plus, Delete } from '@element-plus/icons-vue'
definePageMeta({
  layout: 'admin',
})

const { t } = useI18n()
const config = useRuntimeConfig()
const headers = ref({})

onMounted(() => {
  if (!import.meta.client) return

  const token = localStorage.getItem('admin_token')
  headers.value = { Authorization: `Bearer ${token}` }

  if (!token) navigateTo('/admin/login')
  fetchData()
})

const data = ref([])
const flatCategories = ref([])
const showDialog = ref(false)
const isEdit = ref(false)
const uploading = ref(false)
const uploadPercentage = ref(0)
const uploadType = ref('')
const form = reactive({
  id: null,
  categoryId: null,
  nameEn: '',
  nameZh: '',
  slug: '',
  descriptionEn: '',
  descriptionZh: '',
  imageUrl: '',
  imageUrls: [],
  pdfUrls: [],
  downloadPdfUrl: '',
  detailDescEn: '',
  detailDescZh: '',
  detailedSpecs: [],
  alibabaImages: [],
  externalImages: [],
  specs: [],
  featured: false,
  sortOrder: 0,
  highlights: '',
  applications: '',
})

const treeProps = {
  children: 'products',
  label: 'nameEn',
}

// Build tree data: categories with their products as children
const treeData = computed(() => {
  const result = []

  for (const cat of flatCategories.value) {
    const categoryNode = {
      id: `cat_${cat.id}`,
      nameEn: cat.nameEn,
      nameZh: cat.nameZh,
      isCategory: true,
      productCount: 0,
      products: [],
    }

    const catProducts = data.value.filter(p => p.categoryId === cat.id)
    categoryNode.productCount = catProducts.length

    for (const product of catProducts) {
      categoryNode.products.push({
        ...product,
        isCategory: false,
      })
    }

    // Sort products by sortOrder
    categoryNode.products.sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0))

    // Only add category if it has products or we want to show empty categories
    if (categoryNode.products.length > 0) {
      result.push(categoryNode)
    }
  }

  // Add uncategorized products
  const uncategorizedProducts = data.value.filter(p => !p.categoryId)
  if (uncategorizedProducts.length > 0) {
    result.push({
      id: 'cat_uncategorized',
      nameEn: 'Uncategorized',
      nameZh: '未分类',
      isCategory: true,
      productCount: uncategorizedProducts.length,
      products: uncategorizedProducts.map(p => ({
        ...p,
        isCategory: false,
      })),
    })
  }

  return result
})

const fetchData = async () => {
  try {
    const response = await $fetch(`${config.public.apiBase}/api/admin/products`, { headers: headers.value })
    data.value = response.data || []
    await fetchCategories()
  } catch (error) {
    ElMessage.error(t('admin.fetchFailed'))
  }
}

const fetchCategories = async () => {
  try {
    const response = await $fetch(`${config.public.apiBase}/api/admin/categories/tree`, { headers: headers.value })
    const tree = response.data || []
    // Flatten the tree for the select dropdown
    flatCategories.value = flattenCategories(tree)
  } catch (error) {
    console.error(t('admin.fetchFailed'))
  }
}

const flattenCategories = (cats, result = []) => {
  for (const cat of cats) {
    result.push({ id: cat.id, nameEn: cat.nameEn, nameZh: cat.nameZh })
    if (cat.children && cat.children.length > 0) {
      flattenCategories(cat.children, result)
    }
  }
  return result
}

const addProduct = () => {
  isEdit.value = false
  resetForm()
  showDialog.value = true
}

const addProductUnderCategory = (categoryId) => {
  isEdit.value = false
  resetForm()
  // Extract actual category id from "cat_{id}" format
  const catId = categoryId.startsWith('cat_') ? categoryId.replace('cat_', '') : categoryId
  form.categoryId = parseInt(catId) || null
  showDialog.value = true
}

const resetForm = () => {
  Object.keys(form).forEach(k => {
    if (k === 'sortOrder') form[k] = 0
    else if (k === 'featured') form[k] = false
    else if (['specs', 'imageUrls', 'pdfUrls', 'detailedSpecs', 'alibabaImages', 'externalImages'].includes(k)) form[k] = []
    else if (['highlights', 'applications', 'detailDescEn', 'detailDescZh', 'downloadPdfUrl'].includes(k)) form[k] = ''
    else form[k] = null
  })
}

const editProduct = (row) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    categoryId: row.categoryId,
    nameEn: row.nameEn,
    nameZh: row.nameZh,
    slug: row.slug,
    descriptionEn: row.descriptionEn || row.descriptionZh || '',
    descriptionZh: row.descriptionZh || row.descriptionEn || '',
    imageUrl: parseUrl(row.imageUrl),
    imageUrls: parseJsonArray(row.imageUrls),
    pdfUrls: parseJsonArray(row.pdfUrls),
    downloadPdfUrl: row.downloadPdfUrl || '',
    detailDescEn: row.detailDescEn || '',
    detailDescZh: row.detailDescZh || '',
    detailedSpecs: parseSpecs(row.detailedSpecs),
    alibabaImages: parseJsonArray(row.alibabaImages),
    externalImages: parseExternalImages(row.externalImages),
    featured: row.featured || false,
    sortOrder: row.sortOrder || 0,
    specs: parseSpecs(row.specs),
    highlights: row.highlights || '',
    applications: row.applications || '',
  })
  showDialog.value = true
}

const parseJsonArray = (val) => {
  if (Array.isArray(val)) return [...val]
  if (typeof val === 'string' && val) {
    try { return JSON.parse(val) } catch { return [] }
  }
  return []
}

const parseUrl = (val) => {
  if (!val) return ''
  if (typeof val === 'string') return val
  if (typeof val === 'object' && val !== null) return val.url || ''
  return ''
}

const parseSpecs = (specsObj) => {
  if (!specsObj || typeof specsObj !== 'object') return []
  return Object.entries(specsObj).map(([key, value]) => ({ key, value }))
}

const parseExternalImages = (val) => {
  if (!val) return []
  if (Array.isArray(val)) {
    return val.map(item => ({
      url: typeof item === 'string' ? item : (item?.url || ''),
      link: typeof item === 'object' && item !== null ? (item.link || '') : '',
    }))
  }
  if (typeof val === 'string' && val) {
    try {
      const parsed = JSON.parse(val)
      if (Array.isArray(parsed)) {
        return parsed.map(item => ({
          url: typeof item === 'string' ? item : (item?.url || ''),
          link: typeof item === 'object' && item !== null ? (item.link || '') : '',
        }))
      }
    } catch { /* ignore */ }
  }
  return []
}

const addSpec = () => {
  form.specs.push({ key: '', value: '' })
}

const removeSpec = (index) => {
  form.specs.splice(index, 1)
}

const addDetailedSpec = () => {
  form.detailedSpecs.push({ key: '', value: '' })
}

const removeDetailedSpec = (index) => {
  form.detailedSpecs.splice(index, 1)
}

const addExternalImage = () => {
  form.externalImages.push({ url: '', link: '' })
}

const removeExternalImage = (index) => {
  form.externalImages.splice(index, 1)
}

const handleImageUploadSuccess = (response) => {
  uploading.value = false
  uploadPercentage.value = 0
  if (response.code === 200 && response.data) {
    form.imageUrl = typeof response.data === 'string' ? response.data : (response.data.url || '')
    ElMessage.success(t('admin.uploadSuccess'))
  } else {
    ElMessage.error(response.message || t('admin.uploadFailed'))
  }
}

const handleMultiImageSuccess = (response) => {
  uploading.value = false
  uploadPercentage.value = 0
  if (response.code === 200 && response.data) {
    const url = typeof response.data === 'string' ? response.data : response.data.url
    if (url) form.imageUrls.push(url)
    ElMessage.success(t('admin.uploadSuccess'))
  } else {
    ElMessage.error(response.message || t('admin.uploadFailed'))
  }
}

const handlePdfSuccess = (response) => {
  uploading.value = false
  uploadPercentage.value = 0
  if (response.code === 200 && response.data) {
    const url = typeof response.data === 'string' ? response.data : response.data.url
    if (url) form.pdfUrls.push(url)
    ElMessage.success(t('admin.uploadSuccess'))
  } else {
    ElMessage.error(response.message || t('admin.uploadFailed'))
  }
}

const handleDownloadPdfSuccess = (response) => {
  uploading.value = false
  uploadPercentage.value = 0
  if (response.code === 200 && response.data) {
    const url = typeof response.data === 'string' ? response.data : response.data.url
    if (url) form.downloadPdfUrl = url
    ElMessage.success(t('admin.uploadSuccess'))
  } else {
    ElMessage.error(response.message || t('admin.uploadFailed'))
  }
}

const beforePdfUpload = (file) => {
  const isPdf = file.type === 'application/pdf' || /\.pdf$/i.test(file.name)
  const isLt50M = file.size / 1024 / 1024 < 50
  if (!isPdf) ElMessage.error('仅支持 PDF 文件')
  if (!isLt50M) ElMessage.error('PDF 大小需小于 50MB')
  return isPdf && isLt50M
}

const handleAlibabaImageSuccess = (response) => {
  uploading.value = false
  uploadPercentage.value = 0
  if (response.code === 200 && response.data) {
    const url = typeof response.data === 'string' ? response.data : response.data.url
    if (!url) return
    if (form.alibabaImages.length >= 10) {
      ElMessage.warning('最多 10 张')
      return
    }
    form.alibabaImages.push(url)
    ElMessage.success(t('admin.uploadSuccess'))
  } else {
    ElMessage.error(response.message || t('admin.uploadFailed'))
  }
}

const removeAlibabaImage = (index) => {
  form.alibabaImages.splice(index, 1)
}

const handleExternalImageUploadSuccess = (response, index) => {
  uploading.value = false
  uploadPercentage.value = 0
  if (response.code === 200 && response.data) {
    const url = typeof response.data === 'string' ? response.data : response.data.url
    if (url && form.externalImages[index]) {
      form.externalImages[index].url = url
    }
    ElMessage.success(t('admin.uploadSuccess'))
  } else {
    ElMessage.error(response.message || t('admin.uploadFailed'))
  }
}

const handleUploadProgress = (event, file, fileList, type) => {
  uploading.value = true
  uploadType.value = type
  uploadPercentage.value = Math.round(event.percent || 0)
}

const removeImageUrl = (index) => {
  form.imageUrls.splice(index, 1)
}

const removePdfUrl = (index) => {
  form.pdfUrls.splice(index, 1)
}

const getFileName = (url) => {
  if (!url) return ''
  return url.split('/').pop() || url
}

const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isImage) {
    ElMessage.error(t('admin.uploadImageOnly'))
  }
  if (!isLt10M) {
    ElMessage.error(t('admin.uploadImageSize'))
  }
  return isImage && isLt10M
}

const getFullImageUrl = (url) => {
  if (!url) return ''
  const strUrl = String(url)
  if (strUrl.startsWith('http://') || strUrl.startsWith('https://')) {
    return strUrl
  }
  const base = config.public.apiBase?.replace('/api', '') || ''
  return `${base}${strUrl}`
}

const saveProduct = async () => {
  if (!form.nameEn || !form.slug) {
    ElMessage.warning(t('admin.pleaseEnter') + ' nameEn and slug')
    return
  }

  try {
    const method = isEdit.value ? 'PUT' : 'POST'
    const url = isEdit.value
      ? `${config.public.apiBase}/api/admin/products/${form.id}`
      : `${config.public.apiBase}/api/admin/products`

    // Convert specs arrays to JSON objects
    const specsObj = {}
    form.specs.forEach(spec => {
      if (spec.key && spec.value) specsObj[spec.key] = spec.value
    })
    const detailedSpecsObj = {}
    form.detailedSpecs.forEach(spec => {
      if (spec.key && spec.value) detailedSpecsObj[spec.key] = spec.value
    })

    // Filter out empty external image entries
    const cleanedExternalImages = form.externalImages
      .filter(item => item.url && item.url.trim())
      .map(item => ({ url: item.url, link: item.link || '' }))

    const body = {
      ...form,
      specsJson: Object.keys(specsObj).length > 0 ? JSON.stringify(specsObj) : null,
      detailedSpecs: Object.keys(detailedSpecsObj).length > 0 ? JSON.stringify(detailedSpecsObj) : null,
      imageUrls: form.imageUrls.length > 0 ? JSON.stringify(form.imageUrls) : null,
      pdfUrls: form.pdfUrls.length > 0 ? JSON.stringify(form.pdfUrls) : null,
      alibabaImages: form.alibabaImages.length > 0 ? JSON.stringify(form.alibabaImages) : null,
      externalImages: cleanedExternalImages.length > 0 ? JSON.stringify(cleanedExternalImages) : null,
    }

    await $fetch(url, { method, headers: headers.value, body })
    ElMessage.success(t('admin.saveSuccess'))
    showDialog.value = false
    fetchData()
  } catch (error) {
    ElMessage.error(t('admin.saveFailed') + ': ' + (error.data?.message || error.message || ''))
  }
}

const removeProduct = async (id) => {
  try {
    await ElMessageBox.confirm(t('admin.confirmDelete'), t('admin.delete'), {
      confirmButtonText: t('admin.yes'),
      cancelButtonText: t('admin.cancel'),
      type: 'warning',
    })

    await $fetch(`${config.public.apiBase}/api/admin/products/${id}`, { method: 'DELETE', headers: headers.value })
    ElMessage.success(t('admin.deleteSuccess'))
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      const errorMsg = error.data?.message || error._data?.message || error.message || ''
      ElMessage.error(t('admin.deleteFailed') + (errorMsg ? ': ' + errorMsg : ''))
    }
  }
}
</script>

<style scoped>
.page-container {
  padding: 0;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  border-radius: 12px;
  padding: 20px 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}
.header-content {
  display: flex;
  align-items: center;
  gap: 16px;
}
.header-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}
.header-text h1 {
  font-size: 1.25rem;
  margin-bottom: 4px;
}
.add-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}
.add-btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}
.main-card {
  border-radius: 12px;
  border: none;
}
.product-tree {
  background: transparent;
}
.tree-node {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding-right: 8px;
}
.node-content {
  display: flex;
  align-items: center;
  gap: 4px;
}
.category-icon {
  color: #8b5cf6;
  margin-right: 8px;
}
.category-label,
.product-label {
  display: flex;
  align-items: center;
}
.node-actions {
  display: flex;
  gap: 8px;
}
:deep(.el-tree-node__content) {
  height: 48px;
  border-radius: 8px;
  margin: 2px 0;
}
:deep(.el-tree-node__content:hover) {
  background: #f3f4f6;
}
.empty-state {
  text-align: center;
  padding: 60px 20px;
}
</style>
