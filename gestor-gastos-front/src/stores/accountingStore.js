import { computed, ref, watch } from 'vue';
import router from "@/router";
import {
    fetchAccountingPersonal, fetchAccountings, fetchCategoriesDifferences,
    fetchCategoriesIncome, fetchCategoriesSpent
} from '@/api/accountingAPI';
import { fetchUserRole, fetchUsersAccounting } from '@/api/userRoleAPI';
import {
    fetchAllAccountingsUserOperations, fetchIncomesFiltered, fetchOperations,
    fetchSpentsFiltered
} from '@/api/operationAPI';

import { proccessCategories, processFinancialData, processLinearGraphData } from '@/utils/functions';

// Definición del hook useAccountingStore
export const useAccountingStore = () => {
    // Referencias reactivas para almacenar los datos
    const accountings = ref([]); // Lista de contabilidades
    const userRole = ref([]); // Rol del usuario
    const categories = ref([]); // Categorías (gastos e ingresos combinados)
    const username = ref(localStorage.getItem('username') || ''); // Nombre de usuario almacenado localmente
    const accountingPersonal = ref({}); // Detalles de la contabilidad personal

    const accountingName = ref(''); // Nombre de la contabilidad actual
    const accountingId = computed(() => {
        return router.currentRoute.value.query.id; // ID de la contabilidad actual basado en la ruta actual
    });

    // Listas para almacenar operaciones y datos financieros
    const operations = ref([]); // Todas las operaciones
    const allOperations = ref([]); // Todas las operaciones del usuario
    const allAccountingUserOperations = ref([]); // Todas las operaciones de la contabilidad del usuario
    const spentsFiltered = ref([]); // Gastos filtrados
    const dailySpentData = ref([]); // Datos diarios de gastos
    const incomesFiltered = ref([]); // Ingresos filtrados
    const categoriesDifferences = ref({ // Diferencias de categorías entre gastos e ingresos
        spentDifferences: {},
        incomeDifferences: {}
    });
    const usersAccounting = ref([]); // Usuarios asociados a la contabilidad

    // Función para manejar la obtención de datos de forma genérica
    async function fetchData(endpointFunction, params = undefined, targetRef = undefined, errorHandler = null) {
        try {
            const response = await endpointFunction(params); // Llama a la función de la API con parámetros opcionales
            if (targetRef) {
                targetRef.value = response.data; // Asigna los datos de respuesta al targetRef si está definido
            }
            return response.data; // Devuelve los datos obtenidos
        } catch (error) {
            console.error(errorHandler || `Hubo un error al obtener los datos de: ${endpointFunction.name}`, error);
            if (targetRef) {
                targetRef.value = []; // Limpia el targetRef en caso de error
            }
        }
    }

    // Funciones para cargar datos específicos usando fetchData
    const loadAccountings = async () => await fetchData(fetchAccountings, undefined, accountings, 'Hubo un error al obtener las contabilidades');

    const fetchAccountingPersonalAsync = async () => await fetchData(fetchAccountingPersonal, undefined, accountingPersonal, 'Hubo un error al obtener la contabilidad personal');

    const fetchUserRoleAsync = async (accountingId) => {
        const role = await fetchData(fetchUserRole, accountingId, userRole, 'Error al obtener el rol del usuario');
        if (role) {
            userRole.value = role.role; // Asigna el rol obtenido al userRole si existe
        }
    };

    const fetchUsersAccountingAsync = async (accountingId) => await fetchData(fetchUsersAccounting, accountingId, usersAccounting, 'Hubo un error al obtener los usuarios de la contabilidad');

    const fetchCategoriesSpentAsync = async (accountingId) => await fetchData(fetchCategoriesSpent, accountingId, categories, 'Hubo un error al obtener las categorías de gastos');

    const fetchCategoriesIncomeAsync = async (accountingId) => await fetchData(fetchCategoriesIncome, accountingId, categories, 'Hubo un error al obtener las categorías de ingresos');

    // Función para combinar y obtener todas las categorías (gastos e ingresos)
    const fetchCategoriesAsync = async (accountingId) => {
        try {
            const spentResponse = await fetchData(() => fetchCategoriesSpent(accountingId)); // Obtener categorías de gastos
            const incomeResponse = await fetchData(() => fetchCategoriesIncome(accountingId)); // Obtener categorías de ingresos
            categories.value = [...new Set([...spentResponse, ...incomeResponse])]; // Combinar y eliminar duplicados
        } catch (error) {
            console.error('Hubo un error al obtener las categorías combinadas:', error);
        }
    };

    // Función para obtener todas las operaciones (gastos e ingresos) con filtros
    const fetchOperationsAsync = async (accountingId, filterType, startDate, endDate) => {
        const params = { accountingId, filterType, startDate, endDate };

        try {
            await Promise.all([
                fetchData(fetchOperations, params, operations, 'Hubo un error al obtener las operaciones'),
                fetchData(fetchSpentsFiltered, params, spentsFiltered, 'Hubo un error al obtener los gastos filtrados'),
                fetchData(fetchIncomesFiltered, params, incomesFiltered, 'Hubo un error al obtener los ingresos filtrados')
            ]);
        } catch (error) {
            console.error('Error al actualizar los datos:', error);
        }
    };

    // Función para obtener todas las operaciones de todas las contabilidades del usuario
    const fetchAllAccountingsUserOperationsAsync = async () => await fetchData(fetchAllAccountingsUserOperations, undefined, allAccountingUserOperations, 'Hubo un error al obtener todas las operaciones de la contabilidad del usuario');

    // Funciones para obtener gastos e ingresos filtrados en intervalos específicos
    const fetchSpentsInterval = async (accountingId, filterType, startDate, endDate) => {
        const params = { accountingId, filterType, startDate, endDate };
        await fetchData(fetchSpentsFiltered, params, spentsFiltered, 'Hubo un error al obtener los gastos en el intervalo especificado');
    };

    const fetchIncomeInterval = async (accountingId, filterType, startDate, endDate) => {
        const params = { accountingId, filterType, startDate, endDate };
        await fetchData(fetchIncomesFiltered, params, incomesFiltered, 'Hubo un error al obtener los ingresos en el intervalo especificado');
    };

    // Función para obtener diferencias de categorías entre gastos e ingresos
    const categoryDifferencesAsync = async (accountingId, filterType, startDate, endDate) => {
        const params = { accountingId, filterType, startDate, endDate };
        await fetchData(fetchCategoriesDifferences, params, categoriesDifferences, 'Hubo un error al obtener las diferencias de categorías');
    };

    // Propiedades computadas para datos específicos y procesamiento de datos
    const sharedAccountings = computed(() => accountings.value.filter(accounting => accounting.type === 'SHARED')); // Filtra las contabilidades compartidas
    const accountingSharedSelected = computed(() => sharedAccountings.value.filter(accounting => accounting.id === Number(accountingId.value))); // Contabilidad compartida seleccionada
    const currentAccounting = computed(() => accountings.value.find(account => account.id === Number(accountingId.value))); // Contabilidad actual

    const processedSpents = computed(() => proccessCategories(spentsFiltered.value, 'category')); // Categorías de gastos procesadas
    const totalSpentMonth = computed(() => spentsFiltered.value.reduce((total, { quantity }) => total + quantity, 0)); // Total de gastos en el mes
    const processDailySpentData = computed(() => {
        if (!spentsFiltered.value || spentsFiltered.value.length === 0 || !spentsFiltered.value.some(item => item && item.date)) {
            console.warn("Datos no disponibles o incompletos para procesar en processDailySpentData");
            return [];
        }
        return processLinearGraphData(spentsFiltered.value); // Procesa datos diarios de gastos
    });
    const processedSpentsUser = computed(() => proccessCategories(spentsFiltered.value, 'username')); // Gastos por usuario procesados

    const processedIncomes = computed(() => proccessCategories(incomesFiltered.value, 'category')); // Categorías de ingresos procesadas
    const totalIncomeMonth = computed(() => incomesFiltered.value.reduce((total, { quantity }) => total + quantity, 0)); // Total de ingresos en el mes
    const processDailyIncomeData = computed(() => {
        if (!incomesFiltered.value || incomesFiltered.value.length === 0 || !incomesFiltered.value.some(item => item && item.date)) {
            console.warn("Datos no disponibles o incompletos para procesar en processDailyIncomeData");
            return [];
        }
        return processLinearGraphData(incomesFiltered.value); // Procesa datos diarios de ingresos
    });
    const processedIncomesUser = computed(() => proccessCategories(incomesFiltered.value, 'username')); // Ingresos por usuario procesados

    const combinedDataProcessed = computed(() => processFinancialData(operations.value)); // Procesa los datos financieros combinados
    const savingsData = computed(() => {
        return combinedDataProcessed.value.map(item => ({
            date: item.date,
            savings: item.income - item.spent
        })); // Calcula los ahorros (ingresos menos gastos) por fecha
    });

    // Observador para actualizar el nombre de la contabilidad actual cuando cambie la contabilidad seleccionada
    watch(currentAccounting, (newData) => {
        if (newData) {
            accountingName.value = newData.name;
        }
    });

    // Exporta todas las referencias y funciones para que puedan ser usadas en los componentes
    return {
        accountings, loadAccountings, userRole, fetchUserRoleAsync, fetchCategoriesSpentAsync,
        fetchCategoriesIncomeAsync, categories, fetchAccountingPersonalAsync, accountingPersonal,
        username, accountingName, fetchOperationsAsync, operations, accountingId, processedSpents,
        fetchSpentsInterval, spentsFiltered, totalSpentMonth, fetchIncomeInterval, incomesFiltered,
        totalIncomeMonth, processedIncomes, processDailyIncomeData, combinedDataProcessed, categoryDifferencesAsync,
        categoriesDifferences, sharedAccountings, fetchUsersAccountingAsync, usersAccounting, accountingSharedSelected,
        processedSpentsUser, processedIncomesUser, fetchCategoriesAsync, allOperations, fetchAllAccountingsUserOperationsAsync,
        allAccountingUserOperations, processDailySpentData, dailySpentData, savingsData, currentAccounting
    };
};

