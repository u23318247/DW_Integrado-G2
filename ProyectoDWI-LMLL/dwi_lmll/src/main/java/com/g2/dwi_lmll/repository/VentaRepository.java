package com.g2.dwi_lmll.repository;

import com.g2.dwi_lmll.model.EstadoPedido;
import com.g2.dwi_lmll.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    // Suma el total de ingresos únicamente de transacciones exitosas (PAGADO)
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE v.pedido.estado = :estado")
    BigDecimal sumarTotalIngresosPorEstado(@Param("estado") EstadoPedido estado);

    List<Venta> findByPedidoEstadoAndFechaEmisionBetween(EstadoPedido estado, LocalDateTime inicio, LocalDateTime fin);
}
