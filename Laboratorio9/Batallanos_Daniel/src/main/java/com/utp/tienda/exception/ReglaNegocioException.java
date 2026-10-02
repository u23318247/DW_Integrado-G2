package com.utp.tienda.exception;

/**
 * Se lanza cuando la operacion viola una regla de negocio (se traduce a HTTP 400).
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}