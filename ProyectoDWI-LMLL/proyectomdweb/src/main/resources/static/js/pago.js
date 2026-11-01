/* =========================================================
   LOGICA DE PAGO Y AUTOCOMPLETADO POR DNI RENIEC (LMLL)
   ========================================================= */

function sincronizarNombreCompleto() {
    const elNombres = document.getElementById("clienteNombres");
    const elApellidos = document.getElementById("clienteApellidos");
    const elNombre = document.getElementById("clienteNombre");
    const n = (elNombres ? elNombres.value : "").trim();
    const a = (elApellidos ? elApellidos.value : "").trim();
    if (elNombre) {
        elNombre.value = (n + " " + a).trim();
    }
}
window.sincronizarNombreCompleto = sincronizarNombreCompleto;

function autocompletarDominio(dom) {
    const emailInput = document.getElementById("clienteEmail");
    if (!emailInput) return;
    let val = emailInput.value.trim();
    if (!val) {
        emailInput.value = dom;
        emailInput.focus();
        return;
    }
    if (val.includes("@")) {
        val = val.substring(0, val.indexOf("@"));
    }
    emailInput.value = val + dom;
    emailInput.focus();
}
window.autocompletarDominio = autocompletarDominio;

async function consultarDni(dni) {
    const inputDni = document.getElementById("clienteDni");
    const spinnerDni = document.getElementById("spinner-dni");
    const iconBuscarDni = document.getElementById("icon-buscar-dni");
    const feedbackDni = document.getElementById("dni-feedback");
    const feedbackTexto = document.getElementById("dni-feedback-text");
    const badgeDni = document.getElementById("dni-badge");
    const inputNombres = document.getElementById("clienteNombres");
    const inputApellidos = document.getElementById("clienteApellidos");
    const inputNombre = document.getElementById("clienteNombre");
    const inputTelefono = document.getElementById("clienteTelefono");

    const valDni = (dni || (inputDni ? inputDni.value : "")).toString().trim().replace(/\D/g, "");
    if (valDni.length !== 8) {
        if (feedbackDni && feedbackTexto) {
            feedbackTexto.textContent = "El DNI debe contener exactamente 8 digitos.";
            feedbackDni.className = "mt-2 text-warning small";
            feedbackDni.classList.remove("d-none");
        }
        return;
    }

    if (spinnerDni) spinnerDni.classList.remove("d-none");
    if (iconBuscarDni) iconBuscarDni.classList.add("d-none");
    if (feedbackDni) feedbackDni.classList.add("d-none");

    try {
        const resp = await fetch("/api/consulta/dni/" + valDni);
        if (!resp.ok) {
            throw new Error("Respuesta no satisfactoria del servidor");
        }

        const data = await resp.json();
        if (data && data.encontrado) {
            if (inputNombres && data.nombres) {
                inputNombres.value = data.nombres;
            }
            if (inputApellidos && data.apellidos) {
                inputApellidos.value = data.apellidos;
            }
            sincronizarNombreCompleto();

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
                const nombreMostrar = data.nombre || ((data.nombres || "") + " " + (data.apellidos || "")).trim();
                feedbackTexto.textContent = "Datos de " + nombreMostrar + " obtenidos de " + (data.origen || 'RENIEC') + ".";
                feedbackDni.className = "mt-2 text-success small";
                feedbackDni.classList.remove("d-none");
            }

            if (inputTelefono && !inputTelefono.value) {
                inputTelefono.focus();
            }
        } else {
            if (feedbackDni && feedbackTexto) {
                feedbackTexto.textContent = (data && data.mensaje) ? data.mensaje : "DNI no encontrado. Puedes ingresar los datos manualmente.";
                feedbackDni.className = "mt-2 text-warning small";
                feedbackDni.classList.remove("d-none");
            }
            if (badgeDni) badgeDni.style.display = "none";
            if (inputNombres) inputNombres.focus();
        }
    } catch (err) {
        console.error("Error al consultar DNI:", err);
        if (feedbackDni && feedbackTexto) {
            feedbackTexto.textContent = "Error de conexion al consultar DNI. Por favor ingresa los datos manualmente.";
            feedbackDni.className = "mt-2 text-warning small";
            feedbackDni.classList.remove("d-none");
        }
    } finally {
        if (spinnerDni) spinnerDni.classList.add("d-none");
        if (iconBuscarDni) iconBuscarDni.classList.remove("d-none");
    }
}
window.consultarDni = consultarDni;
window.ejecutarConsultaDni = function() {
    const inputDni = document.getElementById("clienteDni");
    if (inputDni) consultarDni(inputDni.value);
};

