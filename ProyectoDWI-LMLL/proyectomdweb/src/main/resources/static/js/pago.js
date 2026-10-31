/* =========================================================
   LÓGICA DE PAGO Y AUTOCOMPLETADO POR DNI (LMLL)
   ========================================================= */

document.addEventListener("DOMContentLoaded", function() {
    const contenedorResumen = document.getElementById("pago-productos-lista");
    const totalSpan         = document.getElementById("pago-total");
    const checkoutForm      = document.getElementById("checkout-form");
    const inputTotalHidden  = document.getElementById("input-total-hidden");

    // Elementos de DNI y autocompletado
    const inputDni        = document.getElementById("clienteDni");
    const btnBuscarDni    = document.getElementById("btn-buscar-dni");
    const spinnerDni      = document.getElementById("spinner-dni");
    const iconBuscarDni   = document.getElementById("icon-buscar-dni");
    const feedbackDni     = document.getElementById("dni-feedback");
    const feedbackTexto   = document.getElementById("dni-feedback-text");
    const badgeDni        = document.getElementById("dni-badge");

    // Campos de destino a autocompletar
    const inputNombres    = document.getElementById("clienteNombres");
    const inputApellidos  = document.getElementById("clienteApellidos");
    const inputNombre     = document.getElementById("clienteNombre");
    const inputEmail      = document.getElementById("clienteEmail");
    const inputTelefono   = document.getElementById("clienteTelefono");
    const inputDireccion  = document.getElementById("clienteDireccion");

    let carrito = [];

    //-- 1. FUNCIÓN DE CONSULTA Y AUTOCOMPLETADO POR DNI --//
    async function consultarDni(dni) {
        if (!dni) return;
        const dniLimpio = dni.toString().trim().replace(/\D/g, "");
        if (dniLimpio.length !== 8) {
            if (feedbackDni && feedbackTexto) {
                feedbackTexto.textContent = "El DNI debe contener exactamente 8 dígitos.";
                feedbackDni.className = "mt-2 text-warning small";
                feedbackDni.classList.remove("d-none");
            }
            return;
        }

        // Mostrar estado de búsqueda
        if (spinnerDni) spinnerDni.classList.remove("d-none");
        if (iconBuscarDni) iconBuscarDni.classList.add("d-none");
        if (feedbackDni) feedbackDni.classList.add("d-none");

        try {
            const resp = await fetch("/api/consulta/dni/" + dniLimpio);
            if (!resp.ok) {
                throw new Error("No se pudo obtener información del DNI");
            }

            const data = await resp.json();
            if (data && data.encontrado) {
                // Autocompletar solo nombres y apellidos según solicitud del usuario
                if (inputNombres && data.nombres) {
                    inputNombres.value = data.nombres;
                    inputNombres.dispatchEvent(new Event("input", { bubbles: true }));
                }
                if (inputApellidos && data.apellidos) {
                    inputApellidos.value = data.apellidos;
                    inputApellidos.dispatchEvent(new Event("input", { bubbles: true }));
                }
                if (inputNombre) {
                    inputNombre.value = (data.nombre || ((data.nombres || "") + " " + (data.apellidos || "")).trim());
                }

                // Animación visual de campos autocompletados
                [inputNombres, inputApellidos].forEach(el => {
                    if (el) {
                        el.style.transition = "all 0.3s ease";
                        el.style.borderColor = "#28a745";
                        el.style.backgroundColor = "rgba(40, 167, 69, 0.08)";
                        setTimeout(() => {
                            el.style.borderColor = "";
                            el.style.backgroundColor = "";
                        }, 2500);
                    }
                });

                if (badgeDni) {
                    badgeDni.style.display = "inline-flex";
                    badgeDni.innerHTML = '<i class="bi bi-check-circle-fill me-1"></i> ' + (data.origen || 'Verificado');
                }

                if (feedbackDni && feedbackTexto) {
                    feedbackTexto.textContent = "✓ Datos de " + (data.nombre || (data.nombres + ' ' + data.apellidos)) + " obtenidos de " + (data.origen || 'RENIEC') + ".";
                    feedbackDni.className = "mt-2 text-success small";
                    feedbackDni.classList.remove("d-none");
                }

                // Enfocar teléfono para que el cliente ingrese su teléfono manualmente
                if (inputTelefono && !inputTelefono.value) {
                    inputTelefono.focus();
                }
            } else {
                if (feedbackDni && feedbackTexto) {
                    feedbackTexto.textContent = data.mensaje || "DNI no encontrado en RENIEC. Puedes ingresar los datos manualmente.";
                    feedbackDni.className = "mt-2 text-warning small";
                    feedbackDni.classList.remove("d-none");
                }
                if (badgeDni) badgeDni.style.display = "none";
                if (inputNombres) inputNombres.focus();
            }
        } catch (err) {
            console.error("Error al consultar DNI:", err);
            if (feedbackDni && feedbackTexto) {
                feedbackTexto.textContent = "Error de conexión al consultar DNI. Por favor ingresa los datos manualmente.";
                feedbackDni.className = "mt-2 text-warning small";
                feedbackDni.classList.remove("d-none");
            }
        } finally {
            if (spinnerDni) spinnerDni.classList.add("d-none");
            if (iconBuscarDni) iconBuscarDni.classList.remove("d-none");
        }
    }
            if (iconBuscarDni) iconBuscarDni.classList.remove("d-none");
        }
    }

    //-- EVENTOS DEL CAMPO DNI --//
    if (inputDni) {
        let timerDni = null;

        inputDni.addEventListener("input", function(e) {
            // Permitir solo números y máximo 8 dígitos
            this.value = this.value.replace(/\D/g, "").slice(0, 8);
            clearTimeout(timerDni);

            if (this.value.length === 8) {
                timerDni = setTimeout(() => {
                    consultarDni(this.value);
                }, 450);
            } else {
                if (badgeDni) badgeDni.style.display = "none";
                if (feedbackDni) feedbackDni.classList.add("d-none");
            }
        });

        inputDni.addEventListener("keydown", function(e) {
            if (e.key === "Enter") {
                e.preventDefault();
                if (this.value.length === 8) {
                    consultarDni(this.value);
                }
            }
        });

        if (btnBuscarDni) {
            btnBuscarDni.addEventListener("click", function(e) {
                e.preventDefault();
                consultarDni(inputDni.value);
            });
        }

        // Si ya hay un DNI en el campo al cargar y el nombre está vacío, consultar automáticamente
        if (inputDni.value && inputDni.value.trim().length === 8) {
            consultarDni(inputDni.value.trim());
        }
    }

    //-- 2. CARGA DE PRODUCTOS DE LA BOLSA --//
    function cargarResumen() {
        if (!contenedorResumen || !totalSpan) return;

        try {
            const data = localStorage.getItem("carritoLlama");
            if (data) {
                carrito = JSON.parse(data);
            }
        } catch (e) {
            carrito = [];
        }

        if (!carrito || carrito.length === 0) {
            contenedorResumen.innerHTML = `
                <div class="alert alert-dark text-center p-3 mb-3 border-secondary" style="background: rgba(255,255,255,0.03);">
                    <i class="bi bi-bag-x text-gold fs-3 d-block mb-2"></i>
                    <p class="mb-2 text-white small">Tu bolsa de compra está vacía.</p>
                    <a href="/productos" class="btn btn-sm btn-outline-warning" style="font-size: 0.8rem;">
                        <i class="bi bi-bag-plus me-1"></i> Explorar Catálogo
                    </a>
                </div>
            `;
            totalSpan.textContent = "0.00";
            if (inputTotalHidden) inputTotalHidden.value = "0.00";
            return;
        }

        renderizarItems();
    }

    function renderizarItems() {
        if (!contenedorResumen || !totalSpan) return;
        contenedorResumen.innerHTML = "";
        let total = 0;

        carrito.forEach(item => {
            const precio = parseFloat(item.precio) || 0;
            const cantidad = parseInt(item.cantidad) || 1;
            const subtotal = precio * cantidad;
            total += subtotal;

            const div = document.createElement("div");
            div.className = "d-flex justify-content-between align-items-center mb-3 pb-2 border-bottom border-secondary border-opacity-25";
            div.innerHTML = `
                <div class="d-flex align-items-center gap-3">
                    <img src="${item.imagen || '/img/logo.png'}" alt="${item.nombre}" style="width: 50px; height: 50px; object-fit: cover; border-radius: 6px; border: 1px solid var(--border-2, #333);">
                    <div>
                        <h6 class="mb-0 text-white" style="font-size: 0.92rem;">${item.nombre}</h6>
                        <small class="text-muted" style="font-size: 0.78rem;">Cant: ${cantidad} ${item.talla ? ' | Talla: ' + item.talla : ''}</small>
                    </div>
                </div>
                <span class="fw-bold text-gold" style="font-size: 0.95rem;">
                    S/ ${subtotal.toFixed(2)}
                </span>
            `;
            contenedorResumen.appendChild(div);
        });

        totalSpan.textContent = total.toFixed(2);
        if (inputTotalHidden) {
            inputTotalHidden.value = total.toFixed(2);
        }
    }

    //-- 3. VALIDACIÓN Y ENVÍO DEL FORMULARIO --//
    if (checkoutForm) {
        checkoutForm.addEventListener("submit", function(e) {
            // Asegurar que el total esté en el input oculto
            if (inputTotalHidden && totalSpan) {
                inputTotalHidden.value = totalSpan.textContent.trim();
            }

            if (!checkoutForm.checkValidity()) {
                e.preventDefault();
                e.stopPropagation();
            }

            checkoutForm.classList.add("was-validated");
        });
    }

    //-- 4. AUTOCOMPLETADO INTELIGENTE DE CORREO Y PREFIJO DE DIRECCIÓN --//
    if (inputEmail) {
        inputEmail.addEventListener("blur", function() {
            let val = this.value.trim();
            if (val && !val.includes("@") && !val.includes(" ")) {
                this.value = val + "@gmail.com";
            }
        });
        inputEmail.addEventListener("input", function() {
            let val = this.value;
            if (val.endsWith("@") && !val.slice(0, -1).includes("@")) {
                this.value = val + "gmail.com";
            }
        });
    }

    if (inputDireccion) {
        if (!inputDireccion.value || inputDireccion.value.trim() === "") {
            inputDireccion.value = "Av. ";
        }
        inputDireccion.addEventListener("focus", function() {
            if (!this.value || this.value.trim() === "") {
                this.value = "Av. ";
            }
        });
    }

    cargarResumen();
});
