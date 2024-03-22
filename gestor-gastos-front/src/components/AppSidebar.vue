<template>
  <aside class="sidebar">
    <div class="logo-section">
      <img src="../assets/img/econome.png" alt="Logo" class="logo">
    </div>
    <div class="divider"></div>
    <nav class="nav">
      <a href="#" @click="navigate('/home-user')"><img src="../assets/icons/home.svg" alt="Home" class="nav-icon"> Home</a>
      <a href="#" @click="navigate('/home-user/spent')"><img src="../assets/icons/spent.svg" alt="Gastos" class="nav-icon"> Gastos</a>
      <a href="#" @click="navigate('/home-user/income')"><img src="../assets/icons/income.svg" alt="Ingresos" class="nav-icon"> Ingresos</a>
      <a href="#" @click="navigate('/home-user/evolution')"><img src="../assets/icons/evolution.svg" alt="Evolucion" class="nav-icon"> Evolución</a>
    </nav>
    <div class="divider"></div>
    <div class="shared-accountings">
      <span>Contabilidades Compartidas</span>
      <button @click="$emit('openModal', 'addAccounting')">Añadir Contabilidad</button>
      <ul>
        <a href="#" v-for="(accounting, index) in sharedAccountings" :key="index" @click.prevent="navigateToAccounting(accounting)">
          <li class="shared-accountings-list">{{ accounting.name }}</li>
        </a>
      </ul>
    </div>
  </aside>
</template>

<script>
export default {
  name: 'AppSidebar',
  props: ['sharedAccountings'],
  emits: ['openModal'],
  methods: {
    navigate(path) {
      this.$router.push({
        path: path, // Utiliza 'path' en lugar de 'name'
        query: { id: localStorage.getItem('personalAccountingId') }
      });
    },
    navigateToAccounting(accounting) {
      this.$router.push({
        name: 'shared',
        params: { accountingName: accounting.name },
        query: { id: accounting.id }
      });
    }
  }
}
</script>

<style>
.sidebar, .content {
  overflow-y: auto; /* Permite desplazamiento interno si es necesario */
}

.sidebar {
  width: 15%;
  background-color: #2C3E50;
  color: #FFFFFF;
  padding: 20px;
  display: flex;
  flex-direction: column;
  height: 100vh;
  text-align: center;
  box-sizing: border-box;
  overflow-y: auto;
}

.divider{
  border-bottom: 1px solid #FFFFFF;
}

.divider, .logo-section, .nav, .shared-accountings {
  margin-bottom: 30px;
}

.logo-section, .nav a, .shared-accountings details summary, .shared-accountings details {
  display: flex;
  justify-content: center;
  align-items: center;
  text-decoration: none;
}

.logo {
  max-width: 150px;
  margin-right: 10px;
}

.nav-icon, .tooltip a {
  margin-right: 8px;
  text-decoration: none;
}

.nav a, .shared-accountings details summary {
  color: #FFFFFF;
  padding: 5px 0;
  text-decoration: none;
  font-size: 20px;
}

.shared-accountings details {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.shared-accountings ul {
  list-style-type: none; /* Quita los puntos */
  padding: 0;
  margin: 20px 0; /* Agrega un poco de margen arriba y abajo de la lista */
}

.shared-accountings a {
  color: #FFFFFF; /* Establece el color del texto de los enlaces a blanco */
  text-decoration: none; /* Remueve el subrayado de los enlaces */
}

.shared-accountings li {
  margin-bottom: 10px; /* Aumenta la separación */
  border: 2px solid #FFFFFF; /* Borde blanco alrededor de la lista */
}
.shared-accountings a:visited {
  color: #FFFFFF; /* Mantiene el color blanco incluso después de ser visitados */
}

.add-icon {
  cursor: pointer;
  margin-left: 10px;
}

</style>