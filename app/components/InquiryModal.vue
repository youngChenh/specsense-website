<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center p-4">
    <!-- Backdrop -->
    <div class="absolute inset-0 bg-black bg-opacity-50" @click="$emit('close')"></div>

    <!-- Modal -->
    <div class="relative bg-white rounded-2xl shadow-2xl w-full max-w-md p-6 max-h-[90vh] overflow-y-auto">
      <!-- Close Button -->
      <button
        @click="$emit('close')"
        class="absolute top-4 right-4 p-2 text-gray-400 hover:text-gray-600 transition-colors"
      >
        <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>

      <!-- Header -->
      <div class="mb-6">
        <h2 class="text-2xl font-bold text-gray-900">{{ $t('products.inquiry') }}</h2>
        <p class="text-gray-600 mt-1">{{ productName }}</p>
      </div>

      <!-- Form -->
      <form @submit.prevent="submitForm" class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            {{ $t('contact.form.name') }} *
          </label>
          <input
            v-model="form.name"
            type="text"
            required
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            {{ $t('contact.form.email') }} *
          </label>
          <input
            v-model="form.email"
            type="email"
            required
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            {{ $t('contact.form.company') }}
          </label>
          <input
            v-model="form.company"
            type="text"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            {{ $t('contact.form.product') }}
          </label>
          <input
            v-model="form.product"
            type="text"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            {{ $t('contact.form.message') }} *
          </label>
          <textarea
            v-model="form.message"
            required
            rows="4"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          ></textarea>
        </div>

        <button
          type="submit"
          :disabled="isSubmitting"
          class="w-full px-6 py-3 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 transition-colors disabled:bg-blue-400"
        >
          {{ isSubmitting ? $t('contact.form.sending') : $t('contact.form.submit') }}
        </button>
      </form>
    </div>

    <!-- Success Toast -->
    <Teleport to="body">
      <div
        v-if="showToast"
        class="fixed inset-0 z-[200] flex items-center justify-center pointer-events-none"
      >
        <div class="bg-gray-900/90 text-white px-8 py-4 rounded-xl shadow-2xl flex items-center gap-3">
          <svg class="w-6 h-6 text-green-400 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
          </svg>
          <span class="text-lg font-medium">{{ $t('contact.form.success') }}</span>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  productName: string
}>()

const emit = defineEmits<{
  close: []
}>()

const api = useApi()

const form = reactive({
  name: '',
  email: '',
  company: '',
  product: props.productName,
  message: '',
})

const isSubmitting = ref(false)
const showToast = ref(false)

async function submitForm() {
  isSubmitting.value = true

  try {
    await api.submitContact({
      name: form.name,
      email: form.email,
      company: form.company,
      product: form.product,
      message: form.message,
    })
    showToast.value = true
    await new Promise(resolve => setTimeout(resolve, 1200))
    emit('close')
  } catch {
    isSubmitting.value = false
  }
}
</script>