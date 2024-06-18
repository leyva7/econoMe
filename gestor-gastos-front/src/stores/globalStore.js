import { ref, watch } from 'vue'; // Importa las funciones 'ref' y 'watch' de Vue
import { useRouter } from 'vue-router'; // Importa el hook 'useRouter' de Vue Router

export const globalStore = () => { // Define la función globalStore como una función flecha

    const router = useRouter(); // Obtiene el enrutador actual utilizando el hook 'useRouter' de Vue Router

    const accountingId = ref(router.currentRoute.value.query.id); // Crea una referencia reactiva 'accountingId' inicializada con el valor actual del parámetro 'id' de la ruta

    watch(() => router.currentRoute.value.query.id, (newId) => { // Establece un observador para el cambio en el parámetro 'id' de la ruta
        accountingId.value = newId; // Cuando cambia el valor del parámetro 'id', actualiza 'accountingId' con el nuevo valor
    });

    return {
        accountingId, // Retorna 'accountingId' como una propiedad del objeto retornado por globalStore
    };
};
