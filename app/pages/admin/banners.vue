<template>
  <div class="page-container">
    <!-- Page Header -->
    <div class="page-header mb-6">
      <div class="header-content">
        <div class="header-icon">
          <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
          </svg>
        </div>
        <div class="header-text">
          <h1 class="text-2xl font-bold text-gray-800">轮播图管理</h1>
          <p class="text-gray-500 text-sm mt-1">管理首页轮播图</p>
        </div>
      </div>
      <el-button type="primary" @click="addNew" class="add-btn">
        <svg class="w-4 h-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        添加轮播图
      </el-button>
    </div>

    <el-card shadow="never" class="main-card">
      <el-table :data="data" class="admin-table" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="图片" width="200">
          <template #default="{ row }">
            <img :src="row.imageUrl" class="banner-thumb" />
          </template>
        </el-table-column>
        <el-table-column prop="titleZh" label="中文标题" />
        <el-table-column prop="titleEn" label="英文标题" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column label="启用" width="80">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'info'" size="small">
              {{ row.enabled ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" text type="primary" @click="edit(row)">编辑</el-button>
            <el-button size="small" text type="danger" @click="remove(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Add/Edit Dialog -->
    <el-dialog v-model="showDialog" :title="isEdit ? '编辑轮播图' : '添加轮播图'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="图片">
          <div class="flex items-center gap-4">
            <el-input v-model="form.imageUrl" placeholder="输入图片URL" />
            <el-button @click="showUploadDialog = true">上传图片</el-button>
          </div>
          <div v-if="form.imageUrl" class="mt-2">
            <img :src="form.imageUrl" class="preview-thumb" />
          </div>
        </el-form-item>
        <el-form-item label="跳转链接">
          <el-input v-model="form.linkUrl" placeholder="如: /products, /contact" />
        </el-form-item>
        <el-form-item label="中文标题">
          <el-input v-model="form.titleZh" />
        </el-form-item>
        <el-form-item label="英文标题">
          <el-input v-model="form.titleEn" />
        </el-form-item>
        <el-form-item label="中文副标题">
          <el-input v-model="form.subtitleZh" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="英文副标题">
          <el-input v-model="form.subtitleEn" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="启用">
          <el-switch v-model="form.enabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="save">确定</el-button>
      </template>
    </el-dialog>

    <!-- Upload Dialog -->
    <el-dialog v-model="showUploadDialog" title="上传图片" width="500px">
      <div class="upload-area" @click="triggerUpload">
        <input type="file" ref="fileInput" accept="image/*" @change="handleFileChange" class="hidden" />
        <div v-if="!uploading">
          <svg class="w-12 h-12 text-gray-400 mx-auto mb-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
          </svg>
          <p class="text-gray-500">点击上传图片</p>
        </div>
        <div v-else class="text-center">
          <el-icon class="is-loading text-2xl text-blue-500"><Loading /></el-icon>
          <p class="mt-2 text-gray-500">上传中...</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { Loading } from '@element-plus/icons-vue'

definePageMeta({
  layout: 'admin',
})

const config = useRuntimeConfig()
const headers = ref({})

const data = ref([])
const loading = ref(false)
const showDialog = ref(false)
const isEdit = ref(false)
const showUploadDialog = ref(false)
const uploading = ref(false)
const uploadPercentage = ref(0)
const fileInput = ref()

const form = reactive({
  id: null,
  imageUrl: '',
  linkUrl: '',
  titleZh: '',
  titleEn: '',
  subtitleZh: '',
  subtitleEn: '',
  sortOrder: 0,
  enabled: true,
  locale: 'zh'
})

onMounted(() => {
  if (!import.meta.client) return
  const token = localStorage.getItem('admin_token')
  headers.value = { Authorization: `Bearer ${token}` }
  if (!token) navigateTo('/admin/login')
  fetchData()
})

const fetchData = async () => {
  loading.value = true
  try {
    const response = await $fetch(`${config.public.apiBase}/api/admin/banners`, { headers: headers.value })
    data.value = response.data || []
  } catch (error) {
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

const addNew = () => {
  isEdit.value = false
  Object.keys(form).forEach(k => {
    if (k === 'enabled') form[k] = true
    else if (k === 'sortOrder') form[k] = 0
    else if (k === 'locale') form[k] = 'zh'
    else form[k] = ''
  })
  showDialog.value = true
}

const edit = (row) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    imageUrl: row.imageUrl || '',
    linkUrl: row.linkUrl || '',
    titleZh: row.titleZh || '',
    titleEn: row.titleEn || '',
    subtitleZh: row.subtitleZh || '',
    subtitleEn: row.subtitleEn || '',
    sortOrder: row.sortOrder || 0,
    enabled: row.enabled,
    locale: row.locale || 'zh'
  })
  showDialog.value = true
}

const save = async () => {
  try {
    const method = isEdit.value ? 'PUT' : 'POST'
    const url = isEdit.value
      ? `${config.public.apiBase}/api/admin/banners/${form.id}`
      : `${config.public.apiBase}/api/admin/banners`

    await $fetch(url, { method, headers: headers.value, body: form })
    ElMessage.success('保存成功')
    showDialog.value = false
    fetchData()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const remove = async (id) => {
  try {
    await $fetch(`${config.public.apiBase}/api/admin/banners/${id}`, { method: 'DELETE', headers: headers.value })
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

function triggerUpload() {
  fileInput.value?.click()
}

async function handleFileChange(e) {
  const file = e.target.files?.[0]
  if (!file) return

  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)

    const response = await fetch(`${config.public.apiBase}/api/admin/upload`, {
      method: 'POST',
      headers: headers.value,
      body: formData
    })
    const result = await response.json()
    if (result.code === 200 && result.data?.url) {
      form.imageUrl = result.data.url
      showUploadDialog.value = false
    } else {
      ElMessage.error('上传失败')
    }
  } catch (error) {
    console.error('Upload failed:', error)
    ElMessage.error('上传失败')
  } finally {
    uploading.value = false
    e.target.value = ''
  }
}
</script>

<style scoped>
.page-container {
  padding: 0;
}
.banner-thumb {
  width: 160px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
}
.preview-thumb {
  width: 200px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}
.upload-area {
  border: 2px dashed #dcdfe6;
  border-radius: 8px;
  padding: 40px;
  text-align: center;
  cursor: pointer;
  transition: border-color 0.3s;
}
.upload-area:hover {
  border-color: #409eff;
}
</style>
