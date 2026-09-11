package com.g2.dwi_lmll.exception;

import com.g2.dwi_lmll.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> productoNoEncontrado(ProductoNoEncontradoException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validacion(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(400, mensaje, request.getRequestURI()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> argumentoInvalido(IllegalArgumentException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(400, ex.getMessage(), request.getRequestURI()));
    }

    // Los servicios lanzan RuntimeException con "no existe" / "no encontrado" cuando falta un registro
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> errorGenerico(RuntimeException ex, HttpServletRequest request) {
        String msg = ex.getMessage() == null ? "Error interno" : ex.getMessage();
        String lower = msg.toLowerCase();
        HttpStatus status = (lower.contains("no existe") || lower.contains("no encontrado"))
                ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status)
                .body(new ErrorResponse(status.value(), msg, request.getRequestURI()));
    }
}
