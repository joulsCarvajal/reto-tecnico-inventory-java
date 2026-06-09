/* eslint-env node */
const { configure } = require('quasar/wrappers')

module.exports = configure(function (/* ctx */) {
  return {
    boot: ['axios', 'pinia'],
    css: ['app.scss'],
    extras: ['material-icons'],

    build: {
      vueRouterMode: 'history',
      env: {
        API_URL: process.env.API_URL || 'http://localhost:8080/api'
      }
    },

    devServer: {
      open: true,
      port: 9000,
      proxy: {
        '/api': {
          target: 'http://localhost:8080',
          changeOrigin: true
        }
      }
    },

    framework: {
      config: {
        notify: { position: 'top-right' }
      },
      plugins: ['Notify', 'Loading', 'Dialog', 'LocalStorage']
    }
  }
})
