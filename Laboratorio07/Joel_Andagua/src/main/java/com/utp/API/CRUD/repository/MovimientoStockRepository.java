package com.utp.API.CRUD.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.utp.API.CRUD.model.MovimientoStock;

public interface MovimientoStockRepository extends JpaRepository<MovimientoStock, Long> {
}