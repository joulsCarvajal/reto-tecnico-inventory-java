<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated class="bg-primary text-white">
      <q-toolbar>
        <q-btn flat dense round icon="menu" aria-label="Menu" @click="drawerOpen = !drawerOpen" />
        <q-toolbar-title>
          <q-icon name="inventory_2" class="q-mr-sm" />
          Gestión Empresarial
        </q-toolbar-title>
        <div class="text-caption q-mr-md">
          {{ authStore.user?.nombre }} ({{ authStore.user?.rol }})
        </div>
        <q-btn flat round dense icon="logout" @click="logout" />
      </q-toolbar>
    </q-header>

    <q-drawer v-model="drawerOpen" show-if-above bordered>
      <q-list padding>
        <q-item-label header class="text-primary text-weight-bold">
          Navegación
        </q-item-label>

        <q-item clickable v-ripple :to="'/empresas'" active-class="text-primary">
          <q-item-section avatar>
            <q-icon name="business" />
          </q-item-section>
          <q-item-section>Empresas</q-item-section>
        </q-item>

        <template v-if="authStore.isAdmin">
          <q-item clickable v-ripple :to="'/productos'" active-class="text-primary">
            <q-item-section avatar>
              <q-icon name="category" />
            </q-item-section>
            <q-item-section>Productos</q-item-section>
          </q-item>

          <q-item clickable v-ripple :to="'/inventario'" active-class="text-primary">
            <q-item-section avatar>
              <q-icon name="inventory" />
            </q-item-section>
            <q-item-section>Inventario</q-item-section>
          </q-item>
        </template>
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from 'src/stores/auth'
import { useQuasar } from 'quasar'

const authStore = useAuthStore()
const router = useRouter()
const $q = useQuasar()
const drawerOpen = ref(true)

function logout() {
  $q.dialog({
    title: 'Cerrar sesión',
    message: '¿Estás seguro que deseas cerrar sesión?',
    cancel: true,
    persistent: true
  }).onOk(() => {
    authStore.logout()
    router.push('/login')
  })
}
</script>
