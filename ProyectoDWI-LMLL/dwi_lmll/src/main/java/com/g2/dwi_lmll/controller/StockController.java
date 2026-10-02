package com.g2.dwi_lmll.controller;

import com.g2.dwi_lmll.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/** Endpoints del Laboratorio 7 para probar commit y rollback (ej. con Postman/curl). */
@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final ProductoService productoService;

    @PostMapping("/{id}/salida")
    public ResponseEntity<Map<String, String>> salida(@PathVariable Long id, @RequestParam int cantidad) {
        productoService.registrarSalida(id, cantidad);
        return ResponseEntity.ok(Map.of("mensaje", "Salida registrada (COMMIT)"));
    }

    @PostMapping("/{id}/entrada")
    public ResponseEntity<Map<String, String>> entrada(@PathVariable Long id, @RequestParam int cantidad) {
        productoService.registrarEntrada(id, cantidad);
        return ResponseEntity.ok(Map.of("mensaje", "Entrada registrada (COMMIT)"));
    }

    @PostMapping("/{id}/salida-con-error")
    public ResponseEntity<Map<String, String>> salidaConError(@PathVariable Long id, @RequestParam int cantidad) {
        productoService.simularSalidaConError(id, cantidad); // lanza RuntimeException -> ROLLBACK
        return ResponseEntity.ok(Map.of("mensaje", "No deberia llegar aqui"));
    }
}
