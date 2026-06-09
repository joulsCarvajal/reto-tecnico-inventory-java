const routes = [
  {
    path: '/login',
    component: () => import('src/pages/LoginPage.vue')
  },
  {
    path: '/',
    component: () => import('src/layouts/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '', redirect: '/empresas' },
      {
        path: 'empresas',
        component: () => import('src/pages/EmpresasPage.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'productos',
        component: () => import('src/pages/ProductosPage.vue'),
        meta: { requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'inventario',
        component: () => import('src/pages/InventarioPage.vue'),
        meta: { requiresAuth: true, requiresAdmin: true }
      }
    ]
  },
  {
    path: '/:catchAll(.*)*',
    redirect: '/'
  }
]

export default routes
