<template>
  <div class="flex flex-center login-bg" style="min-height: 100vh;">
    <q-card class="login-card q-pa-lg shadow-10">
      <q-card-section class="text-center q-pb-none">
        <q-icon name="inventory_2" size="60px" color="primary" />
        <div class="text-h5 text-primary text-weight-bold q-mt-sm">Gestión Empresarial</div>
        <div class="text-caption text-grey-6">Reto Técnico - Líder Técnico Java</div>
      </q-card-section>

      <q-card-section>
        <q-form @submit.prevent="handleLogin" class="q-gutter-md">
          <q-input
            v-model="form.correo"
            label="Correo electrónico"
            type="email"
            outlined
            :rules="[val => !!val || 'Requerido', val => /.+@.+\..+/.test(val) || 'Correo inválido']"
          >
            <template #prepend>
              <q-icon name="email" />
            </template>
          </q-input>

          <q-input
            v-model="form.password"
            label="Contraseña"
            :type="showPassword ? 'text' : 'password'"
            outlined
            :rules="[val => !!val || 'Requerido']"
          >
            <template #prepend>
              <q-icon name="lock" />
            </template>
            <template #append>
              <q-icon
                :name="showPassword ? 'visibility_off' : 'visibility'"
                class="cursor-pointer"
                @click="showPassword = !showPassword"
              />
            </template>
          </q-input>

          <q-btn
            type="submit"
            label="Iniciar Sesión"
            color="primary"
            class="full-width"
            size="lg"
            :loading="loading"
            icon="login"
          />
        </q-form>
      </q-card-section>

      <q-card-section class="text-center q-pt-none">
        <div class="text-caption text-grey-6">
          <strong>Admin:</strong> admin@reto.com / Admin123! &nbsp;|&nbsp;
          <strong>Externo:</strong> externo@reto.com / Externo123!
        </div>
      </q-card-section>
    </q-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from 'src/stores/auth'
import { useQuasar } from 'quasar'

const authStore = useAuthStore()
const router = useRouter()
const $q = useQuasar()

const loading = ref(false)
const showPassword = ref(false)
const form = reactive({ correo: '', password: '' })

async function handleLogin() {
  loading.value = true
  try {
    await authStore.login(form.correo, form.password)
    $q.notify({ type: 'positive', message: `Bienvenido, ${authStore.user.nombre}` })
    router.push('/empresas')
  } catch (err) {
    $q.notify({
      type: 'negative',
      message: err.response?.data?.message || 'Credenciales inválidas'
    })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-bg {
  background: linear-gradient(135deg, #1565c0 0%, #42a5f5 100%);
  min-height: 100vh;
}
.login-card {
  width: 420px;
  max-width: 95vw;
  border-radius: 16px;
}
</style>
