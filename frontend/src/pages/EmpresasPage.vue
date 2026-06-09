<template>
  <q-page padding>
    <div class="row items-center q-mb-md">
      <div class="col">
        <div class="text-h5 text-primary text-weight-bold">
          <q-icon name="business" class="q-mr-sm" />Empresas
        </div>
        <div class="text-caption text-grey-6">Gestión de empresas registradas en el sistema</div>
      </div>
      <div class="col-auto" v-if="authStore.isAdmin">
        <q-btn color="primary" icon="add" label="Nueva Empresa" @click="openDialog()" />
      </div>
    </div>

    <q-table
      :rows="empresas"
      :columns="columns"
      row-key="nit"
      :loading="loading"
      flat bordered
      :filter="filter"
    >
      <template #top-right>
        <q-input dense debounce="300" v-model="filter" placeholder="Buscar..." outlined>
          <template #append><q-icon name="search" /></template>
        </q-input>
      </template>

      <template #body-cell-acciones="props">
        <q-td :props="props">
          <template v-if="authStore.isAdmin">
            <q-btn flat round dense icon="edit" color="primary"
                   @click="openDialog(props.row)" class="q-mr-xs" />
            <q-btn flat round dense icon="delete" color="negative"
                   @click="confirmDelete(props.row)" />
          </template>
          <q-badge v-else color="grey-5" label="Solo lectura" />
        </q-td>
      </template>
    </q-table>

    <!-- Dialog Empresa -->
    <q-dialog v-model="dialogOpen" persistent>
      <q-card style="min-width: 450px">
        <q-card-section class="bg-primary text-white">
          <div class="text-h6">{{ isEditing ? 'Editar' : 'Nueva' }} Empresa</div>
        </q-card-section>

        <q-card-section>
          <q-form @submit.prevent="saveEmpresa" class="q-gutter-md">
            <q-input v-model="form.nit" label="NIT *" outlined :disable="isEditing"
                     :rules="[v => !!v || 'Requerido']" />
            <q-input v-model="form.nombre" label="Nombre *" outlined
                     :rules="[v => !!v || 'Requerido']" />
            <q-input v-model="form.direccion" label="Dirección *" outlined
                     :rules="[v => !!v || 'Requerido']" />
            <q-input v-model="form.telefono" label="Teléfono *" outlined
                     :rules="[v => !!v || 'Requerido']" />

            <div class="row justify-end q-gutter-sm">
              <q-btn label="Cancelar" flat v-close-popup />
              <q-btn label="Guardar" type="submit" color="primary" :loading="saving" />
            </div>
          </q-form>
        </q-card-section>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { api } from 'src/boot/axios'
import { useAuthStore } from 'src/stores/auth'

const $q = useQuasar()
const authStore = useAuthStore()

const empresas = ref([])
const loading = ref(false)
const saving = ref(false)
const dialogOpen = ref(false)
const isEditing = ref(false)
const filter = ref('')

const form = reactive({ nit: '', nombre: '', direccion: '', telefono: '' })

const columns = [
  { name: 'nit', label: 'NIT', field: 'nit', align: 'left', sortable: true },
  { name: 'nombre', label: 'Nombre', field: 'nombre', align: 'left', sortable: true },
  { name: 'direccion', label: 'Dirección', field: 'direccion', align: 'left' },
  { name: 'telefono', label: 'Teléfono', field: 'telefono', align: 'center' },
  { name: 'acciones', label: 'Acciones', field: 'acciones', align: 'center' }
]

async function loadEmpresas() {
  loading.value = true
  try {
    const { data } = await api.get('/empresas')
    empresas.value = data.data
  } catch (e) {
    $q.notify({ type: 'negative', message: 'Error cargando empresas' })
  } finally {
    loading.value = false
  }
}

function openDialog(empresa = null) {
  isEditing.value = !!empresa
  Object.assign(form, empresa || { nit: '', nombre: '', direccion: '', telefono: '' })
  dialogOpen.value = true
}

async function saveEmpresa() {
  saving.value = true
  try {
    if (isEditing.value) {
      await api.put(`/empresas/${form.nit}`, form)
      $q.notify({ type: 'positive', message: 'Empresa actualizada' })
    } else {
      await api.post('/empresas', form)
      $q.notify({ type: 'positive', message: 'Empresa creada' })
    }
    dialogOpen.value = false
    await loadEmpresas()
  } catch (e) {
    $q.notify({ type: 'negative', message: e.response?.data?.message || 'Error guardando empresa' })
  } finally {
    saving.value = false
  }
}

function confirmDelete(empresa) {
  $q.dialog({
    title: 'Eliminar empresa',
    message: `¿Eliminar "${empresa.nombre}" (NIT: ${empresa.nit})?`,
    cancel: true, persistent: true, color: 'negative'
  }).onOk(async () => {
    try {
      await api.delete(`/empresas/${empresa.nit}`)
      $q.notify({ type: 'positive', message: 'Empresa eliminada' })
      await loadEmpresas()
    } catch (e) {
      $q.notify({ type: 'negative', message: e.response?.data?.message || 'Error eliminando' })
    }
  })
}

onMounted(loadEmpresas)
</script>
