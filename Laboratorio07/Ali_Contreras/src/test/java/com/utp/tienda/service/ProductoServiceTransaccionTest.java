package com.utp.tienda.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.utp.tienda.exception.ReglaNegocioException;
import com.utp.tienda.model.Producto;
import com.utp.tienda.repository.MovimientoStockRepository;
import com.utp.tienda.repository.ProductoRepository;

/**
 * Comprueba commit y rollback reales. La clase NO usa @Transactional a proposito:
 * asi cada llamada al Service abre y cierra su propia transaccion, igual que en produccion.
 */
@SpringBootTest
class ProductoServiceTransaccionTest {

    @Autowired
    private ProductoService service;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private MovimientoStockRepository movimientoRepository;

    private Long productoId;

    @BeforeEach
    void setUp() {
        movimientoRepository.deleteAll();
        productoRepository.deleteAll();
        productoId = productoRepository.save(
                new Producto("Laptop Lenovo ThinkPad", "Tecnologia", new BigDecimal("4200.00"), 8)).getId();
    }

    @Test
    void salidaExitosaHaceCommitDeStockYMovimiento() {
        service.registrarSalida(productoId, 2);

        assertThat(stockActual()).isEqualTo(6);
        assertThat(movimientos()).isEqualTo(1);
    }

    @Test
    void errorSimuladoHaceRollbackCompleto() {
        assertThatThrownBy(() -> service.simularSalidaConError(productoId, 1))
                .isInstanceOf(IllegalStateException.class);

        // Ni el stock ni el movimiento quedan "a medias".
        assertThat(stockActual()).isEqualTo(8);
        assertThat(movimientos()).isZero();
    }

    @Test
    void salidaConStockInsuficienteNoModificaNada() {
        assertThatThrownBy(() -> service.registrarSalida(productoId, 50))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("Stock insuficiente");

        assertThat(stockActual()).isEqualTo(8);
        assertThat(movimientos()).isZero();
    }

    @Test
    void entradaExitosaIncrementaStockYRegistraMovimiento() {
        service.registrarEntrada(productoId, 5);

        assertThat(stockActual()).isEqualTo(13);
        assertThat(movimientos()).isEqualTo(1);
    }

    @Test
    void textoDeBusquedaVacioEsRechazado() {
        assertThatThrownBy(() -> service.buscarPorNombre("   "))
                .isInstanceOf(ReglaNegocioException.class);
    }

    @Test
    void rangoInvertidoEsRechazado() {
        assertThatThrownBy(() -> service.buscarPorRango(new BigDecimal("2000"), new BigDecimal("100")))
                .isInstanceOf(ReglaNegocioException.class);
    }

    private int stockActual() {
        return productoRepository.findById(productoId).orElseThrow().getStock();
    }

    private long movimientos() {
        return movimientoRepository.countByProductoId(productoId);
    }
}
