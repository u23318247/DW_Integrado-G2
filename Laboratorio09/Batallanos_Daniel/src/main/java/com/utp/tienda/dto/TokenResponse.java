package com.utp.tienda.dto;

/**
 * Tarea 3 - Respuesta exitosa del login: el token Bearer y su vigencia.
 *
 * @param tokenType          siempre {@code Bearer}
 * @param accessToken        JWT firmado con HS256
 * @param expiresInSeconds   segundos de vigencia restantes
 */
public record TokenResponse(
        String tokenType,
        String accessToken,
        Long expiresInSeconds
) {
}