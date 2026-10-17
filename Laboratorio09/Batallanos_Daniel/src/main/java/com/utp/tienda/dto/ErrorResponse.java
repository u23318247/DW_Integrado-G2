package com.utp.tienda.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Respuesta de error uniforme de la API.
 */
public record ErrorResponse(
        int status,
        String error,
        String mensaje,
        String ruta,
        Map<String, String> erroresCampos,
        LocalDateTime timestamp
) {
}