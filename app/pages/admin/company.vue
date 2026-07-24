<template>
  <div class="page-container">
    <!-- Page Header -->
    <div class="page-header mb-6">
      <div class="header-content">
        <div class="header-icon">
          <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
          </svg>
        </div>
        <div class="header-text">
          <h1 class="text-2xl font-bold text-gray-800">{{ $t('admin.company') }}</h1>
          <p class="text-gray-500 text-sm mt-1">管理公司基本信息和内容</p>
        </div>
      </div>
    </div>

    <el-card shadow="never" class="main-card">
      <el-form :model="form" label-width="140px" class="company-form">
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
          <div v-if="form.imageUrl" class="mt-3">
            <img :src="getFullImageUrl(form.imageUrl)" class="w-48 h-32 object-cover rounded-lg border" />
          </div>
        </el-form-item>
        <el-form-item label="关于我们(英文)">
          <el-input v-model="form.aboutEn" type="textarea" rows="3" placeholder="About section English" />
        </el-form-item>
        <el-form-item label="关于我们(中文)">
          <el-input v-model="form.aboutZh" type="textarea" rows="3" placeholder="About section Chinese" />
        </el-form-item>
        <el-form-item label="公司简介(英文)">
          <el-input v-model="form.descriptionEn" type="textarea" rows="4" />
        </el-form-item>
        <el-form-item label="公司简介(中文)">
          <el-input v-model="form.descriptionZh" type="textarea" rows="4" />
        </el-form-item>
        <el-form-item :label="$t('admin.missionEn')">
          <el-input v-model="form.missionEn" type="textarea" rows="3" />
        </el-form-item>
        <el-form-item :label="$t('admin.missionZh')">
          <el-input v-model="form.missionZh" type="textarea" rows="3" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="info@example.com" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" placeholder="+86 400-888-8888" />
        </el-form-item>
        <el-form-item label="地址(英文)">
          <el-input v-model="form.addressEn" placeholder="Company address in English" />
        </el-form-item>
        <el-form-item label="地址(中文)">
          <el-input v-model="form.addressZh" placeholder="公司地址中文" />
        </el-form-item>
        <el-form-item :label="$t('admin.values')">
          <el-input v-model="form.valuesJson" type="textarea" rows="4" placeholder='["Value1", "Value2"]' />
        </el-form-item>
        <el-form-item :label="$t('admin.timeline')">
          <el-input v-model="form.timelineJson" type="textarea" rows="4" />
        </el-form-item>
        <el-form-item :label="$t('admin.stats')">
          <el-input v-model="form.statsJson" type="textarea" rows="2" placeholder='{"key": "value"}' />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="save" class="save-btn">{{ $t('admin.save') }}</el-button>
        </el-form-item>
      </el-form>
    </el-card>
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

const form = reactive({
  imageUrl: '',
  aboutEn: '',
  aboutZh: '',
  descriptionEn: '',
  descriptionZh: '',
  missionEn: '',
  missionZh: '',
  email: '',
  phone: '',
  addressEn: '',
  addressZh: '',
  valuesJson: '',
  timelineJson: '',
  statsJson: '',
})

const uploading = ref(false)
const uploadPercentage = ref(0)

const fetchData = async () => {
  try {
    const response = await $fetch(`${config.public.apiBase}/api/admin/company-info`, { headers: headers.value })
    if (response.data) {
      Object.assign(form, response.data)
    }
  } catch (error) {
    ElMessage.error(t('admin.fetchFailed'))
  }
}

const save = async () => {
  try {
    await $fetch(`${config.public.apiBase}/api/admin/company-info`, {
      method: 'PUT',
      headers: headers.value,
      body: { id: 1, ...form },
    })
    ElMessage.success(t('admin.saveSuccess'))
  } catch (error) {
    ElMessage.error(t('admin.saveFailed'))
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
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 15px rgba(99, 102, 241, 0.3);
}
.header-text h1 {
  font-size: 1.25rem;
  margin-bottom: 4px;
}
.main-card {
  border-radius: 12px;
  border: none;
}
.company-form {
  max-width: 800px;
}
.save-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}
</style>
