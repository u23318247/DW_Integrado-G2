package com.utp.productosapi.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
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
    void debeCrearProductoYAsignarId() {
        Producto nuevo = new Producto(null, "Laptop", 3500.0, 10);
        Producto creado = service.crear(nuevo);
        assertNotNull(creado.getId());
        assertEquals("Laptop", creado.getNombre());
        assertEquals(3500.0, creado.getPrecio());
    }

    @Test
    void debeBuscarProductoPorId() {
        Producto creado = service.crear(new Producto(null, "Mouse", 80.0, 20));
        Producto encontrado = service.buscarPorId(creado.getId()).orElseThrow();
        assertEquals("Mouse", encontrado.getNombre());
    }

    @Test
    void debeRechazarPrecioInvalido() {
        Producto invalido = new Producto(null, "Monitor", 0.0, 5);
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
    void debeBuscarProductosPorNombreIgnorandoMayusculas() {
        service.crear(new Producto(null, "Laptop Lenovo", 3500.0, 10, "Tecnologia"));
        service.crear(new Producto(null, "Mouse Gamer", 150.0, 25, "Accesorios"));

        List<Producto> encontrados = service.buscarPorNombre("LAP");
        assertEquals(1, encontrados.size());
        assertEquals("Laptop Lenovo", encontrados.get(0).getNombre());
    }
}
