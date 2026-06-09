<template>
  <q-page padding>
    <div class="row items-center q-mb-md">
      <div class="col">
        <div class="text-h5 text-primary text-weight-bold">
          <q-icon name="inventory" class="q-mr-sm" />Inventario
        </div>
        <div class="text-caption text-grey-6">Gestión de inventario por empresa</div>
      </div>
    </div>

    <!-- Selector de empresa -->
    <q-card flat bordered class="q-mb-md">
      <q-card-section>
        <div class="row q-gutter-md items-end">
          <q-select
            class="col-md-4"
            v-model="selectedEmpresa"
            label="Seleccionar Empresa"
            :options="empresaOptions"
            emit-value map-options outlined
            @update:model-value="loadInventario"
          />
          <template v-if="selectedEmpresa">
            <q-btn color="primary" icon="download" label="Descargar PDF"
                   @click="descargarPdf" :loading="downloadingPdf" />
            <q-btn color="teal" icon="email" label="Enviar por Correo"
                   @click="emailDialogOpen = true" />
            <q-btn color="secondary" icon="add" label="Agregar Producto"
                   @click="openAgregarDialog" />
          </template>
        </div>
      </q-card-section>
    </q-card>

    <!-- Tabla de inventario -->
    <q-table
      v-if="selectedEmpresa"
      :rows="inventario"
      :columns="columns"
      row-key="id"
      :loading="loading"
      flat bordered
    >
      <template #body-cell-cantidad="props">
        <q-td :props="props">
          <q-badge :color="props.row.cantidad > 10 ? 'positive' : props.row.cantidad > 0 ? 'warning' : 'negative'"
                   :label="props.row.cantidad" />
        </q-td>
      </template>

      <template #body-cell-precios="props">
        <q-td :props="props">
          <div v-for="p in props.row.precios" :key="p.moneda" class="text-caption">
            <strong>{{ p.moneda }}:</strong> {{ formatNumber(p.precio) }}
          </div>
        </q-td>
      </template>

      <template #body-cell-acciones="props">
        <q-td :props="props">
          <q-btn flat round dense icon="edit" color="primary"
                 @click="openEditarCantidadDialog(props.row)" class="q-mr-xs" />
          <q-btn flat round dense icon="delete" color="negative"
                 @click="confirmDelete(props.row)" />
        </q-td>
      </template>

      <template #bottom-row>
        <q-tr class="bg-grey-2">
          <q-td colspan="2" class="text-weight-bold">Total registros: {{ inventario.length }}</q-td>
          <q-td colspan="5"></q-td>
        </q-tr>
      </template>
    </q-table>

    <div v-else class="flex flex-center" style="height: 200px">
      <div class="text-center text-grey-5">
        <q-icon name="inventory_2" size="60px" />
        <div class="text-h6 q-mt-md">Selecciona una empresa para ver su inventario</div>
      </div>
    </div>

    <!-- Dialog: Agregar/Editar cantidad -->
    <q-dialog v-model="inventarioDialogOpen" persistent>
      <q-card style="min-width: 400px">
        <q-card-section class="bg-primary text-white">
          <div class="text-h6">{{ editandoItem ? 'Editar Cantidad' : 'Agregar al Inventario' }}</div>
        </q-card-section>
        <q-card-section class="q-gutter-md">
          <q-select v-if="!editandoItem"
            v-model="inventarioForm.productoId"
            label="Producto *" outlined
            :options="productoOptions" emit-value map-options
            :rules="[v => !!v || 'Requerido']"
          />
          <div v-else class="text-subtitle2">{{ editandoItem.productoNombre }}</div>
          <q-input v-model.number="inventarioForm.cantidad" label="Cantidad *"
                   outlined type="number" min="0"
                   :rules="[v => v !== null || 'Requerido', v => v >= 0 || 'Debe ser >= 0']" />
          <div class="row justify-end q-gutter-sm">
            <q-btn label="Cancelar" flat v-close-popup />
            <q-btn label="Guardar" color="primary" :loading="saving" @click="saveInventario" />
          </div>
        </q-card-section>
      </q-card>
    </q-dialog>

    <!-- Dialog: Enviar por correo -->
    <q-dialog v-model="emailDialogOpen" persistent>
      <q-card style="min-width: 400px">
        <q-card-section class="bg-teal text-white">
          <div class="text-h6">Enviar Inventario por Correo</div>
        </q-card-section>
        <q-card-section class="q-gutter-md">
          <q-input v-model="emailDestinatario" label="Correo destinatario *"
                   outlined type="email"
                   :rules="[v => !!v || 'Requerido', v => /.+@.+\..+/.test(v) || 'Correo inválido']" />
          <div class="row justify-end q-gutter-sm">
            <q-btn label="Cancelar" flat v-close-popup />
            <q-btn label="Enviar" color="teal" :loading="sendingEmail" @click="enviarCorreo" />
          </div>
        </q-card-section>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { api } from 'src/boot/axios'

