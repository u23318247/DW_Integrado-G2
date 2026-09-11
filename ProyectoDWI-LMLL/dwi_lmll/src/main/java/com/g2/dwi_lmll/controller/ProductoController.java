package com.g2.dwi_lmll.controller;

import com.g2.dwi_lmll.dto.ProductoDTO;
import com.g2.dwi_lmll.dto.ProductoRequest;
import com.g2.dwi_lmll.exception.ProductoNoEncontradoException;
import com.g2.dwi_lmll.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {
    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listar(@RequestParam(required=false) String categoria, @RequestParam(required=false) String texto) {
        if (texto != null) return ResponseEntity.ok(productoService.buscarPorNombre(texto));
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> buscar(@PathVariable Long id) {
        return productoService.buscarPorId(id).map(ResponseEntity::ok).orElseThrow(() -> new ProductoNoEncontradoException(id));
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> crear(@Valid @RequestBody ProductoRequest request) {
        ProductoDTO creado = productoService.guardar(new ProductoDTO(null, request.nombre(), request.genero(), request.imagenUrl(), request.precioBase(), request.disponibilidad(), request.categoriaId(), null));
        return ResponseEntity.created(URI.create("/api/productos/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        if (productoService.buscarPorId(id).isEmpty()) throw new ProductoNoEncontradoException(id);
        return ResponseEntity.ok(productoService.guardar(new ProductoDTO(id, request.nombre(), request.genero(), request.imagenUrl(), request.precioBase(), request.disponibilidad(), request.categoriaId(), null)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (productoService.buscarPorId(id).isEmpty()) throw new ProductoNoEncontradoException(id);
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
