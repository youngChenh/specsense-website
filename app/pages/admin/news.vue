<template>
  <div class="page-container">
    <!-- Page Header -->
    <div class="page-header mb-6">
      <div class="header-content">
        <div class="header-icon">
          <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V7m2 13a2 2 0 002-2V9a2 2 0 00-2-2h-2m-4-3H9M7 16h6M7 8h6v4H7V8z" />
          </svg>
        </div>
        <div class="header-text">
          <h1 class="text-2xl font-bold text-gray-800">{{ $t('admin.news') }}</h1>
          <p class="text-gray-500 text-sm mt-1">管理新闻资讯内容</p>
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
        <el-select v-model="searchCategory" placeholder="分类" clearable class="!w-40">
          <el-option label="全部" value="" />
          <el-option label="Company" value="company" />
          <el-option label="Industry" value="industry" />
          <el-option label="Technology" value="technology" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
    </el-card>

    <el-card shadow="never" class="main-card">
      <el-table :data="displayData" class="admin-table">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" :label="$t('admin.titleEn')" />
        <el-table-column prop="category" :label="$t('admin.category')" width="120">
          <template #default="{ row }">
            <el-tag size="small" effect="light">{{ row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishedAt" :label="$t('admin.publishedAt')" width="120">
          <template #default="{ row }">
            {{ row.publishedAt ? new Date(row.publishedAt).toLocaleDateString() : '' }}
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

    <el-dialog v-model="showDialog" :title="isEdit ? $t('admin.edit') + ' ' + $t('admin.news') : $t('admin.add') + ' ' + $t('admin.news')" width="900px">
      <el-form :model="form" label-width="100px">
        <el-form-item :label="$t('admin.category')">
          <el-select v-model="form.category">
            <el-option label="Company" value="company" />
            <el-option label="Industry" value="industry" />
            <el-option label="Technology" value="technology" />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('admin.titleEn')">
          <el-input v-model="form.titleEn" />
        </el-form-item>
        <el-form-item :label="$t('admin.titleZh')">
          <el-input v-model="form.titleZh" />
        </el-form-item>
        <el-form-item :label="$t('admin.slug')">
          <el-input v-model="form.slug" />
        </el-form-item>
        <el-form-item :label="$t('admin.descriptionEn')">
          <el-input v-model="form.excerptEn" type="textarea" rows="2" />
        </el-form-item>
        <el-form-item :label="$t('admin.descriptionZh')">
          <el-input v-model="form.excerptZh" type="textarea" rows="2" />
        </el-form-item>
        <el-form-item :label="$t('admin.content') + ' (EN)'">
          <el-input v-model="form.contentEn" type="textarea" rows="6" />
        </el-form-item>
        <el-form-item :label="$t('admin.content') + ' (ZH)'">
          <el-input v-model="form.contentZh" type="textarea" rows="6" />
        </el-form-item>
        <el-form-item :label="$t('admin.imageUrl')">
          <div class="flex items-center gap-4">
            <el-upload
              :action="`${config.public.apiBase}/api/admin/upload`"
              :headers="headers"
              :show-file-list="false"
              :on-success="handleImageUploadSuccess"
              :on-progress="handleUploadProgress"
              :before-upload="beforeImageUpload"
              accept="image/*"
            >
              <el-button type="primary" plain :disabled="uploading">{{ $t('admin.uploadImage') }}</el-button>
            </el-upload>
            <el-input v-model="form.imageUrl" :placeholder="$t('admin.imageUrlPlaceholder')" class="flex-1" />
          </div>
          <div v-if="uploading" class="mt-2">
            <el-progress :percentage="uploadPercentage" :stroke-width="6" />
          </div>
          <div v-if="form.imageUrl" class="mt-2">
            <img :src="getFullImageUrl(form.imageUrl)" class="w-32 h-32 object-cover rounded border" />
          </div>
        </el-form-item>
        <el-form-item :label="$t('admin.publishedAt')">
          <el-date-picker v-model="form.publishedAt" type="datetime" />
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
})

const data = ref([])
const showDialog = ref(false)
const isEdit = ref(false)
const uploading = ref(false)
const uploadPercentage = ref(0)
const searchKeyword = ref('')
const searchCategory = ref('')

const displayData = computed(() => {
  let result = data.value
  if (searchCategory.value) {
    result = result.filter(item => item.category === searchCategory.value)
  }
  if (searchKeyword.value) {
    const kw = searchKeyword.value.toLowerCase()
    result = result.filter(item =>
      (item.titleEn && item.titleEn.toLowerCase().includes(kw)) ||
      (item.titleZh && item.titleZh.toLowerCase().includes(kw))
    )
  }
  return result
})

function handleSearch() {
  // 触发 computed 重新计算
}

function resetSearch() {
  searchKeyword.value = ''
  searchCategory.value = ''
}
const form = reactive({
  id: null,
  category: 'company',
  titleEn: '',
  titleZh: '',
  slug: '',
  excerptEn: '',
  excerptZh: '',
  contentEn: '',
  contentZh: '',
  imageUrl: '',
  author: 'Admin',
  publishedAt: new Date(),
})

const fetchData = async () => {
  try {
    const response = await $fetch(`${config.public.apiBase}/api/admin/news`, { headers: headers.value })
    data.value = response.data || []
  } catch (error) {
    ElMessage.error(t('admin.fetchFailed'))
  }
}

const addNew = () => {
  isEdit.value = false
  Object.keys(form).forEach(k => form[k] = k === 'category' ? 'company' : k === 'publishedAt' ? new Date() : '')
  showDialog.value = true
}

const edit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  showDialog.value = true
}

const save = async () => {
  try {
    const method = isEdit.value ? 'PUT' : 'POST'
    const url = isEdit.value
      ? `${config.public.apiBase}/api/admin/news/${form.id}`
      : `${config.public.apiBase}/api/admin/news`

    await $fetch(url, { method, headers: headers.value, body: form })
    ElMessage.success(t('admin.saveSuccess'))
    showDialog.value = false
    fetchData()
  } catch (error) {
    ElMessage.error(t('admin.saveFailed'))
  }
}

const remove = async (id) => {
  try {
    await $fetch(`${config.public.apiBase}/api/admin/news/${id}`, { method: 'DELETE', headers: headers.value })
    ElMessage.success(t('admin.deleteSuccess'))
    fetchData()
  } catch (error) {
    ElMessage.error(t('admin.deleteFailed'))
  }
}

const handleImageUploadSuccess = (response) => {
  uploading.value = false
  uploadPercentage.value = 0
  if (response.code === 200 && response.data) {
    form.imageUrl = typeof response.data === 'string' ? response.data : response.data.url
    ElMessage.success(t('admin.uploadSuccess'))
  } else {
    ElMessage.error(response.message || t('admin.uploadFailed'))
  }
}

const handleUploadProgress = (event) => {
  uploading.value = true
  uploadPercentage.value = Math.round(event.percent || 0)
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
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 15px rgba(16, 185, 129, 0.3);
}
.header-text h1 {
  font-size: 1.25rem;
  margin-bottom: 4px;
}
.add-btn {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%) !important;
  border: none !important;
  box-shadow: 0 4px 15px rgba(16, 185, 129, 0.3);
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
