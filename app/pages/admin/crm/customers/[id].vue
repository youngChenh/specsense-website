<template>
  <div class="page-container" v-if="customer">
    <div class="page-header mb-4">
      <div class="header-content">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">{{ customer.name }} <span class="text-gray-400 text-base font-normal">#{{ customer.id }}</span></h1>
          <p class="text-gray-500 text-sm mt-1">{{ customer.country || '未填国家' }} · {{ sourceLabel(customer.source) }} · {{ customer.ownerName || '未分配' }}</p>
        </div>
      </div>
      <div class="flex gap-2">
        <el-button @click="showEdit = true">编辑</el-button>
        <el-button v-if="manager" @click="startMerge">合并到已有客户</el-button>
        <el-button v-if="customer.closed !== 1" type="warning" @click="closeCustomer">标记关闭</el-button>
      </div>
    </div>

    <el-row :gutter="16">
      <el-col :xs="24" :md="10">
        <el-card shadow="never" class="main-card mb-4">
          <h2 class="section-title">基本信息</h2>
          <p>官网：{{ customer.website || '-' }}</p>
          <p>行业：{{ customer.industry || '-' }}</p>
          <p>地址：{{ customer.address || '-' }}</p>
          <p>状态：{{ customer.closed === 1 ? '已关闭' : statusLabel(customer.status) }}</p>
          <p class="mt-2">备注：{{ customer.remark || '-' }}</p>
          <div class="mt-3">
            <div class="text-sm text-gray-500 mb-2">附件</div>
            <div v-for="(f, i) in files" :key="i" class="file-row">
              <a :href="fileHref(f.url)" target="_blank">{{ f.name || f.url }}</a>
            </div>
            <el-upload :action="uploadAction" :headers="uploadHeaders" :show-file-list="false" :on-success="onUpload">
              <el-button size="small">上传附件</el-button>
            </el-upload>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="14">
        <el-card shadow="never" class="main-card mb-4">
          <div class="flex justify-between mb-3">
            <h2 class="section-title mb-0">联系人</h2>
            <el-button size="small" type="primary" @click="openContact()">新增联系人</el-button>
          </div>
          <el-table :data="contacts" size="small">
            <el-table-column prop="name" label="姓名" />
            <el-table-column prop="title" label="职位" />
            <el-table-column prop="email" label="邮箱" />
            <el-table-column prop="phone" label="电话" />
            <el-table-column width="80">
              <template #default="{ row }">
                <el-button text type="primary" size="small" @click="openContact(row)">编辑</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="main-card mb-4">
      <div class="flex justify-between mb-3">
        <h2 class="section-title mb-0">询盘</h2>
        <el-button size="small" type="primary" @click="openInquiry">新增询盘</el-button>
      </div>
      <el-table :data="inquiries" size="small">
        <el-table-column prop="inquiryDate" label="日期" width="120" />
        <el-table-column label="阶段" width="110">
          <template #default="{ row }">{{ stageLabel(row.stage) }}</template>
        </el-table-column>
        <el-table-column prop="requirement" label="需求" show-overflow-tooltip />
        <el-table-column prop="ownerName" label="销售" width="100" />
        <el-table-column width="90">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="navigateTo(`/admin/crm/inquiries/${row.id}`)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card shadow="never" class="main-card mb-4">
      <div class="flex justify-between mb-3">
        <h2 class="section-title mb-0">订单</h2>
        <el-button size="small" type="primary" @click="navigateTo(`/admin/crm/orders?customerId=${id}`)">管理订单</el-button>
      </div>
      <el-table :data="orders" size="small">
        <el-table-column prop="orderNo" label="订单号" width="110" />
        <el-table-column prop="supplierName" label="供应商" />
        <el-table-column prop="model" label="型号" width="110" />
        <el-table-column prop="sellPrice" label="卖出价" width="100" />
        <el-table-column prop="grossProfit" label="毛利" width="100" />
      </el-table>
    </el-card>

    <el-card shadow="never" class="main-card mb-4">
      <div class="flex justify-between mb-3">
        <h2 class="section-title mb-0">售后问题</h2>
        <el-button size="small" type="primary" @click="navigateTo('/admin/crm/after-sales')">登记问题</el-button>
      </div>
      <el-table :data="afterSales" size="small">
        <el-table-column prop="supplierName" label="供应商" />
        <el-table-column prop="model" label="型号" width="110" />
        <el-table-column prop="problem" label="问题" show-overflow-tooltip />
        <el-table-column label="解决" width="80">
          <template #default="{ row }">{{ row.resolved === 1 ? '是' : '否' }}</template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card shadow="never" class="main-card">
      <div class="flex justify-between mb-3">
        <h2 class="section-title mb-0">跟进记录</h2>
        <el-button size="small" @click="openFollow">添加跟进</el-button>
      </div>
      <el-timeline>
        <el-timeline-item v-for="f in follows" :key="f.id" :timestamp="f.followDate">
          {{ methodLabel(f.method) }} · {{ f.createdByName }}<br />
          {{ f.content }}
          <div v-if="f.result" class="text-gray-500 text-sm">结果：{{ f.result }}</div>
        </el-timeline-item>
      </el-timeline>
    </el-card>

    <el-dialog v-model="showEdit" title="编辑客户" width="520px">
      <el-form :model="customer" label-width="100px">
        <el-form-item label="客户名称"><el-input v-model="customer.name" /></el-form-item>
        <el-form-item label="国家/地区"><el-input v-model="customer.country" /></el-form-item>
        <el-form-item label="官网"><el-input v-model="customer.website" /></el-form-item>
        <el-form-item label="行业"><el-input v-model="customer.industry" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="customer.address" /></el-form-item>
        <el-form-item label="来源">
          <el-select v-model="customer.source" class="w-full">
            <el-option v-for="s in CRM_SOURCES" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属销售" v-if="manager">
          <el-select v-model="customer.ownerId" class="w-full">
            <el-option v-for="u in users" :key="u.id" :label="u.displayName || u.username" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="customer.status" class="w-full">
            <el-option v-for="s in CRM_STATUS" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="customer.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEdit = false">取消</el-button>
        <el-button type="primary" @click="saveCustomer">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showContact" :title="contactForm.id ? '编辑联系人' : '新增联系人'" width="480px">
      <el-form :model="contactForm" label-width="90px">
        <el-form-item label="姓名" required><el-input v-model="contactForm.name" /></el-form-item>
        <el-form-item label="职位"><el-input v-model="contactForm.title" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="contactForm.email" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="contactForm.phone" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="contactForm.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showContact = false">取消</el-button>
        <el-button type="primary" @click="saveContact">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showInquiry" title="新增询盘" width="520px">
      <el-form :model="inquiryForm" label-width="110px">
        <el-form-item label="联系人">
          <el-select v-model="inquiryForm.contactId" clearable class="w-full">
            <el-option v-for="c in contacts" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="询盘日期"><el-date-picker v-model="inquiryForm.inquiryDate" value-format="YYYY-MM-DD" class="w-full" /></el-form-item>
        <el-form-item label="线索来源">
          <el-select v-model="inquiryForm.source" class="w-full">
            <el-option v-for="s in CRM_SOURCES" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="客户需求"><el-input v-model="inquiryForm.requirement" type="textarea" /></el-form-item>
        <el-form-item label="下次跟进"><el-date-picker v-model="inquiryForm.nextFollowDate" value-format="YYYY-MM-DD" class="w-full" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="inquiryForm.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showInquiry = false">取消</el-button>
        <el-button type="primary" @click="saveInquiry">创建</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showMerge" title="合并到已有客户" width="680px" append-to-body>
      <p class="text-sm text-gray-500 mb-3">搜索并点选要留下的客户。当前客户的联系人、询盘、跟进和附件会并过去，当前客户会关闭，记录不会丢。</p>
      <el-input
        v-model="mergeKeyword"
        placeholder="输入客户名称搜索"
        clearable
        class="mb-3"
        @input="onMergeKeyword"
      />
      <el-table
        v-loading="mergeLoading"
        :data="mergeTable"
        size="small"
        highlight-current-row
        max-height="320"
        @row-click="row => mergeTargetId = row.id"
      >
        <el-table-column prop="name" label="客户" min-width="140" />
        <el-table-column prop="country" label="国家" width="90" />
        <el-table-column prop="ownerName" label="销售" width="100" />
        <el-table-column prop="reason" label="说明" min-width="120" />
      </el-table>
      <p v-if="selectedMergeName" class="text-sm mt-3">将合并到：<b>{{ selectedMergeName }}</b></p>
      <template #footer>
        <el-button @click="showMerge = false">取消</el-button>
        <el-button type="primary" @click="doMerge">确认合并</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showFollow" title="添加跟进" width="480px">
      <el-form :model="followForm" label-width="90px">
        <el-form-item label="关联询盘">
          <el-select v-model="followForm.inquiryId" clearable class="w-full">
            <el-option v-for="i in inquiries" :key="i.id" :label="`#${i.id} ${stageLabel(i.stage)}`" :value="i.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期"><el-date-picker v-model="followForm.followDate" value-format="YYYY-MM-DD" class="w-full" /></el-form-item>
        <el-form-item label="方式">
          <el-select v-model="followForm.method" class="w-full">
            <el-option v-for="m in CRM_METHODS" :key="m.value" :label="m.label" :value="m.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="沟通内容"><el-input v-model="followForm.content" type="textarea" /></el-form-item>
        <el-form-item label="结果"><el-input v-model="followForm.result" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showFollow = false">取消</el-button>
        <el-button type="primary" @click="saveFollow">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {
  CRM_SOURCES, CRM_STATUS, CRM_METHODS, sourceLabel, statusLabel, stageLabel, methodLabel,
  parseAttachments, stringifyAttachments, useCrmApi, isCrmManager,
} from '~/composables/useCrm'

