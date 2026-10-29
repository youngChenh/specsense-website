<template>
  <div class="page-container">
    <div class="page-header mb-4">
      <div class="header-content">
        <div class="header-icon">
          <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z" />
          </svg>
        </div>
        <div class="header-text">
          <h1 class="text-2xl font-bold text-gray-800">客户管理</h1>
          <p class="text-gray-500 text-sm mt-1">搜索、筛选、导入和导出客户</p>
        </div>
      </div>
      <div class="flex flex-wrap gap-2">
        <el-button type="primary" @click="openCreate">新增客户</el-button>
        <el-button v-if="manager" @click="downloadTemplate">下载模板</el-button>
        <el-button v-if="manager" @click="triggerImport">导入 Excel</el-button>
        <el-dropdown @command="exportType">
          <el-button>导出 Excel</el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="customers">客户</el-dropdown-item>
              <el-dropdown-item command="contacts">联系人</el-dropdown-item>
              <el-dropdown-item command="inquiries">询盘</el-dropdown-item>
              <el-dropdown-item command="followups">跟进记录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <el-card shadow="never" class="main-card mb-4">
      <div class="filter-grid">
        <el-input v-model="query.keyword" placeholder="搜索客户名称" clearable @keyup.enter="load" />
        <el-select v-model="query.country" placeholder="国家/地区" clearable filterable>
          <el-option v-for="c in countries" :key="c" :label="c" :value="c" />
        </el-select>
        <el-select v-model="query.ownerId" placeholder="所属销售" clearable v-if="manager">
          <el-option v-for="u in users" :key="u.id" :label="u.displayName || u.username" :value="u.id" />
        </el-select>
        <el-select v-model="query.source" placeholder="线索来源" clearable>
          <el-option v-for="s in CRM_SOURCES" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
        <el-select v-model="query.status" placeholder="客户状态" clearable>
          <el-option v-for="s in CRM_STATUS" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
        <div class="flex gap-2">
          <el-button type="primary" @click="load">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="main-card">
      <el-table :data="list" class="admin-table" @row-click="row => goDetail(row.id)">
        <el-table-column prop="id" label="编号" width="70" />
        <el-table-column prop="name" label="客户名称" min-width="160" />
        <el-table-column prop="country" label="国家/地区" width="110" />
        <el-table-column label="来源" width="120">
          <template #default="{ row }">{{ sourceLabel(row.source) }}</template>
        </el-table-column>
        <el-table-column prop="ownerName" label="所属销售" width="120" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag size="small" :type="row.closed === 1 ? 'info' : 'success'">{{ row.closed === 1 ? '已关闭' : statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="contactCount" label="联系人" width="80" />
        <el-table-column prop="inquiryCount" label="询盘" width="80" />
        <el-table-column label="操作" width="140" @click.stop>
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click.stop="goDetail(row.id)">详情</el-button>
            <el-button v-if="row.closed !== 1" text type="warning" size="small" @click.stop="closeRow(row)">关闭</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="flex justify-end mt-4">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="load"
        />
      </div>
    </el-card>

    <el-dialog v-model="showForm" title="新增客户" width="560px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="客户名称" required>
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="国家/地区"><el-input v-model="form.country" /></el-form-item>
        <el-form-item label="官网"><el-input v-model="form.website" /></el-form-item>
        <el-form-item label="行业"><el-input v-model="form.industry" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="form.address" /></el-form-item>
        <el-form-item label="线索来源">
          <el-select v-model="form.source" class="w-full">
            <el-option v-for="s in CRM_SOURCES" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属销售" v-if="manager">
          <el-select v-model="form.ownerId" class="w-full">
            <el-option v-for="u in users" :key="u.id" :label="u.displayName || u.username" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" class="w-full">
            <el-option v-for="s in CRM_STATUS" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" /></el-form-item>
      </el-form>
      <div v-if="dupList.length" class="dup-box">
        <p>发现疑似重复客户，不会自动覆盖：</p>
        <p v-for="d in dupList" :key="d.id">#{{ d.id }} {{ d.name }}（{{ d.reason }}）</p>
      </div>
      <template #footer>
        <el-button @click="showForm = false">取消</el-button>
        <el-button @click="checkDup">检查重复</el-button>
        <el-button type="primary" @click="save">仍要创建</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showImport" title="导入预览" width="90%">
      <p class="text-sm text-gray-500 mb-3">多个邮箱请用顿号「、」分隔，会生成多条联系人（姓名、职位、电话相同，邮箱不同）。疑似重复请选择：跳过 / 合并 / 仍作为新客户。</p>
      <el-table :data="importRows" size="small" max-height="420">
        <el-table-column prop="rowNum" label="行" width="60" />
        <el-table-column prop="name" label="客户" min-width="140" />
        <el-table-column prop="country" label="国家" width="90" />
        <el-table-column prop="contactName" label="联系人" width="110" />
        <el-table-column label="邮箱" min-width="180">
          <template #default="{ row }">{{ (row.contactEmails && row.contactEmails.length) ? row.contactEmails.join('、') : (row.contactEmail || '-') }}</template>
        </el-table-column>
        <el-table-column label="错误" min-width="140">
          <template #default="{ row }">{{ (row.errors || []).join('；') || '-' }}</template>
        </el-table-column>
        <el-table-column label="疑似重复" min-width="180">
          <template #default="{ row }">
            <div v-for="d in row.duplicates || []" :key="d.id">#{{ d.id }} {{ d.name }}</div>
            <span v-if="!(row.duplicates || []).length">无</span>
          </template>
        </el-table-column>
        <el-table-column label="处理" width="160">
          <template #default="{ row }">
            <el-select v-model="row.action" size="small">
              <el-option label="作为新客户" value="create" />
              <el-option label="跳过" value="skip" />
              <el-option label="合并" value="merge" :disabled="!(row.duplicates || []).length" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="合并到" width="160">
          <template #default="{ row }">
            <el-select v-model="row.mergeTargetId" size="small" :disabled="row.action !== 'merge'">
              <el-option v-for="d in row.duplicates || []" :key="d.id" :label="'#' + d.id + ' ' + d.name" :value="d.id" />
            </el-select>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button :disabled="importing" @click="showImport = false">取消</el-button>
        <el-button type="primary" :loading="importing" @click="commitImport">确认导入</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="showProgress"
      :title="progressTitle"
      width="440px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
    >
      <p class="text-sm text-gray-500 mb-3">{{ progressText }}</p>
      <el-progress :percentage="progress" :status="progressStatus" :stroke-width="14" />
    </el-dialog>

    <input ref="fileInput" type="file" accept=".xlsx" class="hidden" @change="onFile" />
  </div>
</template>

<script setup>
import { CRM_SOURCES, CRM_STATUS, sourceLabel, statusLabel, useCrmApi, isCrmManager } from '~/composables/useCrm'

definePageMeta({ layout: 'admin' })

const api = useCrmApi()
const config = useRuntimeConfig()
const manager = ref(false)
const list = ref([])
const total = ref(0)
const countries = ref([])
const users = ref([])
const query = reactive({ keyword: '', country: '', source: '', status: '', ownerId: undefined, page: 1, pageSize: 20 })
const showForm = ref(false)
const form = reactive({ name: '', country: '', website: '', industry: '', address: '', source: 'website', ownerId: undefined, status: 'active', remark: '' })
const dupList = ref([])
const showImport = ref(false)
const importRows = ref([])
const fileInput = ref(null)
const importing = ref(false)
const showProgress = ref(false)
const progress = ref(0)
const progressTitle = ref('正在导入')
const progressText = ref('')
const progressStatus = ref('')
let progressTimer = null

const setProgress = (pct, text, status = '') => {
  progress.value = Math.max(0, Math.min(100, Math.round(pct)))
  if (text) progressText.value = text
  progressStatus.value = status
}

const startProgress = (title, text) => {
  if (progressTimer) clearInterval(progressTimer)
  progressTimer = null
  progressTitle.value = title
  setProgress(0, text, '')
  showProgress.value = true
}

const tickUntil = (target, ms = 400) => {
  if (progressTimer) clearInterval(progressTimer)
  progressTimer = setInterval(() => {
    if (progress.value >= target) {
      clearInterval(progressTimer)
      progressTimer = null
      return
    }
    setProgress(progress.value + 2)
  }, ms)
}

const stopProgress = (ok = true, text = '') => {
  if (progressTimer) clearInterval(progressTimer)
  progressTimer = null
  setProgress(100, text || (ok ? '完成' : '失败'), ok ? 'success' : 'exception')
}

const postJson = (url, body, onUpload) => new Promise((resolve, reject) => {
  const xhr = new XMLHttpRequest()
  xhr.open('POST', url)
  xhr.setRequestHeader('Authorization', api.headers().Authorization || '')
  if (!(body instanceof FormData)) {
    xhr.setRequestHeader('Content-Type', 'application/json')
  }
  xhr.upload.onprogress = (e) => {
    if (e.lengthComputable && onUpload) onUpload(e.loaded, e.total)
  }
  xhr.onload = () => {
    let data = null
    try {
      data = JSON.parse(xhr.responseText || '{}')
    } catch {
      reject(new Error('服务器返回无法解析'))
      return
    }
    if (xhr.status >= 200 && xhr.status < 300 && (!data.code || data.code === 200)) {
      resolve(data)
    } else {
      reject(new Error(data.message || `请求失败（${xhr.status}）`))
    }
  }
  xhr.onerror = () => reject(new Error('网络错误'))
  xhr.send(body instanceof FormData ? body : JSON.stringify(body))
})

const load = async () => {
  const res = await api.customers(query)
  list.value = res.data?.data || []
  total.value = res.data?.total || 0
}

const reset = () => {
  query.keyword = ''
  query.country = ''
  query.source = ''
  query.status = ''
  query.ownerId = undefined
  query.page = 1
  load()
}

const goDetail = (id) => navigateTo(`/admin/crm/customers/${id}`)

const openCreate = () => {
  Object.assign(form, { name: '', country: '', website: '', industry: '', address: '', source: 'website', ownerId: undefined, status: 'active', remark: '' })
  dupList.value = []
  showForm.value = true
}

const checkDup = async () => {
  const res = await api.duplicates(form)
  dupList.value = res.data || []
  if (!dupList.value.length) ElMessage.success('未发现重复客户')
}

const save = async () => {
  if (!form.name) return ElMessage.warning('请填写客户名称')
  await api.createCustomer(form)
  ElMessage.success('已创建')
  showForm.value = false
  load()
}

const closeRow = async (row) => {
  await ElMessageBox.confirm('关闭后仍可搜索查看，但不参与当前统计。确定关闭？', '关闭客户')
  await api.closeCustomer(row.id)
  ElMessage.success('已关闭')
  load()
}

const downloadBlob = async (url, filename) => {
  try {
    const res = await fetch(url, { headers: api.headers() })
    const type = res.headers.get('content-type') || ''
    if (!res.ok || type.includes('application/json')) {
      let msg = '下载失败'
      try {
        const data = await res.json()
        msg = data.message || msg
      } catch {
        msg = `下载失败（${res.status}）`
      }
      ElMessage.error(msg)
      return
    }
    const blob = await res.blob()
    const a = document.createElement('a')
    a.href = URL.createObjectURL(blob)
    a.download = filename
    a.click()
    URL.revokeObjectURL(a.href)
  } catch (e) {
    ElMessage.error(e?.message || '下载失败')
  }
}

const downloadTemplate = () => downloadBlob(`${config.public.apiBase}/api/admin/crm/import/template`, 'CRM客户导入模板.xlsx')
const exportType = (type) => downloadBlob(`${config.public.apiBase}/api/admin/crm/export?type=${type}`, `CRM-${type}.xlsx`)
const triggerImport = () => fileInput.value?.click()

const onFile = async (e) => {
  const file = e.target.files?.[0]
  e.target.value = ''
  if (!file) return
  const fd = new FormData()
  fd.append('file', file)
  importing.value = true
  startProgress('解析 Excel', `正在上传 ${file.name}`)
  tickUntil(50, 200)
  try {
    const res = await postJson(`${config.public.apiBase}/api/admin/crm/import/preview`, fd, (loaded, total) => {
      const pct = total ? Math.round((loaded / total) * 55) : 10
      setProgress(Math.max(pct, 8), `正在上传 ${file.name}（${Math.round(loaded / 1024)} KB）`)
      if (loaded >= total) {
        setProgress(60, '正在解析表格并检查重复客户…')
        tickUntil(88, 350)
      }
    })
    importRows.value = res.data || []
    stopProgress(true, `已读取 ${importRows.value.length} 行`)
    await new Promise(r => setTimeout(r, 400))
    showProgress.value = false
    if (!importRows.value.length) {
      ElMessage.warning('没有读到数据行，请确认用的是当前模板且第二行起有内容')
      return
    }
    showImport.value = true
  } catch (err) {
    stopProgress(false, err?.message || '导入预览失败')
    ElMessage.error(err?.message || '导入预览失败')
    await new Promise(r => setTimeout(r, 800))
    showProgress.value = false
  } finally {
    importing.value = false
  }
}

const commitImport = async () => {
  importing.value = true
  startProgress('正在导入', `准备写入 ${importRows.value.length} 行`)
  tickUntil(90, 280)
  try {
    const res = await postJson(`${config.public.apiBase}/api/admin/crm/import/commit`, importRows.value)
    const created = res.data?.created || 0
    const merged = res.data?.merged || 0
    const skipped = res.data?.skipped || 0
    stopProgress(true, `新增 ${created}，合并 ${merged}，跳过 ${skipped}`)
    ElMessage.success(`导入完成：新增 ${created}，合并 ${merged}，跳过 ${skipped}`)
    await new Promise(r => setTimeout(r, 500))
    showProgress.value = false
    showImport.value = false
    load()
  } catch (err) {
    stopProgress(false, err?.message || '导入失败')
    ElMessage.error(err?.message || '导入失败')
    await new Promise(r => setTimeout(r, 800))
    showProgress.value = false
  } finally {
    importing.value = false
  }
}

onMounted(async () => {
  if (!localStorage.getItem('admin_token')) return navigateTo('/admin/login')
  manager.value = isCrmManager()
  await load()
  const c = await api.countries()
  countries.value = c.data || []
  if (manager.value) {
    const u = await api.users(true)
    users.value = u.data || []
  }
})
</script>

<style scoped>
.page-header { display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:12px; background:#fff; border-radius:12px; padding:20px 24px; }
.header-content { display:flex; align-items:center; gap:16px; }
.header-icon { width:48px; height:48px; background:linear-gradient(135deg,#0ea5e9,#2563eb); border-radius:12px; display:flex; align-items:center; justify-content:center; color:#fff; }
.main-card { border:none; border-radius:12px; }
.filter-grid { display:grid; grid-template-columns:repeat(auto-fill,minmax(180px,1fr)); gap:12px; align-items:center; }
.dup-box { background:#fff7ed; padding:10px 12px; border-radius:8px; color:#9a3412; font-size:13px; }
</style>
