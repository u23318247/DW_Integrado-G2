package com.proyectomdweb.proyectomdweb.dtos;

import java.math.BigDecimal;

public record ProductoDetalleDTO(
    Long id,
    String sku,
    String nombreCompleto,
    String imagenUrl,
    String marca,
    String talla,
    String color,
    String descripcion,
    Integer stock,
    BigDecimal precioBase,
    BigDecimal precioAdicional,
    BigDecimal precioTotal
) {
    public static ProductoDetalleDTO deEntidad(com.proyectomdweb.proyectomdweb.model.ProductoDetalle d) {
        BigDecimal base = d.getPrecioBase() != null ? d.getPrecioBase() : BigDecimal.ZERO;
        BigDecimal extra = d.getPrecioAdicional() != null ? d.getPrecioAdicional() : BigDecimal.ZERO;
        return new ProductoDetalleDTO(
                d.getId(),
                d.getCodigo(),
                d.getNombreCompleto(),
                d.getImagenUrl(),
                d.getMarca(),
                d.getTalla(),
                d.getColor(),
                d.getDescripcion(),
                d.getStock(),
                base,
                extra,
                base.add(extra)
        );
    }
}
