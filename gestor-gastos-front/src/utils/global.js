import { ref } from "vue";
import router from "@/router";

/**
 * Función que redirige a la página de inicio con el ID de la contabilidad personal almacenado en localStorage.
 */
export const home = () => {
    const personalAccountingId = localStorage.getItem('personalAccountingId');
    router.push({
        name: 'home',
        query: { id: personalAccountingId }
    });
};

/**
 * Función que navega a una ruta específica.
 * @param {string} path - Ruta a la que se desea navegar.
 */
export const navigate = (path) => {
    router.push({
        path: path
    });
};

/**
 * Función que navega a una ruta específica con el ID de la contabilidad personal en los parámetros de la query.
 * @param {string} path - Ruta a la que se desea navegar.
 */
export const navigateHome = (path) => {
    console.log(localStorage.getItem('personalAccountingId'));
    router.push({
        path: path,
        query: { id: localStorage.getItem('personalAccountingId') },
    });
};

/**
 * Función que navega a la contabilidad compartida utilizando el nombre y el ID de la contabilidad.
 * @param {object} accounting - Objeto que contiene el nombre e ID de la contabilidad.
 */
export const navigateToAccounting = (accounting) => {
    router.push({
        name: 'shared',
        params: { accountingName: accounting.name },
        query: { id: accounting.id },
    });
};

/**
 * Función que cierra la sesión borrando los datos de localStorage y redirigiendo a la página de inicio.
 */
export const logout = () => {
    localStorage.clear();
    router.push('/');
};

/**
 * Configuración de las columnas de la tabla de operaciones.
 */
export const tableColumnsOperations = [
    { key: "type", label: "Tipo de operación" },
    { key: "category", label: "Categoría" },
    { key: "quantity", label: "Cantidad" },
    { key: "date", label: "Fecha" }
];

/**
 * Variables reactivas para indicar si hay datos de ingresos y gastos.
 */
export const hasDataIncome = ref(false);
export const hasDataSpent = ref(false);
export const hasData = ref(false);

/**
 * Colores asignados a las categorías de gastos.
 */
export const spentCategoryColors = ['#480707', '#a01414', '#e61c1c', '#ff6a6a', '#ffc7c7', '#a4b0be'];

/**
 * Colores asignados a las categorías de ingresos.
 */
export const incomeCategoryColors = ['#183c27', '#257042', '#3aad66', '#96dfb1', '#e1f7e8', '#a4b0be'];

/**
 * Opciones comunes para la configuración de gráficos.
 */
export const commonOptions = {
    scales: {
        y: {
            ticks: {
                color: 'black',
                font: {
                    size: 16
                }
            }
        },
        x: {
            ticks: {
                color: 'black',
                font: {
                    size: 16
                }
            }
        }
    },
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
        legend: {
            labels: {
                color: 'black',
                font: {
                    size: 16
                }
            }
        },
    }
};

/**
 * Opciones específicas para gráficos de tipo pie.
 */
export const pieOptions = {
    scales: {
        y: {
            display: false,
        },
        x: {
            display: false,
        }
    }
}

/**
 * Función que establece la fecha actual en el objeto de operación.
 * @param {object} operation - Objeto de operación al que se asignará la fecha actual.
 */
export const setToday = (operation) => {
    const today = new Date();
    const yyyy = today.getFullYear();
    let mm = today.getMonth() + 1; // Los meses comienzan desde 0 en JavaScript
    let dd = today.getDate();

    // Agrega un cero delante para los días y meses menores a 10
    mm = mm < 10 ? '0' + mm : mm;
    dd = dd < 10 ? '0' + dd : dd;

    operation.date = `${yyyy}-${mm}-${dd}`;
};

/**
 * Variable reactiva que controla la visibilidad de un elemento.
 */
export let showElement = ref(false);
