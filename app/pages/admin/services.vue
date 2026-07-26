<template>
  <div class="page-container">
    <!-- Page Header -->
    <div class="page-header mb-6">
      <div class="header-content">
        <div class="header-icon">
          <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 5.636l-3.536 3.536m0 5.656l3.536 3.536M9.172 9.172L5.636 5.636m3.536 9.192l-3.536 3.536M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-5 0a4 4 0 11-8 0 4 4 0 018 0z" />
          </svg>
        </div>
        <div class="header-text">
          <h1 class="text-2xl font-bold text-gray-800">{{ $t('admin.services') }}</h1>
          <p class="text-gray-500 text-sm mt-1">管理服务项目内容</p>
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
        <el-table-column :label="$t('admin.actions')" width="150">
          <template #default="{ row }">
            <el-button size="small" text type="primary" @click="edit(row)">{{ $t('admin.edit') }}</el-button>
            <el-button size="small" text type="danger" @click="remove(row.id)">{{ $t('admin.delete') }}</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showDialog" :title="isEdit ? $t('admin.edit') + ' ' + $t('admin.service') : $t('admin.add') + ' ' + $t('admin.service')" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item :label="$t('admin.type')">
          <el-input v-model="form.type" :disabled="isEdit" />
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

const form = reactive({
  id: null,
  type: '',
  titleEn: '',
  titleZh: '',
  descriptionEn: '',
  descriptionZh: '',
})

const fetchData = async () => {
  try {
    const response = await $fetch(`${config.public.apiBase}/api/admin/services`, { headers: headers.value })
    data.value = response.data || []
  } catch (error) {
    ElMessage.error(t('admin.fetchFailed'))
  }
}

const addNew = () => {
  isEdit.value = false
  Object.keys(form).forEach(k => form[k] = '')
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
      ? `${config.public.apiBase}/api/admin/services/${form.id}`
      : `${config.public.apiBase}/api/admin/services`

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
    await $fetch(`${config.public.apiBase}/api/admin/services/${id}`, { method: 'DELETE', headers: headers.value })
    ElMessage.success(t('admin.deleteSuccess'))
    fetchData()
  } catch (error) {
    const errorMsg = error.data?.message || error._data?.message || error.message || ''
    ElMessage.error(t('admin.deleteFailed') + (errorMsg ? ': ' + errorMsg : ''))
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
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 15px rgba(59, 130, 246, 0.3);
}
.header-text h1 {
  font-size: 1.25rem;
  margin-bottom: 4px;
}
.add-btn {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%) !important;
  border: none !important;
  box-shadow: 0 4px 15px rgba(59, 130, 246, 0.3);
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
