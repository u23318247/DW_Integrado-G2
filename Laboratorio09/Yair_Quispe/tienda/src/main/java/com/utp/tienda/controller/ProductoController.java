package com.utp.tienda.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
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

import com.utp.tienda.model.Producto;
import com.utp.tienda.service.ProductoService;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(
            ProductoService productoService) {

        this.productoService = productoService;
    }

    @GetMapping
    public List<Producto> listar() {
        return productoService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscar(
            @PathVariable Long id) {

        return productoService.buscar(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                    ResponseEntity.notFound().build()
                );
    }

    @PostMapping
    public ResponseEntity<Producto> crear(
            @RequestBody Producto producto) {

        Producto guardado =
                productoService.guardar(producto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(
            @PathVariable Long id,
            @RequestBody Producto datos) {

        return productoService.buscar(id)
                .map(producto -> {

                    producto.setNombre(datos.getNombre());
                    producto.setCategoria(datos.getCategoria());
                    producto.setPrecio(datos.getPrecio());
                    producto.setStock(datos.getStock());

                    return ResponseEntity.ok(
                            productoService.guardar(producto)
                    );
                })
                .orElseGet(() ->
                    ResponseEntity.notFound().build()
                );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Producto> actualizarParcial(
            @PathVariable Long id,
            @RequestBody Producto datos) {

        return productoService.buscar(id)
                .map(producto -> {

                    if (datos.getNombre() != null) {
                        producto.setNombre(
                                datos.getNombre());
                    }

                    if (datos.getCategoria() != null) {
                        producto.setCategoria(
                                datos.getCategoria());
                    }

                    if (datos.getPrecio() != null) {
                        producto.setPrecio(
                                datos.getPrecio());
                    }

                    if (datos.getStock() != null) {
                        producto.setStock(
                                datos.getStock());
                    }

                    return ResponseEntity.ok(
                            productoService.guardar(producto)
                    );
                })
                .orElseGet(() ->
                    ResponseEntity.notFound().build()
                );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        if (productoService.buscar(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        productoService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}