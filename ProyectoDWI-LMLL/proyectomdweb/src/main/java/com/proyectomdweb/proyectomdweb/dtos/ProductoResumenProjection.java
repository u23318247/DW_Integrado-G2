package com.proyectomdweb.proyectomdweb.dtos;

import java.math.BigDecimal;

public interface ProductoResumenProjection {
    Long getId();
    String getNombre();
    String getGenero();
    String getImagenUrl();
    BigDecimal getPrecioBase();
    Boolean getDisponibilidad();
    Long getCategoriaId();
    String getCategoriaNombre();
}
