package com.utp.tienda.dto;

import jakarta.validation.constraints.NotBlank;

/** Contrato JSON del login: {"username":"...","password":"..."}. */
public record LoginRequest(
        @NotBlank String username,
        @NotBlank String password) {
}
