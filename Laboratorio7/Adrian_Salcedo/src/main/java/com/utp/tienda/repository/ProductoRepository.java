package com.utp.tienda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.utp.tienda.model.Producto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Consulta derivada: Spring Data interpreta el nombre del método.
    List<Producto> findByCategoriaIgnoreCase(String categoria);
    // JPQL: búsqueda parcial sin distinguir mayúsculas/minúsculas.
    @Query("""
    SELECT p
    FROM Producto p
    WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
    ORDER BY p.nombre
    """)
    List<Producto> buscarPorNombre(@Param("texto") String texto);

    // JPQL: rango de precios.
    @Query("""

    """)
    List<Producto> buscarPorRangoPrecio(
            @Param("min") BigDecimal min,
            @Param("max") BigDecimal max);
    // JPQL adicional: productos con stock bajo.
    @Query("SELECT p FROM Producto p WHERE p.stock <= :limite ORDER BY p.stock ASC")
    List<Producto> buscarConStockBajo(@Param("limite") Integer limite);
}