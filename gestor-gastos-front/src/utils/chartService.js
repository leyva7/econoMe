import Chart from 'chart.js/auto'; // Importa la librería Chart.js, que se asume está instalada como 'chart.js/auto'

/**
 * Función para crear un gráfico utilizando Chart.js.
 * @param {CanvasRenderingContext2D} ctx - Contexto del lienzo HTML donde se renderizará el gráfico.
 * @param {string} chartType - Tipo de gráfico ('bar', 'line', 'pie', etc.).
 * @param {object} data - Datos del gráfico en formato adecuado para Chart.js.
 * @param {object} defaultOptions - Opciones por defecto para el gráfico.
 * @param {object} customOptions - Opciones personalizadas que sobrescriben las opciones por defecto.
 * @returns {Chart} - Instancia del gráfico Chart.js creada.
 */
export function createChart(ctx, chartType, data, defaultOptions, customOptions = {}) {
    // Combina las opciones por defecto con las opciones personalizadas
    const mergedOptions = {...defaultOptions, ...customOptions};

    // Crea y retorna una nueva instancia de Chart.js con el contexto, tipo de gráfico, datos y opciones combinadas
    return new Chart(ctx, {
        type: chartType,
        data: data,
        options: mergedOptions
    });
}
