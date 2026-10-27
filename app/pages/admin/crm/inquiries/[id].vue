<template>
  <div class="page-container" v-if="inquiry">
    <div class="page-header mb-4">
      <div>
        <h1 class="text-2xl font-bold text-gray-800">询盘 #{{ inquiry.id }}</h1>
        <p class="text-gray-500 text-sm mt-1">
          {{ inquiry.customerName }} · {{ inquiry.contactName || '未关联联系人' }} · {{ inquiry.ownerName }}
        </p>
      </div>
      <el-tag :type="inquiry.closed === 1 ? 'info' : 'success'" size="large">{{ inquiry.closed === 1 ? '已关闭' : stageLabel(inquiry.stage) }}</el-tag>
    </div>

    <el-card shadow="never" class="main-card mb-4">
      <h2 class="section-title">客户与需求</h2>
      <p>客户：<NuxtLink class="text-indigo-600" :to="`/admin/crm/customers/${inquiry.customerId}`">{{ inquiry.customerName }}</NuxtLink></p>
      <p>联系人：{{ inquiry.contactName || '-' }} {{ inquiry.contactEmail || '' }} {{ inquiry.contactPhone || '' }}</p>
      <p>询盘日期：{{ inquiry.inquiryDate }} · 来源：{{ sourceLabel(inquiry.source) }}</p>
      <p class="flex items-center gap-2 flex-wrap">
        所属销售：
        <el-select
          v-if="manager"
          :model-value="inquiry.ownerId"
          placeholder="未分配"
          clearable
          size="small"
          style="width: 180px"
          teleported
          @change="assign"
        >
          <el-option v-for="u in users" :key="u.id" :label="u.displayName || u.username" :value="u.id" />
        </el-select>
        <span v-else>{{ inquiry.ownerName || '未分配' }}</span>
        · 下次跟进：{{ inquiry.nextFollowDate || '-' }}
      </p>
      <p class="mt-2">客户需求：{{ inquiry.requirement || '-' }}</p>
      <p>备注：{{ inquiry.remark || '-' }}</p>
      <div class="mt-3">
        <div v-for="(f, i) in files" :key="i" class="mb-1">
          <a :href="fileHref(f.url)" target="_blank">{{ f.name || f.url }}</a>
        </div>
        <el-upload :action="uploadAction" :headers="uploadHeaders" :show-file-list="false" :on-success="onUpload">
          <el-button size="small">上传附件</el-button>
        </el-upload>
      </div>
    </el-card>

    <el-card shadow="never" class="main-card mb-4">
      <h2 class="section-title">更新阶段</h2>
      <div class="flex flex-wrap gap-2">
        <el-button v-for="s in CRM_STAGES.filter(x => !['WON','LOST'].includes(x.value))" :key="s.value" :type="inquiry.stage === s.value ? 'primary' : 'default'" @click="setStage(s.value)">{{ s.label }}</el-button>
        <el-button type="success" @click="setStage('WON')">标记赢单</el-button>
        <el-button type="danger" @click="setStage('LOST')">标记输单</el-button>
        <el-button v-if="inquiry.closed !== 1" type="warning" @click="closeIt">标记关闭</el-button>
      </div>
    </el-card>

    <el-card shadow="never" class="main-card">
      <div class="flex justify-between mb-3">
        <h2 class="section-title mb-0">跟进时间线</h2>
        <el-button size="small" type="primary" @click="showFollow = true">添加跟进</el-button>
      </div>
      <el-timeline>
        <el-timeline-item v-for="f in follows" :key="f.id" :timestamp="`${f.followDate} · ${methodLabel(f.method)} · ${f.createdByName || ''}`">
          <div>{{ f.content }}</div>
          <div v-if="f.result" class="text-gray-500 text-sm mt-1">结果：{{ f.result }}</div>
        </el-timeline-item>
      </el-timeline>
    </el-card>

    <el-dialog v-model="showFollow" title="添加跟进记录" width="480px">
      <el-form :model="followForm" label-width="90px">
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
  CRM_STAGES, CRM_METHODS, stageLabel, sourceLabel, methodLabel,
  parseAttachments, stringifyAttachments, useCrmApi, isCrmManager,
} from '~/composables/useCrm'

definePageMeta({ layout: 'admin' })

const route = useRoute()
const config = useRuntimeConfig()
const api = useCrmApi()
const id = computed(() => Number(route.params.id))
const inquiry = ref(null)
const follows = ref([])
const showFollow = ref(false)
const followForm = reactive({ followDate: '', method: 'email', content: '', result: '' })
const manager = ref(false)
const users = ref([])
const files = computed(() => parseAttachments(inquiry.value?.attachments))
const uploadAction = computed(() => `${config.public.apiBase}/api/admin/upload`)
const uploadHeaders = computed(() => api.headers())

const fileHref = (url) => {
  if (!url) return '#'
  if (url.startsWith('http')) return url
  return `${config.public.apiBase}${url}`
}

const load = async () => {
  try {
    const res = await api.inquiry(id.value)
    if (res.code && res.code !== 200) {
      ElMessage.error(res.message || '无法打开询盘详情')
      return
    }
    inquiry.value = res.data
    follows.value = (await api.followUps({ inquiryId: id.value })).data || []
  } catch (e) {
    ElMessage.error(e?.data?.message || e?.message || '无法打开询盘详情')
  }
}

const assign = async (ownerId) => {
  try {
    await api.assignOwner(id.value, ownerId || null)
    ElMessage.success(ownerId ? '已分配销售' : '已取消分配')
    load()
  } catch (e) {
    ElMessage.error(e?.data?.message || '分配失败')
  }
}

const setStage = async (stage) => {
  await api.updateStage(id.value, stage)
  ElMessage.success('阶段已更新')
  load()
}

const closeIt = async () => {
  await ElMessageBox.confirm('关闭后仍可搜索查看，但不参与当前统计。', '关闭询盘')
  await api.closeInquiry(id.value)
  load()
}

const onUpload = async (response) => {
  if (response.code !== 200) return ElMessage.error('上传失败')
  const next = [...files.value, { url: response.data.url, name: response.data.originalFilename }]
  inquiry.value.attachments = stringifyAttachments(next)
  await api.updateInquiry(id.value, inquiry.value)
  ElMessage.success('附件已保存')
  load()
}

const saveFollow = async () => {
  await api.createFollowUp({
    ...followForm,
    inquiryId: id.value,
    customerId: inquiry.value.customerId,
    followDate: followForm.followDate || new Date().toISOString().slice(0, 10),
  })
  showFollow.value = false
  Object.assign(followForm, { followDate: '', method: 'email', content: '', result: '' })
  load()
}

onMounted(async () => {
  if (!localStorage.getItem('admin_token')) return navigateTo('/admin/login')
  manager.value = isCrmManager()
  if (manager.value) users.value = (await api.users(true)).data || []
  await load()
})

watch(() => route.params.id, () => load())
</script>

<style scoped>
.page-header { display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:12px; background:#fff; border-radius:12px; padding:20px 24px; }
.main-card { border:none; border-radius:12px; }
.section-title { font-size:16px; font-weight:600; margin-bottom:12px; }
</style>
