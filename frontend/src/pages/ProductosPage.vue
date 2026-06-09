<template>
  <q-page padding>
    <div class="row items-center q-mb-md">
      <div class="col">
        <div class="text-h5 text-primary text-weight-bold">
          <q-icon name="category" class="q-mr-sm" />Productos
        </div>
        <div class="text-caption text-grey-6">Registra y gestiona productos por empresa</div>
      </div>
      <q-btn color="primary" icon="add" label="Nuevo Producto" @click="openDialog()" />
    </div>

    <q-table
      :rows="productos"
      :columns="columns"
      row-key="id"
      :loading="loading"
      flat bordered
      :filter="filter"
    >
      <template #top-right>
        <q-input dense debounce="300" v-model="filter" placeholder="Buscar..." outlined>
          <template #append><q-icon name="search" /></template>
        </q-input>
      </template>

      <template #body-cell-precios="props">
        <q-td :props="props">
          <q-badge v-for="p in props.row.precios" :key="p.moneda"
                   color="teal" class="q-mr-xs">
            {{ p.moneda }}: {{ formatNumber(p.precio) }}
          </q-badge>
        </q-td>
      </template>

      <template #body-cell-categorias="props">
        <q-td :props="props">
          <q-chip v-for="c in props.row.categorias" :key="c.id" dense color="purple-2" size="sm">
            {{ c.nombre }}
          </q-chip>
        </q-td>
      </template>

      <template #body-cell-acciones="props">
        <q-td :props="props">
          <q-btn flat round dense icon="edit" color="primary"
                 @click="openDialog(props.row)" class="q-mr-xs" />
          <q-btn flat round dense icon="delete" color="negative"
                 @click="confirmDelete(props.row)" />
        </q-td>
      </template>
    </q-table>

    <!-- Dialog Producto -->
    <q-dialog v-model="dialogOpen" persistent>
      <q-card style="min-width: 550px; max-width: 700px">
        <q-card-section class="bg-primary text-white">
          <div class="text-h6">{{ isEditing ? 'Editar' : 'Nuevo' }} Producto</div>
        </q-card-section>

        <q-card-section class="scroll" style="max-height: 70vh">
          <q-form @submit.prevent="saveProducto" class="q-gutter-md">
            <div class="row q-gutter-md">
              <q-input class="col" v-model="form.codigo" label="Código *" outlined
                       :rules="[v => !!v || 'Requerido']" />
              <q-input class="col" v-model="form.nombre" label="Nombre *" outlined
                       :rules="[v => !!v || 'Requerido']" />
            </div>

            <q-input v-model="form.caracteristicas" label="Características" outlined
                     type="textarea" rows="3" />

            <q-select v-model="form.empresaNit" label="Empresa *" outlined
                      :options="empresaOptions" emit-value map-options
                      :rules="[v => !!v || 'Requerido']" />

            <q-select v-model="form.categoriaIds" label="Categorías" outlined
                      :options="categoriaOptions" emit-value map-options multiple use-chips />

            <div class="text-subtitle2 text-grey-7">Precios por moneda</div>
            <div class="row q-gutter-sm">
              <q-input v-for="moneda in monedas" :key="moneda"
                       v-model.number="form.precios[moneda]"
                       :label="`Precio ${moneda}`" outlined type="number" class="col"
                       :prefix="getCurrencySymbol(moneda)" />
            </div>

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

const $q = useQuasar()

const productos = ref([])
const loading = ref(false)
const saving = ref(false)
const dialogOpen = ref(false)
const isEditing = ref(false)
const filter = ref('')
const empresaOptions = ref([])
const categoriaOptions = ref([])
const monedas = ['COP', 'USD', 'EUR']

const defaultForm = () => ({
  id: null, codigo: '', nombre: '', caracteristicas: '',
  empresaNit: '', categoriaIds: [],
  precios: { COP: null, USD: null, EUR: null }
})
const form = reactive(defaultForm())

const columns = [
  { name: 'codigo', label: 'Código', field: 'codigo', align: 'left', sortable: true },
  { name: 'nombre', label: 'Nombre', field: 'nombre', align: 'left', sortable: true },
  { name: 'empresaNombre', label: 'Empresa', field: 'empresaNombre', align: 'left' },
  { name: 'precios', label: 'Precios', field: 'precios', align: 'left' },
  { name: 'categorias', label: 'Categorías', field: 'categorias', align: 'left' },
  { name: 'acciones', label: 'Acciones', field: 'acciones', align: 'center' }
]

function formatNumber(n) { return new Intl.NumberFormat('es-CO').format(n) }
function getCurrencySymbol(m) { return { COP: '$', USD: 'US$', EUR: '€' }[m] || '' }

async function loadAll() {
  loading.value = true
  try {
    const [prods, emps, cats] = await Promise.all([
      api.get('/productos'), api.get('/empresas'), api.get('/categorias')
    ])
    productos.value = prods.data.data
    empresaOptions.value = emps.data.data.map(e => ({ label: e.nombre, value: e.nit }))
    categoriaOptions.value = cats.data.data.map(c => ({ label: c.nombre, value: c.id }))
  } catch (e) {
    $q.notify({ type: 'negative', message: 'Error cargando datos' })
  } finally {
    loading.value = false
  }
}

function openDialog(producto = null) {
  isEditing.value = !!producto
  Object.assign(form, defaultForm())
  if (producto) {
    form.id = producto.id
    form.codigo = producto.codigo
    form.nombre = producto.nombre
    form.caracteristicas = producto.caracteristicas
    form.empresaNit = producto.empresaNit
    form.categoriaIds = producto.categorias?.map(c => c.id) || []
    producto.precios?.forEach(p => { form.precios[p.moneda] = Number(p.precio) })
  }
  dialogOpen.value = true
}

async function saveProducto() {
  saving.value = true
  const payload = {
    codigo: form.codigo, nombre: form.nombre,
    caracteristicas: form.caracteristicas, empresaNit: form.empresaNit,
    categoriaIds: form.categoriaIds,
    precios: Object.fromEntries(Object.entries(form.precios).filter(([, v]) => v != null && v > 0))
  }
  try {
    if (isEditing.value) {
      await api.put(`/productos/${form.id}`, payload)
      $q.notify({ type: 'positive', message: 'Producto actualizado' })
    } else {
      await api.post('/productos', payload)
      $q.notify({ type: 'positive', message: 'Producto creado' })
    }
    dialogOpen.value = false
    await loadAll()
  } catch (e) {
    $q.notify({ type: 'negative', message: e.response?.data?.message || 'Error guardando' })
  } finally {
    saving.value = false
  }
}

function confirmDelete(producto) {
  $q.dialog({
    title: 'Eliminar producto',
    message: `¿Eliminar "${producto.nombre}"?`,
    cancel: true, persistent: true, color: 'negative'
  }).onOk(async () => {
    try {
      await api.delete(`/productos/${producto.id}`)
      $q.notify({ type: 'positive', message: 'Producto eliminado' })
      await loadAll()
    } catch (e) {
      $q.notify({ type: 'negative', message: e.response?.data?.message || 'Error eliminando' })
    }
  })
}

onMounted(loadAll)
</script>
