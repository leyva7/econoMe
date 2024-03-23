<template>
  <div class="home-details">
    <div class="recuadro">
      <!-- Suponiendo que quieras mostrar algún dato específico de la contabilidad personal -->
      {{ accounting.description }}
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import axios from 'axios';

export default {
  name: "HomeDetails",
  setup() {
    const accounting = ref({}); // Inicializa la variable como un objeto vacío

    // Función para obtener las contabilidades personales
    const fetchAccountings = async () => {
      try {
        const response = await axios.get('http://localhost:8081/api/accounting/accountingPersonal', {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('userToken')}`,
          },
        });
        console.log("Datos recibidos:", response.data); // Verifica los datos recibidos
        accounting.value = response.data; // Asigna los datos recibidos a la variable reactiva
        console.log("Datos asignados a accounting:", accounting.value); // Verifica la asignación
      } catch (error) {
        console.error('Hubo un error al obtener la contabilidad personal:', error);
      }
    };

    // Ejecuta la función fetchAccountings cuando el componente se monta
    onMounted(fetchAccountings);

    // Retorna la variable para que esté disponible en la plantilla
    return {
      accounting,
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
