<template>
  <div class="page-container">
    <div class="page-header mb-4">
      <div class="header-content">
        <div class="header-icon">
          <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 14l6-6m-5 0h6v6M5 21h14a2 2 0 002-2V5a2 2 0 00-2-2H5a2 2 0 00-2 2v14a2 2 0 002 2z" />
          </svg>
        </div>
        <div class="header-text">
          <h1 class="text-2xl font-bold text-gray-800">订单管理</h1>
          <p class="text-gray-500 text-sm mt-1">毛利 = 卖出价 − 采购价 − 运费 − 保险费 − 银行手续费</p>
        </div>
      </div>
      <el-button type="primary" @click="openForm()">新增订单</el-button>
    </div>

    <el-card shadow="never" class="main-card mb-4">
      <div class="filter-grid">
        <el-input v-model="query.keyword" placeholder="订单号/客户/供应商/型号" clearable @keyup.enter="load" />
        <el-select v-model="query.incoterm" placeholder="成交方式" clearable>
          <el-option v-for="s in CRM_INCOTERMS" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
        <el-select v-if="manager" v-model="query.ownerId" placeholder="所属销售" clearable>
          <el-option v-for="u in users" :key="u.id" :label="u.displayName || u.username" :value="u.id" />
        </el-select>
        <div class="flex gap-2">
          <el-button type="primary" @click="load">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="main-card">
      <el-table :data="list" class="admin-table">
        <el-table-column prop="orderNo" label="订单号" width="110" />
        <el-table-column prop="orderDate" label="日期" width="110" />
        <el-table-column prop="customerName" label="客户" min-width="130" />
        <el-table-column prop="supplierName" label="供应商" min-width="120" />
        <el-table-column prop="model" label="型号" width="110" />
        <el-table-column prop="incoterm" label="条款" width="80" />
        <el-table-column prop="purchasePrice" label="采购价" width="100" />
        <el-table-column prop="sellPrice" label="卖出价" width="100" />
        <el-table-column prop="grossProfit" label="毛利" width="100" />
        <el-table-column label="毛利率" width="90">
          <template #default="{ row }">{{ formatPercent(row.grossMargin) }}</template>
        </el-table-column>
        <el-table-column label="发票" width="80">
          <template #default="{ row }">{{ invoiceLabel(row.invoiceType) }}</template>
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

    <el-dialog v-model="showForm" :title="form.id ? '编辑订单' : '新增订单'" width="720px">
      <el-form :model="form" label-width="110px">
        <el-form-item label="客户">
          <el-select v-model="form.customerId" filterable clearable class="w-full" placeholder="选择客户">
            <el-option v-for="c in customers" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="订单号"><el-input v-model="form.orderNo" placeholder="可空，保存后自动生成" /></el-form-item>
        <el-form-item label="订单日期"><el-date-picker v-model="form.orderDate" value-format="YYYY-MM-DD" class="w-full" /></el-form-item>
        <el-form-item label="供应商" required><el-input v-model="form.supplierName" /></el-form-item>
        <el-form-item label="产品名称"><el-input v-model="form.productName" /></el-form-item>
        <el-form-item label="型号"><el-input v-model="form.model" /></el-form-item>
        <el-form-item label="数量"><el-input-number v-model="form.quantity" :min="0" :precision="2" class="w-full" /></el-form-item>
        <el-form-item label="币种"><el-input v-model="form.currency" /></el-form-item>
        <el-form-item label="采购价"><el-input-number v-model="form.purchasePrice" :min="0" :precision="2" class="w-full" /></el-form-item>
        <el-form-item label="卖出价"><el-input-number v-model="form.sellPrice" :min="0" :precision="2" class="w-full" /></el-form-item>
        <el-form-item label="价格条款">
          <el-select v-model="form.incoterm" class="w-full">
            <el-option v-for="s in CRM_INCOTERMS" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="运费"><el-input-number v-model="form.freight" :min="0" :precision="2" class="w-full" /></el-form-item>
        <el-form-item label="保险费"><el-input-number v-model="form.insurance" :min="0" :precision="2" class="w-full" /></el-form-item>
        <el-form-item label="银行手续费"><el-input-number v-model="form.bankFee" :min="0" :precision="2" class="w-full" /></el-form-item>
        <el-form-item label="含税">
          <el-radio-group v-model="form.taxIncluded">
            <el-radio :value="1">含税</el-radio>
            <el-radio :value="0">不含税</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="发票类型">
          <el-select v-model="form.invoiceType" class="w-full" clearable>
            <el-option v-for="s in CRM_INVOICE_TYPES" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="毛利">{{ money(previewProfit) }}</el-form-item>
        <el-form-item label="毛利率">{{ formatPercent(previewMargin) }}</el-form-item>
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
import { CRM_INCOTERMS, CRM_INVOICE_TYPES, invoiceLabel, useCrmApi, isCrmManager } from '~/composables/useCrm'

