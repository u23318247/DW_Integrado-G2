package com.utp.tienda.repository;

import com.utp.tienda.model.MovimientoStock;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de acceso a datos de la bitacora de inventario.
 */
@Repository
public interface MovimientoStockRepository extends JpaRepository<MovimientoStock, Long> {

    List<MovimientoStock> findAllByOrderByFechaDesc();

    List<MovimientoStock> findByProductoIdOrderByFechaDesc(Long productoId);
}