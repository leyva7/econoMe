<template>
  <div id="home-user">
    <!-- Top Bar -->
    <div class="top-bar">
      <div class="logo">Logo</div>
      <div class="accounting-name">Contabilidad: General</div>
      <div class="user-info">Bienvenido, Usuario | <a href="#">Modificar Usuario</a></div>
    </div>



    <div class="main-container">
      <!-- Sidebar -->
      <aside class="sidebar">
        <!-- Principal Accounting Block -->
        <div class="accounting-block principal-accounting">
          <h3>Contabilidad Principal</h3>
        </div>

        <!-- Shared Accountings Block -->
        <div class="accounting-block shared-accountings">
          <h3>Contabilidades Compartidas</h3>
          <ul>
            <li>Compartida 1</li>
            <li>Compartida 2</li>
            <li>Compartida 3</li>
          </ul>
        </div>
      </aside>

      <!-- Content Section -->
      <div class="content">
        <!-- Navigation -->
        <nav class="navigation">
          <a href="#">Inicio</a>
          <a href="#">Estadísticas</a>
          <a href="#">Ajustes</a>
        </nav>

        <!-- Dynamic Content Container -->
        <div class="content-container">
          Aquí va el contenido dinámico.
        </div>
      </div>
    </div>
  </div>
</template>



<script setup>
// Vue Composition API logic here
</script>

<style scoped>
body, html {
  margin: 0;
  padding: 0;
  font-family: Arial, sans-serif;
  height: 100%;
}

#home-user {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background-color: #f5f5f5;
  position: relative;
}

.accounting-name {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  text-align: center;
}

.logo {
  margin-right: auto; /* Empuja todo lo demás a la derecha */
}

.user-info {
  margin-left: auto; /* Se mantiene a la derecha */
}


.main-container {
  display: flex;
  height: calc(100vh - 40px); /* Adjust based on top bar height */
}

.sidebar {
  width: 25%;
  background-color: #e0e0e0;
  padding: 20px;
  box-sizing: border-box;
}

.navigation {
  display: flex;
  justify-content: space-around;
  padding: 10px;
  background-color: #ddd;
}

.content {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.content-container {
  flex-grow: 1;
  padding: 20px;
}

a {
  text-decoration: none;
  color: black;
}

ul {
  list-style: none;
  padding: 0;
}

li {
  margin-bottom: 10px;
}

.accounting-block {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f0f0f0;
  border-radius: 5px;
}

.principal-accounting {
  background-color: #d9edf7; /* Light blue to highlight principal accounting */
}

.shared-accountings {
  background-color: #dff0d8; /* Light green for shared accountings */
}

h3 {
  margin-top: 0;
}
</style>