const $q = useQuasar()

const inventario = ref([])
const loading = ref(false)
const saving = ref(false)
const downloadingPdf = ref(false)
const sendingEmail = ref(false)
const selectedEmpresa = ref(null)
const empresaOptions = ref([])
const productoOptions = ref([])
const inventarioDialogOpen = ref(false)
const emailDialogOpen = ref(false)
const editandoItem = ref(null)
const emailDestinatario = ref('')
const inventarioForm = reactive({ productoId: null, cantidad: 0 })

const columns = [
  { name: 'productoCodigo', label: 'Código', field: 'productoCodigo', align: 'left', sortable: true },
  { name: 'productoNombre', label: 'Producto', field: 'productoNombre', align: 'left', sortable: true },
  { name: 'cantidad', label: 'Cantidad', field: 'cantidad', align: 'center', sortable: true },
  { name: 'productoCaracteristicas', label: 'Características', field: 'productoCaracteristicas', align: 'left' },
  { name: 'precios', label: 'Precios', field: 'precios', align: 'left' },
  { name: 'acciones', label: 'Acciones', field: 'acciones', align: 'center' }
]

function formatNumber(n) { return new Intl.NumberFormat('es-CO').format(n) }

async function loadInventario() {
  if (!selectedEmpresa.value) return
  loading.value = true
  try {
    const [inv, prods] = await Promise.all([
      api.get('/inventario', { params: { empresaNit: selectedEmpresa.value } }),
      api.get('/productos', { params: { empresaNit: selectedEmpresa.value } })
    ])
    inventario.value = inv.data.data
    productoOptions.value = prods.data.data.map(p => ({ label: `${p.codigo} - ${p.nombre}`, value: p.id }))
  } catch (e) {
    $q.notify({ type: 'negative', message: 'Error cargando inventario' })
  } finally {
    loading.value = false
  }
}

function openAgregarDialog() {
  editandoItem.value = null
  inventarioForm.productoId = null
  inventarioForm.cantidad = 0
  inventarioDialogOpen.value = true
}

function openEditarCantidadDialog(item) {
  editandoItem.value = item
  inventarioForm.productoId = item.productoId
  inventarioForm.cantidad = item.cantidad
  inventarioDialogOpen.value = true
}

async function saveInventario() {
  saving.value = true
  try {
    await api.post('/inventario', {
      empresaNit: selectedEmpresa.value,
      productoId: inventarioForm.productoId,
      cantidad: inventarioForm.cantidad
    })
    $q.notify({ type: 'positive', message: 'Inventario actualizado' })
    inventarioDialogOpen.value = false
    await loadInventario()
  } catch (e) {
    $q.notify({ type: 'negative', message: e.response?.data?.message || 'Error guardando' })
  } finally {
    saving.value = false
  }
}

function confirmDelete(item) {
  $q.dialog({
    title: 'Eliminar del inventario',
    message: `¿Eliminar "${item.productoNombre}" del inventario?`,
    cancel: true, persistent: true, color: 'negative'
  }).onOk(async () => {
    try {
      await api.delete(`/inventario/${item.id}`)
      $q.notify({ type: 'positive', message: 'Eliminado del inventario' })
      await loadInventario()
    } catch (e) {
      $q.notify({ type: 'negative', message: 'Error eliminando' })
    }
  })
}

async function descargarPdf() {
  downloadingPdf.value = true
  try {
    const { data } = await api.get('/inventario/pdf',
      { params: { empresaNit: selectedEmpresa.value }, responseType: 'blob' })
    const url = URL.createObjectURL(new Blob([data], { type: 'application/pdf' }))
    const link = document.createElement('a')
    link.href = url
    link.download = `inventario_${selectedEmpresa.value}.pdf`
    link.click()
    URL.revokeObjectURL(url)
  } catch (e) {
    $q.notify({ type: 'negative', message: 'Error generando PDF' })
  } finally {
    downloadingPdf.value = false
  }
}

async function enviarCorreo() {
  sendingEmail.value = true
  try {
    await api.post('/inventario/enviar-correo', {
      empresaNit: selectedEmpresa.value,
      destinatario: emailDestinatario.value
    })
    $q.notify({ type: 'positive', message: `PDF enviado a ${emailDestinatario.value}` })
    emailDialogOpen.value = false
    emailDestinatario.value = ''
  } catch (e) {
    $q.notify({ type: 'negative', message: e.response?.data?.message || 'Error enviando correo' })
  } finally {
    sendingEmail.value = false
  }
}

onMounted(async () => {
  const { data } = await api.get('/empresas')
  empresaOptions.value = data.data.map(e => ({ label: e.nombre, value: e.nit }))
})
</script>