definePageMeta({ layout: 'admin' })

const route = useRoute()
const config = useRuntimeConfig()
const api = useCrmApi()
const id = Number(route.params.id)
const customer = ref(null)
const contacts = ref([])
const inquiries = ref([])
const orders = ref([])
const afterSales = ref([])
const follows = ref([])
const users = ref([])
const manager = ref(false)
const files = computed(() => parseAttachments(customer.value?.attachments))
const showEdit = ref(false)
const showContact = ref(false)
const showInquiry = ref(false)
const showFollow = ref(false)
const showMerge = ref(false)
const mergeTargetId = ref(null)
const mergeKeyword = ref('')
const mergeLoading = ref(false)
const mergeOptions = ref([])
const mergeSuggestions = ref([])
let mergeTimer = null
const mergeTable = computed(() => {
  const seen = new Set()
  const rows = []
  const push = (c, reason) => {
    if (!c || c.id === id || seen.has(c.id)) return
    seen.add(c.id)
    rows.push({ ...c, reason: c.reason || reason || '' })
  }
  mergeSuggestions.value.forEach(c => push(c, '疑似重复'))
  mergeOptions.value.forEach(c => push(c, ''))
  return rows
})
const selectedMergeName = computed(() => {
  const hit = mergeTable.value.find(c => c.id === mergeTargetId.value)
  return hit ? hit.name : ''
})
const contactForm = reactive({ id: null, name: '', title: '', email: '', phone: '', remark: '' })
const inquiryForm = reactive({ contactId: undefined, inquiryDate: '', source: 'website', requirement: '', nextFollowDate: '', remark: '' })
const followForm = reactive({ inquiryId: undefined, followDate: '', method: 'email', content: '', result: '' })
const uploadAction = computed(() => `${config.public.apiBase}/api/admin/upload`)
const uploadHeaders = computed(() => api.headers())