let timerDniAuto = null;
function verificarDniInput(val) {
    const dni = (val || "").replace(/\D/g, "").slice(0, 8);
    const inputDni = document.getElementById("clienteDni");
    if (inputDni && inputDni.value !== dni) {
        inputDni.value = dni;
    }
    clearTimeout(timerDniAuto);
    if (dni.length === 8) {
        timerDniAuto = setTimeout(() => {
            consultarDni(dni);
        }, 350);
    }
}
window.verificarDniInput = verificarDniInput;

document.addEventListener("DOMContentLoaded", function() {
    const contenedorResumen = document.getElementById("pago-productos-lista");
    const totalSpan         = document.getElementById("pago-total");
    const checkoutForm      = document.getElementById("checkout-form");
    const inputTotalHidden  = document.getElementById("input-total-hidden");
    const inputDni          = document.getElementById("clienteDni");
    const btnBuscarDni      = document.getElementById("btn-buscar-dni");
    const inputEmail        = document.getElementById("clienteEmail");
    const inputDireccion    = document.getElementById("clienteDireccion");
    const inputNombres      = document.getElementById("clienteNombres");
    const inputApellidos    = document.getElementById("clienteApellidos");
    const hiddenNombre      = document.getElementById("clienteNombre");

    let carrito = [];

    // Manejo de eventos del campo DNI
    if (inputDni) {
        inputDni.addEventListener("input", function() {
            verificarDniInput(this.value);
        });

        inputDni.addEventListener("keydown", function(e) {
            if (e.key === "Enter") {
                e.preventDefault();
                consultarDni(this.value);
            }
        });

        if (inputDni.value && inputDni.value.trim().length === 8) {
            consultarDni(inputDni.value.trim());
        }
    }

    if (btnBuscarDni) {
        btnBuscarDni.addEventListener("click", function(e) {
            e.preventDefault();
            if (inputDni) consultarDni(inputDni.value);
        });
    }

    // Separar nombre precargado si existe
    if (hiddenNombre && hiddenNombre.value && inputNombres && !inputNombres.value) {
        const partes = hiddenNombre.value.trim().split(" ");
        if (partes.length === 1) {
            inputNombres.value = partes[0];
        } else if (partes.length >= 2) {
            inputNombres.value = partes[0];
            if (inputApellidos) inputApellidos.value = partes.slice(1).join(" ");
        }
    }

    // Autocompletado inteligente de correo
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

    // Autocompletado inteligente de dirección con prefijo 'Av. '
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

    // Cargar productos del carrito
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
                    <p class="mb-2 text-white small">Tu bolsa de compra esta vacia.</p>
                    <a href="/productos" class="btn btn-sm btn-outline-warning" style="font-size: 0.8rem;">
                        <i class="bi bi-bag-plus me-1"></i> Explorar Catalogo
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
                        <small class="text-muted" style="font-size: 0.78rem;">Cant: ${cantidad} ${item.talla ? ' | Talla: ' + item.talla : ''} ${item.color ? ' | Color: ' + item.color : ''}</small>
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

    // Validación y envío del formulario
    if (checkoutForm) {
        checkoutForm.addEventListener("submit", function(e) {
            sincronizarNombreCompleto();
            if (inputTotalHidden && totalSpan) {
                const txt = totalSpan.textContent.trim();
                inputTotalHidden.value = txt !== "" ? txt : "0.00";
            }
            if (!checkoutForm.checkValidity()) {
                e.preventDefault();
                e.stopPropagation();
            }
            checkoutForm.classList.add("was-validated");
        });
    }

    cargarResumen();
});