<template>
  <div class="page-container">
    <div class="page-header mb-4">
      <div class="header-text">
        <h1 class="text-2xl font-bold text-gray-800">用户管理</h1>
        <p class="text-gray-500 text-sm mt-1">创建账号、设置角色、停用和重置密码</p>
      </div>
      <el-button type="primary" @click="openCreate">创建账号</el-button>
    </div>

    <el-card shadow="never" class="main-card">
      <el-table :data="list" class="admin-table">
        <el-table-column prop="username" label="账号" />
        <el-table-column prop="displayName" label="姓名" />
        <el-table-column label="角色">
          <template #default="{ row }">{{ roleLabel(row.role) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'info' : 'success'" size="small">{{ row.status === 0 ? '已停用' : '启用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="重置申请" width="110">
          <template #default="{ row }">
            <el-tag v-if="row.resetRequested === 1" type="warning" size="small">待重置</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="openEdit(row)">编辑</el-button>
            <el-button text type="primary" size="small" @click="openReset(row)">重置密码</el-button>
            <el-button v-if="row.status !== 0" text type="warning" size="small" @click="disable(row)">停用</el-button>
            <el-button v-else text type="success" size="small" @click="enable(row)">启用</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showForm" :title="form.id ? '编辑账号' : '创建账号'" width="440px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="账号">
          <el-input v-model="form.username" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.displayName" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role" class="w-full">
            <el-option label="网站管理员" value="admin" />
            <el-option label="管理层" value="manager" />
            <el-option label="销售" value="sales" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="!form.id" label="初始密码"><el-input v-model="form.password" show-password placeholder="默认 123456" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showForm = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showReset" title="重置密码" width="400px">
      <el-input v-model="newPassword" show-password placeholder="新密码至少6位" />
      <template #footer>
        <el-button @click="showReset = false">取消</el-button>
        <el-button type="primary" @click="doReset">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { useCrmApi, isCrmManager } from '~/composables/useCrm'

definePageMeta({ layout: 'admin' })

const api = useCrmApi()
const list = ref([])
const showForm = ref(false)
const showReset = ref(false)
const newPassword = ref('')
const currentId = ref(null)
const form = reactive({ id: null, username: '', displayName: '', role: 'sales', password: '' })

const roleLabel = (role) => {
  if (role === 'sales') return '销售'
  if (role === 'manager') return '管理层'
  if (role === 'admin') return '网站管理员'
  if (role === 'editor') return '编辑'
  return role
}

const load = async () => {
  list.value = (await api.users(true)).data || []
}

const openCreate = () => {
  Object.assign(form, { id: null, username: '', displayName: '', role: 'sales', password: '' })
  showForm.value = true
}

const openEdit = (row) => {
  Object.assign(form, { id: row.id, username: row.username, displayName: row.displayName, role: row.role, password: '' })
  showForm.value = true
}

const save = async () => {
  if (form.id) {
    await api.updateUser(form.id, { displayName: form.displayName, role: form.role, status: 1 })
  } else {
    await api.createUser(form)
  }
  ElMessage.success('已保存')
  showForm.value = false
  load()
}

const disable = async (row) => {
  await api.disableUser(row.id)
  load()
}

const enable = async (row) => {
  await api.enableUser(row.id)
  load()
}

const openReset = (row) => {
  currentId.value = row.id
  newPassword.value = ''
  showReset.value = true
}

const doReset = async () => {
  await api.resetPassword(currentId.value, newPassword.value)
  ElMessage.success('密码已重置')
  showReset.value = false
  load()
}

onMounted(async () => {
  if (!localStorage.getItem('admin_token')) return navigateTo('/admin/login')
  if (!isCrmManager()) return navigateTo('/admin/crm')
  await load()
})
</script>

<style scoped>
.page-header { display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:12px; background:#fff; border-radius:12px; padding:20px 24px; }
.main-card { border:none; border-radius:12px; }
</style>
