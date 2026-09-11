package com.g2.dwi_lmll.service;

import com.g2.dwi_lmll.model.EstadoPedido;
import com.g2.dwi_lmll.model.Pedido;

import java.util.List;
import java.util.Optional;

public interface PedidoService {

    // Métodos de lectura
    List<Pedido> listarTodos();

    Optional<Pedido> buscarPorId(Long id);

    Long contarPorEstado(EstadoPedido estado);

    // Métodos de escritura
    Pedido guardar(Pedido pedido);

    Pedido actualizar(Long id, Pedido pedido);

    void eliminar(Long id);
}