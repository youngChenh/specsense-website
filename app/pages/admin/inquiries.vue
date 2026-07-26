<template>
  <div class="page-container">
    <!-- Page Header -->
    <div class="page-header mb-6">
      <div class="header-content">
        <div class="header-icon">
          <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
          </svg>
        </div>
        <div class="header-text">
          <h1 class="text-2xl font-bold text-gray-800">{{ $t('admin.inquiries') }}</h1>
          <p class="text-gray-500 text-sm mt-1">管理客户询盘信息</p>
        </div>
      </div>
    </div>

    <!-- Search & Filter -->
    <el-card shadow="never" class="main-card mb-4">
      <div class="flex flex-wrap gap-4 items-center">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索名称/邮箱/公司..."
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
        <el-table-column prop="name" :label="$t('admin.name')" />
        <el-table-column prop="email" :label="$t('contact.form.email')" />
        <el-table-column prop="company" :label="$t('contact.form.company')" />
        <el-table-column prop="product" :label="$t('contact.form.product')">
          <template #default="{ row }">
            <el-tag size="small" effect="light" type="info">{{ row.product || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="message" :label="$t('admin.message')" show-overflow-tooltip />
        <el-table-column prop="createdAt" :label="$t('admin.date')" width="150">
          <template #default="{ row }">
            {{ new Date(row.createdAt).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column :label="$t('admin.actions')" width="100">
          <template #default="{ row }">
            <el-button size="small" text type="danger" @click="remove(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
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

const data = ref([])
const searchKeyword = ref('')

const displayData = computed(() => {
  if (!searchKeyword.value) return data.value
  const kw = searchKeyword.value.toLowerCase()
  return data.value.filter(item =>
    (item.name && item.name.toLowerCase().includes(kw)) ||
    (item.email && item.email.toLowerCase().includes(kw)) ||
    (item.company && item.company.toLowerCase().includes(kw))
  )
})

function handleSearch() {}

function resetSearch() {
  searchKeyword.value = ''
}

const fetchData = async () => {
  try {
    const response = await $fetch(`${config.public.apiBase}/api/admin/inquiries`, { headers: headers.value })
    data.value = response.data || []
  } catch (error) {
    ElMessage.error(t('admin.fetchFailed'))
  }
}

const remove = async (id) => {
  try {
    await $fetch(`${config.public.apiBase}/api/admin/inquiries/${id}`, { method: 'DELETE', headers: headers.value })
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
