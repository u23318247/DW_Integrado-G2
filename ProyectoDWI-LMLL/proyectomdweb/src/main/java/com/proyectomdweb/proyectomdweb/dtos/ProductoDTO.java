package com.proyectomdweb.proyectomdweb.dtos;

import java.math.BigDecimal;

public record ProductoDTO(
    Long        id, 
    String      nombre, 
    String      genero,
    String      imagenUrl,
    BigDecimal  precioBase,
    Boolean     disponibilidad,
    Long        categoriaId,      
    String      categoriaNombre  
) {}
