package com.utp.tienda.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Tarea 3 - Contrato inmutable de entrada para {@code POST /api/auth/login}.
 *
 * <p>Record de Java: inmutable y validado con Jakarta Validation.</p>
 */
public record LoginRequest(
        @NotBlank(message = "El nombre de usuario es obligatorio") String username,
        @NotBlank(message = "La contraseña es obligatoria") String password
) {
}