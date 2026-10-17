package com.proyectomdweb.proyectomdweb.dtos;

import java.math.BigDecimal;
import java.util.List;

public record ProductoDTO(
    Long id,
    String nombre,
    String genero,
    String imagenUrl,
    BigDecimal precioBase,
    Boolean disponibilidad,
    Long categoriaId,
    String categoriaNombre,
    List<String> tallas,
    List<String> colores,
    List<ProductoDetalleDTO> variantes
) {
    public ProductoDTO(Long id, String nombre, String genero, String imagenUrl,
                       BigDecimal precioBase, Boolean disponibilidad, Long categoriaId,
                       String categoriaNombre) {
        this(id, nombre, genero, imagenUrl, precioBase, disponibilidad, categoriaId, categoriaNombre, List.of(), List.of(), List.of());
    }
}
