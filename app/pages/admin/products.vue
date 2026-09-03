<template>
  <div class="page-container">
    <!-- Page Header -->
    <div class="page-header mb-6">
      <div class="header-content">
        <div class="header-icon">
          <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
          </svg>
        </div>
        <div class="header-text">
          <h1 class="text-2xl font-bold text-gray-800">{{ $t('admin.products') }}</h1>
          <p class="text-gray-500 text-sm mt-1">管理您的产品分类和列表</p>
        </div>
      </div>
      <el-button type="primary" @click="addProduct" class="add-btn">
        <svg class="w-4 h-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        {{ $t('admin.add') }}
      </el-button>
    </div>

    <el-card shadow="never" class="main-card">
      <el-tree
        v-if="treeData.length > 0"
        :data="treeData"
        :props="treeProps"
        node-key="id"
        default-expand-all
        :expand-on-click-node="false"
        class="product-tree"
      >
        <template #default="{ node, data }">
          <span class="tree-node">
            <span class="node-content">
              <!-- Category Node -->
              <template v-if="data.isCategory">
                <span class="category-icon">
                  <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z" />
                  </svg>
                </span>
                <span class="category-label">
                  <span class="font-medium text-gray-800">{{ data.nameEn }}</span>
                  <span class="text-gray-400 mx-2">/</span>
                  <span class="text-gray-500">{{ data.nameZh }}</span>
                </span>
                <el-tag size="small" type="info" effect="light" class="ml-3">{{ data.productCount }} {{ $t('admin.products').toLowerCase() }}</el-tag>
              </template>
              <!-- Product Node -->
              <template v-else>
                <span class="product-label">
                  <span class="text-gray-700">{{ data.nameEn }}</span>
                  <span class="text-gray-400 mx-2">/</span>
                  <span class="text-gray-500">{{ data.nameZh }}</span>
                </span>
                <el-tag v-if="data.featured" size="small" type="success" effect="light" class="ml-3">精选</el-tag>
              </template>
            </span>
            <span class="node-actions">
              <template v-if="data.isCategory">
                <el-button size="small" text type="primary" @click="addProductUnderCategory(data.id)">
                  <span class="text-xs">添加产品</span>
                </el-button>
              </template>
              <template v-else>
                <el-button size="small" text type="primary" @click="editProduct(data)">编辑</el-button>
                <el-button size="small" text type="danger" @click="removeProduct(data.id)">删除</el-button>
              </template>
            </span>
          </span>
        </template>
      </el-tree>

      <!-- Empty State -->
      <div v-else class="empty-state">
        <svg class="w-16 h-16 text-gray-300 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
        </svg>
        <p class="text-gray-500">暂无产品，请添加您的第一个产品</p>
      </div>
    </el-card>

    <!-- Product Dialog -->
    <el-dialog
      v-model="showDialog"
      :title="isEdit ? $t('admin.edit') + ' ' + $t('admin.product') : $t('admin.add') + ' ' + $t('admin.product')"
      width="800px"
    >
      <el-form :model="form" label-width="120px">
        <el-form-item :label="$t('admin.category')">
          <el-select v-model="form.categoryId" :placeholder="$t('admin.pleaseSelect')" class="w-full">
            <el-option
              v-for="cat in flatCategories"
              :key="cat.id"
              :label="cat.nameEn + ' / ' + cat.nameZh"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('admin.nameEn')">
          <el-input v-model="form.nameEn" :placeholder="$t('admin.pleaseEnter') + $t('admin.nameEn')" />
        </el-form-item>
        <el-form-item :label="$t('admin.nameZh')">
          <el-input v-model="form.nameZh" :placeholder="$t('admin.pleaseEnter') + $t('admin.nameZh')" />
        </el-form-item>
        <el-form-item :label="$t('admin.slug')">
          <el-input v-model="form.slug" :placeholder="$t('admin.pleaseEnter') + $t('admin.slug')" />
        </el-form-item>
        <el-form-item :label="$t('admin.descriptionEn')">
          <el-input v-model="form.descriptionEn" type="textarea" rows="3" :placeholder="$t('admin.pleaseEnter') + $t('admin.descriptionEn')" />
        </el-form-item>
        <el-form-item :label="$t('admin.descriptionZh')">
          <el-input v-model="form.descriptionZh" type="textarea" rows="3" :placeholder="$t('admin.pleaseEnter') + $t('admin.descriptionZh')" />
        </el-form-item>
        <el-form-item :label="$t('admin.imageUrl')">
          <div class="flex items-center gap-4">
            <el-upload
              :action="`${config.public.apiBase}/api/admin/upload`"
              :headers="headers"
              :show-file-list="false"
              :on-success="handleImageUploadSuccess"
              :on-progress="(evt, file, fileList) => handleUploadProgress(evt, file, fileList, 'image')"
              :before-upload="beforeImageUpload"
              accept="image/*"
            >
              <el-button type="primary" plain :disabled="uploading">{{ $t('admin.uploadImage') }}</el-button>
            </el-upload>
            <el-input v-model="form.imageUrl" :placeholder="$t('admin.imageUrlPlaceholder')" class="flex-1" />
          </div>
          <div v-if="uploading && uploadType === 'image'" class="mt-2">
            <el-progress :percentage="uploadPercentage" :stroke-width="6" />
          </div>
          <div v-if="form.imageUrl" class="mt-2">
            <img :src="getFullImageUrl(form.imageUrl)" class="w-32 h-32 object-cover rounded border" />
          </div>
        </el-form-item>

        <!-- Multi-image Upload -->
        <el-form-item label="产品轮播图片">
          <el-upload
            :action="`${config.public.apiBase}/api/admin/upload`"
            :headers="headers"
            :on-success="handleMultiImageSuccess"
            :on-progress="(evt, file, fileList) => handleUploadProgress(evt, file, fileList, 'multi')"
            :before-upload="beforeImageUpload"
            accept="image/*"
            multiple
            :show-file-list="false"
            class="mb-2"
          >
            <el-button type="primary" plain size="small" :disabled="uploading">
              <svg class="w-4 h-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
              </svg>
              上传图片
            </el-button>
          </el-upload>
          <div v-if="uploading && uploadType === 'multi'" class="mt-2">
            <el-progress :percentage="uploadPercentage" :stroke-width="6" />
          </div>
          <div v-if="form.imageUrls.length > 0" class="flex flex-wrap gap-2 mt-2">
            <div v-for="(url, idx) in form.imageUrls" :key="idx" class="relative w-20 h-20 rounded border overflow-hidden group">
              <img :src="getFullImageUrl(url)" class="w-full h-full object-cover" />
              <button
                @click="removeImageUrl(idx)"
                class="absolute top-0 right-0 bg-red-500 text-white w-5 h-5 flex items-center justify-center text-xs opacity-0 group-hover:opacity-100 transition-opacity"
              >×</button>
            </div>
          </div>
          <p class="text-xs text-gray-400 mt-1">支持多张图片上传，点击×删除</p>
        </el-form-item>

        <!-- Module Editor for Product Overview -->
        <el-form-item label="产品概览模块">
          <div class="module-editor">
            <!-- Module List -->
            <div v-if="form.modules.length > 0" class="module-list mb-4">
              <div
                v-for="(module, index) in form.modules"
                :key="module.id"
                class="module-item mb-3 p-4 border border-gray-200 rounded-lg bg-gray-50"
              >
                <div class="flex items-center justify-between mb-2">
                  <div class="flex items-center gap-2">
                    <el-icon class="cursor-move"><Rank /></el-icon>
                    <el-tag size="small" type="info">{{ getModuleTypeName(module.type) }}</el-tag>
                  </div>
                  <div class="flex gap-2">
                    <el-button size="small" text type="primary" @click="editModule(index)">编辑</el-button>
                    <el-button size="small" text type="danger" @click="removeModule(index)">删除</el-button>
                    <el-button size="small" text :disabled="index === 0" @click="moveModule(index, -1)">上移</el-button>
                    <el-button size="small" text :disabled="index === form.modules.length - 1" @click="moveModule(index, 1)">下移</el-button>
                  </div>
                </div>
                <!-- Module Preview -->
                <div class="module-preview text-sm text-gray-600">
                  <template v-if="module.type === 'heading'">
                    <span class="font-bold">标题:</span> {{ module.content }}
                  </template>
                  <template v-else-if="module.type === 'text'">
                    <span class="font-bold">文字:</span> {{ truncate(module.content, 50) }}
                  </template>
                  <template v-else-if="module.type === 'image'">
                    <span class="font-bold">图片:</span> {{ module.url || '未设置' }}
                  </template>
                  <template v-else-if="module.type === 'table'">
                    <span class="font-bold">表格:</span> {{ module.rows }}行 × {{ module.cols }}列
                  </template>
                  <template v-else-if="module.type === 'two_column'">
                    <span class="font-bold">双栏:</span> 左侧{{ module.leftType }} / 右侧{{ module.rightType }}
                  </template>
                  <template v-else-if="module.type === 'downloads'">
                    <span class="font-bold">下载:</span> {{ module.items?.length || 0 }} 个文件
                  </template>
                </div>
              </div>
            </div>

            <!-- Add Module Button -->
            <el-button type="primary" plain @click="showModuleDialog = true">
              <el-icon><Plus /></el-icon>
              添加模块
            </el-button>
          </div>
        </el-form-item>

        <!-- Module Type Selection Dialog -->
        <el-dialog v-model="showModuleDialog" title="选择模块类型" width="500px">
          <div class="module-types-grid">
            <div
              v-for="type in moduleTypes"
              :key="type.value"
              class="module-type-item p-4 border border-gray-200 rounded-lg cursor-pointer hover:border-blue-400 hover:bg-blue-50 transition-colors"
              @click="addModule(type.value)"
            >
              <div class="text-2xl mb-2">{{ type.icon }}</div>
              <div class="font-medium">{{ type.name }}</div>
              <div class="text-xs text-gray-500">{{ type.desc }}</div>
            </div>
          </div>
        </el-dialog>

        <!-- Module Edit Dialog -->
        <el-dialog v-model="showModuleEditDialog" :title="'编辑 ' + getModuleTypeName(editingModule?.type)" width="700px">
          <!-- Heading Module -->
          <el-form v-if="editingModule?.type === 'heading'" :model="editingModule" label-width="100px">
            <el-form-item label="标题级别">
              <el-select v-model="editingModule.level">
                <el-option label="H1" :value="1" />
                <el-option label="H2" :value="2" />
                <el-option label="H3" :value="3" />
              </el-select>
            </el-form-item>
            <el-form-item label="标题内容">
              <el-input v-model="editingModule.content" type="textarea" :rows="2" placeholder="输入标题文字" />
            </el-form-item>
          </el-form>

          <!-- Text Module -->
          <el-form v-else-if="editingModule?.type === 'text'" :model="editingModule" label-width="100px">
            <el-form-item label="文字内容">
              <el-input v-model="editingModule.content" type="textarea" :rows="6" placeholder="输入文字内容" />
            </el-form-item>
          </el-form>

          <!-- Image Module -->
          <el-form v-else-if="editingModule?.type === 'image'" :model="editingModule" label-width="100px">
            <el-form-item label="图片">
              <div class="flex items-center gap-4">
                <el-upload
                  :action="`${config.public.apiBase}/api/admin/upload`"
                  :headers="headers"
                  :show-file-list="false"
                  :on-success="handleModuleImageUpload"
                  :before-upload="beforeImageUpload"
                  accept="image/*"
                >
                  <el-button type="primary" plain>上传图片</el-button>
                </el-upload>
                <el-input v-model="editingModule.url" placeholder="或输入图片URL" />
              </div>
              <img v-if="editingModule.url" :src="getFullImageUrl(editingModule.url)" class="mt-2 max-h-40 rounded border" />
            </el-form-item>
            <el-form-item label="图片描述">
              <el-input v-model="editingModule.alt" placeholder="图片描述（SEO用）" />
            </el-form-item>
            <el-form-item label="点击链接">
              <el-input v-model="editingModule.link" placeholder="图片点击后的跳转链接（可选）" />
            </el-form-item>
            <el-form-item label="对齐方式">
              <el-select v-model="editingModule.align">
                <el-option label="居中" value="center" />
                <el-option label="左对齐" value="left" />
                <el-option label="右对齐" value="right" />
              </el-select>
            </el-form-item>
          </el-form>

          <!-- Table Module -->
          <el-form v-else-if="editingModule?.type === 'table'" :model="editingModule" label-width="100px">
            <el-form-item label="表格尺寸">
              <div class="flex gap-4">
                <el-input-number v-model="editingModule.rows" :min="1" :max="20" label="行数" />
                <el-input-number v-model="editingModule.cols" :min="1" :max="10" label="列数" />
                <el-button @click="regenerateTable">重新生成</el-button>
              </div>
            </el-form-item>
            <el-form-item label="表头">
              <el-input v-model="editingModule.header" placeholder="输入表头，用逗号分隔，如：名称,值,说明" />
            </el-form-item>
            <div v-if="editingModule.tableData" class="table-edit-wrapper border rounded p-2 max-h-60 overflow-auto">
              <table class="w-full text-sm border-collapse">
                <tbody>
                  <tr v-for="(row, ri) in editingModule.tableData" :key="ri" class="border-b border-gray-200">
                    <td
                      v-for="(cell, ci) in row"
                      :key="ci"
                      class="border border-gray-300 p-1"
                    >
                      <textarea
                        v-model="editingModule.tableData[ri][ci]"
                        class="w-full border-0 bg-transparent p-0 resize-none"
                        rows="2"
                      ></textarea>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </el-form>

          <!-- Two Column Module -->
          <el-form v-else-if="editingModule?.type === 'two_column'" :model="editingModule" label-width="100px">
            <el-form-item label="左侧内容">
              <el-select v-model="editingModule.leftType">
                <el-option label="图片" value="image" />
                <el-option label="文字" value="text" />
              </el-select>
            </el-form-item>
            <template v-if="editingModule.leftType === 'image'">
              <el-form-item label="左侧图片">
                <div class="flex items-center gap-4">
                  <el-upload
                    :action="`${config.public.apiBase}/api/admin/upload`"
                    :headers="headers"
                    :show-file-list="false"
                    :on-success="(res) => handleTwoColImageUpload(res, 'left')"
                    :before-upload="beforeImageUpload"
                    accept="image/*"
                  >
                    <el-button type="primary" plain size="small">上传</el-button>
                  </el-upload>
                  <el-input v-model="editingModule.leftImage" placeholder="或URL" class="flex-1" />
                </div>
                <img v-if="editingModule.leftImage" :src="getFullImageUrl(editingModule.leftImage)" class="mt-2 h-24 rounded" />
              </el-form-item>
            </template>
            <template v-else>
              <el-form-item label="左侧文字">
                <el-input v-model="editingModule.leftText" type="textarea" :rows="3" />
              </el-form-item>
            </template>
            <el-form-item label="右侧内容">
              <el-select v-model="editingModule.rightType">
                <el-option label="图片" value="image" />
                <el-option label="文字" value="text" />
              </el-select>
            </el-form-item>
            <template v-if="editingModule.rightType === 'image'">
              <el-form-item label="右侧图片">
                <div class="flex items-center gap-4">
                  <el-upload
                    :action="`${config.public.apiBase}/api/admin/upload`"
                    :headers="headers"
                    :show-file-list="false"
                    :on-success="(res) => handleTwoColImageUpload(res, 'right')"
                    :before-upload="beforeImageUpload"
                    accept="image/*"
                  >
                    <el-button type="primary" plain size="small">上传</el-button>
                  </el-upload>
                  <el-input v-model="editingModule.rightImage" placeholder="或URL" class="flex-1" />
                </div>
                <img v-if="editingModule.rightImage" :src="getFullImageUrl(editingModule.rightImage)" class="mt-2 h-24 rounded" />
              </el-form-item>
            </template>
            <template v-else>
              <el-form-item label="右侧文字">
                <el-input v-model="editingModule.rightText" type="textarea" :rows="3" />
              </el-form-item>
            </template>
          </el-form>

          <!-- Downloads Module -->
          <el-form v-else-if="editingModule?.type === 'downloads'" :model="editingModule" label-width="100px">
            <el-form-item label="下载文件">
              <div class="space-y-2">
                <div v-for="(item, idx) in editingModule.items" :key="idx" class="flex gap-2 items-center">
                  <el-input v-model="item.label" placeholder="文件名称" class="flex-1" />
                  <el-input v-model="item.url" placeholder="文件URL" class="flex-1" />
                  <el-button type="danger" @click="editingModule.items.splice(idx, 1)" :icon="Delete" circle />
                </div>
                <el-button size="small" @click="editingModule.items.push({ label: '', url: '' })">
                  <el-icon><Plus /></el-icon> 添加文件
                </el-button>
              </div>
            </el-form-item>
          </el-form>

          <template #footer>
            <el-button @click="showModuleEditDialog = false">取消</el-button>
            <el-button type="primary" @click="saveModule">保存</el-button>
          </template>
        </el-dialog>

        <!-- Download PDF (single) -->
        <el-form-item label="下载PDF（独立）">
          <div class="flex items-center gap-4">
            <el-upload
              :action="`${config.public.apiBase}/api/admin/upload`"
              :headers="headers"
              :show-file-list="false"
              :on-success="handleDownloadPdfSuccess"
              :on-progress="(evt, file, fileList) => handleUploadProgress(evt, file, fileList, 'downloadPdf')"
              :before-upload="beforePdfUpload"
              accept=".pdf"
            >
              <el-button type="primary" plain :disabled="uploading">上传下载PDF</el-button>
            </el-upload>
            <el-input v-model="form.downloadPdfUrl" placeholder="下载PDF URL（与上方展示PDF不同）" class="flex-1" />
          </div>
          <div v-if="uploading && uploadType === 'downloadPdf'" class="mt-2">
            <el-progress :percentage="uploadPercentage" :stroke-width="6" />
          </div>
          <div v-if="form.downloadPdfUrl" class="mt-2 flex items-center gap-2 px-2 py-1 bg-gray-100 rounded text-sm">
            <svg class="w-4 h-4 text-red-500" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L15.414 6A2 2 0 0116 7.414V16a2 2 0 01-2 2H6a2 2 0 01-2-2V4z" clip-rule="evenodd" />
            </svg>
            <span class="text-gray-600 truncate flex-1">{{ getFileName(form.downloadPdfUrl) }}</span>
            <button @click="form.downloadPdfUrl = ''" class="text-red-500 hover:text-red-700">×</button>
          </div>
        </el-form-item>

        <el-form-item :label="$t('admin.featured')">
          <el-switch v-model="form.featured" />
        </el-form-item>
        <el-form-item :label="$t('admin.sortOrder')">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item :label="$t('admin.specs')">
          <div class="specs-editor w-full">
            <div v-for="(spec, index) in form.specs" :key="index" class="flex gap-2 mb-2">
              <el-input v-model="spec.key" :placeholder="$t('admin.specKey')" class="flex-1" />
              <el-input v-model="spec.value" :placeholder="$t('admin.specValue')" class="flex-1" />
              <el-button type="danger" @click="removeSpec(index)" :icon="Delete" circle />
            </div>
            <el-button type="primary" plain @click="addSpec" :icon="Plus">{{ $t('admin.addSpec') }}</el-button>
          </div>
        </el-form-item>
        <el-form-item label="亮点 (Highlights)">
          <el-input v-model="form.highlights" type="textarea" :rows="3" placeholder="输入产品亮点，多个亮点用换行分隔" />
        </el-form-item>
        <el-form-item label="应用范围 (Applications)">
          <el-input v-model="form.applications" type="textarea" :rows="3" placeholder="输入产品应用范围" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">{{ $t('admin.cancel') }}</el-button>
        <el-button type="primary" @click="saveProduct">{{ $t('admin.save') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { Plus, Delete, Rank } from '@element-plus/icons-vue'
definePageMeta({
  layout: 'admin',
})

const { t } = useI18n()
const config = useRuntimeConfig()
const headers = ref({})

// Module Editor
const showModuleDialog = ref(false)
const showModuleEditDialog = ref(false)
const editingModuleIndex = ref(-1)
const editingModule = ref(null)

const moduleTypes = [
  { value: 'heading', name: '标题', icon: '📌', desc: '大标题、副标题' },
  { value: 'text', name: '文字', icon: '📝', desc: '段落文字' },
  { value: 'image', name: '图片', icon: '🖼️', desc: '单张图片' },
  { value: 'table', name: '表格', icon: '📊', desc: '参数表格' },
  { value: 'two_column', name: '双栏', icon: '📐', desc: '左右分栏布局' },
  { value: 'downloads', name: '下载', icon: '📥', desc: '下载文件按钮' },
]

const getModuleTypeName = (type) => {
  return moduleTypes.find(m => m.value === type)?.name || type
}

const generateId = () => Math.random().toString(36).substr(2, 9)

const createModule = (type) => {
  const base = { id: generateId(), type }
  switch (type) {
    case 'heading':
      return { ...base, level: 2, content: '' }
    case 'text':
      return { ...base, content: '' }
    case 'image':
      return { ...base, url: '', alt: '', link: '', align: 'center' }
    case 'table':
      return { ...base, rows: 3, cols: 3, header: '', tableData: [] }
    case 'two_column':
      return { ...base, leftType: 'image', leftImage: '', leftText: '', rightType: 'text', rightImage: '', rightText: '' }
    case 'downloads':
      return { ...base, items: [{ label: '', url: '' }] }
    default:
      return base
  }
}

const addModule = (type) => {
  const module = createModule(type)
  if (type === 'table') {
    initTableData(module)
  }
  editingModule.value = JSON.parse(JSON.stringify(module))
  editingModuleIndex.value = -1
  showModuleDialog.value = false
  showModuleEditDialog.value = true
}

const editModule = (index) => {
  editingModule.value = JSON.parse(JSON.stringify(form.modules[index]))
  if (form.modules[index].type === 'table') {
    initTableData(editingModule.value)
  }
  editingModuleIndex.value = index
  showModuleEditDialog.value = true
}

const saveModule = () => {
  if (editingModuleIndex.value >= 0) {
    form.modules[editingModuleIndex.value] = JSON.parse(JSON.stringify(editingModule.value))
  } else {
    form.modules.push(JSON.parse(JSON.stringify(editingModule.value)))
  }
  showModuleEditDialog.value = false
}

const removeModule = (index) => {
  form.modules.splice(index, 1)
}

const moveModule = (index, direction) => {
  const newIndex = index + direction
  if (newIndex < 0 || newIndex >= form.modules.length) return
  const temp = form.modules[index]
  form.modules[index] = form.modules[newIndex]
  form.modules[newIndex] = temp
}

const regenerateTable = () => {
  if (!editingModule.value) return
  const { rows, cols } = editingModule.value
  editingModule.value.tableData = []
  const headers = editingModule.value.header ? editingModule.value.header.split(',') : []
  for (let r = 0; r < rows; r++) {
    const row = []
    for (let c = 0; c < cols; c++) {
      row.push(r === 0 && headers[c] ? headers[c].trim() : '')
    }
    editingModule.value.tableData.push(row)
  }
}

const initTableData = (module) => {
  if (!module.tableData || module.tableData.length === 0) {
    module.tableData = []
    const headers = module.header ? module.header.split(',') : []
    for (let r = 0; r < module.rows; r++) {
      const row = []
      for (let c = 0; c < module.cols; c++) {
        row.push(r === 0 && headers[c] ? headers[c].trim() : '')
      }
      module.tableData.push(row)
    }
  }
}

const handleModuleImageUpload = (response) => {
  if (response.code === 200 && response.data) {
    const url = typeof response.data === 'string' ? response.data : response.data.url
    if (url && editingModule.value) {
      editingModule.value.url = url
    }
    ElMessage.success(t('admin.uploadSuccess'))
  } else {
    ElMessage.error(response.message || t('admin.uploadFailed'))
  }
}

const handleTwoColImageUpload = (response, side) => {
  if (response.code === 200 && response.data) {
    const url = typeof response.data === 'string' ? response.data : response.data.url
    if (url && editingModule.value) {
      editingModule.value[side + 'Image'] = url
    }
    ElMessage.success(t('admin.uploadSuccess'))
  } else {
    ElMessage.error(response.message || t('admin.uploadFailed'))
  }
}

const truncate = (str, len) => {
  if (!str) return ''
  return str.length > len ? str.substring(0, len) + '...' : str
}

// 上传图片到服务器
const uploadImage = async (file) => {
  try {
    const formData = new FormData()
    formData.append('file', file)

    const response = await fetch(`${config.public.apiBase}/api/admin/upload`, {
      method: 'POST',
      headers: headers.value,
      body: formData
    })
    const result = await response.json()

    if (result.code === 200 && result.data?.url) {
      return result.data.url
    } else {
      ElMessage.error(result.message || 'Upload failed')
      return null
    }
  } catch (error) {
    console.error('Upload error:', error)
    ElMessage.error('Upload failed')
    return null
  }
}

// 处理拖拽上传
const handleDrop = async (e, quill) => {
  e.preventDefault()
  e.stopPropagation()

  const files = e.dataTransfer?.files
  if (!files || files.length === 0) return

  for (let i = 0; i < files.length; i++) {
    const file = files[i]
    if (!file.type.startsWith('image/')) continue

    const url = await uploadImage(file)
    if (url) {
      const range = quill.getSelection(true)
      quill.insertEmbed(range.index, 'image', url)
      quill.setSelection(range.index + 1)
    }
  }
}

// Quill 配置
const quillOptions = {
  theme: 'snow',
  placeholder: 'Enter content...',
}

onMounted(() => {
  if (!import.meta.client) return

  const token = localStorage.getItem('admin_token')
  headers.value = { Authorization: `Bearer ${token}` }

  if (!token) navigateTo('/admin/login')
  fetchData()
})

const data = ref([])
const flatCategories = ref([])
const showDialog = ref(false)
const isEdit = ref(false)
const uploading = ref(false)
const uploadPercentage = ref(0)
const uploadType = ref('')
const form = reactive({
  id: null,
  categoryId: null,
  nameEn: '',
  nameZh: '',
  slug: '',
  descriptionEn: '',
  descriptionZh: '',
  imageUrl: '',
  imageUrls: [],
  modules: [],
  downloadPdfUrl: '',
  specs: [],
  featured: false,
  sortOrder: 0,
  highlights: '',
  applications: '',
})

const treeProps = {
  children: 'products',
  label: 'nameEn',
}

// Build tree data: categories with their products as children
const treeData = computed(() => {
  const result = []

  for (const cat of flatCategories.value) {
    const categoryNode = {
      id: `cat_${cat.id}`,
      nameEn: cat.nameEn,
      nameZh: cat.nameZh,
      isCategory: true,
      productCount: 0,
      products: [],
    }

    const catProducts = data.value.filter(p => p.categoryId === cat.id)
    categoryNode.productCount = catProducts.length

    for (const product of catProducts) {
      categoryNode.products.push({
        ...product,
        isCategory: false,
      })
    }

    // Sort products by sortOrder
    categoryNode.products.sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0))

    // Only add category if it has products or we want to show empty categories
    if (categoryNode.products.length > 0) {
      result.push(categoryNode)
    }
  }

  // Add uncategorized products
  const uncategorizedProducts = data.value.filter(p => !p.categoryId)
  if (uncategorizedProducts.length > 0) {
    result.push({
      id: 'cat_uncategorized',
      nameEn: 'Uncategorized',
      nameZh: '未分类',
      isCategory: true,
      productCount: uncategorizedProducts.length,
      products: uncategorizedProducts.map(p => ({
        ...p,
        isCategory: false,
      })),
    })
  }

  return result
})

