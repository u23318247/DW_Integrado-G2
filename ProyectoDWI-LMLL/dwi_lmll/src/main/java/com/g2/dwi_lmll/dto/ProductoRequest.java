package com.g2.dwi_lmll.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductoRequest(
        @NotBlank(message = "El nombre es obligatorio") String nombre,
        @NotBlank(message = "El genero es obligatorio") String genero,
        @NotNull(message = "El precio es obligatorio") @Positive(message = "El precio debe ser positivo") BigDecimal precioBase,
        @NotNull(message = "La disponibilidad es obligatoria") Boolean disponibilidad,
        @NotNull(message = "La categoria es obligatoria") Long categoriaId,
        String imagenUrl
) {}
