<template>
  <div class="dashboard-container">
    <!-- Header -->
    <div class="dashboard-header mb-6">
      <div class="header-content">
        <div class="header-text">
          <h1 class="text-2xl font-bold text-gray-800">{{ $t('admin.dashboard') }}</h1>
          <p class="text-gray-500 text-sm mt-1">欢迎回来，今天是 {{ currentDate }}</p>
        </div>
        <div class="header-badge">
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
          </svg>
        </div>
      </div>
    </div>

    <!-- Stats Cards -->
    <el-row :gutter="20" class="mb-6">
      <el-col :span="6">
        <div class="stat-card stat-card-blue">
          <div class="stat-icon">
            <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
            </svg>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.products }}</div>
            <div class="stat-label">{{ $t('admin.products') }}</div>
          </div>
          <div class="stat-trend trend-up">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 10l7-7m0 0l7 7m-7-7v18" />
            </svg>
            <span>12%</span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-card-green">
          <div class="stat-icon">
            <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V7m2 13a2 2 0 002-2V9a2 2 0 00-2-2h-2m-4-3H9M7 16h6M7 8h6v4H7V8z" />
            </svg>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.news }}</div>
            <div class="stat-label">{{ $t('admin.news') }}</div>
          </div>
          <div class="stat-trend trend-up">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 10l7-7m0 0l7 7m-7-7v18" />
            </svg>
            <span>8%</span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-card-orange">
          <div class="stat-icon">
            <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
            </svg>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.inquiries }}</div>
            <div class="stat-label">{{ $t('admin.inquiries') }}</div>
          </div>
          <div class="stat-badge">待处理: {{ pendingInquiries }}</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-card-purple">
          <div class="stat-icon">
            <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
            </svg>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.categories }}</div>
            <div class="stat-label">{{ $t('admin.categories') }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- Charts and Lists Row -->
    <el-row :gutter="20" class="mb-6">
      <!-- Product Distribution Chart -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <svg class="w-5 h-5 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
              </svg>
              <span class="font-semibold">产品分类分布</span>
            </div>
          </template>
          <div class="chart-placeholder">
            <div class="bar-chart">
              <div v-for="(item, index) in categoryData" :key="index" class="bar-item">
                <div class="bar-label">{{ item.name }}</div>
                <div class="bar-container">
                  <div class="bar-fill" :style="{ width: item.percent + '%', backgroundColor: item.color }"></div>
                </div>
                <div class="bar-value">{{ item.count }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- Recent Inquiries -->
      <el-col :span="12">
        <el-card class="list-card">
          <template #header>
            <div class="card-header flex justify-between items-center">
              <div class="flex items-center gap-2">
                <svg class="w-5 h-5 text-orange-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
                </svg>
                <span class="font-semibold">最新询盘</span>
              </div>
              <el-button type="primary" text size="small" @click="navigateTo('/admin/inquiries')">查看全部</el-button>
            </div>
          </template>
          <div class="inquiry-list">
            <div v-for="inquiry in recentInquiries" :key="inquiry.id" class="inquiry-item">
              <div class="inquiry-avatar">
                {{ inquiry.name?.charAt(0) || 'U' }}
              </div>
              <div class="inquiry-content">
                <div class="inquiry-name">{{ inquiry.name }}</div>
                <div class="inquiry-company">{{ inquiry.company || '未填写公司' }}</div>
              </div>
              <div class="inquiry-time">{{ formatDate(inquiry.createdAt) }}</div>
            </div>
            <div v-if="recentInquiries.length === 0" class="text-center text-gray-400 py-8">
              <svg class="w-12 h-12 mx-auto mb-2 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
              </svg>
              <p>暂无询盘</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Quick Actions and Recent Products -->
    <el-row :gutter="20">
      <!-- Quick Actions -->
      <el-col :span="8">
        <el-card class="action-card">
          <template #header>
            <div class="card-header flex items-center gap-2">
              <svg class="w-5 h-5 text-purple-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
              </svg>
              <span class="font-semibold">快捷操作</span>
            </div>
          </template>
          <div class="action-grid">
            <div class="action-item" @click="navigateTo('/admin/products')">
              <div class="action-icon bg-blue-100 text-blue-600">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                </svg>
              </div>
              <span>添加产品</span>
            </div>
            <div class="action-item" @click="navigateTo('/admin/news')">
              <div class="action-icon bg-green-100 text-green-600">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                </svg>
              </div>
              <span>发布新闻</span>
            </div>
            <div class="action-item" @click="navigateTo('/admin/categories')">
              <div class="action-icon bg-purple-100 text-purple-600">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6z" />
                </svg>
              </div>
              <span>管理分类</span>
            </div>
            <div class="action-item" @click="navigateTo('/admin/inquiries')">
              <div class="action-icon bg-orange-100 text-orange-600">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                </svg>
              </div>
              <span>查看询盘</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- Recent Products -->
      <el-col :span="16">
        <el-card class="product-card">
          <template #header>
            <div class="card-header flex justify-between items-center">
              <div class="flex items-center gap-2">
                <svg class="w-5 h-5 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
                </svg>
                <span class="font-semibold">产品列表</span>
              </div>
              <el-button type="primary" text size="small" @click="navigateTo('/admin/products')">管理产品</el-button>
            </div>
          </template>
          <el-table :data="recentProducts" style="width: 100%" size="small" class="admin-table">
            <el-table-column prop="nameEn" label="产品名称" width="200" />
            <el-table-column prop="categoryId" label="分类ID" width="100" />
            <el-table-column label="精选" width="80">
              <template #default="{ row }">
                <el-tag v-if="row.featured" type="success" size="small" effect="light">是</el-tag>
                <el-tag v-else type="info" size="small" effect="light">否</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="sortOrder" label="排序" width="80" />
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="primary" text size="small" @click="editProduct(row)">编辑</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
definePageMeta({
  layout: 'admin',
})

const { t } = useI18n()
const config = useRuntimeConfig()

const stats = reactive({
  products: 0,
  news: 0,
  inquiries: 0,
  categories: 0,
})

const pendingInquiries = ref(0)
const recentInquiries = ref([])
const recentProducts = ref([])

const currentDate = ref('')

const categoryData = ref([
  { name: '光谱仪', count: 4, percent: 40, color: '#3b82f6' },
  { name: '分析仪', count: 3, percent: 30, color: '#10b981' },
  { name: '传感器', count: 3, percent: 30, color: '#f59e0b' },
])

onMounted(async () => {
  if (!import.meta.client) return

  const token = localStorage.getItem('admin_token')
  if (!token) {
    navigateTo('/admin/login')
    return
  }

  // Set current date
  const now = new Date()
  currentDate.value = now.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })

  const headers = { Authorization: `Bearer ${token}` }

  try {
    const [products, news, inquiries, categories] = await Promise.all([
      $fetch(`${config.public.apiBase}/api/admin/products`, { headers }),
      $fetch(`${config.public.apiBase}/api/admin/news`, { headers }),
      $fetch(`${config.public.apiBase}/api/admin/inquiries`, { headers }),
      $fetch(`${config.public.apiBase}/api/admin/categories`, { headers }),
    ])

    stats.products = products.data?.length || 0
    stats.news = news.data?.length || 0
    stats.inquiries = inquiries.data?.length || 0
    stats.categories = categories.data?.length || 0

    // Recent inquiries
    recentInquiries.value = (inquiries.data || []).slice(0, 5)
    pendingInquiries.value = (inquiries.data || []).filter(i => !i.replied).length

    // Recent products
    recentProducts.value = (products.data || []).slice(0, 5)

    // Build category data from categories
    if (categories.data && categories.data.length > 0) {
      const catCounts = {}
      ;(products.data || []).forEach(p => {
        catCounts[p.categoryId] = (catCounts[p.categoryId] || 0) + 1
      })
      const colors = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6']
      categoryData.value = categories.data.map((c, i) => ({
        name: c.nameZh || c.nameEn || '未分类',
        count: catCounts[c.id] || 0,
        percent: catCounts[c.id] ? Math.round((catCounts[c.id] / stats.products) * 100) : 0,
        color: colors[i % colors.length]
      })).filter(c => c.count > 0)
    }
  } catch (error) {
    console.error(t('admin.fetchFailed'), error)
  }
})

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}

