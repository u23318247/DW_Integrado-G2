/* ==============================
   LÓGICA DE LA PÁGINA DE CARRITO 
   ============================== */

document.addEventListener("DOMContentLoaded", () => {
    const contenedor = document.getElementById("cart-items-list");
    const subtotalElt = document.getElementById("resumen-subtotal");
    const totalElt = document.getElementById("resumen-total");
    
    let carrito = JSON.parse(localStorage.getItem("carritoLlama")) || [];

    function guardarYRenderizar() {
        localStorage.setItem("carritoLlama", JSON.stringify(carrito));
        renderizarCarrito();
    }

    function renderizarCarrito() {
        if (!contenedor) return;

        if (carrito.length === 0) {
            contenedor.innerHTML = `
                <div class="text-center py-5">
                    <i class="bi bi-cart-x display-1 text-muted"></i>
                    <p class="mt-3">Tu bolsa está vacía.</p>
                    <a href="productos" class="btn btn-outline-dark mt-2">Ir a comprar</a>
                </div>`;
            subtotalElt.textContent = "S/ 0.00";
            totalElt.textContent = "0.00";
            document.getElementById("btn-proceder-pago").classList.add("disabled");
            return;
        }

        contenedor.innerHTML = "";
        let totalAcumulado = 0;

        carrito.forEach((item, index) => {
            const subtotalItem = item.precio * item.cantidad;
            totalAcumulado += subtotalItem;

            const row = document.createElement("div");
            row.className = "carrito-full-item row align-items-center g-3 mb-4";
            row.dataset.index = index;

            row.innerHTML = `
                <div class="col-12 col-md-6 d-flex align-items-center gap-3">
                    <img src="${item.imagen}" class="cart-item-img" alt="${item.nombre}" style="width:80px; height:80px; object-fit:cover; border-radius:8px; border: 1px solid var(--border-2);">
                        <div>
                            <h4 class="cart-item-title mb-1" style="font-family: 'Batang', serif; font-size:1.1rem; font-weight:600; color: var(--txt);">${item.nombre}</h4>
            
                            <p class="cart-item-variant mb-2 text-muted-1" style="font-size:0.85rem;">Producto Seleccionado</p>
            
                                <button class="d-flex align-items-center gap-1" onclick="eliminarItem(${index})" style="background: transparent; border: none; color: #ff4d4d; font-size: 0.8rem; padding: 0; cursor: pointer;">
                                    <i class="bi bi-trash3"></i> Quitar
                                </button>
                        </div>
                </div>
    
                <div class="col-4 col-md-2 text-md-center fw-600 text-gold" style="font-family: 'Batang', serif;">
                     S/ ${item.precio.toFixed(2)}
                </div>
    
                <div class="col-4 col-md-2 d-flex justify-content-center">
                    <div class="d-flex align-items-center" style="background: var(--bg-2); border: 1px solid var(--border-2); border-radius: 6px; padding: 2px 4px;">
            
                        <button class="btn btn-sm px-2 text-white border-0" style="background: transparent; box-shadow: none;" onclick="cambiarCantidad(${index}, -1)">-</button>
            
                        <input type="text" value="${item.cantidad}" class="text-center border-0 mx-1" style="width: 35px; background: #ffffff; color: #000000; font-weight: bold; border-radius: 4px; height: 28px;" readonly>
            
                        <button class="btn btn-sm px-2 text-white border-0" style="background: transparent; box-shadow: none;" onclick="cambiarCantidad(${index}, 1)">+</button>
            
                    </div>
                </div>
    
                <div class="col-4 col-md-2 text-end text-brand fw-600" style="font-family: 'Batang', serif;">
                    S/ ${subtotalItem.toFixed(2)}
                </div>
            `;
            contenedor.appendChild(row);
        });

        subtotalElt.textContent = `S/ ${totalAcumulado.toFixed(2)}`;
        totalElt.textContent = totalAcumulado.toFixed(2);
    }

    // Funciones globales para los botones de la fila
    window.cambiarCantidad = (index, delta) => {
        carrito[index].cantidad += delta;
        if (carrito[index].cantidad <= 0) {
            eliminarItem(index);
        } else {
            guardarYRenderizar();
        }
    };

    window.eliminarItem = (index) => {
        carrito.splice(index, 1);
        guardarYRenderizar();
    };

    renderizarCarrito();
});