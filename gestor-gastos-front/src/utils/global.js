import {ref} from "vue";


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
