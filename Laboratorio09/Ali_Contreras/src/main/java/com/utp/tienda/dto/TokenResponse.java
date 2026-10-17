package com.utp.tienda.dto;

/** Respuesta del login. Nunca incluye la contrasena ni el hash. */
public record TokenResponse(
        String tokenType,
        String accessToken,
        long expiresInSeconds) {
}
