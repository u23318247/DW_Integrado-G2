package com.utp.tienda.service;

import static org.junit.jupiter.api.Assertions.*;

import com.utp.tienda.exception.StockInsuficienteException;
import com.utp.tienda.model.MovimientoStock;
import com.utp.tienda.model.Producto;
import com.utp.tienda.repository.MovimientoStockRepository;
import com.utp.tienda.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
class InventarioTransaccionalTest {

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private MovimientoStockRepository movimientoRepository;

    private Long productoId;

    @BeforeEach
    void setUp() {
        movimientoRepository.deleteAll();
        productoRepository.deleteAll();

        Producto p = new Producto("Laptop Test APF2", "Computación", new BigDecimal("2500.00"), 10);
        Producto guardado = productoRepository.save(p);
        productoId = guardado.getId();
    }

    @Test
    @DisplayName("Paso 9 / 12.2: Registrar salida exitosa descuenta stock y registra movimiento SALIDA")
    void registrarSalidaExitosa() {
        Producto resultado = inventarioService.registrarSalida(productoId, 3);

        assertEquals(7, resultado.getStock());
        Producto enBd = productoRepository.findById(productoId).orElseThrow();
        assertEquals(7, enBd.getStock(), "El stock en base de datos debe reflejar el descuento");

        List<MovimientoStock> movimientos = movimientoRepository.findAll();
        assertEquals(1, movimientos.size());
        assertEquals("SALIDA", movimientos.get(0).getTipo());
        assertEquals(3, movimientos.get(0).getCantidad());
    }

    @Test
    @DisplayName("Paso 9 / 12.3: Forzar rollback ante stock insuficiente (atomicidad y consistencia)")
    void rollbackAnteStockInsuficiente() {
        long totalMovimientosAntes = movimientoRepository.count();

        assertThrows(StockInsuficienteException.class, () -> {
            inventarioService.registrarSalida(productoId, 99999);
        });

        Producto enBd = productoRepository.findById(productoId).orElseThrow();
        assertEquals(10, enBd.getStock(), "El stock debe permanecer exactamente igual tras el rollback");
        assertEquals(totalMovimientosAntes, movimientoRepository.count(), "No debe insertarse ningun movimiento en la BD");
    }
}
