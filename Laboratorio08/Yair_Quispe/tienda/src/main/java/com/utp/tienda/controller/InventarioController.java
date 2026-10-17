package com.utp.tienda.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    @GetMapping
    public List<Map<String, Object>> listarInventario() {
        return List.of(
                Map.of("almacen", "Central", "estado", "Operativo"),
                Map.of("almacen", "Secundario", "estado", "Operativo")
        );
    }

    @PostMapping("/ajustes")
    public ResponseEntity<Map<String, String>> realizarAjuste(@RequestBody(required = false) Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("mensaje", "Ajuste de inventario realizado exitosamente por ADMIN"));
    }
}