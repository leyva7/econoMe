<template>
  <div class="text-center">
    <h2>{{ message }}</h2>
  </div>
</template>

<script>
export default {
  name: "NoDataMessage",
  props: {
    message: {
      type: String,
      default: 'No hay datos disponibles'
    }
  }
}
</script>
