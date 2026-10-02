package com.utp.tienda.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminReporteController {

    @GetMapping("/reporte")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> obtenerReporteAdmin() {
        Map<String, Object> reporte = new HashMap<>();
        reporte.put("modulo", "Administracion de Tienda");
        reporte.put("mensaje", "Acceso concedido al modulo administrativo confidencial");
        reporte.put("fechaGeneracion", LocalDateTime.now().toString());
        reporte.put("nivelSeguridad", "ROLE_ADMIN");
        reporte.put("estadoSistema", "Operativo con proteccion JWT");
        return ResponseEntity.ok(reporte);
    }
}
