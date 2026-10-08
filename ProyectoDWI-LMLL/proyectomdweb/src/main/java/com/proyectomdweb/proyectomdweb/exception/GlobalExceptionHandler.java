package com.proyectomdweb.proyectomdweb.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Maneja errores genéricos (Error 500)
    @ExceptionHandler(Exception.class)
    public String manejarErrorGenerico(Exception ex, Model model) {
        model.addAttribute("errorTitulo", "Algo no salió como esperábamos");
        model.addAttribute("errorMensaje", "Estamos trabajando para solucionarlo. Por favor, intenta de nuevo más tarde.");
        // Log del error
        System.err.println("Error interno: " + ex.getMessage());
        return "error"; 
    }

    // Maneja errores de "No encontrado" (Error 404)
    @ExceptionHandler(NoHandlerFoundException.class)
    public String manejarPaginaNoEncontrada(Model model) {
        model.addAttribute("errorTitulo", "Página no encontrada");
        model.addAttribute("errorMensaje", "La prenda o sección que buscas parece haber cambiado de colección.");
        return "error";
    }

    // Para manejar excepciones especificas (ej: CategoriaNotFound)
    @ExceptionHandler(RuntimeException.class)
    public String manejarRuntime(RuntimeException ex, Model model) {
        model.addAttribute("errorTitulo", "Error de Proceso");
        model.addAttribute("errorMensaje", ex.getMessage());
        return "error";
    }
}
