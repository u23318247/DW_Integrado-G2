package com.g2.dwi_lmll.dto;

import java.time.LocalDateTime;

public record ErrorResponse(int estado, String mensaje, String ruta, LocalDateTime fechaHora) {
    public ErrorResponse(int estado, String mensaje, String ruta) {
        this(estado, mensaje, ruta, LocalDateTime.now());
    }
}