const fetchData = async () => {
  try {
    const response = await $fetch(`${config.public.apiBase}/api/admin/products`, { headers: headers.value })
    data.value = response.data || []
    await fetchCategories()
  } catch (error) {
    ElMessage.error(t('admin.fetchFailed'))
  }
}

const fetchCategories = async () => {
  try {
    const response = await $fetch(`${config.public.apiBase}/api/admin/categories/tree`, { headers: headers.value })
    const tree = response.data || []
    // Flatten the tree for the select dropdown
    flatCategories.value = flattenCategories(tree)
  } catch (error) {
    console.error(t('admin.fetchFailed'))
  }
}

const flattenCategories = (cats, result = []) => {
  for (const cat of cats) {
    result.push({ id: cat.id, nameEn: cat.nameEn, nameZh: cat.nameZh })
    if (cat.children && cat.children.length > 0) {
      flattenCategories(cat.children, result)
    }
  }
  return result
}

const addProduct = () => {
  isEdit.value = false
  resetForm()
  showDialog.value = true
}

const addProductUnderCategory = (categoryId) => {
  isEdit.value = false
  resetForm()
  // Extract actual category id from "cat_{id}" format
  const catId = categoryId.startsWith('cat_') ? categoryId.replace('cat_', '') : categoryId
  form.categoryId = parseInt(catId) || null
  showDialog.value = true
}

