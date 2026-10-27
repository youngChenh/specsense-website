<template>
  <div class="login-page">
    <div class="login-panel">
      <div class="brand">
        <img src="/logo.jpg" alt="SpeSense" class="brand-logo" />
        <div>
          <div class="brand-name">SpeSense</div>
          <div class="brand-sub">后台管理</div>
        </div>
      </div>
      <h1 class="headline">客户、询盘与官网内容<br />在同一处管理</h1>
      <p class="lead">销售跟进线索，管理员维护网站。登录后按角色进入对应工作台。</p>
      <ul class="points">
        <li>CRM 看板、客户与询盘跟进</li>
        <li>产品、新闻与品牌内容发布</li>
      </ul>
    </div>

    <div class="login-form-wrap">
      <div class="login-card">
        <h2>{{ $t('admin.loginTitle') }}</h2>
        <p class="hint">使用后台账号继续</p>
        <el-form @submit.prevent="handleLogin" class="form">
          <label>{{ $t('admin.username') }}</label>
          <el-input
            v-model="form.username"
            size="large"
            :placeholder="$t('admin.pleaseEnter') + $t('admin.username')"
            class="login-input"
            @keyup.enter="handleLogin"
          />
          <label>{{ $t('admin.password') }}</label>
          <el-input
            v-model="form.password"
            type="password"
            size="large"
            show-password
            :placeholder="$t('admin.pleaseEnter') + $t('admin.password')"
            class="login-input"
            @keyup.enter="handleLogin"
          />
          <el-button class="submit" size="large" :loading="loading" @click="handleLogin">
            {{ $t('admin.loginBtn') }}
          </el-button>
        </el-form>
        <p class="copy">© 2026 SpeSense Technologies</p>
      </div>
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
        localStorage.setItem('admin_role', response.data.role || '')
        localStorage.setItem('admin_username', response.data.username || form.username)
        if (response.data.userId) localStorage.setItem('admin_user_id', String(response.data.userId))
      }
      ElMessage.success(t('admin.loginSuccess'))
      const role = response.data.role || ''
      navigateTo(role === 'admin' || role === 'editor' ? '/admin' : '/admin/crm')
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
.login-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1.05fr 1fr;
  background: #f5f7fa;
}
.login-panel {
  padding: 56px 64px;
  background:
    radial-gradient(circle at 20% 20%, rgba(102, 126, 234, 0.28), transparent 42%),
    linear-gradient(180deg, #1e1e3f 0%, #1a1a2e 100%);
  color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 48px;
}
.brand-logo {
  height: 44px;
  width: auto;
  border-radius: 10px;
  object-fit: contain;
}
.brand-name {
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 0.3px;
}
.brand-sub {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.55);
}
.headline {
  font-size: 34px;
  line-height: 1.25;
  font-weight: 700;
  margin-bottom: 16px;
}
.lead {
  max-width: 420px;
  color: rgba(255, 255, 255, 0.68);
  font-size: 15px;
  line-height: 1.7;
  margin-bottom: 28px;
}
.points {
  color: rgba(255, 255, 255, 0.78);
  font-size: 14px;
  line-height: 2;
  padding-left: 18px;
}
.login-form-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 24px;
}
.login-card {
  width: 100%;
  max-width: 380px;
}
.login-card h2 {
  font-size: 26px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 6px;
}
.hint {
  color: #6b7280;
  margin-bottom: 28px;
  font-size: 14px;
}
.form label {
  display: block;
  margin: 0 0 8px;
  font-size: 13px;
  font-weight: 600;
  color: #374151;
}
.form label + .login-input {
  margin-bottom: 18px;
}
.login-input :deep(.el-input__wrapper) {
  border-radius: 10px;
  padding: 4px 14px;
  box-shadow: 0 0 0 1px #e5e7eb inset;
}
.login-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #6366f1 inset;
}
.login-input :deep(.el-input__inner) {
  height: 42px;
}
.submit {
  width: 100%;
  margin-top: 8px;
  height: 44px;
  border: none;
  border-radius: 10px;
  background: #4f46e5;
  color: #fff;
  font-weight: 600;
}
.submit:hover {
  background: #4338ca;
  color: #fff;
}
.copy {
  margin-top: 28px;
  font-size: 12px;
  color: #9ca3af;
}
@media (max-width: 900px) {
  .login-page {
    grid-template-columns: 1fr;
  }
  .login-panel {
    padding: 32px 24px 28px;
    min-height: auto;
  }
  .headline {
    font-size: 26px;
  }
  .lead, .points {
    display: none;
  }
  .brand {
    margin-bottom: 0;
  }
}
</style>
