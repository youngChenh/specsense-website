<template>
  <div class="page-container">
    <!-- Page Header -->
    <div class="page-header mb-6">
      <div class="header-content">
        <div class="header-icon">
          <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
          </svg>
        </div>
        <div class="header-text">
          <h1 class="text-2xl font-bold text-gray-800">{{ $t('admin.categories') }}</h1>
          <p class="text-gray-500 text-sm mt-1">管理产品分类层级结构</p>
        </div>
      </div>
      <el-button type="primary" @click="addRootCategory" class="add-btn">
        <svg class="w-4 h-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        {{ $t('admin.add') }}
      </el-button>
    </div>

    <el-card shadow="never" class="main-card">
      <el-tree
        :data="treeData"
        :props="treeProps"
        node-key="id"
        default-expand-all
        :expand-on-click-node="false"
        class="category-tree"
      >
        <template #default="{ node, data }">
          <span class="tree-node">
            <span class="node-label">
              <span class="font-medium text-gray-800">{{ data.nameEn }}</span>
              <span class="text-gray-400 mx-2">/</span>
              <span class="text-gray-500">{{ data.nameZh }}</span>
            </span>
            <span class="node-actions">
              <el-button size="small" text type="primary" @click="addChildCategory(data)">
                <span class="text-xs">添加</span>
              </el-button>
              <el-button size="small" text type="primary" @click="edit(data)">编辑</el-button>
              <el-button size="small" text type="danger" @click="remove(data.id)">删除</el-button>
            </span>
          </span>
        </template>
      </el-tree>
    </el-card>

    <el-dialog
      v-model="showDialog"
      :title="isEdit ? $t('admin.edit') + ' ' + $t('admin.category') : $t('admin.add') + ' ' + $t('admin.category')"
      width="500px"
    >
      <el-form :model="form" label-width="100px">
        <el-form-item :label="$t('admin.nameEn')">
          <el-input v-model="form.nameEn" :placeholder="$t('admin.pleaseEnter') + $t('admin.nameEn')" />
        </el-form-item>
        <el-form-item :label="$t('admin.nameZh')">
          <el-input v-model="form.nameZh" :placeholder="$t('admin.pleaseEnter') + $t('admin.nameZh')" />
        </el-form-item>
        <el-form-item :label="$t('admin.key')">
          <el-input v-model="form.key" :disabled="isEdit" :placeholder="$t('admin.pleaseEnter') + $t('admin.key')" />
        </el-form-item>
        <el-form-item :label="$t('admin.category')">
          <el-select
            v-model="form.parentId"
            :placeholder="$t('admin.pleaseSelect')"
            clearable
            class="w-full"
          >
            <el-option
              v-for="cat in flatCategories"
              :key="cat.id"
              :label="cat.nameEn + ' / ' + cat.nameZh"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('admin.sortOrder')">
          <el-input-number v-model="form.sortOrder" :min="0" />
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

const { t, locale } = useI18n()
const config = useRuntimeConfig()
const headers = ref({})

onMounted(() => {
  if (!import.meta.client) return

  const token = localStorage.getItem('admin_token')
  headers.value = { Authorization: `Bearer ${token}` }

  if (!token) navigateTo('/admin/login')
  fetchData()
})

const treeData = ref([])
const flatCategories = ref([])
const showDialog = ref(false)
const isEdit = ref(false)
const isRoot = ref(false)
const form = reactive({
  id: null,
  nameEn: '',
  nameZh: '',
  key: '',
  parentId: null,
  sortOrder: 0,
})

const treeProps = {
  children: 'children',
  label: 'nameEn',
}

const fetchData = async () => {
  try {
    const response = await $fetch(`${config.public.apiBase}/api/admin/categories/tree`, { headers: headers.value })
    treeData.value = response.data || []
    await fetchFlatCategories()
  } catch (error) {
    ElMessage.error(t('admin.fetchFailed'))
  }
}

const fetchFlatCategories = async () => {
  try {
    const response = await $fetch(`${config.public.apiBase}/api/admin/categories`, { headers: headers.value })
    flatCategories.value = response.data || []
  } catch (error) {
    console.error('Failed to fetch flat categories')
  }
}

const flattenTree = (nodes, result = []) => {
  for (const node of nodes) {
    result.push({ id: node.id, nameEn: node.nameEn, nameZh: node.nameZh })
    if (node.children && node.children.length > 0) {
      flattenTree(node.children, result)
    }
  }
  return result
}

watch(treeData, (newVal) => {
  flatCategories.value = flattenTree(newVal)
}, { deep: true })

const addRootCategory = () => {
  isEdit.value = false
  isRoot.value = true
  Object.keys(form).forEach(k => {
    if (k === 'sortOrder') form[k] = 0
    else if (k === 'parentId') form[k] = null
    else form[k] = ''
  })
  showDialog.value = true
}

const addChildCategory = (parent) => {
  isEdit.value = false
  isRoot.value = false
  Object.keys(form).forEach(k => {
    if (k === 'sortOrder') form[k] = 0
    else if (k === 'parentId') form[k] = parent.id
    else form[k] = ''
  })
  showDialog.value = true
}

const edit = (row) => {
  isEdit.value = true
  isRoot.value = false
  Object.assign(form, {
    id: row.id,
    nameEn: row.nameEn,
    nameZh: row.nameZh,
    key: row.key,
    parentId: row.parentId,
    sortOrder: row.sortOrder || 0,
  })
  showDialog.value = true
}

const save = async () => {
  if (!form.nameEn || !form.key) {
    ElMessage.warning(t('admin.pleaseEnter') + 'nameEn and key')
    return
  }

  try {
    const method = isEdit.value ? 'PUT' : 'POST'
    const url = isEdit.value
      ? `${config.public.apiBase}/api/admin/categories/${form.id}`
      : `${config.public.apiBase}/api/admin/categories`

    await $fetch(url, { method, headers: headers.value, body: form })
    ElMessage.success(t('admin.saveSuccess'))
    showDialog.value = false
    fetchData()
  } catch (error) {
    ElMessage.error(t('admin.saveFailed') + ': ' + (error.message || ''))
  }
}

const remove = async (id) => {
  try {
    await ElMessageBox.confirm(t('admin.confirmDelete'), t('admin.delete'), {
      confirmButtonText: t('admin.yes'),
      cancelButtonText: t('admin.cancel'),
      type: 'warning',
    })

    await $fetch(`${config.public.apiBase}/api/admin/categories/${id}`, { method: 'DELETE', headers: headers.value })
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
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 15px rgba(139, 92, 246, 0.3);
}
.header-text h1 {
  font-size: 1.25rem;
  margin-bottom: 4px;
}
.add-btn {
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%) !important;
  border: none !important;
  box-shadow: 0 4px 15px rgba(139, 92, 246, 0.3);
}
.add-btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}
.main-card {
  border-radius: 12px;
  border: none;
}
.category-tree {
  background: transparent;
}
.tree-node {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding-right: 8px;
}
.node-label {
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
</style>