<template>
    <div class="product-description">
      <h1>econoMe</h1>
      <h2>La solución que estabas buscando</h2>
      <ul>
        <li>Maneja tus finanzas</li>
        <li>Comparte gastos con amigos</li>
        <li>Mejora tu vida y tu situación financiera</li>
      </ul>
    </div>
  </template>
  
  <script>
  export default {
    name: 'ProductDescription',
  };
  </script>
  
  <style scoped>
  .product-description h1 {
    font-size: 2.5rem;
    margin-bottom: 0.5rem;
  }
  
  .product-description h2 {
    font-size: 1.5rem;
    color: var(--pickled-bluewood-900);
    margin-bottom: 1rem;
  }
  
  .product-description ul {
    list-style: none; /* Elimina los marcadores de lista */
    padding: 0;
    margin-bottom: 2rem;
  }
  
  .product-description ul li {
    margin-bottom: 0.5rem; /* Espacio entre elementos de lista */
    padding-left: 1.5rem; /* Espacio para iconos, si decides añadirlos */
    position: relative; /* Necesario para posicionar iconos */
  }
  
  </style>
  