const fileHref = (url) => {
  if (!url) return '#'
  if (url.startsWith('http')) return url
  return `${config.public.apiBase}${url}`
}

const load = async () => {
  const res = await api.customer(id)
  customer.value = res.data
  contacts.value = (await api.contacts(id)).data || []
  inquiries.value = (await api.inquiries({ customerId: id, page: 1, pageSize: 100 })).data?.data || []
  orders.value = (await api.orders({ customerId: id, page: 1, pageSize: 50 })).data?.data || []
  afterSales.value = (await api.afterSales({ customerId: id, page: 1, pageSize: 50 })).data?.data || []
  follows.value = (await api.followUps({ customerId: id })).data || []
}

const saveCustomer = async () => {
  await api.updateCustomer(id, customer.value)
  ElMessage.success('已保存')
  showEdit.value = false
  load()
}

const closeCustomer = async () => {
  await ElMessageBox.confirm('关闭后仍可搜索查看，但不参与当前统计。', '关闭客户')
  await api.closeCustomer(id)
  load()
}

const onUpload = async (response) => {
  if (response.code !== 200) return ElMessage.error(response.message || '上传失败')
  const next = [...files.value, { url: response.data.url, name: response.data.originalFilename }]
  customer.value.attachments = stringifyAttachments(next)
  await api.updateCustomer(id, customer.value)
  ElMessage.success('附件已保存')
  load()
}

