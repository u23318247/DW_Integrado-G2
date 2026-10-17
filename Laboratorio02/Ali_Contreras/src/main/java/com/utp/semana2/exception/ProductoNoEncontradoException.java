package com.utp.semana2.exception;

public class ProductoNoEncontradoException extends RuntimeException {

    // Mejora 4: mensaje de error mas descriptivo para el cliente
    public ProductoNoEncontradoException(Long id) {
        super("No se encontro ningun producto registrado con el id " + id
                + ". Verifique el identificador e intente nuevamente.");
    }
}
