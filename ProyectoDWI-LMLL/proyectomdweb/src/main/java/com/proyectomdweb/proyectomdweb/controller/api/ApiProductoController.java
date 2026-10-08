package com.proyectomdweb.proyectomdweb.controller.api;

import com.proyectomdweb.proyectomdweb.dtos.ProductoDTO;
import com.proyectomdweb.proyectomdweb.dtos.ProductoRequest;
import com.proyectomdweb.proyectomdweb.exception.RecursoNoEncontradoException;
import com.proyectomdweb.proyectomdweb.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ApiProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listar(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String texto) {
        if (texto != null && !texto.isBlank()) {
            return ResponseEntity.ok(productoService.buscarPorNombre(texto));
        }
        if (categoriaId != null) {
            return ResponseEntity.ok(productoService.buscarPorCategoriaId(categoriaId));
        }
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> buscarPorId(@PathVariable Long id) {
        return productoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + id));
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> crear(@Valid @RequestBody ProductoRequest request) {
        ProductoDTO nuevoDto = new ProductoDTO(
                null,
                request.nombre(),
                request.genero(),
                request.imagenUrl(),
                request.precioBase(),
                request.disponibilidad(),
                request.categoriaId(),
                null
        );
        ProductoDTO guardado = productoService.guardar(nuevoDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(guardado.id())
                .toUri();

        return ResponseEntity.created(location).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequest request) {
        if (productoService.buscarPorId(id).isEmpty()) {
            throw new RecursoNoEncontradoException("Producto no encontrado con ID: " + id);
        }
        ProductoDTO updateDto = new ProductoDTO(
                id,
                request.nombre(),
                request.genero(),
                request.imagenUrl(),
                request.precioBase(),
                request.disponibilidad(),
                request.categoriaId(),
                null
        );
        ProductoDTO actualizado = productoService.guardar(updateDto);
        return ResponseEntity.ok(actualizado);
    }

    // Operación PATCH según Laboratorio 4: Actualización parcial
    @PatchMapping("/{id}/disponibilidad")
    public ResponseEntity<ProductoDTO> actualizarDisponibilidad(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> payload) {
        ProductoDTO actual = productoService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + id));

        Boolean nuevaDisponibilidad = payload.get("disponibilidad");
        if (nuevaDisponibilidad == null) {
            throw new IllegalArgumentException("El campo 'disponibilidad' es requerido.");
        }

        ProductoDTO dtoModificado = new ProductoDTO(
                actual.id(),
                actual.nombre(),
                actual.genero(),
                actual.imagenUrl(),
                actual.precioBase(),
                nuevaDisponibilidad,
                actual.categoriaId(),
                actual.categoriaNombre()
        );

        ProductoDTO resultado = productoService.guardar(dtoModificado);
        return ResponseEntity.ok(resultado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (productoService.buscarPorId(id).isEmpty()) {
            throw new RecursoNoEncontradoException("Producto no encontrado con ID: " + id);
        }
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
