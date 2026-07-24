<template>
  <div class="page-container">
    <!-- Page Header -->
    <div class="page-header mb-6">
      <div class="header-content">
        <div class="header-icon">
          <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
          </svg>
        </div>
        <div class="header-text">
          <h1 class="text-2xl font-bold text-gray-800">{{ $t('admin.downloads') }}</h1>
          <p class="text-gray-500 text-sm mt-1">管理下载文件资源</p>
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
          placeholder="搜索名称..."
          clearable
          class="!w-64"
          @keyup.enter="handleSearch"
        />
        <el-select v-model="searchCategory" placeholder="分类" clearable class="!w-40">
          <el-option label="全部" value="" />
          <el-option label="Manuals" value="manuals" />
          <el-option label="Software" value="software" />
          <el-option label="Datasheets" value="datasheets" />
          <el-option label="Catalogs" value="catalogs" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
    </el-card>

    <el-card shadow="never" class="main-card">
      <el-table :data="displayData" class="admin-table">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="category" :label="$t('admin.category')" width="120">
          <template #default="{ row }">
            <el-tag size="small" effect="light">{{ row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="name" :label="$t('admin.nameEn')" />
        <el-table-column prop="fileSize" :label="$t('admin.fileSize')" width="80" />
        <el-table-column :label="$t('admin.downloadFile')" width="120">
          <template #default="{ row }">
            <el-button v-if="row.fileUrl" size="small" text type="primary" @click="downloadFile(row.fileUrl, row.originalFilename)">
              下载
            </el-button>
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

    <el-dialog v-model="showDialog" :title="isEdit ? $t('admin.edit') + ' ' + $t('admin.download') : $t('admin.add') + ' ' + $t('admin.download')" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item :label="$t('admin.category')">
          <el-select v-model="form.category">
            <el-option label="Manuals" value="manuals" />
            <el-option label="Software" value="software" />
            <el-option label="Datasheets" value="datasheets" />
            <el-option label="Catalogs" value="catalogs" />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('admin.nameEn')">
          <el-input v-model="form.nameEn" />
        </el-form-item>
        <el-form-item :label="$t('admin.nameZh')">
          <el-input v-model="form.nameZh" />
        </el-form-item>
        <el-form-item :label="$t('admin.descriptionEn')">
          <el-input v-model="form.descriptionEn" type="textarea" rows="2" />
        </el-form-item>
        <el-form-item :label="$t('admin.descriptionZh')">
          <el-input v-model="form.descriptionZh" type="textarea" rows="2" />
        </el-form-item>
        <el-form-item :label="$t('admin.fileUrl')">
          <div class="flex items-center gap-4">
            <el-upload
              :action="`${config.public.apiBase}/api/admin/upload`"
              :headers="headers"
              :show-file-list="false"
              :on-success="handleFileUploadSuccess"
              :on-error="handleFileUploadError"
              :on-progress="handleFileUploadProgress"
              accept=".pdf,.doc,.docx,.xls,.xlsx,.exe,.zip,.rar"
            >
              <el-button type="primary" plain :disabled="uploading">{{ $t('admin.uploadFile') }}</el-button>
            </el-upload>
            <el-input v-model="form.fileUrl" :placeholder="$t('admin.fileUrlPlaceholder')" class="flex-1" />
          </div>
          <div v-if="uploading" class="mt-2">
            <el-progress :percentage="uploadPercentage" :stroke-width="8" />
          </div>
          <div v-if="form.fileUrl" class="mt-2 text-sm text-gray-500">
            {{ $t('admin.currentFile') }}: {{ form.fileUrl }}
          </div>
        </el-form-item>
        <el-form-item :label="$t('admin.fileSize')">
          <el-input v-model="form.fileSize" placeholder="e.g. 2.5MB" />
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
      (item.nameEn && item.nameEn.toLowerCase().includes(kw)) ||
      (item.nameZh && item.nameZh.toLowerCase().includes(kw))
    )
  }
  return result
})

function handleSearch() {}

function resetSearch() {
  searchKeyword.value = ''
  searchCategory.value = ''
}

const form = reactive({
  id: null,
  category: 'manuals',
  nameEn: '',
  nameZh: '',
  descriptionEn: '',
  descriptionZh: '',
  fileUrl: '',
  fileSize: '',
  originalFilename: '',
})

const fetchData = async () => {
  try {
    const response = await $fetch(`${config.public.apiBase}/api/admin/downloads`, { headers: headers.value })
    data.value = response.data || []
  } catch (error) {
    ElMessage.error(t('admin.fetchFailed'))
  }
}

const addNew = () => {
  isEdit.value = false
  Object.keys(form).forEach(k => form[k] = k === 'category' ? 'manuals' : '')
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
      ? `${config.public.apiBase}/api/admin/downloads/${form.id}`
      : `${config.public.apiBase}/api/admin/downloads`

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
    await $fetch(`${config.public.apiBase}/api/admin/downloads/${id}`, { method: 'DELETE', headers: headers.value })
    ElMessage.success(t('admin.deleteSuccess'))
    fetchData()
  } catch (error) {
    ElMessage.error(t('admin.deleteFailed'))
  }
}

const handleFileUploadProgress = (event, uploadFile) => {
  uploading.value = true
  uploadPercentage.value = Math.round(event.percent || 0)
}

const handleFileUploadSuccess = (response) => {
  uploading.value = false
  uploadPercentage.value = 0
  if (response.code === 200 && response.data) {
    form.fileUrl = response.data.url
    form.originalFilename = response.data.originalFilename
    ElMessage.success(t('admin.uploadSuccess'))
  } else {
    ElMessage.error(t('admin.uploadFailed'))
  }
}

const handleFileUploadError = () => {
  uploading.value = false
  uploadPercentage.value = 0
  ElMessage.error(t('admin.uploadFailed'))
}

const getFullDownloadUrl = (url, filename) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  let downloadUrl = `${config.public.apiBase}${url}`
  if (filename) {
    const encodedName = encodeURIComponent(filename)
    downloadUrl += `?name=${encodedName}`
  }
  return downloadUrl
}

const downloadFile = (url, filename) => {
  if (!url) return
  const fullUrl = getFullDownloadUrl(url, filename)
  window.open(fullUrl, '_blank')
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
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 15px rgba(245, 158, 11, 0.3);
}
.header-text h1 {
  font-size: 1.25rem;
  margin-bottom: 4px;
}
.add-btn {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%) !important;
  border: none !important;
  box-shadow: 0 4px 15px rgba(245, 158, 11, 0.3);
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
