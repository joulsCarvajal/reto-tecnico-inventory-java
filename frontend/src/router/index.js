import { route } from 'quasar/wrappers'
import { createRouter, createWebHistory } from 'vue-router'
import routes from './routes'

export default route(function () {
  const Router = createRouter({
    scrollBehavior: () => ({ left: 0, top: 0 }),
    routes,
    history: createWebHistory(process.env.VUE_ROUTER_BASE)
  })

  Router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')
    const user = JSON.parse(localStorage.getItem('user') || 'null')

    if (to.meta.requiresAuth && !token) {
      return next('/login')
    }
    if (to.meta.requiresAdmin && user?.rol !== 'ADMINISTRADOR') {
      return next('/empresas')
    }
    if (to.path === '/login' && token) {
      return next('/empresas')
    }
    next()
  })

  return Router
})
