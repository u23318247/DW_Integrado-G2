package com.utp.tienda.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Tarea 7.1 - Endpoints publicos: no requieren autenticacion
 * ({@code permitAll} en la cadena de filtros).
 */
@RestController
@RequestMapping("/api/publico")
public class PublicoController {

    /**
     * Responde 200 OK sin encabezado {@code Authorization}.
     * Sirve como health check para comprobar que la API esta levantada.
     */
    @GetMapping("/estado")
    public Map<String, String> estado() {
        return Map.of("estado", "API disponible");
    }
}