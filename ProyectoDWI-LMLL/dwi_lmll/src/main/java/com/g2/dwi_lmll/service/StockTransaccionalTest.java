package com.g2.dwi_lmll.service;

import com.g2.dwi_lmll.model.Producto;
import com.g2.dwi_lmll.repository.MovimientoStockRepository;
import com.g2.dwi_lmll.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Prueba de integración (H2): commit en salida/entrada y rollback en la simulación con error. */
@SpringBootTest
class StockTransaccionalTest {

    @Autowired ProductoService productoService;
    @Autowired ProductoRepository productoRepository;
    @Autowired MovimientoStockRepository movimientoRepository;

    private Producto primero() {
        return productoRepository.findAll().get(0);
    }

    @Test
    void registrarSalida_hacecommit_yDescuentaStock() {
        Producto p = primero();
        int stockAntes = p.getStock();
        long movsAntes = movimientoRepository.count();

        productoService.registrarSalida(p.getId(), 5);

        assertThat(productoRepository.findById(p.getId()).orElseThrow().getStock()).isEqualTo(stockAntes - 5);
        assertThat(movimientoRepository.count()).isEqualTo(movsAntes + 1);
    }

    @Test
    void registrarEntrada_aumentaStock() {
        Producto p = primero();
        int stockAntes = p.getStock();

        productoService.registrarEntrada(p.getId(), 7);

        assertThat(productoRepository.findById(p.getId()).orElseThrow().getStock()).isEqualTo(stockAntes + 7);
    }

    @Test
    void simularSalidaConError_hacerollback_sinDatosInconsistentes() {
        Producto p = primero();
        int stockAntes = p.getStock();
        long movsAntes = movimientoRepository.count();

        assertThatThrownBy(() -> productoService.simularSalidaConError(p.getId(), 5))
                .isInstanceOf(RuntimeException.class);

        assertThat(productoRepository.findById(p.getId()).orElseThrow().getStock()).isEqualTo(stockAntes);
        assertThat(movimientoRepository.count()).isEqualTo(movsAntes);
    }
}
