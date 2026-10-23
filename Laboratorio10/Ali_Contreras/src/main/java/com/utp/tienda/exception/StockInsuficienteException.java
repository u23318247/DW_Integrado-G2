package com.utp.tienda.exception;

/**
 * Semana 10: la salida pide mas unidades de las disponibles.
 * Hereda de ReglaNegocioException (sigue siendo una regla de negocio y provoca rollback
 * por ser RuntimeException), pero ApiExceptionHandler la traduce a 409 Conflict.
 */
public class StockInsuficienteException extends ReglaNegocioException {

    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
