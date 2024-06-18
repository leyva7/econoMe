<template>
  <div class="product-description rounded bg-custom-blue-300">
    <!-- Título y subtítulo de la descripción del producto -->
    <h1>econoMe</h1>
    <h2>La solución que estabas buscando</h2>

    <!-- Contenedor de las frases destacadas -->
    <div class="phrases-container">
      <!-- Primera frase destacada con ícono -->
      <div class="phrase-card">
        <i class="fa-solid fa-wallet"></i>
        <p>Maneja tus finanzas</p>
      </div>
      <!-- Segunda frase destacada con ícono -->
      <div class="phrase-card">
        <i class="fa-solid fa-users"></i>
        <p>Comparte gastos con amigos</p>
      </div>
      <!-- Tercera frase destacada con ícono -->
      <div class="phrase-card">
        <i class="fa-solid fa-chart-line"></i>
        <p>Mejora tu vida y tu situación financiera</p>
      </div>
    </div>

    <!-- Enlace al formulario de registro con botón de llamada a la acción -->
    <router-link to="/register" class="register-link">
      <button class="cta-button">Comenzar Ahora</button>
    </router-link>
  </div>
</template>

<script>
// Importa el router de Vue, aunque no se utiliza en este componente específico
export default {
  name: 'ProductDescription'
};
</script>

<style scoped>
/* Importación de la librería de iconos FontAwesome */
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css');

/* Variables CSS para mantener consistencia en colores */
:root {
  --main-text-color: #2c3e50;
  --secondary-text-color: #34495e;
  --card-background: #ecf0f1;
  --card-border: #bdc3c7;
  --button-bg-color: var(--pickled-bluewood-600);
  --button-hover-bg-color: var(--pickled-bluewood-700);
}

/* Estilos del contenedor principal */
.product-description {
  text-align: center;
  padding: 2rem;
  height: 60vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

/* Estilos para el título principal */
.product-description h1 {
  font-size: 3rem;
  margin-bottom: 0.5rem;
  color: var(--main-text-color);
}

/* Estilos para el subtítulo */
.product-description h2 {
  font-size: 1.75rem;
  margin-bottom: 2rem;
  color: var(--secondary-text-color);
}

/* Estilos del contenedor de frases */
.phrases-container {
  display: flex;
  justify-content: center;
  gap: 1rem;
  flex-wrap: wrap;
  margin-bottom: 2rem;
}

/* Estilos de las tarjetas de frases destacadas */
.phrase-card {
  background-color: var(--card-background);
  border: 2px solid var(--card-border);
  border-radius: 10px;
  padding: 1rem;
  width: 200px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

/* Estilos para los íconos en las tarjetas */
.phrase-card i {
  font-size: 2rem;
  margin-bottom: 0.5rem;
  color: var(--main-text-color);
}

/* Estilos para el texto en las tarjetas */
.phrase-card p {
  font-size: 1rem;
  color: var(--main-text-color);
}

/* Efecto hover para las tarjetas */
.phrase-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.2);
}

/* Estilos del botón de llamada a la acción */
.cta-button {
  background-color: var(--button-bg-color);
  color: white;
  border: none;
  padding: 1rem 2rem;
  font-size: 1rem;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s, transform 0.3s;
}

/* Efecto hover para el botón */
.cta-button:hover {
  background-color: var(--button-hover-bg-color);
  transform: scale(1.05);
}
</style>
