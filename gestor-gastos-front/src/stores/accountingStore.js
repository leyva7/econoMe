import {computed, ref} from 'vue';
import { useRouter } from 'vue-router';
import { fetchAccountings, fetchAccountingPersonal, fetchCategories} from '../service/accountingService';
import { fetchUserRole } from '../service/userRoleService';
import { fetchOperations, fetchSpents, fetchSpentsMonths } from '../service/operationService';

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

    const fetchCategoriesAsync = async (accountingId) => {
        try {
            const response = await fetchCategories(accountingId);
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
        const topCategories = sortedCategories.slice(0, 5);
        const otherTotal = sortedCategories.slice(5).reduce((acc, { total }) => acc + total, 0);
        if (otherTotal > 0) {
            topCategories.push({ category: 'Otros', total: otherTotal });
        }
        return topCategories;
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
        console.log(spentsClone.slice(0, 5));
        return spentsClone.slice(0, 5);
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
        accountings, fetchAccountingsAsync, userRole, fetchUserRoleAsync, fetchCategoriesAsync, categories, fetchAccountingPersonalAsync, accountingPersonal, username, accountingName, logout, navigate:router.push, modifyUser, modifyPassword,
        fetchOperationsAsync, operations, fetchSpentsAsync, spents, accountingId, processedSpents, fetchSpentsMonthsAsync, spentsMonths, totalSpentMonth, latestSpents, weeklySpentData
    };
};
