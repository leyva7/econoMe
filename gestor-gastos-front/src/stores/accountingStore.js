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

    const loadAccountings = async () => {
        try {
            const response = await fetchAccountings();
            accountings.value = response.data;
        } catch (error) {
            console.error('Hubo un error al obtener las contabilidades:', error);
        }
    };

    const fetchAccountingPersonalAsync = async () => {
        try {
            const response = await fetchAccountingPersonal();
            accountingPersonal.value = response.data;
        } catch (error) {
            console.error('Hubo un error al obtener la contabilidad personal:', error);
        }
    };

    const fetchUserRoleAsync = async (accountingId) => {
        try {
            const response = await fetchUserRole(accountingId);
            userRole.value = response.data.role;
        } catch (error) {
            console.error('Error al obtener el rol del usuario:', error);
            userRole.value = null;
        }
    };

    const fetchUsersAccountingAsync = async (accountingId) => {
        try {
            const response = await fetchUsersAccounting(accountingId);
            usersAccounting.value = response.data;
        } catch (error) {
            console.error('Hubo un error al obtener los usuarios de la contabilidad:', error);
        }
    };

    const fetchCategoriesSpentAsync = async (accountingId) => {
        try {
            const response = await fetchCategoriesSpent(accountingId);
            categories.value = response.data;
        } catch (error) {
            console.error('Hubo un error al obtener las categorías:', error);
        }
    };

    const fetchCategoriesIncomeAsync = async (accountingId) => {
        try {
            const response = await fetchCategoriesIncome(accountingId);
            categories.value = response.data;
        } catch (error) {
            console.error('Hubo un error al obtener las categorías:', error);
        }
    };

    const fetchCategoriesAsync = async (accountingId) => {
        try {
            const [spentResponse, incomeResponse] = await Promise.all([
                fetchCategoriesSpent(accountingId),
                fetchCategoriesIncome(accountingId)
            ]);
            categories.value = [...new Set([...spentResponse.data, ...incomeResponse.data])];
        } catch (error) {
            console.error('Hubo un error al obtener las categorías combinadas:', error);
        }
    };

    const fetchOperationsAsync = async (accountingId, filterType, startDate, endDate) => {
        try {
            const response = await fetchOperations(accountingId, filterType, startDate, endDate)
            operations.value = response.data;
        } catch (error) {
            console.error('Hubo un error al obtener las operaciones:', error);
        }
    };

    const fetchAllAccountingsUserOperationsAsync = async () => {
        try {
            const response = await fetchAllAccountingsUserOperations(accountingId)
            allAccountingUserOperations.value = response.data;
        } catch (error) {
            console.error('Hubo un error al obtener las operaciones:', error);
        }
    };

    const fetchSpentsInterval = async (accountingId, filterType, startDate, endDate) => {
        try {
            const response = await fetchSpentsFiltered(accountingId, filterType, startDate, endDate);
            spentsFiltered.value = response.data;
        } catch (error) {
            console.error('Hubo un error al obtener los gastos:', error);
        }
    };

    const sharedAccountings = computed(() => {
        return accountings.value.filter(accounting => accounting.type === 'SHARED');
    });

    const accountingSharedSelected = computed(() => {
        return sharedAccountings.value.filter(accounting => accounting.id === Number(accountingId.value));
    });


    const processedSpents = computed(() => {
        return proccessCategories(spentsFiltered.value, 'category');
    });

    const processedSpentsUser = computed(() => {
        return proccessCategories(spentsFiltered.value, 'username');
    });

    const totalSpentMonth = computed(() => {
        return spentsFiltered.value.reduce((total, { quantity }) => total + quantity, 0);
    });

    const processDailySpentData = computed(() => {
        return processLinearGraphData(spentsFiltered.value);
    });

    const fetchIncomeMonthsAsync = async (accountingId, filterType, startDate, endDate) => {
        try {
            const response = await fetchIncomesFiltered(accountingId, filterType, startDate, endDate);
            incomesFiltered.value = response.data;
        } catch (error) {
            console.error('Hubo un error al obtener los gastos:', error);
        }
    };

    const processedIncomes = computed(() => {
        return proccessCategories(incomesFiltered.value, 'category');
    });

    const processedIncomesUser = computed(() => {
        return proccessCategories(incomesFiltered.value, 'username');
    });

    const totalIncomeMonth = computed(() => {
        return incomesFiltered.value.reduce((total, { quantity }) => total + quantity, 0);
    });

    const processDailyIncomeData = computed(() => {
        return processLinearGraphData(incomesFiltered.value);
    });

    const combinedDataProcessed = computed(() => {
        return processFinancialData(operations.value);
    });

    const savingsData = computed(() => {
        return combinedDataProcessed.value.map(item => ({
            date: item.date,
            savings: item.income - item.spent
        }));
    });

    const categoryDifferencesAsync = async (accountingId, filterType, startDate, endDate) => {
        try {
            const response = await fetchCategoriesDifferences(accountingId, filterType, startDate, endDate);
            categoriesDifferences.value = response.data;
        } catch (error) {
            console.error('Hubo un error al obtener los gastos:', error);
        }
    };

    return {
        accountings, loadAccountings: loadAccountings, userRole, fetchUserRoleAsync, fetchCategoriesSpentAsync, fetchCategoriesIncomeAsync, categories, fetchAccountingPersonalAsync, accountingPersonal, username, accountingName,
        fetchOperationsAsync, operations, accountingId, processedSpents, fetchSpentsInterval, spentsFiltered, totalSpentMonth, fetchIncomeMonthsAsync,
        incomesFiltered, totalIncomeMonth, processedIncomes, processDailyIncomeData, combinedDataProcessed, categoryDifferencesAsync,
        categoriesDifferences, sharedAccountings, fetchUsersAccountingAsync, usersAccounting, accountingSharedSelected, processedSpentsUser, processedIncomesUser, fetchCategoriesAsync,
        allOperations, fetchAllAccountingsUserOperationsAsync, allAccountingUserOperations, processDailySpentData, dailySpentData, savingsData
    };
};
