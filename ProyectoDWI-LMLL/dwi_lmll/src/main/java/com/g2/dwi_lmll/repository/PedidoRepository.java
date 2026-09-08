package com.g2.dwi_lmll.repository;

import com.g2.dwi_lmll.model.EstadoPedido;
import com.g2.dwi_lmll.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Spring Genera automáticamente: SELECT COUNT(*) FROM pedidos WHERE estado = ?;
    long countByEstado(EstadoPedido estado);
}
