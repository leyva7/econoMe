module.exports = {
  devServer: {
    proxy: {
      '^/api': {
        target: 'http://localhost:8081', // Asegúrate de que este sea el puerto correcto de tu backend
        changeOrigin: true,
        logLevel: 'debug', // Opcional, para ver los detalles del proxy en la consola
      },
    },
  },
};
