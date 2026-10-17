package com.utp.tienda.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada del endpoint {@code POST /api/inventario/ajustes},
 * exclusivo para el rol ADMIN.
 */
public record AjusteInventarioRequest(
        @NotNull(message = "El productoId es obligatorio")
        Long productoId,

        @NotNull(message = "El tipo de movimiento es obligatorio")
        String tipo,

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor que cero")
        Integer cantidad,

        @Size(max = 255, message = "El motivo no puede superar 255 caracteres")
        String motivo
) {
}