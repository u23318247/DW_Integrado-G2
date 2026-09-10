package com.g2.dwi_lmll.service;

import com.g2.dwi_lmll.model.EstadoPedido;
import com.g2.dwi_lmll.model.Pedido;
import com.g2.dwi_lmll.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    private Pedido pedido;

    @BeforeEach
    void setUp() {
        pedido = new Pedido();
        pedido.setId(1L);
        pedido.setUsuarioId(1L);
        pedido.setTotal(new BigDecimal("120.50"));
        pedido.setEstado(EstadoPedido.PENDIENTE);
    }

    @Test
    @DisplayName("Debe listar todos los pedidos")
    void listarTodos_debeRetornarLista() {
        when(pedidoRepository.findAll()).thenReturn(List.of(pedido));

        List<Pedido> resultado = pedidoService.listarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo(1L);
        verify(pedidoRepository).findAll();
    }

    @Test
    @DisplayName("Debe buscar pedido por ID cuando existe")
    void buscarPorId_cuandoExiste() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        Optional<Pedido> resultado = pedidoService.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getTotal()).isEqualByComparingTo("120.50");
    }

    @Test
    @DisplayName("Debe guardar pedido asignando estado PENDIENTE si es nulo")
    void guardar_asignaEstadoPendienteSiEsNulo() {
        Pedido pedidoSinEstado = new Pedido();
        pedidoSinEstado.setUsuarioId(2L);
        pedidoSinEstado.setTotal(new BigDecimal("99.00"));

        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pedido guardado = pedidoService.guardar(pedidoSinEstado);

        assertThat(guardado.getEstado()).isEqualTo(EstadoPedido.PENDIENTE);
        verify(pedidoRepository).save(pedidoSinEstado);
    }

    @Test
    @DisplayName("Debe actualizar un pedido existente")
    void actualizar_pedidoExistente() {
        Pedido datosActualizados = new Pedido();
        datosActualizados.setUsuarioId(1L);
        datosActualizados.setTotal(new BigDecimal("200.00"));
        datosActualizados.setEstado(EstadoPedido.PAGADO);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pedido actualizado = pedidoService.actualizar(1L, datosActualizados);

        assertThat(actualizado.getTotal()).isEqualByComparingTo("200.00");
        assertThat(actualizado.getEstado()).isEqualTo(EstadoPedido.PAGADO);
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar pedido inexistente")
    void actualizar_pedidoInexistente_lanzaExcepcion() {
        Pedido datos = new Pedido();
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.actualizar(99L, datos))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("El pedido con ID 99 no existe.");
    }

    @Test
    @DisplayName("Debe eliminar pedido cuando existe")
    void eliminar_cuandoExiste() {
        when(pedidoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(pedidoRepository).deleteById(1L);

        pedidoService.eliminar(1L);

        verify(pedidoRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar pedido inexistente")
    void eliminar_cuandoNoExiste_lanzaExcepcion() {
        when(pedidoRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> pedidoService.eliminar(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("El pedido con ID 99 no existe.");
    }

    @Test
    @DisplayName("Debe contar pedidos por estado")
    void contarPorEstado() {
        when(pedidoRepository.countByEstado(EstadoPedido.PENDIENTE)).thenReturn(5L);

        Long cantidad = pedidoService.contarPorEstado(EstadoPedido.PENDIENTE);

        assertThat(cantidad).isEqualTo(5L);
        verify(pedidoRepository).countByEstado(EstadoPedido.PENDIENTE);
    }
}
