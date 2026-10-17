package com.utp.semana4_api_rest.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.utp.semana4_api_rest.dto.ActualizarStockRequest;
import com.utp.semana4_api_rest.dto.DisminuirStockRequest;
import com.utp.semana4_api_rest.dto.ProductoRequest;
import com.utp.semana4_api_rest.model.producto;
import com.utp.semana4_api_rest.service.ProductoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<producto>> listar(@RequestParam(required = false) String categoria) {
        return ResponseEntity.ok(service.listar(categoria));
    }

    // Ejercicio Complementario 1: Búsqueda por texto parcial
    @GetMapping("/buscar")
    public ResponseEntity<List<producto>> buscarPorTexto(@RequestParam String texto) {
        return ResponseEntity.ok(service.buscarPorTexto(texto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<producto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<producto> crear(@Valid @RequestBody ProductoRequest request) {
        producto nuevoProducto = service.crear(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(nuevoProducto.getId())
                .toUri();
        return ResponseEntity.created(location).body(nuevoProducto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<producto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<producto> actualizarStock(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarStockRequest request) {
        return ResponseEntity.ok(service.actualizarStock(id, request));
    }

    // Ejercicio Complementario 2: Disminuir stock con control de saldo negativo
    @PatchMapping("/{id}/disminuir-stock")
    public ResponseEntity<producto> disminuirStock(
            @PathVariable Long id,
            @Valid @RequestBody DisminuirStockRequest request) {
        return ResponseEntity.ok(service.disminuirStock(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
