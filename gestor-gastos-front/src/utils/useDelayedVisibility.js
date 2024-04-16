import {ref} from "vue";

export function useDelayedVisibility(initialVisibility = false, delay = 1000) {
    const isVisible = ref(initialVisibility);

    const setVisibility = (visible) => {
        isVisible.value = visible;
    };

    const setVisibilityAfterDelay = (visible = true) => {
        setTimeout(() => {
            isVisible.value = visible;
        }, delay);
    };

    return { isVisible, setVisibility, setVisibilityAfterDelay };
}