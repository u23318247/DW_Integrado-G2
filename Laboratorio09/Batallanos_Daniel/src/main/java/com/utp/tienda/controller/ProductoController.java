package com.utp.tienda.controller;

import com.utp.tienda.dto.ProductoRequest;
import com.utp.tienda.dto.ProductoResponse;
import com.utp.tienda.exception.RecursoNoEncontradoException;
import com.utp.tienda.model.Producto;
import com.utp.tienda.service.ProductoService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Tarea 7.2 - CRUD de productos.
 *
 * <p>Control de acceso (definido en el {@code SecurityFilterChain}):</p>
 * <ul>
 *   <li>GET -&gt; USER o ADMIN</li>
 *   <li>POST, PUT, PATCH, DELETE -&gt; solo ADMIN</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /** GET /api/productos -&gt; 200 OK con USER o ADMIN, 401 sin autenticar. */
    @GetMapping
    public List<ProductoResponse> listar() {
        return productoService.listarTodos().stream()
                .map(ProductoResponse::from)
                .toList();
    }

    /** GET /api/productos/{id} -&gt; 200 OK / 404 / 401. */
    @GetMapping("/{id}")
    public ProductoResponse buscar(@PathVariable Long id) {
        Producto producto = productoService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto no encontrado con id: " + id));
        return ProductoResponse.from(producto);
    }

    /** POST /api/productos -&gt; 201 Created solo para ADMIN, 403 para USER. */
    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest request) {
        Producto creado = productoService.crear(request);
        return ResponseEntity
                .created(URI.create("/api/productos/" + creado.getId()))
                .body(ProductoResponse.from(creado));
    }

    /** PUT /api/productos/{id} -&gt; 200 OK solo para ADMIN (reemplazo completo). */
    @PutMapping("/{id}")
    public ProductoResponse actualizar(@PathVariable Long id,
                                       @Valid @RequestBody ProductoRequest request) {
        return ProductoResponse.from(productoService.actualizar(id, request));
    }

    /** PATCH /api/productos/{id} -&gt; 200 OK solo para ADMIN (actualizacion parcial). */
    @PatchMapping("/{id}")
    public ProductoResponse actualizarParcial(@PathVariable Long id,
                                              @RequestBody Map<String, Object> cambios) {
        return ProductoResponse.from(productoService.actualizarParcial(id, cambios));
    }

    /** DELETE /api/productos/{id} -&gt; 204 No Content solo para ADMIN. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}