const resetForm = () => {
  Object.keys(form).forEach(k => {
    if (k === 'sortOrder') form[k] = 0
    else if (k === 'featured') form[k] = false
    else if (['specs', 'imageUrls', 'modules'].includes(k)) form[k] = []
    else if (['highlights', 'applications', 'downloadPdfUrl'].includes(k)) form[k] = ''
    else form[k] = null
  })
}

const editProduct = (row) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    categoryId: row.categoryId,
    nameEn: row.nameEn,
    nameZh: row.nameZh,
    slug: row.slug,
    descriptionEn: row.descriptionEn || row.descriptionZh || '',
    descriptionZh: row.descriptionZh || row.descriptionEn || '',
    imageUrl: parseUrl(row.imageUrl),
    imageUrls: parseJsonArray(row.imageUrls),
    modules: parseModules(row.overviewModules),
    downloadPdfUrl: row.downloadPdfUrl || '',
    specs: parseSpecs(row.specs),
    featured: row.featured || false,
    sortOrder: row.sortOrder || 0,
    highlights: row.highlights || '',
    applications: row.applications || '',
  })
  showDialog.value = true
}

const parseModules = (val) => {
  if (!val) return []
  if (Array.isArray(val)) return val
  if (typeof val === 'string' && val) {
    try { return JSON.parse(val) } catch { return [] }
  }
  return []
}

