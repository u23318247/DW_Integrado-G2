package com.proyectomdweb.proyectomdweb.repository;

import com.proyectomdweb.proyectomdweb.dtos.ProductoResumenProjection;
import com.proyectomdweb.proyectomdweb.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    //* Al usar EntityGraph + JOIN FETCH, traemos producto, categoría y variantes sin N+1 *//
    @EntityGraph(attributePaths = {"categoria", "variantes"})
    @Query("SELECT DISTINCT p FROM Producto p JOIN FETCH p.categoria")
    List<Producto> findAllConCategoria();
    
    // Lo mismo, pero para buscar un producto específico por su ID
    @EntityGraph(attributePaths = {"categoria", "variantes"})
    @Query("SELECT p FROM Producto p JOIN FETCH p.categoria WHERE p.id = :id")
    Optional<Producto> findByIdConCategoria(@Param("id") Long id);

    @Query("""
           SELECT p.id AS id,
                  p.nombre AS nombre,
                  p.genero AS genero,
                  p.imagenUrl AS imagenUrl,
                  p.precioBase AS precioBase,
                  p.disponibilidad AS disponibilidad,
                  c.id AS categoriaId,
                  c.nombre AS categoriaNombre
           FROM Producto p
           JOIN p.categoria c
           WHERE (:categoriaId IS NULL OR c.id = :categoriaId)
             AND (:texto = '' OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%')))
           """)
    Page<ProductoResumenProjection> findResumenPaginado(
            @Param("categoriaId") Long categoriaId,
            @Param("texto") String texto,
            Pageable pageable);

    /*-- Le dice a Spring Data JPA esto:
     //*Busca dentro de la propiedad "categoria" y matchea su "id"*/
    List<Producto> findByCategoriaId(Long categoriaId); 
    
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // Búsqueda exacta por nombre (usada por el inicializador del catálogo)
    Optional<Producto> findFirstByNombreIgnoreCase(String nombre);

    @Query("SELECT p FROM Producto p WHERE " +
           "(p.genero = :genero OR p.genero = 'Unisex') AND " +
           "(:categoriaId IS NULL OR p.categoria.id = :categoriaId) AND " +
           "p.disponibilidad = true")
    List<Producto> filtrarPorGeneroYCategoria(
            @Param("genero") String genero, 
            @Param("categoriaId") Long categoriaId);
}