const editProduct = (row) => {
  navigateTo('/admin/products')
}
</script>

<style scoped>
.dashboard-container {
  padding: 0;
}

.dashboard-header {
  background: white;
  border-radius: 12px;
  padding: 20px 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}
.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-badge {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

/* Stat Cards */
.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}
.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
}
.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  border-radius: 4px 0 0 4px;
}
.stat-card-blue::before { background: linear-gradient(180deg, #3b82f6, #2563eb); }
.stat-card-green::before { background: linear-gradient(180deg, #10b981, #059669); }
.stat-card-orange::before { background: linear-gradient(180deg, #f59e0b, #d97706); }
.stat-card-purple::before { background: linear-gradient(180deg, #8b5cf6, #7c3aed); }

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-card-blue .stat-icon { background: linear-gradient(135deg, #eff6ff, #dbeafe); color: #3b82f6; }
.stat-card-green .stat-icon { background: linear-gradient(135deg, #ecfdf5, #d1fae5); color: #10b981; }
.stat-card-orange .stat-icon { background: linear-gradient(135deg, #fffbeb, #fef3c7); color: #f59e0b; }
.stat-card-purple .stat-icon { background: linear-gradient(135deg, #f5f3ff, #ede9fe); color: #8b5cf6; }

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 2rem;
  font-weight: 700;
  color: #1f2937;
  line-height: 1;
}

.stat-label {
  font-size: 0.875rem;
  color: #6b7280;
  margin-top: 4px;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.75rem;
  font-weight: 500;
  padding: 4px 8px;
  border-radius: 20px;
}

.trend-up {
  color: #10b981;
  background: #ecfdf5;
}

.trend-down {
  color: #ef4444;
  background: #fef2f2;
}

.stat-badge {
  font-size: 0.75rem;
  color: #f59e0b;
  background: #fffbeb;
  padding: 6px 12px;
  border-radius: 20px;
  font-weight: 500;
}

/* Chart Card */
.chart-card, .list-card, .action-card, .product-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
}
.chart-card:hover, .list-card:hover, .action-card:hover, .product-card:hover {
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 1rem;
  color: #374151;
}

/* Bar Chart */
.bar-chart {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.bar-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.bar-label {
  width: 80px;
  font-size: 0.875rem;
  color: #6b7280;
}

.bar-container {
  flex: 1;
  height: 24px;
  background: #f3f4f6;
  border-radius: 6px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 6px;
  transition: width 0.5s ease;
}

.bar-value {
  width: 40px;
  text-align: right;
  font-size: 0.875rem;
  font-weight: 600;
  color: #374151;
}

/* Inquiry List */
.inquiry-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.inquiry-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f9fafb;
  border-radius: 10px;
  transition: all 0.2s ease;
}
.inquiry-item:hover {
  background: #f3f4f6;
}

.inquiry-avatar {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 1rem;
}

.inquiry-content {
  flex: 1;
}

.inquiry-name {
  font-weight: 500;
  color: #374151;
  font-size: 0.875rem;
}

.inquiry-company {
  font-size: 0.75rem;
  color: #9ca3af;
}

.inquiry-time {
  font-size: 0.75rem;
  color: #9ca3af;
}

/* Action Grid */
.action-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 20px;
  background: #f9fafb;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}
.action-item:hover {
  background: #f3f4f6;
  transform: translateY(-2px);
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-item span {
  font-size: 0.875rem;
  color: #6b7280;
}

/* Admin Table */
.admin-table :deep(.el-table__header th) {
  background: #f9fafb !important;
  color: #374151;
  font-weight: 600;
}
.admin-table :deep(.el-table__row:hover > td) {
  background: #f9fafb !important;
}
</style>
