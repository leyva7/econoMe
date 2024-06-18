/**
 * Función para formatear una fecha a formato DD-MM-YYYY.
 * @param {Date|string} date - Fecha a formatear. Puede ser un objeto Date o una cadena de texto representando una fecha.
 * @returns {string} - Fecha formateada en formato DD-MM-YYYY.
 */
export function formatDateToDDMMYYYY(date) {
    const d = new Date(date); // Crea un objeto Date a partir del parámetro date
    let day = '' + d.getDate(); // Obtiene el día del mes
    let month = '' + (d.getMonth() + 1); // Obtiene el mes (0-11)
    const year = d.getFullYear(); // Obtiene el año

    // Asegura que el día y el mes tengan dos dígitos (agrega un '0' si es necesario)
    if (day.length < 2)
        day = '0' + day;
    if (month.length < 2)
        month = '0' + month;

    // Retorna la fecha formateada como una cadena 'DD-MM-YYYY'
    return [day, month, year].join('-');
}

/**
 * Función para formatear una fecha a formato YYYY-MM-DD.
 * @param {Date|string} date - Fecha a formatear. Puede ser un objeto Date o una cadena de texto representando una fecha.
 * @returns {string|null} - Fecha formateada en formato YYYY-MM-DD, o null si el parámetro date es falsy.
 */
export function formatAsYYYYMMDD(date) {
    if (!date) return null; // Retorna null si date no está definido
    const d = new Date(date); // Crea un objeto Date a partir del parámetro date
    const month = `${d.getMonth() + 1}`.padStart(2, '0'); // Obtiene el mes con dos dígitos (0-11)
    const day = `${d.getDate()}`.padStart(2, '0'); // Obtiene el día del mes con dos dígitos
    return `${d.getFullYear()}-${month}-${day}`; // Retorna la fecha formateada como 'YYYY-MM-DD'
}

/**
 * Función para convertir una cadena de fecha en formato DD-MM-YYYY a un objeto Date.
 * @param {string|Date} date - Cadena de fecha en formato 'DD-MM-YYYY' o un objeto Date.
 * @returns {Date} - Objeto Date parseado a partir de la cadena de fecha.
 */
function convertDate(date) {
    return typeof date === 'string' ? new Date(date.split('-').reverse().join('-')) : new Date(date);
}

/**
 * Función para formatear una fecha según el intervalo especificado.
 * @param {Date} date - Objeto Date a formatear.
 * @param {string} interval - Intervalo de tiempo ('days', 'weeks', 'months', 'years').
 * @returns {string} - Clave formateada según el intervalo ('DD-MM', 'MM-YYYY', 'YYYY', etc.).
 */
function formatDate(date, interval) {
    let key;
    let firstDayOfWeek;
    switch (interval) {
        case 'days':
            key = `${String(date.getDate()).padStart(2, '0')}-${String(date.getMonth() + 1).padStart(2, '0')}`;
            break;
        case 'weeks':
            firstDayOfWeek = new Date(date);
            firstDayOfWeek.setDate(date.getDate() - date.getDay());
            key = `${String(firstDayOfWeek.getDate()).padStart(2, '0')}-${String(firstDayOfWeek.getMonth() + 1).padStart(2, '0')}`;
            break;
        case 'months':
            key = `${String(date.getMonth() + 1).padStart(2, '0')}-${date.getFullYear()}`;
            break;
        case 'years':
            key = `${date.getFullYear()}`;
            break;
    }
    return key;
}

/**
 * Función para determinar el intervalo de tiempo adecuado basado en las fechas de los datos proporcionados.
 * @param {Array<object>} dataWithDates - Array de objetos con fechas.
 * @returns {string} - Intervalo de tiempo ('days', 'weeks', 'months', 'years').
 */
function determineInterval(dataWithDates) {
    const minDate = dataWithDates[0].date; // Fecha más antigua en los datos
    const maxDate = dataWithDates[dataWithDates.length - 1].date; // Fecha más reciente en los datos
    const diffTime = Math.abs(maxDate - minDate); // Diferencia en milisegundos entre la fecha más reciente y la más antigua
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)); // Diferencia en días

    // Determina el intervalo basado en la diferencia de días
    if (diffDays <= 7) {
        return 'days';
    } else if (diffDays <= 30) {
        return 'weeks';
    } else if (diffDays <= 365) {
        return 'months';
    } else {
        return 'years';
    }
}

