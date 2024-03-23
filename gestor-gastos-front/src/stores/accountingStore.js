import {computed, ref} from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

export const useAccountingStore = () => {
    const accountings = ref([]);
    const userRole = ref('EDITOR');
    const categories = ref([]);
    const username = ref(localStorage.getItem('username') || '');
    const accountingName = computed(() => {
        if (router.currentRoute.value.name == 'shared') {
            return router.currentRoute.value.params.accountingName;
        }
        else{
            return 'Contabilidad personal';
        }
    });
    const router = useRouter();

    const fetchAccountings = async () => {
        try {
            const response = await axios.get('http://localhost:8081/api/accounting/accountingUser', {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('userToken')}`,
                },
            });
            console.log("Datos recibidos:", response.data);
            accountings.value = response.data;
            console.log("Datos asignados a accountings:", accountings.value);
        } catch (error) {
            console.error('Hubo un error al obtener las contabilidades:', error);
        }
    };
    const fetchUserRole = async (accountingId) => {
        try {
            const response = await axios.get(`http://localhost:8081/api/accounting/${accountingId}/rol`, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('userToken')}`,
                },
            });
            userRole.value = response.data.role;
        } catch (error) {
            console.error('Error al obtener el rol del usuario:', error);
            userRole.value = null; // Asegúrate de manejar errores apropiadamente
        }
    };

    const fetchCategories = async (accountingId) => {
        try {
            const response = await axios.get(`http://localhost:8081/api/accounting/${accountingId}/categories`, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('userToken')}`,
                },
            });
            console.log(response);
            categories.value = response.data;
        } catch (error) {
            console.error('Hubo un error al obtener las categorías:', error);
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
        accountings, fetchAccountings, userRole, fetchUserRole, fetchCategories, username, accountingName, logout, navigate:router.push, modifyUser, modifyPassword
    };
};
