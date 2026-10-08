/* ==================================
   LÓGICA DE GENERAL DE LA PAGINA WEB
   ================================== */
document.addEventListener("DOMContentLoaded", () => {
    // -- POPUP PROMO --//
    const PROMO_KEY = "lmllPromoSeenAt";
    const PROMO_INTERVAL_DAYS = 14;

    function initPromoModal() {
        const popup = document.getElementById("promo-modal");
        if (!popup) return;

        const closeButtons = popup.querySelectorAll("#close-modal, .close-btn, .btn-lmll");
        const lastShown = Number(localStorage.getItem(PROMO_KEY) || 0);
        const daysSinceShown = (Date.now() - lastShown) / (1000 * 60 * 60 * 24);
        const shouldShow = !lastShown || daysSinceShown >= PROMO_INTERVAL_DAYS;

        function closePromo() {
            popup.style.display = "none";
            popup.setAttribute("aria-hidden", "true");
            localStorage.setItem(PROMO_KEY, String(Date.now()));
        }

        if (shouldShow) {
            window.setTimeout(() => {
                popup.style.display = "flex";
                popup.setAttribute("aria-hidden", "false");
            }, 900);
        } else {
            popup.style.display = "none";
            popup.setAttribute("aria-hidden", "true");
        }

        closeButtons.forEach((button) => button.addEventListener("click", closePromo));
        popup.addEventListener("click", (event) => {
            if (event.target === popup) closePromo();
        });
        document.addEventListener("keydown", (event) => {
            if (event.key === "Escape" && popup.style.display === "flex") closePromo();
        });
    }

    //-- BANNER CARRUSEL  --//
    (function () {
        const images = document.querySelectorAll('.banner-image');
        const dots   = document.querySelectorAll('.banner-dot');
        let current = 0;

        function goTo(index) {
            images[current].classList.remove('active');
            dots[current].classList.remove('active');
            current = (index + images.length) % images.length;
            images[current].classList.add('active');
            dots[current].classList.add('active');
        }

        let timer = setInterval(() => goTo(current + 1), 7000);

        dots.forEach((dot, i) => {
            dot.addEventListener('click', () => {
                clearInterval(timer);
                goTo(i);
                timer = setInterval(() => goTo(current + 1), 7000);
            });
        });
    })();

    // -- STAT PRODUCTOS --//
    (function () {
        const el = document.getElementById('stat-productos');
        const cnt = parseInt(localStorage.getItem('cantidadProductos')) || 0;
        if (el) el.textContent = cnt > 0 ? cnt : '24';
    })();
    //-- FRASE DINÁMICA --//
    (function () {
        const el = document.getElementById('texto-dinamico');
        if (!el) return;

        const cant = parseInt(localStorage.getItem('cantidadProductos')) || 0;
        const frases = [
            'Actualmente contamos con <strong>' + cant + ' productos</strong>. ¡Aprovecha hoy!',
            '"La moda no es un hobby, es un <em>estilo de vida</em>."',
            'Promoción limitada: compra dos y llévate el segundo a solo <strong>S/ 449</strong>',
            '¿Tienes dudas? <a href="carrito.html" style="color:var(--accent-2);text-decoration:underline;">Contáctanos aquí</a>'
        ];
        let idx = 0;

        function siguiente() {
            el.style.opacity = '0';
            setTimeout(() => {
                idx = (idx + 1) % frases.length;
                el.innerHTML = frases[idx];
                el.style.opacity = '1';
            }, 400);
        }

        el.style.transition = 'opacity .4s';
        el.innerHTML = frases[0];
        setInterval(siguiente, 7000);
    })();

    /* ===============
       CARRITO LATERAL 
       =============== */
    const botonesComprar    = document.querySelectorAll(".product-overlay button");
    const carritoPanel      = document.getElementById("carrito-panel");
    const carritoToggle     = document.getElementById("carrito-toggle");
    const cerrarCarrito     = document.getElementById("cerrar-carrito");
    const contenedorItems   = document.getElementById("carrito-items");
    const totalSpan         = document.getElementById("carrito-total");
    const btnVerCarrito     = document.getElementById("carrito-ver");

    let carrito = [];

    //-- PERSISTENCIA --//
    function cargarCarrito() {
        const data = localStorage.getItem("carritoLlama");
        if (data) {
            try {
                carrito = JSON.parse(data) || [];
            } catch (e) {
                carrito = [];
            }
        }
    }

    function guardarCarrito() {
        localStorage.setItem("carritoLlama", JSON.stringify(carrito));
    }

    //-- LÓGICA DE INTERFAZ --//
    function abrirCarrito() {
        if (carritoPanel) carritoPanel.style.right = "0";
    }

    function cerrarCarritoPanel() {
        if (carritoPanel) carritoPanel.style.right = "-320px";
    }

    function actualizarCarrito() {
        if (!contenedorItems || !totalSpan) {
            guardarCarrito();
            return;
        }

        contenedorItems.innerHTML = "";
        let total = 0;

        if (carrito.length === 0) {
            contenedorItems.innerHTML = `
            <p style="font-size:.82rem; color:#888; text-align:center; padding:1.5rem 0;">
                Tu bolsa está vacía.<br>¡Añade algo especial!
            </p>`;
        }

        carrito.forEach(function (item, index) {
            const subtotal = item.precio * item.cantidad;
            total += subtotal;

            const div = document.createElement("div");
            div.className = "carrito-item-row";
            div.style.borderBottom = "1px solid #eee";
            div.style.padding = "10px 0";
            div.dataset.index = index;

            div.innerHTML = `
                <div style="display:flex; gap:10px; align-items:center;">
                    <img src="${item.imagen}" style="width:50px; height:50px; object-fit:cover; border-radius:4px;">
                        <div style="flex-grow:1;">
                            <strong style="display:block; font-size:0.9rem;">${item.nombre}</strong>
                            <small>S/ ${item.precio.toFixed(2)}</small>
                        </div>
                </div>
                <div style="display:flex; justify-content:space-between; align-items:center; margin-top:8px;">
                    <div class="cant-controls">
                        <button class="btn-restar btn btn-sm btn-light">-</button>
                            <span class="mx-2">${item.cantidad}</span>
                        <button class="btn-sumar btn btn-sm btn-light">+</button>
                    </div>
                        <button class="btn-eliminar text-danger" style="background:none; border:none; font-size:0.8rem;">Eliminar</button>
                </div>
            `;
            contenedorItems.appendChild(div);
        });

        totalSpan.textContent = total.toFixed(2);
        guardarCarrito();
    }

    //-- AGREGAR PRODUCTOS (COMPATIBLE CON AJAX) --//
    document.addEventListener("click", function (e) {
        // Verifica si el click provino de un botón de "Añadir a bolsa" o su icono interno
        const botonComprar = e.target.closest(".product-overlay button");

        if (botonComprar) {
            e.preventDefault();

            // Busca la tarjeta contenedora para extraer info
            const card   = botonComprar.closest(".product-card-lmll");
            const nombre = card.querySelector(".product-title").textContent.trim();

            let precioTexto  = card.querySelector(".product-price").textContent;
            let precioLimpio = precioTexto.replace("S/", "").trim().split(" ")[0];

            const precio = parseFloat(precioLimpio) || 0;
            const imagen = card.querySelector("img").src;

            // Logica para agregar al array
            let encontrado = carrito.find(item => item.nombre === nombre);

            if (encontrado) {
                encontrado.cantidad += 1;
            } else {
                carrito.push({ nombre, precio, cantidad: 1, imagen });
            }

            actualizarCarrito();
            abrirCarrito();
        }
    });

    //-- EVENTOS DE BOTONES DEL PANEL --//
    if (carritoToggle) {
        carritoToggle.addEventListener("click", () => {
            const isClosed = carritoPanel.style.right === "-320px" || carritoPanel.style.right === "";
            isClosed ? abrirCarrito() : cerrarCarritoPanel();
        });
    }

    if (cerrarCarrito) cerrarCarrito.addEventListener("click", cerrarCarritoPanel);

    if (btnVerCarrito) {
        btnVerCarrito.addEventListener("click", () => window.location.href = "/carrito");
    }

    if (contenedorItems) {
        contenedorItems.addEventListener("click", function (e) {
            const target = e.target;
            const itemDiv = target.closest("div[data-index]");
            if (!itemDiv) return;
            const index = parseInt(itemDiv.dataset.index);

            if (target.classList.contains("btn-sumar")) {
                carrito[index].cantidad += 1;
            } else if (target.classList.contains("btn-restar")) {
                carrito[index].cantidad--;
                if (carrito[index].cantidad <= 0) carrito.splice(index, 1);
            } else if (target.classList.contains("btn-eliminar")) {
                carrito.splice(index, 1);
            }
            actualizarCarrito();
        });
    }

    //-- Inicialización --//
    cargarCarrito();
    actualizarCarrito();
    initPromoModal();

});