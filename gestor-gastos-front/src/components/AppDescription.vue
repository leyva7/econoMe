<template>
  <div class="product-description rounded bg-custom-blue-300">
    <h1>econoMe</h1>
    <h2>La solución que estabas buscando</h2>
    <div class="phrases-container">
      <div class="phrase-card">
        <i class="fa-solid fa-wallet"></i>
        <p>Maneja tus finanzas</p>
      </div>
      <div class="phrase-card">
        <i class="fa-solid fa-users"></i>
        <p>Comparte gastos con amigos</p>
      </div>
      <div class="phrase-card">
        <i class="fa-solid fa-chart-line"></i>
        <p>Mejora tu vida y tu situación financiera</p>
      </div>
    </div>
    <router-link to="/register" class="register-link"><button class="cta-button">Comenzar Ahora</button></router-link>
  </div>
</template>


<script>
import router from "@/router";

export default {
  name: 'ProductDescription',
  setup() {

    const navigate = () => {
      router.push({name: 'register'});
    };

    return {
      navigate
    };

  }

};
</script>

<style scoped>
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css');

.product-description {
  text-align: center;
  padding: 2rem;
  height: 60vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.product-description h1 {
  font-size: 3rem;
  margin-bottom: 0.5rem;
  color: #2c3e50;
}

.product-description h2 {
  font-size: 1.75rem;
  margin-bottom: 2rem;
  color: #34495e;
}

.phrases-container {
  display: flex;
  justify-content: center;
  gap: 1rem;
  flex-wrap: wrap;
  margin-bottom: 2rem;
}

.phrase-card {
  background-color: #ecf0f1;
  border: 2px solid #bdc3c7;
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

.phrase-card i {
  font-size: 2rem;
  margin-bottom: 0.5rem;
  color: #2c3e50;
}

.phrase-card p {
  font-size: 1rem;
  color: #2c3e50;
}

.phrase-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.2);
}

.cta-button {
  background-color: var(--pickled-bluewood-600);
  color: white;
  border: none;
  padding: 1rem 2rem;
  font-size: 1rem;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s, transform 0.3s;
}

.cta-button:hover {
  background-color: var(--pickled-bluewood-700);
  transform: scale(1.05);
}
</style>
