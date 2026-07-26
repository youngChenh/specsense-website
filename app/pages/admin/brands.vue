<template>
  <div class="page-container">
    <!-- Tab Switch -->
    <el-radio-group v-model="activeTab" size="large" class="mb-4">
      <el-radio-button value="categories">分类管理</el-radio-button>
      <el-radio-button value="brands">品牌管理</el-radio-button>
    </el-radio-group>

    <!-- Category Management Tab -->
    <div v-if="activeTab === 'categories'">
      <!-- Search -->
      <el-card shadow="never" class="main-card mb-4">
        <div class="flex gap-4 items-center">
          <el-input
            v-model="categorySearch"
            placeholder="搜索分类名称..."
            clearable
            class="!w-64"
            @keyup.enter="handleCategorySearch"
          />
          <el-button type="primary" @click="handleCategorySearch">查询</el-button>
          <el-button @click="resetCategorySearch">重置</el-button>
        </div>
      </el-card>

      <el-card shadow="never" class="main-card">
        <template #header>
          <div class="flex justify-between items-center">
            <span class="font-medium">品牌分类列表</span>
            <el-button type="primary" size="small" @click="addCategory">
              <svg class="w-4 h-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
              </svg>
              添加分类
            </el-button>
          </div>
        </template>
        <el-table :data="displayCategories" stripe style="width: 100%">
          <el-table-column prop="nameEn" label="英文名称" width="200" />
          <el-table-column prop="nameZh" label="中文名称" width="200" />
          <el-table-column prop="key" label="Key" width="150" />
          <el-table-column prop="sortOrder" label="排序" width="100" />
          <el-table-column label="操作">
            <template #default="{ row }">
              <el-button size="small" text type="primary" @click="editCategory(row)">编辑</el-button>
              <el-button size="small" text type="danger" @click="removeCategory(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- Brand Management Tab -->
    <div v-if="activeTab === 'brands'">
      <div class="flex gap-2 mb-4">
        <el-button type="primary" @click="addBrand" class="add-btn">
          <svg class="w-4 h-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
          </svg>
          添加品牌
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
          class="brand-tree"
        >
          <template #default="{ node, data }">
            <span class="tree-node">
              <span class="node-content">
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
                  <el-tag size="small" type="info" effect="light" class="ml-3">{{ data.brandCount }} {{ $t('admin.brands').toLowerCase() }}</el-tag>
                </template>
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
                  <el-button size="small" text type="primary" @click="addBrandUnderCategory(data.id)">
                    <span class="text-xs">添加品牌</span>
                  </el-button>
                  <el-button size="small" text type="primary" @click="editCategory(data)">编辑分类</el-button>
                </template>
                <template v-else>
                  <el-button size="small" text type="primary" @click="editBrand(data)">编辑</el-button>
                  <el-button size="small" text type="danger" @click="removeBrand(data.id)">删除</el-button>
                </template>
              </span>
            </span>
          </template>
        </el-tree>

        <div v-else class="empty-state">
          <svg class="w-16 h-16 text-gray-300 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
          </svg>
          <p class="text-gray-500">暂无品牌</p>
        </div>
      </el-card>
    </div>

    <!-- Category Dialog -->
    <el-dialog
      v-model="showCategoryDialog"
      :title="isEditCategory ? $t('admin.edit') + ' ' + $t('admin.category') : $t('admin.add') + ' ' + $t('admin.category')"
      width="500px"
    >
      <el-form :model="categoryForm" label-width="120px">
        <el-form-item :label="$t('admin.nameEn')">
          <el-input v-model="categoryForm.nameEn" :placeholder="$t('admin.pleaseEnter') + $t('admin.nameEn')" />
        </el-form-item>
        <el-form-item :label="$t('admin.nameZh')">
          <el-input v-model="categoryForm.nameZh" :placeholder="$t('admin.pleaseEnter') + $t('admin.nameZh')" />
        </el-form-item>
        <el-form-item :label="$t('admin.key')">
          <el-input v-model="categoryForm.key" :placeholder="$t('admin.pleaseEnter') + $t('admin.key')" :disabled="isEditCategory" />
        </el-form-item>
        <el-form-item :label="$t('admin.sortOrder')">
          <el-input-number v-model="categoryForm.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCategoryDialog = false">{{ $t('admin.cancel') }}</el-button>
        <el-button type="primary" @click="saveCategory">{{ $t('admin.save') }}</el-button>
      </template>
    </el-dialog>

    <!-- Brand Dialog -->
    <el-dialog
      v-model="showBrandDialog"
      :title="isEditBrand ? $t('admin.edit') + ' ' + $t('admin.brand') : $t('admin.add') + ' ' + $t('admin.brand')"
      width="800px"
    >
      <el-form :model="brandForm" label-width="120px">
        <el-form-item :label="$t('admin.category')">
          <el-select v-model="brandForm.categoryId" :placeholder="$t('admin.pleaseSelect')" class="w-full">
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.nameEn + ' / ' + cat.nameZh"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('admin.nameEn')">
          <el-input v-model="brandForm.nameEn" :placeholder="$t('admin.pleaseEnter') + $t('admin.nameEn')" />
        </el-form-item>
        <el-form-item :label="$t('admin.nameZh')">
          <el-input v-model="brandForm.nameZh" :placeholder="$t('admin.pleaseEnter') + $t('admin.nameZh')" />
        </el-form-item>
        <el-form-item :label="$t('admin.slug')">
          <el-input v-model="brandForm.slug" :placeholder="$t('admin.pleaseEnter') + $t('admin.slug')" :disabled="isEditBrand" />
        </el-form-item>
        <el-form-item :label="$t('admin.logoUrl')">
          <div class="flex items-center gap-4">
            <el-upload
              :action="`${config.public.apiBase}/api/admin/upload`"
              :headers="headers"
              :show-file-list="false"
              :on-success="handleLogoUploadSuccess"
              :before-upload="beforeImageUpload"
              accept="image/*"
            >
              <el-button type="primary" plain>{{ $t('admin.uploadImage') }}</el-button>
            </el-upload>
            <el-input v-model="brandForm.logoUrl" :placeholder="$t('admin.imageUrlPlaceholder')" class="flex-1" />
          </div>
          <div v-if="brandForm.logoUrl" class="mt-2">
            <img :src="getFullImageUrl(brandForm.logoUrl)" class="w-32 h-32 object-contain rounded border bg-gray-50" />
          </div>
        </el-form-item>
        <el-form-item :label="$t('admin.descriptionEn')">
          <el-input v-model="brandForm.descriptionEn" type="textarea" rows="3" :placeholder="$t('admin.pleaseEnter') + $t('admin.descriptionEn')" />
        </el-form-item>
        <el-form-item :label="$t('admin.descriptionZh')">
          <el-input v-model="brandForm.descriptionZh" type="textarea" rows="3" :placeholder="$t('admin.pleaseEnter') + $t('admin.descriptionZh')" />
        </el-form-item>
        <el-form-item :label="$t('admin.websiteUrl')">
          <el-input v-model="brandForm.websiteUrl" placeholder="https://" />
        </el-form-item>
        <el-form-item :label="$t('admin.featured')">
          <el-switch v-model="brandForm.featured" />
        </el-form-item>
        <el-form-item :label="$t('admin.sortOrder')">
          <el-input-number v-model="brandForm.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showBrandDialog = false">{{ $t('admin.cancel') }}</el-button>
        <el-button type="primary" @click="saveBrand">{{ $t('admin.save') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
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
const activeTab = ref('categories')
const categories = ref([])
const categorySearch = ref('')

const displayCategories = computed(() => {
  if (!categorySearch.value) return categories.value
  const kw = categorySearch.value.toLowerCase()
  return categories.value.filter(cat =>
    (cat.nameEn && cat.nameEn.toLowerCase().includes(kw)) ||
    (cat.nameZh && cat.nameZh.toLowerCase().includes(kw)) ||
    (cat.key && cat.key.toLowerCase().includes(kw))
  )
})

function handleCategorySearch() {}

function resetCategorySearch() {
  categorySearch.value = ''
}

const showCategoryDialog = ref(false)
const isEditCategory = ref(false)
const categoryForm = reactive({
  id: null,
  nameEn: '',
  nameZh: '',
  key: '',
  sortOrder: 0,
})

const showBrandDialog = ref(false)
const isEditBrand = ref(false)
const brandForm = reactive({
  id: null,
  categoryId: null,
  nameEn: '',
  nameZh: '',
  slug: '',
  logoUrl: '',
  descriptionEn: '',
  descriptionZh: '',
  websiteUrl: '',
  featured: false,
  sortOrder: 0,
})

const treeProps = {
  children: 'brands',
  label: 'nameEn',
}

// Build tree data: categories with their brands as children
const treeData = computed(() => {
  const result = []

  for (const cat of categories.value) {
    const categoryNode = {
      id: `cat_${cat.id}`,
      nameEn: cat.nameEn,
      nameZh: cat.nameZh,
      key: cat.key,
      isCategory: true,
      brandCount: 0,
      brands: [],
    }

    const catBrands = data.value.filter(b => b.categoryId === cat.id)
    categoryNode.brandCount = catBrands.length

    for (const brand of catBrands) {
      categoryNode.brands.push({
        ...brand,
        isCategory: false,
      })
    }

    // Sort brands by sortOrder
    categoryNode.brands.sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0))

    // Only add category if it has brands or we want to show empty categories
    if (categoryNode.brands.length > 0) {
      result.push(categoryNode)
    }
  }

  return result
})

