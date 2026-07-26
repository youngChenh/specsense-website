<template>
  <div class="page-container">
    <!-- Page Header -->
    <div class="page-header mb-6">
      <div class="header-content">
        <div class="header-icon">
          <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 5a1 1 0 011-1h14a1 1 0 011 1v2a1 1 0 01-1 1H5a1 1 0 01-1-1V5zM4 13a1 1 0 011-1h6a1 1 0 011 1v6a1 1 0 01-1 1H5a1 1 0 01-1-1v-6zM16 13a1 1 0 011-1h2a1 1 0 011 1v6a1 1 0 01-1 1h-2a1 1 0 01-1-1v-6z" />
          </svg>
        </div>
        <div class="header-text">
          <h1 class="text-2xl font-bold text-gray-800">{{ $t('admin.applications') }}</h1>
          <p class="text-gray-500 text-sm mt-1">管理应用领域内容</p>
        </div>
      </div>
      <el-button type="primary" @click="addNew" class="add-btn">
        <svg class="w-4 h-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        {{ $t('admin.add') }}
      </el-button>
    </div>

    <!-- Search & Filter -->
    <el-card shadow="never" class="main-card mb-4">
      <div class="flex flex-wrap gap-4 items-center">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索标题..."
          clearable
          class="!w-64"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
    </el-card>

    <el-card shadow="never" class="main-card">
      <el-table :data="displayData" class="admin-table">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="type" :label="$t('admin.type')" width="120">
          <template #default="{ row }">
            <el-tag size="small" effect="light">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" :label="$t('admin.titleEn')" />
        <el-table-column :label="$t('admin.imageUrl')" width="100">
          <template #default="{ row }">
            <img v-if="row.imageUrl" :src="getFullImageUrl(row.imageUrl)" class="w-12 h-12 object-cover rounded-lg" />
            <span v-else class="text-gray-400">-</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('admin.actions')" width="150">
          <template #default="{ row }">
            <el-button size="small" text type="primary" @click="edit(row)">编辑</el-button>
            <el-button size="small" text type="danger" @click="remove(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showDialog" :title="isEdit ? $t('admin.edit') + ' ' + $t('admin.application') : $t('admin.add') + ' ' + $t('admin.application')" width="700px">
      <el-form :model="form" label-width="100px">
        <el-form-item :label="$t('admin.type')">
          <el-input v-model="form.type" :disabled="isEdit" :placeholder="$t('admin.typePlaceholder')" />
        </el-form-item>
        <el-form-item :label="$t('admin.titleEn')">
          <el-input v-model="form.titleEn" />
        </el-form-item>
        <el-form-item :label="$t('admin.titleZh')">
          <el-input v-model="form.titleZh" />
        </el-form-item>
        <el-form-item :label="$t('admin.descriptionEn')">
          <el-input v-model="form.descriptionEn" type="textarea" rows="3" />
        </el-form-item>
        <el-form-item :label="$t('admin.descriptionZh')">
          <el-input v-model="form.descriptionZh" type="textarea" rows="3" />
        </el-form-item>
        <el-form-item :label="$t('admin.imageUrl')">
          <div class="flex items-center gap-4">
            <el-upload
              :action="`${config.public.apiBase}/api/admin/upload`"
              :headers="headers"
              :show-file-list="false"
              :on-success="handleImageUploadSuccess"
              :on-error="handleImageUploadError"
              :on-progress="handleImageUploadProgress"
              accept="image/*"
            >
              <el-button type="primary" plain :disabled="uploading">{{ $t('admin.uploadImage') }}</el-button>
            </el-upload>
            <el-input v-model="form.imageUrl" :placeholder="$t('admin.imageUrlPlaceholder')" class="flex-1" />
          </div>
          <div v-if="uploading" class="mt-2">
            <el-progress :percentage="uploadPercentage" :stroke-width="8" />
          </div>
          <div v-if="form.imageUrl" class="mt-2">
            <img :src="getFullImageUrl(form.imageUrl)" class="w-32 h-32 object-cover rounded border" />
          </div>
        </el-form-item>
        <el-form-item :label="$t('admin.linkedCategories')">
          <el-select v-model="form.linkedCategoryKeys" multiple :placeholder="$t('admin.selectCategories')" class="w-full">
            <el-option
              v-for="cat in categoryOptions"
              :key="cat.key"
              :label="cat.name"
              :value="cat.key"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">{{ $t('admin.cancel') }}</el-button>
        <el-button type="primary" @click="save">{{ $t('admin.save') }}</el-button>
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
  fetchCategories()
})

const data = ref([])
const categories = ref([])
const showDialog = ref(false)
const isEdit = ref(false)
const uploading = ref(false)
const uploadPercentage = ref(0)
const searchKeyword = ref('')

const displayData = computed(() => {
  if (!searchKeyword.value) return data.value
  const kw = searchKeyword.value.toLowerCase()
  return data.value.filter(item =>
    (item.titleEn && item.titleEn.toLowerCase().includes(kw)) ||
    (item.titleZh && item.titleZh.toLowerCase().includes(kw))
  )
})

