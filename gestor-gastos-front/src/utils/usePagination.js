// src/composables/usePagination.js
import { ref, computed } from 'vue';

export function usePagination(operations, reduced = false) {
    const currentPage = ref(1);
    let operationsPerPage;

    if (reduced) {
        operationsPerPage = 4;
    } else {
        operationsPerPage = 7;
    }

    const totalPages = computed(() => Math.ceil(operations.value.length / operationsPerPage));

    const paginatedOperations = computed(() => {
        const start = (currentPage.value - 1) * operationsPerPage;
        const end = start + operationsPerPage;
        return operations.value.slice(start, end);
    });

    function nextPage() {
        if (currentPage.value < totalPages.value) currentPage.value++;
    }

    function prevPage() {
        if (currentPage.value > 1) currentPage.value--;
    }

    return {
        currentPage,
        totalPages,
        paginatedOperations,
        nextPage,
        prevPage
    };
}

export function useDateSelection() {
    const selectedInterval = ref('last7days');
    const customStart = ref('');
    const customEnd = ref('');

    return { selectedInterval, customStart, customEnd };
}