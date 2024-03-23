<template>
  <div class="home-details">
    <div class="recuadro">
      <!-- Suponiendo que quieras mostrar algún dato específico de la contabilidad personal -->
      {{ accountingPersonal.description }}
    </div>
  </div>
</template>

<script>
import { onMounted } from 'vue';
import { useAccountingStore } from '@/stores/accountingStore'; // Asegúrate de ajustar la ruta al archivo accountingStore


export default {
  name: "HomeDetails",
  setup() {
    const { accountingPersonal, fetchAccountingPersonal } = useAccountingStore();

    onMounted(fetchAccountingPersonal);

    return {
      accountingPersonal,
    };
  },
};
</script>

<style scoped>
.home-details {
  padding: 8px;
  background-color: #f0f0f0;
}

.recuadro {
  margin: 10px 0;
  padding: 15px;
  box-sizing: border-box;
  background-color: #ffffff;
  border-radius: 20px;
  border: 2px solid #2C3E50;
}
</style>
