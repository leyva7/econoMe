
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


export function processLinearGraphData(data) {
    // Create a copy of the data with new Date objects to avoid modifying the original data
    const dataCopy = data.map(item => ({
        ...item,
        date: typeof item.date === 'string' ? new Date(item.date.split('-').reverse().join('-')) : new Date(item.date)
    }));

    let firstDayOfWeek;

    // Sort by date
    dataCopy.sort((a, b) => a.date - b.date);

    const minDate = dataCopy[0].date;
    const maxDate = dataCopy[dataCopy.length - 1].date;
    const diffTime = Math.abs(maxDate - minDate);
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    let interval;
    if (diffDays <= 7) {
        interval = 'days';
    } else if (diffDays <= 30) {
        interval = 'weeks';
    } else if (diffDays <= 365) {
        interval = 'months';
    } else {
        interval = 'years';
    }

    const mappedData = {};
    dataCopy.forEach(item => {
        let key;
        switch (interval) {
            case 'days':
                key = `${String(item.date.getDate()).padStart(2, '0')}-${String(item.date.getMonth() + 1).padStart(2, '0')}`;
                break;
            case 'weeks':
                firstDayOfWeek = new Date(item.date);
                firstDayOfWeek.setDate(item.date.getDate() - item.date.getDay());
                key = `${String(firstDayOfWeek.getDate()).padStart(2, '0')}-${String(firstDayOfWeek.getMonth() + 1).padStart(2, '0')}`;
                break;
            case 'months':
                key = `${String(item.date.getMonth() + 1).padStart(2, '0')}-${item.date.getFullYear()}`;
                break;
            case 'years':
                key = `${item.date.getFullYear()}`;
                break;
        }
        mappedData[key] = (mappedData[key] || 0) + item.quantity;
    });

    return Object.keys(mappedData).map(key => ({
        date: key,
        total: mappedData[key]
    }));
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

