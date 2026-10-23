package com.utp.tienda.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.utp.tienda.model.MovimientoStock;

public interface MovimientoStockRepository extends JpaRepository<MovimientoStock, Long> {

    // Consulta derivada usada para evidenciar commit/rollback en las pruebas.
    long countByProductoId(Long productoId);
}
