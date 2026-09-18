package com.utp.productosapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.utp.productosapi.model.Producto;

class ProductoServiceTest {

    private ProductoService service;

    @BeforeEach
    void setUp() {
        service = new ProductoService();
    }

    @Test
    void debeCrearAsignandoId() {
        Producto creado = service.crear(new Producto(null, "Mouse", 80.0, 20));
        assertEquals(1L, creado.getId());
        assertTrue(service.buscarPorId(creado.getId()).isPresent());
    }

    @Test
    void debeBuscarProductoPorId() {
        Producto creado = service.crear(new Producto(null, "Teclado", 120.0, 8));
        Producto encontrado = service.buscarPorId(creado.getId()).orElseThrow();
        assertEquals("Teclado", encontrado.getNombre());
    }

    @Test
    void debeRechazarPrecioInvalido() {
        Producto invalido = new Producto(null, "Monitor", 0.0, 5);
        assertThrows(IllegalArgumentException.class, () -> service.crear(invalido));
    }

    @Test
    void debeRechazarStockNegativo() {
        Producto invalido = new Producto(null, "Monitor", 500.0, -1);
        assertThrows(IllegalArgumentException.class, () -> service.crear(invalido));
    }

    @Test
    void debeEliminarProducto() {
        Producto creado = service.crear(new Producto(null, "Teclado", 120.0, 8));
        boolean eliminado = service.eliminar(creado.getId());
        assertTrue(eliminado);
        assertTrue(service.buscarPorId(creado.getId()).isEmpty());
    }

    @Test
    void debeActualizarProducto() {
        Producto creado = service.crear(new Producto(null, "Mouse", 80.0, 20));
        Producto actualizado = service.actualizar(creado.getId(),
                        new Producto(null, "Mouse Pro", "Perifericos", 150.0, 30))
                .orElseThrow();
        assertEquals("Mouse Pro", actualizado.getNombre());
        assertEquals("Perifericos", actualizado.getCategoria());
        assertEquals(150.0, actualizado.getPrecio());
        assertEquals(30, actualizado.getStock());
    }

    @Test
    void debeActualizarPrecio() {
        Producto creado = service.crear(new Producto(null, "Mouse", 80.0, 20));
        Producto actualizado = service.actualizarPrecio(creado.getId(), 99.9).orElseThrow();
        assertEquals(99.9, actualizado.getPrecio());
    }

    @Test
    void debeRechazarPrecioInvalidoAlActualizar() {
        Producto creado = service.crear(new Producto(null, "Mouse", 80.0, 20));
        assertThrows(IllegalArgumentException.class,
                () -> service.actualizarPrecio(creado.getId(), 0.0));
    }
}