const parseJsonArray = (val) => {
  if (Array.isArray(val)) return [...val]
  if (typeof val === 'string' && val) {
    try { return JSON.parse(val) } catch { return [] }
  }
  return []
}

const parseUrl = (val) => {
  if (!val) return ''
  if (typeof val === 'string') return val
  if (typeof val === 'object' && val !== null) return val.url || ''
  return ''
}

const parseSpecs = (specsObj) => {
  if (!specsObj || typeof specsObj !== 'object') return []
  return Object.entries(specsObj).map(([key, value]) => ({ key, value }))
}

const parseExternalImages = (val) => {
  if (!val) return []
  if (Array.isArray(val)) {
    return val.map(item => ({
      url: typeof item === 'string' ? item : (item?.url || ''),
      link: typeof item === 'object' && item !== null ? (item.link || '') : '',
    }))
  }
  if (typeof val === 'string' && val) {
    try {
      const parsed = JSON.parse(val)
      if (Array.isArray(parsed)) {
        return parsed.map(item => ({
          url: typeof item === 'string' ? item : (item?.url || ''),
          link: typeof item === 'object' && item !== null ? (item.link || '') : '',
        }))
      }
    } catch { /* ignore */ }
  }
  return []
}

const addSpec = () => {
  form.specs.push({ key: '', value: '' })
}

