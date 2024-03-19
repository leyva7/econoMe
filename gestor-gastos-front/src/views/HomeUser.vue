<template>
  <div id="window">
    <!-- Sidebar con navegación y contabilidades compartidas -->
    <aside class="sidebar">
      <div class="logo-section">
        <img src="../assets/img/econome.png" alt="Logo" class="logo">
      </div>
      <div class="divider"></div>
      <nav class="nav">
        <a href="#" @click="navigate('home')"><img src="../assets/icons/home.svg" class="nav-icon"> Home</a>
        <a href="#" @click="navigate('gastos')"><img src="../assets/icons/spent.svg" class="nav-icon"> Gastos</a>
        <a href="#" @click="navigate('ingresos')"><img src="../assets/icons/income.svg" class="nav-icon"> Ingresos</a>
        <a href="#" @click="navigate('evolucion')"><img src="../assets/icons/evolution.svg" class="nav-icon"> Evolución</a>
      </nav>
      <div class="divider"></div>
      <div class="shared-accountings">
        <details>
          <summary>Contabilidades Compartidas</summary>
          <ul>
            <li>Compartida 1</li>
            <li>Compartida 2</li>
            <li>Compartida 3</li>
          </ul>
        </details>
      </div>
    </aside>

    <!-- Área de contenido principal -->
    <main class="content">
      <!-- Barra superior con acciones del usuario -->
      <div class="top-bar">
        <div class="user-actions-container">
          <div class="user-actions">
            <img src="path/to/user/icon.png" alt="User Icon" class="user-icon">
          </div>
          <div class="tooltip">
            <a href="#" @click="modifyUser">Modificar usuario</a>
            <a href="#" @click="modifyPassword">Modificar contraseña</a>
            <a href="#" @click="logout">Cerrar sesión</a>
          </div>
        </div>
      </div>
      <!-- Contenido dinámico aquí -->
      <div class="dynamic-content">
        Aquí va el contenido dinámico de las distintas secciones.
      </div>
    </main>
  </div>
</template>


<script>
import { ref, onMounted } from 'vue';
import router from "@/router";

export default {
  name: 'UserHome',
  setup() {
    const username = ref('');
    const showTooltip = ref(false);

    onMounted(() => {

      username.value = localStorage.getItem('username');
    });

    const navigate = (routeName) => {
      router.push({ name: routeName });
    };

    const logout = () => {
      localStorage.clear(); // Limpia todo el almacenamiento local
      navigate('login');
    };

    const modifyUser = () => {
      navigate('modify-details');
    };

    const modifyPassword = () => {
      navigate('modify-password');
    };

    return {
      username,
      showTooltip,
      logout,
      modifyUser,
      modifyPassword,
    };
  },
};
</script>


<style scoped>
#window {
  display: flex;
  height: 100vh; /* Altura total de la ventana del navegador */
  min-height: 100%;
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
}

.divider {
  border-bottom: 1px solid #FFFFFF;
  margin: 10px 20px;
}

.logo-section {
  display: flex;
  align-items: center;
  justify-content: center;
  //margin-bottom: 0px;
}

.logo {
  max-width: 150px;
}

.nav a, .shared-accountings details summary {
  display: flex;
  align-items: center;
  color: #FFFFFF;
  padding: 5px 0;
  text-decoration: none;
  justify-content: center;
}

.nav, .shared-accountings details summary {
  text-align: center;
}

.nav a {
  display: flex;
  justify-content: center; /* Centra los elementos flexibles */
  align-items: center;
  color: #FFFFFF;
  padding: 5px 0;
  text-decoration: none;
}

.shared-accountings details summary {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
}

.shared-accountings details {
  text-align: center; /* Centra el texto de los elementos desplegables */
}

.logo-section, .divider, .nav{
  margin-bottom: 30px;
}

.shared-accountings{
  margin-top: 10px;
}

.nav-icon {
  margin-right: 8px;
}

.content {
  flex-grow: 1;
  overflow: auto;
  height: 100vh; /* Asegura que el contenido principal también ocupe toda la altura */
}

.top-bar {
  position: relative;
  display: flex;
  justify-content: flex-end;
  padding: 40px;
  background-color: #ECF0F1;
}

.user-actions-container {
  position: relative;
  display: inline-block;
}

.user-actions-container:hover .tooltip {
  display: block;
}

.user-actions {
  position: relative;
  display: flex;
  align-items: center;
}

.user-icon {
  width: 150px;
  height: auto;
  margin-right: 15px;
}

.tooltip {
  display: none;
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  background-color: #FFFFFF;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 100;
  width: auto;
  padding: 10px;
  border-radius: 5px;
}

.tooltip a {
  display: block;
  color: #2C3E50; /* Ajusta el color del texto para una mejor legibilidad */
  text-align: center; /* Centra el texto dentro del tooltip */
  margin: 5px 0; /* Añade un margen entre los enlaces para separarlos */
}

.user-actions:hover .tooltip {
  display: block;
}

.dynamic-content {
  padding: 20px;
}

/*@media (max-width: 768px) {

  #window {
    flex-direction: column;
  }

  .sidebar, .content {
    width: 100%;
    height: auto;
  }

  .sidebar {
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: space-around;
    padding: 10px 0;
  }

  .logo-section {
    flex: 1;
    justify-content: flex-start;
    margin-bottom: 0;
    padding-left: 20px;
  }

  .nav {
    flex: 2;
    justify-content: center;
  }

  .shared-accountings {
    flex: 1;
    padding-right: 20px;
    text-align: right;
  }

  .content {
    order: -1;
  }

  .top-bar {
    justify-content: space-between;
    flex-direction: row-reverse;
    padding: 10px 20px;
  }

  .user-actions .tooltip {
    top: 100%;
    right: auto;
    left: 50%;
    transform: translateX(-50%);
  }


  .nav a, .shared-accountings details summary {
    padding: 5px 10px;
  }

  .dynamic-content {
    padding: 20px;
  }

  .tooltip a {
    display: block;
  }

  .nav-icon {
    width: 24px;
  }
}
*/

</style>

