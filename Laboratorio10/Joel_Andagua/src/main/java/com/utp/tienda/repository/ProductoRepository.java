package com.utp.tienda.repository;

import com.utp.tienda.model.Producto;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @Query("""
            SELECT p
            FROM Producto p
            WHERE p.precio BETWEEN :min AND :max
            ORDER BY p.precio ASC
            """)
    List<Producto> buscarPorRangoPrecio(
            @Param("min") BigDecimal min,
            @Param("max") BigDecimal max);

    long countByStockLessThan(int stock);
}
