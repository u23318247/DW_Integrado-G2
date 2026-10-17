package com.utp.tienda.dto;

import com.utp.tienda.model.MovimientoStock;
import java.time.LocalDateTime;

/**
 * DTO de salida de la bitacora de inventario.
 * Expone solo datos seguros: nunca credenciales ni relaciones de seguridad.
 */
public record MovimientoStockResponse(
        Long id,
        Long productoId,
        String productoNombre,
        String tipo,
        Integer cantidad,
        String motivo,
        String usuario,
        LocalDateTime fecha
) {

    public static MovimientoStockResponse from(MovimientoStock movimiento) {
        return new MovimientoStockResponse(
                movimiento.getId(),
                movimiento.getProducto() != null ? movimiento.getProducto().getId() : null,
                movimiento.getProducto() != null ? movimiento.getProducto().getNombre() : null,
                movimiento.getTipo() != null ? movimiento.getTipo().name() : null,
                movimiento.getCantidad(),
                movimiento.getMotivo(),
                movimiento.getUsuario(),
                movimiento.getFecha()
        );
    }
}