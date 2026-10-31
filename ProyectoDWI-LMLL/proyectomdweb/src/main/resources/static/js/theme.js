/* =========================================================
   CONTROLADOR DE TEMA: MODO CLARO Y MODO OSCURO (LMLL)
   ========================================================= */

(function () {
    // 1. Aplicar tema guardado inmediatamente al cargar para evitar parpadeos
    const temaGuardado = localStorage.getItem("lmll_theme") || "dark";
    if (temaGuardado === "light") {
        document.documentElement.setAttribute("data-theme", "light");
    } else {
        document.documentElement.removeAttribute("data-theme");
    }

    function actualizarIconos(esClaro) {
        const botones = document.querySelectorAll(".btn-theme-toggle");
        botones.forEach(btn => {
            const icon = btn.querySelector("i");
            if (icon) {
                if (esClaro) {
                    icon.className = "bi bi-sun-fill text-warning";
                    btn.setAttribute("title", "Cambiar a modo oscuro");
                    btn.setAttribute("aria-label", "Cambiar a modo oscuro");
                } else {
                    icon.className = "bi bi-moon-stars";
                    btn.setAttribute("title", "Cambiar a modo claro");
                    btn.setAttribute("aria-label", "Cambiar a modo claro");
                }
            }
        });
    }

    document.addEventListener("DOMContentLoaded", function () {
        const esClaro = document.documentElement.getAttribute("data-theme") === "light";
        actualizarIconos(esClaro);

        // Delegación de eventos para cualquier botón de cambio de tema
        document.querySelectorAll(".btn-theme-toggle").forEach(btn => {
            btn.addEventListener("click", function (e) {
                e.preventDefault();
                const actual = document.documentElement.getAttribute("data-theme") === "light";
                const nuevoTema = actual ? "dark" : "light";

                if (nuevoTema === "light") {
                    document.documentElement.setAttribute("data-theme", "light");
                } else {
                    document.documentElement.removeAttribute("data-theme");
                }

                localStorage.setItem("lmll_theme", nuevoTema);
                actualizarIconos(nuevoTema === "light");
            });
        });
    });
})();
