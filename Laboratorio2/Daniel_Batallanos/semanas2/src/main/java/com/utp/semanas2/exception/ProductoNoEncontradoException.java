package com.utp.semanas2.exception;

public class ProductoNoEncontradoException extends RuntimeException {
    public ProductoNoEncontradoException(Long id) {
        super("El producto con ID " + id + " no fue encontrado en el sistema.");
    }
}
