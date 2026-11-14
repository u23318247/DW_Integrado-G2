package com.utp.backend_deploy.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controlador REST que expone el estado del backend
 * Guía Laboratorio 17: Comunicación mediante Nginx Reverse Proxy
 * Estudiante: Ali Contreras
 */
@RestController
@RequestMapping("/api/estado")
public class EstadoController {

    @GetMapping
    public Map<String, Object> obtenerEstado() {
        return Map.of(
            "estado", "ACTIVO",
            "mensaje", "Backend funcionando correctamente en Docker - UTP",
            "autor", "Ali Contreras",
            "timestamp", LocalDateTime.now().toString(),
            "version", "1.0.0"
        );
    }
}