const removeSpec = (index) => {
  form.specs.splice(index, 1)
}

const handleImageUploadSuccess = (response) => {
  uploading.value = false
  uploadPercentage.value = 0
  if (response.code === 200 && response.data) {
    form.imageUrl = typeof response.data === 'string' ? response.data : (response.data.url || '')
    ElMessage.success(t('admin.uploadSuccess'))
  } else {
    ElMessage.error(response.message || t('admin.uploadFailed'))
  }
}

const handleMultiImageSuccess = (response) => {
  uploading.value = false
  uploadPercentage.value = 0
  if (response.code === 200 && response.data) {
    const url = typeof response.data === 'string' ? response.data : response.data.url
    if (url) form.imageUrls.push(url)
    ElMessage.success(t('admin.uploadSuccess'))
  } else {
    ElMessage.error(response.message || t('admin.uploadFailed'))
  }
}

const handleDownloadPdfSuccess = (response) => {
  uploading.value = false
  uploadPercentage.value = 0
  if (response.code === 200 && response.data) {
    const url = typeof response.data === 'string' ? response.data : response.data.url
    if (url) form.downloadPdfUrl = url
    ElMessage.success(t('admin.uploadSuccess'))
  } else {
    ElMessage.error(response.message || t('admin.uploadFailed'))
  }
}

