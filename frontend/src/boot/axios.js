import { boot } from 'quasar/wrappers'
import axios from 'axios'

const api = axios.create({
  baseURL: process.env.API_URL || 'http://localhost:8080/api',
  timeout: 30000
})

export default boot(({ app, router }) => {
  // Interceptor: adjuntar token
  api.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if (token) config.headers.Authorization = `Bearer ${token}`
    return config
  })

  // Interceptor: manejar 401
  api.interceptors.response.use(
    res => res,
    err => {
      if (err.response?.status === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        router.push('/login')
      }
      return Promise.reject(err)
    }
  )

  app.config.globalProperties.$axios = axios
  app.config.globalProperties.$api = api
})

export { api }
