// Importa la función createApp desde Vue para inicializar una aplicación Vue
import { createApp } from 'vue';

// Importa el componente raíz de la aplicación (App.vue)
import App from './App.vue';

// Importa el enrutador configurado para la aplicación Vue
import router from './router';

// Importa el plugin de notificaciones Toast para Vue
import ToastPlugin from 'vue-toast-notification';

// Importa el archivo de estilos del tema Bootstrap para las notificaciones Toast
import 'vue-toast-notification/dist/theme-bootstrap.css';

// Importa los estilos globales para toda la aplicación
import './assets/styles/global.css';

// Crea una nueva instancia de la aplicación Vue con el componente raíz (App.vue)
const app = createApp(App);

// Usa Vue Router para manejar la navegación
app.use(router);

// Usa el plugin de notificaciones Toast en la aplicación Vue
app.use(ToastPlugin);

// Monta la aplicación en el elemento con id 'app' en el DOM
app.mount('#app');