const openContact = (row) => {
  Object.assign(contactForm, row ? { ...row } : { id: null, name: '', title: '', email: '', phone: '', remark: '' })
  showContact.value = true
}

const saveContact = async () => {
  if (!contactForm.name) return ElMessage.warning('请填写姓名')
  if (contactForm.id) await api.updateContact(contactForm.id, contactForm)
  else await api.createContact({ ...contactForm, customerId: id })
  showContact.value = false
  load()
}

const openInquiry = () => {
  Object.assign(inquiryForm, { contactId: undefined, inquiryDate: new Date().toISOString().slice(0, 10), source: customer.value.source || 'website', requirement: '', nextFollowDate: '', remark: '' })
  showInquiry.value = true
}

const saveInquiry = async () => {
  await api.createInquiry({ ...inquiryForm, customerId: id, stage: 'NEW' })
  showInquiry.value = false
  load()
}

const openFollow = () => {
  Object.assign(followForm, { inquiryId: undefined, followDate: new Date().toISOString().slice(0, 10), method: 'email', content: '', result: '' })
  showFollow.value = true
}

const saveFollow = async () => {
  await api.createFollowUp({ ...followForm, customerId: id })
  showFollow.value = false
  load()
}

const searchMergeTargets = async (keyword) => {
  mergeLoading.value = true
  try {
    const res = await api.customers({ keyword: keyword || '', page: 1, pageSize: 20 })
    mergeOptions.value = (res.data?.data || []).filter(c => c.id !== id)
  } finally {
    mergeLoading.value = false
  }
}

const onMergeKeyword = (val) => {
  clearTimeout(mergeTimer)
  mergeTimer = setTimeout(() => searchMergeTargets(typeof val === 'string' ? val : mergeKeyword.value), 300)
}

const startMerge = async () => {
  mergeTargetId.value = null
  mergeKeyword.value = customer.value?.name || ''
  showMerge.value = true
  await searchMergeTargets(mergeKeyword.value)
  const dup = await api.duplicates({
    id,
    name: customer.value?.name,
    website: customer.value?.website,
  })
  mergeSuggestions.value = (dup.data || []).filter(c => c.id !== id)
}

const doMerge = async () => {
  if (!mergeTargetId.value) return ElMessage.warning('请先搜索并选择要留下的客户')
  const name = selectedMergeName.value || '目标客户'
  const fromName = customer.value.name
  const toId = mergeTargetId.value
  showMerge.value = false
  await nextTick()
  try {
    await ElMessageBox.confirm(
      `将「${fromName}」合并到「${name}」？当前客户会关闭，记录会全部并过去。`,
      '确认合并',
      { type: 'warning', appendTo: document.body, zIndex: 5000 }
    )
  } catch {
    showMerge.value = true
    return
  }
  await api.mergeCustomers(id, toId)
  ElMessage.success('已合并')
  navigateTo(`/admin/crm/customers/${toId}`)
}

onMounted(async () => {
  if (!localStorage.getItem('admin_token')) return navigateTo('/admin/login')
  manager.value = isCrmManager()
  if (manager.value) users.value = (await api.users(true)).data || []
  await load()
})
</script>

<style scoped>
.page-header { display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:12px; background:#fff; border-radius:12px; padding:20px 24px; }
.main-card { border:none; border-radius:12px; }
.section-title { font-size:16px; font-weight:600; margin-bottom:12px; }
.file-row { margin-bottom:6px; }
</style>
