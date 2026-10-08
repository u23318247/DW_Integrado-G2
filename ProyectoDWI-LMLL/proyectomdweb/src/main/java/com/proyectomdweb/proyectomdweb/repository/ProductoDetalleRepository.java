package com.proyectomdweb.proyectomdweb.repository;

import com.proyectomdweb.proyectomdweb.model.ProductoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoDetalleRepository extends JpaRepository<ProductoDetalle, Long> {

    // Prendas con stock bajo o agotado (<= 5 unidades)
    List<ProductoDetalle> findByStockLessThanEqualOrderByStockAsc(Integer limiteStock);

    // Listar todos los detalles ordenados por stock ascendente
    @Query("SELECT d FROM ProductoDetalle d JOIN FETCH d.producto ORDER BY d.stock ASC")
    List<ProductoDetalle> findAllConProductoOrdenadoPorStock();
}
