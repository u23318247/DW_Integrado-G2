package com.utp.productosapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.utp.productosapi.model.Producto;

/**
 * Pruebas unitarias del servicio (ciclo TDD: RED -> GREEN -> REFACTOR).
 * No levantan el contexto de Spring: se instancia el servicio directamente.
 */
class ProductoServiceTest {

    private ProductoService service;

    @BeforeEach
    void setUp() {
        service = new ProductoService();
    }

    // ---------- Paso 9: primera prueba (RED) ----------

    @Test
    void debeCrearProductoYAsignarId() {
        Producto nuevo = new Producto(null, "Laptop", 3500.0, 10);

        Producto creado = service.crear(nuevo);

        assertNotNull(creado.getId());
        assertEquals("Laptop", creado.getNombre());
        assertEquals(3500.0, creado.getPrecio());
    }

    // ---------- Paso 11: completar pruebas del servicio ----------

    @Test
    void debeBuscarProductoPorId() {
        Producto creado = service.crear(new Producto(null, "Mouse", 80.0, 20));

        Producto encontrado = service.buscarPorId(creado.getId()).orElseThrow();

        assertEquals("Mouse", encontrado.getNombre());
    }

    @Test
    void debeRetornarVacioSiElIdNoExiste() {
        assertTrue(service.buscarPorId(999L).isEmpty());
    }

    @Test
    void debeRechazarPrecioInvalido() {
        Producto invalido = new Producto(null, "Monitor", 0.0, 5);

        assertThrows(IllegalArgumentException.class,
                () -> service.crear(invalido));
    }

    @Test
    void debeRechazarNombreVacio() {
        Producto invalido = new Producto(null, "   ", 100.0, 5);

        assertThrows(IllegalArgumentException.class,
                () -> service.crear(invalido));
    }

    @Test
    void debeRechazarStockNegativo() {
        Producto invalido = new Producto(null, "Teclado", 100.0, -1);

        assertThrows(IllegalArgumentException.class,
                () -> service.crear(invalido));
    }

    @Test
    void debeActualizarProductoExistente() {
        Producto creado = service.crear(new Producto(null, "Laptop", 3500.0, 10));

        Producto actualizado = service
                .actualizar(creado.getId(), new Producto(null, "Laptop Pro", 3900.0, 7))
                .orElseThrow();

        assertEquals("Laptop Pro", actualizado.getNombre());
        assertEquals(3900.0, actualizado.getPrecio());
        assertEquals(7, actualizado.getStock());
    }

    @Test
    void debeRetornarVacioAlActualizarInexistente() {
        assertTrue(service.actualizar(999L, new Producto(null, "X", 10.0, 1)).isEmpty());
    }

    @Test
    void debeActualizarSoloElPrecio() {
        Producto creado = service.crear(new Producto(null, "Laptop", 3500.0, 10));

        Producto actualizado = service.actualizarPrecio(creado.getId(), 4100.0).orElseThrow();

        assertEquals(4100.0, actualizado.getPrecio());
        assertEquals("Laptop", actualizado.getNombre());
        assertEquals(10, actualizado.getStock());
    }

    @Test
    void debeRechazarPrecioInvalidoAlActualizarPrecio() {
        Producto creado = service.crear(new Producto(null, "Laptop", 3500.0, 10));

        assertThrows(IllegalArgumentException.class,
                () -> service.actualizarPrecio(creado.getId(), -5.0));
    }

    @Test
    void debeEliminarProducto() {
        Producto creado = service.crear(new Producto(null, "Teclado", 120.0, 8));

        boolean eliminado = service.eliminar(creado.getId());

        assertTrue(eliminado);
        assertTrue(service.buscarPorId(creado.getId()).isEmpty());
    }

    @Test
    void debeRetornarFalseAlEliminarInexistente() {
        assertFalse(service.eliminar(999L));
    }

    @Test
    void debeListarTodosLosProductos() {
        service.crear(new Producto(null, "Laptop", 3500.0, 10));
        service.crear(new Producto(null, "Mouse", 80.0, 20));

        assertEquals(2, service.listar().size());
    }

    // ---------- Paso 16: reto integrador (prueba primero) ----------

    @Test
    void debeBuscarPorNombreSinDistinguirMayusculas() {
        service.crear(new Producto(null, "Laptop Lenovo", "Tecnologia", 3500.0, 10));
        service.crear(new Producto(null, "LAPTOP HP", "Tecnologia", 2800.0, 4));
        service.crear(new Producto(null, "Mouse", "Accesorios", 80.0, 20));

        List<Producto> resultado = service.buscarPorNombre("lap");

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream()
                .allMatch(p -> p.getNombre().toLowerCase().contains("lap")));
    }

    @Test
    void debeRetornarListaVaciaSiNingunNombreCoincide() {
        service.crear(new Producto(null, "Mouse", "Accesorios", 80.0, 20));

        assertTrue(service.buscarPorNombre("impresora").isEmpty());
    }

    @Test
    void debeGuardarLaCategoriaDelProducto() {
        Producto creado = service.crear(new Producto(null, "Silla", "Muebles", 750.0, 5));

        assertEquals("Muebles", service.buscarPorId(creado.getId()).orElseThrow().getCategoria());
    }
}
