import {computed, ref} from 'vue';
import { useRouter } from 'vue-router';
import {
    fetchAccountings,
    fetchAccountingPersonal,
    fetchCategoriesSpent, fetchCategoriesIncome
} from '../service/accountingService';
import { fetchUserRole } from '../service/userRoleService';
import {
    fetchIncomes,
    fetchIncomesMonths,
    fetchOperations,
    fetchSpents,
    fetchSpentsMonths
} from '../service/operationService';

export const useAccountingStore = () => {
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
    const spents = ref([]);
    const spentsMonths = ref([]);
    const weeklySpentData = ref([]);
    const incomes = ref([]);
    const incomesMonths = ref([]);
    const monthlyIncomeData = ref([]);
    const router = useRouter();

    const fetchAccountingsAsync = async () => {
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

    const fetchOperationsAsync = async (accountingId) => {
        try {
            const response = await fetchOperations(accountingId)
            operations.value = response.data;
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

    const fetchSpentsMonthsAsync = async (accountingId) => {
        try {
            const response = await fetchSpentsMonths(accountingId);
            spentsMonths.value = response.data;
            processWeeklySpentData();
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

    const totalSpentMonth = computed(() => {
        return spentsMonths.value.reduce((total, { quantity }) => total + quantity, 0);
    });

    const processWeeklySpentData = () => {
        const weeks = {};
        spentsMonths.value.forEach(spent => {
            const spentDate = new Date(spent.date);
            const dayOfMonth = spentDate.getDate();

            // Determina la semana del mes basada en el día
            let weekOfMonth;
            if (dayOfMonth <= 6) {
                weekOfMonth = 1;
            } else if (dayOfMonth <= 13) {
                weekOfMonth = 2;
            } else if (dayOfMonth <= 20) {
                weekOfMonth = 3;
            } else if (dayOfMonth <= 27) {
                weekOfMonth = 4;
            } else {
                weekOfMonth = 5;
            }

            if (!weeks[weekOfMonth]) {
                weeks[weekOfMonth] = 0;
            }
            weeks[weekOfMonth] += spent.quantity;
        });

        // Prepara los datos para el gráfico
        weeklySpentData.value = Object.entries(weeks).map(([week, total]) => {
            return { week: `Semana ${week}`, total };
        });
    };

    const latestSpents = computed(() => {
        // Clona el arreglo de spents para no modificar el original
        const spentsClone = [...spentsMonths.value];
        // Ordena los gastos por fecha de forma descendente
        spentsClone.sort((a, b) => new Date(b.date) - new Date(a.date));
        // Retorna los primeros 5 elementos del arreglo ordenado
        return spentsClone.slice(0, 5);
    });

    const fetchIncomeAsync = async (accountingId) => {
        try {
            const response = await fetchIncomes(accountingId);
            incomes.value = response.data;
            processMonthlyIncomeData();
        } catch (error) {
            console.error('Hubo un error al obtener los gastos:', error);
        }
    };

    const fetchIncomeMonthsAsync = async (accountingId) => {
        try {
            const response = await fetchIncomesMonths(accountingId);
            incomesMonths.value = response.data;
            processWeeklySpentData();
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

    const totalIncomeMonth = computed(() => {
        return incomesMonths.value.reduce((total, { quantity }) => total + quantity, 0);
    });

    const processMonthlyIncomeData = () => {
        const now = new Date();
        let monthCounts = Array.from({ length: 6 }, (_, i) => {
            let date = new Date(now.getFullYear(), now.getMonth() - i, 1);
            let month = date.toLocaleString('default', { month: 'short' }).substring(0, 3); // Abreviación del mes
            let year = date.getFullYear().toString().substring(2); // Últimos dos dígitos del año
            return `${month} ${year}`;
        }).reverse();

        let incomeSums = monthCounts.map(month => {
            return {
                month: month,
                total: 0
            };
        });

        incomes.value.forEach(income => {
            let incomeDate = new Date(income.date);
            let incomeMonth = incomeDate.toLocaleString('default', { month: 'short' }).substring(0, 3); // Abreviación del mes
            let incomeYear = incomeDate.getFullYear().toString().substring(2); // Últimos dos dígitos del año
            let incomeMonthYear = `${incomeMonth} ${incomeYear}`;
            let index = incomeSums.findIndex(sum => sum.month === incomeMonthYear);
            if (index !== -1) {
                incomeSums[index].total += income.quantity;
            }
        });

        monthlyIncomeData.value = incomeSums;

        console.log(monthlyIncomeData.value);
    };





    const latestIncomes = computed(() => {
        // Clona el arreglo de spents para no modificar el original
        const incomesClone = [...incomesMonths.value];
        // Ordena los gastos por fecha de forma descendente
        incomesClone.sort((a, b) => new Date(b.date) - new Date(a.date));
        // Retorna los primeros 5 elementos del arreglo ordenado
        return incomesClone.slice(0, 5);
    });

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
        accountings, fetchAccountingsAsync, userRole, fetchUserRoleAsync, fetchCategoriesSpentAsync, fetchCategoriesIncomeAsync, categories, fetchAccountingPersonalAsync, accountingPersonal, username, accountingName, logout, navigate:router.push, modifyUser, modifyPassword,
        fetchOperationsAsync, operations, fetchSpentsAsync, spents, accountingId, processedSpents, fetchSpentsMonthsAsync, spentsMonths, totalSpentMonth, latestSpents, weeklySpentData, fetchIncomeMonthsAsync,
        fetchIncomeAsync, incomesMonths, totalIncomeMonth, latestIncomes, monthlyIncomeData, processedIncomes, processMonthlyIncomeData
    };
};
