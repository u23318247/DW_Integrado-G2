package com.utp.tienda.controller;

import com.utp.tienda.service.ProductoNoEncontradoException;
import com.utp.tienda.service.StockInsuficienteException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> productoNoEncontrado(
            ProductoNoEncontradoException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler({StockInsuficienteException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String, String>> errorDeNegocio(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", exception.getMessage()));
    }
}
