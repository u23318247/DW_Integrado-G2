/* ==============
   LÓGICA DE PAGO 
   ============== */

document.addEventListener("DOMContentLoaded", function() {
    const contenedorResumen = document.getElementById("pago-productos-lista");
    const totalSpan         = document.getElementById("pago-total");
    const checkoutForm      = document.getElementById("checkout-form");
    const cardDetails       = document.getElementById("card-details");
    const walletDetails     = document.getElementById("wallet-details");
    const radioMetodos      = document.querySelectorAll('input[name="payment_method"]');

    let carrito = [];

    //-- 1. CARGA DATOS DEL LOCAL STORAGE --//

    function cargarResumen() {
        const data = localStorage.getItem("carritoLlama");
        if (data) {
            carrito = JSON.parse(data);
        }

        if (carrito.length === 0) {
            contenedorResumen.innerHTML = "<p>No hay productos en tu bolsa.</p>";
            // Redirige a productos si intentan entrar a pagar con carrito vacío
            setTimeout(() => { window.location.href = "/productos"; }, 2000);
            return;
        }

        renderizarItems();
    }

    //-- 2. MUESTRA PRODUCTOS EN EL LATERAL --//

    function renderizarItems() {
        contenedorResumen.innerHTML = "";
        let total = 0;

        carrito.forEach(item => {
            const subtotal = item.precio * item.cantidad;
            total += subtotal;

            const div = document.createElement("div");
            div.className = "d-flex justify-content-between align-items-center mb-3";
            div.innerHTML = `
                <div class="d-flex align-items-center gap-3">
                    <img src="${item.imagen}" alt="${item.nombre}" style="width: 50px; height: 50px; object-fit: cover; border-radius: 6px; border: 1px solid var(--border-2);">
                    <div>
                        <h6 class="mb-0" style="font-family: 'Batang', serif; font-size: 0.95rem; color: var(--txt);">${item.nombre}</h6>
                            <small class="text-muted-1" style="font-size: 0.8rem;">Cant: ${item.cantidad}</small>
                    </div>
                </div>
                <span class="fw-bold text-brand" style="font-family: 'Batang', serif; font-size: 1rem;">
                    S/ ${subtotal.toFixed(2)}
                </span>
            `;
            contenedorResumen.appendChild(div);
        });

        totalSpan.textContent = total.toFixed(2);
    }

    //-- 3. ALTERNA LA VISIBILIDAD DE LOS METODOS DE PAGO --//

    radioMetodos.forEach(radio => {
        radio.addEventListener("change", function() {
            if (this.value === "wallet") {
                cardDetails.classList.add("d-none");
                walletDetails.classList.remove("d-none");
                cardDetails.querySelectorAll('input').forEach(i => i.required = false);
            } else {
                cardDetails.classList.remove("d-none");
                walletDetails.classList.add("d-none");
                cardDetails.querySelectorAll('input').forEach(i => i.required = true);
            }
        });
    });

    //-- 4. MANEJO DEL ENVIO DEL FORMULARIO --//

    checkoutForm.addEventListener("submit", function(e) {
        // A. Captura el total del texto y lo mete en el input oculto para el controlador
        const totalTexto = totalSpan.textContent;
        document.getElementById('input-total-hidden').value = totalTexto;

        // B. Valida que los campos obligatorios no estén vacíos
        if (!checkoutForm.checkValidity()) {
            e.preventDefault(); 
            e.stopPropagation();
        }
        
        // C. Agrega las clases visuales de Bootstrap (bordes rojos/verdes)
        checkoutForm.classList.add("was-validated");
        // SI TODO ES VÁLIDO: El formulario continuará su curso natural hacia /pago/procesar-local
        
        //-- 5. RECOLECTAR DATOS DEL FORMULARIO --//

        const inputsEnvio = document.querySelectorAll('.form-section:first-of-type input');
        const nombre    = inputsEnvio[0].value;
        const telefono  = inputsEnvio[1].value;
        const direccion = inputsEnvio[2].value;
        const metodoPago  = document.querySelector('input[name="payment_method"]:checked').value;

        const totalPagar = carrito.reduce((acc, item) => acc + (item.precio * item.cantidad), 0);

        // -- 6. ARMAR EL PAQUETE JSON --//

        // Los nombres deben coincidir EXACTAMENTE con el CheckoutRequestDTO en Java
        const payload = {
            clienteNombre: nombre,
            clienteTelefono: telefono,
            clienteDireccion: direccion,
            metodoPago: metodoPago,
            total: parseFloat(totalPagar.toFixed(2)),
            items: carrito.map(item => ({
                nombre: item.nombre,
                precio: parseFloat(item.precio),
                cantidad: parseInt(item.cantidad)
            }))
        };

        // -- 7. EFECTOS VISUALES (Desactiva el botón para evitar doble click) --//

        const btnSubmit     = document.querySelector("button[type='submit']");
        const textoOriginal = btnSubmit.innerHTML;
        btnSubmit.innerHTML = `<span class="spinner-border spinner-border-sm"></span> Procesando...`;
        btnSubmit.disabled = true;

        // -- 8. ENVIAR A SPRING BOOT VÍA AJAX --//

        fetch('/pedidos/procesar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        })
        .then(response => {
            if (response.ok) {
                //* Si el servidor de Java respondió "OK", la venta se guarda en MySQL
                localStorage.removeItem("carritoLlama");
                window.location.href = "/"; // Lleva al inicio de la tienda
            } else {
                //! Si Java detecta un error (ej: falta un dato), lanza error
                throw new Error('El servidor rechazó la operación');
            }
        })
        .catch(error => {
            // Si el servidor está apagado o hubo un error en Java
            console.error("Detalle del error:", error);
            // Muestra el motivo en pantalla
            alert("Error del servidor: \n" + error.message);
            // Restaura el botón a su estado normal
            btnSubmit.innerHTML = textoOriginal;
            btnSubmit.disabled = false;
        });

        checkoutForm.classList.add("was-validated");
    });

    // Formateador de fecha de vencimiento (MM/YY)
    const inputVenc = document.getElementById("input-venc");
    if(inputVenc) {
        inputVenc.addEventListener("input", function(e) {
            let v = this.value.replace(/\D/g, ''); 
            if (v.length > 2) v = v.substring(0,2) + '/' + v.substring(2,4); 
            this.value = v;
        });
    }

    //-- LÓGICA DE AUTOCOMPLETADO POR DNI --//
    const inputDni        = document.getElementById("clienteDni");
    const btnBuscarDni    = document.getElementById("btn-buscar-dni");
    const spinnerDni      = document.getElementById("spinner-dni");
    const iconBuscarDni   = document.getElementById("icon-buscar-dni");
    const feedbackDni     = document.getElementById("dni-feedback");
    const feedbackTexto   = document.getElementById("dni-feedback-text");
    const badgeDni        = document.getElementById("dni-badge");

    const inputNombre     = document.getElementById("clienteNombre");
    const inputEmail      = document.getElementById("clienteEmail");
    const inputTelefono   = document.getElementById("clienteTelefono");
    const inputDireccion  = document.getElementById("clienteDireccion");

    let timeoutDni = null;

    async function consultarDni(dni) {
        const dniLimpio = (dni || "").trim().replace(/\D/g, "");
        if (dniLimpio.length !== 8) return;

        // Mostrar spinner
        if (spinnerDni) spinnerDni.classList.remove("d-none");
        if (iconBuscarDni) iconBuscarDni.classList.add("d-none");
        if (feedbackDni) feedbackDni.classList.add("d-none");

        try {
            const resp = await fetch(`/api/consulta/dni/${dniLimpio}`);
            if (!resp.ok) throw new Error("No se pudo obtener información del DNI");

            const data = await resp.json();
            if (data && data.encontrado) {
                if (inputNombre && data.nombre) inputNombre.value = data.nombre;
                if (inputEmail && data.email && (!inputEmail.value || inputEmail.value.includes("@"))) inputEmail.value = data.email;
                if (inputTelefono && data.telefono) inputTelefono.value = data.telefono;
                if (inputDireccion && data.direccion) inputDireccion.value = data.direccion;

                // Animación visual de campos autocompletados
                [inputNombre, inputEmail, inputTelefono, inputDireccion].forEach(el => {
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
                    badgeDni.innerHTML = `<i class="bi bi-check-circle-fill me-1"></i> ${data.origen || 'Verificado'}`;
                }

                if (feedbackDni && feedbackTexto) {
                    feedbackTexto.textContent = `Datos de ${data.nombre} autocompletados con éxito.`;
                    feedbackDni.classList.remove("d-none");
                }
            }
        } catch (error) {
            console.warn("Consulta DNI:", error);
        } finally {
            if (spinnerDni) spinnerDni.classList.add("d-none");
            if (iconBuscarDni) iconBuscarDni.classList.remove("d-none");
        }
    }

    if (inputDni) {
        inputDni.addEventListener("input", function(e) {
            this.value = this.value.replace(/\D/g, "").slice(0, 8);
            clearTimeout(timeoutDni);
            if (this.value.length === 8) {
                timeoutDni = setTimeout(() => consultarDni(this.value), 350);
            }
        });

        if (btnBuscarDni) {
            btnBuscarDni.addEventListener("click", () => {
                consultarDni(inputDni.value);
            });
        }

        // Si ya viene con DNI precargado al abrir la página y el nombre está vacío o por completar
        if (inputDni.value && inputDni.value.length === 8 && (!inputNombre.value || inputNombre.value.trim() === "")) {
            consultarDni(inputDni.value);
        }
    }

    cargarResumen();
});