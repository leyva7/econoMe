import { ref, computed } from 'vue';

/**
 * Hook personalizado para gestionar la paginación de múltiples fuentes de datos.
 * @param {Array} dataSources - Arreglo de objetos que contienen la información de las fuentes de datos.
 * @returns {Array} Arreglo de objetos con el estado y funciones para la paginación de cada fuente de datos.
 */
export function useMultiplePagination(dataSources) {
    // Itera sobre cada fuente de datos en el arreglo dataSources y genera el estado y funciones para cada una.
    const pages = dataSources.map(source => {
        const currentPage = ref(1);  // Página actual, inicializada en 1
        const operationsPerPage = source.reduced ? 4 : 7;  // Número de operaciones por página, dependiendo de si está reducido o no

        // Calcula el número total de páginas basado en la longitud de los datos y el número de operaciones por página.
        const totalPages = computed(() => Math.ceil(source.data.value.length / operationsPerPage));

        // Calcula los datos paginados para la página actual.
        const paginatedData = computed(() => {
            const start = (currentPage.value - 1) * operationsPerPage;  // Índice de inicio de los datos paginados
            const end = start + operationsPerPage;  // Índice de fin de los datos paginados
            return source.data.value.slice(start, end);  // Retorna los datos paginados
        });

        // Función para avanzar a la siguiente página si no estamos en la última página.
        function nextPage() {
            if (currentPage.value < totalPages.value) currentPage.value++;
        }

        // Función para retroceder a la página anterior si no estamos en la primera página.
        function prevPage() {
            if (currentPage.value > 1) currentPage.value--;
        }

        // Retorna un objeto con el estado y las funciones para la paginación de esta fuente de datos.
        return {
            currentPage,    // Ref reactiva que almacena la página actual
            totalPages,     // Cálculo computado del número total de páginas
            paginatedData,  // Cálculo computado de los datos paginados para la página actual
            nextPage,       // Función para avanzar a la siguiente página
            prevPage        // Función para retroceder a la página anterior
        };
    });

    return pages;  // Retorna un arreglo de objetos, uno por cada fuente de datos, cada uno con su propio estado y funciones de paginación.
}
