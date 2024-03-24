import {computed, ref} from 'vue';
import { useRouter } from 'vue-router';
import { fetchAccountings, fetchAccountingPersonal, fetchCategories} from '../service/accountingService';
import { fetchUserRole } from '../service/userRoleService';
import { fetchOperations, fetchSpents } from '../service/operationService';

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
            console.log(response);
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
        accountings, fetchAccountingsAsync, userRole, fetchUserRoleAsync, fetchCategoriesAsync, categories, fetchAccountingPersonalAsync, accountingPersonal, username, accountingName, logout, navigate:router.push, modifyUser, modifyPassword,
        fetchOperationsAsync, operations, fetchSpentsAsync, spents, accountingId
    };
};
