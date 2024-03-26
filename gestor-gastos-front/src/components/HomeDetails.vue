<template>
  <div class="home-details">
    <div class="recuadro">
      <!-- Suponiendo que quieras mostrar algún dato específico de la contabilidad personal -->
      <p>{{ accountingPersonal.description }}</p>

    </div>
  </div>
</template>

<script>
import { onMounted } from 'vue';
import { useAccountingStore } from '@/stores/accountingStore';


export default {
  name: "HomeDetails",
  setup() {
    const { accountingPersonal, fetchAccountingPersonalAsync, fetchOperationsAsync , accountingId } = useAccountingStore();

    onMounted(fetchAccountingPersonalAsync);

    onMounted(async () => {
      await fetchOperationsAsync(accountingId.value);
    });

    return {
      accountingPersonal, accountingId
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

.operation {
  margin-bottom: 10px;
  padding: 10px;
  background-color: #eaeaea;
  border-radius: 10px;
}

.operation p {
  margin: 5px 0;
}
</style>
