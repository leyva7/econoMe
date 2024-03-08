
<template>
  <div id="app">
    <div class="container">
      <div class="left-panel">
        <DescripcionApp />
      </div>
      <div class="right-panel">
        <InicioSesion />
      </div>
    </div>
  </div>
</template>

<script>
import DescripcionApp from './components/AppDescription.vue';
import InicioSesion from './components/Login.vue';

export default {
  name: 'App',
  components: {
    DescripcionApp,
    InicioSesion,
  },
};
</script>

<style>

body{
  margin:0px;
}

#app {
  display: flex;
  flex-direction: column; /* Ajusta según la estructura de tu aplicación */
  margin:0;
}
.container {
  display: flex; /* Activamos Flexbox */
  height: 100vh; /* El contenedor toma toda la altura de la ventana */
}

.left-panel, .right-panel {
  flex: 1; /* Cada panel toma una cantidad igual del espacio disponible */
  display: flex;
  justify-content: center; /* Centra el contenido horizontalmente */
  align-items: center; /* Centra el contenido verticalmente */
  padding: 20px; /* Añade un poco de padding para no pegar el contenido a los bordes */
  text-align: center;
}

/* Ejemplo de cómo aplicar los colores de fondo usando tu paleta */
.left-panel {
  background-color: var(--blue-chill-500);
}

.right-panel {
  background-color: var(--blue-chill-300);
}

</style>
