package com.utp.tienda.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint publico para comprobar que la API esta disponible sin credenciales.
 */
@RestController
@RequestMapping("/api/publico")
public class PublicoController {

    @GetMapping("/estado")
    public Map<String, String> estado() {
        return Map.of("estado", "API disponible");
    }
}
