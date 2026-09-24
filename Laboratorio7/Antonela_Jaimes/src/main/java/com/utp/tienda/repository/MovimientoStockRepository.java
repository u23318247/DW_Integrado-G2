package com.utp.tienda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.utp.tienda.model.MovimientoStock;

@Repository
public interface MovimientoStockRepository extends JpaRepository<MovimientoStock, Long> {
}
