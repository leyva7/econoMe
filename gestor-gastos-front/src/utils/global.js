import {ref} from "vue";
import router from "@/router";

export const home = () => {
    const personalAccountingId = localStorage.getItem('personalAccountingId');
    router.push({
        name: 'home',
        query: { id: personalAccountingId }
    });
};

export const navigate = (path) => {
    router.push({
        path: path
    });
};

export const navigateHome = (path) => {
    router.push({
        path: path,
        query: { id: localStorage.getItem('personalAccountingId') },
    });
};

export const navigateToAccounting = (accounting) => {
    router.push({
        name: 'shared',
        params: { accountingName: accounting.name },
        query: { id: accounting.id },
    });
};

export const logout = () => {
    localStorage.clear();
    router.push('/');
};

export const spentCategoryColors = ['#480707', '#a01414', '#e61c1c', '#ff6a6a', '#ffc7c7', '#a4b0be'];
export const incomeCategoryColors = ['#183c27', '#257042', '#3aad66', '#96dfb1', '#e1f7e8', '#a4b0be'];

export const hasDataSpents = ref(false);

export  const commonOptions = {
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
