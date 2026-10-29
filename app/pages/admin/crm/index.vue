<template>
  <div class="page-container">
    <div class="page-header mb-6">
      <div class="header-content">
        <div class="header-icon">
          <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 13h8V3H3v10zm10 8h8V3h-8v18zM3 21h8v-6H3v6z" />
          </svg>
        </div>
        <div class="header-text">
          <h1 class="text-2xl font-bold text-gray-800">CRM 看板</h1>
          <p class="text-gray-500 text-sm mt-1">{{ manager ? '查看销售漏斗与本月询盘情况' : '查看自己的客户与询盘进度' }}</p>
        </div>
      </div>
    </div>

    <el-row :gutter="16" class="mb-4">
      <el-col :xs="24" :sm="8">
        <el-card shadow="never" class="stat-card">
          <div class="stat-label">客户数量</div>
          <div class="stat-value">{{ dash.customerCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="never" class="stat-card">
          <div class="stat-label">进行中询盘</div>
          <div class="stat-value">{{ dash.inquiryCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="never" class="stat-card">
          <div class="stat-label">本月新增询盘</div>
          <div class="stat-value">{{ dash.monthNewInquiries || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="16" class="mb-4">
      <el-col :xs="24" :sm="12">
        <el-card shadow="never" class="stat-card" @click="navigateTo('/admin/crm/orders')">
          <div class="stat-label">订单数量</div>
          <div class="stat-value">{{ dash.orderCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12">
        <el-card shadow="never" class="stat-card" @click="navigateTo('/admin/crm/after-sales')">
          <div class="stat-label">未解决售后</div>
          <div class="stat-value">{{ dash.openAfterSaleCount || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="main-card mb-4">
      <h2 class="section-title">销售漏斗（不含已关闭）</h2>
      <div v-for="item in dash.funnel || []" :key="item.stage" class="funnel-row">
        <div class="funnel-label">{{ stageLabel(item.stage) }}</div>
        <div class="funnel-bar-wrap">
          <div class="funnel-bar" :style="{ width: funnelWidth(item.count) }">{{ item.count }}</div>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="main-card mb-4">
      <h2 class="section-title">商机阶段分布</h2>
      <div class="flex flex-wrap gap-3">
        <el-tag v-for="s in CRM_STAGES" :key="s.value" effect="plain" size="large">
          {{ s.label }}：{{ (dash.stageDistribution && dash.stageDistribution[s.value]) || 0 }}
        </el-tag>
      </div>
    </el-card>

    <el-card shadow="never" class="main-card">
      <h2 class="section-title">各阶段询盘</h2>
      <el-tabs>
        <el-tab-pane v-for="s in CRM_STAGES" :key="s.value" :label="s.label">
          <el-table :data="(dash.inquiriesByStage && dash.inquiriesByStage[s.value]) || []" size="small" class="admin-table">
            <el-table-column prop="customerName" label="客户" min-width="140" />
            <el-table-column prop="inquiryDate" label="询盘日期" width="120" />
            <el-table-column prop="ownerName" label="所属销售" width="120" />
            <el-table-column label="操作" width="90">
              <template #default="{ row }">
                <el-button text type="primary" size="small" @click="goInquiry(row.id)">查看</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { CRM_STAGES, stageLabel, useCrmApi } from '~/composables/useCrm'

definePageMeta({ layout: 'admin' })

const api = useCrmApi()
const dash = ref({})
const manager = ref(false)

const maxFunnel = computed(() => {
  const list = dash.value.funnel || []
  return Math.max(1, ...list.map(i => Number(i.count) || 0))
})

const funnelWidth = (count) => `${Math.max(8, (Number(count) / maxFunnel.value) * 100)}%`

const goInquiry = (id) => navigateTo(`/admin/crm/inquiries/${id}`)

onMounted(async () => {
  const token = localStorage.getItem('admin_token')
  if (!token) return navigateTo('/admin/login')
  try {
    const res = await api.dashboard()
    dash.value = res.data || {}
    manager.value = !!dash.value.manager
  } catch (e) {
    ElMessage.error('加载看板失败')
  }
})
</script>

<style scoped>
.page-header { display:flex; justify-content:space-between; align-items:center; background:#fff; border-radius:12px; padding:20px 24px; }
.header-content { display:flex; align-items:center; gap:16px; }
.header-icon { width:48px; height:48px; background:linear-gradient(135deg,#667eea,#764ba2); border-radius:12px; display:flex; align-items:center; justify-content:center; color:#fff; }
.stat-card { border:none; border-radius:12px; margin-bottom:12px; cursor:pointer; }
.stat-label { color:#6b7280; font-size:13px; }
.stat-value { font-size:28px; font-weight:700; color:#111827; margin-top:6px; }
.main-card { border:none; border-radius:12px; }
.section-title { font-size:16px; font-weight:600; margin-bottom:16px; }
.funnel-row { display:flex; align-items:center; gap:12px; margin-bottom:10px; }
.funnel-label { width:80px; color:#4b5563; font-size:13px; }
.funnel-bar-wrap { flex:1; background:#f3f4f6; border-radius:8px; overflow:hidden; }
.funnel-bar { background:linear-gradient(135deg,#667eea,#764ba2); color:#fff; font-size:12px; padding:8px 10px; min-width:32px; }
</style>
