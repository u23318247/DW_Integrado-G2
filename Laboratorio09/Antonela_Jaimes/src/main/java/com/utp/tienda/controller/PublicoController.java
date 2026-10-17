package com.utp.tienda.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/publico")
public class PublicoController {

    @GetMapping("/estado")
    public ResponseEntity<Map<String, Object>> obtenerEstado() {
        Map<String, Object> estado = new HashMap<>();
        estado.put("aplicacion", "Tienda API - Laboratorio 9");
        estado.put("estado", "DISPONIBLE");
        estado.put("seguridad", "Autenticacion basada en JWT (Stateless)");
        estado.put("servidorTimestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(estado);
    }
}
