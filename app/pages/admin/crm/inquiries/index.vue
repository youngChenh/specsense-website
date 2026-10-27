<template>
  <div class="page-container">
    <div class="page-header mb-4">
      <div class="header-content">
        <div class="header-icon">
          <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
          </svg>
        </div>
        <div class="header-text">
          <h1 class="text-2xl font-bold text-gray-800">询盘跟进</h1>
          <p class="text-gray-500 text-sm mt-1">按阶段、来源和日期筛选询盘</p>
        </div>
      </div>
    </div>

    <el-card shadow="never" class="main-card mb-4">
      <div class="filter-grid">
        <el-input v-model="query.keyword" placeholder="客户名称/需求" clearable @keyup.enter="load" />
        <el-select v-model="query.stage" placeholder="询盘阶段" clearable>
          <el-option v-for="s in CRM_STAGES" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
        <el-select v-model="query.source" placeholder="线索来源" clearable>
          <el-option v-for="s in CRM_SOURCES" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
        <el-select v-model="query.country" placeholder="国家/地区" clearable filterable>
          <el-option v-for="c in countries" :key="c" :label="c" :value="c" />
        </el-select>
        <el-select v-if="manager" v-model="query.ownerId" placeholder="所属销售" clearable>
          <el-option label="未分配" :value="-1" />
          <el-option v-for="u in users" :key="u.id" :label="u.displayName || u.username" :value="u.id" />
        </el-select>
        <el-date-picker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD" start-placeholder="开始日期" end-placeholder="结束日期" />
        <div class="flex gap-2">
          <el-button type="primary" @click="load">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="main-card">
      <el-table :data="list" class="admin-table" @row-click="row => goDetail(row.id)">
        <el-table-column prop="customerName" label="客户" min-width="140" />
        <el-table-column prop="contactName" label="联系人" width="110" />
        <el-table-column prop="inquiryDate" label="询盘日期" width="120" />
        <el-table-column label="阶段" width="110">
          <template #default="{ row }">
            <el-tag size="small">{{ stageLabel(row.stage) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="来源" width="120">
          <template #default="{ row }">{{ sourceLabel(row.source) }}</template>
        </el-table-column>
        <el-table-column label="销售" width="170">
          <template #default="{ row }">
            <el-select
              v-if="manager"
              :model-value="row.ownerId"
              placeholder="未分配"
              clearable
              size="small"
              teleported
              @click.stop
              @change="val => assign(row, val)"
            >
              <el-option v-for="u in users" :key="u.id" :label="u.displayName || u.username" :value="u.id" />
            </el-select>
            <span v-else>{{ row.ownerName || '未分配' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="nextFollowDate" label="下次跟进" width="120" />
        <el-table-column label="关闭" width="80">
          <template #default="{ row }">{{ row.closed === 1 ? '是' : '否' }}</template>
        </el-table-column>
        <el-table-column width="90">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click.stop="goDetail(row.id)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="flex justify-end mt-4">
        <el-pagination v-model:current-page="query.page" :total="total" layout="total, prev, pager, next" @current-change="load" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { CRM_STAGES, CRM_SOURCES, stageLabel, sourceLabel, useCrmApi, isCrmManager } from '~/composables/useCrm'

definePageMeta({ layout: 'admin' })

const api = useCrmApi()
const manager = ref(false)
const list = ref([])
const total = ref(0)
const countries = ref([])
const users = ref([])
const dateRange = ref([])
const query = reactive({ keyword: '', stage: '', source: '', country: '', ownerId: undefined, page: 1, pageSize: 20 })

const goDetail = (id) => navigateTo(`/admin/crm/inquiries/${id}`)

const load = async () => {
  const params = {
    ...query,
    ownerId: query.ownerId === -1 ? undefined : query.ownerId,
    unassigned: query.ownerId === -1 ? 1 : undefined,
    dateFrom: dateRange.value?.[0],
    dateTo: dateRange.value?.[1],
  }
  const res = await api.inquiries(params)
  list.value = res.data?.data || []
  total.value = res.data?.total || 0
}

const assign = async (row, ownerId) => {
  try {
    await api.assignOwner(row.id, ownerId || null)
    const user = users.value.find(u => u.id === ownerId)
    row.ownerId = ownerId || null
    row.ownerName = user ? (user.displayName || user.username) : ''
    ElMessage.success(ownerId ? '已分配销售' : '已取消分配')
  } catch (e) {
    ElMessage.error(e.data?.message || e._data?.message || '分配失败')
    load()
  }
}

const reset = () => {
  query.keyword = ''
  query.stage = ''
  query.source = ''
  query.country = ''
  query.ownerId = undefined
  dateRange.value = []
  query.page = 1
  load()
}

onMounted(async () => {
  if (!localStorage.getItem('admin_token')) return navigateTo('/admin/login')
  manager.value = isCrmManager()
  const route = useRoute()
  if (route.query.keyword) query.keyword = String(route.query.keyword)
  countries.value = (await api.countries()).data || []
  if (manager.value) users.value = (await api.users(true)).data || []
  await load()
})
</script>

<style scoped>
.page-header { display:flex; justify-content:space-between; align-items:center; background:#fff; border-radius:12px; padding:20px 24px; }
.header-content { display:flex; align-items:center; gap:16px; }
.header-icon { width:48px; height:48px; background:linear-gradient(135deg,#f59e0b,#d97706); border-radius:12px; display:flex; align-items:center; justify-content:center; color:#fff; }
.main-card { border:none; border-radius:12px; }
.filter-grid { display:grid; grid-template-columns:repeat(auto-fill,minmax(180px,1fr)); gap:12px; align-items:center; }
</style>
