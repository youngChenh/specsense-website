<template>
  <div class="page-container">
    <!-- Page Header -->
    <div class="page-header mb-6">
      <div class="header-content">
        <div class="header-icon">
          <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
          </svg>
        </div>
        <div class="header-text">
          <h1 class="text-2xl font-bold text-gray-800">导航菜单管理</h1>
          <p class="text-gray-500 text-sm mt-1">管理网站顶部和底部导航菜单</p>
        </div>
      </div>
      <el-button type="primary" @click="addMenu" class="add-btn">
        <svg class="w-4 h-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        添加菜单
      </el-button>
    </div>

    <!-- Tabs for Header and Footer -->
    <el-card shadow="never" class="main-card">
      <el-tabs v-model="activeTab" @tab-change="fetchMenus" class="menu-tabs">
        <el-tab-pane label="顶部导航" name="header">
          <el-table
            :data="headerTreeData"
            style="width: 100%"
            row-key="id"
            :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
            default-expand-all
            class="admin-table"
          >
            <el-table-column prop="labelZh" label="中文标签" width="150">
              <template #default="{ row }">
                <span>{{ row.labelZh }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="labelEn" label="英文标签" width="150" />
            <el-table-column prop="key" label="Key" width="150" />
            <el-table-column prop="path" label="链接路径" />
            <el-table-column prop="sortOrder" label="排序" width="80" />
            <el-table-column label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.enabled ? 'success' : 'info'" size="small" effect="light">
                  {{ row.enabled ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <el-button type="primary" text size="small" @click="addSubMenu(row)" v-if="row.position === 'header'">添加子菜单</el-button>
                <el-button type="primary" text size="small" @click="editMenu(row)">编辑</el-button>
                <el-button type="danger" text size="small" @click="deleteMenu(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="底部导航" name="footer">
          <el-table
            :data="footerTreeData"
            style="width: 100%"
            row-key="id"
            :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
            default-expand-all
            class="admin-table"
          >
            <el-table-column prop="labelZh" label="中文标签" width="150">
              <template #default="{ row }">
                <span>{{ row.labelZh }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="labelEn" label="英文标签" width="150" />
            <el-table-column prop="key" label="Key" width="180" />
            <el-table-column prop="path" label="链接路径" />
            <el-table-column prop="sortOrder" label="排序" width="80" />
            <el-table-column label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.enabled ? 'success' : 'info'" size="small" effect="light">
                  {{ row.enabled ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <el-button type="primary" text size="small" @click="addSubMenu(row)">添加子菜单</el-button>
                <el-button type="primary" text size="small" @click="editMenu(row)">编辑</el-button>
                <el-button type="danger" text size="small" @click="deleteMenu(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- Menu Dialog -->
    <el-dialog v-model="showDialog" :title="isEdit ? '编辑菜单' : '添加菜单'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="位置">
          <el-select v-model="form.position" class="w-full" :disabled="isEdit">
            <el-option label="顶部导航" value="header" />
            <el-option label="底部导航" value="footer" />
          </el-select>
        </el-form-item>
        <el-form-item label="父菜单">
          <el-select v-model="form.parentId" class="w-full" clearable placeholder="顶级菜单">
            <el-option
              v-for="menu in parentMenuOptions"
              :key="menu.id"
              :label="menu.labelZh + ' / ' + menu.labelEn"
              :value="menu.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="Key">
          <el-input v-model="form.key" placeholder="如: header-home" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="英文标签">
          <el-input v-model="form.labelEn" placeholder="Home" />
        </el-form-item>
        <el-form-item label="中文标签">
          <el-input v-model="form.labelZh" placeholder="首页" />
        </el-form-item>
        <el-form-item label="链接路径">
          <el-input v-model="form.path" placeholder="/home" />
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
        <el-button type="primary" @click="saveMenu">保存</el-button>
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
  fetchMenus()
})

const activeTab = ref('header')
const headerMenus = ref([])
const footerMenus = ref([])
const showDialog = ref(false)
const isEdit = ref(false)
const isSubMenu = ref(false)

const form = reactive({
  id: null,
  key: '',
  position: 'header',
  parentId: null,
  labelEn: '',
  labelZh: '',
  path: '',
  sortOrder: 0,
  enabled: true,
})

// Build tree structure for header menus
const headerTreeData = computed(() => {
  return buildTree(headerMenus.value)
})

// Build tree structure for footer menus
const footerTreeData = computed(() => {
  return buildTree(footerMenus.value)
})

// Build parent menu options for dropdown (based on current position)
const parentMenuOptions = computed(() => {
  const menus = form.position === 'header' ? headerMenus.value : footerMenus.value
  return menus.filter(m => !m.parentId)
})

function buildTree(menus) {
  const map = {}
  const roots = []

  // Create a map of all menus
  menus.forEach(menu => {
    map[menu.id] = { ...menu, children: [] }
  })

  // Build the tree
  menus.forEach(menu => {
    if (menu.parentId) {
      if (map[menu.parentId]) {
        map[menu.parentId].children.push(map[menu.id])
      }
    } else {
      roots.push(map[menu.id])
    }
  })

  // Sort by sortOrder
  const sortByOrder = (arr) => {
    arr.sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0))
    arr.forEach(item => {
      if (item.children && item.children.length > 0) {
        sortByOrder(item.children)
      }
    })
    return arr
  }

  return sortByOrder(roots)
}

const fetchMenus = async () => {
  try {
    const response = await $fetch(`${config.public.apiBase}/api/admin/menus`, { headers: headers.value })
    const allMenus = response.data || []
    headerMenus.value = allMenus.filter(m => m.position === 'header')
    footerMenus.value = allMenus.filter(m => m.position === 'footer')
  } catch (error) {
    ElMessage.error('获取菜单失败')
  }
}

const addMenu = () => {
  isEdit.value = false
  isSubMenu.value = false
  Object.keys(form).forEach(k => {
    if (k === 'sortOrder') form[k] = 0
    else if (k === 'enabled') form[k] = true
    else if (k === 'position') form[k] = activeTab.value
    else if (k === 'parentId') form[k] = null
    else form[k] = ''
  })
  showDialog.value = true
}

const addSubMenu = (parent) => {
  isEdit.value = false
  isSubMenu.value = true
  Object.keys(form).forEach(k => {
    if (k === 'sortOrder') form[k] = 0
    else if (k === 'enabled') form[k] = true
    else if (k === 'position') form[k] = parent.position
    else if (k === 'parentId') form[k] = parent.id
    else if (k === 'key') form[k] = parent.key + '-child'
    else form[k] = ''
  })
  showDialog.value = true
}

const editMenu = (row) => {
  isEdit.value = true
  isSubMenu.value = false
  Object.assign(form, {
    id: row.id,
    key: row.key || '',
    position: row.position || 'header',
    parentId: row.parentId || null,
    labelEn: row.labelEn || '',
    labelZh: row.labelZh || '',
    path: row.path || '',
    sortOrder: row.sortOrder || 0,
    enabled: row.enabled !== false,
  })
  showDialog.value = true
}

const saveMenu = async () => {
  if (!form.key || !form.labelEn || !form.labelZh) {
    ElMessage.warning('请填写完整信息')
    return
  }

  try {
    const method = isEdit.value ? 'PUT' : 'POST'
    const url = isEdit.value
      ? `${config.public.apiBase}/api/admin/menus/${form.id}`
      : `${config.public.apiBase}/api/admin/menus`

    await $fetch(url, { method, headers: headers.value, body: form })
    ElMessage.success('保存成功')
    showDialog.value = false
    fetchMenus()
  } catch (error) {
    ElMessage.error('保存失败: ' + (error.data?.message || error.message || ''))
  }
}

const deleteMenu = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除此菜单吗？', '删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    await $fetch(`${config.public.apiBase}/api/admin/menus/${id}`, { method: 'DELETE', headers: headers.value })
    ElMessage.success('删除成功')
    fetchMenus()
  } catch (error) {
    if (error !== 'cancel') {
      const errorMsg = error.data?.message || error._data?.message || error.message || ''
      ElMessage.error('删除失败' + (errorMsg ? ': ' + errorMsg : ''))
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
  background: linear-gradient(135deg, #64748b 0%, #475569 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 15px rgba(100, 116, 139, 0.3);
}
.header-text h1 {
  font-size: 1.25rem;
  margin-bottom: 4px;
}
.add-btn {
  background: linear-gradient(135deg, #64748b 0%, #475569 100%) !important;
  border: none !important;
  box-shadow: 0 4px 15px rgba(100, 116, 139, 0.3);
}
.add-btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}
.main-card {
  border-radius: 12px;
  border: none;
}
.menu-tabs :deep(.el-tabs__item) {
  font-weight: 500;
}
.menu-tabs :deep(.el-tabs__item.is-active) {
  color: #667eea;
}
.menu-tabs :deep(.el-tabs__active-bar) {
  background-color: #667eea;
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