const fetchData = async () => {
  try {
    const response = await $fetch(`${config.public.apiBase}/api/admin/brands`, { headers: headers.value })
    data.value = response.data || []
    await fetchCategories()
  } catch (error) {
    ElMessage.error(t('admin.fetchFailed'))
  }
}

const fetchCategories = async () => {
  try {
    const response = await $fetch(`${config.public.apiBase}/api/admin/brand-categories`, { headers: headers.value })
    categories.value = response.data || []
  } catch (error) {
    console.error(t('admin.fetchFailed'))
  }
}

// Category operations
const addCategory = () => {
  isEditCategory.value = false
  categoryForm.id = null
  categoryForm.nameEn = ''
  categoryForm.nameZh = ''
  categoryForm.key = ''
  categoryForm.sortOrder = 0
  showCategoryDialog.value = true
}

const editCategory = (row) => {
  isEditCategory.value = true
  const catId = String(row.id).startsWith('cat_') ? String(row.id).replace('cat_', '') : row.id
  const cat = categories.value.find(c => c.id === parseInt(catId))
  if (cat) {
    categoryForm.id = cat.id
    categoryForm.nameEn = cat.nameEn
    categoryForm.nameZh = cat.nameZh
    categoryForm.key = cat.key
    categoryForm.sortOrder = cat.sortOrder || 0
  }
  showCategoryDialog.value = true
}

