import Chart from 'chart.js/auto';

export function createChart(ctx, chartType, data, defaultOptions, customOptions = {}) {

    const mergedOptions = {...defaultOptions, ...customOptions};

    return new Chart(ctx, {
        type: chartType,
        data: data,
        options: mergedOptions
    });
}