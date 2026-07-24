<template>
  <aside class="w-full lg:w-64 flex-shrink-0">
    <div class="bg-white rounded-lg shadow-sm border border-gray-100 overflow-hidden">
      <div class="p-4 border-b border-gray-100">
        <h3 class="font-semibold text-gray-900">{{ $t('products.filterBy') }}</h3>
      </div>

      <!-- All Products -->
      <div class="p-2">
        <button
          @click="$emit('category-change', 'all')"
          class="w-full px-3 py-2 text-left rounded-md text-sm font-medium transition-colors"
          :class="selectedCategory === 'all' ? 'bg-blue-50 text-blue-600' : 'text-gray-600 hover:bg-gray-50'"
        >
          {{ $t('products.allProducts') }}
        </button>
      </div>

      <!-- Category Groups -->
      <div
        v-for="category in categories"
        :key="category.key"
        class="border-t border-gray-100 category-group"
        @mouseenter="hoveredCategory = category.key"
        @mouseleave="hoveredCategory = null"
      >
        <!-- Category Header (一级) -->
        <button
          @click="toggleCategory(category.key)"
          class="w-full px-4 py-3 text-left flex items-center justify-between hover:bg-gray-50 transition-colors"
          :class="{ 'bg-blue-50': isCategoryActive(category.key) }"
        >
          <span class="font-medium text-gray-900 text-sm">{{ category.label }}</span>
          <svg
            class="w-4 h-4 text-gray-400 transition-transform duration-200"
            :class="{ 'rotate-180': expandedCategories.includes(category.key) }"
            fill="none" viewBox="0 0 24 24" stroke="currentColor"
          >
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
          </svg>
        </button>

        <!-- Subcategories (二级) - 悬停显示 -->
        <div
          v-if="expandedCategories.includes(category.key) && category.subcategories?.length"
          class="subcategories bg-gray-50"
        >
          <button
            v-for="sub in category.subcategories"
            :key="sub.key"
            @click="$emit('category-change', sub.key)"
            class="w-full px-6 py-2 text-left text-sm transition-colors flex items-center"
            :class="selectedCategory === sub.key ? 'text-blue-600 font-medium' : 'text-gray-500 hover:text-blue-600 hover:bg-blue-50'"
          >
            <span class="w-1.5 h-1.5 rounded-full mr-2" :class="selectedCategory === sub.key ? 'bg-blue-600' : 'bg-gray-300'"></span>
            {{ sub.label }}
          </button>
        </div>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
interface Subcategory {
  key: string
  label: string
}

interface Category {
  key: string
  label: string
  subcategories?: Subcategory[]
}

const props = defineProps<{
  selectedCategory: string
  categories: Category[]
}>()

defineEmits<{
  (e: 'category-change', category: string): void
}>()

const expandedCategories = ref<string[]>([])
const hoveredCategory = ref<string | null>(null)

// Auto-expand all categories when categories change
watch(() => props.categories, (newCategories) => {
  expandedCategories.value = newCategories.map(c => c.key)
}, { immediate: true, deep: true })

// 悬停时保持子菜单展开
watch(hoveredCategory, (newVal) => {
  if (newVal && !expandedCategories.value.includes(newVal)) {
    expandedCategories.value.push(newVal)
  }
})

function toggleCategory(key: string) {
  const index = expandedCategories.value.indexOf(key)
  if (index > -1) {
    expandedCategories.value.splice(index, 1)
  } else {
    expandedCategories.value.push(key)
  }
}

function isCategoryActive(key: string) {
  if (props.selectedCategory === 'all') return false
  return props.selectedCategory.startsWith(key)
}
</script>

<style scoped>
.category-group {
  position: relative;
}

.subcategories {
  /* 确保悬停区域连续，没有间隙 */
  padding-top: 2px;
  padding-bottom: 2px;
}

.subcategories button {
  position: relative;
}

/* 确保从父按钮移动到子菜单时不会触发间隙 */
.category-group::before {
  content: '';
  position: absolute;
  left: 0;
  top: 100%;
  width: 100%;
  height: 8px;
  background: transparent;
  z-index: 10;
}
</style>
