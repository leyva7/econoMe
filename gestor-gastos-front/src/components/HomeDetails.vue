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
  padding: 8px; /* Ajusta el tamaño del padding según necesites */
  background-color: #f0f0f0;
}

.recuadro {
  margin: 10px 0; /* Ajusta el margen superior e inferior */
  padding: 15px; /* Ajusta el padding interno del recuadro */
  width: calc(100% - 30px); /* Ajusta el ancho para tener en cuenta el padding y el ancho del borde */
  box-sizing: border-box; /* Asegura que el padding y el borde no aumenten el ancho del recuadro */
  background-color: #ffffff; /* Cambia el color de fondo a blanco para que se note sobre el fondo gris */
  border-radius: 20px; /* Bordes redondeados del recuadro */
  border: 2px solid #2C3E50; /* Color y ancho del borde */
}
</style>
