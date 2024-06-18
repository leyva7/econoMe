<template>
  <div class="text-center">
    <h2>{{ message }}</h2>
  </div>
</template>

<script>
export default {
  name: "NoDataMessage", // Nombre del componente
  props: {
    message: {
      type: String, // Tipo de la propiedad es String
      default: 'No hay datos disponibles' // Valor por defecto si no se proporciona ninguna prop message
    }
  }
}
</script>

