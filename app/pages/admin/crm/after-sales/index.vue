<template>
  <div class="page-container">
    <div class="page-header mb-4">
      <div class="header-content">
        <div class="header-icon">
          <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z" />
          </svg>
        </div>
        <div class="header-text">
          <h1 class="text-2xl font-bold text-gray-800">售后问题</h1>
          <p class="text-gray-500 text-sm mt-1">登记产品问题，按供应商和型号统计是否该更换供应商</p>
        </div>
      </div>
      <el-button type="primary" @click="openForm()">登记问题</el-button>
    </div>

    <el-card shadow="never" class="main-card mb-4">
      <h2 class="section-title">按供应商 / 型号统计</h2>
      <el-table :data="stats" size="small" class="admin-table">
        <el-table-column prop="supplierName" label="供应商" min-width="140" />
        <el-table-column prop="model" label="型号" min-width="120" />
        <el-table-column prop="issueCount" label="问题次数" width="100" />
        <el-table-column prop="openCount" label="未解决" width="90" />
        <el-table-column label="建议换供应商" width="130">
          <template #default="{ row }">
            <el-tag :type="row.suggestChange ? 'danger' : 'success'" size="small">{{ row.suggestChange ? '建议更换' : '暂不需要' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <p v-if="!stats.length" class="text-gray-400 text-sm mt-2">暂无售后数据</p>
    </el-card>

    <el-card shadow="never" class="main-card mb-4">
      <div class="filter-grid">
        <el-input v-model="query.keyword" placeholder="供应商/型号/问题/客户" clearable @keyup.enter="load" />
        <el-select v-model="query.resolved" placeholder="是否解决" clearable>
          <el-option label="未解决" :value="0" />
          <el-option label="已解决" :value="1" />
        </el-select>
        <div class="flex gap-2">
          <el-button type="primary" @click="load">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="main-card">
      <el-table :data="list" class="admin-table">
        <el-table-column prop="supplierName" label="供应商" min-width="130" />
        <el-table-column prop="model" label="型号" width="120" />
        <el-table-column prop="problem" label="问题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="customerName" label="客户" width="120" />
        <el-table-column label="是否解决" width="90">
          <template #default="{ row }">
            <el-tag :type="row.resolved === 1 ? 'success' : 'warning'" size="small">{{ row.resolved === 1 ? '已解决' : '未解决' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="换供应商" width="100">
          <template #default="{ row }">{{ row.needChangeSupplier === 1 ? '是' : '否' }}</template>
        </el-table-column>
        <el-table-column width="140">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="openForm(row)">编辑</el-button>
            <el-button text type="danger" size="small" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="flex justify-end mt-4">
        <el-pagination v-model:current-page="query.page" :total="total" layout="total, prev, pager, next" @current-change="load" />
      </div>
    </el-card>

    <el-dialog v-model="showForm" :title="form.id ? '编辑售后' : '登记售后问题'" width="560px">
      <el-form :model="form" label-width="110px">
        <el-form-item label="客户">
          <el-select v-model="form.customerId" filterable clearable class="w-full">
            <el-option v-for="c in customers" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联订单">
          <el-select v-model="form.orderId" filterable clearable class="w-full" placeholder="可选">
            <el-option v-for="o in orders" :key="o.id" :label="`${o.orderNo || o.id} ${o.model || ''}`" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="供应商" required><el-input v-model="form.supplierName" /></el-form-item>
        <el-form-item label="具体型号" required><el-input v-model="form.model" /></el-form-item>
        <el-form-item label="具体问题" required><el-input v-model="form.problem" type="textarea" /></el-form-item>
        <el-form-item label="是否解决">
          <el-radio-group v-model="form.resolved">
            <el-radio :value="0">未解决</el-radio>
            <el-radio :value="1">已解决</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否更换供应商">
          <el-radio-group v-model="form.needChangeSupplier">
            <el-radio :value="0">否</el-radio>
            <el-radio :value="1">是</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showForm = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { useCrmApi } from '~/composables/useCrm'

definePageMeta({ layout: 'admin' })

const api = useCrmApi()
const list = ref([])
const stats = ref([])
const total = ref(0)
const customers = ref([])
const orders = ref([])
const showForm = ref(false)
const query = reactive({ keyword: '', resolved: undefined, page: 1, pageSize: 20 })
const emptyForm = () => ({
  id: null,
  customerId: undefined,
  orderId: undefined,
  supplierName: '',
  model: '',
  problem: '',
  resolved: 0,
  needChangeSupplier: 0,
  remark: '',
})
const form = reactive(emptyForm())

const load = async () => {
  const [res, st] = await Promise.all([api.afterSales(query), api.afterSaleStats()])
  list.value = res.data?.data || []
  total.value = res.data?.total || 0
  stats.value = st.data || []
}

const reset = () => {
  query.keyword = ''
  query.resolved = undefined
  query.page = 1
  load()
}

const openForm = (row) => {
  Object.assign(form, emptyForm(), row || {})
  showForm.value = true
}

const save = async () => {
  if (!form.supplierName || !form.model || !form.problem) return ElMessage.warning('请填写供应商、型号和问题')
  if (form.id) await api.updateAfterSale(form.id, form)
  else await api.createAfterSale(form)
  ElMessage.success('已保存')
  showForm.value = false
  load()
}

const remove = async (row) => {
  await ElMessageBox.confirm('删除这条售后记录？', '确认')
  await api.deleteAfterSale(row.id)
  load()
}

onMounted(async () => {
  if (!localStorage.getItem('admin_token')) return navigateTo('/admin/login')
  customers.value = (await api.customers({ page: 1, pageSize: 200 })).data?.data || []
  orders.value = (await api.orders({ page: 1, pageSize: 200 })).data?.data || []
  await load()
})
</script>

<style scoped>
.page-header { display:flex; justify-content:space-between; align-items:center; background:#fff; border-radius:12px; padding:20px 24px; }
.header-content { display:flex; align-items:center; gap:16px; }
.header-icon { width:48px; height:48px; background:linear-gradient(135deg,#f59e0b,#dc2626); border-radius:12px; display:flex; align-items:center; justify-content:center; color:#fff; }
.main-card { border:none; border-radius:12px; }
.section-title { font-size:16px; font-weight:600; margin-bottom:12px; }
.filter-grid { display:grid; grid-template-columns:repeat(auto-fill,minmax(180px,1fr)); gap:12px; align-items:center; }
</style>
