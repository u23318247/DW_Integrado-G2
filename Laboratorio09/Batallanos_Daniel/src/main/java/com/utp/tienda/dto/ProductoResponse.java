package com.utp.tienda.dto;

import com.utp.tienda.model.Producto;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de salida de productos. Evita exponer entidades JPA (y por tanto
 * informacion interna de persistencia) en los endpoints REST.
 */
public record ProductoResponse(
        Long id,
        String nombre,
        BigDecimal precio,
        Integer stock,
        String descripcion,
        Boolean activo,
        LocalDateTime fechaRegistro
) {

    public static ProductoResponse from(Producto producto) {
        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getDescripcion(),
                producto.getActivo(),
                producto.getFechaRegistro()
        );
    }
}