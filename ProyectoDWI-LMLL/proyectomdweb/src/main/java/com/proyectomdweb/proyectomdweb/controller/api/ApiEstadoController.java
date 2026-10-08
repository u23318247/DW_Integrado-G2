package com.proyectomdweb.proyectomdweb.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/estado")
public class ApiEstadoController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> verificarEstado() {
        return ResponseEntity.ok(Map.of(
                "estado", "UP",
                "aplicacion", "MDWeb - E-Commerce Tienda",
                "version", "1.0.0",
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}