const beforePdfUpload = (file) => {
  const isPdf = file.type === 'application/pdf' || /\.pdf$/i.test(file.name)
  const isLt50M = file.size / 1024 / 1024 < 50
  if (!isPdf) ElMessage.error('仅支持 PDF 文件')
  if (!isLt50M) ElMessage.error('PDF 大小需小于 50MB')
  return isPdf && isLt50M
}

const handleUploadProgress = (event, file, fileList, type) => {
  uploading.value = true
  uploadType.value = type
  uploadPercentage.value = Math.round(event.percent || 0)
}

const removeImageUrl = (index) => {
  form.imageUrls.splice(index, 1)
}

const getFileName = (url) => {
  if (!url) return ''
  return url.split('/').pop() || url
}

const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isImage) {
    ElMessage.error(t('admin.uploadImageOnly'))
  }
  if (!isLt10M) {
    ElMessage.error(t('admin.uploadImageSize'))
  }
  return isImage && isLt10M
}

const getFullImageUrl = (url) => {
  if (!url) return ''
  const strUrl = String(url)
  if (strUrl.startsWith('http://') || strUrl.startsWith('https://')) {
    return strUrl
  }
  const base = config.public.apiBase?.replace('/api', '') || ''
  return `${base}${strUrl}`
}

const saveProduct = async () => {
  if (!form.nameEn || !form.slug) {
    ElMessage.warning(t('admin.pleaseEnter') + ' nameEn and slug')
    return
  }

  try {
    const method = isEdit.value ? 'PUT' : 'POST'
    const url = isEdit.value
      ? `${config.public.apiBase}/api/admin/products/${form.id}`
      : `${config.public.apiBase}/api/admin/products`

    // Convert specs arrays to JSON objects
    const specsObj = {}
    form.specs.forEach(spec => {
      if (spec.key && spec.value) specsObj[spec.key] = spec.value
    })

    const body = {
      ...form,
      specsJson: Object.keys(specsObj).length > 0 ? JSON.stringify(specsObj) : null,
      imageUrls: form.imageUrls.length > 0 ? JSON.stringify(form.imageUrls) : null,
      overviewModules: form.modules.length > 0 ? JSON.stringify(form.modules) : null,
    }

    await $fetch(url, { method, headers: headers.value, body })
    ElMessage.success(t('admin.saveSuccess'))
    showDialog.value = false
    fetchData()
  } catch (error) {
    ElMessage.error(t('admin.saveFailed') + ': ' + (error.data?.message || error.message || ''))
  }
}

