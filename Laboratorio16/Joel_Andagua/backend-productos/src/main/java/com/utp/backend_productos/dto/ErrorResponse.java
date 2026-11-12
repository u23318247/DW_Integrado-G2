package com.utp.backend_productos.dto;

import java.time.LocalDateTime;

public class ErrorResponse {
    private LocalDateTime fecha;
    private int estado;
    private String error;
    private String mensaje;
    private String ruta;

    public ErrorResponse(LocalDateTime fecha, int estado, String error, String mensaje, String ruta) {
        this.fecha = fecha;
        this.estado = estado;
        this.error = error;
        this.mensaje = mensaje;
        this.ruta = ruta;
    }

    public LocalDateTime getFecha() { return fecha; }
    public int getEstado() { return estado; }
    public String getError() { return error; }
    public String getMensaje() { return mensaje; }
    public String getRuta() { return ruta; }
}
