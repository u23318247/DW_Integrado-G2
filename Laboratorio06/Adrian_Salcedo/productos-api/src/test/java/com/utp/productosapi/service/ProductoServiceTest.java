package com.utp.productosapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.utp.productosapi.model.Producto;
import com.utp.productosapi.repository.ProductoRepository;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    private ProductoService service;

    @BeforeEach
    void setUp() {
        service = new ProductoService(repository);
    }

    @Test
    void debeListarProductos() {
        when(repository.findAll()).thenReturn(List.of(
                new Producto(1L, "Mouse", 80.0, 20),
                new Producto(2L, "Teclado", 120.0, 8)));
        assertEquals(2, service.listar().size());
    }

    @Test
    void debeBuscarProductoPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(new Producto(1L, "Mouse", 80.0, 20)));
        Producto encontrado = service.buscarPorId(1L).orElseThrow();
        assertEquals("Mouse", encontrado.getNombre());
    }

    @Test
    void debeCrearProducto() {
        Producto nuevo = new Producto(null, "Mouse", 80.0, 20);
        when(repository.save(any(Producto.class))).thenAnswer(inv -> {
            Producto p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });
        Producto creado = service.crear(nuevo);
        assertEquals(1L, creado.getId());
        verify(repository).save(nuevo);
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
    void debeActualizarProducto() {
        Producto existente = new Producto(1L, "Mouse", 80.0, 20);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);

        Producto actualizado = service.actualizar(1L,
                new Producto(null, "Mouse Pro", 150.0, 30)).orElseThrow();
        assertEquals("Mouse Pro", actualizado.getNombre());
        assertEquals(150.0, actualizado.getPrecio());
        assertEquals(30, actualizado.getStock());
    }

    @Test
    void debeActualizarPrecio() {
        Producto existente = new Producto(1L, "Mouse", 80.0, 20);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);

        Producto actualizado = service.actualizarPrecio(1L, 99.9).orElseThrow();
        assertEquals(99.9, actualizado.getPrecio());
    }

    @Test
    void debeRechazarPrecioInvalidoAlActualizar() {
        assertThrows(IllegalArgumentException.class, () -> service.actualizarPrecio(1L, 0.0));
    }

    @Test
    void debeEliminarProducto() {
        when(repository.existsById(1L)).thenReturn(true);
        assertTrue(service.eliminar(1L));
        verify(repository).deleteById(1L);
    }

    @Test
    void debeDevolverVacioSiNoExiste() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        assertTrue(service.buscarPorId(999L).isEmpty());

        when(repository.existsById(999L)).thenReturn(false);
        assertFalse(service.eliminar(999L));
    }
}