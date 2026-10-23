package com.utp.tienda.controller;

import com.utp.tienda.model.Producto;
import com.utp.tienda.service.InventarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioService service;

    public InventarioController(InventarioService service) {
        this.service = service;
    }

    @PostMapping("/{id}/salida")
    public ResponseEntity<Producto> registrarSalida(
            @PathVariable Long id,
            @RequestParam int cantidad) {
        return ResponseEntity.ok(service.registrarSalida(id, cantidad));
    }
}