const saveCategory = async () => {
  if (!categoryForm.nameEn || !categoryForm.key) {
    ElMessage.warning(t('admin.pleaseEnter') + ' nameEn and key')
    return
  }

  try {
    const method = isEditCategory.value ? 'PUT' : 'POST'
    const url = isEditCategory.value
      ? `${config.public.apiBase}/api/admin/brand-categories/${categoryForm.id}`
      : `${config.public.apiBase}/api/admin/brand-categories`

    await $fetch(url, { method, headers: headers.value, body: categoryForm })
    ElMessage.success(t('admin.saveSuccess'))
    showCategoryDialog.value = false
    fetchCategories()
  } catch (error) {
    ElMessage.error(t('admin.saveFailed') + ': ' + (error.data?.message || error.message || ''))
  }
}

const removeCategory = async (id) => {
  try {
    await ElMessageBox.confirm(t('admin.confirmDelete'), t('admin.delete'), {
      confirmButtonText: t('admin.yes'),
      cancelButtonText: t('admin.cancel'),
      type: 'warning',
    })

    const catId = String(id).startsWith('cat_') ? id.replace('cat_', '') : id
    const response = await fetch(`${config.public.apiBase}/api/admin/brand-categories/${catId}`, {
      method: 'DELETE',
      headers: headers.value
    })
    const data = await response.json()
    if (!response.ok) {
      throw new Error(data.message || 'Delete failed')
    }
    ElMessage.success(t('admin.deleteSuccess'))
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      const errorMsg = error.message || ''
      ElMessage.error(t('admin.deleteFailed') + (errorMsg ? ': ' + errorMsg : ''))
    }
  }
}

