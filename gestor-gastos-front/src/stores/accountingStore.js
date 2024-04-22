import {computed, ref} from 'vue';
import {useRouter} from 'vue-router';
import {
    fetchAccountingPersonal,
    fetchAccountings,
    fetchCategoriesDifferences,
    fetchCategoriesIncome,
    fetchCategoriesSpent
} from '@/service/accountingService';
import {fetchUserRole, fetchUsersAccounting} from '@/service/userRoleService';
import {
    fetchAllAccountingsUserOperations,
    fetchIncomesFiltered,
    fetchOperations,
    fetchSpentsFiltered
} from '@/service/operationService';

import {proccessCategories, processFinancialData, processLinearGraphData} from '@/utils/functions';

export const useAccountingStore = () => {
    const router = useRouter();
    const accountings = ref([]);
    const userRole = ref([]);
    const categories = ref([]);
    const username = ref(localStorage.getItem('username') || '');
    const accountingPersonal = ref({});

    const accountingName = computed(() => {
        if (router.currentRoute.value.name === 'shared') {
            return router.currentRoute.value.params.accountingName;
        }
        else{
            return 'Contabilidad personal';
        }
    });
    const accountingId = computed(() => {
        return router.currentRoute.value.query.id;
    });

    const operations = ref([]);
    const allOperations = ref([]);
    const allAccountingUserOperations = ref([]);
    const spentsFiltered = ref([]);
    const dailySpentData = ref([]);
    const incomesFiltered = ref([]);
    const categoriesDifferences = ref({
        spentDifferences: {},
        incomeDifferences: {}
    });
    const usersAccounting = ref([]);

    async function fetchData(endpointFunction, params = undefined, targetRef = undefined, errorHandler = null) {
        try {
            const response = await endpointFunction(params);
            if (targetRef) {
                targetRef.value = response.data;
            }
            return response.data;
        } catch (error) {
            console.error(errorHandler || `Hubo un error al obtener los datos de: ${endpointFunction.name}`, error);
            if (targetRef) {
                targetRef.value = [];
            }
        }
    }

    const loadAccountings = async () => await fetchData(fetchAccountings, undefined, accountings, 'Hubo un error al obtener las contabilidades');

    const fetchAccountingPersonalAsync = async () => await fetchData(fetchAccountingPersonal, undefined, accountingPersonal, 'Hubo un error al obtener la contabilidad personal');

    const fetchUserRoleAsync = async (accountingId) => {
        const role = await fetchData(fetchUserRole, accountingId, userRole, 'Error al obtener el rol del usuario');
        if (role) {
            userRole.value = role.role;
        }
    };

    const fetchUsersAccountingAsync = async (accountingId) => await fetchData(fetchUsersAccounting, accountingId, usersAccounting, 'Hubo un error al obtener los usuarios de la contabilidad');

    const fetchCategoriesSpentAsync = async (accountingId) => await fetchData(fetchCategoriesSpent, accountingId, categories, 'Hubo un error al obtener las categorías de gastos');

    const fetchCategoriesIncomeAsync = async (accountingId) => await fetchData(fetchCategoriesIncome, accountingId, categories, 'Hubo un error al obtener las categorías de ingresos');

    const fetchCategoriesAsync = async (accountingId) => {
        try {
            const spentResponse = await fetchData(() => fetchCategoriesSpent(accountingId));
            const incomeResponse = await fetchData(() => fetchCategoriesIncome(accountingId));
            categories.value = [...new Set([...spentResponse, ...incomeResponse])]; // Combinando y eliminando duplicados
        } catch (error) {
            console.error('Hubo un error al obtener las categorías combinadas:', error);
        }
    };

    const fetchOperationsAsync = async (accountingId, filterType, startDate, endDate) => {
        const params = {accountingId, filterType, startDate, endDate};
        await fetchData(fetchOperations, params, operations, 'Hubo un error al obtener las operaciones');
    };

    const fetchAllAccountingsUserOperationsAsync = async () => await fetchData(fetchAllAccountingsUserOperations, undefined, allAccountingUserOperations, 'Hubo un error al obtener todas las operaciones de la contabilidad del usuario');

    const fetchSpentsInterval = async (accountingId, filterType, startDate, endDate) => {
        const params = {accountingId, filterType, startDate, endDate};
        await fetchData(fetchSpentsFiltered, params, spentsFiltered, 'Hubo un error al obtener los gastos en el intervalo especificado');
    };

    const fetchIncomeInterval = async (accountingId, filterType, startDate, endDate) => {
        const params = {accountingId, filterType, startDate, endDate};
        await fetchData(fetchIncomesFiltered, params, incomesFiltered, 'Hubo un error al obtener los ingresos en el intervalo especificado');
    };

    const categoryDifferencesAsync = async (accountingId, filterType, startDate, endDate) => {
        const params = {accountingId, filterType, startDate, endDate};
        await fetchData(fetchCategoriesDifferences, params, categoriesDifferences, 'Hubo un error al obtener las diferencias de categorías');
    };

    const sharedAccountings = computed(() => { return accountings.value.filter(accounting => accounting.type === 'SHARED') });
    const accountingSharedSelected = computed(() => {
        return sharedAccountings.value.filter(accounting => accounting.id === Number(accountingId.value));
    });

    const processedSpents = computed(() => proccessCategories(spentsFiltered.value, 'category'));
    const totalSpentMonth = computed(() => spentsFiltered.value.reduce((total, { quantity }) => total + quantity, 0));
    const processDailySpentData = computed(() => processLinearGraphData(spentsFiltered.value));
    const processedSpentsUser = computed(() => { return proccessCategories(spentsFiltered.value, 'username'); });


    const processedIncomes = computed(() => { return proccessCategories(incomesFiltered.value, 'category'); });
    const totalIncomeMonth = computed(() => { return incomesFiltered.value.reduce((total, { quantity }) => total + quantity, 0); });
    const processDailyIncomeData = computed(() => {return processLinearGraphData(incomesFiltered.value); });
    const processedIncomesUser = computed(() => { return proccessCategories(incomesFiltered.value, 'username'); });

    const combinedDataProcessed = computed(() => { return processFinancialData(operations.value); });
    const savingsData = computed(() => {
        return combinedDataProcessed.value.map(item => ({
            date: item.date,
            savings: item.income - item.spent
        }));
    });

    return {
        accountings, loadAccountings: loadAccountings, userRole, fetchUserRoleAsync, fetchCategoriesSpentAsync, fetchCategoriesIncomeAsync, categories, fetchAccountingPersonalAsync, accountingPersonal, username, accountingName,
        fetchOperationsAsync, operations, accountingId, processedSpents, fetchSpentsInterval, spentsFiltered, totalSpentMonth, fetchIncomeInterval,
        incomesFiltered, totalIncomeMonth, processedIncomes, processDailyIncomeData, combinedDataProcessed, categoryDifferencesAsync,
        categoriesDifferences, sharedAccountings, fetchUsersAccountingAsync, usersAccounting, accountingSharedSelected, processedSpentsUser, processedIncomesUser, fetchCategoriesAsync,
        allOperations, fetchAllAccountingsUserOperationsAsync, allAccountingUserOperations, processDailySpentData, dailySpentData, savingsData
    };
};
