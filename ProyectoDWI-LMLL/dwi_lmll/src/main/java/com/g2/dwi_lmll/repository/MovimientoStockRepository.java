package com.g2.dwi_lmll.repository;

import com.g2.dwi_lmll.model.MovimientoStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientoStockRepository extends JpaRepository<MovimientoStock, Long> {
}
