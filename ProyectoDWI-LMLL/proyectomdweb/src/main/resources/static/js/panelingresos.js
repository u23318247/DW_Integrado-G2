document.addEventListener("DOMContentLoaded", function() {
    
    const filtroTipo = document.getElementById("filtroTipo");
    const filtroMes = document.getElementById("filtroMes");
    const filtroAnio = document.getElementById("filtroAnio");
    const ctx = document.getElementById('ingresosChart').getContext('2d');
    let ingresosChart; 

    // --- CONFIGURACIÓN DINÁMICA DEL TIEMPO --- //
    const fechaActual = new Date();
    const anioActual = fechaActual.getFullYear();
    const mesActual = fechaActual.getMonth() + 1; // JavaScript cuenta los meses de 0 a 11

    // 1. Seleccionar automáticamente el mes actual
    filtroMes.value = mesActual;

    // 2. Generar los años dinámicamente
    const anioAperturaTienda = 2025; 
    
    filtroAnio.innerHTML = '';
    for (let i = anioAperturaTienda; i <= anioActual; i++) {
        const option = document.createElement("option");
        option.value = i;
        option.textContent = i;
        if (i === anioActual) {
            option.selected = true; 
        }
        filtroAnio.appendChild(option);
    }


    filtroTipo.addEventListener("change", function() {
        if (this.value === "anual") {
            filtroMes.style.display = "none";
        } else {
            filtroMes.style.display = "block";
        }
        cargarDatosGrafica();
    });

    filtroMes.addEventListener("change", cargarDatosGrafica);
    filtroAnio.addEventListener("change", cargarDatosGrafica);

    function cargarDatosGrafica() {
        const tipo = filtroTipo.value;
        const anio = filtroAnio.value;
        const mes = filtroMes.value;

        let url = `/admin/estadisticas/api/ingresos/anual?anio=${anio}`;
        if (tipo === "mensual") {
            url = `/admin/estadisticas/api/ingresos/mensual?anio=${anio}&mes=${mes}`;
        }
        fetch(url)
            .then(response => response.json())
            .then(data => {
                const etiquetas = data.map(item => item.etiqueta);
                const montos = data.map(item => item.total);
                renderizarGrafica(etiquetas, montos, tipo);
            })
            .catch(error => console.error("Error cargando la gráfica:", error));
    }

    function renderizarGrafica(etiquetas, montos, tipo) {
        if (ingresosChart) {
            ingresosChart.destroy();
        }

        let gradient = ctx.createLinearGradient(0, 0, 0, 400);
        gradient.addColorStop(0, 'rgba(40, 167, 69, 0.4)');
        gradient.addColorStop(1, 'rgba(40, 167, 69, 0.0)');

        ingresosChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: etiquetas,
                datasets: [{
                    label: 'Ingresos Netos (S/)',
                    data: montos,
                    borderColor: '#28a745',       
                    backgroundColor: gradient,    
                    borderWidth: 2,
                    pointBackgroundColor: '#fff', 
                    pointBorderColor: '#28a745',  
                    pointBorderWidth: 2,
                    pointRadius: 4,
                    pointHoverRadius: 6,
                    fill: true,                   
                    tension: 0.4                  
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: true,
                        position: 'top',
                        labels: { font: { family: "'Batang', serif" } }
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return ' Ingresos: S/ ' + context.parsed.y.toFixed(2);
                            }
                        }
                    }
                },
                scales: {
                    x: { grid: { display: false } },
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) {
                                return 'S/ ' + value;
                            }
                        }
                    }
                }
            }
        });
    }

    cargarDatosGrafica();
});