import { ref, watch } from 'vue';
import { useRouter } from 'vue-router';

export const globalStore = () => {
    const router = useRouter();

    const accountingId = ref(router.currentRoute.value.query.id);

    watch(() => router.currentRoute.value.query.id, (newId) => {
        accountingId.value = newId;
    });

    return {
        accountingId,
    };
};