/**
 * Función para procesar datos en formato lineal para gráficos.
 * @param {Array<object>} data - Datos con fechas y cantidades.
 * @param {string} valueKey - Clave que representa la cantidad ('quantity' por defecto).
 * @returns {Array<object>} - Array de objetos con fechas y totales sumados por intervalo de tiempo.
 */
export function processLinearGraphData(data, valueKey = 'quantity') {
    const dataWithDates = data.map(item => ({
        ...item,
        date: convertDate(item.date) // Convierte todas las fechas a objetos Date para consistencia
    }));

    dataWithDates.sort((a, b) => a.date - b.date);  // Ordena los datos por fecha

    const interval = determineInterval(dataWithDates); // Determina el intervalo de tiempo adecuado
    const mappedData = {};

    // Mapea los datos sumando las cantidades por intervalo de tiempo
    dataWithDates.forEach(item => {
        const key = formatDate(item.date, interval); // Formatea la fecha según el intervalo
        mappedData[key] = (mappedData[key] || 0) + item[valueKey]; // Suma las cantidades
    });

    // Convierte el objeto mapeado en un array de objetos con fecha y total
    return Object.keys(mappedData).map(key => ({
        date: key,
        total: mappedData[key]
    }));
}

/**
 * Función para generar claves de fechas entre startDate y endDate según un intervalo de tiempo dado.
 * @param {Date} startDate - Fecha de inicio.
 * @param {Date} endDate - Fecha de fin.
 * @param {string} interval - Intervalo de tiempo ('days', 'weeks', 'months', 'years').
 * @returns {object} - Objeto con claves de fecha y valores iniciales para ingresos y gastos.
 */
function generateDateKeys(startDate, endDate, interval) {
    let currentDate = new Date(startDate.getTime()); // Inicializa la fecha actual con startDate
    const keys = {};

    // Genera claves de fecha y valores iniciales para ingresos y gastos
    while (currentDate <= endDate) {
        const key = formatDate(currentDate, interval); // Formatea la fecha según el intervalo
        keys[key] = { income: 0, spent: 0 }; // Inicializa el objeto con ingreso y gasto a 0
        if (interval === 'days') {
            currentDate.setDate(currentDate.getDate() + 1); // Avanza al siguiente día
        } else if (interval === 'weeks') {
            currentDate.setDate(currentDate.getDate() + 7); // Avanza 7 días (semana)
        } else if (interval === 'months') {
            currentDate.setMonth(currentDate.getMonth() + 1); // Avanza un mes
        } else if (interval === 'years') {
            currentDate.setFullYear(currentDate.getFullYear() + 1); // Avanza un año
        }
    }

    return keys; // Retorna el objeto con las claves de fecha generadas
}

/**
 * Función para procesar datos financieros sumando ingresos y gastos por intervalo de tiempo.
 * @param {Array<object>} data - Datos financieros con fechas, tipos (INGRESO/GASTO) y cantidades.
 * @returns {Array<object>} - Array de objetos con fecha, ingresos y gastos sumados por intervalo de tiempo.
 */
export function processFinancialData(data) {
    if (!data || data.length === 0) {
        console.log("No se proporcionaron datos o los datos están vacíos");
        return []; // Retorna un array vacío si no hay datos
    }

    const dataWithDates = data.map(item => ({
        ...item,
        date: convertDate(item.date) // Convierte todas las fechas a objetos Date para consistencia
    }));

    dataWithDates.sort((a, b) => a.date - b.date); // Ordena los datos por fecha

    const interval = determineInterval(dataWithDates); // Determina el intervalo de tiempo adecuado
    const startDate = dataWithDates[0].date; // Fecha de inicio
    const endDate = dataWithDates[dataWithDates.length - 1].date; // Fecha de fin
    const financialData = generateDateKeys(startDate, endDate, interval); // Genera claves de fecha entre startDate y endDate

    // Asegura que cada clave tenga un objeto inicializado
    Object.keys(financialData).forEach(key => {
        financialData[key] = financialData[key] || { income: 0, spent: 0 };
    });

    // Procesa los datos financieros sumando ingresos y gastos por intervalo de tiempo
    dataWithDates.forEach(item => {
        const key = formatDate(item.date, interval); // Formatea la fecha según el intervalo
        financialData[key] = financialData[key] || { income: 0, spent: 0 };
        if (item.type === "INCOME") {
            financialData[key].income += item.quantity; // Suma ingresos
        } else if (item.type === "SPENT") {
            financialData[key].spent += item.quantity; // Suma gastos
        }
    });

    // Convierte el objeto financialData en un array de objetos con fecha, ingresos y gastos
    return Object.keys(financialData).map(key => ({
        date: key,
        income: financialData[key].income,
        spent: financialData[key].spent
    }));
}

