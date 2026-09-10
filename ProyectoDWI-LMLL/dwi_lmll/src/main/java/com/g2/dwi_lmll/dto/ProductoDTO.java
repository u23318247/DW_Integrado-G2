package com.g2.dwi_lmll.dto;

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
