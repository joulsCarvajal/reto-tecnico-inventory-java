import { defineStore } from 'pinia'
import { api } from 'src/boot/axios'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || null,
    user: JSON.parse(localStorage.getItem('user') || 'null')
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
    isAdmin: (state) => state.user?.rol === 'ADMINISTRADOR',
    isExterno: (state) => state.user?.rol === 'EXTERNO'
  },

  actions: {
    async login(correo, password) {
      const { data } = await api.post('/auth/login', { correo, password })
      this.token = data.data.token
      this.user = {
        correo: data.data.correo,
        nombre: data.data.nombre,
        rol: data.data.rol
      }
      localStorage.setItem('token', this.token)
      localStorage.setItem('user', JSON.stringify(this.user))
      return data.data
    },

    logout() {
      this.token = null
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }
})