const removeProduct = async (id) => {
  try {
    await ElMessageBox.confirm(t('admin.confirmDelete'), t('admin.delete'), {
      confirmButtonText: t('admin.yes'),
      cancelButtonText: t('admin.cancel'),
      type: 'warning',
    })

    await $fetch(`${config.public.apiBase}/api/admin/products/${id}`, { method: 'DELETE', headers: headers.value })
    ElMessage.success(t('admin.deleteSuccess'))
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      const errorMsg = error.data?.message || error._data?.message || error.message || ''
      ElMessage.error(t('admin.deleteFailed') + (errorMsg ? ': ' + errorMsg : ''))
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}
.header-text h1 {
  font-size: 1.25rem;
  margin-bottom: 4px;
}
.add-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}
.add-btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}
.main-card {
  border-radius: 12px;
  border: none;
}
.product-tree {
  background: transparent;
}
.tree-node {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding-right: 8px;
}
.node-content {
  display: flex;
  align-items: center;
  gap: 4px;
}
.category-icon {
  color: #8b5cf6;
  margin-right: 8px;
}
.category-label,
.product-label {
  display: flex;
  align-items: center;
}
.node-actions {
  display: flex;
  gap: 8px;
}
:deep(.el-tree-node__content) {
  height: 48px;
  border-radius: 8px;
  margin: 2px 0;
}
:deep(.el-tree-node__content:hover) {
  background: #f3f4f6;
}
.empty-state {
  text-align: center;
  padding: 60px 20px;
}
.module-editor {
  background: #fafafa;
  border-radius: 8px;
  padding: 16px;
}
.module-list {
  max-height: 400px;
  overflow-y: auto;
}
.module-item {
  transition: all 0.2s;
}
.module-item:hover {
  border-color: #409eff;
}
.module-types-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}
.module-type-item {
  text-align: center;
}
.table-edit-wrapper input {
  outline: none;
}
.table-edit-wrapper input:focus {
  background: #f0f7ff;
}
</style>
