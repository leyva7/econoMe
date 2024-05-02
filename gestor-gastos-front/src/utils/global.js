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
    console.log(localStorage.getItem('personalAccountingId'));
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

export const tableColumnsOperations = [
    { key: "type", label: "Tipo de operación" },
    { key: "category", label: "Categoría" },
    { key: "quantity", label: "Cantidad" },
    { key: "date", label: "Fecha" }
];



export const hasDataIncome = ref(false);
export const hasDataSpent = ref(false);
export const hasData = ref(false);

export const spentCategoryColors = ['#480707', '#a01414', '#e61c1c', '#ff6a6a', '#ffc7c7', '#a4b0be'];
export const incomeCategoryColors = ['#183c27', '#257042', '#3aad66', '#96dfb1', '#e1f7e8', '#a4b0be'];

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

export const setToday = (operation) => {
    const today = new Date();
    const yyyy = today.getFullYear();
    let mm = today.getMonth() + 1; // Los meses comienzan desde 0 en JavaScript
    let dd = today.getDate();

    // Agrega un cero delante para los días y meses menores a 10
    mm = mm < 10 ? '0' + mm : mm;
    dd = dd < 10 ? '0' + dd : dd;

    operation.date = `${yyyy}-${mm}-${dd}`;
};

export let showElement = ref[false];
