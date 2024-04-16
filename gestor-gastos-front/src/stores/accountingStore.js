import {computed, ref} from 'vue';
import { useRouter } from 'vue-router';
import {
    fetchAccountings,
    fetchAccountingPersonal,
    fetchCategoriesSpent, fetchCategoriesIncome, fetchCategoriesDifferences
} from '@/service/accountingService';
import { fetchUserRole, fetchUsersAccounting } from '@/service/userRoleService';
import {
    fetchIncomes,
    fetchIncomesMonths,
    fetchOperations,
    fetchAllOperations,
    fetchSpents,
    fetchAllAccountingsUserOperations, fetchSpentsFiltered
} from '@/service/operationService';

import { processLinearGraphData } from '@/utils/functions';

export const useAccountingStore = () => {
    const router = useRouter();
    const accountings = ref([]);
    const userRole = ref([]);
    const categories = ref([]);
    const username = ref(localStorage.getItem('username') || '');
    const accountingPersonal = ref({});
    const accountingName = computed(() => {
        if (router.currentRoute.value.name == 'shared') {
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
    const spents = ref([]);
    const spentsMonths = ref([]);
    const dailySpentData = ref([]);
    const incomes = ref([]);
    const incomesMonths = ref([]);
    const monthlyIncomeData = ref([]);
    const monthlySpentData = ref([]);
    const categoriesDifferences = ref([]);
    const usersAccounting = ref([]);

    const loadAccountings = async () => {
        try {
            const response = await fetchAccountings();
            accountings.value = response.data;
        } catch (error) {
            console.error('Hubo un error al obtener las contabilidades:', error);
        }
    };

    const sharedAccountings = computed(() => {
        return accountings.value.filter(accounting => accounting.type === 'SHARED');
    });

    const accountingSharedSelected = computed(() => {
            return sharedAccountings.value.filter(accounting => accounting.id === Number(accountingId.value));
    });

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
            // Combina las listas de categorías y elimina duplicados.
            const combinedCategories = [...new Set([...spentResponse.data, ...incomeResponse.data])];
            categories.value = combinedCategories;
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

    const fetchAllOperationsAsync = async () => {
        try {
            const response = await fetchAllOperations(accountingId)
            allOperations.value = response.data;
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

    const fetchSpentsAsync = async (accountingId) => {
        try {
            const response = await fetchSpents(accountingId);
            spents.value = response.data;
        } catch (error) {
            console.error('Hubo un error al obtener los gastos:', error);
        }
    };

    const fetchSpentsInterval = async (accountingId, filterType, startDate, endDate) => {
        try {
            const response = await fetchSpentsFiltered(accountingId, filterType, startDate, endDate);
            spentsMonths.value = response.data;
        } catch (error) {
            console.error('Hubo un error al obtener los gastos:', error);
        }
    };


    const processedSpents = computed(() => {
        const sumsByCategory = spentsMonths.value.reduce((acc, { category, quantity }) => {
            acc[category] = (acc[category] || 0) + parseFloat(quantity);
            return acc;
        }, {});
        const sortedCategories = Object.entries(sumsByCategory)
            .sort((a, b) => b[1] - a[1])
            .map(([category, total]) => ({ category, total }));
        const topCategoriesSpents = sortedCategories.slice(0, 5);
        const otherTotal = sortedCategories.slice(5).reduce((acc, { total }) => acc + total, 0);
        if (otherTotal > 0) {
            topCategoriesSpents.push({ category: 'Otros', total: otherTotal });
        }
        return topCategoriesSpents;
    });

    const processedSpentsUser = computed(() => {
        const sumsByCategory = spentsMonths.value.reduce((acc, { username, quantity }) => {
            acc[username] = (acc[username] || 0) + parseFloat(quantity);
            return acc;
        }, {});
        const sortedCategories = Object.entries(sumsByCategory)
            .sort((a, b) => b[1] - a[1])
            .map(([username, total]) => ({ username, total }));
        const topUsersSpent = sortedCategories.slice(0, 5);
        const otherTotal = sortedCategories.slice(5).reduce((acc, { total }) => acc + total, 0);
        if (otherTotal > 0) {
            topUsersSpent.push({ category: 'Otros', total: otherTotal });
        }
        return topUsersSpent;
    });

    const totalSpentMonth = computed(() => {
        return spentsMonths.value.reduce((total, { quantity }) => total + quantity, 0);
    });

    const processDailySpentData = computed(() => {
        return processLinearGraphData(spentsMonths.value);
    });

    const topSpentCategory = computed(() => {
        return processedSpents.value[0] || null;
    });

    const fetchIncomeAsync = async (accountingId) => {
        try {
            const response = await fetchIncomes(accountingId);
            incomes.value = response.data;
        } catch (error) {
            console.error('Hubo un error al obtener los gastos:', error);
        }
    };

    const fetchIncomeMonthsAsync = async (accountingId, filterType, startDate, endDate) => {
        try {
            const response = await fetchIncomesMonths(accountingId, filterType, startDate, endDate);
            incomesMonths.value = response.data;
        } catch (error) {
            console.error('Hubo un error al obtener los gastos:', error);
        }
    };

    const processedIncomes = computed(() => {
        const sumsByCategory = incomesMonths.value.reduce((acc, { category, quantity }) => {
            acc[category] = (acc[category] || 0) + parseFloat(quantity);
            return acc;
        }, {});
        const sortedCategories = Object.entries(sumsByCategory)
            .sort((a, b) => b[1] - a[1])
            .map(([category, total]) => ({ category, total }));
        const topCategoriesIncomes = sortedCategories.slice(0, 5);
        const otherTotal = sortedCategories.slice(5).reduce((acc, { total }) => acc + total, 0);
        if (otherTotal > 0) {
            topCategoriesIncomes.push({ category: 'Otros', total: otherTotal });
        }
        return topCategoriesIncomes;
    });

    const processedIncomesUser = computed(() => {
        const sumsByCategory = incomesMonths.value.reduce((acc, { username, quantity }) => {
            acc[username] = (acc[username] || 0) + parseFloat(quantity);
            return acc;
        }, {});
        const sortedCategories = Object.entries(sumsByCategory)
            .sort((a, b) => b[1] - a[1])
            .map(([username, total]) => ({ username, total }));
        const topUsersIncomes = sortedCategories.slice(0, 5);
        const otherTotal = sortedCategories.slice(5).reduce((acc, { total }) => acc + total, 0);
        if (otherTotal > 0) {
            topUsersIncomes.push({ category: 'Otros', total: otherTotal });
        }
        return topUsersIncomes;
    });

    const totalIncomeMonth = computed(() => {
        return incomesMonths.value.reduce((total, { quantity }) => total + quantity, 0);
    });

    const processDailyIncomeData = computed(() => {
        return processLinearGraphData(incomesMonths.value);
    });


    const latestIncomes = computed(() => {
        return incomesMonths.value
            .slice()
            .sort((a, b) => new Date(b.date.split('-').reverse().join('-')) - new Date(a.date.split('-').reverse().join('-')))
            .slice(0, 5);
    });

    const monthlySavingsData = computed(() => {
        const savings = processedIncomes.value.map(income => {
            const expense = processedSpents.value.find(e => e.date === income.date) || { total: 0 };
            const totalSavings = income.total - expense.total;
            return {
                date: income.date,
                totalSavings
            };
        });

        return savings;
    });

    const topIncomeCategory = computed(() => {
        // Asumiendo que processedIncomes está ordenado, toma la primera categoría como la principal
        return processedIncomes.value[0] || null;
    });

    const categoryDifferencesAsync = async (accountingId, filterType, startDate, endDate) => {
        try {
            const response = await fetchCategoriesDifferences(accountingId, filterType, startDate, endDate);
            categoriesDifferences.value = response.data;
        } catch (error) {
            console.error('Hubo un error al obtener los gastos:', error);
        }
    };


    const logout = () => {
        localStorage.clear();
        router.push('/login');
    };

    const modifyUser = () => {
        router.push('/modify-details');
    };

    const modifyPassword = () => {
        router.push('/modify-password');
    };

    return {
        accountings, loadAccountings: loadAccountings, userRole, fetchUserRoleAsync, fetchCategoriesSpentAsync, fetchCategoriesIncomeAsync, categories, fetchAccountingPersonalAsync, accountingPersonal, username, accountingName, logout, navigate:router.push, modifyUser, modifyPassword,
        fetchOperationsAsync, operations, fetchSpentsAsync, spents, accountingId, processedSpents, fetchSpentsInterval, spentsMonths, totalSpentMonth, monthlySpentData ,fetchIncomeMonthsAsync,
        fetchIncomeAsync, incomesMonths, incomes,totalIncomeMonth, latestIncomes, monthlyIncomeData, processedIncomes, processDailyIncomeData, monthlySavingsData, topSpentCategory, topIncomeCategory, categoryDifferencesAsync,
        categoriesDifferences, sharedAccountings, fetchUsersAccountingAsync, usersAccounting, accountingSharedSelected, processedSpentsUser, processedIncomesUser, fetchCategoriesAsync,
        fetchAllOperationsAsync, allOperations, fetchAllAccountingsUserOperationsAsync, allAccountingUserOperations, processDailySpentData, dailySpentData
    };
};