definePageMeta({ layout: 'admin' })

const api = useCrmApi()
const manager = ref(false)
const list = ref([])
const total = ref(0)
const users = ref([])
const customers = ref([])
const showForm = ref(false)
const query = reactive({ keyword: '', incoterm: '', ownerId: undefined, customerId: undefined, page: 1, pageSize: 20 })
const emptyForm = () => ({
  id: null,
  customerId: undefined,
  orderNo: '',
  orderDate: new Date().toISOString().slice(0, 10),
  supplierName: '',
  productName: '',
  model: '',
  quantity: 1,
  currency: 'USD',
  purchasePrice: 0,
  sellPrice: 0,
  incoterm: 'FOB',
  freight: 0,
  insurance: 0,
  bankFee: 0,
  taxIncluded: 0,
  invoiceType: 'normal',
  remark: '',
})
const form = reactive(emptyForm())

const n = (v) => Number(v || 0)
const previewProfit = computed(() => n(form.sellPrice) - n(form.purchasePrice) - n(form.freight) - n(form.insurance) - n(form.bankFee))
const previewMargin = computed(() => n(form.sellPrice) === 0 ? 0 : (previewProfit.value / n(form.sellPrice)) * 100)
const money = (v) => Number(v || 0).toFixed(2)
const formatPercent = (v) => `${Number(v || 0).toFixed(2)}%`

const load = async () => {
  const res = await api.orders(query)
  list.value = res.data?.data || []
  total.value = res.data?.total || 0
}

const reset = () => {
  query.keyword = ''
  query.incoterm = ''
  query.ownerId = undefined
  query.page = 1
  load()
}

const openForm = (row) => {
  Object.assign(form, emptyForm(), row ? { ...row, taxIncluded: row.taxIncluded ?? 0 } : {})
  if (row?.customerId && !customers.value.find(c => c.id === row.customerId) && row.customerName) {
    customers.value = [{ id: row.customerId, name: row.customerName }, ...customers.value]
  }
  showForm.value = true
}

const save = async () => {
  if (!form.supplierName) return ElMessage.warning('请填写供应商')
  if (form.id) await api.updateOrder(form.id, form)
  else await api.createOrder(form)
  ElMessage.success('已保存')
  showForm.value = false
  load()
}

const remove = async (row) => {
  await ElMessageBox.confirm(`删除订单 ${row.orderNo || row.id}？`, '确认')
  await api.deleteOrder(row.id)
  load()
}

onMounted(async () => {
  if (!localStorage.getItem('admin_token')) return navigateTo('/admin/login')
  manager.value = isCrmManager()
  const route = useRoute()
  if (route.query.customerId) query.customerId = Number(route.query.customerId)
  customers.value = (await api.customers({ page: 1, pageSize: 200 })).data?.data || []
  if (manager.value) users.value = (await api.users(true)).data || []
  await load()
})
</script>

<style scoped>
.page-header { display:flex; justify-content:space-between; align-items:center; background:#fff; border-radius:12px; padding:20px 24px; }
.header-content { display:flex; align-items:center; gap:16px; }
.header-icon { width:48px; height:48px; background:linear-gradient(135deg,#2563eb,#4f46e5); border-radius:12px; display:flex; align-items:center; justify-content:center; color:#fff; }
.main-card { border:none; border-radius:12px; }
.filter-grid { display:grid; grid-template-columns:repeat(auto-fill,minmax(180px,1fr)); gap:12px; align-items:center; }
</style>
