package com.utp.semana7.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.utp.semana7.model.MovimientoStock;
public interface MovimientoStockRepository
 extends JpaRepository<MovimientoStock, Long> {
}
