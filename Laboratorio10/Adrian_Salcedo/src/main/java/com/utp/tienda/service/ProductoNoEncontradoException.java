package com.utp.tienda.service;

public class ProductoNoEncontradoException extends RuntimeException {

    public ProductoNoEncontradoException(Long productoId) {
        super("No existe el producto con id " + productoId);
    }
}
