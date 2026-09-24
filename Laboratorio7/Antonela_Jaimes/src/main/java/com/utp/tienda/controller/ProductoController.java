package com.utp.tienda.controller;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.utp.tienda.model.Producto;
import com.utp.tienda.service.ProductoService;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping("/buscar")
    public List<Producto> buscarPorNombre(@RequestParam String texto) {
        return service.buscarPorNombre(texto);
    }

    @GetMapping("/categoria/{categoria}")
    public List<Producto> buscarPorCategoria(@PathVariable String categoria) {
        return service.buscarPorCategoria(categoria);
    }

    @GetMapping("/precio")
    public List<Producto> buscarPorRango(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        return service.buscarPorRango(min, max);
    }

    @GetMapping("/stock-bajo")
    public List<Producto> buscarConStockBajo(@RequestParam(defaultValue = "5") Integer limite) {
        return service.buscarConStockBajo(limite);
    }

    @PostMapping("/{id}/salidas")
    public ResponseEntity<Producto> registrarSalida(
            @PathVariable Long id,
            @RequestParam int cantidad) {
        return ResponseEntity.ok(service.registrarSalida(id, cantidad));
    }

    @PostMapping("/{id}/entradas")
    public ResponseEntity<Producto> registrarEntrada(
            @PathVariable Long id,
            @RequestParam int cantidad) {
        return ResponseEntity.ok(service.registrarEntrada(id, cantidad));
    }

    @PostMapping("/{id}/salidas/simular-error")
    public ResponseEntity<Void> simularError(
            @PathVariable Long id,
            @RequestParam int cantidad) {
        service.simularSalidaConError(id, cantidad);
        return ResponseEntity.noContent().build();
    }
}