// Brand operations
const addBrand = () => {
  isEditBrand.value = false
  brandForm.id = null
  brandForm.categoryId = categories.value.length > 0 ? categories.value[0].id : null
  brandForm.nameEn = ''
  brandForm.nameZh = ''
  brandForm.slug = ''
  brandForm.logoUrl = ''
  brandForm.descriptionEn = ''
  brandForm.descriptionZh = ''
  brandForm.websiteUrl = ''
  brandForm.featured = false
  brandForm.sortOrder = 0
  showBrandDialog.value = true
}

const addBrandUnderCategory = (categoryId) => {
  isEditBrand.value = false
  const catId = String(categoryId).startsWith('cat_') ? categoryId.replace('cat_', '') : categoryId
  brandForm.id = null
  brandForm.categoryId = parseInt(catId) || null
  brandForm.nameEn = ''
  brandForm.nameZh = ''
  brandForm.slug = ''
  brandForm.logoUrl = ''
  brandForm.descriptionEn = ''
  brandForm.descriptionZh = ''
  brandForm.websiteUrl = ''
  brandForm.featured = false
  brandForm.sortOrder = 0
  showBrandDialog.value = true
}

const editBrand = (row) => {
  isEditBrand.value = true
  brandForm.id = row.id
  brandForm.categoryId = row.categoryId
  brandForm.nameEn = row.nameEn
  brandForm.nameZh = row.nameZh
  brandForm.slug = row.slug
  brandForm.logoUrl = row.logoUrl || ''
  brandForm.descriptionEn = row.descriptionEn || row.descriptionZh || ''
  brandForm.descriptionZh = row.descriptionZh || row.descriptionEn || ''
  brandForm.websiteUrl = row.websiteUrl || ''
  brandForm.featured = row.featured || false
  brandForm.sortOrder = row.sortOrder || 0
  showBrandDialog.value = true
}

const handleLogoUploadSuccess = (response) => {
  if (response.code === 200 && response.data) {
    brandForm.logoUrl = response.data.url || response.data
    ElMessage.success(t('admin.uploadSuccess'))
  } else {
    ElMessage.error(response.message || t('admin.uploadFailed'))
  }
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

const saveBrand = async () => {
  if (!brandForm.nameEn || !brandForm.slug) {
    ElMessage.warning(t('admin.pleaseEnter') + ' nameEn and slug')
    return
  }

  try {
    const method = isEditBrand.value ? 'PUT' : 'POST'
    const url = isEditBrand.value
      ? `${config.public.apiBase}/api/admin/brands/${brandForm.id}`
      : `${config.public.apiBase}/api/admin/brands`

    await $fetch(url, { method, headers: headers.value, body: brandForm })
    ElMessage.success(t('admin.saveSuccess'))
    showBrandDialog.value = false
    fetchData()
  } catch (error) {
    ElMessage.error(t('admin.saveFailed') + ': ' + (error.data?.message || error.message || ''))
  }
}

const removeBrand = async (id) => {
  try {
    await ElMessageBox.confirm(t('admin.confirmDelete'), t('admin.delete'), {
      confirmButtonText: t('admin.yes'),
      cancelButtonText: t('admin.cancel'),
      type: 'warning',
    })

    const response = await fetch(`${config.public.apiBase}/api/admin/brands/${id}`, {
      method: 'DELETE',
      headers: headers.value
    })
    const data = await response.json()
    if (!response.ok) {
      throw new Error(data.message || 'Delete failed')
    }
    ElMessage.success(t('admin.deleteSuccess'))
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      const errorMsg = error.message || ''
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
.brand-tree {
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
