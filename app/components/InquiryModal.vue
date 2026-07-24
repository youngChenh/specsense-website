<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center p-4">
    <!-- Backdrop -->
    <div class="absolute inset-0 bg-black bg-opacity-50" @click="$emit('close')"></div>

    <!-- Modal -->
    <div class="relative bg-white rounded-2xl shadow-2xl w-full max-w-lg p-8 max-h-[90vh] overflow-y-auto">
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

        <div v-if="submitStatus === 'success'" class="p-3 bg-green-50 text-green-700 rounded-lg text-center">
          {{ $t('contact.form.success') }}
        </div>

        <div v-if="submitStatus === 'error'" class="p-3 bg-red-50 text-red-700 rounded-lg text-center">
          {{ $t('contact.form.error') }}
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  productName: string
}>()

const emit = defineEmits<{
  close: []
}>()

const form = reactive({
  name: '',
  email: '',
  company: '',
  message: '',
})

const isSubmitting = ref(false)
const submitStatus = ref<'idle' | 'success' | 'error'>('idle')

async function submitForm() {
  isSubmitting.value = true
  submitStatus.value = 'idle'

  try {
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1500))
    submitStatus.value = 'success'

    // Auto close after success
    setTimeout(() => {
      emit('close')
    }, 2000)
  } catch {
    submitStatus.value = 'error'
  } finally {
    isSubmitting.value = false
  }
}
</script>