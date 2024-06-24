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
  configureWebpack: {
    plugins: [
      new webpack.DefinePlugin({
        '__VUE_OPTIONS_API__': JSON.stringify(true),
        '__VUE_PROD_DEVTOOLS__': JSON.stringify(false),
        '__VUE_PROD_HYDRATION_MISMATCH_DETAILS__': JSON.stringify(false)
      })
    ]
  }
};