function handleSearch() {}

function resetSearch() {
  searchKeyword.value = ''
}

const categoryOptions = computed(() => {
  return categories.value.map(cat => ({
    key: cat.key,
    name: cat.nameZh || cat.nameEn || cat.key
  }))
})

const form = reactive({
  id: null,
  type: '',
  titleEn: '',
  titleZh: '',
  descriptionEn: '',
  descriptionZh: '',
  icon: 'default',
  imageUrl: '',
  linkedCategoryKeys: [],
})

const fetchData = async () => {
  try {
    const response = await $fetch(`${config.public.apiBase}/api/admin/applications`, { headers: headers.value })
    data.value = response.data || []
  } catch (error) {
    ElMessage.error(t('admin.fetchFailed'))
  }
}

const fetchCategories = async () => {
  try {
    const response = await $fetch(`${config.public.apiBase}/api/admin/categories`, { headers: headers.value })
    categories.value = response.data || []
  } catch (error) {
    console.error('Failed to fetch categories:', error)
  }
}

const addNew = () => {
  isEdit.value = false
  Object.keys(form).forEach(k => {
    if (k === 'linkedCategoryKeys') form[k] = []
    else if (k === 'icon') form[k] = 'default'
    else form[k] = ''
  })
  showDialog.value = true
}

const edit = (row) => {
  isEdit.value = true
  form.id = row.id
  form.type = row.type
  form.titleEn = row.titleEn || row.title?.split(' - ')[0] || ''
  form.titleZh = row.titleZh || ''
  form.descriptionEn = row.descriptionEn || ''
  form.descriptionZh = row.descriptionZh || ''
  form.icon = row.icon || 'default'
  form.imageUrl = row.imageUrl || ''
  // Parse linkedCategoryKeys from comma-separated string to array
  form.linkedCategoryKeys = row.linkedCategoryKeys ? row.linkedCategoryKeys.split(',').filter(k => k) : []
  showDialog.value = true
}

const save = async () => {
  try {
    // Convert array to comma-separated string for backend
    const saveData = {
      ...form,
      linkedCategoryKeys: Array.isArray(form.linkedCategoryKeys) ? form.linkedCategoryKeys.join(',') : form.linkedCategoryKeys
    }
    const method = isEdit.value ? 'PUT' : 'POST'
    const url = isEdit.value
      ? `${config.public.apiBase}/api/admin/applications/${form.id}`
      : `${config.public.apiBase}/api/admin/applications`

    await $fetch(url, { method, headers: headers.value, body: saveData })
    ElMessage.success(t('admin.saveSuccess'))
    showDialog.value = false
    fetchData()
  } catch (error) {
    ElMessage.error(t('admin.saveFailed'))
  }
}

const remove = async (id) => {
  try {
    await $fetch(`${config.public.apiBase}/api/admin/applications/${id}`, { method: 'DELETE', headers: headers.value })
    ElMessage.success(t('admin.deleteSuccess'))
    fetchData()
  } catch (error) {
    const errorMsg = error.data?.message || error._data?.message || error.message || ''
    ElMessage.error(t('admin.deleteFailed') + (errorMsg ? ': ' + errorMsg : ''))
  }
}

const handleImageUploadProgress = (event) => {
  uploading.value = true
  uploadPercentage.value = Math.round(event.percent || 0)
}

const handleImageUploadSuccess = (response) => {
  uploading.value = false
  uploadPercentage.value = 0
  if (response.code === 200 && response.data) {
    form.imageUrl = typeof response.data === 'string' ? response.data : response.data.url
    ElMessage.success(t('admin.uploadSuccess'))
  } else {
    ElMessage.error(t('admin.uploadFailed'))
  }
}

const handleImageUploadError = () => {
  uploading.value = false
  uploadPercentage.value = 0
  ElMessage.error(t('admin.uploadFailed'))
}

const getFullImageUrl = (url) => {
  if (!url) return ''
  const strUrl = String(url)
  if (strUrl.startsWith('http')) return strUrl
  const base = config.public.apiBase?.replace('/api', '') || ''
  return `${base}${strUrl}`
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
  background: linear-gradient(135deg, #ec4899 0%, #db2777 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 15px rgba(236, 72, 153, 0.3);
}
.header-text h1 {
  font-size: 1.25rem;
  margin-bottom: 4px;
}
.add-btn {
  background: linear-gradient(135deg, #ec4899 0%, #db2777 100%) !important;
  border: none !important;
  box-shadow: 0 4px 15px rgba(236, 72, 153, 0.3);
}
.add-btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}
.main-card {
  border-radius: 12px;
  border: none;
}
.admin-table :deep(.el-table__header th) {
  background: #f9fafb !important;
  color: #374151;
  font-weight: 600;
}
.admin-table :deep(.el-table__row:hover > td) {
  background: #f9fafb !important;
}
</style>
