export function formatDateToDDMMYYYY(date) {
    const d = new Date(date);
    let day = '' + d.getDate();
    let month = '' + (d.getMonth() + 1);
    const year = d.getFullYear();

    if (day.length < 2)
        day = '0' + day;
    if (month.length < 2)
        month = '0' + month;

    return [day, month, year].join('-');
}

export function formatAsYYYYMMDD (date) {
    if (!date) return null;
    const d = new Date(date);
    const month = `${d.getMonth() + 1}`.padStart(2, '0'); // Meses son de 0-11
    const day = `${d.getDate()}`.padStart(2, '0');
    return `${d.getFullYear()}-${month}-${day}`;
}

function convertDate(date) {
    return typeof date === 'string' ? new Date(date.split('-').reverse().join('-')) : new Date(date);
}

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


function determineInterval(dataWithDates) {
    const minDate = dataWithDates[0].date;
    const maxDate = dataWithDates[dataWithDates.length - 1].date;
    const diffTime = Math.abs(maxDate - minDate);
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

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


export function processLinearGraphData(data, valueKey = 'quantity') {
    const dataWithDates = data.map(item => ({
        ...item,
        date: convertDate(item.date)
    }));

    dataWithDates.sort((a, b) => a.date - b.date);  // Ordenar por fecha

    const interval = determineInterval(dataWithDates);
    const mappedData = {};

    dataWithDates.forEach(item => {
        const key = formatDate(item.date, interval);
        mappedData[key] = (mappedData[key] || 0) + item[valueKey];
    });

    return Object.keys(mappedData).map(key => ({
        date: key,
        total: mappedData[key]
    }));
}

function generateDateKeys(startDate, endDate, interval) {
    let currentDate = new Date(startDate.getTime());
    const keys = {};

    while (currentDate <= endDate) {
        const key = formatDate(currentDate, interval);
        keys[key] = { income: 0, spent: 0 };
        if (interval === 'days') {
            currentDate.setDate(currentDate.getDate() + 1);
        } else if (interval === 'weeks') {
            currentDate.setDate(currentDate.getDate() + 7);
        } else if (interval === 'months') {
            currentDate.setMonth(currentDate.getMonth() + 1);
        } else if (interval === 'years') {
            currentDate.setFullYear(currentDate.getFullYear() + 1);
        }
    }

    return keys;
}

export function processFinancialData(data) {
    if (!data || data.length === 0) {
        console.log("No data provided or data is empty");
        return [];
    }

    const dataWithDates = data.map(item => ({
        ...item,
        date: convertDate(item.date) // Assuming convertDate is a function that parses dates correctly
    }));

    dataWithDates.sort((a, b) => a.date - b.date);

    const interval = determineInterval(dataWithDates); // Ensure this function provides a valid interval
    const startDate = dataWithDates[0].date;
    const endDate = dataWithDates[dataWithDates.length - 1].date;
    const financialData = generateDateKeys(startDate, endDate, interval); // Presumably this initializes keys between startDate and endDate

    // Ensure every key has an initialized object
    Object.keys(financialData).forEach(key => {
        financialData[key] = financialData[key] || { income: 0, spent: 0 };
    });

    dataWithDates.forEach(item => {
        const key = formatDate(item.date, interval); // Ensure formatDate returns a string that matches keys in financialData
        financialData[key] = financialData[key] || { income: 0, spent: 0 };
        if (item.type === "INCOME") {
            financialData[key].income += item.quantity;
        } else if (item.type === "SPENT") {
            financialData[key].spent += item.quantity;
        }
    });

    return Object.keys(financialData).map(key => ({
        date: key,
        income: financialData[key].income,
        spent: financialData[key].spent
    }));
}


export function proccessCategories(data, key) {
    const sumsByKey = data.reduce((acc, { [key]: keyValue, quantity }) => {
        acc[keyValue] = (acc[keyValue] || 0) + parseFloat(quantity);
        return acc;
    }, {});

    const sortedItems = Object.entries(sumsByKey)
        .sort((a, b) => b[1] - a[1])
        .map(([categoryOrUser, total]) => ({ [key]: categoryOrUser, total }));

    const topItems = sortedItems.slice(0, 5);
    const otherTotal = sortedItems.slice(5).reduce((acc, { total }) => acc + total, 0);

    if (otherTotal > 0) {
        topItems.push({ [key]: 'Otros', total: otherTotal });
    }

    return topItems;
}

export function processFilterSelection(selection) {
    let filterType = selection.interval;
    let startDate = '';
    let endDate = '';

    if (selection.interval === 'custom') {
        startDate = formatAsYYYYMMDD(selection.dates.start);
        endDate = formatAsYYYYMMDD(selection.dates.end);
        filterType = 'custom';
    }

    return { filterType, startDate, endDate };
}

export function getTypeRepresentation(type) {
    return type === 'INCOME' ? 'Ingreso' : 'Gasto';
}

export function addEuroSymbol(value) {
    if (!isNaN(value)) {
        return value.toFixed(2) + " €";
    } else {
        return value;
    }
}

export function determineInitialType(operationToEdit) {
    if (operationToEdit) {
        return operationToEdit.type === 'INCOME' ? 'Ingreso' : 'Gasto';
    }
    return '';
}


