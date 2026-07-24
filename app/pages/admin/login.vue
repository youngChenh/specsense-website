<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-900 via-blue-800 to-indigo-900">
    <!-- Decorative circles -->
    <div class="absolute top-20 left-20 w-64 h-64 bg-blue-400 rounded-full opacity-10 blur-3xl"></div>
    <div class="absolute bottom-20 right-20 w-96 h-96 bg-indigo-400 rounded-full opacity-10 blur-3xl"></div>
    <div class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-blue-300 rounded-full opacity-5 blur-3xl"></div>

    <div class="relative w-full max-w-md mx-4">
      <!-- Logo and Title -->
      <div class="text-center mb-8">
        <img src="/logo.jpg" alt="SpeSense" class="inline-flex items-center justify-center h-16 w-auto object-contain rounded-2xl shadow-lg mb-4" />
        <h1 class="text-3xl font-bold text-white mb-2">SpeSense</h1>
        <p class="text-blue-200">{{ $t('admin.loginTitle') }}</p>
      </div>

      <!-- Login Card -->
      <div class="bg-white rounded-2xl shadow-2xl p-8 backdrop-blur-sm bg-opacity-95">
        <el-form @submit.prevent="handleLogin" class="space-y-5">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">{{ $t('admin.username') }}</label>
            <el-input
              v-model="form.username"
              size="large"
              :placeholder="$t('admin.pleaseEnter') + $t('admin.username')"
              class="login-input"
            >
              <template #prefix>
                <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                </svg>
              </template>
            </el-input>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">{{ $t('admin.password') }}</label>
            <el-input
              v-model="form.password"
              type="password"
              size="large"
              :placeholder="$t('admin.pleaseEnter') + $t('admin.password')"
              show-password
              class="login-input"
            >
              <template #prefix>
                <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                </svg>
              </template>
            </el-input>
          </div>

          <el-button
            type="primary"
            size="large"
            class="w-full bg-gradient-to-r from-blue-600 to-indigo-600 border-0 hover:from-blue-700 hover:to-indigo-700 transition-all duration-200"
            @click="handleLogin"
            :loading="loading"
          >
            <span v-if="!loading" class="font-medium">{{ $t('admin.loginBtn') }}</span>
            <span v-else>...</span>
          </el-button>
        </el-form>
      </div>

      <!-- Footer -->
      <p class="text-center text-blue-200 text-sm mt-6">
        © 2026 SpeSense Technologies
      </p>
    </div>
  </div>
</template>

<script setup>
definePageMeta({
  layout: false,
})

const { t } = useI18n()
const config = useRuntimeConfig()
const form = reactive({
  username: '',
  password: '',
})
const loading = ref(false)

const handleLogin = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning(t('admin.pleaseEnter') + t('admin.username'))
    return
  }

  loading.value = true
  try {
    const response = await $fetch(`${config.public.apiBase}/api/admin/login`, {
      method: 'POST',
      body: form,
    })

    if (response.code === 200 && response.data) {
      if (import.meta.client) {
        localStorage.setItem('admin_token', response.data.token)
      }
      ElMessage.success(t('admin.loginSuccess'))
      navigateTo('/admin')
    } else {
      ElMessage.error(response.message || t('admin.loginFailed'))
    }
  } catch (error) {
    ElMessage.error(t('admin.loginFailed') + ': ' + error.message)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-input :deep(.el-input__wrapper) {
  padding: 4px 15px;
  border-radius: 10px;
}
.login-input :deep(.el-input__inner) {
  height: 40px;
}
</style>