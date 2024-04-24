import { ref, computed } from 'vue';

export function useMultiplePagination(dataSources) {

    const pages = dataSources.map(source => {

        const currentPage = ref(1);
        const operationsPerPage = source.reduced ? 4 : 7;

        const totalPages = computed(() => Math.ceil(source.data.value.length / operationsPerPage));

        const paginatedData = computed(() => {
            const start = (currentPage.value - 1) * operationsPerPage;
            const end = start + operationsPerPage;
            return source.data.value.slice(start, end);
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
            paginatedData,
            nextPage,
            prevPage
        };
    });

    return pages;
}