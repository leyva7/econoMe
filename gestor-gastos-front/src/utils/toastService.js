import {useToast} from "vue-toast-notification";
const $toast = useToast();

export function saveToastMessage(type, message) {
    localStorage.setItem('toastType', type);
    localStorage.setItem('toastMessage', message);
}

export function clearToastMessage() {
    localStorage.removeItem('toastType');
    localStorage.removeItem('toastMessage');
}

export function showToastFromStorage() {
    const type = localStorage.getItem('toastType');
    const message = localStorage.getItem('toastMessage');
    if (message) {
        $toast[type](message, {
            duration: 5000,
            position: 'top'
        });
        clearToastMessage();
    }
}