/**
 * Función para procesar categorías o usuarios sumando cantidades y ordenando de mayor a menor.
 * @param {Array<object>} data - Datos con categorías o usuarios y cantidades.
 * @param {string} key - Clave para acceder a las categorías o usuarios ('category' o 'username').
 * @returns {Array<object>} - Array de objetos ordenado por total de cantidades y agrupado en los primeros 5 elementos más altos y 'Otros'.
 */
export function proccessCategories(data, key) {
    // Suma las cantidades agrupadas por la clave especificada (category o username)
    const sumsByKey = data.reduce((acc, { [key]: keyValue, quantity }) => {
        acc[keyValue] = (acc[keyValue] || 0) + parseFloat(quantity); // Suma cantidades convertidas a número
        return acc;
    }, {});

    // Ordena los elementos por total de cantidades de mayor a menor
    const sortedItems = Object.entries(sumsByKey)
        .sort((a, b) => b[1] - a[1])
        .map(([categoryOrUser, total]) => ({ [key]: categoryOrUser, total }));

    const topItems = sortedItems.slice(0, 5); // Obtiene los primeros 5 elementos ordenados

    const otherTotal = sortedItems.slice(5).reduce((acc, { total }) => acc + total, 0); // Suma los totales restantes

    if (otherTotal > 0) {
        topItems.push({ [key]: 'Otros', total: otherTotal }); // Agrega un elemento 'Otros' con el total de los elementos restantes
    }

    return topItems; // Retorna los elementos ordenados y agrupados
}

/**
 * Función para procesar la selección de filtros y retornar el tipo de filtro y las fechas seleccionadas.
 * @param {object} selection - Selección de filtro con intervalo y fechas seleccionadas.
 * @returns {object} - Objeto con el tipo de filtro (intervalo) y las fechas de inicio y fin en formato 'YYYY-MM-DD'.
 */
export function processFilterSelection(selection) {
    let filterType = selection.interval; // Tipo de intervalo seleccionado
    let startDate = '';
    let endDate = '';

    if (selection.interval === 'custom') {
        startDate = formatAsYYYYMMDD(selection.dates.start); // Formatea la fecha de inicio
        endDate = formatAsYYYYMMDD(selection.dates.end); // Formatea la fecha de fin
        filterType = 'custom'; // Establece el tipo de filtro como 'custom' si es personalizado
    }

    return { filterType, startDate, endDate }; // Retorna el tipo de filtro y las fechas formateadas
}

/**
 * Función para obtener la representación textual de un tipo de operación ('INCOME' o 'SPENT').
 * @param {string} type - Tipo de operación ('INCOME' o 'SPENT').
 * @returns {string} - Representación textual de la operación ('Ingreso' o 'Gasto').
 */
export function getTypeRepresentation(type) {
    return type === 'INCOME' ? 'Ingreso' : 'Gasto'; // Retorna 'Ingreso' si type es 'INCOME', 'Gasto' si es 'SPENT'
}

/**
 * Función para añadir el símbolo de euro a un valor numérico.
 * @param {number} value - Valor numérico al que se le añadirá el símbolo de euro.
 * @returns {string|number} - Valor formateado con el símbolo de euro, o el mismo valor si no es un número válido.
 */
export function addEuroSymbol(value) {
    if (!isNaN(value)) {
        return value.toFixed(2) + " €"; // Retorna el valor formateado con dos decimales y el símbolo de euro
    } else {
        return value; // Retorna el valor original si no es un número válido
    }
}

/**
 * Función para determinar el tipo inicial de una operación ('Ingreso' o 'Gasto').
 * @param {object|null} operationToEdit - Operación a editar, si existe.
 * @returns {string} - Tipo inicial de la operación ('Ingreso' o 'Gasto') o una cadena vacía si no se proporciona operationToEdit.
 */
export function determineInitialType(operationToEdit) {
    if (operationToEdit) {
        return operationToEdit.type === 'INCOME' ? 'Ingreso' : 'Gasto'; // Retorna 'Ingreso' si type es 'INCOME', 'Gasto' si es 'SPENT'
    }
    return ''; // Retorna una cadena vacía si operationToEdit no está definido
}

